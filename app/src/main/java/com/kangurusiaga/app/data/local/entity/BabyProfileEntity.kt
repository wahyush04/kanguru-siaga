package com.kangurusiaga.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "baby_profiles")
data class BabyProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val babyId: Long = 0,
    val name: String,
    val gender: String, // "MALE" / "FEMALE"
    val birthDate: Long, // Epoch timestamp (millis)
    val gestationalAgeWeeksAtBirth: Int, // e.g. 28
    val gestationalAgeDaysAtBirth: Int = 0, // 0..6
    val birthWeightGrams: Double,
    val birthLengthCm: Double,
    val birthHeadCircumferenceCm: Double
)
