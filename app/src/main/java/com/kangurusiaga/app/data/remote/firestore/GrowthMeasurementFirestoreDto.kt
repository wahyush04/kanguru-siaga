package com.kangurusiaga.app.data.remote.firestore

import com.google.firebase.firestore.FieldValue
import com.kangurusiaga.app.data.local.entity.GrowthMeasurementEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class GrowthMeasurementFirestoreDto(
    val measurementIdLocal: Long,
    val parameter: String,
    val value: Float,
    val unit: String,
    val measurementDateEpochMillis: Long,
    val measurementDateFormatted: String,
    val pmaWeeks: Float,
    val chronologicalAgeWeeks: Float,
    val percentileBadge: String,
    val note: String,
    val syncedAtEpochMillis: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "measurementIdLocal" to measurementIdLocal,
            "parameter" to parameter,
            "value" to value,
            "unit" to unit,
            "measurementDateEpochMillis" to measurementDateEpochMillis,
            "measurementDateFormatted" to measurementDateFormatted,
            "pmaWeeks" to pmaWeeks,
            "chronologicalAgeWeeks" to chronologicalAgeWeeks,
            "percentileBadge" to percentileBadge,
            "note" to note,
            "syncedAtEpochMillis" to syncedAtEpochMillis,
            "updatedAtServer" to FieldValue.serverTimestamp()
        )
    }

    companion object {
        fun fromEntity(entity: GrowthMeasurementEntity): GrowthMeasurementFirestoreDto {
            val formattedDate = try {
                val sdf = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
                sdf.format(Date(entity.measurementDateEpochMillis))
            } catch (_: Exception) {
                ""
            }
            val validParam = when (entity.parameter.uppercase()) {
                "WEIGHT" -> "WEIGHT"
                "LENGTH" -> "LENGTH"
                "HEAD_CIRCUMFERENCE" -> "HEAD_CIRCUMFERENCE"
                else -> "WEIGHT"
            }
            val validUnit = entity.unit.trim().ifEmpty {
                when (validParam) {
                    "WEIGHT" -> "gram"
                    else -> "cm"
                }
            }.take(10)

            return GrowthMeasurementFirestoreDto(
                measurementIdLocal = entity.id,
                parameter = validParam,
                value = entity.value.coerceIn(0.1f, 50000.0f),
                unit = validUnit,
                measurementDateEpochMillis = if (entity.measurementDateEpochMillis > 0) entity.measurementDateEpochMillis else System.currentTimeMillis(),
                measurementDateFormatted = formattedDate.take(50),
                pmaWeeks = entity.pmaWeeks.coerceIn(20.0f, 60.0f),
                chronologicalAgeWeeks = entity.chronologicalAgeWeeks.coerceIn(0.0f, 200.0f),
                percentileBadge = (entity.percentileBadge ?: "").take(50),
                note = (entity.note ?: "").take(200)
            )
        }
    }
}
