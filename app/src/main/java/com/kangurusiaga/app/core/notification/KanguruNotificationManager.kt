package com.kangurusiaga.app.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KanguruNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_PMK_ID = "channel_pmk_timer"
        const val CHANNEL_PMK_NAME = "Timer PMK & Perawatan"
        const val CHANNEL_FEEDING_ID = "channel_feeding_reminder"
        const val CHANNEL_FEEDING_NAME = "Jadwal Pemberian ASI"
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pmkChannel = NotificationChannel(
                CHANNEL_PMK_ID,
                CHANNEL_PMK_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi sesi Metode Kanguru"
            }

            val feedingChannel = NotificationChannel(
                CHANNEL_FEEDING_ID,
                CHANNEL_FEEDING_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Pengingat jadwal minum ASI/OGT"
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(pmkChannel)
            notificationManager.createNotificationChannel(feedingChannel)
        }
    }

    fun showNotification(channelId: String, notificationId: Int, title: String, message: String) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Handled when notification permission is not yet granted
        }
    }
}
