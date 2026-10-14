package com.kangurusiaga.app.domain.usecase

import com.kangurusiaga.app.domain.model.PmkCaregiver
import com.kangurusiaga.app.domain.model.PmkSegment
import com.kangurusiaga.app.domain.model.PmkSegmentStatus
import com.kangurusiaga.app.domain.model.PmkTimerState
import com.kangurusiaga.app.domain.repository.PmkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

data class TimelineBlock(
    val caregiver: PmkCaregiver,
    val isPaused: Boolean,
    val pauseReason: String? = null,
    val startFraction: Float, // 0.0f at 00:00, 1.0f at 24:00
    val endFraction: Float,
    val durationMinutes: Int,
    val timeRangeLabel: String
)

data class DailyTimeline(
    val dayEpoch: Long,
    val dayDateFormatted: String,
    val blocks: List<TimelineBlock>,
    val totalActiveMinutes: Int,
    val totalPauseMinutes: Int,
    val ibuMinutes: Int,
    val ayahMinutes: Int,
    val pendampingMinutes: Int,
    val totalActiveSeconds: Long = totalActiveMinutes * 60L
) {
    val totalActiveHours: Float get() = totalActiveMinutes / 60f
    val isTargetMet: Boolean get() = totalActiveMinutes >= (18 * 60)

    fun syncWithActiveTimer(
        timer: PmkTimerState,
        nowCalendar: Calendar = Calendar.getInstance()
    ): DailyTimeline {
        if (!timer.isActive) return this

        val totalDayMs = 24 * 3600 * 1000f
        val nowMsInDay = (nowCalendar.get(Calendar.HOUR_OF_DAY) * 3600 +
                nowCalendar.get(Calendar.MINUTE) * 60 +
                nowCalendar.get(Calendar.SECOND)) * 1000L + nowCalendar.get(Calendar.MILLISECOND)
        val currentFraction = (nowMsInDay / totalDayMs).coerceIn(0f, 1f)

        val realtimeActiveMinutes = (timer.todayTotalActiveSeconds / 60).toInt()
        val activeDiff = max(0, realtimeActiveMinutes - totalActiveMinutes)

        val timeSdf = SimpleDateFormat("HH:mm", Locale("id", "ID"))
        val nowFormatted = timeSdf.format(nowCalendar.time)

        val updatedBlocks = if (blocks.isNotEmpty()) {
            val last = blocks.last()
            val isLastMatchingState = (timer.isRunning && !last.isPaused) || (timer.isPaused && last.isPaused)
            if (isLastMatchingState && currentFraction >= last.startFraction) {
                val updatedDuration = ((currentFraction - last.startFraction) * 24 * 60).roundToInt().coerceAtLeast(0)
                val updatedLast = last.copy(
                    endFraction = currentFraction,
                    durationMinutes = updatedDuration,
                    timeRangeLabel = if (last.timeRangeLabel.contains(" - ")) {
                        val startLabel = last.timeRangeLabel.substringBefore(" - ")
                        "$startLabel - $nowFormatted"
                    } else last.timeRangeLabel
                )
                blocks.dropLast(1) + updatedLast
            } else if (currentFraction >= last.endFraction) {
                val newBlock = TimelineBlock(
                    caregiver = timer.currentCaregiver,
                    isPaused = timer.isPaused,
                    pauseReason = timer.pauseReason,
                    startFraction = last.endFraction,
                    endFraction = currentFraction,
                    durationMinutes = ((currentFraction - last.endFraction) * 24 * 60).roundToInt().coerceAtLeast(0),
                    timeRangeLabel = "$nowFormatted - $nowFormatted"
                )
                blocks + newBlock
            } else {
                blocks
            }
        } else {
            if (timer.sessionStartEpoch > 0L) {
                val startCal = Calendar.getInstance().apply { timeInMillis = timer.sessionStartEpoch }
                val startMsInDay = (startCal.get(Calendar.HOUR_OF_DAY) * 3600 +
                        startCal.get(Calendar.MINUTE) * 60 +
                        startCal.get(Calendar.SECOND)) * 1000L + startCal.get(Calendar.MILLISECOND)
                val startFraction = (startMsInDay / totalDayMs).coerceIn(0f, 1f)
                val initialBlock = TimelineBlock(
                    caregiver = timer.currentCaregiver,
                    isPaused = timer.isPaused,
                    pauseReason = timer.pauseReason,
                    startFraction = startFraction,
                    endFraction = currentFraction,
                    durationMinutes = realtimeActiveMinutes,
                    timeRangeLabel = "${timeSdf.format(startCal.time)} - $nowFormatted"
                )
                listOf(initialBlock)
            } else {
                blocks
            }
        }

        val newIbu = if (timer.isRunning && timer.currentCaregiver == PmkCaregiver.IBU) ibuMinutes + activeDiff else ibuMinutes
        val newAyah = if (timer.isRunning && timer.currentCaregiver == PmkCaregiver.AYAH) ayahMinutes + activeDiff else ayahMinutes
        val newPendamping = if (timer.isRunning && timer.currentCaregiver == PmkCaregiver.PENDAMPING) pendampingMinutes + activeDiff else pendampingMinutes
        val newPauseMinutes = if (timer.isPaused) totalPauseMinutes + (timer.pauseElapsedSeconds / 60).toInt() else totalPauseMinutes

        return copy(
            blocks = updatedBlocks,
            totalActiveMinutes = max(totalActiveMinutes, realtimeActiveMinutes),
            totalPauseMinutes = newPauseMinutes,
            ibuMinutes = newIbu,
            ayahMinutes = newAyah,
            pendampingMinutes = newPendamping,
            totalActiveSeconds = max(totalActiveSeconds, timer.todayTotalActiveSeconds)
        )
    }
}

