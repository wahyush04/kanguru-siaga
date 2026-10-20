package com.kangurusiaga.app.presentation.pmk.history

import com.kangurusiaga.app.domain.model.PmkSegment
import com.kangurusiaga.app.domain.model.PmkSegmentStatus
import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.model.TimerStatus
import com.kangurusiaga.app.domain.usecase.DailyTimeline
import com.kangurusiaga.app.domain.usecase.GetDailyPmkTimelineUseCase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DailyGroup(
    val dateKey: String, // "yyyy-MM-dd"
    val dateFormatted: String, // "Kamis, 24 Oktober 2024"
    val isToday: Boolean,
    val isYesterday: Boolean,
    val dayStartEpoch: Long,
    val dayEndEpoch: Long,
    val timeline: DailyTimeline,
    val segments: List<PmkSegment>,
    val sessions: List<PmkSession>
)

fun groupSessionsByDay(sessions: List<PmkSession>): List<DailyGroup> {
    if (sessions.isEmpty()) {
        return emptyList()
    }

    val dateKeySdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val displaySdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
    val cal = Calendar.getInstance()

    val todayKey = dateKeySdf.format(Date())
    cal.add(Calendar.DAY_OF_YEAR, -1)
    val yesterdayKey = dateKeySdf.format(cal.time)

    // Map each day
    val grouped = mutableMapOf<String, MutableList<PmkSession>>()
    for (session in sessions) {
        val key = dateKeySdf.format(Date(session.startTimeEpoch))
        grouped.getOrPut(key) { mutableListOf() }.add(session)
    }

    return grouped.entries.sortedByDescending { it.key }.map { (key, daySessions) ->
        val firstSession = daySessions.first()
        cal.timeInMillis = firstSession.startTimeEpoch
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val dayStart = cal.timeInMillis

        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val dayEnd = cal.timeInMillis

        val formattedDate = displaySdf.format(Date(dayStart))

        // Collect all segments for this day, fallback to session-level segment if empty
        val allSegments = daySessions.flatMap { session ->
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
                        durationMinutes = session.activeDurationMinutes,
                        status = PmkSegmentStatus.ACTIVE,
                        babyTemperature = session.babyTemperature,
                        babyResponse = session.babyResponse,
                        notes = session.notes
                    )
                )
            }
        }.sortedBy { it.startTimeEpoch }

        // Compute 24h timeline
        val timeline = GetDailyPmkTimelineUseCase.computeTimeline(
            dayStartEpoch = dayStart,
            dayEndEpoch = dayEnd,
            dayFormatted = formattedDate,
            segments = allSegments
        )

        DailyGroup(
            dateKey = key,
            dateFormatted = formattedDate,
            isToday = key == todayKey,
            isYesterday = key == yesterdayKey,
            dayStartEpoch = dayStart,
            dayEndEpoch = dayEnd,
            timeline = timeline,
            segments = allSegments,
            sessions = daySessions
        )
    }
}
