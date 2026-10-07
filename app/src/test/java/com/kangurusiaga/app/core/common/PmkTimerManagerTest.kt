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
        return PmkTimerManager(context, repository, getDailyTimelineUseCase, testDispatcher).apply {
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

        // Resume session
        manager.resume()

        val resumedState = manager.timerState.value
        assertThat(resumedState.status).isEqualTo(TimerStatus.RUNNING)
        assertThat(resumedState.pauseReason).isNull()
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
}
