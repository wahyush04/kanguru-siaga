package com.kangurusiaga.app.domain.model

enum class StatsPeriod(val title: String) {
    THIS_WEEK("Minggu Ini"),
    LAST_WEEK("Minggu Lalu"),
    THIS_MONTH("Bulan Ini"),
    DAYS_30("30 Hari"),
    ALL("Semua")
}

data class DailyDuration(
    val dayLabel: String, // "Sen", "Sel", "Rab", etc.
    val durationMinutes: Int,
    val isAboveTarget: Boolean = durationMinutes >= (18 * 60), // >= 18 hours clinical target
    val dayEpoch: Long = 0L
) {
    val durationHours: Float get() = durationMinutes / 60f
}

data class TimeDistribution(
    val morningCount: Int = 0,
    val morningPercent: Int = 0,
    val afternoonCount: Int = 0,
    val afternoonPercent: Int = 0,
    val eveningCount: Int = 0,
    val eveningPercent: Int = 0,
    val mostProductiveTime: String = "Pagi"
)

data class CaregiverDistribution(
    val ibuMinutes: Int = 0,
    val ayahMinutes: Int = 0,
    val pendampingMinutes: Int = 0,
    val ibuPercent: Int = 60,
    val ayahPercent: Int = 25,
    val pendampingPercent: Int = 15
) {
    val totalMinutes: Int get() = ibuMinutes + ayahMinutes + pendampingMinutes
    val ibuHoursFormatted: String get() = "${ibuMinutes / 60}j ${ibuMinutes % 60}m"
    val ayahHoursFormatted: String get() = "${ayahMinutes / 60}j ${ayahMinutes % 60}m"
    val pendampingHoursFormatted: String get() = "${pendampingMinutes / 60}j ${pendampingMinutes % 60}m"
}

data class ClinicalEvaluation(
    val averageTemperature: Double = 36.8,
    val normalTemperaturePercentage: Int = 98,
    val calmnessPercentage: Int = 94,
    val summaryNote: String = "Suhu tubuh bayi sangat stabil dalam rentang normal fisiologis (36.5° - 37.5°C). Pola pernapasan dan saturasi oksigen terpantau optimal selama kontak kulit."
)

data class PmkStatistics(
    val period: StatsPeriod = StatsPeriod.THIS_WEEK,
    val dateRangeText: String = "",
    val compliancePercentage: Int = 0,
    val complianceBadge: String = "Perlu Ditingkatkan",
    val totalDurationMinutes: Int = 0,
    val targetDurationMinutes: Int = 7560, // 7 days * 18 hours = 126 hours weekly target
    val totalDurationFormatted: String = "0 jam 0 mnt",
    val targetDurationFormatted: String = "Target: 18j/hari",
    val dailyAverageHours: Float = 0f,
    val targetAchievedDays: Int = 0,
    val totalDaysInPeriod: Int = 7,
    val motivationalTip: String = "",
    val averageMinutesPerSession: Int = 0,
    val totalSessions: Int = 0,
    val longestSessionMinutes: Int = 0,
    val dailyDurations: List<DailyDuration> = emptyList(),
    val timeDistribution: TimeDistribution = TimeDistribution(),
    val caregiverDistribution: CaregiverDistribution = CaregiverDistribution(),
    val clinicalEvaluation: ClinicalEvaluation = ClinicalEvaluation(),
    val averageTemperature: Double = 36.8,
    val calmnessPercentage: Int = 100,
    val clinicalNote: String = "Kontak kulit kontinu terbukti signifikan mempertahankan ritme napas, kestabilan suhu, dan kedekatan emosional."
)
