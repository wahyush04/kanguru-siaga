package com.kangurusiaga.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "growth_measurements",
    foreignKeys = [
        ForeignKey(
            entity = BabyEntity::class,
            parentColumns = ["id"],
            childColumns = ["babyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("babyId"),
        Index("parameter"),
        Index("measurementDateEpochMillis")
    ]
)
data class GrowthMeasurementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val babyId: Long,
    val parameter: String, // "WEIGHT", "LENGTH", "HEAD_CIRCUMFERENCE"
    val measurementDateEpochMillis: Long,
    val chronologicalAgeWeeks: Float,
    val pmaWeeks: Float,
    val value: Float,
    val unit: String,
    val note: String? = null,
    val percentileBadge: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
