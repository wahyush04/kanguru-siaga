package com.kangurusiaga.app.domain.model

enum class ScreeningType {
    USG_HEAD,
    ROP_EYE,
    HEARING_OAE,
    THYROID_FUNCTION,
    CUSTOM
}

data class PrematureScreening(
    val id: Long = 0,
    val babyId: Long = 0,
    val type: ScreeningType = ScreeningType.USG_HEAD,
    val title: String,
    val categoryBadge: String,
    val targetTimingLabel: String,
    val targetDateEpoch: Long,
    val targetDateEpoch2: Long? = null,
    val targetDateFormatted: String = "",
    val targetDate2Formatted: String = "",
    val indication: String,
    val clinicalGoal: String,
    val clinicOrLab: String,
    val isAlarmActive: Boolean = true,
    val isAlarm2Active: Boolean = true,
    val isCompleted: Boolean = false,
    val realizationDateEpoch: Long? = null,
    val realizationDateFormatted: String? = null,
    val hospitalName: String? = null,
    val doctorName: String? = null,
    val clinicalResult: String? = null,
    val findings: String? = null,
    val conclusion: String? = null,
    val attachmentName: String? = null,
    val attachmentCount: Int = 0,
    val notes: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)
