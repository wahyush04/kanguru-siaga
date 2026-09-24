package com.kangurusiaga.app.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PmkAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationManager: KanguruNotificationManager

    @Inject
    lateinit var reminderScheduler: PmkReminderScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(EXTRA_REMINDER_ID, 0L)
        val label = intent.getStringExtra(EXTRA_LABEL) ?: "PMK"
        val targetMinutes = intent.getIntExtra(EXTRA_TARGET_MINUTES, 60)
        val hour = intent.getIntExtra(EXTRA_HOUR, 8)
        val minute = intent.getIntExtra(EXTRA_MINUTE, 0)

        // Show notification
        notificationManager.showPmkReminderNotification(label, targetMinutes)

        // Reschedule for next day recurrence
        reminderScheduler.scheduleNextOccurrence(reminderId, label, targetMinutes, hour, minute)
    }

    companion object {
        const val EXTRA_REMINDER_ID = "extra_reminder_id"
        const val EXTRA_LABEL = "extra_label"
        const val EXTRA_TARGET_MINUTES = "extra_target_minutes"
        const val EXTRA_HOUR = "extra_hour"
        const val EXTRA_MINUTE = "extra_minute"
    }
}
