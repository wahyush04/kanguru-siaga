package com.kangurusiaga.app.presentation.growth.batch

import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.GrowthMeasurement
import com.kangurusiaga.app.domain.model.GrowthParameter
import com.kangurusiaga.app.domain.repository.BabyRepository
import com.kangurusiaga.app.domain.usecase.EnsureInitialGrowthDataUseCase
import com.kangurusiaga.app.domain.usecase.SaveBatchGrowthRecordsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
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
class GrowthBatchMeasurementViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val babyRepository: BabyRepository = mockk(relaxed = true)
    private val ensureInitialGrowthDataUseCase: EnsureInitialGrowthDataUseCase = mockk(relaxed = true)
    private val saveBatchGrowthRecordsUseCase: SaveBatchGrowthRecordsUseCase = mockk(relaxed = true)
    private val fentonGrowthApiClient: com.kangurusiaga.app.data.remote.fenton.FentonGrowthApiClient = mockk(relaxed = true)
    private val growthRepository: com.kangurusiaga.app.domain.repository.GrowthRepository = mockk(relaxed = true)

    private val testBaby = Baby(
        id = 1L,
        name = "Bayi Arka Narendra",
        gender = Gender.MALE,
        birthDateEpochMillis = System.currentTimeMillis() - 86400000L * 21,
        birthWeightGram = 1850,
        gestationalAgeWeeks = 32
    )

    private lateinit var viewModel: GrowthBatchMeasurementViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { babyRepository.getActiveBaby() } returns flowOf(testBaby)
        coEvery { ensureInitialGrowthDataUseCase.ensureArkaNarendraSampleData() } returns testBaby

        viewModel = GrowthBatchMeasurementViewModel(
            babyRepository = babyRepository,
            ensureInitialGrowthDataUseCase = ensureInitialGrowthDataUseCase,
            saveBatchGrowthRecordsUseCase = saveBatchGrowthRecordsUseCase,
            fentonGrowthApiClient = fentonGrowthApiClient,
            growthRepository = growthRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_loadsBabyProfileSuccessfully() = runTest {
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.baby?.name).isEqualTo("Bayi Arka Narendra")
        assertThat(state.rows).isNotEmpty()
        assertThat(state.currentPmaWeeks).isEqualTo(35)
    }

    @Test
    fun loadBabyData_prepopulatesExistingDatabaseMeasurementsAndAppendsEmptyRow() = runTest {
        val testWeights = listOf(
            GrowthMeasurement(babyId = 1L, parameter = GrowthParameter.WEIGHT, measurementDateEpochMillis = 1000L, chronologicalAgeWeeks = 0f, pmaWeeks = 32f, value = 1.85f, unit = "kg"),
            GrowthMeasurement(babyId = 1L, parameter = GrowthParameter.WEIGHT, measurementDateEpochMillis = 2000L, chronologicalAgeWeeks = 1f, pmaWeeks = 33f, value = 2.0f, unit = "kg")
        )
        val testLengths = listOf(
            GrowthMeasurement(babyId = 1L, parameter = GrowthParameter.LENGTH, measurementDateEpochMillis = 1000L, chronologicalAgeWeeks = 0f, pmaWeeks = 32f, value = 42f, unit = "cm"),
            GrowthMeasurement(babyId = 1L, parameter = GrowthParameter.LENGTH, measurementDateEpochMillis = 2000L, chronologicalAgeWeeks = 1f, pmaWeeks = 33f, value = 43.5f, unit = "cm")
        )
        every { growthRepository.getMeasurementsAsc(1L, GrowthParameter.WEIGHT) } returns flowOf(testWeights)
        every { growthRepository.getMeasurementsAsc(1L, GrowthParameter.LENGTH) } returns flowOf(testLengths)
        every { growthRepository.getMeasurementsAsc(1L, GrowthParameter.HEAD_CIRCUMFERENCE) } returns flowOf(emptyList())

        val testVm = GrowthBatchMeasurementViewModel(
            babyRepository = babyRepository,
            ensureInitialGrowthDataUseCase = ensureInitialGrowthDataUseCase,
            saveBatchGrowthRecordsUseCase = saveBatchGrowthRecordsUseCase,
            fentonGrowthApiClient = fentonGrowthApiClient,
            growthRepository = growthRepository
        )
        advanceUntilIdle()

        val rows = testVm.uiState.value.rows
        // Should have row for 32, row for 33, and row for 34 (new empty row)
        assertThat(rows).hasSize(3)
        assertThat(rows[0].pmaWeeks).isEqualTo("32")
        assertThat(rows[0].weightGrams).isEqualTo("1850")
        assertThat(rows[0].lengthCm).isEqualTo("42")

        assertThat(rows[1].pmaWeeks).isEqualTo("33")
        assertThat(rows[1].weightGrams).isEqualTo("2000")
        assertThat(rows[1].lengthCm).isEqualTo("43.5")

        assertThat(rows[2].pmaWeeks).isEqualTo("34")
        assertThat(rows[2].weightGrams).isEmpty()
    }

    @Test
    fun addRow_incrementsRowListWithCalculatedNextPma() = runTest {
        advanceUntilIdle()

        val initialCount = viewModel.uiState.value.rows.size
        val expectedNextPma = ((viewModel.uiState.value.rows.lastOrNull()?.pmaWeeks?.toIntOrNull() ?: 35) + 1).toString()
        viewModel.addRow()

        val newCount = viewModel.uiState.value.rows.size
        assertThat(newCount).isEqualTo(initialCount + 1)
        assertThat(viewModel.uiState.value.rows.last().pmaWeeks).isEqualTo(expectedNextPma)
    }

    @Test
    fun deleteRow_removesSpecifiedRow() = runTest {
        advanceUntilIdle()

        val firstRowId = viewModel.uiState.value.rows.first().id
        viewModel.deleteRow(firstRowId)

        val remainingIds = viewModel.uiState.value.rows.map { it.id }
        assertThat(remainingIds).doesNotContain(firstRowId)
    }

    @Test
    fun deleteRow_whenSingleRowLeft_clearsValuesInsteadOfRemoving() = runTest {
        advanceUntilIdle()

        // Delete until 1 row left
        while (viewModel.uiState.value.rows.size > 1) {
            viewModel.deleteRow(viewModel.uiState.value.rows.first().id)
        }

        val lastRowId = viewModel.uiState.value.rows.first().id
        viewModel.updateField(lastRowId, BatchField.PMA, "35")
        viewModel.updateField(lastRowId, BatchField.WEIGHT, "1900")

        viewModel.deleteRow(lastRowId)

        assertThat(viewModel.uiState.value.rows).hasSize(1)
        assertThat(viewModel.uiState.value.rows.first().pmaWeeks).isEmpty()
        assertThat(viewModel.uiState.value.rows.first().weightGrams).isEmpty()
    }

    @Test
    fun updateField_pmaValidation_flagsOutOfRange() = runTest {
        advanceUntilIdle()

        val rowId = viewModel.uiState.value.rows.first().id
        viewModel.updateField(rowId, BatchField.PMA, "55")

        val row = viewModel.uiState.value.rows.first { it.id == rowId }
        assertThat(row.pmaError).isNotNull()
    }

    @Test
    fun saveBatchMeasurements_whenValid_triggersSaveAndCallback() = runTest {
        advanceUntilIdle()

        coEvery {
            saveBatchGrowthRecordsUseCase(
                babyId = any(),
                gender = any(),
                birthDateEpochMillis = any(),
                gestationalAgeWeeksAtBirth = any(),
                rows = any(),
                measurementDateEpochMillis = any(),
                clinicalNotes = any(),
                enableReminder = any()
            )
        } returns Result.success(2)

        var successCalled = false
        viewModel.saveBatchMeasurements(onSuccess = { successCalled = true })
        advanceUntilIdle()

        assertThat(successCalled).isTrue()
        assertThat(viewModel.uiState.value.isSaved).isTrue()
        coVerify(exactly = 1) { saveBatchGrowthRecordsUseCase(any(), any(), any(), any(), any(), any(), any(), any()) }
    }
}
