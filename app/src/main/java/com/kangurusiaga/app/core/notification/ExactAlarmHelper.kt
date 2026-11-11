package com.kangurusiaga.app.core.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.kangurusiaga.app.MainActivity

/**
 * Shared exact-alarm and fallback scheduler used by notification and timer components.
 *
 * Without `USE_EXACT_ALARM` in the manifest, Android 14+ installs do not grant exact alarms by
 * default. We check [AlarmManager.canScheduleExactAlarms] first and fall back gracefully to
 * [AlarmManager.setAndAllowWhileIdle] so reminders still deliver reliably without throwing.
 */
object ExactAlarmHelper {

    const val ACTION_EXACT_ALARM_PERMISSION_STATE_CHANGED =
        "android.app.action.SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED"

    private const val ACTION_QUICKBOOT_POWERON = "android.intent.action.QUICKBOOT_POWERON"

    /**
     * Returns true when a system broadcast action indicates that active alarms should be
     * rescheduled (device reboot, app update, or exact-alarm permission granted in Settings).
     */
    fun isRescheduleBroadcastAction(action: String?): Boolean {
        return action == Intent.ACTION_BOOT_COMPLETED ||
            action == ACTION_QUICKBOOT_POWERON ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == ACTION_EXACT_ALARM_PERMISSION_STATE_CHANGED
    }

    /**
     * Schedules a user-visible alarm clock when exact alarms are permitted, falling back to
     * exact/inexact idle-tolerant alarms otherwise.
     */
    fun scheduleAlarmClockOrFallback(
        context: Context,
        alarmManager: AlarmManager?,
        triggerAtMillis: Long,
        pendingIntent: PendingIntent,
        showRequestCode: Int,
        navigateDestination: String,
        sdkInt: Int = Build.VERSION.SDK_INT
    ) {
        if (alarmManager == null) return

        val canUseExact = sdkInt < Build.VERSION_CODES.S || canScheduleExactSafely(alarmManager)
        if (!canUseExact) {
            scheduleInexactAllowWhileIdle(alarmManager, triggerAtMillis, pendingIntent, sdkInt)
            return
        }

        try {
            val showIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("navigate_to", navigateDestination)
            }
            val showPendingIntent = PendingIntent.getActivity(
                context,
                showRequestCode,
                showIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerAtMillis, showPendingIntent)
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
        } catch (_: SecurityException) {
            scheduleExactOrFallback(alarmManager, triggerAtMillis, pendingIntent, sdkInt)
        }
    }

    /**
     * Schedules an exact idle-tolerant alarm when permitted, falling back to
     * [AlarmManager.setAndAllowWhileIdle] if exact-alarm permission is not granted.
     */
    fun scheduleExactOrFallback(
        alarmManager: AlarmManager?,
        triggerAtMillis: Long,
        pendingIntent: PendingIntent,
        sdkInt: Int = Build.VERSION.SDK_INT
    ) {
        if (alarmManager == null) return

        try {
            if (sdkInt >= Build.VERSION_CODES.S) {
                if (canScheduleExactSafely(alarmManager)) {
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
            } else if (sdkInt >= Build.VERSION_CODES.M) {
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
            scheduleInexactAllowWhileIdle(alarmManager, triggerAtMillis, pendingIntent, sdkInt)
        } catch (_: Throwable) {
            // Gracefully ignored in restricted OEM environments or unit test stubs
        }
    }

    private fun scheduleInexactAllowWhileIdle(
        alarmManager: AlarmManager,
        triggerAtMillis: Long,
        pendingIntent: PendingIntent,
        sdkInt: Int
    ) {
        try {
            if (sdkInt >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (_: Throwable) {
            // Gracefully ignored when background scheduling is restricted by the OS/OEM
        }
    }

    private fun canScheduleExactSafely(alarmManager: AlarmManager): Boolean {
        return try {
            alarmManager.canScheduleExactAlarms()
        } catch (_: Throwable) {
            false
        }
    }
}

