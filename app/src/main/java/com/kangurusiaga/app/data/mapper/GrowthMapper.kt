package com.kangurusiaga.app.data.mapper

import com.kangurusiaga.app.data.local.entity.GrowthMeasurementEntity
import com.kangurusiaga.app.domain.model.GrowthMeasurement
import com.kangurusiaga.app.domain.model.GrowthParameter

fun GrowthMeasurementEntity.toDomain(): GrowthMeasurement {
    return GrowthMeasurement(
        id = id,
        babyId = babyId,
        parameter = GrowthParameter.fromName(parameter),
        measurementDateEpochMillis = measurementDateEpochMillis,
        chronologicalAgeWeeks = chronologicalAgeWeeks,
        pmaWeeks = pmaWeeks,
        value = value,
        unit = unit,
        note = note,
        percentileBadge = percentileBadge,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun GrowthMeasurement.toEntity(): GrowthMeasurementEntity {
    return GrowthMeasurementEntity(
        id = id,
        babyId = babyId,
        parameter = parameter.name,
        measurementDateEpochMillis = measurementDateEpochMillis,
        chronologicalAgeWeeks = chronologicalAgeWeeks,
        pmaWeeks = pmaWeeks,
        value = value,
        unit = unit,
        note = note,
        percentileBadge = percentileBadge,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
