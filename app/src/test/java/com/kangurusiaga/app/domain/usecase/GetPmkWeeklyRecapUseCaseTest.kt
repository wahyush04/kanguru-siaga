package com.kangurusiaga.app.domain.usecase

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.GrowthMeasurement
import com.kangurusiaga.app.domain.model.GrowthParameter
import com.kangurusiaga.app.domain.model.PmkCaregiver
import com.kangurusiaga.app.domain.model.PmkSegment
import com.kangurusiaga.app.domain.model.PmkSegmentStatus
import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.model.TimerStatus
import com.kangurusiaga.app.data.remote.fenton.FentonGrowthApiClient
import com.kangurusiaga.app.domain.repository.GrowthRepository
import com.kangurusiaga.app.domain.repository.PmkRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetPmkWeeklyRecapUseCaseTest {

    private val getBabyProfileUseCase: GetBabyProfileUseCase = mockk()
    private val pmkRepository: PmkRepository = mockk()
    private val growthRepository: GrowthRepository = mockk()
    private val fentonGrowthApiClient: FentonGrowthApiClient = mockk(relaxed = true)

    private lateinit var useCase: GetPmkWeeklyRecapUseCase

    @Before
    fun setUp() {
        every { fentonGrowthApiClient.readLocalZScoreEntries(any()) } returns emptyList()
        useCase = GetPmkWeeklyRecapUseCase(
            getBabyProfileUseCase = getBabyProfileUseCase,
            pmkRepository = pmkRepository,
            growthRepository = growthRepository,
            fentonGrowthApiClient = fentonGrowthApiClient
        )
    }

    @Test
    fun invoke_whenBabyIsNull_returnsEmptyWeeklyRecapData() = runTest {
        every { getBabyProfileUseCase() } returns flowOf(null)

        useCase().test {
            val item = awaitItem()
            assertThat(item.babyName).isEqualTo("Si Kecil")
            assertThat(item.weeklyEvaluations).isEmpty()
            assertThat(item.correlationData.chartPoints).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun invoke_withBabyAndSessions_computesDynamicWeeklyEvaluations() = runTest {
        val now = System.currentTimeMillis()
        val twoWeeksAgo = now - (14L * 24 * 3600 * 1000L)
        val baby = Baby(
            id = 100L,
            name = "Bayi Cantika",
            gender = Gender.FEMALE,
            birthDateEpochMillis = twoWeeksAgo,
            birthWeightGram = 1900,
            gestationalAgeWeeks = 32
        )

        val session1 = PmkSession(
            id = 1L,
            babyId = 100L,
            startTimeEpoch = twoWeeksAgo + (1L * 24 * 3600 * 1000L),
            endTimeEpoch = twoWeeksAgo + (1L * 24 * 3600 * 1000L) + (9 * 3600 * 1000L),
            durationMinutes = 540,
            activeDurationMinutes = 540,
            status = TimerStatus.COMPLETED,
            currentCaregiver = PmkCaregiver.IBU,
            segments = listOf(
                PmkSegment(
                    id = 10L,
                    sessionId = 1L,
                    caregiver = PmkCaregiver.IBU,
                    startTimeEpoch = twoWeeksAgo + (1L * 24 * 3600 * 1000L),
                    endTimeEpoch = twoWeeksAgo + (1L * 24 * 3600 * 1000L) + (9 * 3600 * 1000L),
                    durationMinutes = 540,
                    status = PmkSegmentStatus.ACTIVE,
                    babyTemperature = 36.8
                )
            )
        )

        val measurement = GrowthMeasurement(
            id = 1L,
            babyId = 100L,
            parameter = GrowthParameter.WEIGHT,
            measurementDateEpochMillis = now,
            chronologicalAgeWeeks = 2.0f,
            pmaWeeks = 34.0f,
            value = 2150.0f,
            unit = "g"
        )

        every { getBabyProfileUseCase() } returns flowOf(baby)
        every { pmkRepository.getSessionsForBaby(100L) } returns flowOf(listOf(session1))
        every { growthRepository.getAllMeasurementsDesc(100L) } returns flowOf(listOf(measurement))

        useCase().test {
            val item = awaitItem()
            assertThat(item.babyName).isEqualTo("Bayi Cantika")
            assertThat(item.birthWeightGram).isEqualTo(1900)
            assertThat(item.currentWeightGram).isEqualTo(2150)
            assertThat(item.weeklyEvaluations).isNotEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun invoke_withBabyBornWeeksAgo_onlyEvaluatesWeeksSinceAppUsageStarted() = runTest {
        val now = System.currentTimeMillis()
        val eightWeeksAgo = now - (56L * 24 * 3600 * 1000L)
        // Baby born 8 weeks ago, but registered in app today with no prior sessions
        val baby = Baby(
            id = 200L,
            name = "Nirmala",
            gender = Gender.FEMALE,
            birthDateEpochMillis = eightWeeksAgo,
            birthWeightGram = 1800,
            gestationalAgeWeeks = 32,
            photoUri = "content://media/photo/baby_nirmala.jpg",
            createdAt = now
        )

        val measurement = GrowthMeasurement(
            id = 1L,
            babyId = 200L,
            parameter = GrowthParameter.WEIGHT,
            measurementDateEpochMillis = now,
            chronologicalAgeWeeks = 8.0f,
            pmaWeeks = 40.0f,
            value = 3200.0f,
            unit = "g"
        )

        every { getBabyProfileUseCase() } returns flowOf(baby)
        every { pmkRepository.getSessionsForBaby(200L) } returns flowOf(emptyList())
        every { growthRepository.getAllMeasurementsDesc(200L) } returns flowOf(listOf(measurement))

        useCase().test {
            val item = awaitItem()
            assertThat(item.babyName).isEqualTo("Nirmala")
            assertThat(item.babyPhotoUri).isEqualTo("content://media/photo/baby_nirmala.jpg")
            assertThat(item.birthWeightGram).isEqualTo(1800)
            assertThat(item.currentWeightGram).isEqualTo(3200)
            assertThat(item.pmaWeeksText).isEqualTo("40 Mgg")
            // Must only have 1 week (Minggu 1 Berjalan), not 4 weeks (Minggu 9, 8, 7, 6)
            assertThat(item.weeklyEvaluations.size).isEqualTo(1)
            assertThat(item.weeklyEvaluations.first().weekNumber).isEqualTo(1)
            assertThat(item.weeklyEvaluations.first().isCurrentWeek).isTrue()
            assertThat(item.complianceFormatted).isEqualTo("0/1 Mgg")
            cancelAndIgnoreRemainingEvents()
        }
    }
}

