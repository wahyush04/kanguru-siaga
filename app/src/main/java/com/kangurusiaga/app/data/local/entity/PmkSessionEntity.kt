package com.kangurusiaga.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pmk_sessions")
data class PmkSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "baby_id")
    val babyId: Long,
    @ColumnInfo(name = "start_time_epoch")
    val startTimeEpoch: Long,
    @ColumnInfo(name = "end_time_epoch")
    val endTimeEpoch: Long,
    @ColumnInfo(name = "duration_minutes")
    val durationMinutes: Int,
    @ColumnInfo(name = "target_duration_minutes")
    val targetDurationMinutes: Int = 1200, // 20 hours default continuous KMC target
    @ColumnInfo(name = "source")
    val source: String, // "TIMER" or "MANUAL"
    @ColumnInfo(name = "current_caregiver")
    val currentCaregiver: String = "IBU",
    @ColumnInfo(name = "status")
    val status: String = "COMPLETED", // "RUNNING", "PAUSED", "COMPLETED"
    @ColumnInfo(name = "active_duration_minutes")
    val activeDurationMinutes: Int = durationMinutes,
    @ColumnInfo(name = "pause_duration_minutes")
    val pauseDurationMinutes: Int = 0,
    @ColumnInfo(name = "baby_temperature")
    val babyTemperature: Double? = null,
    @ColumnInfo(name = "baby_response")
    val babyResponse: String? = null, // "Tidur Tenang", "Tenang", "Gelisah", "Menangis"
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    @ColumnInfo(name = "created_at_epoch")
    val createdAtEpoch: Long = System.currentTimeMillis()
)
