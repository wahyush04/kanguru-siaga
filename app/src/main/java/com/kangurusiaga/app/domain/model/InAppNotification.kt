package com.kangurusiaga.app.domain.model

enum class NotificationCategory {
    PMK_PAUSE,
    PMK_REMINDER,
    FEEDING_ALARM,
    GROWTH_WEIGHT,
    GROWTH_LENGTH,
    GROWTH_HEAD,
    SCREENING_REMINDER
}

enum class NotificationBadgeColor {
    AMBER,
    PINK,
    BLUE,
    GREEN
}

data class InAppNotification(
    val id: Long = 0L,
    val babyId: Long,
    val category: NotificationCategory,
    val referenceId: Long? = null,
    val title: String,
    val message: String,
    val badgeLabel: String,
    val badgeColor: NotificationBadgeColor = NotificationBadgeColor.PINK,
    val timeLabel: String,
    val targetInfo: String? = null,
    val isRead: Boolean = false,
    val isToday: Boolean = true,
    val primaryActionLabel: String,
    val secondaryActionLabel: String? = null,
    val timestampEpoch: Long = System.currentTimeMillis()
)
