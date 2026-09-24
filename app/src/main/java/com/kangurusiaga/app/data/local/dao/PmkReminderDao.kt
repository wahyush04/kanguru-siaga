package com.kangurusiaga.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kangurusiaga.app.data.local.entity.PmkReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PmkReminderDao {

    @Query("SELECT * FROM pmk_reminders WHERE baby_id = :babyId ORDER BY time_hour ASC, time_minute ASC")
    fun getRemindersForBaby(babyId: Long): Flow<List<PmkReminderEntity>>

    @Query("SELECT * FROM pmk_reminders WHERE is_enabled = 1")
    suspend fun getAllEnabledReminders(): List<PmkReminderEntity>

    @Query("SELECT * FROM pmk_reminders WHERE id = :reminderId LIMIT 1")
    suspend fun getReminderById(reminderId: Long): PmkReminderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: PmkReminderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminders(reminders: List<PmkReminderEntity>): List<Long>

    @Update
    suspend fun updateReminder(reminder: PmkReminderEntity)

    @Query("UPDATE pmk_reminders SET is_enabled = :isEnabled WHERE id = :reminderId")
    suspend fun updateReminderEnabled(reminderId: Long, isEnabled: Boolean)

    @Delete
    suspend fun deleteReminder(reminder: PmkReminderEntity)

    @Query("DELETE FROM pmk_reminders WHERE id = :reminderId")
    suspend fun deleteReminderById(reminderId: Long)

    @Query("SELECT COUNT(*) FROM pmk_reminders WHERE baby_id = :babyId")
    suspend fun getReminderCount(babyId: Long): Int
}
