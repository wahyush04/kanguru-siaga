package com.kangurusiaga.app.domain.model

enum class WeeklyRecapTab(val title: String) {
    REKAP_MINGGUAN("Rekap Mingguan PMK"),
    KORELASI_PERTUMBUHAN("Korelasi Pertumbuhan")
}

data class DailyActivityBar(
    val dayLabel: String,
    val durationHours: Float,
    val durationFormatted: String,
    val isAboveTarget: Boolean
)

enum class FentonStatusType {
    NORMAL,
    WARNING,
    ALERT
}

data class FentonMetricCardData(
    val label: String,
    val valueNumber: String,
    val unit: String,
    val deltaPercentileText: String,
    val statusText: String,
    val statusType: FentonStatusType
)

data class WeeklyFentonSummary(
    val statusBadge: String,
    val statusType: FentonStatusType,
    val warningNote: String? = null,
    val metrics: List<FentonMetricCardData>
)

data class WeeklyPmkEvaluation(
    val weekNumber: Int,
    val statusLabel: String, // "Berjalan", "Selesai"
    val isCurrentWeek: Boolean = false,
    val targetStatusBadge: String, // "Tidak Mencapai Target", "Mencapai Target", "Tidak Sesuai Target"
    val isTargetMet: Boolean,
    val dateRangeText: String,
    val targetHoursText: String = "8 - 24 Jam/Hari",
    val achievementText: String,
    val unmetDaysCount: Int = 2,
    val unmetDaysDetail: String = "",
    val unmetDaysText: String,
    val statusRequirementText: String = "• Status: Tidak Mencapai Target (syarat: seluruh 7 hari wajib ≥8j/hari)",
    val averageDailyHoursFormatted: String,
    val dailyActivities: List<DailyActivityBar>,
    val fentonSummary: WeeklyFentonSummary
)

data class CorrelationChartPoint(
    val weekLabel: String, // "Mgg 1", "Mgg 2", "Mgg 3"
    val phaseLabel: String, // "Adaptasi", "Estafet", "Aktif"
    val contactHours: Float, // 6.2f, 8.8f, 9.4f
    val weightGainGram: Int, // 110, 170, 180
    val isAboveTarget: Boolean
)

data class FentonTriParameterItem(
    val title: String,
    val valueFormatted: String,
    val deltaFormatted: String,
    val percentileFormatted: String,
    val statusBadge: String,
    val isOptimal: Boolean,
    val isTeal: Boolean = false,
    val subDescription: String
)

data class ClinicalResponseRow(
    val periodLabel: String, // "Mgg 3", "Mgg 2", "Mgg 1"
    val avgPmkFormatted: String, // "9j 25m", "8j 50m", "6j 15m"
    val weightVelocityFormatted: String, // "+26g/hr", "+24g/hr", "+15g/hr"
    val temperatureFormatted: String, // "36.8°C", "36.7°C", "36.5°C"
    val statusBadge: String, // "Optimal", "Baik", "<8 Jam"
    val isOptimal: Boolean,
    val isWarning: Boolean = false
)

data class GrowthCorrelationData(
    val correlationCoefficient: String = "+0.88",
    val statusBadge: String = "Pertumbuhan Optimal",
    val dominantFactor: String = "Termoregulasi Stabil",
    val insightDescription: String = "Setiap peningkatan 2 jam durasi PMK kontinu harian berkorelasi dengan kenaikan berat badan +28g/hari (sesuai target catch-up growth kurva Fenton 25-30g/hari).",
    val chartPoints: List<CorrelationChartPoint> = emptyList(),
    val triParameters: List<FentonTriParameterItem> = emptyList(),
    val clinicalResponseRows: List<ClinicalResponseRow> = emptyList(),
    val clinicalRecommendationQuote: String = "\"Kenaikan BB bayi stabil mendekati garis P50 Fenton. Pertahankan durasi PMK estafet minimal 8–12 jam sehari terutama pada malam hari untuk meminimalkan fluktuasi suhu dan pengeluaran energi kalori.\""
)

data class WeeklyRecapData(
    val babyName: String = "",
    val babyPhotoUri: String? = null,
    val birthWeightGram: Int = 0,
    val currentWeightGram: Int = 0,
    val pmaWeeksText: String = "-",
    val avgContactFormatted: String = "0j 0m/hr",
    val complianceFormatted: String = "0/0 Mgg",
    val weightGainFormatted: String = "0g",
    val weeklyEvaluations: List<WeeklyPmkEvaluation> = emptyList(),
    val correlationData: GrowthCorrelationData = GrowthCorrelationData()
)