class GetDailyPmkTimelineUseCase @Inject constructor(
    private val pmkRepository: PmkRepository
) {
    operator fun invoke(babyId: Long, targetDayCalendar: Calendar = Calendar.getInstance()): Flow<DailyTimeline> {
        val copy = targetDayCalendar.clone() as Calendar
        copy.set(Calendar.HOUR_OF_DAY, 0)
        copy.set(Calendar.MINUTE, 0)
        copy.set(Calendar.SECOND, 0)
        copy.set(Calendar.MILLISECOND, 0)
        val dayStartEpoch = copy.timeInMillis

        copy.set(Calendar.HOUR_OF_DAY, 23)
        copy.set(Calendar.MINUTE, 59)
        copy.set(Calendar.SECOND, 59)
        copy.set(Calendar.MILLISECOND, 999)
        val dayEndEpoch = copy.timeInMillis

        val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        val dayFormatted = sdf.format(dayStartEpoch)

        return pmkRepository.getSegmentsInRange(babyId, dayStartEpoch, dayEndEpoch)
            .map { segments ->
                computeTimeline(dayStartEpoch, dayEndEpoch, dayFormatted, segments)
            }
    }

    companion object {
        fun computeTimeline(
            dayStartEpoch: Long,
            dayEndEpoch: Long,
            dayFormatted: String,
            segments: List<PmkSegment>,
            nowEpoch: Long = System.currentTimeMillis()
        ): DailyTimeline {
            val totalDayMs = 24 * 3600 * 1000f
            val timeSdf = SimpleDateFormat("HH:mm", Locale("id", "ID"))
            val blocks = mutableListOf<TimelineBlock>()

            var totalActiveMs = 0L
            var totalPauseMs = 0L
            var ibuMs = 0L
            var ayahMs = 0L
            var pendampingMs = 0L

            for (seg in segments) {
                val segStart = seg.startTimeEpoch
                val segEnd = seg.endTimeEpoch ?: nowEpoch

                // Slice segment to bounds of the day
                val effectiveStart = max(dayStartEpoch, segStart)
                val effectiveEnd = min(dayEndEpoch, segEnd)

                if (effectiveEnd > effectiveStart) {
                    val durationMs = effectiveEnd - effectiveStart
                    val startFraction = ((effectiveStart - dayStartEpoch) / totalDayMs).coerceIn(0f, 1f)
                    val endFraction = ((effectiveEnd - dayStartEpoch) / totalDayMs).coerceIn(0f, 1f)
                    val durationMin = (durationMs / 60000L).toInt()

                    val label = "${timeSdf.format(effectiveStart)} - ${timeSdf.format(effectiveEnd)}"

                    blocks.add(
                        TimelineBlock(
                            caregiver = seg.caregiver,
                            isPaused = seg.status == PmkSegmentStatus.PAUSED,
                            pauseReason = seg.pauseReason,
                            startFraction = startFraction,
                            endFraction = endFraction,
                            durationMinutes = durationMin,
                            timeRangeLabel = label
                        )
                    )

                    if (seg.status == PmkSegmentStatus.ACTIVE) {
                        totalActiveMs += durationMs
                        when (seg.caregiver) {
                            PmkCaregiver.IBU -> ibuMs += durationMs
                            PmkCaregiver.AYAH -> ayahMs += durationMs
                            PmkCaregiver.PENDAMPING -> pendampingMs += durationMs
                        }
                    } else {
                        totalPauseMs += durationMs
                    }
                }
            }

            val totalActiveSec = totalActiveMs / 1000L
            val totalActiveMin = (totalActiveMs / 60000L).toInt()
            val totalPauseMin = (totalPauseMs / 60000L).toInt()
            val ibuMin = (ibuMs / 60000L).toInt()
            val ayahMin = (ayahMs / 60000L).toInt()
            val pendampingMin = (pendampingMs / 60000L).toInt()

            if (blocks.isEmpty()) {
                return DailyTimeline(
                    dayEpoch = dayStartEpoch,
                    dayDateFormatted = dayFormatted,
                    blocks = emptyList(),
                    totalActiveMinutes = 0,
                    totalPauseMinutes = 0,
                    ibuMinutes = 0,
                    ayahMinutes = 0,
                    pendampingMinutes = 0,
                    totalActiveSeconds = 0L
                )
            }

            return DailyTimeline(
                dayEpoch = dayStartEpoch,
                dayDateFormatted = dayFormatted,
                blocks = blocks,
                totalActiveMinutes = totalActiveMin,
                totalPauseMinutes = totalPauseMin,
                ibuMinutes = ibuMin,
                ayahMinutes = ayahMin,
                pendampingMinutes = pendampingMin,
                totalActiveSeconds = totalActiveSec
            )
        }
    }
}
