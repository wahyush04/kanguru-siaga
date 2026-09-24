package com.kangurusiaga.app.core.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.kangurusiaga.app.domain.model.PmkReminder
import com.kangurusiaga.app.domain.repository.PmkReminderRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PmkReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val reminderRepository: PmkReminderRepository
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    fun scheduleReminder(reminder: PmkReminder) {
        if (!reminder.isEnabled) {
            cancelReminder(reminder.id)
            return
        }

        val triggerTime = computeNextTriggerTime(reminder.timeHour, reminder.timeMinute)
        val pendingIntent = createPendingIntent(
            reminderId = reminder.id,
            label = reminder.label,
            targetMinutes = reminder.targetMinutes,
            hour = reminder.timeHour,
            minute = reminder.timeMinute
        )

        scheduleAlarm(triggerTime, pendingIntent)
    }

    fun scheduleNextOccurrence(
        reminderId: Long,
        label: String,
        targetMinutes: Int,
        hour: Int,
        minute: Int
    ) {
        val nextTriggerTime = computeNextTriggerTime(hour, minute, allowToday = false)
        val pendingIntent = createPendingIntent(reminderId, label, targetMinutes, hour, minute)
        scheduleAlarm(nextTriggerTime, pendingIntent)
    }

    fun cancelReminder(reminderId: Long) {
        val intent = Intent(context, PmkAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun rescheduleAllEnabledReminders() {
        CoroutineScope(Dispatchers.IO).launch {
            val enabledReminders = reminderRepository.getAllEnabledReminders()
            for (reminder in enabledReminders) {
                scheduleReminder(reminder)
            }
        }
    }

    private fun scheduleAlarm(triggerAtMillis: Long, pendingIntent: PendingIntent) {
        if (alarmManager == null) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    private fun computeNextTriggerTime(hour: Int, minute: Int, allowToday: Boolean = true): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (!allowToday || target.timeInMillis <= now.timeInMillis) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        return target.timeInMillis
    }

    private fun createPendingIntent(
        reminderId: Long,
        label: String,
        targetMinutes: Int,
        hour: Int,
        minute: Int
    ): PendingIntent {
        val intent = Intent(context, PmkAlarmReceiver::class.java).apply {
            putExtra(PmkAlarmReceiver.EXTRA_REMINDER_ID, reminderId)
            putExtra(PmkAlarmReceiver.EXTRA_LABEL, label)
            putExtra(PmkAlarmReceiver.EXTRA_TARGET_MINUTES, targetMinutes)
            putExtra(PmkAlarmReceiver.EXTRA_HOUR, hour)
            putExtra(PmkAlarmReceiver.EXTRA_MINUTE, minute)
        }

        return PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
