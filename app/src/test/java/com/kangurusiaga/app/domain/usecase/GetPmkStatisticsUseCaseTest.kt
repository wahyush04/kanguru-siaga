package com.kangurusiaga.app.domain.usecase

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
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

        every {
            pmkRepository.getSessionsForBabyInRange(1L, any(), any())
        } returns flowOf(listOf(session1, session2))

        useCase(1L, StatsPeriod.THIS_WEEK, cal).test {
            val stats = awaitItem()
            assertThat(stats.totalSessions).isEqualTo(2)
            assertThat(stats.totalDurationMinutes).isEqualTo(180)
            assertThat(stats.averageMinutesPerSession).isEqualTo(90)
            assertThat(stats.longestSessionMinutes).isEqualTo(120)
            assertThat(stats.averageTemperature).isEqualTo(36.9)
            assertThat(stats.calmnessPercentage).isEqualTo(100)
            assertThat(stats.dailyDurations).hasSize(7)
            awaitComplete()
        }
    }
}
