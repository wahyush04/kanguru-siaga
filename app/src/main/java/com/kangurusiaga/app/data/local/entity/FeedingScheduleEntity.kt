package com.kangurusiaga.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feeding_schedules")
data class FeedingScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "hour")
    val hour: Int,
    @ColumnInfo(name = "minute")
    val minute: Int,
    @ColumnInfo(name = "volume_ml")
    val volumeMl: Int,
    @ColumnInfo(name = "method")
    val method: String,
    @ColumnInfo(name = "note")
    val note: String?,
    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean,
    @ColumnInfo(name = "reminder_enabled")
    val reminderEnabled: Boolean,
    @ColumnInfo(name = "reminder_offset_minutes")
    val reminderOffsetMinutes: Int,
    @ColumnInfo(name = "repeat_type")
    val repeatType: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
