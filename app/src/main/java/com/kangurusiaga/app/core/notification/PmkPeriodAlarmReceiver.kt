package com.kangurusiaga.app.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kangurusiaga.app.core.common.PmkTimerManager
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
class PmkPeriodAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationManager: KanguruNotificationManager

    @Inject
    lateinit var inAppNotificationDao: InAppNotificationDao

    @Inject
    lateinit var babyDao: BabyDao

    @Inject
    lateinit var timerManager: PmkTimerManager

    override fun onReceive(context: Context, intent: Intent) {
        val periodNumber = intent.getIntExtra("period_number", 1)

        notificationManager.showPmkPeriodCompletedNotification(periodNumber)

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Auto finish session if still running
                timerManager.finishSessionOnPeriodComplete(periodNumber)

                val baby = babyDao.getActiveBabyOnce() ?: babyDao.getFirstBaby()
                val babyId = baby?.id ?: 1L

                inAppNotificationDao.insertOrUpdate(
                    InAppNotificationEntity(
                        id = 0L,
                        babyId = babyId,
                        category = NotificationCategory.PMK_REMINDER.name,
                        referenceId = System.currentTimeMillis(),
                        title = "Periode $periodNumber PMK Selesai (8 Jam)",
                        message = "Sesi 8 jam Periode $periodNumber telah selesai secara otomatis. Dekap dan pantau si kecil, lalu mulai periode berikutnya saat siap.",
                        badgeLabel = "Periode $periodNumber Tuntas",
                        badgeColor = NotificationBadgeColor.GREEN.name,
                        timeLabel = "Baru saja",
                        targetInfo = null,
                        isRead = false,
                        isToday = true,
                        primaryActionLabel = "Mulai Periode Berikutnya",
                        secondaryActionLabel = null,
                        timestampEpoch = System.currentTimeMillis()
                    )
                )
            } catch (_: Exception) {
            } finally {
                pendingResult.finish()
            }
        }
    }
}
