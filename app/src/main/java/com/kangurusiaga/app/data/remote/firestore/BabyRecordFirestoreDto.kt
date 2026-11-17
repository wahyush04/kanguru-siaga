package com.kangurusiaga.app.data.remote.firestore

import com.google.firebase.firestore.FieldValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BabyRecordFirestoreDto(
    val babyIdLocal: Long,
    val motherName: String,
    val motherPhoneNumber: String,
    val babyName: String,
    val birthDateEpochMillis: Long,
    val birthDateFormatted: String,
    val gestationalAgeWeeks: Int,
    val birthWeightGram: Int,
    val currentWeightGram: Int,
    val birthLengthCm: Float,
    val currentLengthCm: Float,
    val birthHeadCircumferenceCm: Float,
    val currentHeadCircumferenceCm: Float,
    val gender: String,
    val environment: String,
    val appVersion: String,
    val syncedAtEpochMillis: Long = System.currentTimeMillis()
) {
    fun toMap(isNewDocument: Boolean = false): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>(
            "babyId" to babyIdLocal,
            "babyIdLocal" to babyIdLocal,
            "motherName" to motherName,
            "motherPhoneNumber" to motherPhoneNumber,
            "babyName" to babyName,
            "birthDateEpochMillis" to birthDateEpochMillis,
            "birthDateFormatted" to birthDateFormatted,
            "gestationalAgeWeeks" to gestationalAgeWeeks,
            "birthWeightGram" to birthWeightGram,
            "currentWeightGram" to currentWeightGram,
            "birthLengthCm" to birthLengthCm,
            "currentLengthCm" to currentLengthCm,
            "birthHeadCircumferenceCm" to birthHeadCircumferenceCm,
            "currentHeadCircumferenceCm" to currentHeadCircumferenceCm,
            "gender" to gender,
            "environment" to environment,
            "appVersion" to appVersion,
            "syncedAtEpochMillis" to syncedAtEpochMillis,
            "updatedAtServer" to FieldValue.serverTimestamp()
        )
        if (isNewDocument) {
            map["createdAtServer"] = FieldValue.serverTimestamp()
        }
        return map
    }

    companion object {
        fun create(
            babyIdLocal: Long,
            motherName: String,
            motherPhoneNumber: String,
            babyName: String,
            birthDateEpochMillis: Long,
            gestationalAgeWeeks: Int,
            birthWeightGram: Int,
            currentWeightGram: Int,
            birthLengthCm: Float = 0f,
            currentLengthCm: Float = 0f,
            birthHeadCircumferenceCm: Float = 0f,
            currentHeadCircumferenceCm: Float = 0f,
            gender: String,
            environment: String,
            appVersion: String
        ): BabyRecordFirestoreDto {
            val formattedDate = try {
                val sdf = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
                sdf.format(Date(birthDateEpochMillis))
            } catch (_: Exception) {
                ""
            }
            val safePhone = motherPhoneNumber.trim().let { if (it.length >= 5) it else "080000000000" }
            return BabyRecordFirestoreDto(
                babyIdLocal = babyIdLocal,
                motherName = motherName.trim().ifEmpty { "Bunda" },
                motherPhoneNumber = safePhone,
                babyName = babyName.trim(),
                birthDateEpochMillis = birthDateEpochMillis,
                birthDateFormatted = formattedDate,
                gestationalAgeWeeks = gestationalAgeWeeks,
                birthWeightGram = birthWeightGram,
                currentWeightGram = if (currentWeightGram > 0) currentWeightGram else birthWeightGram,
                birthLengthCm = birthLengthCm,
                currentLengthCm = if (currentLengthCm > 0f) currentLengthCm else birthLengthCm,
                birthHeadCircumferenceCm = birthHeadCircumferenceCm,
                currentHeadCircumferenceCm = if (currentHeadCircumferenceCm > 0f) currentHeadCircumferenceCm else birthHeadCircumferenceCm,
                gender = gender,
                environment = environment,
                appVersion = appVersion
            )
        }
    }
}
