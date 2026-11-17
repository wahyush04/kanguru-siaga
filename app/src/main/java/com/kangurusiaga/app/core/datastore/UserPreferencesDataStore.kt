package com.kangurusiaga.app.core.datastore

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kangurusiaga.app.domain.model.settings.AppTextScale
import com.kangurusiaga.app.domain.model.settings.AppThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "kanguru_user_preferences")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>
) {
    private object PreferencesKeys {
        val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val TEXT_SCALE = floatPreferencesKey("text_scale")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val HAS_SEEN_ALARM_GUIDANCE = booleanPreferencesKey("has_seen_alarm_guidance")
        val FIRESTORE_BABY_DOC_ID = stringPreferencesKey("firestore_baby_doc_id")
    }

    val isOnboardingCompleted: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_ONBOARDING_COMPLETED] ?: false
    }

    val hasCompletedOnboarding: Flow<Boolean> = isOnboardingCompleted

    val firestoreBabyDocId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.FIRESTORE_BABY_DOC_ID]
    }

    val themeMode: Flow<AppThemeMode> = dataStore.data.map { preferences ->
        AppThemeMode.fromString(preferences[PreferencesKeys.THEME_MODE])
    }

    val textScale: Flow<AppTextScale> = dataStore.data.map { preferences ->
        AppTextScale.fromScale(preferences[PreferencesKeys.TEXT_SCALE])
    }

    val notificationsEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
    }

    val hasSeenAlarmGuidance: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.HAS_SEEN_ALARM_GUIDANCE] ?: false
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

    suspend fun setHasSeenAlarmGuidance(hasSeen: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_SEEN_ALARM_GUIDANCE] = hasSeen
        }
    }

    @SuppressLint("HardwareIds")
    suspend fun getOrCreateFirestoreBabyDocId(): String {
        var currentId = dataStore.data.firstOrNull()?.get(PreferencesKeys.FIRESTORE_BABY_DOC_ID)
        if (currentId.isNullOrBlank()) {
            val androidId = context?.let {
                try {
                    Settings.Secure.getString(it.contentResolver, Settings.Secure.ANDROID_ID)
                } catch (_: Exception) {
                    null
                }
            }
            val newId = if (!androidId.isNullOrBlank()) {
                "user_$androidId"
            } else {
                "user_${java.util.UUID.randomUUID()}"
            }
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.FIRESTORE_BABY_DOC_ID] = newId
            }
            currentId = newId
        }
        return currentId
    }

    suspend fun setFirestoreBabyDocId(id: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.FIRESTORE_BABY_DOC_ID] = id
        }
    }
}
