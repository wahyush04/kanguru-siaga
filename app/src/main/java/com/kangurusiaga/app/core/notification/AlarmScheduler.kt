package com.kangurusiaga.app.core.notification

interface AlarmScheduler {
    /**
     * Synchronizes all active schedules in Room (evaluating missed ones according to policy)
     * and schedules only the single next earliest active alarm in AlarmManager.
     */
    suspend fun syncAndScheduleNextAlarm()

    /**
     * Schedules a temporary 10-minute snooze for a triggered urgent alarm.
     */
    suspend fun snoozeReminder(reminderId: String, durationMinutes: Int = 10)

    /**
     * Cancels any pending single alarm from AlarmManager.
     */
    suspend fun cancelAlarm()

    /**
     * Enables or disables the weekly 7-day Fenton growth measurement routine reminder for a baby.
     */
    suspend fun scheduleFentonWeeklyReminder(babyId: Long, isEnabled: Boolean)
}
