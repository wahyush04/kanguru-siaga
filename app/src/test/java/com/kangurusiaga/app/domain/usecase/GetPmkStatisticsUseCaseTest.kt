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

    @Test
    fun getPmkStatistics_withFourAlignedBabyResponses_correctlyAggregatesPercentages() = runTest {
        val sundayCal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 25, 15, 0, 0)
        }

        val session1 = PmkSession(
            id = 1L,
            babyId = 1L,
            startTimeEpoch = sundayCal.timeInMillis - (4 * 3600 * 1000L),
            endTimeEpoch = sundayCal.timeInMillis - (3 * 3600 * 1000L),
            durationMinutes = 60,
            babyResponse = "Tenang"
        )
        val session2 = PmkSession(
            id = 2L,
            babyId = 1L,
            startTimeEpoch = sundayCal.timeInMillis - (3 * 3600 * 1000L),
            endTimeEpoch = sundayCal.timeInMillis - (2 * 3600 * 1000L),
            durationMinutes = 60,
            babyResponse = "Tidur"
        )
        val session3 = PmkSession(
            id = 3L,
            babyId = 1L,
            startTimeEpoch = sundayCal.timeInMillis - (2 * 3600 * 1000L),
            endTimeEpoch = sundayCal.timeInMillis - (3600 * 1000L),
            durationMinutes = 60,
            babyResponse = "Menyusu Aktif"
        )
        val session4 = PmkSession(
            id = 4L,
            babyId = 1L,
            startTimeEpoch = sundayCal.timeInMillis - (3600 * 1000L),
            endTimeEpoch = sundayCal.timeInMillis,
            durationMinutes = 60,
            babyResponse = "Gelisah / Menangis"
        )

        every {
            pmkRepository.getSessionsForBabyInRange(1L, any(), any())
        } returns flowOf(listOf(session1, session2, session3, session4))

        useCase(
            babyId = 1L,
            period = StatsPeriod.THIS_WEEK,
            calendarInstance = sundayCal
        ).test {
            val stats = awaitItem()
            val breakdown = stats.babyResponseBreakdown

            assertThat(breakdown.totalSessions).isEqualTo(4)
            assertThat(breakdown.calmSessions).isEqualTo(1)
            assertThat(breakdown.sleepSessions).isEqualTo(1)
            assertThat(breakdown.activeFeedingSessions).isEqualTo(1)
            assertThat(breakdown.restlessCryingSessions).isEqualTo(1)

            assertThat(breakdown.calmPercent).isEqualTo(25)
            assertThat(breakdown.sleepPercent).isEqualTo(25)
            assertThat(breakdown.activeFeedingPercent).isEqualTo(25)
            assertThat(breakdown.restlessCryingPercent).isEqualTo(25)

            // Tenang + Tidur = 50%
            assertThat(breakdown.overallCalmPercentage).isEqualTo(50)

            awaitComplete()
        }
    }

    @Test
    fun getPmkStatistics_isWeeklyTargetMet_isFalse_whenNotAllSevenDaysAchieveEightHours() = runTest {
        // Monday 19 Oct 2026 to Sunday 25 Oct 2026
        val sundayCal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 25, 20, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Create sessions for only 5 days (Mon-Fri) with 12 hours (720 min) each -> Total 60 hours (> 56h target)
        // Saturday and Sunday have 0 hours.
        val sessions = (0..4).map { dayOffset ->
            val dayCal = Calendar.getInstance().apply {
                set(2026, Calendar.OCTOBER, 19 + dayOffset, 8, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val start = dayCal.timeInMillis
            val end = start + (720 * 60 * 1000L)
            PmkSession(
                id = (dayOffset + 1).toLong(),
                babyId = 1L,
                startTimeEpoch = start,
                endTimeEpoch = end,
                durationMinutes = 720,
                activeDurationMinutes = 720,
                currentCaregiver = PmkCaregiver.IBU,
                source = PmkSource.TIMER
            )
        }

        every {
            pmkRepository.getSessionsForBabyInRange(1L, any(), any())
        } returns flowOf(sessions)

        useCase(
            babyId = 1L,
            period = StatsPeriod.THIS_WEEK,
            calendarInstance = sundayCal
        ).test {
            val stats = awaitItem()
            // Even though total is 60 hours (3600 min), targetAchievedDays is only 5.
            assertThat(stats.targetAchievedDays).isEqualTo(5)
            assertThat(stats.totalDurationMinutes).isEqualTo(3600)
            // MUST be false because not all 7 days achieved >= 8 hours
            assertThat(stats.isWeeklyTargetMet).isFalse()
            awaitComplete()
        }
    }

    @Test
    fun getPmkStatistics_isWeeklyTargetMet_isTrue_whenAllSevenDaysAchieveAtLeastEightHours() = runTest {
        val sundayCal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 25, 20, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Create sessions for all 7 days (Mon-Sun), each exactly 8 hours (480 min)
        val sessions = (0..6).map { dayOffset ->
            val dayCal = Calendar.getInstance().apply {
                set(2026, Calendar.OCTOBER, 19 + dayOffset, 8, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val start = dayCal.timeInMillis
            val end = start + (480 * 60 * 1000L)
            PmkSession(
                id = (dayOffset + 1).toLong(),
                babyId = 1L,
                startTimeEpoch = start,
                endTimeEpoch = end,
                durationMinutes = 480,
                activeDurationMinutes = 480,
                currentCaregiver = PmkCaregiver.IBU,
                source = PmkSource.TIMER
            )
        }

        every {
            pmkRepository.getSessionsForBabyInRange(1L, any(), any())
        } returns flowOf(sessions)

        useCase(
            babyId = 1L,
            period = StatsPeriod.THIS_WEEK,
            calendarInstance = sundayCal
        ).test {
            val stats = awaitItem()
            assertThat(stats.targetAchievedDays).isEqualTo(7)
            assertThat(stats.isWeeklyTargetMet).isTrue()
            awaitComplete()
        }
    }

    @Test
    fun getPmkStatistics_customFirstDayOfWeek_startsTrendFromFirstTimerDay() = runTest {
        // First timer PMK was on Wednesday 14 Oct 2026
        val firstPmkCal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 14, 10, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val firstPmkEpoch = firstPmkCal.timeInMillis

        // Current time is Thursday 22 Oct 2026 (Next week)
        val currentCal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 22, 14, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        every {
            pmkRepository.getSessionsForBabyInRange(1L, any(), any())
        } returns flowOf(emptyList())

        // 1. THIS_WEEK: should start from Wednesday 21 Oct to Tuesday 27 Oct
        useCase(
            babyId = 1L,
            period = StatsPeriod.THIS_WEEK,
            calendarInstance = currentCal,
            firstPmkEpoch = firstPmkEpoch
        ).test {
            val stats = awaitItem()
            assertThat(stats.dailyDurations).hasSize(7)
            val dayLabels = stats.dailyDurations.map { it.dayLabel }
            assertThat(dayLabels).containsExactly("Rab", "Kam", "Jum", "Sab", "Min", "Sen", "Sel").inOrder()
            awaitComplete()
        }

        // 2. LAST_WEEK: should also start from Wednesday 14 Oct to Tuesday 20 Oct
        useCase(
            babyId = 1L,
            period = StatsPeriod.LAST_WEEK,
            calendarInstance = currentCal,
            firstPmkEpoch = firstPmkEpoch
        ).test {
            val stats = awaitItem()
            assertThat(stats.dailyDurations).hasSize(7)
            val dayLabels = stats.dailyDurations.map { it.dayLabel }
            assertThat(dayLabels).containsExactly("Rab", "Kam", "Jum", "Sab", "Min", "Sen", "Sel").inOrder()
            awaitComplete()
        }
    }
}
