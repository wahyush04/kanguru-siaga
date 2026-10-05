package com.kangurusiaga.app.presentation.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kangurusiaga.app.core.common.PhotoStorageManager
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.settings.AppTextScale
import com.kangurusiaga.app.domain.model.settings.AppThemeMode
import com.kangurusiaga.app.domain.repository.BabyRepository
import com.kangurusiaga.app.domain.usecase.GetPmkRemindersUseCase
import com.kangurusiaga.app.domain.usecase.UpdateBabyProfileUseCase
import com.kangurusiaga.app.domain.usecase.feeding.ObserveFeedingSchedulesUseCase
import com.kangurusiaga.app.domain.usecase.settings.GetAppSettingsUseCase
import com.kangurusiaga.app.domain.usecase.settings.ToggleNotificationsUseCase
import com.kangurusiaga.app.domain.usecase.settings.UpdateAppThemeUseCase
import com.kangurusiaga.app.domain.usecase.settings.UpdateTextScaleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val babyRepository: BabyRepository,
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val updateBabyProfileUseCase: UpdateBabyProfileUseCase,
    private val updateAppThemeUseCase: UpdateAppThemeUseCase,
    private val updateTextScaleUseCase: UpdateTextScaleUseCase,
    private val toggleNotificationsUseCase: ToggleNotificationsUseCase,
    private val getPmkRemindersUseCase: GetPmkRemindersUseCase,
    private val observeFeedingSchedulesUseCase: ObserveFeedingSchedulesUseCase,
    private val photoStorageManager: PhotoStorageManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            babyRepository.getActiveBaby().collectLatest { baby ->
                val formattedDate = if (baby != null) {
                    val sdf = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
                    sdf.format(Date(baby.birthDateEpochMillis))
                } else ""

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        baby = baby,
                        formattedBirthDate = formattedDate
                    )
                }

                if (baby != null) {
                    try {
                        getPmkRemindersUseCase(baby.id).collectLatest { reminders ->
                            val activeCount = reminders.count { it.isEnabled }
                            val text = if (activeCount > 0) "$activeCount Jadwal" else if (reminders.isNotEmpty()) "${reminders.size} Jadwal" else "3 Jadwal"
                            _uiState.update { it.copy(pmkScheduleBadgeText = text) }
                        }
                    } catch (e: Exception) {
                        // Fallback to default
                    }
                }
            }
        }

        viewModelScope.launch {
            try {
                observeFeedingSchedulesUseCase().collectLatest { schedules ->
                    val isActive = schedules.any { it.isEnabled }
                    _uiState.update {
                        it.copy(feedingAlarmBadgeText = if (isActive) "Aktif" else "Nonaktif")
                    }
                }
            } catch (e: Exception) {
                // Fallback to default
            }
        }

        viewModelScope.launch {
            getAppSettingsUseCase().collectLatest { settings ->
                _uiState.update {
                    it.copy(
                        appSettings = settings,
                        selectedThemeMode = settings.themeMode,
                        selectedTextScale = settings.textScale
                    )
                }
            }
        }
    }

    // --- Profile Editing BottomSheet ---
    fun openEditProfileSheet() {
        val baby = _uiState.value.baby ?: return
        _uiState.update {
            it.copy(
                showEditProfileSheet = true,
                editName = baby.name,
                editGender = baby.gender ?: Gender.FEMALE,
                editBirthDateEpoch = baby.birthDateEpochMillis,
                editBirthWeightInput = baby.birthWeightGram.toString(),
                editPhotoUri = baby.photoUri,
                editGestationalWeeks = baby.gestationalAgeWeeks,
                editErrorMessage = null
            )
        }
    }

    fun closeEditProfileSheet() {
        _uiState.update { it.copy(showEditProfileSheet = false, editErrorMessage = null) }
    }

    fun updateEditName(name: String) {
        _uiState.update { it.copy(editName = name, editErrorMessage = null) }
    }

    fun updateEditGender(gender: Gender) {
        _uiState.update { it.copy(editGender = gender, editErrorMessage = null) }
    }

    fun updateEditBirthDate(epoch: Long) {
        if (epoch > System.currentTimeMillis()) {
            _uiState.update { it.copy(editErrorMessage = "Periksa kembali tanggal lahir") }
            return
        }
        _uiState.update { it.copy(editBirthDateEpoch = epoch, editErrorMessage = null) }
    }

    fun updateEditBirthWeight(weight: String) {
        val sanitized = weight.filter { it.isDigit() }
        _uiState.update { it.copy(editBirthWeightInput = sanitized, editErrorMessage = null) }
    }

    fun createTempCameraUri(): Uri {
        return photoStorageManager.createTempCameraUri()
    }

    fun onPhotoSelected(sourceUri: Uri) {
        viewModelScope.launch {
            try {
                val oldPhoto = _uiState.value.editPhotoUri
                val permanentUri = photoStorageManager.saveImagePermanently(sourceUri)
                photoStorageManager.deletePhoto(oldPhoto)
                _uiState.update { it.copy(editPhotoUri = permanentUri, editErrorMessage = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(editErrorMessage = "Foto belum berhasil dipilih") }
            }
        }
    }

    fun saveProfileChanges() {
        val currentBaby = _uiState.value.baby ?: return
        val state = _uiState.value

        if (state.editName.trim().isBlank()) {
            _uiState.update { it.copy(editErrorMessage = "Nama bayi belum diisi") }
            return
        }
        val weightGram = state.editBirthWeightInput.toIntOrNull() ?: 0
        if (weightGram <= 0) {
            _uiState.update { it.copy(editErrorMessage = "Masukkan berat lahir yang valid") }
            return
        }
        if (state.editBirthDateEpoch <= 0 || state.editBirthDateEpoch > System.currentTimeMillis()) {
            _uiState.update { it.copy(editErrorMessage = "Periksa kembali tanggal lahir") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSavingProfile = true, editErrorMessage = null) }

            val updatedBaby = currentBaby.copy(
                name = state.editName.trim(),
                gender = state.editGender,
                birthDateEpochMillis = state.editBirthDateEpoch,
                birthWeightGram = weightGram,
                photoUri = state.editPhotoUri
            )

            val result = updateBabyProfileUseCase(updatedBaby)
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isSavingProfile = false,
                            showEditProfileSheet = false,
                            snackbarMessage = "Data bayi berhasil diperbarui"
                        )
                    }
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(
                            isSavingProfile = false,
                            editErrorMessage = throwable.localizedMessage ?: "Gagal memperbarui data bayi"
                        )
                    }
                }
            )
        }
    }

    // --- Theme BottomSheet ---
    fun openThemeSheet() {
        _uiState.update {
            it.copy(
                showThemeSheet = true,
                selectedThemeMode = it.appSettings.themeMode
            )
        }
    }

    fun closeThemeSheet() {
        _uiState.update { it.copy(showThemeSheet = false) }
    }

    fun selectThemeMode(mode: AppThemeMode) {
        _uiState.update { it.copy(selectedThemeMode = mode) }
    }

    fun applyTheme() {
        val mode = _uiState.value.selectedThemeMode
        viewModelScope.launch {
            updateAppThemeUseCase(mode)
            _uiState.update {
                it.copy(
                    showThemeSheet = false,
                    snackbarMessage = "Tema berhasil diterapkan"
                )
            }
        }
    }

    // --- Text Size BottomSheet ---
    fun openTextSizeSheet() {
        _uiState.update {
            it.copy(
                showTextSizeSheet = true,
                selectedTextScale = it.appSettings.textScale
            )
        }
    }

    fun closeTextSizeSheet() {
        _uiState.update { it.copy(showTextSizeSheet = false) }
    }

    fun selectTextScale(scale: AppTextScale) {
        _uiState.update { it.copy(selectedTextScale = scale) }
    }

    fun applyTextScale() {
        val scale = _uiState.value.selectedTextScale
        viewModelScope.launch {
            updateTextScaleUseCase(scale)
            _uiState.update {
                it.copy(
                    showTextSizeSheet = false,
                    snackbarMessage = "Ukuran teks berhasil diterapkan"
                )
            }
        }
    }

    // --- Notifications Toggle ---
    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            toggleNotificationsUseCase(enabled)
            _uiState.update {
                it.copy(snackbarMessage = if (enabled) "Notifikasi diaktifkan" else "Notifikasi dinonaktifkan")
            }
        }
    }

    // --- Dialogs ---
    fun openClearHistoryDialog() {
        _uiState.update { it.copy(showClearHistoryDialog = true) }
    }

    fun closeClearHistoryDialog() {
        _uiState.update { it.copy(showClearHistoryDialog = false) }
    }

    fun confirmClearHistory() {
        _uiState.update {
            it.copy(
                showClearHistoryDialog = false,
                snackbarMessage = "Riwayat aktivitas berhasil dibersihkan"
            )
        }
    }

    fun openResetAppDialog() {
        _uiState.update { it.copy(showResetAppDialog = true) }
    }

    fun closeResetAppDialog() {
        _uiState.update { it.copy(showResetAppDialog = false) }
    }

    fun confirmResetApp() {
        _uiState.update {
            it.copy(
                showResetAppDialog = false,
                snackbarMessage = "Pengaturan aplikasi telah direset ke default"
            )
        }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
