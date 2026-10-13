package com.kangurusiaga.app.data.remote.firestore

import com.google.firebase.firestore.FieldValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BabyOnboardingFirestoreDto(
    val babyName: String = "",
    val birthDateEpochMillis: Long = 0L,
    val birthDateFormatted: String = "",
    val gestationalAgeWeeks: Int = 0,
    val birthWeightGram: Int = 0,
    val currentWeightGram: Int = 0,
    val gender: String = "",
    val environment: String = "",
    val appVersion: String = "",
    val syncedAtEpochMillis: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "babyName" to babyName,
            "birthDateEpochMillis" to birthDateEpochMillis,
            "birthDateFormatted" to birthDateFormatted,
            "gestationalAgeWeeks" to gestationalAgeWeeks,
            "birthWeightGram" to birthWeightGram,
            "currentWeightGram" to currentWeightGram,
            "gender" to gender,
            "environment" to environment,
            "appVersion" to appVersion,
            "syncedAtEpochMillis" to syncedAtEpochMillis,
            "createdAtServer" to FieldValue.serverTimestamp()
        )
    }

    companion object {
        fun create(
            babyName: String,
            birthDateEpochMillis: Long,
            gestationalAgeWeeks: Int,
            birthWeightGram: Int,
            currentWeightGram: Int,
            gender: String,
            environment: String,
            appVersion: String
        ): BabyOnboardingFirestoreDto {
            val formattedDate = try {
                val sdf = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
                sdf.format(Date(birthDateEpochMillis))
            } catch (_: Exception) {
                ""
            }
            return BabyOnboardingFirestoreDto(
                babyName = babyName,
                birthDateEpochMillis = birthDateEpochMillis,
                birthDateFormatted = formattedDate,
                gestationalAgeWeeks = gestationalAgeWeeks,
                birthWeightGram = birthWeightGram,
                currentWeightGram = if (currentWeightGram > 0) currentWeightGram else birthWeightGram,
                gender = gender,
                environment = environment,
                appVersion = appVersion
            )
        }
    }
}
