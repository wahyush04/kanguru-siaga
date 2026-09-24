package com.kangurusiaga.app.domain.usecase

import com.kangurusiaga.app.domain.repository.PmkReminderRepository
import javax.inject.Inject

class DeletePmkReminderUseCase @Inject constructor(
    private val pmkReminderRepository: PmkReminderRepository
) {
    suspend operator fun invoke(reminderId: Long) {
        pmkReminderRepository.deleteReminder(reminderId)
    }
}
