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
        assertThat(state.currentStep).isEqualTo(ProfileSetupStep.NAME)
        assertThat(state.name).isEmpty()
        assertThat(state.gender).isEqualTo(Gender.FEMALE)
        assertThat(state.birthWeightGram).isEqualTo(1850)
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
    fun updateBirthWeight_sanitizesInput() {
        viewModel.updateBirthWeight("2.100")
        assertThat(viewModel.uiState.value.birthWeightInput).isEqualTo("2100")
        assertThat(viewModel.uiState.value.birthWeightGram).isEqualTo(2100)
    }

    @Test
    fun onNextStep_fromNameStep_whenNameEmpty_setsErrorAndReturnsFalse() {
        viewModel.updateName("")
        val result = viewModel.onNextStep()

        assertThat(result).isFalse()
        assertThat(viewModel.uiState.value.currentStep).isEqualTo(ProfileSetupStep.NAME)
        assertThat(viewModel.uiState.value.errorMessage).isEqualTo("Nama bayi belum diisi")
    }

    @Test
    fun onNextStep_walksThroughAllStepsCorrectly() {
        // Step 1: NAME
        viewModel.updateName("Raka Pratama")
        var nextResult = viewModel.onNextStep()
        assertThat(nextResult).isTrue()
        assertThat(viewModel.uiState.value.currentStep).isEqualTo(ProfileSetupStep.GENDER)

        // Step 2: GENDER
        viewModel.updateGender(Gender.MALE)
        nextResult = viewModel.onNextStep()
        assertThat(nextResult).isTrue()
        assertThat(viewModel.uiState.value.currentStep).isEqualTo(ProfileSetupStep.BIRTH_DATE)

        // Step 3: BIRTH_DATE
        viewModel.updateBirthDate(System.currentTimeMillis() - 3600000L)
        nextResult = viewModel.onNextStep()
        assertThat(nextResult).isTrue()
        assertThat(viewModel.uiState.value.currentStep).isEqualTo(ProfileSetupStep.BIRTH_WEIGHT)

        // Step 4: BIRTH_WEIGHT
        viewModel.updateBirthWeight("1900")
        nextResult = viewModel.onNextStep()
        assertThat(nextResult).isTrue()
        assertThat(viewModel.uiState.value.currentStep).isEqualTo(ProfileSetupStep.PHOTO)

        // Step 5: PHOTO
        nextResult = viewModel.onNextStep()
        assertThat(nextResult).isTrue()
        assertThat(viewModel.uiState.value.currentStep).isEqualTo(ProfileSetupStep.CONFIRMATION)
    }

    @Test
    fun onPreviousStep_navigatesBackwards() {
        viewModel.jumpToStep(ProfileSetupStep.CONFIRMATION)

        assertThat(viewModel.onPreviousStep()).isTrue()
        assertThat(viewModel.uiState.value.currentStep).isEqualTo(ProfileSetupStep.PHOTO)

        assertThat(viewModel.onPreviousStep()).isTrue()
        assertThat(viewModel.uiState.value.currentStep).isEqualTo(ProfileSetupStep.BIRTH_WEIGHT)

        assertThat(viewModel.onPreviousStep()).isTrue()
        assertThat(viewModel.uiState.value.currentStep).isEqualTo(ProfileSetupStep.BIRTH_DATE)

        assertThat(viewModel.onPreviousStep()).isTrue()
        assertThat(viewModel.uiState.value.currentStep).isEqualTo(ProfileSetupStep.GENDER)

        assertThat(viewModel.onPreviousStep()).isTrue()
        assertThat(viewModel.uiState.value.currentStep).isEqualTo(ProfileSetupStep.NAME)

        // Cannot go back before NAME
        assertThat(viewModel.onPreviousStep()).isFalse()
        assertThat(viewModel.uiState.value.currentStep).isEqualTo(ProfileSetupStep.NAME)
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
        viewModel.jumpToStep(ProfileSetupStep.PHOTO)
        // set photo directly through photo selection
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
    fun saveProfile_onSuccess_setsIsCompleteTrue() = runTest {
        viewModel.updateName("Kirana")
        viewModel.updateGender(Gender.FEMALE)
        viewModel.updateBirthWeight("2150")

        coEvery { saveBabyProfileUseCase(any()) } returns Result.success(1L)

        viewModel.saveProfile()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.isLoading).isFalse()
        assertThat(viewModel.uiState.value.isComplete).isTrue()
        assertThat(viewModel.uiState.value.errorMessage).isNull()
    }

    @Test
    fun saveProfile_onFailure_setsErrorMessage() = runTest {
        viewModel.updateName("Kirana")
        viewModel.updateGender(Gender.FEMALE)
        viewModel.updateBirthWeight("2150")

        coEvery { saveBabyProfileUseCase(any()) } returns Result.failure(RuntimeException("Gagal menyimpan ke basis data"))

        viewModel.saveProfile()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.isLoading).isFalse()
        assertThat(viewModel.uiState.value.isComplete).isFalse()
        assertThat(viewModel.uiState.value.errorMessage).isEqualTo("Gagal menyimpan ke basis data")
    }
}
