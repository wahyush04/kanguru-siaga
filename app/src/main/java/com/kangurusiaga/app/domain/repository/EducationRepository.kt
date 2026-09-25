package com.kangurusiaga.app.domain.repository

import com.kangurusiaga.app.domain.model.education.EducationModule
import kotlinx.coroutines.flow.Flow

interface EducationRepository {
    fun getModules(): Flow<List<EducationModule>>
    fun getFavoriteModules(): Flow<List<EducationModule>>
    fun getModule(moduleId: String): Flow<EducationModule?>
    suspend fun toggleBookmark(moduleId: String)
    suspend fun markCompleted(moduleId: String)
    suspend fun recordLastRead(moduleId: String)
}
