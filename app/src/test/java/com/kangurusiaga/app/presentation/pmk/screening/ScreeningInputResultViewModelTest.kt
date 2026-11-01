package com.kangurusiaga.app.presentation.pmk.screening

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.core.common.AttachmentResult
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
class ScreeningInputResultViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val babyRepository: BabyRepository = mockk(relaxed = true)
    private val screeningRepository: PrematureScreeningRepository = mockk(relaxed = true)
    private val attachmentManager: ScreeningAttachmentManager = mockk(relaxed = true)

    private val testBaby = Baby(
        id = 1L,
        name = "By. Ny. Nirmala",
        birthDateEpochMillis = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 12, 0, 0, 0)
        }.timeInMillis,
        birthWeightGram = 1850,
        gestationalAgeWeeks = 31
    )

    private val testScreenings = listOf(
        PrematureScreening(
            id = 101L,
            babyId = 1L,
            type = ScreeningType.ROP_EYE,
            title = "Retinopati Prematuritas (ROP)",
            categoryBadge = "Oftalmologi Pediatrik",
            targetTimingLabel = "Usia Koreksi 0 Bulan",
            targetDateEpoch = 1730000000000L,
            indication = "Skrining Retina",
            clinicalGoal = "Cegah kebutaan",
            clinicOrLab = "Poli Mata Anak"
        ),
        PrematureScreening(
            id = 102L,
            babyId = 1L,
            type = ScreeningType.USG_HEAD,
            title = "USG Kepala",
            categoryBadge = "Neurosonografi",
            targetTimingLabel = "Usia 2 Minggu Kronologis",
            targetDateEpoch = 1729000000000L,
            indication = "Skrining IVH",
            clinicalGoal = "Deteksi IVH",
            clinicOrLab = "Poli Anak"
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { babyRepository.getActiveBaby() } returns flowOf(testBaby)
        every { screeningRepository.getScreeningsForBaby(1L) } returns flowOf(testScreenings)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadData_populatesInitialStateForRopEye() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("type" to "ROP_EYE"))
        val viewModel = ScreeningInputResultViewModel(
            savedStateHandle = savedStateHandle,
            babyRepository = babyRepository,
            screeningRepository = screeningRepository,
            attachmentManager = attachmentManager
        )

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.babyName).isEqualTo("By. Ny. Nirmala")
            assertThat(state.babyInitials).isEqualTo("NN")
            assertThat(state.selectedType).isEqualTo(ScreeningType.ROP_EYE)
            assertThat(state.currentScreening?.id).isEqualTo(101L)
        }
    }

    @Test
    fun selectScreeningType_updatesSelectedTypeAndNotes() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("type" to "ROP_EYE"))
        val viewModel = ScreeningInputResultViewModel(
            savedStateHandle = savedStateHandle,
            babyRepository = babyRepository,
            screeningRepository = screeningRepository,
            attachmentManager = attachmentManager
        )

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectScreeningType(ScreeningType.USG_HEAD)

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.selectedType).isEqualTo(ScreeningType.USG_HEAD)
            assertThat(state.currentScreening?.id).isEqualTo(102L)
        }
    }

    @Test
    fun updateNotesAndInsertQuickPoint_modifiesClinicalNotes() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("type" to "ROP_EYE"))
        val viewModel = ScreeningInputResultViewModel(
            savedStateHandle = savedStateHandle,
            babyRepository = babyRepository,
            screeningRepository = screeningRepository,
            attachmentManager = attachmentManager
        )

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateClinicalNotes("Vaskularisasi retina normal zona 3.")
        assertThat(viewModel.uiState.value.clinicalNotes).isEqualTo("Vaskularisasi retina normal zona 3.")
        assertThat(viewModel.uiState.value.charCount).isEqualTo(35)

        viewModel.insertQuickPoint("Rekomendasi follow-up 2 minggu")
        assertThat(viewModel.uiState.value.clinicalNotes)
            .contains("• Rekomendasi follow-up 2 minggu")
    }

    @Test
    fun onPhotoCaptured_savesPhotoAndUpdatesUiState() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("type" to "ROP_EYE"))
        val viewModel = ScreeningInputResultViewModel(
            savedStateHandle = savedStateHandle,
            babyRepository = babyRepository,
            screeningRepository = screeningRepository,
            attachmentManager = attachmentManager
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val mockUri = mockk<Uri>()
        val expectedResult = AttachmentResult(
            fileName = "Foto_rop_eye_1730000000.jpg",
            formattedSize = "1.8 MB",
            fileUri = "file:///data/user/0/screening_attachments/photo.jpg",
            isImage = true
        )
        coEvery { attachmentManager.saveCapturedPhoto(mockUri, 1L, ScreeningType.ROP_EYE) } returns expectedResult

        viewModel.onPhotoCaptured(mockUri)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.attachmentName).isEqualTo("Foto_rop_eye_1730000000.jpg")
        assertThat(state.attachmentSize).contains("1.8 MB")
        assertThat(state.attachmentUri).isEqualTo("file:///data/user/0/screening_attachments/photo.jpg")
        assertThat(state.isAttachmentImage).isTrue()
        assertThat(state.userMessage).isEqualTo("Foto dokumentasi klinis berhasil diambil.")
    }

    @Test
    fun onDocumentSelected_savesDocumentAndUpdatesUiState() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("type" to "ROP_EYE"))
        val viewModel = ScreeningInputResultViewModel(
            savedStateHandle = savedStateHandle,
            babyRepository = babyRepository,
            screeningRepository = screeningRepository,
            attachmentManager = attachmentManager
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val mockUri = mockk<Uri>()
        val expectedResult = AttachmentResult(
            fileName = "resume_rop_oktober.pdf",
            formattedSize = "2.3 MB",
            fileUri = "file:///data/user/0/screening_attachments/resume.pdf",
            isImage = false
        )
        coEvery { attachmentManager.saveDocument(mockUri, 1L, ScreeningType.ROP_EYE) } returns expectedResult

        viewModel.onDocumentSelected(mockUri)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.attachmentName).isEqualTo("resume_rop_oktober.pdf")
        assertThat(state.attachmentSize).contains("2.3 MB")
        assertThat(state.attachmentUri).isEqualTo("file:///data/user/0/screening_attachments/resume.pdf")
        assertThat(state.isAttachmentImage).isFalse()
        assertThat(state.userMessage).isEqualTo("Berkas dokumen berhasil dilampirkan.")
    }

    @Test
    fun removeAttachment_deletesFileAndResetsState() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("type" to "ROP_EYE"))
        val viewModel = ScreeningInputResultViewModel(
            savedStateHandle = savedStateHandle,
            babyRepository = babyRepository,
            screeningRepository = screeningRepository,
            attachmentManager = attachmentManager
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val mockUri = mockk<Uri>()
        coEvery { attachmentManager.saveCapturedPhoto(any(), any(), any()) } returns AttachmentResult(
            fileName = "foto.jpg",
            formattedSize = "1 MB",
            fileUri = "file:///temp/foto.jpg",
            isImage = true
        )
        coEvery { attachmentManager.deleteAttachment("file:///temp/foto.jpg") } returns true

        viewModel.onPhotoCaptured(mockUri)
        testDispatcher.scheduler.advanceUntilIdle()
        assertThat(viewModel.uiState.value.attachmentName).isNotNull()

        viewModel.removeAttachment()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { attachmentManager.deleteAttachment("file:///temp/foto.jpg") }
        val state = viewModel.uiState.value
        assertThat(state.attachmentName).isNull()
        assertThat(state.attachmentUri).isNull()
        assertThat(state.attachmentSize).isNull()
        assertThat(state.userMessage).isEqualTo("Lampiran dihapus.")
    }

    @Test
    fun saveResult_persistsScreeningWithAttachment() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("type" to "ROP_EYE"))
        val viewModel = ScreeningInputResultViewModel(
            savedStateHandle = savedStateHandle,
            babyRepository = babyRepository,
            screeningRepository = screeningRepository,
            attachmentManager = attachmentManager
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val mockUri = mockk<Uri>()
        coEvery { attachmentManager.saveDocument(any(), any(), any()) } returns AttachmentResult(
            fileName = "hasil_lab.pdf",
            formattedSize = "1.5 MB",
            fileUri = "file:///data/hasil_lab.pdf",
            isImage = false
        )
        viewModel.onDocumentSelected(mockUri)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateClinicalNotes("Kondisi vaskularisasi zona 3 matang sempurna.")

        val slot = slot<PrematureScreening>()
        coEvery { screeningRepository.updateScreening(capture(slot)) } returns Unit

        var successCalled = false
        viewModel.saveResult(onSuccess = { successCalled = true })
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(successCalled).isTrue()
        val savedScreening = slot.captured
        assertThat(savedScreening.id).isEqualTo(101L)
        assertThat(savedScreening.attachmentName).isEqualTo("hasil_lab.pdf")
        assertThat(savedScreening.attachmentUri).isEqualTo("file:///data/hasil_lab.pdf")
        assertThat(savedScreening.attachmentCount).isEqualTo(1)
        assertThat(savedScreening.findings).isEqualTo("Kondisi vaskularisasi zona 3 matang sempurna.")
        assertThat(savedScreening.isCompleted).isTrue()
    }
}

