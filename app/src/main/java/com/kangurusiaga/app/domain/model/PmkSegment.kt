package com.kangurusiaga.app.domain.model

enum class PmkSegmentStatus {
    ACTIVE,
    PAUSED
}

data class PmkSegment(
    val id: Long = 0,
    val sessionId: Long = 0,
    val caregiver: PmkCaregiver = PmkCaregiver.IBU,
    val startTimeEpoch: Long,
    val endTimeEpoch: Long? = null,
    val durationMinutes: Int = 0,
    val status: PmkSegmentStatus = PmkSegmentStatus.ACTIVE,
    val pauseReason: String? = null,
    val babyTemperature: Double? = null,
    val babyResponse: String? = null,
    val notes: String? = null
) {
    val isActive: Boolean get() = status == PmkSegmentStatus.ACTIVE
    val isPaused: Boolean get() = status == PmkSegmentStatus.PAUSED

    /**
     * Calculates duration in seconds. If the segment is currently ongoing (endTimeEpoch == null),
     * computes relative to [nowEpoch].
     */
    fun calculateDurationSeconds(nowEpoch: Long = System.currentTimeMillis()): Long {
        val end = endTimeEpoch ?: nowEpoch
        return ((end - startTimeEpoch) / 1000L).coerceAtLeast(0L)
    }
}
