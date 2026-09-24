package com.kangurusiaga.app.domain.usecase

import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.repository.PmkRepository
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject

class GetTodayPmkSessionsUseCase @Inject constructor(
    private val pmkRepository: PmkRepository
) {
    operator fun invoke(babyId: Long): Flow<List<PmkSession>> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfToday = calendar.timeInMillis
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val endOfToday = calendar.timeInMillis

        return pmkRepository.getSessionsForBabyInRange(babyId, startOfToday, endOfToday)
    }
}
