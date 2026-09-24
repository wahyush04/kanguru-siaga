package com.kangurusiaga.app.presentation.home

import com.kangurusiaga.app.domain.model.Baby

data class HomeUiState(
    val isLoading: Boolean = false,
    val activeBaby: Baby? = null,
    val userMessage: String? = null
)
