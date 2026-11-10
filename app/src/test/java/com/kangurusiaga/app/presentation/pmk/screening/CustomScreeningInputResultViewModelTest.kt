package com.kangurusiaga.app.presentation.pmk.screening

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.core.common.ScreeningAttachmentManager
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.PrematureScreening
import com.kangurusiaga.app.domain.model.ScreeningType
import com.kangurusiaga.app.domain.repository.BabyRepository
import com.kangurusiaga.app.domain.repository.PrematureScreeningRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class CustomScreeningInputResultViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val babyRepository: BabyRepository = mockk(relaxed = true)
    private val screeningRepository: PrematureScreeningRepository = mockk(relaxed = true)
    private val attachmentManager: ScreeningAttachmentManager = mockk(relaxed = true)

    private val testBaby = Baby(
        id = 1L,
        name = "By. Arka Narendra",
        birthDateEpochMillis = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 12, 0, 0, 0)
        }.timeInMillis,
        birthWeightGram = 1850,
        gestationalAgeWeeks = 32
    )

    private val existingCustomScreening = PrematureScreening(
        id = 205L,
        babyId = 1L,
        type = ScreeningType.CUSTOM,
        title = "USG Ekokardiografi",
        categoryBadge = "Skrining Tambahan Kustom",
        targetTimingLabel = "34 Minggu PMA",
        targetDateEpoch = 1730000000000L,
        indication = "Deteksi PDA",
        clinicalGoal = "Evaluasi jantung",
        clinicOrLab = "Poli Kardiologi Anak",
        findings = "PDA menutup spontan",
        notes = "PDA menutup spontan"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { babyRepository.getActiveBaby() } returns flowOf(testBaby)
        every { screeningRepository.getScreeningsForBaby(1L) } returns flowOf(listOf(existingCustomScreening))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadData_newCustomScreening_populatesBabyInfoAndEmptyTitle() = runTest {
        val savedStateHandle = SavedStateHandle()
        val viewModel = CustomScreeningInputResultViewModel(
            savedStateHandle = savedStateHandle,
            babyRepository = babyRepository,
            screeningRepository = screeningRepository,
            attachmentManager = attachmentManager
        )

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.babyName).isEqualTo("By. Arka Narendra")
            assertThat(state.babyInitials).isEqualTo("AN")
            assertThat(state.customTitle).isEmpty()
            assertThat(state.currentScreening).isNull()
        }
    }

    @Test
    fun loadData_existingCustomScreening_populatesTitleAndNotes() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("screeningId" to 205L))
        val viewModel = CustomScreeningInputResultViewModel(
            savedStateHandle = savedStateHandle,
            babyRepository = babyRepository,
            screeningRepository = screeningRepository,
            attachmentManager = attachmentManager
        )

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.currentScreening?.id).isEqualTo(205L)
            assertThat(state.customTitle).isEqualTo("USG Ekokardiografi")
            assertThat(state.clinicalNotes).isEqualTo("PDA menutup spontan")
        }
    }

    @Test
    fun saveResult_newCustomScreening_insertsCustomScreening() = runTest {
        val savedStateHandle = SavedStateHandle()
        val viewModel = CustomScreeningInputResultViewModel(
            savedStateHandle = savedStateHandle,
            babyRepository = babyRepository,
            screeningRepository = screeningRepository,
            attachmentManager = attachmentManager
        )

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateCustomTitle("Ekokardiografi")
        viewModel.updateClinicalNotes("Katup jantung normal")

        val slotScreening = slot<PrematureScreening>()
        coEvery { screeningRepository.insertScreening(capture(slotScreening)) } returns 300L

        var savedCallbackCalled = false
        viewModel.saveResult {
            savedCallbackCalled = true
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(savedCallbackCalled).isTrue()
        coVerify(exactly = 1) { screeningRepository.insertScreening(any()) }
        assertThat(slotScreening.captured.type).isEqualTo(ScreeningType.CUSTOM)
        assertThat(slotScreening.captured.title).isEqualTo("Ekokardiografi")
        assertThat(slotScreening.captured.findings).isEqualTo("Katup jantung normal")
        assertThat(slotScreening.captured.isCompleted).isTrue()
    }

    @Test
    fun saveResult_existingCustomScreening_updatesScreening() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("screeningId" to 205L))
        val viewModel = CustomScreeningInputResultViewModel(
            savedStateHandle = savedStateHandle,
            babyRepository = babyRepository,
            screeningRepository = screeningRepository,
            attachmentManager = attachmentManager
        )

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateCustomTitle("Ekokardiografi Lanjutan")
        viewModel.updateClinicalNotes("Fungsi ventrikel baik")

        val slotScreening = slot<PrematureScreening>()
        coEvery { screeningRepository.updateScreening(capture(slotScreening)) } returns Unit

        var savedCallbackCalled = false
        viewModel.saveResult {
            savedCallbackCalled = true
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(savedCallbackCalled).isTrue()
        coVerify(exactly = 1) { screeningRepository.updateScreening(any()) }
        assertThat(slotScreening.captured.id).isEqualTo(205L)
        assertThat(slotScreening.captured.title).isEqualTo("Ekokardiografi Lanjutan")
        assertThat(slotScreening.captured.findings).isEqualTo("Fungsi ventrikel baik")
        assertThat(slotScreening.captured.isCompleted).isTrue()
    }

    @Test
    fun updateCustomTitleAndNotes_modifiesUiStateCorrectly() = runTest {
        val savedStateHandle = SavedStateHandle()
        val viewModel = CustomScreeningInputResultViewModel(
            savedStateHandle = savedStateHandle,
            babyRepository = babyRepository,
            screeningRepository = screeningRepository,
            attachmentManager = attachmentManager
        )

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateCustomTitle("USG Abdomen")
        viewModel.updateClinicalNotes("Tidak ada kelainan organ.")
        viewModel.insertQuickPoint("Anjuran kontrol 1 bulan")

        val state = viewModel.uiState.value
        assertThat(state.customTitle).isEqualTo("USG Abdomen")
        assertThat(state.clinicalNotes).contains("• Anjuran kontrol 1 bulan")
    }
}

