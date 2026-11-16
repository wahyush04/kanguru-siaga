package com.kangurusiaga.app.domain.repository

import com.kangurusiaga.app.domain.model.settings.AppSettings
import com.kangurusiaga.app.domain.model.settings.AppTextScale
import com.kangurusiaga.app.domain.model.settings.AppThemeMode
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val isOnboardingCompleted: Flow<Boolean>
    val hasCompletedOnboarding: Flow<Boolean>
    val themeMode: Flow<AppThemeMode>
    val textScale: Flow<AppTextScale>
    val notificationsEnabled: Flow<Boolean>
    val hasSeenAlarmGuidance: Flow<Boolean>
    val firestoreBabyDocId: Flow<String?>
    val appSettings: Flow<AppSettings>

    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun setThemeMode(mode: AppThemeMode)
    suspend fun setTextScale(scale: AppTextScale)
    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun setHasSeenAlarmGuidance(hasSeen: Boolean)
    suspend fun getOrCreateFirestoreBabyDocId(): String
    suspend fun setFirestoreBabyDocId(id: String)
}
