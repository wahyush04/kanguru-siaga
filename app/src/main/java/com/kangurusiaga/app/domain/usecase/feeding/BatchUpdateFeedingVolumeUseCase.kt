package com.kangurusiaga.app.domain.usecase.feeding

import com.kangurusiaga.app.core.notification.FeedingReminderScheduler
import com.kangurusiaga.app.domain.repository.FeedingScheduleRepository
import javax.inject.Inject

class BatchUpdateFeedingVolumeUseCase @Inject constructor(
    private val repository: FeedingScheduleRepository,
    private val scheduler: FeedingReminderScheduler
) {
    suspend operator fun invoke(volumeMl: Int): Result<Unit> {
        return runCatching {
            require(volumeMl in 5..150) { "Volume ASI harus antara 5 ml dan 150 ml" }
            repository.updateAllVolumes(volumeMl)
            scheduler.rescheduleAllEnabled()
        }
    }
}
