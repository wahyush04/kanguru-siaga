package com.kangurusiaga.app.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kangurusiaga.app.domain.model.RepeatType
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FeedingAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationManager: KanguruNotificationManager

    @Inject
    lateinit var reminderScheduler: FeedingReminderScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val scheduleId = intent.getLongExtra(EXTRA_SCHEDULE_ID, 0L)
        val hour = intent.getIntExtra(EXTRA_HOUR, 0)
        val minute = intent.getIntExtra(EXTRA_MINUTE, 0)
        val volumeMl = intent.getIntExtra(EXTRA_VOLUME_ML, 30)
        val methodDisplayName = intent.getStringExtra(EXTRA_METHOD) ?: "OGT/NGT"
        val offsetMinutes = intent.getIntExtra(EXTRA_OFFSET_MINUTES, 10)
        val repeatType = intent.getStringExtra(EXTRA_REPEAT_TYPE) ?: RepeatType.DAILY.name

        // Show notification to user
        notificationManager.showFeedingReminderNotification(
            scheduleId = scheduleId,
            volumeMl = volumeMl,
            methodDisplayName = methodDisplayName
        )

        // Reschedule for next day if DAILY
        if (repeatType == RepeatType.DAILY.name) {
            reminderScheduler.scheduleNextOccurrence(
                scheduleId = scheduleId,
                hour = hour,
                minute = minute,
                volumeMl = volumeMl,
                methodDisplayName = methodDisplayName,
                offsetMinutes = offsetMinutes
            )
        }
    }

    companion object {
        const val EXTRA_SCHEDULE_ID = "extra_schedule_id"
        const val EXTRA_HOUR = "extra_hour"
        const val EXTRA_MINUTE = "extra_minute"
        const val EXTRA_VOLUME_ML = "extra_volume_ml"
        const val EXTRA_METHOD = "extra_method"
        const val EXTRA_OFFSET_MINUTES = "extra_offset_minutes"
        const val EXTRA_REPEAT_TYPE = "extra_repeat_type"
    }
}
