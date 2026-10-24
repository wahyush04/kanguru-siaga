package com.kangurusiaga.app.presentation.growth.batch

import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
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
            saveBatchGrowthRecordsUseCase = saveBatchGrowthRecordsUseCase
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
    fun addRow_incrementsRowListWithCalculatedNextPma() = runTest {
        advanceUntilIdle()

        val initialCount = viewModel.uiState.value.rows.size
        viewModel.addRow()

        val newCount = viewModel.uiState.value.rows.size
        assertThat(newCount).isEqualTo(initialCount + 1)
        assertThat(viewModel.uiState.value.rows.last().pmaWeeks).isEqualTo("37")
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
