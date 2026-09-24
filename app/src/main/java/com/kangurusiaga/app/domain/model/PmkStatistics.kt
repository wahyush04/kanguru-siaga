package com.kangurusiaga.app.domain.model

enum class StatsPeriod(val title: String) {
    THIS_WEEK("Minggu Ini"),
    THIS_MONTH("Bulan Ini"),
    DAYS_30("30 Hari"),
    ALL("Semua")
}

data class DailyDuration(
    val dayLabel: String, // "Sen", "Sel", "Rab", etc.
    val durationMinutes: Int,
    val isAboveTarget: Boolean = durationMinutes >= 60,
    val dayEpoch: Long = 0L
)

data class TimeDistribution(
    val morningCount: Int = 0,
    val morningPercent: Int = 0,
    val afternoonCount: Int = 0,
    val afternoonPercent: Int = 0,
    val eveningCount: Int = 0,
    val eveningPercent: Int = 0,
    val mostProductiveTime: String = "Pagi"
)

data class PmkStatistics(
    val period: StatsPeriod = StatsPeriod.THIS_WEEK,
    val dateRangeText: String = "",
    val compliancePercentage: Int = 0,
    val complianceBadge: String = "Perlu Ditingkatkan",
    val totalDurationMinutes: Int = 0,
    val targetDurationMinutes: Int = 1020, // 17 hours weekly default
    val totalDurationFormatted: String = "0 jam 0 mnt",
    val targetDurationFormatted: String = "Target: 17 jam",
    val motivationalTip: String = "",
    val averageMinutesPerSession: Int = 0,
    val totalSessions: Int = 0,
    val longestSessionMinutes: Int = 0,
    val dailyDurations: List<DailyDuration> = emptyList(),
    val timeDistribution: TimeDistribution = TimeDistribution(),
    val averageTemperature: Double = 36.8,
    val calmnessPercentage: Int = 100,
    val clinicalNote: String = "Kontak kulit rutin minggu ini terbukti signifikan mempertahankan ritme napas dan stabilitas suhu bayi."
)
