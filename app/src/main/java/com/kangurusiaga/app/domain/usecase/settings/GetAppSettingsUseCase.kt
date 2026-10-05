package com.kangurusiaga.app.domain.usecase.settings

import com.kangurusiaga.app.domain.model.settings.AppSettings
import com.kangurusiaga.app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAppSettingsUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<AppSettings> = repository.appSettings
}
