package com.kangurusiaga.app.domain.usecase

import com.kangurusiaga.app.domain.model.PmkReminder
import com.kangurusiaga.app.domain.repository.PmkReminderRepository
import javax.inject.Inject

class SavePmkReminderUseCase @Inject constructor(
    private val pmkReminderRepository: PmkReminderRepository
) {
    suspend operator fun invoke(reminder: PmkReminder): Long {
        return pmkReminderRepository.saveReminder(reminder)
    }
}
