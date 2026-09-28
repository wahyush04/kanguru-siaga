package com.kangurusiaga.app.domain.usecase.feeding

import com.kangurusiaga.app.core.notification.FeedingReminderScheduler
import com.kangurusiaga.app.domain.model.FeedingSchedule
import com.kangurusiaga.app.domain.repository.FeedingScheduleRepository
import javax.inject.Inject

class UpdateFeedingScheduleUseCase @Inject constructor(
    private val repository: FeedingScheduleRepository,
    private val scheduler: FeedingReminderScheduler
) {
    suspend operator fun invoke(schedule: FeedingSchedule): Result<Unit> {
        return runCatching {
            require(schedule.id > 0) { "ID jadwal tidak valid" }
            require(schedule.hour in 0..23) { "Format jam tidak valid (0-23)" }
            require(schedule.minute in 0..59) { "Format menit tidak valid (0-59)" }
            require(schedule.volumeMl > 0) { "Jumlah ASI harus lebih dari 0 ml" }

            repository.updateSchedule(schedule)
            scheduler.reschedule(schedule)
        }
    }
}
