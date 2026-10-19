package com.kangurusiaga.app.presentation.babyprofile

import android.net.Uri
import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.core.common.PhotoStorageManager
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.usecase.SaveBabyProfileUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BabyProfileSetupViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val saveBabyProfileUseCase: SaveBabyProfileUseCase = mockk()
    private val photoStorageManager: PhotoStorageManager = mockk(relaxed = true)

    private lateinit var viewModel: BabyProfileSetupViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = BabyProfileSetupViewModel(saveBabyProfileUseCase, photoStorageManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_hasDefaultValues() {
        val state = viewModel.uiState.value
        assertThat(state.currentStep).isEqualTo(ProfileSetupStep.FORM)
        assertThat(state.name).isEmpty()
        assertThat(state.gender).isNull()
        assertThat(state.birthWeightGram).isEqualTo(0)
        assertThat(state.gestationalAgeWeeks).isEmpty()
        assertThat(state.photoUri).isNull()
        assertThat(state.isComplete).isFalse()
        assertThat(state.errorMessage).isNull()
    }

    @Test
    fun updateName_updatesStateAndClearsError() {
        viewModel.updateName("Siti Rahma")
        assertThat(viewModel.uiState.value.name).isEqualTo("Siti Rahma")
        assertThat(viewModel.uiState.value.errorMessage).isNull()
    }

    @Test
    fun updateGender_updatesGender() {
        viewModel.updateGender(Gender.MALE)
        assertThat(viewModel.uiState.value.gender).isEqualTo(Gender.MALE)
    }

    @Test
    fun updateBirthDate_withValidDate_updatesState() {
        val validDate = System.currentTimeMillis() - 86400000L
        viewModel.updateBirthDate(validDate)
        assertThat(viewModel.uiState.value.birthDateEpochMillis).isEqualTo(validDate)
        assertThat(viewModel.uiState.value.errorMessage).isNull()
    }

    @Test
    fun updateBirthDate_withFutureDate_setsErrorMessage() {
        val futureDate = System.currentTimeMillis() + 86400000L
        viewModel.updateBirthDate(futureDate)
        assertThat(viewModel.uiState.value.errorMessage).isEqualTo("Periksa kembali tanggal lahir")
    }

    @Test
    fun updateGestationalAge_sanitizesInput() {
        viewModel.updateGestationalAge("34 minggu")
        assertThat(viewModel.uiState.value.gestationalAgeWeeks).isEqualTo("34")
    }

    @Test
    fun updateBirthWeight_sanitizesInput() {
        viewModel.updateBirthWeight("2.100")
        assertThat(viewModel.uiState.value.birthWeightInput).isEqualTo("2100")
        assertThat(viewModel.uiState.value.birthWeightGram).isEqualTo(2100)
    }

    @Test
    fun updateCurrentWeight_sanitizesInput() {
        viewModel.updateCurrentWeight("3.400")
        assertThat(viewModel.uiState.value.currentWeightInput).isEqualTo("3400")
        assertThat(viewModel.uiState.value.currentWeightGram).isEqualTo(3400)
    }

    @Test
    fun proceedToConfirmation_whenNameEmpty_setsErrorAndReturnsFalse() {
        viewModel.updateName("")
        val result = viewModel.proceedToConfirmation()

        assertThat(result).isFalse()
        assertThat(viewModel.uiState.value.currentStep).isEqualTo(ProfileSetupStep.FORM)
        assertThat(viewModel.uiState.value.errorMessage).isEqualTo("Nama bayi belum diisi")
    }

    @Test
    fun proceedToConfirmation_withValidData_movesToConfirmation() {
        viewModel.updateName("Raka Pratama")
        viewModel.updateGender(Gender.MALE)
        viewModel.updateBirthDate(System.currentTimeMillis() - 3600000L)
        viewModel.updateBirthWeight("1900")

        val result = viewModel.proceedToConfirmation()
        assertThat(result).isTrue()
        assertThat(viewModel.uiState.value.currentStep).isEqualTo(ProfileSetupStep.CONFIRMATION)
        assertThat(viewModel.uiState.value.errorMessage).isNull()
    }

    @Test
    fun onPreviousStep_fromConfirmation_navigatesBackToForm() {
        viewModel.jumpToStep(ProfileSetupStep.CONFIRMATION)

        assertThat(viewModel.onPreviousStep()).isTrue()
        assertThat(viewModel.uiState.value.currentStep).isEqualTo(ProfileSetupStep.FORM)

        // Cannot go back before FORM
        assertThat(viewModel.onPreviousStep()).isFalse()
        assertThat(viewModel.uiState.value.currentStep).isEqualTo(ProfileSetupStep.FORM)
    }

    @Test
    fun onPhotoSelected_savesImagePermanentlyAndUpdatesState() = runTest {
        val inputUri = mockk<Uri>()
        val savedUri = "file:///data/user/0/app/files/baby_photos/saved.jpg"
        coEvery { photoStorageManager.saveImagePermanently(inputUri) } returns savedUri

        viewModel.onPhotoSelected(inputUri)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.photoUri).isEqualTo(savedUri)
        coVerify(exactly = 1) { photoStorageManager.saveImagePermanently(inputUri) }
    }

    @Test
    fun onRemovePhoto_deletesPhotoAndClearsState() = runTest {
        val dummyUri = "file:///dummy/path.jpg"
        viewModel.jumpToStep(ProfileSetupStep.FORM)
        val inputUri = mockk<Uri>()
        coEvery { photoStorageManager.saveImagePermanently(inputUri) } returns dummyUri
        viewModel.onPhotoSelected(inputUri)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onRemovePhoto()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.photoUri).isNull()
        coVerify { photoStorageManager.deletePhoto(dummyUri) }
    }

    @Test
    fun updateBirthLength_sanitizesAndUpdatesState() {
        viewModel.updateBirthLength("48,5")
        assertThat(viewModel.uiState.value.birthLengthInput).isEqualTo("48.5")
        assertThat(viewModel.uiState.value.birthLengthCm).isEqualTo(48.5f)
    }

    @Test
    fun updateCurrentLength_sanitizesAndUpdatesState() {
        viewModel.updateCurrentLength("50.2")
        assertThat(viewModel.uiState.value.currentLengthInput).isEqualTo("50.2")
        assertThat(viewModel.uiState.value.currentLengthCm).isEqualTo(50.2f)
    }

    @Test
    fun updateBirthHeadCircumference_sanitizesAndUpdatesState() {
        viewModel.updateBirthHeadCircumference("33,0")
        assertThat(viewModel.uiState.value.birthHeadCircumferenceInput).isEqualTo("33.0")
        assertThat(viewModel.uiState.value.birthHeadCircumferenceCm).isEqualTo(33.0f)
    }

    @Test
    fun updateCurrentHeadCircumference_sanitizesAndUpdatesState() {
        viewModel.updateCurrentHeadCircumference("34.5")
        assertThat(viewModel.uiState.value.currentHeadCircumferenceInput).isEqualTo("34.5")
        assertThat(viewModel.uiState.value.currentHeadCircumferenceCm).isEqualTo(34.5f)
    }

    @Test
    fun saveProfile_onSuccess_setsIsCompleteTrue() = runTest {
        viewModel.updateName("Kirana")
        viewModel.updateGender(Gender.FEMALE)
        viewModel.updateBirthWeight("2150")
        viewModel.updateBirthLength("45")
        viewModel.updateBirthHeadCircumference("32")

        coEvery { saveBabyProfileUseCase(any(), any(), any(), any()) } returns Result.success(1L)

        viewModel.saveProfile()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.isLoading).isFalse()
        assertThat(viewModel.uiState.value.isComplete).isTrue()
        assertThat(viewModel.uiState.value.errorMessage).isNull()
        coVerify(exactly = 1) {
            saveBabyProfileUseCase(
                baby = match { it.name == "Kirana" && it.birthLengthCm == 45f && it.birthHeadCircumferenceCm == 32f },
                currentWeightGram = 2150,
                currentLengthCm = 45f,
                currentHeadCircumferenceCm = 32f
            )
        }
    }

    @Test
    fun saveProfile_onFailure_setsErrorMessage() = runTest {
        viewModel.updateName("Kirana")
        viewModel.updateGender(Gender.FEMALE)
        viewModel.updateBirthWeight("2150")

        coEvery { saveBabyProfileUseCase(any(), any(), any(), any()) } returns Result.failure(RuntimeException("Gagal menyimpan ke basis data"))

        viewModel.saveProfile()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.isLoading).isFalse()
        assertThat(viewModel.uiState.value.isComplete).isFalse()
        assertThat(viewModel.uiState.value.errorMessage).isEqualTo("Gagal menyimpan ke basis data")
    }
}
