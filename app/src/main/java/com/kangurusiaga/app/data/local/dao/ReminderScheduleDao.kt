package com.kangurusiaga.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kangurusiaga.app.data.local.entity.ReminderScheduleEntity

@Dao
interface ReminderScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(schedule: ReminderScheduleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAll(schedules: List<ReminderScheduleEntity>)

    @Query("SELECT * FROM reminder_schedules WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ReminderScheduleEntity?

    @Query("SELECT * FROM reminder_schedules WHERE is_enabled = 1 ORDER BY next_trigger_millis ASC")
    suspend fun getAllEnabled(): List<ReminderScheduleEntity>

    @Query("SELECT * FROM reminder_schedules WHERE is_enabled = 1 AND next_trigger_millis <= :now ORDER BY next_trigger_millis ASC")
    suspend fun getMissedReminders(now: Long): List<ReminderScheduleEntity>

    @Query("SELECT * FROM reminder_schedules WHERE is_enabled = 1 AND next_trigger_millis > :now ORDER BY next_trigger_millis ASC LIMIT 1")
    suspend fun getNextEarliestActive(now: Long): ReminderScheduleEntity?

    @Query("UPDATE reminder_schedules SET next_trigger_millis = :nextMillis, updated_at = :updatedAt WHERE id = :id")
    suspend fun updateNextTrigger(id: String, nextMillis: Long, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE reminder_schedules SET is_enabled = :enabled, updated_at = :updatedAt WHERE id = :id")
    suspend fun updateEnabled(id: String, enabled: Boolean, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM reminder_schedules WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM reminder_schedules WHERE type = :type AND reference_id = :referenceId")
    suspend fun deleteByReference(type: String, referenceId: Long)

    @Query("DELETE FROM reminder_schedules WHERE type = :type AND reference_id NOT IN (:validReferenceIds)")
    suspend fun pruneOrphanedReferences(type: String, validReferenceIds: List<Long>)

    @Query("SELECT * FROM reminder_schedules")
    suspend fun getAll(): List<ReminderScheduleEntity>

    @Query("SELECT * FROM reminder_schedules WHERE type = :type")
    suspend fun getByType(type: String): List<ReminderScheduleEntity>
}
