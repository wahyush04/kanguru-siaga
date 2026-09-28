package com.kangurusiaga.app.domain.repository

import com.kangurusiaga.app.domain.model.FeedingSchedule
import kotlinx.coroutines.flow.Flow

interface FeedingScheduleRepository {
    fun observeSchedules(): Flow<List<FeedingSchedule>>
    suspend fun getScheduleById(id: Long): FeedingSchedule?
    suspend fun getAllEnabledSchedules(): List<FeedingSchedule>
    suspend fun addSchedule(schedule: FeedingSchedule): Long
    suspend fun updateSchedule(schedule: FeedingSchedule)
    suspend fun deleteSchedule(schedule: FeedingSchedule)
    suspend fun deleteScheduleById(id: Long)
    suspend fun setEnabled(id: Long, isEnabled: Boolean)
    suspend fun seedDefaultSchedulesIfEmpty()
    suspend fun deleteAll()
}
