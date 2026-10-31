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
import javax.inject.Inject

@AndroidEntryPoint
class ScreeningAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationManager: KanguruNotificationManager

    @Inject
    lateinit var inAppNotificationDao: InAppNotificationDao

    @Inject
    lateinit var babyDao: BabyDao

    override fun onReceive(context: Context, intent: Intent) {
        val screeningId = intent.getLongExtra(EXTRA_SCREENING_ID, 0L)
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Pemeriksaan Skrining"
        val categoryBadge = intent.getStringExtra(EXTRA_CATEGORY) ?: "Skrining Bayi"
        val clinicOrLab = intent.getStringExtra(EXTRA_CLINIC) ?: "Fasilitas Kesehatan"
        val alarmIndex = intent.getIntExtra(EXTRA_ALARM_INDEX, 1)

        // 1. Show Android Status Bar Notification
        notificationManager.showScreeningReminderNotification(
            screeningId = screeningId,
            title = title,
            categoryBadge = categoryBadge,
            clinicOrLab = clinicOrLab,
            alarmIndex = alarmIndex
        )

        // 2. Insert In-App Notification into local database inbox
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val baby = babyDao.getActiveBabyOnce() ?: babyDao.getFirstBaby()
                val babyId = baby?.id ?: 1L
                val babyName = baby?.name ?: "Si Kecil"

                inAppNotificationDao.insertOrUpdate(
                    InAppNotificationEntity(
                        id = 0L,
                        babyId = babyId,
                        category = NotificationCategory.SCREENING_REMINDER.name,
                        referenceId = screeningId,
                        title = "Jadwal Skrining: $title",
                        message = "Waktunya evaluasi klinis $categoryBadge untuk $babyName ($clinicOrLab). Lakukan penapisan tepat waktu demi tumbuh kembang optimal.",
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
            } catch (_: Exception) {
                // Silently handle error
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val EXTRA_SCREENING_ID = "extra_screening_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_CLINIC = "extra_clinic"
        const val EXTRA_ALARM_INDEX = "extra_alarm_index"
    }
}

