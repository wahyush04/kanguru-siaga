package com.kangurusiaga.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.kangurusiaga.app.core.common.AppDispatchers
import com.kangurusiaga.app.core.common.Dispatcher
import com.kangurusiaga.app.data.local.dao.PmkReminderDao
import com.kangurusiaga.app.data.local.entity.PmkReminderEntity
import com.kangurusiaga.app.data.mapper.toDomain
import com.kangurusiaga.app.data.mapper.toEntity
import com.kangurusiaga.app.domain.model.PmkReminder
import com.kangurusiaga.app.domain.repository.PmkReminderRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PmkReminderRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pmkReminderDao: PmkReminderDao,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : PmkReminderRepository {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("pmk_reminder_prefs", Context.MODE_PRIVATE)
    }

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
                pmkReminderDao.insertReminder(reminder.toEntity())
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
            val seedKey = "pmk_seeded_relay_v4_$babyId"
            if (prefs.getBoolean(seedKey, false)) {
                // Inisialisasi awal hanya dilakukan sekali saja saat fresh install / profil baru.
                // Tidak akan re-seed agar perubahan atau penghapusan jadwal oleh pengguna tersimpan permanen.
                return@withContext
            }

            // Bersihkan jadwal lama jika sebelumnya sempat tersimpan
            pmkReminderDao.deleteRemindersByTime(1, 0)
            pmkReminderDao.deleteRemindersByTime(6, 0)
            pmkReminderDao.deleteRemindersByTime(8, 0)
            pmkReminderDao.deleteRemindersByTime(13, 0)
            pmkReminderDao.deleteRemindersByTime(14, 0)
            pmkReminderDao.deleteRemindersByTime(19, 0)
            pmkReminderDao.deleteRemindersByTime(22, 0)

            val defaults = listOf(
                PmkReminderEntity(
                    babyId = babyId,
                    timeHour = 7,
                    timeMinute = 0,
                    label = "Estafet Sesi Pagi",
                    targetMinutes = 480,
                    isEnabled = true,
                    category = "Pagi",
                    notes = "Estafet Sesi Pagi bersama Ibu",
                    repeatDaily = true
                ),
                PmkReminderEntity(
                    babyId = babyId,
                    timeHour = 15,
                    timeMinute = 0,
                    label = "Estafet Sesi Siang",
                    targetMinutes = 480,
                    isEnabled = true,
                    category = "Siang",
                    notes = "Estafet Sesi Siang bersama Ayah",
                    repeatDaily = true
                ),
                PmkReminderEntity(
                    babyId = babyId,
                    timeHour = 23,
                    timeMinute = 0,
                    label = "Estafet Sesi Malam",
                    targetMinutes = 480,
                    isEnabled = true,
                    category = "Malam",
                    notes = "Estafet Sesi Malam bersama Ibu",
                    repeatDaily = true
                )
            )

            pmkReminderDao.insertReminders(defaults)

            // Tandai sudah di-seed sekali saat fresh install, sehingga tidak akan pernah di-seed ulang
            prefs.edit().putBoolean(seedKey, true).apply()
        }
    }
}
