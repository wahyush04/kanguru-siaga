package com.kangurusiaga.app.domain.model

enum class PmkSource {
    TIMER,
    MANUAL
}

data class PmkSession(
    val id: Long = 0,
    val babyId: Long,
    val startTimeEpoch: Long,
    val endTimeEpoch: Long = System.currentTimeMillis(),
    val durationMinutes: Int,
    val targetDurationMinutes: Int = 1200, // Default 20 hours (1200 mins) continuous KMC target
    val source: PmkSource = PmkSource.TIMER,
    val currentCaregiver: PmkCaregiver = PmkCaregiver.IBU,
    val status: TimerStatus = TimerStatus.COMPLETED,
    val activeDurationMinutes: Int = durationMinutes,
    val pauseDurationMinutes: Int = 0,
    val babyTemperature: Double? = null,
    val babyResponse: String? = null, // "Tidur Tenang", "Tenang", "Gelisah", "Menangis"
    val notes: String? = null,
    val createdAtEpoch: Long = System.currentTimeMillis(),
    val segments: List<PmkSegment> = emptyList()
)
