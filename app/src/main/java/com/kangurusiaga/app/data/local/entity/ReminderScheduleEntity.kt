package com.kangurusiaga.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminder_schedules",
    indices = [
        Index(value = ["is_enabled", "next_trigger_millis"]),
        Index(value = ["type", "reference_id"])
    ]
)
data class ReminderScheduleEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "reference_id")
    val referenceId: Long,

    @ColumnInfo(name = "sub_index")
    val subIndex: Int = 0,

    @ColumnInfo(name = "label")
    val label: String,

    @ColumnInfo(name = "time_hour")
    val timeHour: Int,

    @ColumnInfo(name = "time_minute")
    val timeMinute: Int,

    @ColumnInfo(name = "target_date_millis")
    val targetDateMillis: Long? = null,

    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean = true,

    @ColumnInfo(name = "next_trigger_millis")
    val nextTriggerMillis: Long = 0L,

    @ColumnInfo(name = "extra_data")
    val extraData: String? = null,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
