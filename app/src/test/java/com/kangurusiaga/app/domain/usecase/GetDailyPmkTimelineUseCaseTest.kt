package com.kangurusiaga.app.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.domain.model.PmkCaregiver
import com.kangurusiaga.app.domain.model.PmkSegment
import com.kangurusiaga.app.domain.model.PmkSegmentStatus
import org.junit.Test
import java.util.Calendar

class GetDailyPmkTimelineUseCaseTest {

    @Test
    fun computeTimeline_correctlySlicesSegmentsAndCalculatesCaregiverMinutes() {
        val cal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 24, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val dayStart = cal.timeInMillis
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val dayEnd = cal.timeInMillis

        // Segment 1: Ibu (08:00 - 11:00) -> 3 hours = 180 min
        val cal1 = Calendar.getInstance().apply {
            timeInMillis = dayStart
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
        }
        val cal2 = Calendar.getInstance().apply {
            timeInMillis = dayStart
            set(Calendar.HOUR_OF_DAY, 11)
            set(Calendar.MINUTE, 0)
        }

        val seg1 = PmkSegment(
            id = 1L,
            sessionId = 1L,
            caregiver = PmkCaregiver.IBU,
            startTimeEpoch = cal1.timeInMillis,
            endTimeEpoch = cal2.timeInMillis,
            durationMinutes = 180,
            status = PmkSegmentStatus.ACTIVE
        )

        // Segment 2: Pause (11:00 - 11:30) -> 30 min
        val cal3 = Calendar.getInstance().apply {
            timeInMillis = dayStart
            set(Calendar.HOUR_OF_DAY, 11)
            set(Calendar.MINUTE, 30)
        }
        val seg2 = PmkSegment(
            id = 2L,
            sessionId = 1L,
            caregiver = PmkCaregiver.IBU,
            startTimeEpoch = cal2.timeInMillis,
            endTimeEpoch = cal3.timeInMillis,
            durationMinutes = 30,
            status = PmkSegmentStatus.PAUSED,
            pauseReason = "Menyusu Langsung"
        )

        // Segment 3: Ayah (11:30 - 15:30) -> 4 hours = 240 min
        val cal4 = Calendar.getInstance().apply {
            timeInMillis = dayStart
            set(Calendar.HOUR_OF_DAY, 15)
            set(Calendar.MINUTE, 30)
        }
        val seg3 = PmkSegment(
            id = 3L,
            sessionId = 1L,
            caregiver = PmkCaregiver.AYAH,
            startTimeEpoch = cal3.timeInMillis,
            endTimeEpoch = cal4.timeInMillis,
            durationMinutes = 240,
            status = PmkSegmentStatus.ACTIVE
        )

        val timeline = GetDailyPmkTimelineUseCase.computeTimeline(
            dayStartEpoch = dayStart,
            dayEndEpoch = dayEnd,
            dayFormatted = "Sabtu, 24 Oktober 2026",
            segments = listOf(seg1, seg2, seg3)
        )

        assertThat(timeline.blocks).hasSize(3)
        assertThat(timeline.totalActiveMinutes).isEqualTo(420) // 180 + 240 = 420 mins = 7 hours
        assertThat(timeline.totalPauseMinutes).isEqualTo(30)
        assertThat(timeline.ibuMinutes).isEqualTo(180)
        assertThat(timeline.ayahMinutes).isEqualTo(240)
        assertThat(timeline.pendampingMinutes).isEqualTo(0)

        // Verify fractions
        // 08:00 is 8/24 = 0.3333f
        assertThat(timeline.blocks[0].startFraction).isWithin(0.01f).of(8f / 24f)
        assertThat(timeline.blocks[0].endFraction).isWithin(0.01f).of(11f / 24f)
    }

