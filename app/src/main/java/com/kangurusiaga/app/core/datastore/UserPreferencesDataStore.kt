package com.kangurusiaga.app.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kangurusiaga.app.domain.model.settings.AppTextScale
import com.kangurusiaga.app.domain.model.settings.AppThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "kanguru_user_preferences")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object PreferencesKeys {
        val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val TEXT_SCALE = floatPreferencesKey("text_scale")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }

    val isOnboardingCompleted: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_ONBOARDING_COMPLETED] ?: false
    }

    val hasCompletedOnboarding: Flow<Boolean> = isOnboardingCompleted

    val themeMode: Flow<AppThemeMode> = dataStore.data.map { preferences ->
        AppThemeMode.fromString(preferences[PreferencesKeys.THEME_MODE])
    }

    val textScale: Flow<AppTextScale> = dataStore.data.map { preferences ->
        AppTextScale.fromScale(preferences[PreferencesKeys.TEXT_SCALE])
    }

    val notificationsEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setThemeMode(mode: AppThemeMode) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode.name
        }
    }

    suspend fun setTextScale(scale: AppTextScale) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.TEXT_SCALE] = scale.scaleFactor
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }
}
