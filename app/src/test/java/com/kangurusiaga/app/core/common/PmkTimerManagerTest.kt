package com.kangurusiaga.app.core.common

import android.content.Context
import android.content.SharedPreferences
import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.domain.model.PmkCaregiver
import com.kangurusiaga.app.domain.model.PmkPauseReason
import com.kangurusiaga.app.domain.model.PmkSegment
import com.kangurusiaga.app.domain.model.PmkSegmentStatus
import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.model.TimerStatus
import com.kangurusiaga.app.domain.repository.PmkRepository
import com.kangurusiaga.app.domain.usecase.DailyTimeline
import com.kangurusiaga.app.domain.usecase.GetDailyPmkTimelineUseCase
import com.kangurusiaga.app.core.notification.KanguruNotificationManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class PmkTimerManagerTest {

    private val context: Context = mockk(relaxed = true)
    private val prefs: SharedPreferences = mockk(relaxed = true)
    private val editor: SharedPreferences.Editor = mockk(relaxed = true)
    private val repository: PmkRepository = mockk(relaxed = true)
    private val getDailyTimelineUseCase: GetDailyPmkTimelineUseCase = mockk(relaxed = true)
    private val notificationManager: KanguruNotificationManager = mockk(relaxed = true)

    private val memoryPrefs = mutableMapOf<String, Any?>()

    @Before
    fun setup() {
        memoryPrefs.clear()
        every { context.getSharedPreferences(any(), any()) } returns prefs
        every { prefs.edit() } returns editor

        // Mock shared preferences get/put in memory
        every { prefs.getString(any(), any()) } answers {
            val key = firstArg<String>()
            val def = secondArg<String?>()
            (memoryPrefs[key] as? String) ?: def
        }
        every { prefs.getLong(any(), any()) } answers {
            val key = firstArg<String>()
            val def = secondArg<Long>()
            (memoryPrefs[key] as? Long) ?: def
        }
        every { editor.putString(any(), any()) } answers {
            val key = firstArg<String>()
            val v = secondArg<String?>()
            if (v != null) {
                memoryPrefs[key] = v
            } else {
                memoryPrefs.remove(key)
            }
            editor
        }
        every { editor.putLong(any(), any()) } answers {
            val key = firstArg<String>()
            val v = secondArg<Long>()
            memoryPrefs[key] = v
            editor
        }
        every { editor.clear() } answers {
            memoryPrefs.clear()
            editor
        }
        every { editor.apply() } returns Unit

        coEvery { repository.saveSession(any()) } returns 1L
        coEvery { repository.saveSegment(any()) } returns 10L
        every { repository.getSegmentsInRange(any(), any(), any()) } returns flowOf(emptyList())
        every { getDailyTimelineUseCase(any(), any()) } returns flowOf(
            DailyTimeline(
                dayEpoch = 0L,
                dayDateFormatted = "Hari ini",
                blocks = emptyList(),
                totalActiveMinutes = 60,
                totalPauseMinutes = 0,
                ibuMinutes = 60,
                ayahMinutes = 0,
                pendampingMinutes = 0
            )
        )
    }

    private fun createManager(testDispatcher: kotlinx.coroutines.CoroutineDispatcher): PmkTimerManager {
        return PmkTimerManager(
            context = context,
            pmkRepository = repository,
            getDailyTimelineUseCase = getDailyTimelineUseCase,
            defaultDispatcher = testDispatcher,
            notificationManager = notificationManager
        ).apply {
            isTickerEnabled = false
            stopTicker()
        }
    }

    @Test
    fun start_createsSessionAndInitialActiveSegment() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        val manager = createManager(testDispatcher)

        manager.start(babyId = 1L, initialCaregiver = PmkCaregiver.IBU)

        val state = manager.timerState.value
        assertThat(state.status).isEqualTo(TimerStatus.RUNNING)
        assertThat(state.currentCaregiver).isEqualTo(PmkCaregiver.IBU)

        coVerify { repository.saveSession(match { it.status == TimerStatus.RUNNING }) }
        coVerify { repository.saveSegment(match { it.status == PmkSegmentStatus.ACTIVE && it.caregiver == PmkCaregiver.IBU }) }
    }

    @Test
    fun switchCaregiver_closesOldSegmentAndOpensNewSegmentWithoutStopping() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        val manager = createManager(testDispatcher)
        manager.start(babyId = 1L, initialCaregiver = PmkCaregiver.IBU)

        // Handover to Ayah
        manager.switchCaregiver(
            newCaregiver = PmkCaregiver.AYAH,
            temperature = 36.8,
            response = "Tenang & Rileks",
            notes = "Ibu istirahat"
        )

        val state = manager.timerState.value
        assertThat(state.currentCaregiver).isEqualTo(PmkCaregiver.AYAH)

        // Verify new segment for Ayah is saved
        coVerify { repository.saveSegment(match { it.caregiver == PmkCaregiver.AYAH && it.status == PmkSegmentStatus.ACTIVE }) }
    }

    @Test
    fun pauseAndResume_togglesStatusAndTracksReasons() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        val manager = createManager(testDispatcher)
        manager.start(babyId = 1L, initialCaregiver = PmkCaregiver.IBU)

        // Pause for diaper change
        manager.pause(reason = PmkPauseReason.DIAPER, note = "Ganti popok basah")

        val pausedState = manager.timerState.value
        assertThat(pausedState.status).isEqualTo(TimerStatus.PAUSED)
        assertThat(pausedState.pauseReason).isEqualTo(PmkPauseReason.DIAPER.title)
        assertThat(pausedState.todayTotalActiveSeconds).isEqualTo(0L)

        // Resume session
        manager.resume()

        val resumedState = manager.timerState.value
        assertThat(resumedState.status).isEqualTo(TimerStatus.RUNNING)
        assertThat(resumedState.pauseReason).isNull()
        assertThat(resumedState.todayTotalActiveSeconds).isEqualTo(0L)
    }

    @Test
    fun finishSession_completesAndClearsActiveTimer() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        val manager = createManager(testDispatcher)
        manager.start(babyId = 1L, initialCaregiver = PmkCaregiver.IBU)

        val session = manager.finishSession(finalTemp = 36.9, finalResponse = "Tidur Pulas")

        assertThat(session).isNotNull()
        assertThat(session?.status).isEqualTo(TimerStatus.COMPLETED)
        assertThat(session?.babyTemperature).isEqualTo(36.9)

        val finalState = manager.timerState.value
        assertThat(finalState.status).isEqualTo(TimerStatus.IDLE)
    }

    @Test
    fun setCaregiver_updatesCaregiverBeforeStart() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        val manager = createManager(testDispatcher)

        manager.setCaregiver(PmkCaregiver.AYAH)

        val state = manager.timerState.value
        assertThat(state.currentCaregiver).isEqualTo(PmkCaregiver.AYAH)
    }

    @Test
    fun autoFinishPeriod_savesCompletedSessionAndResetsToIdle() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        val manager = createManager(testDispatcher)
        manager.start(babyId = 1L, initialCaregiver = PmkCaregiver.IBU)

        manager.autoFinishPeriod(periodNumber = 1)

        val state = manager.timerState.value
        assertThat(state.status).isEqualTo(TimerStatus.IDLE)
        assertThat(state.elapsedSeconds).isEqualTo(0L)
        coVerify { notificationManager.showPmkPeriodCompletedNotification(1) }
    }

    @Test
    fun start_onNewDay_resetsTimerToZero() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        val manager = createManager(testDispatcher)

        // Simulate last day was yesterday
        val calYesterday = java.util.Calendar.getInstance().apply {
            add(java.util.Calendar.DAY_OF_YEAR, -1)
        }
        memoryPrefs["timer_last_day_epoch"] = calYesterday.timeInMillis

        manager.start(babyId = 1L, initialCaregiver = PmkCaregiver.IBU)

        val state = manager.timerState.value
        assertThat(state.status).isEqualTo(TimerStatus.RUNNING)
        assertThat(state.elapsedSeconds).isEqualTo(0L)
        assertThat(state.todayTotalActiveSeconds).isEqualTo(0L)
        assertThat(state.currentPeriod).isEqualTo(1)
    }

    @Test
    fun start_onSameDayWithCompletedSessions_continuesFromAccumulatedSeconds() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        val manager = createManager(testDispatcher)

        val cal = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 10)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        val startEpoch = cal.timeInMillis
        val endEpoch = startEpoch + 7200_000L
        memoryPrefs["timer_last_day_epoch"] = startEpoch

        // Mock 2 hours completed earlier today (10:00 - 12:00)
        val segment = PmkSegment(
            id = 1L,
            sessionId = 99L,
            caregiver = PmkCaregiver.IBU,
            startTimeEpoch = startEpoch,
            endTimeEpoch = endEpoch,
            durationMinutes = 120,
            status = PmkSegmentStatus.ACTIVE
        )
        every { repository.getSegmentsInRange(any(), any(), any()) } returns flowOf(listOf(segment))

        manager.start(babyId = 1L, initialCaregiver = PmkCaregiver.IBU)

        val state = manager.timerState.value
        assertThat(state.status).isEqualTo(TimerStatus.RUNNING)
        assertThat(state.todayTotalActiveSeconds).isEqualTo(7200L)
        assertThat(state.currentPeriod).isEqualTo(1)
    }

    @Test
    fun autoFinishPeriod_setsPeriod2_andNextManualStartBeginsPeriod2() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        val manager = createManager(testDispatcher)
        manager.start(babyId = 1L, initialCaregiver = PmkCaregiver.IBU)

        // Mock 8 hours completed session in repository
        val cal = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 2)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        val startEpoch = cal.timeInMillis
        val endEpoch = startEpoch + 28800_000L
        val segPeriod1 = PmkSegment(
            id = 1L,
            sessionId = 99L,
            caregiver = PmkCaregiver.IBU,
            startTimeEpoch = startEpoch,
            endTimeEpoch = endEpoch,
            durationMinutes = 480,
            status = PmkSegmentStatus.ACTIVE
        )
        every { repository.getSegmentsInRange(any(), any(), any()) } returns flowOf(listOf(segPeriod1))

        // 8 hours period 1 finished automatically
        manager.autoFinishPeriod(periodNumber = 1)

        val completedState = manager.timerState.value
        assertThat(completedState.status).isEqualTo(TimerStatus.IDLE)
        assertThat(completedState.todayTotalActiveSeconds).isEqualTo(28800L)
        assertThat(completedState.currentPeriod).isEqualTo(2)

        // User manually starts PMK again for Period 2
        manager.start(babyId = 1L, initialCaregiver = PmkCaregiver.AYAH)

        val period2State = manager.timerState.value
        assertThat(period2State.status).isEqualTo(TimerStatus.RUNNING)
        assertThat(period2State.currentCaregiver).isEqualTo(PmkCaregiver.AYAH)
        assertThat(period2State.todayTotalActiveSeconds).isEqualTo(28800L)
        assertThat(period2State.elapsedSeconds).isEqualTo(0L)
        assertThat(period2State.currentPeriod).isEqualTo(2)
    }
}
