package com.kangurusiaga.app.data.mapper

import com.kangurusiaga.app.data.local.entity.PmkReminderEntity
import com.kangurusiaga.app.data.local.entity.PmkSessionEntity
import com.kangurusiaga.app.domain.model.PmkReminder
import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.model.PmkSource

fun PmkSessionEntity.toDomain(): PmkSession {
    val pmkSource = try {
        PmkSource.valueOf(source)
    } catch (_: Exception) {
        PmkSource.TIMER
    }
    return PmkSession(
        id = id,
        babyId = babyId,
        startTimeEpoch = startTimeEpoch,
        endTimeEpoch = endTimeEpoch,
        durationMinutes = durationMinutes,
        targetDurationMinutes = targetDurationMinutes,
        source = pmkSource,
        babyTemperature = babyTemperature,
        babyResponse = babyResponse,
        notes = notes,
        createdAtEpoch = createdAtEpoch
    )
}

fun PmkSession.toEntity(): PmkSessionEntity {
    return PmkSessionEntity(
        id = id,
        babyId = babyId,
        startTimeEpoch = startTimeEpoch,
        endTimeEpoch = endTimeEpoch,
        durationMinutes = durationMinutes,
        targetDurationMinutes = targetDurationMinutes,
        source = source.name,
        babyTemperature = babyTemperature,
        babyResponse = babyResponse,
        notes = notes,
        createdAtEpoch = createdAtEpoch
    )
}

fun PmkReminderEntity.toDomain(): PmkReminder {
    return PmkReminder(
        id = id,
        babyId = babyId,
        timeHour = timeHour,
        timeMinute = timeMinute,
        label = label,
        targetMinutes = targetMinutes,
        daysOfWeekMask = daysOfWeekMask,
        isEnabled = isEnabled,
        createdAtEpoch = createdAtEpoch
    )
}

fun PmkReminder.toEntity(): PmkReminderEntity {
    return PmkReminderEntity(
        id = id,
        babyId = babyId,
        timeHour = timeHour,
        timeMinute = timeMinute,
        label = label,
        targetMinutes = targetMinutes,
        daysOfWeekMask = daysOfWeekMask,
        isEnabled = isEnabled,
        createdAtEpoch = createdAtEpoch
    )
}
