package com.kangurusiaga.app.data.repository

import com.kangurusiaga.app.core.datastore.UserPreferencesDataStore
import com.kangurusiaga.app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: UserPreferencesDataStore
) : UserPreferencesRepository {

    override val isOnboardingCompleted: Flow<Boolean> = dataStore.isOnboardingCompleted

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.setOnboardingCompleted(completed)
    }
}
