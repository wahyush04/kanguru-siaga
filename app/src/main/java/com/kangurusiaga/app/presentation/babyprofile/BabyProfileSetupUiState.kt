package com.kangurusiaga.app.presentation.babyprofile

import com.kangurusiaga.app.domain.model.Gender

enum class ProfileSetupStep(val stepNumber: Int, val totalSteps: Int = 6) {
    NAME(1),
    GENDER(2),
    BIRTH_DATE(3),
    BIRTH_WEIGHT(4),
    PHOTO(5),
    CONFIRMATION(6)
}

data class BabyProfileSetupUiState(
    val currentStep: ProfileSetupStep = ProfileSetupStep.NAME,
    val name: String = "",
    val gender: Gender = Gender.FEMALE,
    val birthDateEpochMillis: Long = System.currentTimeMillis(),
    val birthWeightInput: String = "1850",
    val photoUri: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isComplete: Boolean = false
) {
    val birthWeightGram: Int
        get() = birthWeightInput.replace(".", "").replace(",", "").toIntOrNull() ?: 0

    val isBblr: Boolean
        get() = birthWeightGram in 1..2499
}
