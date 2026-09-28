package com.kangurusiaga.app.domain.model

import java.util.Locale

data class FeedingSchedule(
    val id: Long = 0L,
    val hour: Int,
    val minute: Int,
    val volumeMl: Int,
    val method: FeedingMethod = FeedingMethod.OGT_NGT,
    val note: String? = null,
    val isEnabled: Boolean = true,
    val reminderEnabled: Boolean = true,
    val reminderOffsetMinutes: Int = 10,
    val repeatType: RepeatType = RepeatType.DAILY,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val formattedTime: String
        get() = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)

    val timeMinutesFromMidnight: Int
        get() = hour * 60 + minute

    /**
     * Relative sort key starting from 06:00 AM (standard 24-hour caregiver cycle in neonatal care).
     * 06:00 -> 0, 08:00 -> 120, ..., 04:00 -> 1320.
     */
    val cycleSortKey: Int
        get() = (timeMinutesFromMidnight - 360 + 1440) % 1440
}
