package com.kangurusiaga.app.data.repository

import com.kangurusiaga.app.data.local.EducationContentDataSource
import com.kangurusiaga.app.data.local.dao.EducationDao
import com.kangurusiaga.app.data.local.entity.EducationProgressEntity
import com.kangurusiaga.app.domain.model.education.EducationModule
import com.kangurusiaga.app.domain.repository.EducationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EducationRepositoryImpl @Inject constructor(
    private val contentDataSource: EducationContentDataSource,
    private val educationDao: EducationDao
) : EducationRepository {

    override fun getModules(): Flow<List<EducationModule>> {
        val staticModules = contentDataSource.getModules()
        return educationDao.getAllProgress().map { progressList ->
            val progressMap = progressList.associateBy { it.moduleId }
            staticModules.map { module ->
                val progress = progressMap[module.id]
                module.copy(
                    isBookmarked = progress?.isBookmarked ?: false,
                    isCompleted = progress?.isCompleted ?: false,
                    completedAt = progress?.completedAt,
                    lastReadAt = progress?.lastReadAt
                )
            }.sortedBy { it.order }
        }
    }

    override fun getFavoriteModules(): Flow<List<EducationModule>> {
        return getModules().map { list ->
            list.filter { it.isBookmarked }
        }
    }

    override fun getModule(moduleId: String): Flow<EducationModule?> {
        val baseModule = contentDataSource.getModuleById(moduleId)
        return educationDao.getProgressByModuleId(moduleId).map { progress ->
            baseModule?.copy(
                isBookmarked = progress?.isBookmarked ?: false,
                isCompleted = progress?.isCompleted ?: false,
                completedAt = progress?.completedAt,
                lastReadAt = progress?.lastReadAt
            )
        }
    }

    override suspend fun toggleBookmark(moduleId: String) {
        val existing = educationDao.getProgressByModuleIdOnce(moduleId)
        if (existing == null) {
            educationDao.upsertProgress(
                EducationProgressEntity(
                    moduleId = moduleId,
                    isBookmarked = true
                )
            )
        } else {
            educationDao.updateBookmark(moduleId, !existing.isBookmarked)
        }
    }

    override suspend fun markCompleted(moduleId: String) {
        val existing = educationDao.getProgressByModuleIdOnce(moduleId)
        val now = System.currentTimeMillis()
        if (existing == null) {
            educationDao.upsertProgress(
                EducationProgressEntity(
                    moduleId = moduleId,
                    isCompleted = true,
                    completedAt = now
                )
            )
        } else {
            val newCompleted = !existing.isCompleted
            educationDao.updateCompletion(
                moduleId = moduleId,
                isCompleted = newCompleted,
                completedAt = if (newCompleted) now else null
            )
        }
    }

    override suspend fun recordLastRead(moduleId: String) {
        val existing = educationDao.getProgressByModuleIdOnce(moduleId)
        val now = System.currentTimeMillis()
        if (existing == null) {
            educationDao.upsertProgress(
                EducationProgressEntity(
                    moduleId = moduleId,
                    lastReadAt = now
                )
            )
        } else {
            educationDao.updateLastRead(moduleId, now)
        }
    }
}
