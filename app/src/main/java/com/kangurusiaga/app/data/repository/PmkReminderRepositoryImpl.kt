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
            // Bersihkan jadwal legacy jika sebelumnya sempat tersimpan (jam 1, 8, 13, 19)
            pmkReminderDao.deleteRemindersByTime(1, 0)
            pmkReminderDao.deleteRemindersByTime(8, 0)
            pmkReminderDao.deleteRemindersByTime(13, 0)
            pmkReminderDao.deleteRemindersByTime(19, 0)

            val existing = pmkReminderDao.getRemindersListForBaby(babyId)
            val hasPagi = existing.any { it.timeHour == 6 && it.timeMinute == 0 }
            val hasSiang = existing.any { it.timeHour == 14 && it.timeMinute == 0 }
            val hasMalam = existing.any { it.timeHour == 22 && it.timeMinute == 0 }

            val toInsert = mutableListOf<PmkReminderEntity>()
            if (!hasPagi) {
                toInsert.add(
                    PmkReminderEntity(
                        babyId = babyId,
                        timeHour = 6,
                        timeMinute = 0,
                        label = "Estafet Sesi Pagi",
                        targetMinutes = 480,
                        isEnabled = true,
                        category = "Pagi",
                        notes = "Estafet Sesi Pagi bersama Ibu",
                        repeatDaily = true
                    )
                )
            }
            if (!hasSiang) {
                toInsert.add(
                    PmkReminderEntity(
                        babyId = babyId,
                        timeHour = 14,
                        timeMinute = 0,
                        label = "Estafet Sesi Siang",
                        targetMinutes = 480,
                        isEnabled = true,
                        category = "Siang",
                        notes = "Estafet Sesi Siang bersama Ayah",
                        repeatDaily = true
                    )
                )
            }
            if (!hasMalam) {
                toInsert.add(
                    PmkReminderEntity(
                        babyId = babyId,
                        timeHour = 22,
                        timeMinute = 0,
                        label = "Estafet Sesi Malam",
                        targetMinutes = 480,
                        isEnabled = true,
                        category = "Malam",
                        notes = "Estafet Sesi Malam bersama Ibu",
                        repeatDaily = true
                    )
                )
            }

            if (toInsert.isNotEmpty()) {
                pmkReminderDao.insertReminders(toInsert)
            }

            // Perbarui jika jam 6, 14, atau 22 sebelumnya sempat tersimpan dengan durasi lama (< 480 menit)
            existing.filter { it.timeHour in listOf(6, 14, 22) && it.timeMinute == 0 && it.targetMinutes != 480 }
                .forEach { legacyReminder ->
                    pmkReminderDao.updateReminder(legacyReminder.copy(targetMinutes = 480))
                }
        }
    }
}
