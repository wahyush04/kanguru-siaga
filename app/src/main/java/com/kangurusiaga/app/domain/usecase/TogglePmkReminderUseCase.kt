package com.kangurusiaga.app.domain.usecase

import com.kangurusiaga.app.domain.repository.PmkReminderRepository
import javax.inject.Inject

class TogglePmkReminderUseCase @Inject constructor(
    private val pmkReminderRepository: PmkReminderRepository
) {
    suspend operator fun invoke(reminderId: Long, isEnabled: Boolean) {
        pmkReminderRepository.toggleReminder(reminderId, isEnabled)
    }
}
