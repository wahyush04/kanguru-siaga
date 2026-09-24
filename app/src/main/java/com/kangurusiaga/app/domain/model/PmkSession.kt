package com.kangurusiaga.app.domain.model

enum class PmkSource {
    TIMER,
    MANUAL
}

data class PmkSession(
    val id: Long = 0,
    val babyId: Long,
    val startTimeEpoch: Long,
    val endTimeEpoch: Long,
    val durationMinutes: Int,
    val targetDurationMinutes: Int = 60,
    val source: PmkSource = PmkSource.TIMER,
    val babyTemperature: Double? = null,
    val babyResponse: String? = null, // "Tidur Tenang", "Tenang", "Gelisah", "Menangis"
    val notes: String? = null,
    val createdAtEpoch: Long = System.currentTimeMillis()
)
