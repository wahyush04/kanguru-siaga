package com.kangurusiaga.app.fake

import com.kangurusiaga.app.core.notification.FeedingReminderScheduler
import com.kangurusiaga.app.domain.model.FeedingSchedule

class FakeFeedingReminderScheduler : FeedingReminderScheduler {

    val scheduledSchedules = mutableMapOf<Long, FeedingSchedule>()
    val cancelledIds = mutableListOf<Long>()
    val nextOccurrences = mutableListOf<Long>()
    var rescheduleAllCallCount = 0

    override fun schedule(schedule: FeedingSchedule) {
        scheduledSchedules[schedule.id] = schedule
    }

    override fun cancel(scheduleId: Long) {
        scheduledSchedules.remove(scheduleId)
        cancelledIds.add(scheduleId)
    }

    override fun reschedule(schedule: FeedingSchedule) {
        cancel(schedule.id)
        schedule(schedule)
    }

    override fun rescheduleAllEnabled() {
        rescheduleAllCallCount++
    }

    override fun scheduleNextOccurrence(
        scheduleId: Long,
        hour: Int,
        minute: Int,
        volumeMl: Int,
        methodDisplayName: String,
        offsetMinutes: Int
    ) {
        nextOccurrences.add(scheduleId)
    }
}
