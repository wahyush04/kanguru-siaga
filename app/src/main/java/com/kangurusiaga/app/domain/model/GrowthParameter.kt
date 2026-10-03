package com.kangurusiaga.app.domain.model

enum class GrowthParameter(
    val title: String,
    val unit: String,
    val description: String,
    val badgeLabel: String,
    val minValidValue: Float,
    val maxValidValue: Float,
    val yMin: Float,
    val yMax: Float,
    val yStep: Float
) {
    WEIGHT(
        title = "Berat Badan",
        unit = "kg",
        description = "Grafik pertumbuhan berat badan per minggu",
        badgeLabel = "Rutin",
        minValidValue = 0.3f,
        maxValidValue = 12.0f,
        yMin = 0f,
        yMax = 5f,
        yStep = 1f
    ),
    LENGTH(
        title = "Panjang Badan",
        unit = "cm",
        description = "Grafik pertumbuhan panjang badan (cm)",
        badgeLabel = "Fenton PB",
        minValidValue = 20.0f,
        maxValidValue = 80.0f,
        yMin = 20f,
        yMax = 70f,
        yStep = 10f
    ),
    HEAD_CIRCUMFERENCE(
        title = "Lingkar Kepala",
        unit = "cm",
        description = "Grafik lingkar kepala per minggu (cm)",
        badgeLabel = "Fenton LK",
        minValidValue = 15.0f,
        maxValidValue = 60.0f,
        yMin = 15f,
        yMax = 45f,
        yStep = 5f
    );

    val displayName: String get() = title

    companion object {
        fun fromName(name: String?): GrowthParameter {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: WEIGHT
        }
    }
}
