package com.kangurusiaga.app.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PmkBootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var reminderScheduler: PmkReminderScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (ExactAlarmHelper.isRescheduleBroadcastAction(intent.action)) {
            reminderScheduler.rescheduleAllEnabledReminders()
        }
    }
}
