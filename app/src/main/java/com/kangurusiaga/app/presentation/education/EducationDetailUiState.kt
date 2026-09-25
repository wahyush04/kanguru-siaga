package com.kangurusiaga.app.presentation.education

import com.kangurusiaga.app.domain.model.education.EducationModule

data class EducationDetailUiState(
    val module: EducationModule? = null,
    val nextModuleId: String? = null,
    val nextModuleOrder: Int? = null,
    val nextModuleTitle: String? = null,
    val isLoading: Boolean = false,
    val userMessage: String? = null
)
