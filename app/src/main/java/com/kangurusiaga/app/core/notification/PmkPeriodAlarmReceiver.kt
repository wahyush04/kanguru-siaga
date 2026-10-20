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
                // Period milestone reached - timer continues running!
                timerManager.onPeriodTargetReached(periodNumber)

                val baby = babyDao.getActiveBabyOnce() ?: babyDao.getFirstBaby()
                val babyId = baby?.id ?: 1L

                inAppNotificationDao.insertOrUpdate(
                    InAppNotificationEntity(
                        id = 0L,
                        babyId = babyId,
                        category = NotificationCategory.PMK_REMINDER.name,
                        referenceId = System.currentTimeMillis(),
                        title = "Target Periode $periodNumber PMK Tercapai! 🎉",
                        message = "Hebat Bunda & Ayah! Target 8 jam Periode $periodNumber telah tercapai. Pemantauan PMK terus berlanjut tanpa henti.",
                        badgeLabel = "Periode $periodNumber Tuntas",
                        badgeColor = NotificationBadgeColor.GREEN.name,
                        timeLabel = "Baru saja",
                        targetInfo = null,
                        isRead = false,
                        isToday = true,
                        primaryActionLabel = "Lihat Timer PMK",
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
