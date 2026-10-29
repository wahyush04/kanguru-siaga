package com.kangurusiaga.app.presentation.pmk.screening

import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.PrematureScreening
import com.kangurusiaga.app.domain.model.ScreeningType

data class ScreeningInputResultUiState(
    val baby: Baby? = null,
    val babyInitials: String = "-",
    val babyName: String = "Si Kecil",
    val genderDisplay: String = "-",
    val isMale: Boolean = true,
    val pmaWeeksDisplay: String = "-",
    val birthGestationalDisplay: String = "-",
    val birthWeightDisplay: String = "-",
    val selectedType: ScreeningType = ScreeningType.ROP_EYE,
    val availableScreenings: List<PrematureScreening> = emptyList(),
    val currentScreening: PrematureScreening? = null,
    val clinicalNotes: String = "",
    val charCount: Int = 0,
    val isSaved: Boolean = false,
    val attachmentName: String? = null,
    val attachmentSize: String? = null,
    val helpDialogVisible: Boolean = false,
    val userMessage: String? = null,
    val isSaving: Boolean = false
)
