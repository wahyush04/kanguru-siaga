package com.kangurusiaga.app.data.remote.firestore

import com.google.firebase.firestore.FieldValue
import com.kangurusiaga.app.data.local.entity.PmkSessionEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PmkSessionFirestoreDto(
    val sessionIdLocal: Long,
    val startTimeEpochMillis: Long,
    val endTimeEpochMillis: Long,
    val durationMinutes: Int,
    val caregiverRole: String,
    val moodRating: Int,
    val notes: String,
    val syncedAtEpochMillis: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> {
        val formattedDate = try {
            val sdf = SimpleDateFormat("d MMM yyyy", Locale("id", "ID"))
            sdf.format(Date(startTimeEpochMillis))
        } catch (_: Exception) {
            ""
        }
        return mapOf(
            "sessionId" to sessionIdLocal,
            "sessionIdLocal" to sessionIdLocal,
            "dateEpochMillis" to startTimeEpochMillis,
            "dateFormatted" to formattedDate,
            "totalDurationMinutes" to durationMinutes,
            "startTimeEpochMillis" to startTimeEpochMillis,
            "endTimeEpochMillis" to endTimeEpochMillis,
            "durationMinutes" to durationMinutes,
            "caregiverRole" to caregiverRole,
            "moodRating" to moodRating,
            "notes" to notes,
            "syncedAtEpochMillis" to syncedAtEpochMillis,
            "updatedAtServer" to FieldValue.serverTimestamp()
        )
    }

    companion object {
        fun fromEntity(entity: PmkSessionEntity): PmkSessionFirestoreDto {
            val validStart = if (entity.startTimeEpoch > 0) entity.startTimeEpoch else System.currentTimeMillis()
            val validEnd = if (entity.endTimeEpoch >= validStart) entity.endTimeEpoch else validStart
            val validDuration = entity.durationMinutes.coerceIn(0, 1440)
            val caregiver = entity.currentCaregiver.trim().ifEmpty { "IBU" }.take(50)
            val notes = (entity.notes ?: "").take(500)
            
            return PmkSessionFirestoreDto(
                sessionIdLocal = entity.id,
                startTimeEpochMillis = validStart,
                endTimeEpochMillis = validEnd,
                durationMinutes = validDuration,
                caregiverRole = caregiver,
                moodRating = 0, // default 0 jika tidak ada rating eksplisit (0..5)
                notes = notes
            )
        }
    }
}
