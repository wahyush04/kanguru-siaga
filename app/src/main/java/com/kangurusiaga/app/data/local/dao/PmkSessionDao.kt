package com.kangurusiaga.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kangurusiaga.app.data.local.entity.PmkSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PmkSessionDao {

    @Query("SELECT * FROM pmk_sessions WHERE baby_id = :babyId ORDER BY start_time_epoch DESC")
    fun getSessionsForBaby(babyId: Long): Flow<List<PmkSessionEntity>>

    @Query("SELECT * FROM pmk_sessions WHERE baby_id = :babyId AND start_time_epoch >= :startEpoch AND start_time_epoch <= :endEpoch ORDER BY start_time_epoch DESC")
    fun getSessionsForBabyInRange(babyId: Long, startEpoch: Long, endEpoch: Long): Flow<List<PmkSessionEntity>>

    @Query("SELECT * FROM pmk_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: Long): PmkSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PmkSessionEntity): Long

    @Update
    suspend fun updateSession(session: PmkSessionEntity)

    @Delete
    suspend fun deleteSession(session: PmkSessionEntity)

    @Query("DELETE FROM pmk_sessions WHERE id = :sessionId")
    suspend fun deleteSessionById(sessionId: Long)

    @Query("SELECT COUNT(*) FROM pmk_sessions WHERE baby_id = :babyId AND start_time_epoch >= :startEpoch AND start_time_epoch <= :endEpoch")
    fun getSessionCountInRange(babyId: Long, startEpoch: Long, endEpoch: Long): Flow<Int>

    @Query("SELECT SUM(duration_minutes) FROM pmk_sessions WHERE baby_id = :babyId AND start_time_epoch >= :startEpoch AND start_time_epoch <= :endEpoch")
    fun getTotalMinutesInRange(babyId: Long, startEpoch: Long, endEpoch: Long): Flow<Int?>
}
