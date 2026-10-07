package com.kangurusiaga.app.domain.repository

import com.kangurusiaga.app.domain.model.PmkSegment
import com.kangurusiaga.app.domain.model.PmkSession
import kotlinx.coroutines.flow.Flow

interface PmkRepository {
    fun getSessionsForBaby(babyId: Long): Flow<List<PmkSession>>
    fun getSessionsForBabyInRange(babyId: Long, startEpoch: Long, endEpoch: Long): Flow<List<PmkSession>>
    suspend fun getSessionById(sessionId: Long): PmkSession?
    suspend fun saveSession(session: PmkSession): Long
    suspend fun deleteSession(sessionId: Long)
    fun getSessionCountInRange(babyId: Long, startEpoch: Long, endEpoch: Long): Flow<Int>
    fun getTotalMinutesInRange(babyId: Long, startEpoch: Long, endEpoch: Long): Flow<Int>

    // Continuous KMC Segments & Active Session
    suspend fun getActiveSession(babyId: Long): PmkSession?
    suspend fun saveSegment(segment: PmkSegment): Long
    suspend fun updateSegment(segment: PmkSegment)
    fun getSegmentsInRange(babyId: Long, startEpoch: Long, endEpoch: Long): Flow<List<PmkSegment>>
    fun getAllSegmentsInRange(startEpoch: Long, endEpoch: Long): Flow<List<PmkSegment>>
}
