package com.kangurusiaga.app.domain.usecase.feeding

import com.kangurusiaga.app.core.notification.FeedingReminderScheduler
import com.kangurusiaga.app.domain.repository.FeedingScheduleRepository
import javax.inject.Inject

class DeleteFeedingScheduleUseCase @Inject constructor(
    private val repository: FeedingScheduleRepository,
    private val scheduler: FeedingReminderScheduler
) {
    suspend operator fun invoke(scheduleId: Long): Result<Unit> {
        return runCatching {
            require(scheduleId > 0) { "ID jadwal tidak valid" }
            scheduler.cancel(scheduleId)
            repository.deleteScheduleById(scheduleId)
        }
    }
}
