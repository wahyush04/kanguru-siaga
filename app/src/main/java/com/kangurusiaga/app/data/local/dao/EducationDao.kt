package com.kangurusiaga.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kangurusiaga.app.data.local.entity.EducationProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EducationDao {
    @Query("SELECT * FROM education_progress")
    fun getAllProgress(): Flow<List<EducationProgressEntity>>

    @Query("SELECT * FROM education_progress WHERE moduleId = :moduleId")
    fun getProgressByModuleId(moduleId: String): Flow<EducationProgressEntity?>

    @Query("SELECT * FROM education_progress WHERE moduleId = :moduleId")
    suspend fun getProgressByModuleIdOnce(moduleId: String): EducationProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: EducationProgressEntity)

    @Query("UPDATE education_progress SET isBookmarked = :isBookmarked WHERE moduleId = :moduleId")
    suspend fun updateBookmark(moduleId: String, isBookmarked: Boolean)

    @Query("UPDATE education_progress SET isCompleted = :isCompleted, completedAt = :completedAt WHERE moduleId = :moduleId")
    suspend fun updateCompletion(moduleId: String, isCompleted: Boolean, completedAt: Long?)

    @Query("UPDATE education_progress SET lastReadAt = :lastReadAt WHERE moduleId = :moduleId")
    suspend fun updateLastRead(moduleId: String, lastReadAt: Long)
}
