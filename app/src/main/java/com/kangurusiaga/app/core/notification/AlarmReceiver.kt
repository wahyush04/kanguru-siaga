package com.kangurusiaga.app.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.kangurusiaga.app.data.local.dao.BabyDao
import com.kangurusiaga.app.data.local.dao.InAppNotificationDao
import com.kangurusiaga.app.data.local.dao.ReminderScheduleDao
import com.kangurusiaga.app.data.local.entity.InAppNotificationEntity
import com.kangurusiaga.app.domain.model.NotificationBadgeColor
import com.kangurusiaga.app.domain.model.NotificationCategory
import com.kangurusiaga.app.domain.model.ReminderType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    @Inject
    lateinit var reminderScheduleDao: ReminderScheduleDao

    @Inject
    lateinit var notificationManager: KanguruNotificationManager

    @Inject
    lateinit var inAppNotificationDao: InAppNotificationDao

    @Inject
    lateinit var babyDao: BabyDao

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getStringExtra(EXTRA_REMINDER_ID) ?: return
        val type = intent.getStringExtra(EXTRA_TYPE) ?: ReminderType.PMK.name
        val referenceId = intent.getLongExtra(EXTRA_REFERENCE_ID, 0L)
        val subIndex = intent.getIntExtra(EXTRA_SUB_INDEX, 0)
        val label = intent.getStringExtra(EXTRA_LABEL) ?: "Pengingat"
        val extraData = intent.getStringExtra(EXTRA_EXTRA_DATA)

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val baby = babyDao.getActiveBabyOnce() ?: babyDao.getFirstBaby()
                val babyId = baby?.id ?: 1L
                val babyName = baby?.name ?: "si kecil"

                // 1. Execute Reminder Action (Separation of Audio & Routine Notifications)
                when (type) {
                    ReminderType.PMK.name -> {
                        // Urgent: Run ForegroundService with custom looping ringtone
                        val serviceIntent = Intent(context, AlarmSoundService::class.java).apply {
                            action = AlarmSoundService.ACTION_START_ALARM
                            putExtra(AlarmSoundService.EXTRA_REMINDER_ID, reminderId)
                            putExtra(AlarmSoundService.EXTRA_TYPE, ReminderType.PMK.name)
                            putExtra(AlarmSoundService.EXTRA_LABEL, label)
                            putExtra(AlarmSoundService.EXTRA_EXTRA_DATA, extraData)
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(serviceIntent)
                        } else {
                            context.startService(serviceIntent)
                        }

                        // Insert In-App Notification
                        val targetMins = extraData?.toIntOrNull() ?: 480
                        inAppNotificationDao.insertOrUpdate(
                            InAppNotificationEntity(
                                id = 0L,
                                babyId = babyId,
                                category = NotificationCategory.PMK_REMINDER.name,
                                referenceId = referenceId,
                                title = "Waktunya Sesi PMK Mandiri",
                                message = "Mulai sesi kontak kulit ke kulit ($label) selama $targetMins menit untuk kehangatan si kecil.",
                                badgeLabel = "PMK",
                                badgeColor = NotificationBadgeColor.PINK.name,
                                timeLabel = "Baru saja",
                                targetInfo = null,
                                isRead = false,
                                isToday = true,
                                primaryActionLabel = "Mulai PMK",
                                secondaryActionLabel = null,
                                timestampEpoch = System.currentTimeMillis()
                            )
                        )
                    }

                    ReminderType.ASI.name -> {
                        // Urgent: Run ForegroundService with custom looping ringtone
                        val serviceIntent = Intent(context, AlarmSoundService::class.java).apply {
                            action = AlarmSoundService.ACTION_START_ALARM
                            putExtra(AlarmSoundService.EXTRA_REMINDER_ID, reminderId)
                            putExtra(AlarmSoundService.EXTRA_TYPE, ReminderType.ASI.name)
                            putExtra(AlarmSoundService.EXTRA_LABEL, label)
                            putExtra(AlarmSoundService.EXTRA_EXTRA_DATA, extraData)
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(serviceIntent)
                        } else {
                            context.startService(serviceIntent)
                        }

                        // Insert In-App Notification
                        val parts = extraData?.split("|") ?: emptyList()
                        val volumeMl = parts.getOrNull(0)?.toIntOrNull() ?: 30
                        val method = parts.getOrNull(1) ?: "OGT/Oral"
                        inAppNotificationDao.insertOrUpdate(
                            InAppNotificationEntity(
                                id = 0L,
                                babyId = babyId,
                                category = NotificationCategory.FEEDING_ALARM.name,
                                referenceId = referenceId,
                                title = "Waktunya Pemberian ASI ($volumeMl ml)",
                                message = "Jadwal minum ASI $volumeMl ml via $method untuk $babyName. Pastikan posisi bayi semi-elevasi.",
                                badgeLabel = "ASI $volumeMl ml",
                                badgeColor = NotificationBadgeColor.BLUE.name,
                                timeLabel = "Baru saja",
                                targetInfo = null,
                                isRead = false,
                                isToday = true,
                                primaryActionLabel = "Konfirmasi Selesai",
                                secondaryActionLabel = "Buka Detail",
                                timestampEpoch = System.currentTimeMillis()
                            )
                        )
                    }

                    ReminderType.SKRINING.name -> {
                        // Routine: Standard notification without ForegroundService
                        val parts = extraData?.split("|") ?: emptyList()
                        val categoryBadge = parts.getOrNull(0) ?: "Skrining Bayi"
                        val clinicOrLab = parts.getOrNull(1) ?: "Fasilitas Kesehatan"

                        notificationManager.showScreeningReminderNotification(
                            screeningId = referenceId,
                            title = label,
                            categoryBadge = categoryBadge,
                            clinicOrLab = clinicOrLab,
                            alarmIndex = subIndex
                        )

                        inAppNotificationDao.insertOrUpdate(
                            InAppNotificationEntity(
                                id = 0L,
                                babyId = babyId,
                                category = NotificationCategory.SCREENING_REMINDER.name,
                                referenceId = referenceId,
                                title = "Jadwal Skrining: $label",
                                message = "Waktunya evaluasi klinis $categoryBadge untuk $babyName ($clinicOrLab).",
                                badgeLabel = categoryBadge,
                                badgeColor = NotificationBadgeColor.AMBER.name,
                                timeLabel = "Baru saja",
                                targetInfo = null,
                                isRead = false,
                                isToday = true,
                                primaryActionLabel = "Buka Jadwal",
                                secondaryActionLabel = "Catat Hasil",
                                timestampEpoch = System.currentTimeMillis()
                            )
                        )
                    }

                    ReminderType.FENTON.name -> {
                        // Routine: Standard notification without ForegroundService
                        val title = "Pengingat Grafik Pertumbuhan Fenton"
                        val message = "Waktunya mencatat penimbangan berat badan dan lingkar kepala mingguan $babyName untuk mengevaluasi grafik pertumbuhan Fenton."

                        notificationManager.showFentonWeeklyReminderNotification(title, message)

                        inAppNotificationDao.insertOrUpdate(
                            InAppNotificationEntity(
                                id = 0L,
                                babyId = babyId,
                                category = NotificationCategory.GROWTH_WEIGHT.name,
                                referenceId = referenceId,
                                title = title,
                                message = message,
                                badgeLabel = "Fenton Mingguan",
                                badgeColor = NotificationBadgeColor.BLUE.name,
                                timeLabel = "Baru saja",
                                targetInfo = null,
                                isRead = false,
                                isToday = true,
                                primaryActionLabel = "Catat Pertumbuhan",
                                secondaryActionLabel = null,
                                timestampEpoch = System.currentTimeMillis()
                            )
                        )
                    }
                }

                // 2. Compute accurate nextTriggerMillis without minute drift and update Room
                if (reminderId.startsWith("SNOOZE_")) {
                    // Temporary snooze alarm finishes after firing
                    reminderScheduleDao.deleteById(reminderId)
                } else {
                    val currentSchedule = reminderScheduleDao.getById(reminderId)
                    if (currentSchedule != null) {
                        when (currentSchedule.type) {
                            ReminderType.PMK.name, ReminderType.ASI.name -> {
                                val nextSlot = AlarmSchedulerImpl.computeNextDailyTrigger(
                                    currentSchedule.timeHour,
                                    currentSchedule.timeMinute,
                                    allowToday = false
                                )
                                reminderScheduleDao.updateNextTrigger(reminderId, nextSlot)
                            }

                            ReminderType.SKRINING.name -> {
                                // One-shot milestone: disable after triggered
                                reminderScheduleDao.updateEnabled(reminderId, false)
                            }

                            ReminderType.FENTON.name -> {
                                // Weekly schedule: advance 7 days without drift
                                val nextSlot = AlarmSchedulerImpl.computeNextWeeklyTrigger(
                                    currentSchedule.timeHour,
                                    currentSchedule.timeMinute,
                                    startFromEpoch = currentSchedule.nextTriggerMillis
                                )
                                reminderScheduleDao.updateNextTrigger(reminderId, nextSlot)
                            }
                        }
                    }
                }

                // 3. Sequential Chaining: re-invoke syncAndScheduleNextAlarm to schedule the single next alarm
                alarmScheduler.syncAndScheduleNextAlarm()

            } catch (_: Exception) {
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_TRIGGER_REMINDER = "com.kangurusiaga.app.action.TRIGGER_REMINDER"

        const val EXTRA_REMINDER_ID = "extra_reminder_id"
        const val EXTRA_TYPE = "extra_type"
        const val EXTRA_REFERENCE_ID = "extra_reference_id"
        const val EXTRA_SUB_INDEX = "extra_sub_index"
        const val EXTRA_LABEL = "extra_label"
        const val EXTRA_TARGET_MILLIS = "extra_target_millis"
        const val EXTRA_EXTRA_DATA = "extra_extra_data"
    }
}
