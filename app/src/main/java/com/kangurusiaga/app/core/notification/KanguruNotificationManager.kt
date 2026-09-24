package com.kangurusiaga.app.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kangurusiaga.app.MainActivity
import com.kangurusiaga.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KanguruNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_PMK_ID = "channel_pmk_timer"
        const val CHANNEL_PMK_NAME = "Timer PMK & Pengingat"
        const val CHANNEL_FEEDING_ID = "channel_feeding_reminder"
        const val CHANNEL_FEEDING_NAME = "Jadwal Pemberian ASI"

        const val NOTIFICATION_ID_PMK_REMINDER = 1001
        const val NOTIFICATION_ID_PMK_TIMER = 1002
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
                description = "Pengingat dan status sesi Perawatan Metode Kanguru (PMK)"
                enableVibration(true)
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

    fun showPmkReminderNotification(label: String, targetMinutes: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "pmk_timer")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_PMK_REMINDER,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "Waktunya Sesi PMK ($label) 🦘"
        val message = "Yuk Bunda/Ayah, mulai sesi kontak kulit ke kulit selama $targetMinutes menit untuk kehangatan si kecil."

        val builder = NotificationCompat.Builder(context, CHANNEL_PMK_ID)
            .setSmallIcon(R.drawable.ic_kangaroo_mascot)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_PMK_REMINDER, builder.build())
        } catch (_: SecurityException) {
            // Handled when notification permission is not granted
        }
    }

    fun showNotification(channelId: String, notificationId: Int, title: String, message: String) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_kangaroo_mascot)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Handled when notification permission is not granted
        }
    }
}
