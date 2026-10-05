package com.kangurusiaga.app.domain.usecase.settings

import com.kangurusiaga.app.domain.model.settings.AppThemeMode
import com.kangurusiaga.app.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class UpdateAppThemeUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(mode: AppThemeMode) {
        repository.setThemeMode(mode)
    }
}
