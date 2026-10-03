package com.kangurusiaga.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "baby_growth_records",
    foreignKeys = [
        ForeignKey(
            entity = BabyProfileEntity::class,
            parentColumns = ["babyId"],
            childColumns = ["babyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("babyId")
    ]
)
data class GrowthRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val recordId: Long = 0,
    val babyId: Long,
    val recordedAt: Long,
    val pmaWeeks: Int,
    val pmaDays: Int = 0,
    val weightGrams: Double? = null,
    val lengthCm: Double? = null,
    val headCircumferenceCm: Double? = null,
    val calculatedWeightZScore: Double? = null,
    val calculatedWeightPercentile: String? = null,
    val clinicalClassification: String? = null
)
