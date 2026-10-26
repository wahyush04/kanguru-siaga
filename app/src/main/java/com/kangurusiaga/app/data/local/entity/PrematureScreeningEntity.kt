package com.kangurusiaga.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kangurusiaga.app.domain.model.PrematureScreening
import com.kangurusiaga.app.domain.model.ScreeningType

@Entity(tableName = "premature_screenings")
data class PrematureScreeningEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "baby_id")
    val babyId: Long,
    @ColumnInfo(name = "screening_type")
    val screeningType: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "category_badge")
    val categoryBadge: String,
    @ColumnInfo(name = "target_timing_label")
    val targetTimingLabel: String,
    @ColumnInfo(name = "target_date_epoch")
    val targetDateEpoch: Long,
    @ColumnInfo(name = "target_date_epoch_2")
    val targetDateEpoch2: Long? = null,
    @ColumnInfo(name = "indication")
    val indication: String,
    @ColumnInfo(name = "clinical_goal")
    val clinicalGoal: String,
    @ColumnInfo(name = "clinic_or_lab")
    val clinicOrLab: String,
    @ColumnInfo(name = "is_alarm_active")
    val isAlarmActive: Boolean = true,
    @ColumnInfo(name = "is_alarm_2_active")
    val isAlarm2Active: Boolean = true,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
    @ColumnInfo(name = "realization_date_epoch")
    val realizationDateEpoch: Long? = null,
    @ColumnInfo(name = "hospital_name")
    val hospitalName: String? = null,
    @ColumnInfo(name = "doctor_name")
    val doctorName: String? = null,
    @ColumnInfo(name = "clinical_result")
    val clinicalResult: String? = null,
    @ColumnInfo(name = "findings")
    val findings: String? = null,
    @ColumnInfo(name = "conclusion")
    val conclusion: String? = null,
    @ColumnInfo(name = "attachment_name")
    val attachmentName: String? = null,
    @ColumnInfo(name = "attachment_count")
    val attachmentCount: Int = 0,
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(
        dateFormatted: String = "",
        date2Formatted: String = "",
        realizationFormatted: String? = null
    ): PrematureScreening {
        val parsedType = try {
            ScreeningType.valueOf(screeningType)
        } catch (_: Exception) {
            ScreeningType.CUSTOM
        }
        return PrematureScreening(
            id = id,
            babyId = babyId,
            type = parsedType,
            title = title,
            categoryBadge = categoryBadge,
            targetTimingLabel = targetTimingLabel,
            targetDateEpoch = targetDateEpoch,
            targetDateEpoch2 = targetDateEpoch2,
            targetDateFormatted = dateFormatted,
            targetDate2Formatted = date2Formatted,
            indication = indication,
            clinicalGoal = clinicalGoal,
            clinicOrLab = clinicOrLab,
            isAlarmActive = isAlarmActive,
            isAlarm2Active = isAlarm2Active,
            isCompleted = isCompleted,
            realizationDateEpoch = realizationDateEpoch,
            realizationDateFormatted = realizationFormatted,
            hospitalName = hospitalName,
            doctorName = doctorName,
            clinicalResult = clinicalResult,
            findings = findings,
            conclusion = conclusion,
            attachmentName = attachmentName,
            attachmentCount = attachmentCount,
            notes = notes,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(domain: PrematureScreening): PrematureScreeningEntity {
            return PrematureScreeningEntity(
                id = domain.id,
                babyId = domain.babyId,
                screeningType = domain.type.name,
                title = domain.title,
                categoryBadge = domain.categoryBadge,
                targetTimingLabel = domain.targetTimingLabel,
                targetDateEpoch = domain.targetDateEpoch,
                targetDateEpoch2 = domain.targetDateEpoch2,
                indication = domain.indication,
                clinicalGoal = domain.clinicalGoal,
                clinicOrLab = domain.clinicOrLab,
                isAlarmActive = domain.isAlarmActive,
                isAlarm2Active = domain.isAlarm2Active,
                isCompleted = domain.isCompleted,
                realizationDateEpoch = domain.realizationDateEpoch,
                hospitalName = domain.hospitalName,
                doctorName = domain.doctorName,
                clinicalResult = domain.clinicalResult,
                findings = domain.findings,
                conclusion = domain.conclusion,
                attachmentName = domain.attachmentName,
                attachmentCount = domain.attachmentCount,
                notes = domain.notes,
                updatedAt = domain.updatedAt
            )
        }
    }
}
