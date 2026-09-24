package com.kangurusiaga.app.data.repository

import com.kangurusiaga.app.core.common.AppDispatchers
import com.kangurusiaga.app.core.common.Dispatcher
import com.kangurusiaga.app.data.local.dao.PmkReminderDao
import com.kangurusiaga.app.data.local.entity.PmkReminderEntity
import com.kangurusiaga.app.data.mapper.toDomain
import com.kangurusiaga.app.data.mapper.toEntity
import com.kangurusiaga.app.domain.model.PmkReminder
import com.kangurusiaga.app.domain.repository.PmkReminderRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PmkReminderRepositoryImpl @Inject constructor(
    private val pmkReminderDao: PmkReminderDao,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : PmkReminderRepository {

    override fun getRemindersForBaby(babyId: Long): Flow<List<PmkReminder>> {
        return pmkReminderDao.getRemindersForBaby(babyId)
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override suspend fun getAllEnabledReminders(): List<PmkReminder> {
        return withContext(ioDispatcher) {
            pmkReminderDao.getAllEnabledReminders().map { it.toDomain() }
        }
    }

    override suspend fun getReminderById(reminderId: Long): PmkReminder? {
        return withContext(ioDispatcher) {
            pmkReminderDao.getReminderById(reminderId)?.toDomain()
        }
    }

    override suspend fun saveReminder(reminder: PmkReminder): Long {
        return withContext(ioDispatcher) {
            if (reminder.id == 0L) {
                pmkReminderDao.insertReminder(reminder.toEntity())
            } else {
                pmkReminderDao.updateReminder(reminder.toEntity())
                reminder.id
            }
        }
    }

    override suspend fun toggleReminder(reminderId: Long, isEnabled: Boolean) {
        withContext(ioDispatcher) {
            pmkReminderDao.updateReminderEnabled(reminderId, isEnabled)
        }
    }

    override suspend fun deleteReminder(reminderId: Long) {
        withContext(ioDispatcher) {
            pmkReminderDao.deleteReminderById(reminderId)
        }
    }

    override suspend fun seedDefaultRemindersIfEmpty(babyId: Long) {
        withContext(ioDispatcher) {
            val count = pmkReminderDao.getReminderCount(babyId)
            if (count == 0) {
                val defaults = listOf(
                    PmkReminderEntity(
                        babyId = babyId,
                        timeHour = 8,
                        timeMinute = 0,
                        label = "Pagi",
                        targetMinutes = 60,
                        isEnabled = true
                    ),
                    PmkReminderEntity(
                        babyId = babyId,
                        timeHour = 13,
                        timeMinute = 0,
                        label = "Siang",
                        targetMinutes = 60,
                        isEnabled = true
                    ),
                    PmkReminderEntity(
                        babyId = babyId,
                        timeHour = 19,
                        timeMinute = 0,
                        label = "Malam",
                        targetMinutes = 60,
                        isEnabled = false
                    )
                )
                pmkReminderDao.insertReminders(defaults)
            }
        }
    }
}
