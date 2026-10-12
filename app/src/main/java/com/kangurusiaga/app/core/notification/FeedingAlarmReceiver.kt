package com.kangurusiaga.app.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kangurusiaga.app.data.local.dao.BabyDao
import com.kangurusiaga.app.data.local.dao.InAppNotificationDao
import com.kangurusiaga.app.data.local.entity.InAppNotificationEntity
import com.kangurusiaga.app.domain.model.NotificationBadgeColor
import com.kangurusiaga.app.domain.model.NotificationCategory
import com.kangurusiaga.app.domain.model.RepeatType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class FeedingAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationManager: KanguruNotificationManager

    @Inject
    lateinit var reminderScheduler: FeedingReminderScheduler

    @Inject
    lateinit var inAppNotificationDao: InAppNotificationDao

    @Inject
    lateinit var babyDao: BabyDao

    override fun onReceive(context: Context, intent: Intent) {
        val scheduleId = intent.getLongExtra(EXTRA_SCHEDULE_ID, 0L)
        val hour = intent.getIntExtra(EXTRA_HOUR, 0)
        val minute = intent.getIntExtra(EXTRA_MINUTE, 0)
        val volumeMl = intent.getIntExtra(EXTRA_VOLUME_ML, 30)
        val methodDisplayName = intent.getStringExtra(EXTRA_METHOD) ?: "OGT/NGT"
        val offsetMinutes = intent.getIntExtra(EXTRA_OFFSET_MINUTES, 0)
        val repeatType = intent.getStringExtra(EXTRA_REPEAT_TYPE) ?: RepeatType.DAILY.name

        // 1. Show system status bar notification
        notificationManager.showFeedingReminderNotification(
            scheduleId = scheduleId,
            volumeMl = volumeMl,
            methodDisplayName = methodDisplayName
        )

        // 2. Insert into In-App Notifications table so it appears in NotificationScreen
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val baby = babyDao.getActiveBabyOnce() ?: babyDao.getFirstBaby()
                val babyId = baby?.id ?: 1L
                val babyName = baby?.name ?: "si kecil"
                val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)

                inAppNotificationDao.insertOrUpdate(
                    InAppNotificationEntity(
                        id = 0L,
                        babyId = babyId,
                        category = NotificationCategory.FEEDING_ALARM.name,
                        referenceId = scheduleId,
                        title = "Waktunya Pemberian ASI (OGT/Oral)",
                        message = "Jadwal minum ASI $volumeMl ml untuk $babyName. Pastikan posisi bayi semi-elevasi 30°–45° untuk keamanan pencernaan.",
                        badgeLabel = "Jadwal ASI $formattedTime",
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
            } catch (_: Exception) {
                // Silently handle error
            } finally {
                pendingResult.finish()
            }
        }

        // 3. Reschedule for next day if DAILY
        if (repeatType == RepeatType.DAILY.name) {
            reminderScheduler.scheduleNextOccurrence(
                scheduleId = scheduleId,
                hour = hour,
                minute = minute,
                volumeMl = volumeMl,
                methodDisplayName = methodDisplayName,
                offsetMinutes = 0
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
