package com.kangurusiaga.app.domain.repository

import com.kangurusiaga.app.domain.model.Baby
import kotlinx.coroutines.flow.Flow

interface BabyRepository {
    fun getActiveBaby(): Flow<Baby?>
    suspend fun saveBaby(baby: Baby): Long
    suspend fun updateBaby(baby: Baby)
    suspend fun deleteBaby(baby: Baby)
}
