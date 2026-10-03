package com.kangurusiaga.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fenton_lms_reference",
    indices = [
        Index(
            value = ["metric", "gender", "week"],
            unique = true
        )
    ]
)
data class FentonLmsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val metric: String, // "WEIGHT", "LENGTH", "HEAD_CIRCUMFERENCE"
    val gender: String, // "MALE", "FEMALE"
    val week: Int,      // 22 to 50
    val l: Double,      // Skewness / Box-Cox power
    val m: Double,      // Median / P50 value (Weight in grams, Length/HC in cm)
    val s: Double       // Coefficient of variation
)
