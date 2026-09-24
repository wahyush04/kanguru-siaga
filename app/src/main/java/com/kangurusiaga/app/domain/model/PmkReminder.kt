package com.kangurusiaga.app.domain.model

data class PmkReminder(
    val id: Long = 0,
    val babyId: Long,
    val timeHour: Int, // 0 - 23
    val timeMinute: Int, // 0 - 59
    val label: String, // "Pagi", "Siang", "Malam", etc.
    val targetMinutes: Int = 60,
    val daysOfWeekMask: Int = 127,
    val isEnabled: Boolean = true,
    val createdAtEpoch: Long = System.currentTimeMillis()
) {
    val formattedTime: String
        get() = String.format("%02d:%02d", timeHour, timeMinute)
}
