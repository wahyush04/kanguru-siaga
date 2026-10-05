package com.kangurusiaga.app.domain.usecase.settings

import com.kangurusiaga.app.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class ToggleNotificationsUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        repository.setNotificationsEnabled(enabled)
    }
}
