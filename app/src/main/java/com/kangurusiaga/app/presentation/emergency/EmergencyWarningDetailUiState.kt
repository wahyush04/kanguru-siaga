package com.kangurusiaga.app.presentation.emergency

import com.kangurusiaga.app.domain.model.emergency.EmergencyWarningModule

data class EmergencyWarningDetailUiState(
    val isLoading: Boolean = true,
    val module: EmergencyWarningModule? = null,
    val errorMessage: String? = null
)
