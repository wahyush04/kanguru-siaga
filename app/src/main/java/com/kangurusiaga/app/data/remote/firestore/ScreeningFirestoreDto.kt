package com.kangurusiaga.app.data.remote.firestore

import com.google.firebase.firestore.FieldValue
import com.kangurusiaga.app.data.local.entity.PrematureScreeningEntity

data class ScreeningFirestoreDto(
    val screeningIdLocal: Long,
    val screeningType: String,
    val title: String,
    val categoryBadge: String,
    val targetTimingLabel: String,
    val targetDateEpoch: Long,
    val targetDateEpoch2: Long,
    val clinicOrLab: String,
    val isAlarmActive: Boolean,
    val isAlarm2Active: Boolean,
    val isCompleted: Boolean,
    val realizationDateEpoch: Long,
    val hospitalName: String,
    val doctorName: String,
    val clinicalResult: String,
    val findings: String,
    val conclusion: String,
    val syncedAtEpochMillis: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "screeningIdLocal" to screeningIdLocal,
            "screeningType" to screeningType,
            "title" to title,
            "categoryBadge" to categoryBadge,
            "targetTimingLabel" to targetTimingLabel,
            "targetDateEpoch" to targetDateEpoch,
            "targetDateEpoch2" to targetDateEpoch2,
            "clinicOrLab" to clinicOrLab,
            "isAlarmActive" to isAlarmActive,
            "isAlarm2Active" to isAlarm2Active,
            "isCompleted" to isCompleted,
            "realizationDateEpoch" to realizationDateEpoch,
            "hospitalName" to hospitalName,
            "doctorName" to doctorName,
            "clinicalResult" to clinicalResult,
            "findings" to findings,
            "conclusion" to conclusion,
            "syncedAtEpochMillis" to syncedAtEpochMillis,
            "updatedAtServer" to FieldValue.serverTimestamp()
        )
    }

    companion object {
        fun fromEntity(entity: PrematureScreeningEntity): ScreeningFirestoreDto {
            return ScreeningFirestoreDto(
                screeningIdLocal = entity.id,
                screeningType = entity.screeningType.take(50).ifEmpty { "DEFAULT" },
                title = entity.title.take(100).ifEmpty { "Skrining" },
                categoryBadge = entity.categoryBadge.take(50),
                targetTimingLabel = entity.targetTimingLabel.take(50),
                targetDateEpoch = if (entity.targetDateEpoch > 0) entity.targetDateEpoch else System.currentTimeMillis(),
                targetDateEpoch2 = entity.targetDateEpoch2?.coerceAtLeast(0L) ?: 0L,
                clinicOrLab = entity.clinicOrLab.take(100),
                isAlarmActive = entity.isAlarmActive,
                isAlarm2Active = entity.isAlarm2Active,
                isCompleted = entity.isCompleted,
                realizationDateEpoch = entity.realizationDateEpoch?.coerceAtLeast(0L) ?: 0L,
                hospitalName = (entity.hospitalName ?: "").take(100),
                doctorName = (entity.doctorName ?: "").take(100),
                clinicalResult = (entity.clinicalResult ?: "").take(200),
                findings = (entity.findings ?: "").take(500),
                conclusion = (entity.conclusion ?: "").take(500)
            )
        }
    }
}
