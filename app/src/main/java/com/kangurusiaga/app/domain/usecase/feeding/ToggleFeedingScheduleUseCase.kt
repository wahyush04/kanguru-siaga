package com.kangurusiaga.app.domain.usecase.feeding

import com.kangurusiaga.app.core.notification.FeedingReminderScheduler
import com.kangurusiaga.app.domain.repository.FeedingScheduleRepository
import javax.inject.Inject

class ToggleFeedingScheduleUseCase @Inject constructor(
    private val repository: FeedingScheduleRepository,
    private val scheduler: FeedingReminderScheduler
) {
    suspend operator fun invoke(scheduleId: Long, isEnabled: Boolean): Result<Unit> {
        return runCatching {
            repository.setEnabled(scheduleId, isEnabled)
            val schedule = repository.getScheduleById(scheduleId)
            if (schedule != null) {
                if (isEnabled && schedule.reminderEnabled) {
                    scheduler.schedule(schedule.copy(isEnabled = true))
                } else {
                    scheduler.cancel(scheduleId)
                }
            }
        }
    }
}
