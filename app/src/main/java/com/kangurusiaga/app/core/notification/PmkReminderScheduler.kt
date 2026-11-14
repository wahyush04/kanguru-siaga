package com.kangurusiaga.app.core.notification

import android.content.Context
import com.kangurusiaga.app.domain.model.PmkReminder
import com.kangurusiaga.app.domain.repository.PmkReminderRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PmkReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val reminderRepository: PmkReminderRepository,
    private val alarmScheduler: AlarmScheduler
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun scheduleReminder(reminder: PmkReminder) {
        scope.launch {
            alarmScheduler.syncAndScheduleNextAlarm()
        }
    }

    fun scheduleNextOccurrence(
        reminderId: Long,
        label: String,
        targetMinutes: Int,
        hour: Int,
        minute: Int,
        repeatDaily: Boolean = true
    ) {
        scope.launch {
            alarmScheduler.syncAndScheduleNextAlarm()
        }
    }

    fun cancelReminder(reminderId: Long) {
        scope.launch {
            alarmScheduler.syncAndScheduleNextAlarm()
        }
    }

    fun rescheduleAllEnabledReminders() {
        scope.launch {
            alarmScheduler.syncAndScheduleNextAlarm()
        }
    }
}
