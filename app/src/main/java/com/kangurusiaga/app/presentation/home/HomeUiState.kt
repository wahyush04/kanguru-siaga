package com.kangurusiaga.app.presentation.home

import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.PmkSession

data class HomeUiState(
    val isLoading: Boolean = true,
    val activeBaby: Baby? = null,
    val babyAgeFormatted: String = "",
    val babyWeightFormatted: String = "",
    val todayPmkSessions: List<PmkSession> = emptyList(),
    val todaySessionsCount: Int = 0,
    val todayTargetSessions: Int = 3,
    val todayProgressFraction: Float = 0f,
    val unreadNotificationsCount: Int = 1,
    val showEmergencyDialog: Boolean = false,
    val showNotificationSheet: Boolean = false,
    val infoMessage: String? = null
)
