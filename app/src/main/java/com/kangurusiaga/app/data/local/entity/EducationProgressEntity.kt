package com.kangurusiaga.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "education_progress")
data class EducationProgressEntity(
    @PrimaryKey
    val moduleId: String,
    val isBookmarked: Boolean = false,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val lastReadAt: Long? = null
)
