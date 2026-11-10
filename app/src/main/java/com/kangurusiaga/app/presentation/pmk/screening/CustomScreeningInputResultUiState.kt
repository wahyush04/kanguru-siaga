package com.kangurusiaga.app.presentation.pmk.screening

import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.PrematureScreening

data class CustomScreeningInputResultUiState(
    val baby: Baby? = null,
    val babyInitials: String = "-",
    val babyName: String = "Si Kecil",
    val genderDisplay: String = "-",
    val isMale: Boolean = true,
    val pmaWeeksDisplay: String = "-",
    val birthGestationalDisplay: String = "-",
    val birthWeightDisplay: String = "-",
    val screeningId: Long = -1L,
    val currentScreening: PrematureScreening? = null,
    val customTitle: String = "",
    val clinicalNotes: String = "",
    val charCount: Int = 0,
    val isSaved: Boolean = false,
    val attachmentName: String? = null,
    val attachmentSize: String? = null,
    val attachmentUri: String? = null,
    val isAttachmentImage: Boolean = false,
    val helpDialogVisible: Boolean = false,
    val userMessage: String? = null,
    val isSaving: Boolean = false
)

