package com.kangurusiaga.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pmk_reminders")
data class PmkReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "baby_id")
    val babyId: Long,
    @ColumnInfo(name = "time_hour")
    val timeHour: Int, // 0 - 23
    @ColumnInfo(name = "time_minute")
    val timeMinute: Int, // 0 - 59
    @ColumnInfo(name = "label")
    val label: String, // "Pagi", "Siang", "Malam"
    @ColumnInfo(name = "target_minutes")
    val targetMinutes: Int = 60,
    @ColumnInfo(name = "days_of_week_mask")
    val daysOfWeekMask: Int = 127, // Bitmask for Mon-Sun (1111111 = all days)
    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean = true,
    @ColumnInfo(name = "created_at_epoch")
    val createdAtEpoch: Long = System.currentTimeMillis()
)
