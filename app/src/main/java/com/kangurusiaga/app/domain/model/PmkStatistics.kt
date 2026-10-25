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
    val isAboveTarget: Boolean = durationMinutes >= (8 * 60), // >= 8 hours minimal target
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
    val ibuPercent: Int = 0,
    val ayahPercent: Int = 0,
    val pendampingPercent: Int = 0
) {
    val totalMinutes: Int get() = ibuMinutes + ayahMinutes + pendampingMinutes
    val activeCaregiverCount: Int get() {
        var count = 0
        if (ibuMinutes > 0) count++
        if (ayahMinutes > 0) count++
        if (pendampingMinutes > 0) count++
        return count
    }
    val ibuHoursFormatted: String get() = "${ibuMinutes / 60}j ${ibuMinutes % 60}m"
    val ayahHoursFormatted: String get() = "${ayahMinutes / 60}j ${ayahMinutes % 60}m"
    val pendampingHoursFormatted: String get() = "${pendampingMinutes / 60}j ${pendampingMinutes % 60}m"
}

data class ClinicalEvaluation(
    val averageTemperature: Double = 0.0,
    val normalTemperaturePercentage: Int = 0,
    val calmnessPercentage: Int = 0,
    val feedingPercentage: Int = 0,
    val summaryNote: String = ""
)

data class BabyResponseBreakdown(
    val deepSleepCount: Int = 0,
    val deepSleepPercent: Int = 0,
    val activeFeedingCount: Int = 0,
    val activeFeedingPercent: Int = 0,
    val alertCalmCount: Int = 0,
    val alertCalmPercent: Int = 0,
    val adjustingCount: Int = 0,
    val adjustingPercent: Int = 0,
    val totalRecordedSessions: Int = 0,
    val overallCalmPercentage: Int = 0
) {
    val totalSessions: Int get() = totalRecordedSessions
    val deepSleepSessions: Int get() = deepSleepCount
    val activeFeedingSessions: Int get() = activeFeedingCount
    val alertCalmSessions: Int get() = alertCalmCount
    val adjustingSessions: Int get() = adjustingCount
}

data class PmkStatistics(
    val period: StatsPeriod = StatsPeriod.THIS_WEEK,
    val dateRangeText: String = "",
    val compliancePercentage: Int = 0,
    val complianceBadge: String = "Belum Ada",
    val totalDurationMinutes: Int = 0,
    val targetDurationMinutes: Int = 3360, // 7 days * 8 hours = 3360 minutes (56 hours)
    val totalDurationFormatted: String = "0 jam 0 mnt",
    val targetDurationFormatted: String = "Target: 8j/hari",
    val dailyAverageHours: Float = 0f,
    val targetAchievedDays: Int = 0,
    val targetUnmetDays: Int = 7,
    val consecutiveDays: Int = 0,
    val isWeeklyTargetMet: Boolean = false,
    val babyChronologicalWeek: Int = 1,
    val totalDaysInPeriod: Int = 7,
    val motivationalTip: String = "",
    val averageMinutesPerSession: Int = 0,
    val totalSessions: Int = 0,
    val longestSessionMinutes: Int = 0,
    val dailyDurations: List<DailyDuration> = emptyList(),
    val timeDistribution: TimeDistribution = TimeDistribution(),
    val caregiverDistribution: CaregiverDistribution = CaregiverDistribution(),
    val clinicalEvaluation: ClinicalEvaluation = ClinicalEvaluation(),
    val babyResponseBreakdown: BabyResponseBreakdown = BabyResponseBreakdown(),
    val averageTemperature: Double = 0.0,
    val calmnessPercentage: Int = 0,
    val clinicalNote: String = "Kontak kulit kontinu terbukti signifikan mempertahankan ritme napas, kestabilan suhu, dan kedekatan emosional."
)