    @Test
    fun computeTimeline_midnightRollover_clipsToDayBounds() {
        val cal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 24, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val dayStart = cal.timeInMillis
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val dayEnd = cal.timeInMillis

        // Segment starts yesterday at 22:00 and ends today at 04:00
        val segStart = dayStart - (2 * 3600 * 1000L) // Yesterday 22:00
        val segEnd = dayStart + (4 * 3600 * 1000L)   // Today 04:00

        val seg = PmkSegment(
            id = 1L,
            sessionId = 1L,
            caregiver = PmkCaregiver.IBU,
            startTimeEpoch = segStart,
            endTimeEpoch = segEnd,
            durationMinutes = 360,
            status = PmkSegmentStatus.ACTIVE
        )

        val timeline = GetDailyPmkTimelineUseCase.computeTimeline(
            dayStartEpoch = dayStart,
            dayEndEpoch = dayEnd,
            dayFormatted = "Sabtu, 24 Oktober 2026",
            segments = listOf(seg)
        )

        // Today's portion must be exactly 00:00 to 04:00 (4 hours = 240 minutes)
        assertThat(timeline.blocks).hasSize(1)
        assertThat(timeline.blocks[0].startFraction).isEqualTo(0f)
        assertThat(timeline.blocks[0].endFraction).isWithin(0.01f).of(4f / 24f)
        assertThat(timeline.totalActiveMinutes).isEqualTo(240)
    }

    @Test
    fun computeTimeline_shortSegments_accumulatesAccuratelyWithoutInflatingMinutes() {
        val cal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 24, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val dayStart = cal.timeInMillis
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val dayEnd = cal.timeInMillis

        // 3 short active segments: 20s, 25s, 15s (Total 60s = 1 min)
        val t0 = dayStart + (8 * 3600 * 1000L)
        val seg1 = PmkSegment(
            id = 1L,
            sessionId = 1L,
            caregiver = PmkCaregiver.IBU,
            startTimeEpoch = t0,
            endTimeEpoch = t0 + 20_000L,
            durationMinutes = 0,
            status = PmkSegmentStatus.ACTIVE
        )
        val seg2 = PmkSegment(
            id = 2L,
            sessionId = 1L,
            caregiver = PmkCaregiver.IBU,
            startTimeEpoch = t0 + 30_000L,
            endTimeEpoch = t0 + 55_000L,
            durationMinutes = 0,
            status = PmkSegmentStatus.ACTIVE
        )
        val seg3 = PmkSegment(
            id = 3L,
            sessionId = 1L,
            caregiver = PmkCaregiver.IBU,
            startTimeEpoch = t0 + 60_000L,
            endTimeEpoch = t0 + 75_000L,
            durationMinutes = 0,
            status = PmkSegmentStatus.ACTIVE
        )

        val timeline = GetDailyPmkTimelineUseCase.computeTimeline(
            dayStartEpoch = dayStart,
            dayEndEpoch = dayEnd,
            dayFormatted = "Sabtu, 24 Oktober 2026",
            segments = listOf(seg1, seg2, seg3)
        )

        // 20s + 25s + 15s = 60s = 1 minute total, NOT 3 minutes!
        assertThat(timeline.totalActiveMinutes).isEqualTo(1)
        assertThat(timeline.totalActiveSeconds).isEqualTo(60L)
    }

    @Test
    fun syncWithActiveTimer_updatesTimelineToMatchTimerActiveMinutesAndBlocks() {
        val initialTimeline = DailyTimeline(
            dayEpoch = 0L,
            dayDateFormatted = "Sabtu, 24 Oktober 2026",
            blocks = listOf(
                TimelineBlock(
                    caregiver = PmkCaregiver.IBU,
                    isPaused = false,
                    startFraction = 8f / 24f, // 08:00
                    endFraction = 11f / 24f,  // 11:00
                    durationMinutes = 180,
                    timeRangeLabel = "08:00 - 11:00"
                )
            ),
            totalActiveMinutes = 180,
            totalPauseMinutes = 0,
            ibuMinutes = 180,
            ayahMinutes = 0,
            pendampingMinutes = 0
        )

        // Active timer is at 200 minutes today (20 minutes additional)
        val timerState = com.kangurusiaga.app.domain.model.PmkTimerState(
            status = com.kangurusiaga.app.domain.model.TimerStatus.RUNNING,
            currentCaregiver = PmkCaregiver.IBU,
            elapsedSeconds = 20 * 60L,
            todayTotalActiveSeconds = 200 * 60L
        )

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 11)
            set(Calendar.MINUTE, 20)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val synced = initialTimeline.syncWithActiveTimer(timerState, cal)

        assertThat(synced.totalActiveMinutes).isEqualTo(200)
        assertThat(synced.ibuMinutes).isEqualTo(200)
        assertThat(synced.blocks.last().durationMinutes).isEqualTo(200)
    }
}

