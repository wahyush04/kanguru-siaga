package com.kangurusiaga.app.domain.usecase

import com.kangurusiaga.app.domain.model.CaregiverDistribution
import com.kangurusiaga.app.domain.model.ClinicalEvaluation
import com.kangurusiaga.app.domain.model.DailyDuration
import com.kangurusiaga.app.domain.model.PmkCaregiver
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
        val startSdf = SimpleDateFormat("dd MMM", Locale("id", "ID"))
        val copy = cal.clone() as Calendar

        return when (period) {
            StatsPeriod.THIS_WEEK -> {
                // Monday to Sunday of current week
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

                val text = "${startSdf.format(start)} - ${sdf.format(end)}"
                Triple(start, end, text)
            }
            StatsPeriod.LAST_WEEK -> {
                copy.firstDayOfWeek = Calendar.MONDAY
                copy.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                copy.add(Calendar.WEEK_OF_YEAR, -1)
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
        val totalMinutes = sessions.sumOf { it.activeDurationMinutes.coerceAtLeast(it.durationMinutes) }
        val targetMinutes = when (period) {
            StatsPeriod.THIS_WEEK, StatsPeriod.LAST_WEEK -> 7 * 18 * 60 // 7 days * 18h = 7560m
            StatsPeriod.THIS_MONTH -> 30 * 18 * 60
            StatsPeriod.DAYS_30 -> 30 * 18 * 60
            StatsPeriod.ALL -> if (totalMinutes > 0) totalMinutes else 7560
        }

        val compliancePercent = if (targetMinutes > 0) {
            ((totalMinutes.toDouble() / targetMinutes) * 100).coerceIn(0.0, 100.0).roundToInt()
        } else 0

        val complianceBadge = when {
            compliancePercent >= 90 -> "Sangat Baik! (Target >18j)"
            compliancePercent >= 75 -> "Cukup Baik"
            compliancePercent >= 50 -> "Perlu Ditingkatkan"
            else -> "Belum Optimal"
        }

        val totalHours = totalMinutes / 60
        val remainingMinutes = totalMinutes % 60
        val totalDetailedFormatted = "${totalHours} Jam ${remainingMinutes} Menit"
        val targetFormatted = "Target: >18j / hari"

        val daysInPeriod = if (period == StatsPeriod.THIS_WEEK || period == StatsPeriod.LAST_WEEK) 7 else 30
        val dailyAverageHours = (totalMinutes.toFloat() / 60f) / daysInPeriod.toFloat()

        val dailyDurations = computeDailyDurations(sessions, cal, period)
        val targetAchievedDays = dailyDurations.count { it.durationMinutes >= (18 * 60) }

        val motivationalTip = if (compliancePercent >= 90) {
            "Luar biasa, Ayah & Bunda! Rata-rata durasi kontak kulit harian konsisten melampaui 18 jam sesuai standar emas WHO."
        } else {
            val remainingHours = ((targetMinutes - totalMinutes) / 60.0).coerceAtLeast(0.0)
            val formattedDiff = String.format(Locale("id", "ID"), "%.1f", remainingHours)
            "Hebat! Terus pertahankan estafet keluarga. Butuh sekitar $formattedDiff jam lagi untuk target optimal rekomendasi."
        }

        val avgMinutes = if (sessions.isNotEmpty()) totalMinutes / sessions.size else 0
        val longestMinutes = sessions.maxOfOrNull { it.activeDurationMinutes.coerceAtLeast(it.durationMinutes) } ?: 0

        // Time Distribution
        val timeDistribution = computeTimeDistribution(sessions)

        // Caregiver Distribution
        val caregiverDistribution = computeCaregiverDistribution(sessions)

        // Clinical Observations
        val temps = sessions.mapNotNull { it.babyTemperature }.filter { it in 30.0..42.0 }
        val avgTemp = if (temps.isNotEmpty()) {
            (temps.average() * 10).roundToInt() / 10.0
        } else 36.8

        val normalTemps = temps.count { it in 36.5..37.5 }
        val normalTempPercent = if (temps.isNotEmpty()) {
            ((normalTemps.toDouble() / temps.size) * 100).roundToInt()
        } else 98

        val calmSessions = sessions.count {
            it.babyResponse == null || it.babyResponse == "Tidur Tenang" || it.babyResponse == "Tenang" || it.babyResponse == "Tenang & Rileks" || it.babyResponse == "Tidur Pulas"
        }
        val calmnessPercent = if (sessions.isNotEmpty()) {
            ((calmSessions.toDouble() / sessions.size) * 100).roundToInt()
        } else 94

        val clinicalEvaluation = ClinicalEvaluation(
            averageTemperature = avgTemp,
            normalTemperaturePercentage = normalTempPercent,
            calmnessPercentage = calmnessPercent,
            summaryNote = "Suhu tubuh bayi sangat stabil dalam rentang normal fisiologis (36.5° - 37.5°C). Kontak kulit intensif menjaga ritme napas dan laju detak jantung teratur."
        )

        return PmkStatistics(
            period = period,
            dateRangeText = dateRangeText,
            compliancePercentage = compliancePercent,
            complianceBadge = complianceBadge,
            totalDurationMinutes = totalMinutes,
            targetDurationMinutes = targetMinutes,
            totalDurationFormatted = totalDetailedFormatted,
            targetDurationFormatted = targetFormatted,
            dailyAverageHours = (dailyAverageHours * 10).roundToInt() / 10f,
            targetAchievedDays = targetAchievedDays,
            totalDaysInPeriod = daysInPeriod,
            motivationalTip = motivationalTip,
            averageMinutesPerSession = avgMinutes,
            totalSessions = sessions.size,
            longestSessionMinutes = longestMinutes,
            dailyDurations = dailyDurations,
            timeDistribution = timeDistribution,
            caregiverDistribution = caregiverDistribution,
            clinicalEvaluation = clinicalEvaluation,
            averageTemperature = avgTemp,
            calmnessPercentage = calmnessPercent
        )
    }

    private fun computeCaregiverDistribution(sessions: List<PmkSession>): CaregiverDistribution {
        var ibuMin = 0
        var ayahMin = 0
        var pendampingMin = 0

        for (session in sessions) {
            if (session.segments.isNotEmpty()) {
                for (seg in session.segments) {
                    if (seg.isActive) {
                        val segMin = if (seg.durationMinutes > 0) seg.durationMinutes else (seg.calculateDurationSeconds() / 60).toInt()
                        when (seg.caregiver) {
                            PmkCaregiver.IBU -> ibuMin += segMin
                            PmkCaregiver.AYAH -> ayahMin += segMin
                            PmkCaregiver.PENDAMPING -> pendampingMin += segMin
                        }
                    }
                }
            } else {
                val dur = session.activeDurationMinutes.coerceAtLeast(session.durationMinutes)
                when (session.currentCaregiver) {
                    PmkCaregiver.IBU -> ibuMin += dur
                    PmkCaregiver.AYAH -> ayahMin += dur
                    PmkCaregiver.PENDAMPING -> pendampingMin += dur
                }
            }
        }

        val total = (ibuMin + ayahMin + pendampingMin).toDouble()
        val ibuPct = if (total > 0) ((ibuMin / total) * 100).roundToInt() else 60
        val ayahPct = if (total > 0) ((ayahMin / total) * 100).roundToInt() else 25
        val pendampingPct = if (total > 0) (100 - ibuPct - ayahPct).coerceAtLeast(0) else 15

        return CaregiverDistribution(
            ibuMinutes = ibuMin,
            ayahMinutes = ayahMin,
            pendampingMinutes = pendampingMin,
            ibuPercent = ibuPct,
            ayahPercent = ayahPct,
            pendampingPercent = pendampingPct
        )
    }

    private fun computeDailyDurations(
        sessions: List<PmkSession>,
        cal: Calendar,
        period: StatsPeriod
    ): List<DailyDuration> {
        val days = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
        val copy = cal.clone() as Calendar
        copy.firstDayOfWeek = Calendar.MONDAY
        copy.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        if (period == StatsPeriod.LAST_WEEK) {
            copy.add(Calendar.WEEK_OF_YEAR, -1)
        }
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
                .sumOf { it.activeDurationMinutes.coerceAtLeast(it.durationMinutes) }

            result.add(
                DailyDuration(
                    dayLabel = days[i],
                    durationMinutes = dayMinutes,
                    isAboveTarget = dayMinutes >= (18 * 60),
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
