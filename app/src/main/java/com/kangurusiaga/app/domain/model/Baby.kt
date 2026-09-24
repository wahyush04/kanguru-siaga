package com.kangurusiaga.app.domain.model

data class Baby(
    val id: Long = 0,
    val name: String,
    val gender: Gender,
    val birthDateEpochMillis: Long,
    val birthWeightGram: Int,
    val birthLengthCm: Float,
    val birthHeadCircumferenceCm: Float,
    val photoUri: String? = null
)
