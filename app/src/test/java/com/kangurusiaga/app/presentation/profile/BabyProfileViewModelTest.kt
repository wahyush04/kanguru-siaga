package com.kangurusiaga.app.presentation.profile

import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.data.local.entity.GrowthRecordEntity
import com.kangurusiaga.app.data.remote.fenton.FentonGrowthApiClient
import com.kangurusiaga.app.data.remote.fenton.FentonZScoreEntry
import com.kangurusiaga.app.data.repository.FentonGrowthRepository
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.GrowthMeasurement
import com.kangurusiaga.app.domain.model.GrowthParameter
import com.kangurusiaga.app.domain.repository.BabyRepository
import com.kangurusiaga.app.domain.repository.GrowthRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BabyProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val babyRepository: BabyRepository = mockk()
    private val growthRepository: GrowthRepository = mockk()
    private val fentonGrowthRepository: FentonGrowthRepository = mockk()
    private val fentonGrowthApiClient: FentonGrowthApiClient = mockk()

    private val dummyBaby = Baby(
        id = 1L,
        name = "Arka",
        gender = Gender.MALE,
        birthDateEpochMillis = System.currentTimeMillis() - (4 * 7 * 24 * 60 * 60 * 1000L),
        birthWeightGram = 1850,
        birthLengthCm = 42.0f,
        birthHeadCircumferenceCm = 30.0f,
        gestationalAgeWeeks = 31
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { babyRepository.getActiveBaby() } returns flowOf(dummyBaby)
        every { growthRepository.getLatestMeasurement(any(), any()) } returns flowOf(null)
        every { fentonGrowthRepository.getLatestRecordFlow(any()) } returns flowOf(null)
        every { fentonGrowthApiClient.getLatestZScoreEntry(any()) } returns null
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `profile metrics match latest week Fenton Z-score growth entry`() = runTest {
        val latestZScore = FentonZScoreEntry(
            dateString = "31-Oct-2026",
            gaWeeks = 35.0f,
            weightGrams = 1950.0,
            weightZScore = -0.45,
            weightPercentile = 32,
            lengthCm = 46.0,
            lengthZScore = -0.73,
            lengthPercentile = 23,
            headCm = 34.0,
            headZScore = -0.55,
            headPercentile = 29
        )
        every { fentonGrowthApiClient.getLatestZScoreEntry(1L) } returns latestZScore

        val viewModel = BabyProfileViewModel(
            babyRepository = babyRepository,
            growthRepository = growthRepository,
            fentonGrowthRepository = fentonGrowthRepository,
            fentonGrowthApiClient = fentonGrowthApiClient
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()

        // Weight metric should match latest week (1.95 kg, P32)
        assertThat(state.weightMetric.isMeasured).isTrue()
        assertThat(state.weightMetric.valueFormatted).isEqualTo("1,95")
        assertThat(state.weightMetric.unit).isEqualTo("kg")
        assertThat(state.weightMetric.percentileText).isEqualTo("P32")
        assertThat(state.weightMetric.statusText).contains("Sesuai Usia Gestasi")

        // Length metric should match latest week (46.0 cm, P23)
        assertThat(state.lengthMetric.isMeasured).isTrue()
        assertThat(state.lengthMetric.valueFormatted).isEqualTo("46,0")
        assertThat(state.lengthMetric.unit).isEqualTo("cm")
        assertThat(state.lengthMetric.percentileText).isEqualTo("P23")
        assertThat(state.lengthMetric.statusText).contains("Normal")

        // Head metric should match latest week (34.0 cm, P29)
        assertThat(state.headMetric.isMeasured).isTrue()
        assertThat(state.headMetric.valueFormatted).isEqualTo("34,0")
        assertThat(state.headMetric.unit).isEqualTo("cm")
        assertThat(state.headMetric.percentileText).isEqualTo("P29")
        assertThat(state.headMetric.statusText).contains("Optimal")
    }

    @Test
    fun `profile metrics fallback to latest growth record when z-score entry is absent`() = runTest {
        val latestRecord = GrowthRecordEntity(
            recordId = 10L,
            babyId = 1L,
            recordedAt = System.currentTimeMillis(),
            pmaWeeks = 35,
            weightGrams = 1950.0,
            lengthCm = 46.0,
            headCircumferenceCm = 34.0,
            calculatedWeightPercentile = "P32",
            clinicalClassification = "AGA (P10-P90)"
        )
        every { fentonGrowthRepository.getLatestRecordFlow(1L) } returns flowOf(latestRecord)

        val viewModel = BabyProfileViewModel(
            babyRepository = babyRepository,
            growthRepository = growthRepository,
            fentonGrowthRepository = fentonGrowthRepository,
            fentonGrowthApiClient = fentonGrowthApiClient
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.weightMetric.valueFormatted).isEqualTo("1,95")
        assertThat(state.weightMetric.percentileText).isEqualTo("P32")
        assertThat(state.lengthMetric.valueFormatted).isEqualTo("46,0")
        assertThat(state.headMetric.valueFormatted).isEqualTo("34,0")
    }
}

