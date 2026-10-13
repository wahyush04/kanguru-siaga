package com.kangurusiaga.app.domain.usecase

import com.kangurusiaga.app.domain.model.PmkReminder
import com.kangurusiaga.app.domain.repository.PmkReminderRepository
import javax.inject.Inject

class GetPmkReminderByIdUseCase @Inject constructor(
    private val pmkReminderRepository: PmkReminderRepository
) {
    suspend operator fun invoke(reminderId: Long): PmkReminder? {
        return pmkReminderRepository.getReminderById(reminderId)
    }
}
