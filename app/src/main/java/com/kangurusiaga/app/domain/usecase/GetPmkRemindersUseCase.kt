package com.kangurusiaga.app.domain.usecase

import com.kangurusiaga.app.domain.model.PmkReminder
import com.kangurusiaga.app.domain.repository.PmkReminderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPmkRemindersUseCase @Inject constructor(
    private val pmkReminderRepository: PmkReminderRepository
) {
    suspend operator fun invoke(babyId: Long): Flow<List<PmkReminder>> {
        pmkReminderRepository.seedDefaultRemindersIfEmpty(babyId)
        return pmkReminderRepository.getRemindersForBaby(babyId)
    }
}
