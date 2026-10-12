package com.kangurusiaga.app.domain.usecase

import com.kangurusiaga.app.domain.model.PmkCaregiver
import com.kangurusiaga.app.domain.model.PmkSegment
import com.kangurusiaga.app.domain.model.PmkSegmentStatus
import com.kangurusiaga.app.domain.repository.PmkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

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
    val pendampingMinutes: Int
) {
    val totalActiveHours: Float get() = totalActiveMinutes / 60f
    val isTargetMet: Boolean get() = totalActiveMinutes >= (18 * 60)
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

            var totalActiveMin = 0
            var totalPauseMin = 0
            var ibuMin = 0
            var ayahMin = 0
            var pendampingMin = 0

            for (seg in segments) {
                val segStart = seg.startTimeEpoch
                val segEnd = seg.endTimeEpoch ?: nowEpoch

                // Slice segment to bounds of the day
                val effectiveStart = max(dayStartEpoch, segStart)
                val effectiveEnd = min(dayEndEpoch, segEnd)

                if (effectiveEnd > effectiveStart) {
                    val startFraction = ((effectiveStart - dayStartEpoch) / totalDayMs).coerceIn(0f, 1f)
                    val endFraction = ((effectiveEnd - dayStartEpoch) / totalDayMs).coerceIn(0f, 1f)
                    val durationMin = ((effectiveEnd - effectiveStart) / 60000L).toInt().coerceAtLeast(1)

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
                        totalActiveMin += durationMin
                        when (seg.caregiver) {
                            PmkCaregiver.IBU -> ibuMin += durationMin
                            PmkCaregiver.AYAH -> ayahMin += durationMin
                            PmkCaregiver.PENDAMPING -> pendampingMin += durationMin
                        }
                    } else {
                        totalPauseMin += durationMin
                    }
                }
            }

            if (blocks.isEmpty()) {
                return DailyTimeline(
                    dayEpoch = dayStartEpoch,
                    dayDateFormatted = dayFormatted,
                    blocks = emptyList(),
                    totalActiveMinutes = 0,
                    totalPauseMinutes = 0,
                    ibuMinutes = 0,
                    ayahMinutes = 0,
                    pendampingMinutes = 0
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
                pendampingMinutes = pendampingMin
            )
        }
    }
}
