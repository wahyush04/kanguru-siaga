package com.kangurusiaga.app.domain.model

enum class MetricType {
    WEIGHT,
    LENGTH,
    HEAD_CIRCUMFERENCE;

    fun toGrowthParameter(): GrowthParameter = when (this) {
        WEIGHT -> GrowthParameter.WEIGHT
        LENGTH -> GrowthParameter.LENGTH
        HEAD_CIRCUMFERENCE -> GrowthParameter.HEAD_CIRCUMFERENCE
    }

    companion object {
        fun fromGrowthParameter(param: GrowthParameter): MetricType = when (param) {
            GrowthParameter.WEIGHT -> WEIGHT
            GrowthParameter.LENGTH -> LENGTH
            GrowthParameter.HEAD_CIRCUMFERENCE -> HEAD_CIRCUMFERENCE
        }

        fun fromString(value: String): MetricType = entries.firstOrNull {
            it.name.equals(value, ignoreCase = true)
        } ?: WEIGHT
    }
}
