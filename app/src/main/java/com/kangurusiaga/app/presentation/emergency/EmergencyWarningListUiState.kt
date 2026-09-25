package com.kangurusiaga.app.presentation.emergency

import com.kangurusiaga.app.domain.model.emergency.EmergencyWarningModule

data class EmergencyWarningListUiState(
    val isLoading: Boolean = true,
    val modules: List<EmergencyWarningModule> = emptyList(),
    val errorMessage: String? = null
)
