package com.kangurusiaga.app.domain.repository

import com.kangurusiaga.app.domain.model.PrematureScreening
import kotlinx.coroutines.flow.Flow

interface PrematureScreeningRepository {
    fun getScreeningsForBaby(babyId: Long): Flow<List<PrematureScreening>>
    suspend fun ensureDefaultScreenings(babyId: Long, birthDateEpoch: Long, gestationalAgeWeeks: Int)
    suspend fun updateScreening(screening: PrematureScreening)
    suspend fun insertScreening(screening: PrematureScreening): Long
    suspend fun deleteScreening(screeningId: Long)
}
