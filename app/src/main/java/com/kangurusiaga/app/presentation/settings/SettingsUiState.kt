package com.kangurusiaga.app.presentation.settings

import android.net.Uri
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.settings.AppSettings
import com.kangurusiaga.app.domain.model.settings.AppTextScale
import com.kangurusiaga.app.domain.model.settings.AppThemeMode

data class SettingsUiState(
    val isLoading: Boolean = true,
    val baby: Baby? = null,
    val appSettings: AppSettings = AppSettings(),
    val formattedBirthDate: String = "",
    val pmkScheduleBadgeText: String = "3 Jadwal",
    val feedingAlarmBadgeText: String = "Aktif",

    // BottomSheet Visibility
    val showEditProfileSheet: Boolean = false,
    val showThemeSheet: Boolean = false,
    val showTextSizeSheet: Boolean = false,

    // Dialog Visibility
    val showClearHistoryDialog: Boolean = false,
    val showResetAppDialog: Boolean = false,

    // Edit Profile Form State
    val editName: String = "",
    val editGender: Gender = Gender.MALE,
    val editBirthDateEpoch: Long = 0L,
    val editBirthWeightInput: String = "",
    val editPhotoUri: String? = null,
    val editGestationalWeeks: Int = 32,
    val editErrorMessage: String? = null,
    val isSavingProfile: Boolean = false,

    // Theme Selection Temporary State in Sheet
    val selectedThemeMode: AppThemeMode = AppThemeMode.WARM_LIGHT,

    // Text Size Selection Temporary State in Sheet
    val selectedTextScale: AppTextScale = AppTextScale.STANDARD,

    val snackbarMessage: String? = null
)
