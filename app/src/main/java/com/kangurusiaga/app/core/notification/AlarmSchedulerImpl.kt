package com.kangurusiaga.app.core.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.kangurusiaga.app.data.local.dao.BabyDao
import com.kangurusiaga.app.data.local.dao.FeedingScheduleDao
import com.kangurusiaga.app.data.local.dao.PmkReminderDao
import com.kangurusiaga.app.data.local.dao.PrematureScreeningDao
import com.kangurusiaga.app.data.local.dao.ReminderScheduleDao
import com.kangurusiaga.app.data.local.entity.ReminderScheduleEntity
import com.kangurusiaga.app.domain.model.ReminderType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val reminderScheduleDao: ReminderScheduleDao,
    private val pmkReminderDao: PmkReminderDao,
    private val feedingScheduleDao: FeedingScheduleDao,
    private val prematureScreeningDao: PrematureScreeningDao,
    private val notificationManager: KanguruNotificationManager,
    private val babyDao: BabyDao
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    private val syncMutex = Mutex()

    override suspend fun syncAndScheduleNextAlarm() {
        syncMutex.withLock {
            val now = System.currentTimeMillis()

            // 1. Sync domain entities (PMK, Feeding, Screening) into unified reminder_schedules table
            syncDomainTablesWithRoom(now)

            // 2. Catch-Up Evaluation for Missed Alarms (nextTriggerMillis <= now)
            handleMissedAlarms(now)

            // 3. Sequential Single Next-Alarm: find the 1 earliest active schedule (nextTriggerMillis > now)
            val nextAlarm = reminderScheduleDao.getNextEarliestActive(now)
            if (nextAlarm == null) {
                cancelAlarmInternal()
            } else {
                scheduleSingleAlarm(nextAlarm)
            }
        }
    }

    override suspend fun snoozeReminder(reminderId: String, durationMinutes: Int) {
        syncMutex.withLock {
            val now = System.currentTimeMillis()
            val original = reminderScheduleDao.getById(reminderId)
            val snoozeTrigger = now + TimeUnit.MINUTES.toMillis(durationMinutes.toLong())
            val snoozeId = "SNOOZE_${reminderId}_${now}"

            val snoozeSchedule = ReminderScheduleEntity(
                id = snoozeId,
                type = original?.type ?: ReminderType.PMK.name,
                referenceId = original?.referenceId ?: 0L,
                subIndex = original?.subIndex ?: 0,
                label = "${original?.label ?: "Pengingat"} (Tunda)",
                timeHour = 0,
                timeMinute = 0,
                targetDateMillis = null,
                isEnabled = true,
                nextTriggerMillis = snoozeTrigger,
                extraData = original?.extraData,
                updatedAt = now
            )
            reminderScheduleDao.insertOrUpdate(snoozeSchedule)

            // Schedule the single earliest next alarm (which may now be this snooze)
            val nextAlarm = reminderScheduleDao.getNextEarliestActive(now)
            if (nextAlarm != null) {
                scheduleSingleAlarm(nextAlarm)
            }
        }
    }

    override suspend fun cancelAlarm() {
        syncMutex.withLock {
            cancelAlarmInternal()
        }
    }

    override suspend fun scheduleFentonWeeklyReminder(babyId: Long, isEnabled: Boolean) {
        syncMutex.withLock {
            val id = "FENTON_$babyId"
            val now = System.currentTimeMillis()
            if (!isEnabled) {
                reminderScheduleDao.updateEnabled(id, false)
            } else {
                val nextTrigger = computeNextWeeklyTrigger(9, 0, startFromEpoch = now + TimeUnit.DAYS.toMillis(7))
                val fentonSchedule = ReminderScheduleEntity(
                    id = id,
                    type = ReminderType.FENTON.name,
                    referenceId = babyId,
                    subIndex = 0,
                    label = "Pengukuran Mingguan Grafik Fenton",
                    timeHour = 9,
                    timeMinute = 0,
                    targetDateMillis = null,
                    isEnabled = true,
                    nextTriggerMillis = nextTrigger,
                    extraData = null,
                    updatedAt = now
                )
                reminderScheduleDao.insertOrUpdate(fentonSchedule)
            }
        }
        syncAndScheduleNextAlarm()
    }

    private suspend fun syncDomainTablesWithRoom(now: Long) {
        // A. Synchronize PMK Reminders
        try {
            val pmkList = pmkReminderDao.getAllReminders()
            for (pmk in pmkList) {
                val scheduleId = "PMK_${pmk.id}"
                val existing = reminderScheduleDao.getById(scheduleId)
                val targetNext = if (existing != null && existing.nextTriggerMillis > now &&
                    existing.timeHour == pmk.timeHour && existing.timeMinute == pmk.timeMinute) {
                    existing.nextTriggerMillis
                } else {
                    computeNextDailyTrigger(pmk.timeHour, pmk.timeMinute, allowToday = true)
                }

                reminderScheduleDao.insertOrUpdate(
                    ReminderScheduleEntity(
                        id = scheduleId,
                        type = ReminderType.PMK.name,
                        referenceId = pmk.id,
                        subIndex = 0,
                        label = pmk.label.ifBlank { "Sesi PMK" },
                        timeHour = pmk.timeHour,
                        timeMinute = pmk.timeMinute,
                        targetDateMillis = null,
                        isEnabled = pmk.isEnabled,
                        nextTriggerMillis = targetNext,
                        extraData = "${pmk.targetMinutes}",
                        updatedAt = now
                    )
                )
            }
        } catch (_: Exception) {
        }

        // B. Synchronize Feeding / ASI Schedules
        try {
            val feedingList = feedingScheduleDao.getAllSchedules()
            for (feed in feedingList) {
                val scheduleId = "ASI_${feed.id}"
                val existing = reminderScheduleDao.getById(scheduleId)
                val isFeedActive = feed.isEnabled && feed.reminderEnabled
                val targetNext = if (existing != null && existing.nextTriggerMillis > now &&
                    existing.timeHour == feed.hour && existing.timeMinute == feed.minute) {
                    existing.nextTriggerMillis
                } else {
                    computeNextDailyTrigger(feed.hour, feed.minute, allowToday = true)
                }

                reminderScheduleDao.insertOrUpdate(
                    ReminderScheduleEntity(
                        id = scheduleId,
                        type = ReminderType.ASI.name,
                        referenceId = feed.id,
                        subIndex = 0,
                        label = "Pemberian ASI (${String.format(Locale.getDefault(), "%02d:%02d", feed.hour, feed.minute)})",
                        timeHour = feed.hour,
                        timeMinute = feed.minute,
                        targetDateMillis = null,
                        isEnabled = isFeedActive,
                        nextTriggerMillis = targetNext,
                        extraData = "${feed.volumeMl}|${feed.method}",
                        updatedAt = now
                    )
                )
            }
        } catch (_: Exception) {
        }

        // C. Synchronize Premature Screenings (4 non-repeating milestones)
        try {
            val screeningList = prematureScreeningDao.getAllIncomplete()
            for (screening in screeningList) {
                // Alarm 1: Primary Target Date
                if (screening.targetDateEpoch > 0) {
                    val id1 = "SKRINING_${screening.id}_1"
                    val existing1 = reminderScheduleDao.getById(id1)
                    val trigger1 = computeScreeningTrigger(screening.targetDateEpoch)
                    val next1 = if (existing1 != null && existing1.nextTriggerMillis > now) {
                        existing1.nextTriggerMillis
                    } else {
                        trigger1
                    }

                    reminderScheduleDao.insertOrUpdate(
                        ReminderScheduleEntity(
                            id = id1,
                            type = ReminderType.SKRINING.name,
                            referenceId = screening.id,
                            subIndex = 1,
                            label = screening.title,
                            timeHour = 8,
                            timeMinute = 0,
                            targetDateMillis = screening.targetDateEpoch,
                            isEnabled = screening.isAlarmActive && !screening.isCompleted,
                            nextTriggerMillis = next1,
                            extraData = "${screening.categoryBadge}|${screening.clinicOrLab}",
                            updatedAt = now
                        )
                    )
                }

                // Alarm 2: Secondary Target Date (Hearing OAE/BERA)
                val date2 = screening.targetDateEpoch2
                if (date2 != null && date2 > 0) {
                    val id2 = "SKRINING_${screening.id}_2"
                    val existing2 = reminderScheduleDao.getById(id2)
                    val trigger2 = computeScreeningTrigger(date2)
                    val next2 = if (existing2 != null && existing2.nextTriggerMillis > now) {
                        existing2.nextTriggerMillis
                    } else {
                        trigger2
                    }

                    reminderScheduleDao.insertOrUpdate(
                        ReminderScheduleEntity(
                            id = id2,
                            type = ReminderType.SKRINING.name,
                            referenceId = screening.id,
                            subIndex = 2,
                            label = "${screening.title} (Lanjutan)",
                            timeHour = 8,
                            timeMinute = 0,
                            targetDateMillis = date2,
                            isEnabled = screening.isAlarm2Active && !screening.isCompleted,
                            nextTriggerMillis = next2,
                            extraData = "${screening.categoryBadge}|${screening.clinicOrLab}",
                            updatedAt = now
                        )
                    )
                }
            }
        } catch (_: Exception) {
        }
    }

    private suspend fun handleMissedAlarms(now: Long) {
        val missedList = reminderScheduleDao.getMissedReminders(now)
        for (missed in missedList) {
            when (missed.type) {
                ReminderType.PMK.name, ReminderType.ASI.name -> {
                    val delayMillis = now - missed.nextTriggerMillis
                    val thirtyMinutesMillis = TimeUnit.MINUTES.toMillis(30)

                    if (delayMillis <= thirtyMinutesMillis) {
                        // Missed <= 30 mins: show informational missed-alarm notification without loud audio
                        val minutesAgo = TimeUnit.MILLISECONDS.toMinutes(delayMillis).coerceAtLeast(1)
                        val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", missed.timeHour, missed.timeMinute)
                        notificationManager.showMissedReminderNotification(
                            title = "Pengingat Terlewat: ${missed.label}",
                            message = "Jadwal pukul $formattedTime terlewat sekitar $minutesAgo menit lalu saat perangkat tidak aktif."
                        )
                        // Advance to next daily slot (tomorrow)
                        val nextSlot = computeNextDailyTrigger(missed.timeHour, missed.timeMinute, allowToday = false)
                        reminderScheduleDao.updateNextTrigger(missed.id, nextSlot)
                    } else {
                        // Missed > 30 mins: fast-forward to next future slot silently
                        val nextSlot = computeNextDailyTrigger(missed.timeHour, missed.timeMinute, allowToday = true)
                        reminderScheduleDao.updateNextTrigger(missed.id, nextSlot)
                    }

                    // If it was a temporary snooze alarm that expired, remove it
                    if (missed.id.startsWith("SNOOZE_")) {
                        reminderScheduleDao.deleteById(missed.id)
                    }
                }

                ReminderType.SKRINING.name -> {
                    // Inform user of overdue milestone, then disable
                    notificationManager.showDelayedReminderNotification(
                        title = "Pengingat Tertunda: ${missed.label}",
                        message = "Jadwal evaluasi klinis telah melewati batas tanggal. Segera konsultasikan ke fasilitas kesehatan.",
                        navigateDestination = "screening_schedule"
                    )
                    reminderScheduleDao.updateEnabled(missed.id, false)
                }

                ReminderType.FENTON.name -> {
                    // Inform user of overdue weekly growth recording, then advance 7 days
                    notificationManager.showDelayedReminderNotification(
                        title = "Pengingat Tertunda: Grafik Pertumbuhan Fenton",
                        message = "Yuk catat penimbangan berat badan dan lingkar kepala mingguan si kecil untuk memantau kurva Fenton.",
                        navigateDestination = "growth"
                    )
                    val nextSlot = computeNextWeeklyTrigger(missed.timeHour, missed.timeMinute, startFromEpoch = missed.nextTriggerMillis)
                    reminderScheduleDao.updateNextTrigger(missed.id, nextSlot)
                }

                else -> {
                    reminderScheduleDao.updateEnabled(missed.id, false)
                }
            }
        }
    }

    private fun scheduleSingleAlarm(reminder: ReminderScheduleEntity) {
        if (alarmManager == null) return

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_TRIGGER_REMINDER
            putExtra(AlarmReceiver.EXTRA_REMINDER_ID, reminder.id)
            putExtra(AlarmReceiver.EXTRA_TYPE, reminder.type)
            putExtra(AlarmReceiver.EXTRA_REFERENCE_ID, reminder.referenceId)
            putExtra(AlarmReceiver.EXTRA_SUB_INDEX, reminder.subIndex)
            putExtra(AlarmReceiver.EXTRA_LABEL, reminder.label)
            putExtra(AlarmReceiver.EXTRA_TARGET_MILLIS, reminder.nextTriggerMillis)
            putExtra(AlarmReceiver.EXTRA_EXTRA_DATA, reminder.extraData)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAtMillis = reminder.nextTriggerMillis

        if (reminder.type == ReminderType.PMK.name || reminder.type == ReminderType.ASI.name) {
            // Urgent reminders: PMK & ASI use setExactAndAllowWhileIdle with permission check and fallback
            val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                try {
                    alarmManager.canScheduleExactAlarms()
                } catch (_: SecurityException) {
                    false
                }
            } else {
                true
            }

            if (canScheduleExact) {
                try {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                } catch (_: SecurityException) {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } else {
            // Routine reminders: SKRINING & FENTON use inexact idle-tolerant alarm
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    private fun cancelAlarmInternal() {
        if (alarmManager == null) return
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_TRIGGER_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    companion object {
        const val ALARM_REQUEST_CODE = 4001

        fun computeNextDailyTrigger(hour: Int, minute: Int, allowToday: Boolean = true): Long {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val now = System.currentTimeMillis()
            if (!allowToday || calendar.timeInMillis <= now) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            return calendar.timeInMillis
        }

        fun computeNextWeeklyTrigger(hour: Int, minute: Int, startFromEpoch: Long = System.currentTimeMillis()): Long {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = startFromEpoch
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val now = System.currentTimeMillis()
            while (calendar.timeInMillis <= now) {
                calendar.add(Calendar.DAY_OF_YEAR, 7)
            }
            return calendar.timeInMillis
        }

        fun computeScreeningTrigger(targetEpoch: Long): Long {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = targetEpoch
                set(Calendar.HOUR_OF_DAY, 8)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            return calendar.timeInMillis
        }
    }
}
