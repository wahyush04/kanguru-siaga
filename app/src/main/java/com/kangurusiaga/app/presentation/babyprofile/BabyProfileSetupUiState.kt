package com.kangurusiaga.app.presentation.babyprofile

import com.kangurusiaga.app.domain.model.Gender

enum class ProfileSetupStep {
    FORM,
    CONFIRMATION
}

data class BabyProfileSetupUiState(
    val currentStep: ProfileSetupStep = ProfileSetupStep.FORM,
    val name: String = "",
    val gender: Gender = Gender.FEMALE,
    val birthDateEpochMillis: Long = System.currentTimeMillis(),
    val gestationalAgeWeeks: String = "32",
    val birthWeightInput: String = "1850",
    val currentWeightInput: String = "3200",
    val photoUri: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isComplete: Boolean = false
) {
    val birthWeightGram: Int
        get() = birthWeightInput.replace(".", "").replace(",", "").toIntOrNull() ?: 0

    val currentWeightGram: Int
        get() = currentWeightInput.replace(".", "").replace(",", "").toIntOrNull() ?: 0

    val isBblr: Boolean
        get() = birthWeightGram in 1..2499
}
