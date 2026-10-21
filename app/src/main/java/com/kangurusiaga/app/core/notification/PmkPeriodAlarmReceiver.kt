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

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Period milestone reached - timer auto-finishes session automatically!
                timerManager.autoFinishPeriod(periodNumber)

                val baby = babyDao.getActiveBabyOnce() ?: babyDao.getFirstBaby()
                val babyId = baby?.id ?: 1L

                inAppNotificationDao.insertOrUpdate(
                    InAppNotificationEntity(
                        id = 0L,
                        babyId = babyId,
                        category = NotificationCategory.PMK_REMINDER.name,
                        referenceId = System.currentTimeMillis(),
                        title = "Target Periode $periodNumber PMK Selesai Otomatis! 🎉",
                        message = "Hebat Bunda & Ayah! Target 8 jam Periode $periodNumber telah selesai otomatis. Buka aplikasi dan klik 'Mulai PMK' untuk melanjutkan periode berikutnya.",
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
