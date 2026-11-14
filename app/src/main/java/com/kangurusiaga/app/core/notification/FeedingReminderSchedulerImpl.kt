package com.kangurusiaga.app.core.notification

import android.content.Context
import com.kangurusiaga.app.domain.model.FeedingSchedule
import com.kangurusiaga.app.domain.repository.FeedingScheduleRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedingReminderSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: FeedingScheduleRepository,
    private val alarmScheduler: AlarmScheduler
) : FeedingReminderScheduler {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun schedule(schedule: FeedingSchedule) {
        scope.launch {
            alarmScheduler.syncAndScheduleNextAlarm()
        }
    }

    override fun cancel(scheduleId: Long) {
        scope.launch {
            alarmScheduler.syncAndScheduleNextAlarm()
        }
    }

    override fun reschedule(schedule: FeedingSchedule) {
        scope.launch {
            alarmScheduler.syncAndScheduleNextAlarm()
        }
    }

    override fun rescheduleAllEnabled() {
        scope.launch {
            repository.resetAllOffsetToZero()
            alarmScheduler.syncAndScheduleNextAlarm()
        }
    }

    override fun scheduleNextOccurrence(
        scheduleId: Long,
        hour: Int,
        minute: Int,
        volumeMl: Int,
        methodDisplayName: String,
        offsetMinutes: Int
    ) {
        scope.launch {
            alarmScheduler.syncAndScheduleNextAlarm()
        }
    }
}
