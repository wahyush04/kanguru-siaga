package com.kangurusiaga.app.domain.usecase.settings

import com.kangurusiaga.app.domain.model.settings.AppTextScale
import com.kangurusiaga.app.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class UpdateTextScaleUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(scale: AppTextScale) {
        repository.setTextScale(scale)
    }
}
