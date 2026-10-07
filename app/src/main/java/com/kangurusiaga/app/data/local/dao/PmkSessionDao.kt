package com.kangurusiaga.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.kangurusiaga.app.data.local.entity.PmkSegmentEntity
import com.kangurusiaga.app.data.local.entity.PmkSessionEntity
import com.kangurusiaga.app.data.local.entity.PmkSessionWithSegments
import kotlinx.coroutines.flow.Flow

@Dao
interface PmkSessionDao {

    @Query("SELECT * FROM pmk_sessions WHERE baby_id = :babyId ORDER BY start_time_epoch DESC")
    fun getSessionsForBaby(babyId: Long): Flow<List<PmkSessionEntity>>

    @Query("SELECT * FROM pmk_sessions WHERE baby_id = :babyId AND start_time_epoch >= :startEpoch AND start_time_epoch <= :endEpoch ORDER BY start_time_epoch DESC")
    fun getSessionsForBabyInRange(babyId: Long, startEpoch: Long, endEpoch: Long): Flow<List<PmkSessionEntity>>

    @Transaction
    @Query("SELECT * FROM pmk_sessions WHERE baby_id = :babyId ORDER BY start_time_epoch DESC")
    fun getSessionsWithSegmentsForBaby(babyId: Long): Flow<List<PmkSessionWithSegments>>

    @Transaction
    @Query("SELECT * FROM pmk_sessions WHERE baby_id = :babyId AND start_time_epoch >= :startEpoch AND start_time_epoch <= :endEpoch ORDER BY start_time_epoch DESC")
    fun getSessionsWithSegmentsForBabyInRange(babyId: Long, startEpoch: Long, endEpoch: Long): Flow<List<PmkSessionWithSegments>>

    @Query("SELECT * FROM pmk_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: Long): PmkSessionEntity?

    @Transaction
    @Query("SELECT * FROM pmk_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSessionWithSegmentsById(sessionId: Long): PmkSessionWithSegments?

    @Transaction
    @Query("SELECT * FROM pmk_sessions WHERE baby_id = :babyId AND status != 'COMPLETED' ORDER BY start_time_epoch DESC LIMIT 1")
    suspend fun getActiveSessionForBaby(babyId: Long): PmkSessionWithSegments?

    @Transaction
    @Query("SELECT * FROM pmk_sessions WHERE status != 'COMPLETED' ORDER BY start_time_epoch DESC LIMIT 1")
    suspend fun getActiveSession(): PmkSessionWithSegments?

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

    // =========================================================================
    // PMK SEGMENT QUERIES (Continuous KMC & Caregiver Handover)
    // =========================================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSegment(segment: PmkSegmentEntity): Long

    @Update
    suspend fun updateSegment(segment: PmkSegmentEntity)

    @Query("SELECT * FROM pmk_segments WHERE session_id = :sessionId ORDER BY start_time_epoch ASC")
    fun getSegmentsForSession(sessionId: Long): Flow<List<PmkSegmentEntity>>

    @Query("SELECT * FROM pmk_segments WHERE session_id = :sessionId ORDER BY start_time_epoch DESC LIMIT 1")
    suspend fun getLatestSegmentForSession(sessionId: Long): PmkSegmentEntity?

    @Query("SELECT seg.* FROM pmk_segments seg INNER JOIN pmk_sessions sess ON seg.session_id = sess.id WHERE sess.baby_id = :babyId AND ((seg.start_time_epoch <= :endEpoch AND (seg.end_time_epoch IS NULL OR seg.end_time_epoch >= :startEpoch))) ORDER BY seg.start_time_epoch ASC")
    fun getSegmentsInRange(babyId: Long, startEpoch: Long, endEpoch: Long): Flow<List<PmkSegmentEntity>>

    @Query("SELECT seg.* FROM pmk_segments seg WHERE (seg.start_time_epoch <= :endEpoch AND (seg.end_time_epoch IS NULL OR seg.end_time_epoch >= :startEpoch)) ORDER BY seg.start_time_epoch ASC")
    fun getAllSegmentsInRange(startEpoch: Long, endEpoch: Long): Flow<List<PmkSegmentEntity>>
}
