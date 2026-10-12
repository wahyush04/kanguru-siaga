package com.kangurusiaga.app.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kangurusiaga.app.data.local.dao.BabyDao
import com.kangurusiaga.app.data.local.dao.InAppNotificationDao
import com.kangurusiaga.app.data.local.entity.InAppNotificationEntity
import com.kangurusiaga.app.domain.model.NotificationBadgeColor
import com.kangurusiaga.app.domain.model.NotificationCategory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class PmkAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationManager: KanguruNotificationManager

    @Inject
    lateinit var reminderScheduler: PmkReminderScheduler

    @Inject
    lateinit var inAppNotificationDao: InAppNotificationDao

    @Inject
    lateinit var babyDao: BabyDao

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(EXTRA_REMINDER_ID, 0L)
        val label = intent.getStringExtra(EXTRA_LABEL) ?: "PMK"
        val targetMinutes = intent.getIntExtra(EXTRA_TARGET_MINUTES, 60)
        val hour = intent.getIntExtra(EXTRA_HOUR, 8)
        val minute = intent.getIntExtra(EXTRA_MINUTE, 0)

        // Show system status bar notification
        notificationManager.showPmkReminderNotification(label, targetMinutes)

        // Insert into In-App Notification database
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val baby = babyDao.getActiveBabyOnce() ?: babyDao.getFirstBaby()
                val babyId = baby?.id ?: 1L
                val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)

                inAppNotificationDao.insertOrUpdate(
                    InAppNotificationEntity(
                        id = 0L,
                        babyId = babyId,
                        category = NotificationCategory.PMK_REMINDER.name,
                        referenceId = reminderId,
                        title = "Lanjutkan PMK Kontinu Mandiri di Rumah",
                        message = "Yuk Bunda/Ayah, mulai sesi kontak kulit ke kulit ($label) selama $targetMinutes menit untuk kenyamanan dan kehangatan si kecil.",
                        badgeLabel = "PMK ($formattedTime)",
                        badgeColor = NotificationBadgeColor.PINK.name,
                        timeLabel = "Baru saja",
                        targetInfo = null,
                        isRead = false,
                        isToday = true,
                        primaryActionLabel = "Mulai PMK Kontinu",
                        secondaryActionLabel = null,
                        timestampEpoch = System.currentTimeMillis()
                    )
                )
            } catch (_: Exception) {
            } finally {
                pendingResult.finish()
            }
        }

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
