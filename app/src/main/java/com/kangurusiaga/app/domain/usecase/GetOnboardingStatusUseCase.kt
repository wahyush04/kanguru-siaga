package com.kangurusiaga.app.domain.usecase

import com.kangurusiaga.app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOnboardingStatusUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return userPreferencesRepository.hasCompletedOnboarding
    }
}
