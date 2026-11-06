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
import com.kangurusiaga.app.data.repository.FentonGrowthRepository
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
    private val fentonGrowthRepository: FentonGrowthRepository = mockk(relaxed = true)

    private lateinit var useCase: GetPmkWeeklyRecapUseCase

    @Before
    fun setUp() {
        every { fentonGrowthApiClient.readLocalZScoreEntries(any()) } returns emptyList()
        every { fentonGrowthRepository.getPatientRecords(any()) } returns flowOf(emptyList())
        useCase = GetPmkWeeklyRecapUseCase(
            getBabyProfileUseCase = getBabyProfileUseCase,
            pmkRepository = pmkRepository,
            growthRepository = growthRepository,
            fentonGrowthApiClient = fentonGrowthApiClient,
            fentonGrowthRepository = fentonGrowthRepository
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

    @Test
    fun invoke_withMultipleBatchMeasurements_correctlyPicksLatestPmaAndAlignsWithFenton() = runTest {
        val now = System.currentTimeMillis()
        val baby = Baby(
            id = 300L,
            name = "Nirmala",
            gender = Gender.FEMALE,
            birthDateEpochMillis = now - (7L * 24 * 3600 * 1000L),
            birthWeightGram = 1800,
            birthLengthCm = 42.0f,
            birthHeadCircumferenceCm = 30.0f,
            gestationalAgeWeeks = 31,
            createdAt = now - (7L * 24 * 3600 * 1000L)
        )

        // Two batch measurements saved at same timestamp `now`, but different PMA weeks (31 vs 32)
        val w31 = GrowthMeasurement(
            id = 1L,
            babyId = 300L,
            parameter = GrowthParameter.WEIGHT,
            measurementDateEpochMillis = now,
            chronologicalAgeWeeks = 1.0f,
            pmaWeeks = 31.0f,
            value = 1850.0f,
            unit = "g"
        )
        val w32 = GrowthMeasurement(
            id = 2L,
            babyId = 300L,
            parameter = GrowthParameter.WEIGHT,
            measurementDateEpochMillis = now,
            chronologicalAgeWeeks = 1.0f,
            pmaWeeks = 32.0f,
            value = 1950.0f,
            unit = "g"
        )
        val l32 = GrowthMeasurement(
            id = 3L,
            babyId = 300L,
            parameter = GrowthParameter.LENGTH,
            measurementDateEpochMillis = now,
            chronologicalAgeWeeks = 1.0f,
            pmaWeeks = 32.0f,
            value = 46.0f,
            unit = "cm"
        )
        val hc32 = GrowthMeasurement(
            id = 4L,
            babyId = 300L,
            parameter = GrowthParameter.HEAD_CIRCUMFERENCE,
            measurementDateEpochMillis = now,
            chronologicalAgeWeeks = 1.0f,
            pmaWeeks = 32.0f,
            value = 34.0f,
            unit = "cm"
        )

        every { getBabyProfileUseCase() } returns flowOf(baby)
        every { pmkRepository.getSessionsForBaby(300L) } returns flowOf(emptyList())
        // Room DAO returns measurements in pmaWeeks DESC order
        every { growthRepository.getAllMeasurementsDesc(300L) } returns flowOf(listOf(w32, w31, l32, hc32))

        useCase().test {
            val item = awaitItem()
            // Must pick latest PMA (32), so current weight is 1950g, NOT 1850g!
            assertThat(item.currentWeightGram).isEqualTo(1950)
            assertThat(item.weightGainFormatted).isEqualTo("+150g")

            val latestEval = item.weeklyEvaluations.first()
            val bbMetric = latestEval.fentonSummary.metrics.find { it.label == "BB Akhir" }
            val pbMetric = latestEval.fentonSummary.metrics.find { it.label == "PB Akhir" }
            val lkMetric = latestEval.fentonSummary.metrics.find { it.label == "LK Akhir" }

            assertThat(bbMetric?.valueNumber).isEqualTo("1.950")
            assertThat(pbMetric?.valueNumber).isEqualTo("46,0")
            assertThat(lkMetric?.valueNumber).isEqualTo("34,0")

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun invoke_withFirstTimerSessionOnWednesday_alignsEveryWeekStartToWednesday() = runTest {
        // Wednesday 14 Oct 2026 at 10:00
        val wedCal = java.util.Calendar.getInstance().apply {
            set(2026, java.util.Calendar.OCTOBER, 14, 10, 0, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        val wedEpoch = wedCal.timeInMillis

        val baby = Baby(
            id = 400L,
            name = "Arka",
            gender = Gender.MALE,
            birthDateEpochMillis = wedEpoch,
            birthWeightGram = 1850,
            gestationalAgeWeeks = 32,
            createdAt = wedEpoch
        )

        val session = PmkSession(
            id = 1L,
            babyId = 400L,
            startTimeEpoch = wedEpoch,
            endTimeEpoch = wedEpoch + (9 * 3600 * 1000L),
            durationMinutes = 540,
            source = com.kangurusiaga.app.domain.model.PmkSource.TIMER,
            status = TimerStatus.COMPLETED
        )

        every { getBabyProfileUseCase() } returns flowOf(baby)
        every { pmkRepository.getSessionsForBaby(400L) } returns flowOf(listOf(session))
        every { growthRepository.getAllMeasurementsDesc(400L) } returns flowOf(emptyList())

        useCase().test {
            val item = awaitItem()
            val eval = item.weeklyEvaluations.first()
            val dayLabels = eval.dailyActivities.map { it.dayLabel }
            // Must start from Wednesday ("Rab") and end on Tuesday ("Sel")
            assertThat(dayLabels).containsExactly("Rab", "Kam", "Jum", "Sab", "Min", "Sen", "Sel").inOrder()
            cancelAndIgnoreRemainingEvents()
        }
    }
}

