package com.kangurusiaga.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pmk_segments",
    foreignKeys = [
        ForeignKey(
            entity = PmkSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["session_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("session_id"),
        Index("start_time_epoch")
    ]
)
data class PmkSegmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "session_id")
    val sessionId: Long,
    @ColumnInfo(name = "caregiver")
    val caregiver: String, // "IBU", "AYAH", "PENDAMPING"
    @ColumnInfo(name = "start_time_epoch")
    val startTimeEpoch: Long,
    @ColumnInfo(name = "end_time_epoch")
    val endTimeEpoch: Long? = null,
    @ColumnInfo(name = "duration_minutes")
    val durationMinutes: Int = 0,
    @ColumnInfo(name = "status")
    val status: String = "ACTIVE", // "ACTIVE", "PAUSED"
    @ColumnInfo(name = "pause_reason")
    val pauseReason: String? = null,
    @ColumnInfo(name = "baby_temperature")
    val babyTemperature: Double? = null,
    @ColumnInfo(name = "baby_response")
    val babyResponse: String? = null,
    @ColumnInfo(name = "notes")
    val notes: String? = null
)
