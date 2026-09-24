package com.kangurusiaga.app.core.common

import android.content.Context
import android.content.SharedPreferences
import android.os.SystemClock
import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.model.PmkSource
import com.kangurusiaga.app.domain.model.PmkTimerState
import com.kangurusiaga.app.domain.model.TimerStatus
import com.kangurusiaga.app.domain.repository.PmkRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PmkTimerManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pmkRepository: PmkRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val prefs: SharedPreferences = context.getSharedPreferences("pmk_timer_prefs", Context.MODE_PRIVATE)

    private val _timerState = MutableStateFlow(PmkTimerState())
    val timerState: StateFlow<PmkTimerState> = _timerState.asStateFlow()

    private var tickerJob: Job? = null

    companion object {
        private const val KEY_STATUS = "timer_status"
        private const val KEY_START_REALTIME = "timer_start_realtime"
        private const val KEY_ACCUMULATED_MS = "timer_accumulated_ms"
        private const val KEY_TARGET_MINUTES = "timer_target_minutes"
        private const val KEY_BABY_ID = "timer_baby_id"
        private const val KEY_NOTES = "timer_notes"
        private const val KEY_BABY_TEMP = "timer_baby_temp"
        private const val KEY_BABY_RESPONSE = "timer_baby_response"
        private const val KEY_SESSION_START_EPOCH = "timer_session_start_epoch"
    }

    init {
        restoreTimerState()
    }

    private fun restoreTimerState() {
        val statusStr = prefs.getString(KEY_STATUS, TimerStatus.IDLE.name) ?: TimerStatus.IDLE.name
        val status = try {
            TimerStatus.valueOf(statusStr)
        } catch (_: Exception) {
            TimerStatus.IDLE
        }

        val startRealtime = prefs.getLong(KEY_START_REALTIME, 0L)
        val accumulatedMs = prefs.getLong(KEY_ACCUMULATED_MS, 0L)
        val targetMinutes = prefs.getInt(KEY_TARGET_MINUTES, 60)
        val notes = prefs.getString(KEY_NOTES, "") ?: ""
        val tempRaw = prefs.getString(KEY_BABY_TEMP, null)
        val babyTemp = tempRaw?.toDoubleOrNull()
        val babyResponse = prefs.getString(KEY_BABY_RESPONSE, "Tidur Tenang")

        val elapsedMs = when (status) {
            TimerStatus.RUNNING -> {
                val nowRealtime = SystemClock.elapsedRealtime()
                accumulatedMs + (nowRealtime - startRealtime).coerceAtLeast(0L)
            }
            TimerStatus.PAUSED -> accumulatedMs
            else -> 0L
        }

        _timerState.value = PmkTimerState(
            status = status,
            elapsedSeconds = elapsedMs / 1000L,
            targetDurationMinutes = targetMinutes,
            notes = notes,
            babyTemperature = babyTemp,
            babyResponse = babyResponse
        )

        if (status == TimerStatus.RUNNING) {
            startTicker()
        }
    }

    fun start(babyId: Long, targetMinutes: Int = 60) {
        val nowRealtime = SystemClock.elapsedRealtime()
        val nowEpoch = System.currentTimeMillis()

        prefs.edit()
            .putString(KEY_STATUS, TimerStatus.RUNNING.name)
            .putLong(KEY_START_REALTIME, nowRealtime)
            .putLong(KEY_ACCUMULATED_MS, 0L)
            .putInt(KEY_TARGET_MINUTES, targetMinutes)
            .putLong(KEY_BABY_ID, babyId)
            .putLong(KEY_SESSION_START_EPOCH, nowEpoch)
            .apply()

        _timerState.update {
            it.copy(
                status = TimerStatus.RUNNING,
                elapsedSeconds = 0L,
                targetDurationMinutes = targetMinutes
            )
        }

        startTicker()
    }

    fun pause() {
        if (_timerState.value.status != TimerStatus.RUNNING) return

        val nowRealtime = SystemClock.elapsedRealtime()
        val startRealtime = prefs.getLong(KEY_START_REALTIME, nowRealtime)
        val accumulatedMs = prefs.getLong(KEY_ACCUMULATED_MS, 0L)
        val totalMs = accumulatedMs + (nowRealtime - startRealtime).coerceAtLeast(0L)

        prefs.edit()
            .putString(KEY_STATUS, TimerStatus.PAUSED.name)
            .putLong(KEY_ACCUMULATED_MS, totalMs)
            .apply()

        tickerJob?.cancel()

        _timerState.update {
            it.copy(
                status = TimerStatus.PAUSED,
                elapsedSeconds = totalMs / 1000L
            )
        }
    }

    fun resume() {
        if (_timerState.value.status != TimerStatus.PAUSED) return

        val nowRealtime = SystemClock.elapsedRealtime()

        prefs.edit()
            .putString(KEY_STATUS, TimerStatus.RUNNING.name)
            .putLong(KEY_START_REALTIME, nowRealtime)
            .apply()

        _timerState.update {
            it.copy(status = TimerStatus.RUNNING)
        }

        startTicker()
    }

    fun setTargetMinutes(minutes: Int) {
        prefs.edit().putInt(KEY_TARGET_MINUTES, minutes).apply()
        _timerState.update { it.copy(targetDurationMinutes = minutes) }
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

    suspend fun finishSession(): PmkSession? {
        val currentState = _timerState.value
        val nowRealtime = SystemClock.elapsedRealtime()
        val accumulatedMs = prefs.getLong(KEY_ACCUMULATED_MS, 0L)
        val startRealtime = prefs.getLong(KEY_START_REALTIME, nowRealtime)

        val totalMs = if (currentState.status == TimerStatus.RUNNING) {
            accumulatedMs + (nowRealtime - startRealtime).coerceAtLeast(0L)
        } else {
            accumulatedMs
        }

        val durationMinutes = ((totalMs + 30000L) / 60000L).toInt().coerceAtLeast(1)
        val babyId = prefs.getLong(KEY_BABY_ID, 1L)
        val startEpoch = prefs.getLong(KEY_SESSION_START_EPOCH, System.currentTimeMillis() - totalMs)
        val endEpoch = System.currentTimeMillis()

        val session = PmkSession(
            babyId = babyId,
            startTimeEpoch = startEpoch,
            endTimeEpoch = endEpoch,
            durationMinutes = durationMinutes,
            targetDurationMinutes = currentState.targetDurationMinutes,
            source = PmkSource.TIMER,
            babyTemperature = currentState.babyTemperature,
            babyResponse = currentState.babyResponse,
            notes = currentState.notes.ifBlank { null }
        )

        // Save session to Room
        val savedId = pmkRepository.saveSession(session)

        // Reset timer
        reset()

        return session.copy(id = savedId)
    }

    fun reset() {
        tickerJob?.cancel()
        prefs.edit().clear().apply()
        _timerState.value = PmkTimerState()
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = scope.launch {
            while (isActive) {
                val startRealtime = prefs.getLong(KEY_START_REALTIME, SystemClock.elapsedRealtime())
                val accumulatedMs = prefs.getLong(KEY_ACCUMULATED_MS, 0L)
                val totalMs = accumulatedMs + (SystemClock.elapsedRealtime() - startRealtime).coerceAtLeast(0L)

                _timerState.update {
                    it.copy(
                        status = TimerStatus.RUNNING,
                        elapsedSeconds = totalMs / 1000L
                    )
                }

                delay(500)
            }
        }
    }
}
