package com.kangurusiaga.app.data.repository

import com.kangurusiaga.app.core.datastore.UserPreferencesDataStore
import com.kangurusiaga.app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import com.kangurusiaga.app.domain.model.settings.AppSettings
import com.kangurusiaga.app.domain.model.settings.AppTextScale
import com.kangurusiaga.app.domain.model.settings.AppThemeMode
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: UserPreferencesDataStore
) : UserPreferencesRepository {

    override val isOnboardingCompleted: Flow<Boolean> = dataStore.isOnboardingCompleted
    override val hasCompletedOnboarding: Flow<Boolean> = dataStore.hasCompletedOnboarding

    override val themeMode: Flow<AppThemeMode> = dataStore.themeMode
    override val textScale: Flow<AppTextScale> = dataStore.textScale
    override val notificationsEnabled: Flow<Boolean> = dataStore.notificationsEnabled

    override val appSettings: Flow<AppSettings> = combine(
        themeMode,
        textScale,
        notificationsEnabled
    ) { theme, scale, notif ->
        AppSettings(
            themeMode = theme,
            textScale = scale,
            notificationsEnabled = notif
        )
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.setOnboardingCompleted(completed)
    }

    override suspend fun setThemeMode(mode: AppThemeMode) {
        dataStore.setThemeMode(mode)
    }

    override suspend fun setTextScale(scale: AppTextScale) {
        dataStore.setTextScale(scale)
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.setNotificationsEnabled(enabled)
    }
}
