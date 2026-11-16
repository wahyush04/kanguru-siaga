package com.kangurusiaga.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "babies")
data class BabyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "mother_name", defaultValue = "")
    val motherName: String = "",
    @ColumnInfo(name = "mother_phone_number", defaultValue = "")
    val motherPhoneNumber: String = "",
    val gender: String? = null,
    val birthDateEpochMillis: Long,
    val birthWeightGram: Int,
    val birthLengthCm: Float = 0f,
    val birthHeadCircumferenceCm: Float = 0f,
    val gestationalAgeWeeks: Int = 32,
    val photoUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)
