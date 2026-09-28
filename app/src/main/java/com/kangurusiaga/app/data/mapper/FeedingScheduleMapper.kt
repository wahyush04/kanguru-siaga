package com.kangurusiaga.app.data.mapper

import com.kangurusiaga.app.data.local.entity.FeedingScheduleEntity
import com.kangurusiaga.app.domain.model.FeedingMethod
import com.kangurusiaga.app.domain.model.FeedingSchedule
import com.kangurusiaga.app.domain.model.RepeatType

fun FeedingScheduleEntity.toDomain(): FeedingSchedule {
    return FeedingSchedule(
        id = id,
        hour = hour,
        minute = minute,
        volumeMl = volumeMl,
        method = FeedingMethod.fromString(method),
        note = note,
        isEnabled = isEnabled,
        reminderEnabled = reminderEnabled,
        reminderOffsetMinutes = reminderOffsetMinutes,
        repeatType = RepeatType.fromString(repeatType),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun FeedingSchedule.toEntity(): FeedingScheduleEntity {
    return FeedingScheduleEntity(
        id = id,
        hour = hour,
        minute = minute,
        volumeMl = volumeMl,
        method = method.name,
        note = note,
        isEnabled = isEnabled,
        reminderEnabled = reminderEnabled,
        reminderOffsetMinutes = reminderOffsetMinutes,
        repeatType = repeatType.name,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
