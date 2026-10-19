package com.kangurusiaga.app.presentation.babyprofile

import com.kangurusiaga.app.domain.model.Gender

enum class ProfileSetupStep {
    FORM,
    CONFIRMATION
}

data class BabyProfileSetupUiState(
    val currentStep: ProfileSetupStep = ProfileSetupStep.FORM,
    val name: String = "",
    val gender: Gender? = null,
    val birthDateEpochMillis: Long = System.currentTimeMillis(),
    val gestationalAgeWeeks: String = "",
    val birthWeightInput: String = "",
    val currentWeightInput: String = "",
    val birthLengthInput: String = "",
    val currentLengthInput: String = "",
    val birthHeadCircumferenceInput: String = "",
    val currentHeadCircumferenceInput: String = "",
    val photoUri: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isComplete: Boolean = false
) {
    val birthWeightGram: Int
        get() = birthWeightInput.replace(".", "").replace(",", "").toIntOrNull() ?: 0

    val currentWeightGram: Int
        get() = currentWeightInput.replace(".", "").replace(",", "").toIntOrNull() ?: 0

    val birthLengthCm: Float
        get() = birthLengthInput.replace(',', '.').toFloatOrNull() ?: 0f

    val currentLengthCm: Float
        get() = currentLengthInput.replace(',', '.').toFloatOrNull() ?: 0f

    val birthHeadCircumferenceCm: Float
        get() = birthHeadCircumferenceInput.replace(',', '.').toFloatOrNull() ?: 0f

    val currentHeadCircumferenceCm: Float
        get() = currentHeadCircumferenceInput.replace(',', '.').toFloatOrNull() ?: 0f

    val isBblr: Boolean
        get() = birthWeightGram in 1..2499
}
