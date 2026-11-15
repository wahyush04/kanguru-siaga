package com.kangurusiaga.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kangurusiaga.app.data.local.entity.PrematureScreeningEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrematureScreeningDao {

    @Query("SELECT * FROM premature_screenings WHERE baby_id = :babyId ORDER BY id ASC")
    fun getScreeningsForBaby(babyId: Long): Flow<List<PrematureScreeningEntity>>

    @Query("SELECT * FROM premature_screenings WHERE id = :screeningId LIMIT 1")
    suspend fun getScreeningById(screeningId: Long): PrematureScreeningEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScreening(screening: PrematureScreeningEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScreenings(screenings: List<PrematureScreeningEntity>): List<Long>

    @Update
    suspend fun updateScreening(screening: PrematureScreeningEntity)

    @Query("SELECT * FROM premature_screenings WHERE is_completed = 0")
    suspend fun getAllIncomplete(): List<PrematureScreeningEntity>

    @Query("SELECT * FROM premature_screenings WHERE baby_id = :babyId ORDER BY id ASC")
    suspend fun getScreeningsListForBaby(babyId: Long): List<PrematureScreeningEntity>

    @Query("UPDATE premature_screenings SET is_alarm_active = :isActive WHERE id = :screeningId")
    suspend fun updateAlarmActive(screeningId: Long, isActive: Boolean)

    @Query("UPDATE premature_screenings SET is_alarm_2_active = :isActive WHERE id = :screeningId")
    suspend fun updateAlarm2Active(screeningId: Long, isActive: Boolean)

    @Query("DELETE FROM premature_screenings WHERE id = :screeningId")
    suspend fun deleteScreeningById(screeningId: Long)
}
