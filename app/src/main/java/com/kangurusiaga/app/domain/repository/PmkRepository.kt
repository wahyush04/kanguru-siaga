package com.kangurusiaga.app.domain.repository

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
}
