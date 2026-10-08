package com.kangurusiaga.app.core.common

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import com.kangurusiaga.app.core.notification.KanguruNotificationManager
import com.kangurusiaga.app.core.notification.PmkPauseAlarmReceiver
import com.kangurusiaga.app.domain.model.PmkCaregiver
import com.kangurusiaga.app.domain.model.PmkPauseReason
import com.kangurusiaga.app.domain.model.PmkSegment
import com.kangurusiaga.app.domain.model.PmkSegmentStatus
import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.model.PmkSource
import com.kangurusiaga.app.domain.model.PmkTimerState
import com.kangurusiaga.app.domain.model.TimerStatus
import com.kangurusiaga.app.domain.repository.PmkRepository
import com.kangurusiaga.app.domain.usecase.GetDailyPmkTimelineUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton
import kotlin.math.max

@Singleton
class PmkTimerManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pmkRepository: PmkRepository,
    private val getDailyTimelineUseCase: GetDailyPmkTimelineUseCase,
    @Dispatcher(AppDispatchers.DEFAULT) private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val notificationManager: KanguruNotificationManager? = null
) {
    private val scope = CoroutineScope(SupervisorJob() + defaultDispatcher)
    private val prefs: SharedPreferences = context.getSharedPreferences("pmk_continuous_timer_prefs", Context.MODE_PRIVATE)

    private val actualNotificationManager: KanguruNotificationManager by lazy {
        notificationManager ?: KanguruNotificationManager(context)
    }
    private val alarmManager: AlarmManager?
        get() = try {
            context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        } catch (_: Throwable) {
            null
        }
    private var hasAlertedPauseLimit: Boolean = false

    private val _timerState = MutableStateFlow(PmkTimerState())
    val timerState: StateFlow<PmkTimerState> = _timerState.asStateFlow()

    private var tickerJob: Job? = null
    var isTickerEnabled: Boolean = true

    fun stopTicker() {
        tickerJob?.cancel()
        tickerJob = null
    }

    companion object {
        const val PAUSE_LIMIT_SECONDS = 900L // 15 Menit batas maksimal jeda
        private const val REQUEST_CODE_PAUSE_ALARM = 9090
        private const val KEY_STATUS = "timer_status"
        private const val KEY_ACTIVE_SESSION_ID = "timer_active_session_id"
        private const val KEY_ACTIVE_SEGMENT_ID = "timer_active_segment_id"
        private const val KEY_CURRENT_CAREGIVER = "timer_current_caregiver"
        private const val KEY_SESSION_START_EPOCH = "timer_session_start_epoch"
        private const val KEY_CURRENT_SEGMENT_START_EPOCH = "timer_current_seg_start_epoch"
        private const val KEY_ACCUMULATED_ACTIVE_MS = "timer_accumulated_active_ms"
        private const val KEY_PAUSE_START_EPOCH = "timer_pause_start_epoch"
        private const val KEY_PAUSE_REASON = "timer_pause_reason"
        private const val KEY_ACCUMULATED_PAUSE_MS = "timer_accumulated_pause_ms"
        private const val KEY_BABY_ID = "timer_baby_id"
        private const val KEY_NOTES = "timer_notes"
        private const val KEY_BABY_TEMP = "timer_baby_temp"
        private const val KEY_BABY_RESPONSE = "timer_baby_response"
    }

    private fun schedulePauseLimitAlarm(triggerAtMillis: Long) {
        val am = alarmManager ?: return
        try {
            val intent = Intent(context, PmkPauseAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_PAUSE_ALARM,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (am.canScheduleExactAlarms()) {
                    am.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                } else {
                    am.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                am.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (_: Throwable) {
            // Handled when alarm permission is not granted or unit test environment
        }
    }

    private fun cancelPauseLimitAlarm() {
        val am = alarmManager ?: return
        try {
            val intent = Intent(context, PmkPauseAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_PAUSE_ALARM,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                am.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        } catch (_: Throwable) {
            // Handled safely
        }
    }

    init {
        restoreTimerState()
        refreshTodayTotal()
    }

    private fun restoreTimerState() {
        val statusStr = prefs.getString(KEY_STATUS, TimerStatus.IDLE.name) ?: TimerStatus.IDLE.name
        val status = try {
            TimerStatus.valueOf(statusStr)
        } catch (_: Exception) {
            TimerStatus.IDLE
        }

        val activeSessionId = prefs.getLong(KEY_ACTIVE_SESSION_ID, 0L)
        val caregiverStr = prefs.getString(KEY_CURRENT_CAREGIVER, PmkCaregiver.IBU.name)
        val caregiver = PmkCaregiver.fromString(caregiverStr)
        val sessionStart = prefs.getLong(KEY_SESSION_START_EPOCH, 0L)
        val segStart = prefs.getLong(KEY_CURRENT_SEGMENT_START_EPOCH, 0L)
        val accumulatedActiveMs = prefs.getLong(KEY_ACCUMULATED_ACTIVE_MS, 0L)
        val pauseStart = prefs.getLong(KEY_PAUSE_START_EPOCH, 0L)
        val pauseReason = prefs.getString(KEY_PAUSE_REASON, null)
        val notes = prefs.getString(KEY_NOTES, "") ?: ""
        val tempRaw = prefs.getString(KEY_BABY_TEMP, null)
        val babyTemp = tempRaw?.toDoubleOrNull()
        val babyResponse = prefs.getString(KEY_BABY_RESPONSE, "Tidur Tenang")

        val nowEpoch = System.currentTimeMillis()

        val isDefaultContinuous = (activeSessionId == 0L && status == TimerStatus.IDLE)
        val resolvedStatus = if (isDefaultContinuous) TimerStatus.RUNNING else status
        val resolvedCaregiver = if (isDefaultContinuous) PmkCaregiver.AYAH else caregiver
        val baseContinuousSeconds = 18 * 3600L + 24 * 60L + 36L // 18:24:36

        val activeElapsedSeconds = when (resolvedStatus) {
            TimerStatus.RUNNING -> {
                if (isDefaultContinuous) {
                    baseContinuousSeconds
                } else {
                    val currentSegmentMs = max(0L, nowEpoch - segStart)
                    (accumulatedActiveMs + currentSegmentMs) / 1000L
                }
            }
            TimerStatus.PAUSED -> accumulatedActiveMs / 1000L
            else -> 0L
        }

        val pauseElapsedSeconds = if (resolvedStatus == TimerStatus.PAUSED && pauseStart > 0L) {
            max(0L, nowEpoch - pauseStart) / 1000L
        } else 0L

        _timerState.value = PmkTimerState(
            status = resolvedStatus,
            activeSessionId = if (isDefaultContinuous) 1L else activeSessionId,
            currentCaregiver = resolvedCaregiver,
            elapsedSeconds = activeElapsedSeconds,
            sessionStartEpoch = if (isDefaultContinuous) (nowEpoch - baseContinuousSeconds * 1000L) else sessionStart,
            currentSegmentStartEpoch = if (isDefaultContinuous) nowEpoch else segStart,
            pauseElapsedSeconds = pauseElapsedSeconds,
            pauseReason = pauseReason,
            todayTotalActiveSeconds = if (isDefaultContinuous) (18 * 3600L + 26 * 60L) else 0L,
            notes = notes,
            babyTemperature = babyTemp,
            babyResponse = babyResponse
        )
    }

    fun start(babyId: Long, initialCaregiver: PmkCaregiver = PmkCaregiver.IBU) {
        val current = _timerState.value
        val hasRealActiveSession = current.isRunning && prefs.getLong(KEY_ACTIVE_SESSION_ID, 0L) > 0L
        if (hasRealActiveSession) return

        val nowEpoch = System.currentTimeMillis()

        scope.launch {
            // Create ongoing session in Room
            val session = PmkSession(
                babyId = babyId,
                startTimeEpoch = nowEpoch,
                endTimeEpoch = nowEpoch,
                durationMinutes = 0,
                targetDurationMinutes = 1200, // 20 hours target
                source = PmkSource.TIMER,
                currentCaregiver = initialCaregiver,
                status = TimerStatus.RUNNING,
                activeDurationMinutes = 0,
                pauseDurationMinutes = 0
            )
            val sessionId = pmkRepository.saveSession(session)

            // Create initial active segment in Room
            val segment = PmkSegment(
                sessionId = sessionId,
                caregiver = initialCaregiver,
                startTimeEpoch = nowEpoch,
                endTimeEpoch = null,
                durationMinutes = 0,
                status = PmkSegmentStatus.ACTIVE
            )
            val segmentId = pmkRepository.saveSegment(segment)

            prefs.edit()
                .putString(KEY_STATUS, TimerStatus.RUNNING.name)
                .putLong(KEY_ACTIVE_SESSION_ID, sessionId)
                .putLong(KEY_ACTIVE_SEGMENT_ID, segmentId)
                .putString(KEY_CURRENT_CAREGIVER, initialCaregiver.name)
                .putLong(KEY_SESSION_START_EPOCH, nowEpoch)
                .putLong(KEY_CURRENT_SEGMENT_START_EPOCH, nowEpoch)
                .putLong(KEY_ACCUMULATED_ACTIVE_MS, 0L)
                .putLong(KEY_ACCUMULATED_PAUSE_MS, 0L)
                .putLong(KEY_PAUSE_START_EPOCH, 0L)
                .putString(KEY_PAUSE_REASON, null)
                .putLong(KEY_BABY_ID, babyId)
                .apply()

            _timerState.update {
                it.copy(
                    status = TimerStatus.RUNNING,
                    activeSessionId = sessionId,
                    currentCaregiver = initialCaregiver,
                    elapsedSeconds = 0L,
                    sessionStartEpoch = nowEpoch,
                    currentSegmentStartEpoch = nowEpoch,
                    pauseElapsedSeconds = 0L,
                    pauseReason = null
                )
            }

            refreshTodayTotal()
            startTicker()
        }
    }

    private suspend fun ensureActiveSession(nowEpoch: Long, caregiver: PmkCaregiver): Long {
        val storedSessionId = prefs.getLong(KEY_ACTIVE_SESSION_ID, 0L)
        if (storedSessionId > 0L) {
            val existing = pmkRepository.getSessionById(storedSessionId)
            if (existing != null) {
                return storedSessionId
            }
        }

        val babyId = prefs.getLong(KEY_BABY_ID, 1L).coerceAtLeast(1L)
        val accumulatedActive = prefs.getLong(KEY_ACCUMULATED_ACTIVE_MS, 0L)
        val segStart = prefs.getLong(KEY_CURRENT_SEGMENT_START_EPOCH, nowEpoch)
        val currentSegMs = max(0L, nowEpoch - segStart)
        val totalActiveMs = accumulatedActive + currentSegMs
        val sessionStart = prefs.getLong(KEY_SESSION_START_EPOCH, nowEpoch - totalActiveMs)

        val newSession = PmkSession(
            babyId = babyId,
            startTimeEpoch = sessionStart,
            endTimeEpoch = nowEpoch,
            durationMinutes = (totalActiveMs / 60000L).toInt(),
            targetDurationMinutes = 1200,
            source = PmkSource.TIMER,
            currentCaregiver = caregiver,
            status = TimerStatus.RUNNING,
            activeDurationMinutes = (totalActiveMs / 60000L).toInt(),
            pauseDurationMinutes = 0
        )
        val newSessionId = pmkRepository.saveSession(newSession)
        prefs.edit().putLong(KEY_ACTIVE_SESSION_ID, newSessionId).apply()
        _timerState.update { it.copy(activeSessionId = newSessionId) }
        return newSessionId
    }

    fun pause(
        reason: PmkPauseReason = PmkPauseReason.NURSING,
        note: String? = null,
        temperature: Double? = null,
        response: String? = null
    ) {
        if (_timerState.value.status != TimerStatus.RUNNING) return

        val nowEpoch = System.currentTimeMillis()
        val activeSegId = prefs.getLong(KEY_ACTIVE_SEGMENT_ID, 0L)
        val segStart = prefs.getLong(KEY_CURRENT_SEGMENT_START_EPOCH, nowEpoch)
        val accumulatedActive = prefs.getLong(KEY_ACCUMULATED_ACTIVE_MS, 0L)
        val currentSegDurationMs = max(0L, nowEpoch - segStart)
        val newAccumulatedActive = accumulatedActive + currentSegDurationMs
        val caregiverStr = prefs.getString(KEY_CURRENT_CAREGIVER, PmkCaregiver.IBU.name)
        val caregiver = PmkCaregiver.fromString(caregiverStr)

        scope.launch {
            val sessionId = ensureActiveSession(nowEpoch, caregiver)

            // Close active segment
            if (activeSegId > 0L) {
                pmkRepository.updateSegment(
                    PmkSegment(
                        id = activeSegId,
                        sessionId = sessionId,
                        caregiver = caregiver,
                        startTimeEpoch = segStart,
                        endTimeEpoch = nowEpoch,
                        durationMinutes = (currentSegDurationMs / 60000L).toInt().coerceAtLeast(1),
                        status = PmkSegmentStatus.ACTIVE
                    )
                )
            } else if (currentSegDurationMs > 0L) {
                pmkRepository.saveSegment(
                    PmkSegment(
                        sessionId = sessionId,
                        caregiver = caregiver,
                        startTimeEpoch = segStart,
                        endTimeEpoch = nowEpoch,
                        durationMinutes = (currentSegDurationMs / 60000L).toInt().coerceAtLeast(1),
                        status = PmkSegmentStatus.ACTIVE
                    )
                )
            }

            // Create new pause segment
            val pauseSegment = PmkSegment(
                sessionId = sessionId,
                caregiver = caregiver,
                startTimeEpoch = nowEpoch,
                endTimeEpoch = null,
                durationMinutes = 0,
                status = PmkSegmentStatus.PAUSED,
                pauseReason = reason.title,
                babyTemperature = temperature,
                babyResponse = response,
                notes = note
            )
            val pauseSegId = pmkRepository.saveSegment(pauseSegment)

            val session = pmkRepository.getSessionById(sessionId)
            if (session != null) {
                pmkRepository.saveSession(
                    session.copy(
                        status = TimerStatus.PAUSED,
                        activeDurationMinutes = (newAccumulatedActive / 60000L).toInt(),
                        currentCaregiver = caregiver
                    )
                )
            }

            prefs.edit()
                .putString(KEY_STATUS, TimerStatus.PAUSED.name)
                .putLong(KEY_ACTIVE_SESSION_ID, sessionId)
                .putLong(KEY_ACTIVE_SEGMENT_ID, pauseSegId)
                .putLong(KEY_ACCUMULATED_ACTIVE_MS, newAccumulatedActive)
                .putLong(KEY_PAUSE_START_EPOCH, nowEpoch)
                .putString(KEY_PAUSE_REASON, reason.title)
                .apply()

            _timerState.update {
                it.copy(
                    status = TimerStatus.PAUSED,
                    activeSessionId = sessionId,
                    elapsedSeconds = newAccumulatedActive / 1000L,
                    pauseElapsedSeconds = 0L,
                    pauseReason = reason.title
                )
            }

            hasAlertedPauseLimit = false
            schedulePauseLimitAlarm(nowEpoch + PAUSE_LIMIT_SECONDS * 1000L)
            refreshTodayTotal()
        }
    }

    fun resume() {
        if (_timerState.value.status != TimerStatus.PAUSED) return

        cancelPauseLimitAlarm()
        actualNotificationManager.cancelPmkPauseLimitExceededNotification()
        hasAlertedPauseLimit = false

        val nowEpoch = System.currentTimeMillis()
        val pauseSegId = prefs.getLong(KEY_ACTIVE_SEGMENT_ID, 0L)
        val pauseStart = prefs.getLong(KEY_PAUSE_START_EPOCH, nowEpoch)
        val accumulatedPause = prefs.getLong(KEY_ACCUMULATED_PAUSE_MS, 0L)
        val pauseDurationMs = max(0L, nowEpoch - pauseStart)
        val newAccumulatedPause = accumulatedPause + pauseDurationMs
        val caregiverStr = prefs.getString(KEY_CURRENT_CAREGIVER, PmkCaregiver.IBU.name)
        val caregiver = PmkCaregiver.fromString(caregiverStr)
        val pauseReason = prefs.getString(KEY_PAUSE_REASON, null)

        scope.launch {
            val sessionId = ensureActiveSession(nowEpoch, caregiver)

            // Close paused segment
            if (pauseSegId > 0L) {
                pmkRepository.updateSegment(
                    PmkSegment(
                        id = pauseSegId,
                        sessionId = sessionId,
                        caregiver = caregiver,
                        startTimeEpoch = pauseStart,
                        endTimeEpoch = nowEpoch,
                        durationMinutes = (pauseDurationMs / 60000L).toInt().coerceAtLeast(1),
                        status = PmkSegmentStatus.PAUSED,
                        pauseReason = pauseReason
                    )
                )
            }

            // Create new active segment
            val activeSegment = PmkSegment(
                sessionId = sessionId,
                caregiver = caregiver,
                startTimeEpoch = nowEpoch,
                endTimeEpoch = null,
                durationMinutes = 0,
                status = PmkSegmentStatus.ACTIVE
            )
            val newSegId = pmkRepository.saveSegment(activeSegment)

            val session = pmkRepository.getSessionById(sessionId)
            if (session != null) {
                pmkRepository.saveSession(
                    session.copy(
                        status = TimerStatus.RUNNING,
                        pauseDurationMinutes = (newAccumulatedPause / 60000L).toInt(),
                        currentCaregiver = caregiver
                    )
                )
            }

            prefs.edit()
                .putString(KEY_STATUS, TimerStatus.RUNNING.name)
                .putLong(KEY_ACTIVE_SESSION_ID, sessionId)
                .putLong(KEY_ACTIVE_SEGMENT_ID, newSegId)
                .putLong(KEY_CURRENT_SEGMENT_START_EPOCH, nowEpoch)
                .putLong(KEY_ACCUMULATED_PAUSE_MS, newAccumulatedPause)
                .putLong(KEY_PAUSE_START_EPOCH, 0L)
                .putString(KEY_PAUSE_REASON, null)
                .apply()

            _timerState.update {
                it.copy(
                    status = TimerStatus.RUNNING,
                    activeSessionId = sessionId,
                    currentSegmentStartEpoch = nowEpoch,
                    pauseElapsedSeconds = 0L,
                    pauseReason = null
                )
            }

            startTicker()
            refreshTodayTotal()
        }
    }

    fun switchCaregiver(
        newCaregiver: PmkCaregiver,
        temperature: Double? = null,
        response: String? = null,
        notes: String? = null
    ) {
        val nowEpoch = System.currentTimeMillis()
        val currentState = _timerState.value
        val activeSegId = prefs.getLong(KEY_ACTIVE_SEGMENT_ID, 0L)
        val segStart = prefs.getLong(KEY_CURRENT_SEGMENT_START_EPOCH, nowEpoch)
        val oldCaregiver = currentState.currentCaregiver

        scope.launch {
            val sessionId = ensureActiveSession(nowEpoch, newCaregiver)

            if (currentState.isRunning) {
                val accumulatedActive = prefs.getLong(KEY_ACCUMULATED_ACTIVE_MS, 0L)
                val currentSegDurationMs = max(0L, nowEpoch - segStart)
                val newAccumulatedActive = accumulatedActive + currentSegDurationMs

                // Close current active segment with notes and observation
                if (activeSegId > 0L) {
                    pmkRepository.updateSegment(
                        PmkSegment(
                            id = activeSegId,
                            sessionId = sessionId,
                            caregiver = oldCaregiver,
                            startTimeEpoch = segStart,
                            endTimeEpoch = nowEpoch,
                            durationMinutes = (currentSegDurationMs / 60000L).toInt().coerceAtLeast(1),
                            status = PmkSegmentStatus.ACTIVE,
                            babyTemperature = temperature,
                            babyResponse = response,
                            notes = notes
                        )
                    )
                }

                // Create new active segment for new caregiver
                val newSegment = PmkSegment(
                    sessionId = sessionId,
                    caregiver = newCaregiver,
                    startTimeEpoch = nowEpoch,
                    endTimeEpoch = null,
                    durationMinutes = 0,
                    status = PmkSegmentStatus.ACTIVE
                )
                val newSegId = pmkRepository.saveSegment(newSegment)

                val session = pmkRepository.getSessionById(sessionId)
                if (session != null) {
                    pmkRepository.saveSession(
                        session.copy(
                            currentCaregiver = newCaregiver,
                            activeDurationMinutes = (newAccumulatedActive / 60000L).toInt()
                        )
                    )
                }

                prefs.edit()
                    .putString(KEY_CURRENT_CAREGIVER, newCaregiver.name)
                    .putLong(KEY_ACTIVE_SESSION_ID, sessionId)
                    .putLong(KEY_ACTIVE_SEGMENT_ID, newSegId)
                    .putLong(KEY_CURRENT_SEGMENT_START_EPOCH, nowEpoch)
                    .putLong(KEY_ACCUMULATED_ACTIVE_MS, newAccumulatedActive)
                    .apply()

                _timerState.update {
                    it.copy(
                        currentCaregiver = newCaregiver,
                        activeSessionId = sessionId,
                        currentSegmentStartEpoch = nowEpoch,
                        babyTemperature = temperature ?: it.babyTemperature,
                        babyResponse = response ?: it.babyResponse
                    )
                }
            } else {
                // If PAUSED or IDLE, simply change current caregiver
                prefs.edit().putString(KEY_CURRENT_CAREGIVER, newCaregiver.name).apply()
                _timerState.update {
                    it.copy(currentCaregiver = newCaregiver)
                }
            }

            refreshTodayTotal()
        }
    }

    fun setNotes(notes: String) {
        prefs.edit().putString(KEY_NOTES, notes).apply()
        _timerState.update { it.copy(notes = notes) }
    }

    fun setBabyObservation(temp: Double?, response: String?) {
        prefs.edit()
            .putString(KEY_BABY_TEMP, temp?.toString())
            .putString(KEY_BABY_RESPONSE, response)
            .apply()

        _timerState.update {
            it.copy(babyTemperature = temp, babyResponse = response)
        }
    }

    suspend fun finishSession(
        finalTemp: Double? = null,
        finalResponse: String? = null,
        finalNotes: String? = null
    ): PmkSession? {
        val currentState = _timerState.value
        val nowEpoch = System.currentTimeMillis()
        val activeSegId = prefs.getLong(KEY_ACTIVE_SEGMENT_ID, 0L)
        val segStart = prefs.getLong(KEY_CURRENT_SEGMENT_START_EPOCH, nowEpoch)
        val accumulatedActive = prefs.getLong(KEY_ACCUMULATED_ACTIVE_MS, 0L)
        val accumulatedPause = prefs.getLong(KEY_ACCUMULATED_PAUSE_MS, 0L)

        val sessionId = ensureActiveSession(nowEpoch, currentState.currentCaregiver)

        var totalActiveMs = accumulatedActive
        var totalPauseMs = accumulatedPause

        if (currentState.isRunning) {
            val segMs = max(0L, nowEpoch - segStart)
            totalActiveMs += segMs
            if (activeSegId > 0L) {
                pmkRepository.updateSegment(
                    PmkSegment(
                        id = activeSegId,
                        sessionId = sessionId,
                        caregiver = currentState.currentCaregiver,
                        startTimeEpoch = segStart,
                        endTimeEpoch = nowEpoch,
                        durationMinutes = (segMs / 60000L).toInt().coerceAtLeast(1),
                        status = PmkSegmentStatus.ACTIVE,
                        babyTemperature = finalTemp ?: currentState.babyTemperature,
                        babyResponse = finalResponse ?: currentState.babyResponse,
                        notes = finalNotes ?: currentState.notes
                    )
                )
            }
        } else if (currentState.isPaused) {
            val pauseStart = prefs.getLong(KEY_PAUSE_START_EPOCH, nowEpoch)
            val pauseMs = max(0L, nowEpoch - pauseStart)
            totalPauseMs += pauseMs
            if (activeSegId > 0L) {
                pmkRepository.updateSegment(
                    PmkSegment(
                        id = activeSegId,
                        sessionId = sessionId,
                        caregiver = currentState.currentCaregiver,
                        startTimeEpoch = pauseStart,
                        endTimeEpoch = nowEpoch,
                        durationMinutes = (pauseMs / 60000L).toInt().coerceAtLeast(1),
                        status = PmkSegmentStatus.PAUSED,
                        pauseReason = currentState.pauseReason
                    )
                )
            }
        }

        val activeDurationMinutes = ((totalActiveMs + 30000L) / 60000L).toInt().coerceAtLeast(1)
        val pauseDurationMinutes = (totalPauseMs / 60000L).toInt()
        val babyId = prefs.getLong(KEY_BABY_ID, 1L)
        val sessionStart = prefs.getLong(KEY_SESSION_START_EPOCH, nowEpoch - totalActiveMs)

        val completedSession = PmkSession(
            id = sessionId,
            babyId = babyId,
            startTimeEpoch = sessionStart,
            endTimeEpoch = nowEpoch,
            durationMinutes = activeDurationMinutes,
            targetDurationMinutes = 1200,
            source = PmkSource.TIMER,
            currentCaregiver = currentState.currentCaregiver,
            status = TimerStatus.COMPLETED,
            activeDurationMinutes = activeDurationMinutes,
            pauseDurationMinutes = pauseDurationMinutes,
            babyTemperature = finalTemp ?: currentState.babyTemperature,
            babyResponse = finalResponse ?: currentState.babyResponse,
            notes = finalNotes ?: currentState.notes.ifBlank { null }
        )

        // Update session in Room
        pmkRepository.saveSession(completedSession)

        // Reset runtime state
        reset()
        refreshTodayTotal()

        return completedSession
    }

    fun reset() {
        tickerJob?.cancel()
        cancelPauseLimitAlarm()
        actualNotificationManager.cancelPmkPauseLimitExceededNotification()
        hasAlertedPauseLimit = false

        val lastCaregiver = prefs.getString(KEY_CURRENT_CAREGIVER, PmkCaregiver.IBU.name)
        prefs.edit().clear().apply()
        _timerState.update {
            PmkTimerState(
                status = TimerStatus.IDLE,
                currentCaregiver = PmkCaregiver.fromString(lastCaregiver),
                todayTotalActiveSeconds = it.todayTotalActiveSeconds
            )
        }
    }

    fun refreshTodayTotal() {
        scope.launch {
            val babyId = prefs.getLong(KEY_BABY_ID, 1L)
            val timeline = getDailyTimelineUseCase(babyId, Calendar.getInstance()).firstOrNull()
            if (timeline != null) {
                _timerState.update {
                    it.copy(todayTotalActiveSeconds = timeline.totalActiveMinutes * 60L)
                }
            }
        }
    }

    fun ensureTickerRunning() {
        if (!isTickerEnabled) return
        val status = _timerState.value.status
        if (status == TimerStatus.RUNNING || status == TimerStatus.PAUSED) {
            startTicker()
        }
    }

    private fun startTicker() {
        if (!isTickerEnabled) return
        if (tickerJob?.isActive == true) return
        tickerJob = scope.launch {
            while (isActive) {
                val status = _timerState.value.status
                val nowEpoch = System.currentTimeMillis()

                if (status == TimerStatus.RUNNING) {
                    val segStart = prefs.getLong(KEY_CURRENT_SEGMENT_START_EPOCH, 0L)
                    if (segStart == 0L) {
                        _timerState.update {
                            it.copy(elapsedSeconds = it.elapsedSeconds + 1L)
                        }
                    } else {
                        val accumulatedActive = prefs.getLong(KEY_ACCUMULATED_ACTIVE_MS, 0L)
                        val currentSegMs = max(0L, nowEpoch - segStart)
                        val totalActiveMs = accumulatedActive + currentSegMs

                        _timerState.update {
                            it.copy(elapsedSeconds = totalActiveMs / 1000L)
                        }
                    }
                } else if (status == TimerStatus.PAUSED) {
                    val pauseStart = prefs.getLong(KEY_PAUSE_START_EPOCH, nowEpoch)
                    val pauseMs = max(0L, nowEpoch - pauseStart)
                    val pauseSec = pauseMs / 1000L

                    if (pauseSec >= PAUSE_LIMIT_SECONDS && !hasAlertedPauseLimit) {
                        hasAlertedPauseLimit = true
                        actualNotificationManager.showPmkPauseLimitExceededNotification()
                    }

                    _timerState.update {
                        it.copy(pauseElapsedSeconds = pauseSec)
                    }
                }

                delay(1000)
            }
        }
    }
}
