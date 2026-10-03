package com.kangurusiaga.app.domain.model

data class GrowthMeasurement(
    val id: Long = 0,
    val babyId: Long,
    val parameter: GrowthParameter,
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
