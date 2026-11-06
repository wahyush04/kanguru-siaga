package com.kangurusiaga.app.domain.usecase

import com.kangurusiaga.app.domain.model.BabyResponseBreakdown
import com.kangurusiaga.app.domain.model.CaregiverDistribution
import com.kangurusiaga.app.domain.model.ClinicalEvaluation
import com.kangurusiaga.app.domain.model.DailyDuration
import com.kangurusiaga.app.domain.model.PmkCaregiver
import com.kangurusiaga.app.domain.model.PmkSegment
import com.kangurusiaga.app.domain.model.PmkSegmentStatus
import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.model.PmkStatistics
import com.kangurusiaga.app.domain.model.StatsPeriod
import com.kangurusiaga.app.domain.model.TimeDistribution
import com.kangurusiaga.app.domain.model.TimerStatus
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
        calendarInstance: Calendar = Calendar.getInstance(),
        babyBirthDateEpoch: Long? = null,
        firstPmkEpoch: Long? = null
    ): Flow<PmkStatistics> {
        val startDayOfWeek = if (firstPmkEpoch != null && firstPmkEpoch > 0L) {
            val c = Calendar.getInstance().apply { timeInMillis = firstPmkEpoch }
            c.get(Calendar.DAY_OF_WEEK)
        } else {
            Calendar.MONDAY
        }

        val (startEpoch, endEpoch, dateRangeText) = calculateEpochRange(period, calendarInstance, startDayOfWeek)

        return pmkRepository.getSessionsForBabyInRange(babyId, startEpoch, endEpoch)
            .map { sessions ->
                computeStatistics(period, sessions, dateRangeText, calendarInstance, babyBirthDateEpoch, startDayOfWeek)
            }
    }

    private fun calculateEpochRange(
        period: StatsPeriod,
        cal: Calendar,
        startDayOfWeek: Int = Calendar.MONDAY
    ): Triple<Long, Long, String> {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        val startSdf = SimpleDateFormat("dd MMM", Locale("id", "ID"))
        val copy = cal.clone() as Calendar

        val currentDayOfWeek = copy.get(Calendar.DAY_OF_WEEK)
        val daysDiff = (currentDayOfWeek - startDayOfWeek + 7) % 7

        return when (period) {
            StatsPeriod.THIS_WEEK -> {
                copy.add(Calendar.DAY_OF_YEAR, -daysDiff)
                copy.set(Calendar.HOUR_OF_DAY, 0)
                copy.set(Calendar.MINUTE, 0)
                copy.set(Calendar.SECOND, 0)
                copy.set(Calendar.MILLISECOND, 0)
                val start = copy.timeInMillis

                copy.add(Calendar.DAY_OF_YEAR, 6)
                copy.set(Calendar.HOUR_OF_DAY, 23)
                copy.set(Calendar.MINUTE, 59)
                copy.set(Calendar.SECOND, 59)
                copy.set(Calendar.MILLISECOND, 999)
                val end = copy.timeInMillis

                val text = "${startSdf.format(start)} - ${sdf.format(end)}"
                Triple(start, end, text)
            }
            StatsPeriod.LAST_WEEK -> {
                copy.add(Calendar.DAY_OF_YEAR, -daysDiff - 7)
                copy.set(Calendar.HOUR_OF_DAY, 0)
                copy.set(Calendar.MINUTE, 0)
                copy.set(Calendar.SECOND, 0)
                copy.set(Calendar.MILLISECOND, 0)
                val start = copy.timeInMillis

                copy.add(Calendar.DAY_OF_YEAR, 6)
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
        cal: Calendar,
        babyBirthDateEpoch: Long? = null,
        startDayOfWeek: Int = Calendar.MONDAY
    ): PmkStatistics {
        val dailyDurations = computeDailyDurations(sessions, cal, period, startDayOfWeek)
        val totalMinutes = if (period == StatsPeriod.THIS_WEEK || period == StatsPeriod.LAST_WEEK) {
            dailyDurations.sumOf { it.durationMinutes }
        } else {
            sessions.sumOf { calculateSessionActiveMinutes(it) }
        }
        val targetMinutes = when (period) {
            StatsPeriod.THIS_WEEK, StatsPeriod.LAST_WEEK -> 7 * 8 * 60 // 7 days * 8h = 3360m (56h target minimal)
            StatsPeriod.THIS_MONTH -> 30 * 8 * 60
            StatsPeriod.DAYS_30 -> 30 * 8 * 60
            StatsPeriod.ALL -> if (totalMinutes > 0) totalMinutes else 3360
        }

        val compliancePercent = if (targetMinutes > 0) {
            ((totalMinutes.toDouble() / targetMinutes) * 100).coerceIn(0.0, 100.0).roundToInt()
        } else 0

        val complianceBadge = when {
            compliancePercent >= 80 -> "Sangat Baik"
            compliancePercent >= 60 -> "Baik"
            compliancePercent >= 40 -> "Cukup"
            else -> "Perlu Ditingkatkan"
        }

        val totalHours = totalMinutes / 60
        val remainingMinutes = totalMinutes % 60
        val totalDetailedFormatted = "${totalHours} Jam ${remainingMinutes} Menit"
        val targetFormatted = "Target: 8j/hari"

        val daysInPeriod = if (period == StatsPeriod.THIS_WEEK || period == StatsPeriod.LAST_WEEK) 7 else 30
        val dailyAverageHours = (totalMinutes.toFloat() / 60f) / daysInPeriod.toFloat()

        val targetAchievedDays = dailyDurations.count { it.durationMinutes >= (8 * 60) }
        val targetUnmetDays = (daysInPeriod - targetAchievedDays).coerceAtLeast(0)

        var maxStreak = 0
        var curStreak = 0
        for (d in dailyDurations) {
            if (d.durationMinutes > 0) {
                curStreak++
                if (curStreak > maxStreak) maxStreak = curStreak
            } else {
                curStreak = 0
            }
        }
        val consecutiveDays = maxStreak
        // Aturan: Tercapai HANYA JIKA seluruh 7 hari dalam minggu tersebut tercapai >= 8 jam/hari (targetAchievedDays >= 7).
        // Jika tidak tercapai (ada hari yang < 8 jam), maka isWeeklyTargetMet = false (menampilkan Tidak Tercapai).
        val isWeeklyTargetMet = if (period == StatsPeriod.THIS_WEEK || period == StatsPeriod.LAST_WEEK) {
            targetAchievedDays >= 7
        } else {
            targetAchievedDays >= daysInPeriod
        }

        val babyChronologicalWeek = if (babyBirthDateEpoch != null && babyBirthDateEpoch > 0L) {
            val diff = (cal.timeInMillis - babyBirthDateEpoch).coerceAtLeast(0L)
            ((diff / (7 * 24 * 3600 * 1000L)) + 1).toInt()
        } else {
            1
        }

        val motivationalTip = if (compliancePercent >= 80) {
            "Luar biasa, Ayah & Bunda! Rata-rata durasi kontak kulit harian konsisten memenuhi target minimal 8 jam."
        } else {
            val remainingHours = ((targetMinutes - totalMinutes) / 60.0).coerceAtLeast(0.0)
            val formattedDiff = String.format(Locale("id", "ID"), "%.1f", remainingHours)
            "Hebat! Terus pertahankan estafet keluarga. Butuh sekitar $formattedDiff jam lagi untuk target optimal rekomendasi."
        }

        val avgMinutes = if (sessions.isNotEmpty()) totalMinutes / sessions.size else 0
        val longestMinutes = sessions.maxOfOrNull { calculateSessionActiveMinutes(it) } ?: 0

        // Time Distribution
        val timeDistribution = computeTimeDistribution(sessions)

        // Caregiver Distribution
        val caregiverDistribution = computeCaregiverDistribution(sessions)

        // Baby Response Breakdown (Stitch Screen 5331220106580777785)
        val babyResponseBreakdown = computeBabyResponseBreakdown(sessions)

        // Clinical Observations
        val temps = sessions.flatMap { session ->
            val list = mutableListOf<Double>()
            session.babyTemperature?.takeIf { it in 30.0..42.0 }?.let { list.add(it) }
            session.segments.mapNotNull { it.babyTemperature }.filter { it in 30.0..42.0 }.forEach { list.add(it) }
            list
        }
        val avgTemp = if (temps.isNotEmpty()) {
            (temps.average() * 10).roundToInt() / 10.0
        } else 0.0

        val normalTemps = temps.count { it in 36.5..37.5 }
        val normalTempPercent = if (temps.isNotEmpty()) {
            ((normalTemps.toDouble() / temps.size) * 100).roundToInt()
        } else 0

        val calmOverallPercent = if (babyResponseBreakdown.overallCalmPercentage > 0) {
            babyResponseBreakdown.overallCalmPercentage
        } else if (sessions.isNotEmpty()) {
            val calmCount = sessions.count {
                it.babyResponse == null || it.babyResponse?.contains("Tenang", ignoreCase = true) == true ||
                    it.babyResponse?.contains("Tidur", ignoreCase = true) == true
            }
            ((calmCount.toDouble() / sessions.size) * 100).roundToInt()
        } else {
            0
        }

        val clinicalEvaluation = ClinicalEvaluation(
            averageTemperature = avgTemp,
            normalTemperaturePercentage = normalTempPercent,
            calmnessPercentage = calmOverallPercent,
            feedingPercentage = babyResponseBreakdown.activeFeedingPercent,
            summaryNote = if (sessions.isNotEmpty()) {
                "Suhu tubuh bayi terpantau rata-rata ${if (avgTemp > 0) "${avgTemp}°C" else "-"} dengan kestabilan fisiologis yang terpantau optimal selama kontak kulit."
            } else {
                "Belum ada data evaluasi klinis yang tercatat untuk periode ini."
            }
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
            targetUnmetDays = targetUnmetDays,
            consecutiveDays = consecutiveDays,
            isWeeklyTargetMet = isWeeklyTargetMet,
            babyChronologicalWeek = babyChronologicalWeek,
            totalDaysInPeriod = daysInPeriod,
            motivationalTip = motivationalTip,
            averageMinutesPerSession = avgMinutes,
            totalSessions = sessions.size,
            longestSessionMinutes = longestMinutes,
            dailyDurations = dailyDurations,
            timeDistribution = timeDistribution,
            caregiverDistribution = caregiverDistribution,
            clinicalEvaluation = clinicalEvaluation,
            babyResponseBreakdown = babyResponseBreakdown,
            averageTemperature = avgTemp,
            calmnessPercentage = calmOverallPercent
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
        val ibuPct = if (total > 0) ((ibuMin / total) * 100).roundToInt() else 0
        val ayahPct = if (total > 0) ((ayahMin / total) * 100).roundToInt() else 0
        val pendampingPct = if (total > 0) (100 - ibuPct - ayahPct).coerceAtLeast(0) else 0

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
        period: StatsPeriod,
        startDayOfWeek: Int = Calendar.MONDAY
    ): List<DailyDuration> {
        val copy = cal.clone() as Calendar
        val currentDayOfWeek = copy.get(Calendar.DAY_OF_WEEK)
        val daysDiff = (currentDayOfWeek - startDayOfWeek + 7) % 7

        val weekOffsetDays = if (period == StatsPeriod.LAST_WEEK) -daysDiff - 7 else -daysDiff
        copy.add(Calendar.DAY_OF_YEAR, weekOffsetDays)
        copy.set(Calendar.HOUR_OF_DAY, 0)
        copy.set(Calendar.MINUTE, 0)
        copy.set(Calendar.SECOND, 0)
        copy.set(Calendar.MILLISECOND, 0)

        // Flatten all segments (same as PmkHistoryModels)
        val allSegments = sessions.flatMap { session ->
            if (session.segments.isNotEmpty()) {
                session.segments
            } else {
                listOf(
                    PmkSegment(
                        id = session.id,
                        sessionId = session.id,
                        caregiver = session.currentCaregiver,
                        startTimeEpoch = session.startTimeEpoch,
                        endTimeEpoch = if (session.status == TimerStatus.RUNNING) null else session.endTimeEpoch,
                        durationMinutes = session.activeDurationMinutes.coerceAtLeast(session.durationMinutes),
                        status = PmkSegmentStatus.ACTIVE,
                        babyTemperature = session.babyTemperature,
                        babyResponse = session.babyResponse,
                        notes = session.notes
                    )
                )
            }
        }.sortedBy { it.startTimeEpoch }

        val result = mutableListOf<DailyDuration>()

        for (i in 0..6) {
            val dayStart = copy.timeInMillis
            val dayOfWeekVal = copy.get(Calendar.DAY_OF_WEEK)
            val dayLabel = when (dayOfWeekVal) {
                Calendar.SUNDAY -> "Min"
                Calendar.MONDAY -> "Sen"
                Calendar.TUESDAY -> "Sel"
                Calendar.WEDNESDAY -> "Rab"
                Calendar.THURSDAY -> "Kam"
                Calendar.FRIDAY -> "Jum"
                Calendar.SATURDAY -> "Sab"
                else -> "Sen"
            }

            copy.add(Calendar.DAY_OF_YEAR, 1)
            val dayEnd = copy.timeInMillis - 1

            val timeline = GetDailyPmkTimelineUseCase.computeTimeline(
                dayStartEpoch = dayStart,
                dayEndEpoch = dayEnd,
                dayFormatted = dayLabel,
                segments = allSegments
            )
            val dayMinutes = timeline.totalActiveMinutes

            result.add(
                DailyDuration(
                    dayLabel = dayLabel,
                    durationMinutes = dayMinutes,
                    isAboveTarget = dayMinutes >= (8 * 60),
                    dayEpoch = dayStart
                )
            )
        }

        return result
    }

    private fun calculateSessionActiveMinutes(session: PmkSession): Int {
        val segmentMinutes = if (session.segments.isNotEmpty()) {
            session.segments.filter { it.status == PmkSegmentStatus.ACTIVE }.sumOf { seg ->
                if (seg.durationMinutes > 0) seg.durationMinutes
                else (seg.calculateDurationSeconds() / 60L).toInt().coerceAtLeast(0)
            }
        } else 0
        return maxOf(segmentMinutes, session.activeDurationMinutes, session.durationMinutes)
    }

    private fun computeBabyResponseBreakdown(sessions: List<PmkSession>): BabyResponseBreakdown {
        val responses = mutableListOf<String>()
        for (session in sessions) {
            val segResponses = session.segments.mapNotNull { it.babyResponse?.takeIf { r -> r.isNotBlank() } }
            if (segResponses.isNotEmpty()) {
                for (sr in segResponses) {
                    responses.addAll(sr.split(",").map { it.trim() }.filter { it.isNotEmpty() })
                }
            } else if (!session.babyResponse.isNullOrBlank()) {
                responses.addAll(session.babyResponse.split(",").map { it.trim() }.filter { it.isNotEmpty() })
            }
        }
        val totalRecorded = responses.size
        if (totalRecorded == 0) {
            return BabyResponseBreakdown()
        }

        var calm = 0
        var sleep = 0
        var activeFeeding = 0
        var restlessCrying = 0

        for (resp in responses) {
            val trimmed = resp.trim()
            when {
                // 4. Gelisah / Menangis
                trimmed.contains("Gelisah", ignoreCase = true) ||
                    trimmed.contains("Menangis", ignoreCase = true) ||
                    trimmed.contains("Rewel", ignoreCase = true) ||
                    trimmed.contains("Penyesuaian", ignoreCase = true) -> restlessCrying++

                // 3. Menyusu Aktif
                trimmed.contains("Menyusu", ignoreCase = true) ||
                    trimmed.contains("Rooting", ignoreCase = true) ||
                    trimmed.contains("Lapar", ignoreCase = true) -> activeFeeding++

                // 2. Tidur
                trimmed.equals("Tidur", ignoreCase = true) ||
                    trimmed.contains("Tidur Pulas", ignoreCase = true) ||
                    trimmed.contains("Tidur Tenang", ignoreCase = true) ||
                    trimmed.contains("Tidur", ignoreCase = true) -> sleep++

                // 1. Tenang
                trimmed.equals("Tenang", ignoreCase = true) ||
                    trimmed.contains("Tenang", ignoreCase = true) ||
                    trimmed.contains("Rileks", ignoreCase = true) ||
                    trimmed.contains("Nyaman", ignoreCase = true) -> calm++

                else -> calm++
            }
        }

        val calmPct = ((calm.toDouble() / totalRecorded) * 100).roundToInt()
        val sleepPct = ((sleep.toDouble() / totalRecorded) * 100).roundToInt()
        val activeFeedingPct = ((activeFeeding.toDouble() / totalRecorded) * 100).roundToInt()
        val restlessCryingPct = (100 - calmPct - sleepPct - activeFeedingPct).coerceAtLeast(0)
        val calmOverall = (calmPct + sleepPct).coerceIn(0, 100)

        return BabyResponseBreakdown(
            calmCount = calm,
            calmPercent = calmPct,
            sleepCount = sleep,
            sleepPercent = sleepPct,
            activeFeedingCount = activeFeeding,
            activeFeedingPercent = activeFeedingPct,
            restlessCryingCount = restlessCrying,
            restlessCryingPercent = restlessCryingPct,
            totalRecordedSessions = totalRecorded,
            overallCalmPercentage = calmOverall
        )
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
