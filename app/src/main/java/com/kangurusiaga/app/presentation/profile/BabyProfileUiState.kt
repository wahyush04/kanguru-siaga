package com.kangurusiaga.app.presentation.profile

import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.GrowthMeasurement

data class MetricEvaluation(
    val valueFormatted: String = "-",
    val unit: String = "",
    val percentileText: String = "Persentil -",
    val statusText: String = "Normal"
)

data class BabyProfileUiState(
    val isLoading: Boolean = true,
    val baby: Baby? = null,
    val formattedBirthDate: String = "",
    val chronologicalAgeWeeks: Int = 0,
    val chronologicalAgeMonths: Int = 0,
    val isBblr: Boolean = false,
    val isPremature: Boolean = false,
    val weightMetric: MetricEvaluation = MetricEvaluation(valueFormatted = "-", unit = "kg"),
    val lengthMetric: MetricEvaluation = MetricEvaluation(valueFormatted = "-", unit = "cm"),
    val headMetric: MetricEvaluation = MetricEvaluation(valueFormatted = "-", unit = "cm"),
    val growthStatusSummary: String = "Catch-up Growth Normal",
    val errorMessage: String? = null
)
