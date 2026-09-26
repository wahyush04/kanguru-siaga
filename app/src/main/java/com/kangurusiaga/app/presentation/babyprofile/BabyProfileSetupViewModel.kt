package com.kangurusiaga.app.presentation.babyprofile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.core.common.PhotoStorageManager
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.usecase.SaveBabyProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BabyProfileSetupViewModel @Inject constructor(
    private val saveBabyProfileUseCase: SaveBabyProfileUseCase,
    private val photoStorageManager: PhotoStorageManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(BabyProfileSetupUiState())
    val uiState: StateFlow<BabyProfileSetupUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name, errorMessage = null) }
    }

    fun updateGender(gender: Gender) {
        _uiState.update { it.copy(gender = gender, errorMessage = null) }
    }

    fun updateBirthDate(epochMillis: Long) {
        if (epochMillis > System.currentTimeMillis()) {
            _uiState.update { it.copy(errorMessage = "Periksa kembali tanggal lahir") }
            return
        }
        _uiState.update { it.copy(birthDateEpochMillis = epochMillis, errorMessage = null) }
    }

    fun updateGestationalAge(weeks: String) {
        val sanitized = weeks.filter { it.isDigit() }
        _uiState.update { it.copy(gestationalAgeWeeks = sanitized, errorMessage = null) }
    }

    fun updateBirthWeight(weight: String) {
        val sanitized = weight.filter { it.isDigit() }
        _uiState.update { it.copy(birthWeightInput = sanitized, errorMessage = null) }
    }

    fun updateCurrentWeight(weight: String) {
        val sanitized = weight.filter { it.isDigit() }
        _uiState.update { it.copy(currentWeightInput = sanitized, errorMessage = null) }
    }

    fun createTempCameraUri(): Uri {
        return photoStorageManager.createTempCameraUri()
    }

    fun onPhotoSelected(sourceUri: Uri) {
        viewModelScope.launch {
            try {
                val oldPhoto = _uiState.value.photoUri
                val permanentUri = photoStorageManager.saveImagePermanently(sourceUri)
                photoStorageManager.deletePhoto(oldPhoto)
                _uiState.update { it.copy(photoUri = permanentUri, errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Foto belum berhasil dipilih") }
            }
        }
    }

    fun onRemovePhoto() {
        viewModelScope.launch {
            val oldPhoto = _uiState.value.photoUri
            photoStorageManager.deletePhoto(oldPhoto)
            _uiState.update { it.copy(photoUri = null, errorMessage = null) }
        }
    }

    fun proceedToConfirmation(): Boolean {
        val state = _uiState.value
        if (state.name.trim().isBlank()) {
            _uiState.update { it.copy(errorMessage = "Nama bayi belum diisi") }
            return false
        }
        if (state.birthWeightGram <= 0) {
            _uiState.update { it.copy(errorMessage = "Masukkan berat lahir yang valid") }
            return false
        }
        if (state.birthDateEpochMillis <= 0 || state.birthDateEpochMillis > System.currentTimeMillis()) {
            _uiState.update { it.copy(errorMessage = "Periksa kembali tanggal lahir") }
            return false
        }

        _uiState.update { it.copy(currentStep = ProfileSetupStep.CONFIRMATION, errorMessage = null) }
        return true
    }

    fun backToForm() {
        _uiState.update { it.copy(currentStep = ProfileSetupStep.FORM, errorMessage = null) }
    }

    fun onNextStep(): Boolean {
        return if (_uiState.value.currentStep == ProfileSetupStep.FORM) {
            proceedToConfirmation()
        } else {
            saveProfile()
            true
        }
    }

    fun onPreviousStep(): Boolean {
        return if (_uiState.value.currentStep == ProfileSetupStep.CONFIRMATION) {
            backToForm()
            true
        } else {
            false
        }
    }

    fun jumpToStep(step: ProfileSetupStep) {
        _uiState.update { it.copy(currentStep = step, errorMessage = null) }
    }

    fun saveProfile() {
        val state = _uiState.value
        if (state.name.trim().isBlank()) {
            _uiState.update { it.copy(errorMessage = "Nama bayi belum diisi") }
            return
        }
        if (state.birthWeightGram <= 0) {
            _uiState.update { it.copy(errorMessage = "Masukkan berat lahir yang valid") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val baby = Baby(
                name = state.name.trim(),
                gender = state.gender,
                birthDateEpochMillis = state.birthDateEpochMillis,
                birthWeightGram = state.birthWeightGram,
                photoUri = state.photoUri
            )

            val result = saveBabyProfileUseCase(baby)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isComplete = true) }
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.localizedMessage ?: "Gagal menyimpan data bayi"
                        )
                    }
                }
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
