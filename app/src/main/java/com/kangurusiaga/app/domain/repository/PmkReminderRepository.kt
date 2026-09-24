package com.kangurusiaga.app.domain.repository

import com.kangurusiaga.app.domain.model.PmkReminder
import kotlinx.coroutines.flow.Flow

interface PmkReminderRepository {
    fun getRemindersForBaby(babyId: Long): Flow<List<PmkReminder>>
    suspend fun getAllEnabledReminders(): List<PmkReminder>
    suspend fun getReminderById(reminderId: Long): PmkReminder?
    suspend fun saveReminder(reminder: PmkReminder): Long
    suspend fun toggleReminder(reminderId: Long, isEnabled: Boolean)
    suspend fun deleteReminder(reminderId: Long)
    suspend fun seedDefaultRemindersIfEmpty(babyId: Long)
}
