package com.kangurusiaga.app.domain.usecase.feeding

import com.kangurusiaga.app.domain.model.FeedingSchedule
import com.kangurusiaga.app.domain.repository.FeedingScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveFeedingSchedulesUseCase @Inject constructor(
    private val repository: FeedingScheduleRepository
) {
    operator fun invoke(): Flow<List<FeedingSchedule>> {
        return repository.observeSchedules().map { list ->
            list.sortedBy { it.cycleSortKey }
        }
    }
}
