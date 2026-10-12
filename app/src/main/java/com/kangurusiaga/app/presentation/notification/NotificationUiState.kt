package com.kangurusiaga.app.presentation.notification

import com.kangurusiaga.app.domain.model.InAppNotification

data class NotificationUiState(
    val babyId: Long? = null,
    val babyName: String = "",
    val todayNotifications: List<InAppNotification> = emptyList(),
    val earlierNotifications: List<InAppNotification> = emptyList(),
    val unreadCount: Int = 0,
    val isLoading: Boolean = true,
    val showEmergencySheet: Boolean = false
)
