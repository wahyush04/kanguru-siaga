package com.kangurusiaga.app.domain.model

data class PmkReminder(
    val id: Long = 0,
    val babyId: Long,
    val timeHour: Int, // 0 - 23
    val timeMinute: Int, // 0 - 59
    val label: String, // e.g. "Estafet Sesi Pagi"
    val targetMinutes: Int = 120, // default minimal 2 jam (120 mnt)
    val daysOfWeekMask: Int = 127,
    val isEnabled: Boolean = true,
    val category: String = "Pagi", // Pagi, Siang, Sore, Malam, Dini Hari / Tidur (Estafet Pendamping)
    val notes: String = "",
    val repeatDaily: Boolean = true,
    val createdAtEpoch: Long = System.currentTimeMillis()
) {
    val formattedTime: String
        get() = String.format("%02d:%02d", timeHour, timeMinute)

    val formattedTimeWithPeriod: String
        get() {
            val period = if (timeHour in 4..11) "Pagi" else if (timeHour in 12..14) "Siang" else if (timeHour in 15..17) "Sore" else "Malam"
            return String.format("%02d:%02d %s", timeHour, timeMinute, period)
        }

    val targetHours: Int
        get() = (targetMinutes / 60).coerceAtLeast(1)
}
