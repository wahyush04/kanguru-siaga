package com.kangurusiaga.app.core.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.kangurusiaga.app.domain.model.FeedingSchedule
import com.kangurusiaga.app.domain.model.RepeatType
import com.kangurusiaga.app.domain.repository.FeedingScheduleRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedingReminderSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: FeedingScheduleRepository
) : FeedingReminderScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    override fun schedule(schedule: FeedingSchedule) {
        if (!schedule.isEnabled || !schedule.reminderEnabled) {
            cancel(schedule.id)
            return
        }

        val triggerTime = computeNextTriggerTime(
            hour = schedule.hour,
            minute = schedule.minute,
            offsetMinutes = schedule.reminderOffsetMinutes,
            allowToday = true
        )

        // If repeat type is ONCE and the time for today has already passed, skip scheduling
        if (schedule.repeatType == RepeatType.ONCE) {
            val now = Calendar.getInstance().timeInMillis
            if (triggerTime < now) {
                return
            }
        }

        val pendingIntent = createPendingIntent(
            scheduleId = schedule.id,
            hour = schedule.hour,
            minute = schedule.minute,
            volumeMl = schedule.volumeMl,
            methodDisplayName = schedule.method.displayName,
            offsetMinutes = schedule.reminderOffsetMinutes,
            repeatType = schedule.repeatType.name
        )

        scheduleAlarm(triggerTime, pendingIntent)
    }

    override fun cancel(scheduleId: Long) {
        val intent = Intent(context, FeedingAlarmReceiver::class.java)
        val requestCode = (scheduleId % Int.MAX_VALUE).toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    override fun reschedule(schedule: FeedingSchedule) {
        cancel(schedule.id)
        schedule(schedule)
    }

    override fun rescheduleAllEnabled() {
        CoroutineScope(Dispatchers.IO).launch {
            val enabledSchedules = repository.getAllEnabledSchedules()
            for (schedule in enabledSchedules) {
                if (schedule.reminderEnabled) {
                    schedule(schedule)
                }
            }
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
        val nextTriggerTime = computeNextTriggerTime(
            hour = hour,
            minute = minute,
            offsetMinutes = offsetMinutes,
            allowToday = false
        )

        val pendingIntent = createPendingIntent(
            scheduleId = scheduleId,
            hour = hour,
            minute = minute,
            volumeMl = volumeMl,
            methodDisplayName = methodDisplayName,
            offsetMinutes = offsetMinutes,
            repeatType = RepeatType.DAILY.name
        )

        scheduleAlarm(nextTriggerTime, pendingIntent)
    }

    private fun scheduleAlarm(triggerAtMillis: Long, pendingIntent: PendingIntent) {
        if (alarmManager == null) return

        try {
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
        } catch (_: SecurityException) {
            // Gracefully handle exact alarm permission rejection
        }
    }

    private fun computeNextTriggerTime(
        hour: Int,
        minute: Int,
        offsetMinutes: Int,
        allowToday: Boolean
    ): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.MINUTE, -offsetMinutes)
        }

        if (!allowToday || target.timeInMillis <= now.timeInMillis) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        return target.timeInMillis
    }

    private fun createPendingIntent(
        scheduleId: Long,
        hour: Int,
        minute: Int,
        volumeMl: Int,
        methodDisplayName: String,
        offsetMinutes: Int,
        repeatType: String
    ): PendingIntent {
        val intent = Intent(context, FeedingAlarmReceiver::class.java).apply {
            putExtra(FeedingAlarmReceiver.EXTRA_SCHEDULE_ID, scheduleId)
            putExtra(FeedingAlarmReceiver.EXTRA_HOUR, hour)
            putExtra(FeedingAlarmReceiver.EXTRA_MINUTE, minute)
            putExtra(FeedingAlarmReceiver.EXTRA_VOLUME_ML, volumeMl)
            putExtra(FeedingAlarmReceiver.EXTRA_METHOD, methodDisplayName)
            putExtra(FeedingAlarmReceiver.EXTRA_OFFSET_MINUTES, offsetMinutes)
            putExtra(FeedingAlarmReceiver.EXTRA_REPEAT_TYPE, repeatType)
        }

        val requestCode = (scheduleId % Int.MAX_VALUE).toInt()
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
