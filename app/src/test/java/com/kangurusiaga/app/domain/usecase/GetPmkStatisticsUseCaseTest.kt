package com.kangurusiaga.app.domain.usecase

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.domain.model.PmkCaregiver
import com.kangurusiaga.app.domain.model.PmkSegment
import com.kangurusiaga.app.domain.model.PmkSegmentStatus
import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.model.PmkSource
import com.kangurusiaga.app.domain.model.StatsPeriod
import com.kangurusiaga.app.domain.repository.PmkRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.Calendar

class GetPmkStatisticsUseCaseTest {

    private val pmkRepository: PmkRepository = mockk()
    private val useCase = GetPmkStatisticsUseCase(pmkRepository)

    @Test
    fun getPmkStatistics_calculatesWeeklyMetricsAndComplianceCorrectly() = runTest {
        val cal = Calendar.getInstance().apply {
            set(2026, Calendar.SEPTEMBER, 24, 10, 0, 0)
        }

        val session1 = PmkSession(
            id = 1L,
            babyId = 1L,
            startTimeEpoch = cal.timeInMillis - (2 * 3600 * 1000L),
            endTimeEpoch = cal.timeInMillis - (3600 * 1000L),
            durationMinutes = 60,
            source = PmkSource.TIMER,
            babyTemperature = 36.8,
            babyResponse = "Tidur Tenang"
        )
        val session2 = PmkSession(
            id = 2L,
            babyId = 1L,
            startTimeEpoch = cal.timeInMillis - (5 * 3600 * 1000L),
            endTimeEpoch = cal.timeInMillis - (3 * 3600 * 1000L),
            durationMinutes = 120,
            source = PmkSource.TIMER,
            babyTemperature = 37.0,
            babyResponse = "Tenang"
        )

        val birthCal = Calendar.getInstance().apply {
            set(2026, Calendar.SEPTEMBER, 1, 0, 0, 0)
        }

        every {
            pmkRepository.getSessionsForBabyInRange(1L, any(), any())
        } returns flowOf(listOf(session1, session2))

        useCase(
            babyId = 1L,
            period = StatsPeriod.THIS_WEEK,
            calendarInstance = cal,
            babyBirthDateEpoch = birthCal.timeInMillis
        ).test {
            val stats = awaitItem()
            assertThat(stats.totalSessions).isEqualTo(2)
            assertThat(stats.totalDurationMinutes).isEqualTo(180)
            assertThat(stats.averageMinutesPerSession).isEqualTo(90)
            assertThat(stats.longestSessionMinutes).isEqualTo(120)
            assertThat(stats.averageTemperature).isEqualTo(36.9)
            assertThat(stats.calmnessPercentage).isEqualTo(100)
            assertThat(stats.dailyDurations).hasSize(7)
            assertThat(stats.babyChronologicalWeek).isEqualTo(4)
            assertThat(stats.babyResponseBreakdown.deepSleepSessions).isEqualTo(1)
            assertThat(stats.babyResponseBreakdown.alertCalmSessions).isEqualTo(1)
            assertThat(stats.babyResponseBreakdown.overallCalmPercentage).isEqualTo(100)
            assertThat(stats.isWeeklyTargetMet).isFalse()
            awaitComplete()
        }
    }

    @Test
    fun getPmkStatistics_includesAllSegmentsForDailyTrendAndCaregiverDistribution() = runTest {
        // Today is Sunday 25 Oct 2026
        val sundayCal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 25, 15, 0, 0)
        }

        // Saturday 24 Oct 2026
        val satCal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 24, 8, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val satStart = satCal.timeInMillis
        val seg1DurationMs = 681 * 60 * 1000L // 11j 21m
        val seg1End = satStart + seg1DurationMs
        val seg2DurationMs = 77 * 60 * 1000L  // 1j 17m
        val seg2End = seg1End + seg2DurationMs

        val seg1 = PmkSegment(
            id = 101L,
            sessionId = 1L,
            caregiver = PmkCaregiver.IBU,
            startTimeEpoch = satStart,
            endTimeEpoch = seg1End,
            durationMinutes = 681,
            status = PmkSegmentStatus.ACTIVE,
            babyTemperature = 36.8,
            babyResponse = "Tidur Nyenyak"
        )
        val seg2 = PmkSegment(
            id = 102L,
            sessionId = 1L,
            caregiver = PmkCaregiver.AYAH,
            startTimeEpoch = seg1End,
            endTimeEpoch = seg2End,
            durationMinutes = 77,
            status = PmkSegmentStatus.ACTIVE,
            babyTemperature = 37.0,
            babyResponse = "Tenang Waspada"
        )

        // The session entity in continuous PMK might only retain the last caregiver or latest duration
        val multiSegmentSession = PmkSession(
            id = 1L,
            babyId = 1L,
            startTimeEpoch = satStart,
            endTimeEpoch = seg2End,
            durationMinutes = 77,
            activeDurationMinutes = 77,
            currentCaregiver = PmkCaregiver.AYAH,
            source = PmkSource.TIMER,
            segments = listOf(seg1, seg2)
        )

        every {
            pmkRepository.getSessionsForBabyInRange(1L, any(), any())
        } returns flowOf(listOf(multiSegmentSession))

        useCase(
            babyId = 1L,
            period = StatsPeriod.THIS_WEEK,
            calendarInstance = sundayCal
        ).test {
            val stats = awaitItem()
            // Total should be 681 + 77 = 758 mins (12j 39m), NOT just 77 mins (1.3h)!
            assertThat(stats.totalDurationMinutes).isEqualTo(758)
            assertThat(stats.dailyDurations).hasSize(7)

            val saturday = stats.dailyDurations.find { it.dayLabel == "Sab" }
            assertThat(saturday).isNotNull()
            assertThat(saturday!!.durationMinutes).isEqualTo(758)
            assertThat(saturday.durationHours).isWithin(0.1f).of(12.63f)
            assertThat(saturday.isAboveTarget).isTrue()

            // Caregiver distribution should sum Ibu (681m) and Ayah (77m)
            assertThat(stats.caregiverDistribution.ibuMinutes).isEqualTo(681)
            assertThat(stats.caregiverDistribution.ayahMinutes).isEqualTo(77)
            assertThat(stats.caregiverDistribution.ibuPercent).isEqualTo(90)
            assertThat(stats.caregiverDistribution.ayahPercent).isEqualTo(10)

            awaitComplete()
        }
    }
}
