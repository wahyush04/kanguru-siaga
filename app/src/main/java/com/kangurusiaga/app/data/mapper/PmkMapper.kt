package com.kangurusiaga.app.data.mapper

import com.kangurusiaga.app.data.local.entity.PmkReminderEntity
import com.kangurusiaga.app.data.local.entity.PmkSegmentEntity
import com.kangurusiaga.app.data.local.entity.PmkSessionEntity
import com.kangurusiaga.app.data.local.entity.PmkSessionWithSegments
import com.kangurusiaga.app.domain.model.PmkCaregiver
import com.kangurusiaga.app.domain.model.PmkReminder
import com.kangurusiaga.app.domain.model.PmkSegment
import com.kangurusiaga.app.domain.model.PmkSegmentStatus
import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.model.PmkSource
import com.kangurusiaga.app.domain.model.TimerStatus

fun PmkSessionEntity.toDomain(segments: List<PmkSegment> = emptyList()): PmkSession {
    val pmkSource = try {
        PmkSource.valueOf(source)
    } catch (_: Exception) {
        PmkSource.TIMER
    }
    val timerStatus = try {
        TimerStatus.valueOf(status)
    } catch (_: Exception) {
        TimerStatus.COMPLETED
    }
    val caregiver = PmkCaregiver.fromString(currentCaregiver)

    return PmkSession(
        id = id,
        babyId = babyId,
        startTimeEpoch = startTimeEpoch,
        endTimeEpoch = endTimeEpoch,
        durationMinutes = durationMinutes,
        targetDurationMinutes = targetDurationMinutes,
        source = pmkSource,
        currentCaregiver = caregiver,
        status = timerStatus,
        activeDurationMinutes = activeDurationMinutes,
        pauseDurationMinutes = pauseDurationMinutes,
        babyTemperature = babyTemperature,
        babyResponse = babyResponse,
        notes = notes,
        createdAtEpoch = createdAtEpoch,
        segments = segments
    )
}

fun PmkSessionWithSegments.toDomain(): PmkSession {
    val domainSegments = segments.map { it.toDomain() }
    return session.toDomain(domainSegments)
}

fun PmkSegmentEntity.toDomain(): PmkSegment {
    val caregiver = PmkCaregiver.fromString(this.caregiver)
    val segmentStatus = try {
        PmkSegmentStatus.valueOf(this.status)
    } catch (_: Exception) {
        PmkSegmentStatus.ACTIVE
    }

    return PmkSegment(
        id = id,
        sessionId = sessionId,
        caregiver = caregiver,
        startTimeEpoch = startTimeEpoch,
        endTimeEpoch = endTimeEpoch,
        durationMinutes = durationMinutes,
        status = segmentStatus,
        pauseReason = pauseReason,
        babyTemperature = babyTemperature,
        babyResponse = babyResponse,
        notes = notes
    )
}

fun PmkSegment.toEntity(): PmkSegmentEntity {
    return PmkSegmentEntity(
        id = id,
        sessionId = sessionId,
        caregiver = caregiver.name,
        startTimeEpoch = startTimeEpoch,
        endTimeEpoch = endTimeEpoch,
        durationMinutes = durationMinutes,
        status = status.name,
        pauseReason = pauseReason,
        babyTemperature = babyTemperature,
        babyResponse = babyResponse,
        notes = notes
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
        currentCaregiver = currentCaregiver.name,
        status = status.name,
        activeDurationMinutes = activeDurationMinutes,
        pauseDurationMinutes = pauseDurationMinutes,
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
