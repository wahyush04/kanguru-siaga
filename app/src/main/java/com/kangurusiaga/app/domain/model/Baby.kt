package com.kangurusiaga.app.domain.model

data class Baby(
    val id: Long = 0,
    val name: String,
    val gender: Gender,
    val birthDateEpochMillis: Long,
    val birthWeightGram: Int,
    val birthLengthCm: Float = 0f,
    val birthHeadCircumferenceCm: Float = 0f,
    val photoUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
