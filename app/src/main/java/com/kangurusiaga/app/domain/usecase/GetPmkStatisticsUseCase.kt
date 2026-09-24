package com.kangurusiaga.app.domain.usecase

import com.kangurusiaga.app.domain.model.DailyDuration
import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.model.PmkStatistics
import com.kangurusiaga.app.domain.model.StatsPeriod
import com.kangurusiaga.app.domain.model.TimeDistribution
import com.kangurusiaga.app.domain.repository.PmkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt

class GetPmkStatisticsUseCase @Inject constructor(
    private val pmkRepository: PmkRepository
) {
    operator fun invoke(
        babyId: Long,
        period: StatsPeriod = StatsPeriod.THIS_WEEK,
        calendarInstance: Calendar = Calendar.getInstance()
    ): Flow<PmkStatistics> {
        val (startEpoch, endEpoch, dateRangeText) = calculateEpochRange(period, calendarInstance)

        return pmkRepository.getSessionsForBabyInRange(babyId, startEpoch, endEpoch)
            .map { sessions ->
                computeStatistics(period, sessions, dateRangeText, calendarInstance)
            }
    }

    private fun calculateEpochRange(
        period: StatsPeriod,
        cal: Calendar
    ): Triple<Long, Long, String> {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        val copy = cal.clone() as Calendar

        return when (period) {
            StatsPeriod.THIS_WEEK -> {
                // Adjust to Monday of this week
                copy.firstDayOfWeek = Calendar.MONDAY
                copy.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                copy.set(Calendar.HOUR_OF_DAY, 0)
                copy.set(Calendar.MINUTE, 0)
                copy.set(Calendar.SECOND, 0)
                copy.set(Calendar.MILLISECOND, 0)
                val start = copy.timeInMillis

                copy.add(Calendar.DAY_OF_WEEK, 6)
                copy.set(Calendar.HOUR_OF_DAY, 23)
                copy.set(Calendar.MINUTE, 59)
                copy.set(Calendar.SECOND, 59)
                copy.set(Calendar.MILLISECOND, 999)
                val end = copy.timeInMillis

                val startSdf = SimpleDateFormat("dd MMM", Locale("id", "ID"))
                val text = "${startSdf.format(start)} - ${sdf.format(end)}"
                Triple(start, end, text)
            }
            StatsPeriod.THIS_MONTH -> {
                copy.set(Calendar.DAY_OF_MONTH, 1)
                copy.set(Calendar.HOUR_OF_DAY, 0)
                copy.set(Calendar.MINUTE, 0)
                copy.set(Calendar.SECOND, 0)
                copy.set(Calendar.MILLISECOND, 0)
                val start = copy.timeInMillis

                val maxDay = copy.getActualMaximum(Calendar.DAY_OF_MONTH)
                copy.set(Calendar.DAY_OF_MONTH, maxDay)
                copy.set(Calendar.HOUR_OF_DAY, 23)
                copy.set(Calendar.MINUTE, 59)
                copy.set(Calendar.SECOND, 59)
                copy.set(Calendar.MILLISECOND, 999)
                val end = copy.timeInMillis

                val text = "1 - ${sdf.format(end)}"
                Triple(start, end, text)
            }
            StatsPeriod.DAYS_30 -> {
                copy.set(Calendar.HOUR_OF_DAY, 23)
                copy.set(Calendar.MINUTE, 59)
                copy.set(Calendar.SECOND, 59)
                val end = copy.timeInMillis

                copy.add(Calendar.DAY_OF_YEAR, -30)
                copy.set(Calendar.HOUR_OF_DAY, 0)
                copy.set(Calendar.MINUTE, 0)
                copy.set(Calendar.SECOND, 0)
                val start = copy.timeInMillis

                val startSdf = SimpleDateFormat("dd MMM", Locale("id", "ID"))
                val text = "${startSdf.format(start)} - ${sdf.format(end)}"
                Triple(start, end, text)
            }
            StatsPeriod.ALL -> {
                val start = 0L
                val end = System.currentTimeMillis()
                val text = "Semua Catatan"
                Triple(start, end, text)
            }
        }
    }

    private fun computeStatistics(
        period: StatsPeriod,
        sessions: List<PmkSession>,
        dateRangeText: String,
        cal: Calendar
    ): PmkStatistics {
        val totalMinutes = sessions.sumOf { it.durationMinutes }
        val targetMinutes = when (period) {
            StatsPeriod.THIS_WEEK -> 1020 // 17 hours = ~2.5 hrs/day
            StatsPeriod.THIS_MONTH -> 4380
            StatsPeriod.DAYS_30 -> 4380
            StatsPeriod.ALL -> if (totalMinutes > 0) totalMinutes else 1020
        }

        val compliancePercent = if (targetMinutes > 0) {
            ((totalMinutes.toDouble() / targetMinutes) * 100).coerceIn(0.0, 100.0).roundToInt()
        } else 0

        val complianceBadge = when {
            compliancePercent >= 80 -> "Sangat Baik!"
            compliancePercent >= 50 -> "Cukup Baik"
            else -> "Perlu Ditingkatkan"
        }

        val totalHours = totalMinutes / 60
        val remainingMinutes = totalMinutes % 60
        val totalFormatted = "${totalHours}j ${remainingMinutes}m"
        val totalDetailedFormatted = "${totalHours} jam ${remainingMinutes} mnt"
        val targetFormatted = "Target: ${targetMinutes / 60} jam"

        val motivationalTip = if (totalMinutes >= targetMinutes) {
            "Luar biasa, Bunda! Target rekomendasi PMK minggu ini telah tercapai dengan sangat sempurna."
        } else {
            val remainingDiffHours = String.format(Locale("id", "ID"), "%.1f", (targetMinutes - totalMinutes) / 60.0)
            "Luar biasa, Bunda! Hanya butuh $remainingDiffHours jam lagi untuk menuntaskan rekomendasi optimal minggu ini."
        }

        val avgMinutes = if (sessions.isNotEmpty()) totalMinutes / sessions.size else 0
        val longestMinutes = sessions.maxOfOrNull { it.durationMinutes } ?: 0

        // Daily Bar Chart (7 days)
        val dailyDurations = computeDailyDurations(sessions, cal)

        // Time Distribution
        val timeDistribution = computeTimeDistribution(sessions)

        // Clinical Observations
        val temps = sessions.mapNotNull { it.babyTemperature }.filter { it > 30.0 }
        val avgTemp = if (temps.isNotEmpty()) {
            val rounded = (temps.average() * 10).roundToInt() / 10.0
            rounded
        } else 36.8

        val calmSessions = sessions.count {
            it.babyResponse == null || it.babyResponse == "Tidur Tenang" || it.babyResponse == "Tenang"
        }
        val calmnessPercent = if (sessions.isNotEmpty()) {
            ((calmSessions.toDouble() / sessions.size) * 100).roundToInt()
        } else 92

        return PmkStatistics(
            period = period,
            dateRangeText = dateRangeText,
            compliancePercentage = compliancePercent,
            complianceBadge = complianceBadge,
            totalDurationMinutes = totalMinutes,
            targetDurationMinutes = targetMinutes,
            totalDurationFormatted = totalDetailedFormatted,
            targetDurationFormatted = targetFormatted,
            motivationalTip = motivationalTip,
            averageMinutesPerSession = avgMinutes,
            totalSessions = sessions.size,
            longestSessionMinutes = longestMinutes,
            dailyDurations = dailyDurations,
            timeDistribution = timeDistribution,
            averageTemperature = avgTemp,
            calmnessPercentage = calmnessPercent
        )
    }

    private fun computeDailyDurations(
        sessions: List<PmkSession>,
        cal: Calendar
    ): List<DailyDuration> {
        val days = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
        val copy = cal.clone() as Calendar
        copy.firstDayOfWeek = Calendar.MONDAY
        copy.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        copy.set(Calendar.HOUR_OF_DAY, 0)
        copy.set(Calendar.MINUTE, 0)
        copy.set(Calendar.SECOND, 0)
        copy.set(Calendar.MILLISECOND, 0)

        val result = mutableListOf<DailyDuration>()

        for (i in 0..6) {
            val dayStart = copy.timeInMillis
            copy.add(Calendar.DAY_OF_YEAR, 1)
            val dayEnd = copy.timeInMillis - 1

            val dayMinutes = sessions.filter { it.startTimeEpoch in dayStart..dayEnd }
                .sumOf { it.durationMinutes }

            result.add(
                DailyDuration(
                    dayLabel = days[i],
                    durationMinutes = dayMinutes,
                    isAboveTarget = dayMinutes >= 60,
                    dayEpoch = dayStart
                )
            )
        }

        return result
    }

    private fun computeTimeDistribution(sessions: List<PmkSession>): TimeDistribution {
        if (sessions.isEmpty()) {
            return TimeDistribution(
                morningCount = 0,
                morningPercent = 0,
                afternoonCount = 0,
                afternoonPercent = 0,
                eveningCount = 0,
                eveningPercent = 0,
                mostProductiveTime = "Pagi"
            )
        }

        val cal = Calendar.getInstance()
        var morning = 0
        var afternoon = 0
        var evening = 0

        for (session in sessions) {
            cal.timeInMillis = session.startTimeEpoch
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            when (hour) {
                in 6..10 -> morning++
                in 11..15 -> afternoon++
                else -> evening++
            }
        }

        val total = sessions.size.toDouble()
        val morningPct = ((morning / total) * 100).roundToInt()
        val afternoonPct = ((afternoon / total) * 100).roundToInt()
        val eveningPct = ((evening / total) * 100).roundToInt()

        val mostProductive = when {
            morning >= afternoon && morning >= evening -> "Pagi"
            afternoon >= morning && afternoon >= evening -> "Siang"
            else -> "Malam"
        }

        return TimeDistribution(
            morningCount = morning,
            morningPercent = morningPct,
            afternoonCount = afternoon,
            afternoonPercent = afternoonPct,
            eveningCount = evening,
            eveningPercent = eveningPct,
            mostProductiveTime = mostProductive
        )
    }
}
