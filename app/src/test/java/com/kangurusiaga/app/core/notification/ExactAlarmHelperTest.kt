package com.kangurusiaga.app.core.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExactAlarmHelperTest {

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `isRescheduleBroadcastAction recognizes boot update and permission state changed actions`() {
        assertTrue(ExactAlarmHelper.isRescheduleBroadcastAction(Intent.ACTION_BOOT_COMPLETED))
        assertTrue(ExactAlarmHelper.isRescheduleBroadcastAction("android.intent.action.QUICKBOOT_POWERON"))
        assertTrue(ExactAlarmHelper.isRescheduleBroadcastAction(Intent.ACTION_MY_PACKAGE_REPLACED))
        assertTrue(
            ExactAlarmHelper.isRescheduleBroadcastAction(
                ExactAlarmHelper.ACTION_EXACT_ALARM_PERMISSION_STATE_CHANGED
            )
        )
        assertFalse(ExactAlarmHelper.isRescheduleBroadcastAction(Intent.ACTION_SCREEN_ON))
        assertFalse(ExactAlarmHelper.isRescheduleBroadcastAction(null))
    }

    @Test
    fun `scheduleExactOrFallback falls back to setAndAllowWhileIdle on Android 12+ when exact alarm is denied`() {
        val alarmManager = mockk<AlarmManager>(relaxed = true)
        val pendingIntent = mockk<PendingIntent>(relaxed = true)
        every { alarmManager.canScheduleExactAlarms() } returns false

        ExactAlarmHelper.scheduleExactOrFallback(
            alarmManager = alarmManager,
            triggerAtMillis = 123456L,
            pendingIntent = pendingIntent,
            sdkInt = Build.VERSION_CODES.S
        )

        verify(exactly = 0) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 123456L, pendingIntent)
        }
        verify(exactly = 1) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 123456L, pendingIntent)
        }
    }

    @Test
    fun `scheduleExactOrFallback schedules exact alarm on Android 12+ when exact alarm is allowed`() {
        val alarmManager = mockk<AlarmManager>(relaxed = true)
        val pendingIntent = mockk<PendingIntent>(relaxed = true)
        every { alarmManager.canScheduleExactAlarms() } returns true

        ExactAlarmHelper.scheduleExactOrFallback(
            alarmManager = alarmManager,
            triggerAtMillis = 123456L,
            pendingIntent = pendingIntent,
            sdkInt = Build.VERSION_CODES.S
        )

        verify(exactly = 1) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 123456L, pendingIntent)
        }
    }

    @Test
    fun `scheduleExactOrFallback recovers from SecurityException using setAndAllowWhileIdle`() {
        val alarmManager = mockk<AlarmManager>(relaxed = true)
        val pendingIntent = mockk<PendingIntent>(relaxed = true)
        every { alarmManager.canScheduleExactAlarms() } returns true
        every {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 123456L, pendingIntent)
        } throws SecurityException("OEM exact alarm restriction")

        ExactAlarmHelper.scheduleExactOrFallback(
            alarmManager = alarmManager,
            triggerAtMillis = 123456L,
            pendingIntent = pendingIntent,
            sdkInt = Build.VERSION_CODES.S
        )

        verify(exactly = 1) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 123456L, pendingIntent)
        }
    }

    @Test
    fun `scheduleAlarmClockOrFallback skips setAlarmClock when exact alarm is denied on Android 12+`() {
        val context = mockk<Context>(relaxed = true)
        val alarmManager = mockk<AlarmManager>(relaxed = true)
        val pendingIntent = mockk<PendingIntent>(relaxed = true)
        every { alarmManager.canScheduleExactAlarms() } returns false

        ExactAlarmHelper.scheduleAlarmClockOrFallback(
            context = context,
            alarmManager = alarmManager,
            triggerAtMillis = 500000L,
            pendingIntent = pendingIntent,
            showRequestCode = 7,
            navigateDestination = "feeding",
            sdkInt = Build.VERSION_CODES.S
        )

        verify(exactly = 0) { alarmManager.setAlarmClock(any(), any()) }
        verify(exactly = 1) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 500000L, pendingIntent)
        }
    }

    @Test
    fun `scheduleAlarmClockOrFallback uses setAlarmClock when exact alarm is permitted`() {
        val context = mockk<Context>(relaxed = true)
        val alarmManager = mockk<AlarmManager>(relaxed = true)
        val pendingIntent = mockk<PendingIntent>(relaxed = true)
        val showPendingIntent = mockk<PendingIntent>(relaxed = true)

        every { alarmManager.canScheduleExactAlarms() } returns true
        mockkStatic(PendingIntent::class)
        mockkConstructor(Intent::class)
        mockkConstructor(AlarmManager.AlarmClockInfo::class)
        every {
            PendingIntent.getActivity(context, 7, any(), any())
        } returns showPendingIntent

        ExactAlarmHelper.scheduleAlarmClockOrFallback(
            context = context,
            alarmManager = alarmManager,
            triggerAtMillis = 500000L,
            pendingIntent = pendingIntent,
            showRequestCode = 7,
            navigateDestination = "feeding",
            sdkInt = Build.VERSION_CODES.S
        )

        verify(exactly = 1) { alarmManager.setAlarmClock(any(), pendingIntent) }
    }
}

