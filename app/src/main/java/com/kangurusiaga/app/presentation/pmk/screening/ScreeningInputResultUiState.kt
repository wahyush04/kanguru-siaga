package com.kangurusiaga.app.presentation.pmk.screening

import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.PrematureScreening
import com.kangurusiaga.app.domain.model.ScreeningType

data class ScreeningInputResultUiState(
    val baby: Baby? = null,
    val babyInitials: String = "AN",
    val babyName: String = "By. Arka Narendra",
    val genderDisplay: String = "Laki-laki",
    val isMale: Boolean = true,
    val pmaWeeksDisplay: String = "35 Minggu",
    val birthGestationalDisplay: String = "32 mg",
    val birthWeightDisplay: String = "1.850 g",
    val selectedType: ScreeningType = ScreeningType.ROP_EYE,
    val availableScreenings: List<PrematureScreening> = emptyList(),
    val currentScreening: PrematureScreening? = null,
    val clinicalNotes: String = "",
    val charCount: Int = 0,
    val isSaved: Boolean = true,
    val attachmentName: String? = "Scan_USG_Neurokranial_Arka.pdf",
    val attachmentSize: String? = "1.4 MB • Siap disimpan",
    val helpDialogVisible: Boolean = false,
    val userMessage: String? = null,
    val isSaving: Boolean = false
)
