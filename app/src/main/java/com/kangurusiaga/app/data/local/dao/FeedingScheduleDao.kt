package com.kangurusiaga.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kangurusiaga.app.data.local.entity.FeedingScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedingScheduleDao {

    @Query("SELECT * FROM feeding_schedules ORDER BY hour ASC, minute ASC")
    fun observeAll(): Flow<List<FeedingScheduleEntity>>

    @Query("SELECT * FROM feeding_schedules WHERE id = :id")
    suspend fun getById(id: Long): FeedingScheduleEntity?

    @Query("SELECT * FROM feeding_schedules WHERE is_enabled = 1")
    suspend fun getAllEnabled(): List<FeedingScheduleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FeedingScheduleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<FeedingScheduleEntity>)

    @Update
    suspend fun update(entity: FeedingScheduleEntity)

    @Delete
    suspend fun delete(entity: FeedingScheduleEntity)

    @Query("DELETE FROM feeding_schedules WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE feeding_schedules SET is_enabled = :isEnabled, updated_at = :updatedAt WHERE id = :id")
    suspend fun updateEnabled(id: Long, isEnabled: Boolean, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM feeding_schedules")
    suspend fun count(): Int

    @Query("UPDATE feeding_schedules SET volume_ml = :volumeMl, updated_at = :updatedAt")
    suspend fun updateAllVolumes(volumeMl: Int, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE feeding_schedules SET reminder_offset_minutes = 0 WHERE reminder_offset_minutes != 0")
    suspend fun resetAllOffsetToZero()

    @Query("DELETE FROM feeding_schedules")
    suspend fun deleteAll()
}
