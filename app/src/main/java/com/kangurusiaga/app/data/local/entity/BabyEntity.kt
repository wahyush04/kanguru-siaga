package com.kangurusiaga.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "babies")
data class BabyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val gender: String,
    val birthDateEpochMillis: Long,
    val birthWeightGram: Int,
    val birthLengthCm: Float,
    val birthHeadCircumferenceCm: Float,
    val photoUri: String?,
    val isActive: Boolean = true
)
