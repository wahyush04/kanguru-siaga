package com.kangurusiaga.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.kangurusiaga.app.data.local.dao.FeedingScheduleDao
import com.kangurusiaga.app.data.local.entity.FeedingScheduleEntity
import com.kangurusiaga.app.data.mapper.toDomain
import com.kangurusiaga.app.data.mapper.toEntity
import com.kangurusiaga.app.domain.model.FeedingMethod
import com.kangurusiaga.app.domain.model.FeedingSchedule
import com.kangurusiaga.app.domain.model.RepeatType
import com.kangurusiaga.app.domain.repository.FeedingScheduleRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedingScheduleRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: FeedingScheduleDao
) : FeedingScheduleRepository {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("feeding_schedule_prefs", Context.MODE_PRIVATE)
    }

    override fun observeSchedules(): Flow<List<FeedingSchedule>> {
        return dao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getScheduleById(id: Long): FeedingSchedule? {
        return dao.getById(id)?.toDomain()
    }

    override suspend fun getAllEnabledSchedules(): List<FeedingSchedule> {
        return dao.getAllEnabled().map { it.toDomain() }
    }

    override suspend fun addSchedule(schedule: FeedingSchedule): Long {
        return dao.insert(schedule.toEntity())
    }

    override suspend fun updateSchedule(schedule: FeedingSchedule) {
        dao.update(schedule.toEntity().copy(updatedAt = System.currentTimeMillis()))
    }

    override suspend fun updateAllVolumes(volumeMl: Int) {
        dao.updateAllVolumes(volumeMl)
    }

    override suspend fun deleteSchedule(schedule: FeedingSchedule) {
        dao.delete(schedule.toEntity())
    }

    override suspend fun deleteScheduleById(id: Long) {
        dao.deleteById(id)
    }

    override suspend fun setEnabled(id: Long, isEnabled: Boolean) {
        dao.updateEnabled(id, isEnabled, System.currentTimeMillis())
    }

    override suspend fun seedDefaultSchedulesIfEmpty() {
        val seedKey = "feeding_seeded_v1"
        if (prefs.getBoolean(seedKey, false)) return
        if (dao.count() > 0) {
            prefs.edit().putBoolean(seedKey, true).apply()
            return
        }

        val defaultSlots = listOf(
            Pair(6, 0),
            Pair(8, 0),
            Pair(10, 0),
            Pair(12, 0),
            Pair(14, 0),
            Pair(16, 0),
            Pair(18, 0),
            Pair(20, 0),
            Pair(22, 0),
            Pair(0, 0),
            Pair(2, 0),
            Pair(4, 0)
        )

        val now = System.currentTimeMillis()
        val entities = defaultSlots.mapIndexed { index, (hour, minute) ->

            FeedingScheduleEntity(
                id = 0L,
                hour = hour,
                minute = minute,
                volumeMl = 30,
                method = FeedingMethod.OGT_NGT.name,
                note = null,
                isEnabled = true,
                reminderEnabled = true,
                reminderOffsetMinutes = 0,
                repeatType = RepeatType.DAILY.name,
                createdAt = now + index,
                updatedAt = now + index
            )
        }

        dao.insertAll(entities)
        prefs.edit().putBoolean(seedKey, true).apply()
    }

    override suspend fun resetAllOffsetToZero() {
        dao.resetAllOffsetToZero()
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }
}
