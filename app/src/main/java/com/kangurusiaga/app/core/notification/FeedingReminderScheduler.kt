package com.kangurusiaga.app.core.notification

import com.kangurusiaga.app.domain.model.FeedingSchedule

interface FeedingReminderScheduler {
    fun schedule(schedule: FeedingSchedule)
    fun cancel(scheduleId: Long)
    fun reschedule(schedule: FeedingSchedule)
    fun rescheduleAllEnabled()
    fun scheduleNextOccurrence(
        scheduleId: Long,
        hour: Int,
        minute: Int,
        volumeMl: Int,
        methodDisplayName: String,
        offsetMinutes: Int
    )
}
