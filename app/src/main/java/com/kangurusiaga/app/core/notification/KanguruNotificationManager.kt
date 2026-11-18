package com.kangurusiaga.app.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kangurusiaga.app.MainActivity
import com.kangurusiaga.app.R
import com.kangurusiaga.app.domain.repository.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KanguruNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferencesRepository: UserPreferencesRepository? = null
) {
    private var areAppNotificationsEnabled: Boolean = true

    companion object {
        const val CHANNEL_PMK_ID = "channel_pmk_timer"
        const val CHANNEL_PMK_NAME = "Timer PMK & Pengingat"
        const val CHANNEL_FEEDING_ID = "channel_feeding_reminder"
        const val CHANNEL_FEEDING_NAME = "Jadwal Pemberian ASI"
        const val CHANNEL_SCREENING_ID = "channel_screening_reminder"
        const val CHANNEL_SCREENING_NAME = "Jadwal & Evaluasi Skrining"
        const val CHANNEL_ALARM_PMK_ID = "channel_alarm_pmk_v3"
        const val CHANNEL_ALARM_PMK_NAME = "Alarm Pengingat PMK"
        const val CHANNEL_ALARM_ASI_ID = "channel_alarm_asi_v3"
        const val CHANNEL_ALARM_ASI_NAME = "Alarm Pengingat ASI"
        const val CHANNEL_ROUTINE_ID = "channel_routine_reminder"
        const val CHANNEL_ROUTINE_NAME = "Pengingat Rutin (Skrining & Fenton)"
        const val CHANNEL_MISSED_ID = "channel_missed_reminder"
        const val CHANNEL_MISSED_NAME = "Pemberitahuan Jadwal Terlewat"

        const val NOTIFICATION_ID_PMK_REMINDER = 1001
        const val NOTIFICATION_ID_PMK_TIMER = 1002
        const val NOTIFICATION_ID_PMK_PAUSE_ALERT = 1003
        const val NOTIFICATION_ID_PMK_PERIOD_COMPLETED = 1004
        const val NOTIFICATION_ID_FEEDING_BASE = 2000
        const val NOTIFICATION_ID_SCREENING_BASE = 3000
        const val NOTIFICATION_ID_ALARM_SOUND = 4001
        const val NOTIFICATION_ID_FENTON = 5001
        const val NOTIFICATION_ID_MISSED_BASE = 6000
        const val NOTIFICATION_ID_DELAYED_BASE = 7000

        private const val REQUEST_CODE_DISMISS_ALARM = 9101
        private const val REQUEST_CODE_SNOOZE_ALARM = 9102
    }

    init {
        createNotificationChannels()
        userPreferencesRepository?.let { repo ->
            CoroutineScope(Dispatchers.IO).launch {
                repo.notificationsEnabled.collect { enabled ->
                    areAppNotificationsEnabled = enabled
                }
            }
        }
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
                enableVibration(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            val screeningChannel = NotificationChannel(
                CHANNEL_SCREENING_ID,
                CHANNEL_SCREENING_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Pengingat jadwal penapisan klinis bayi prematur (USG Kepala, ROP, OAE, Tiroid)"
                enableVibration(true)
            }

            val alarmAudioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val pmkAlarmChannel = NotificationChannel(
                CHANNEL_ALARM_PMK_ID,
                CHANNEL_ALARM_PMK_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alarm bersuara khusus untuk pengingat penting sesi PMK"
                enableVibration(true)
                setSound(
                    Uri.parse("android.resource://${context.packageName}/${R.raw.sound_alarm_pmk}"),
                    alarmAudioAttributes
                )
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            val asiAlarmChannel = NotificationChannel(
                CHANNEL_ALARM_ASI_ID,
                CHANNEL_ALARM_ASI_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alarm bersuara khusus untuk pengingat penting jadwal minum ASI"
                enableVibration(true)
                setSound(
                    Uri.parse("android.resource://${context.packageName}/${R.raw.sound_alarm_asi}"),
                    alarmAudioAttributes
                )
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            val routineChannel = NotificationChannel(
                CHANNEL_ROUTINE_ID,
                CHANNEL_ROUTINE_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Pengingat rutin skrining dan pengukuran grafik pertumbuhan mingguan Fenton"
                enableVibration(true)
            }

            val missedChannel = NotificationChannel(
                CHANNEL_MISSED_ID,
                CHANNEL_MISSED_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Pemberitahuan informatif jika ada jadwal pengingat yang terlewat saat perangkat tidak aktif"
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            try {
                notificationManager.deleteNotificationChannel("channel_alarm_urgent")
                notificationManager.deleteNotificationChannel("channel_alarm_urgent_v2")
            } catch (_: Exception) {
            }
            notificationManager.createNotificationChannel(pmkChannel)
            notificationManager.createNotificationChannel(feedingChannel)
            notificationManager.createNotificationChannel(screeningChannel)
            notificationManager.createNotificationChannel(pmkAlarmChannel)
            notificationManager.createNotificationChannel(asiAlarmChannel)
            notificationManager.createNotificationChannel(routineChannel)
            notificationManager.createNotificationChannel(missedChannel)
        }
    }

    fun showUrgentAlarmNotification(
        reminderId: String,
        type: String,
        label: String
    ) {
        if (!areAppNotificationsEnabled) return

        val isPmk = type.equals("PMK", ignoreCase = true)
        val channelId = if (isPmk) CHANNEL_ALARM_PMK_ID else CHANNEL_ALARM_ASI_ID
        val soundResId = if (isPmk) R.raw.sound_alarm_pmk else R.raw.sound_alarm_asi
        val soundUri = Uri.parse("android.resource://${context.packageName}/$soundResId")

        val navigateDestination = if (isPmk) "pmk_timer" else "feeding"
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", navigateDestination)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_ALARM_SOUND,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dismissIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_DISMISS_ALARM
            putExtra(AlarmReceiver.EXTRA_REMINDER_ID, reminderId)
            putExtra(AlarmReceiver.EXTRA_TYPE, type)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_DISMISS_ALARM,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_SNOOZE_ALARM
            putExtra(AlarmReceiver.EXTRA_REMINDER_ID, reminderId)
            putExtra(AlarmReceiver.EXTRA_TYPE, type)
            putExtra(AlarmReceiver.EXTRA_LABEL, label)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_SNOOZE_ALARM,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (isPmk) {
            "Waktunya Sesi PMK \uD83E\uDD98"
        } else {
            "Waktunya Pemberian ASI \uD83C\uDF7C"
        }

        val message = if (isPmk) {
            "Sesi kontak kulit ($label). Ketuk untuk buka timer PMK."
        } else {
            "Jadwal minum ASI ($label). Ketuk untuk lihat detail pemberian."
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_kangaroo_mascot)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSound(soundUri)
            .setDefaults(NotificationCompat.DEFAULT_VIBRATE or NotificationCompat.DEFAULT_LIGHTS)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .addAction(0, context.getString(R.string.alarm_action_dismiss), dismissPendingIntent)
            .addAction(0, context.getString(R.string.alarm_action_snooze), snoozePendingIntent)

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_ALARM_SOUND, builder.build())
        } catch (_: SecurityException) {
            // Handled when notification permission is not granted
        }
    }

    fun cancelUrgentAlarmNotification() {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID_ALARM_SOUND)
    }

    fun showPmkReminderNotification(label: String, targetMinutes: Int) {
        if (!areAppNotificationsEnabled) return

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
        if (!areAppNotificationsEnabled) return

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

    fun showFeedingReminderNotification(scheduleId: Long, volumeMl: Int, methodDisplayName: String) {
        if (!areAppNotificationsEnabled) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "feeding")
        }

        val notificationId = NOTIFICATION_ID_FEEDING_BASE
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "Waktunya Pemberian ASI \uD83C\uDF7C"
        val message = "Jadwal minum ASI $volumeMl ml via $methodDisplayName."

        val builder = NotificationCompat.Builder(context, CHANNEL_FEEDING_ID)
            .setSmallIcon(R.drawable.ic_kangaroo_mascot)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Handled when notification permission is not granted
        }
    }

    fun showPmkPauseLimitExceededNotification() {
        if (!areAppNotificationsEnabled) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "pmk_timer")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_PMK_PAUSE_ALERT,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "Waktu Jeda PMK Sudah 15 Menit! 🦘"
        val message = "Yuk Bunda/Ayah, segera lanjutkan kontak kulit PMK agar suhu tubuh dan kenyamanan si kecil tetap terjaga stabil."

        val builder = NotificationCompat.Builder(context, CHANNEL_PMK_ID)
            .setSmallIcon(R.drawable.ic_kangaroo_mascot)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_PMK_PAUSE_ALERT, builder.build())
        } catch (_: SecurityException) {
            // Handled when notification permission is not granted
        }
    }

    fun cancelPmkPauseLimitExceededNotification() {
        try {
            NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID_PMK_PAUSE_ALERT)
        } catch (_: SecurityException) {
            // Handled safely
        }
    }

    fun showPmkPeriodCompletedNotification(periodNumber: Int) {
        if (!areAppNotificationsEnabled) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "pmk_timer")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_PMK_PERIOD_COMPLETED,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "Target Periode $periodNumber PMK Selesai Otomatis! 🎉"
        val message = "Hebat Bunda & Ayah! Target 8 jam Periode $periodNumber telah selesai otomatis. Buka aplikasi dan klik 'Mulai PMK' untuk melanjutkan periode berikutnya."

        val builder = NotificationCompat.Builder(context, CHANNEL_PMK_ID)
            .setSmallIcon(R.drawable.ic_kangaroo_mascot)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_PMK_PERIOD_COMPLETED, builder.build())
        } catch (_: SecurityException) {
            // Handled when notification permission is not granted
        }
    }

    fun cancelPmkPeriodCompletedNotification() {
        try {
            NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID_PMK_PERIOD_COMPLETED)
        } catch (_: SecurityException) {
            // Handled safely
        }
    }

    fun showScreeningReminderNotification(
        screeningId: Long,
        title: String,
        categoryBadge: String,
        clinicOrLab: String,
        alarmIndex: Int = 1
    ) {
        if (!areAppNotificationsEnabled) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "screening_schedule")
        }

        val notificationId = (NOTIFICATION_ID_SCREENING_BASE + (screeningId * 10 + alarmIndex) % 1000).toInt()
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notifTitle = "Pengingat Skrining: $title 📋"
        val message = "Jadwal evaluasi $categoryBadge si kecil ($clinicOrLab). Segera konfirmasi jadwal kontrol dengan dokter/faskes."

        val builder = NotificationCompat.Builder(context, CHANNEL_SCREENING_ID)
            .setSmallIcon(R.drawable.ic_kangaroo_mascot)
            .setContentTitle(notifTitle)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Handled when notification permission is not granted
        }
    }

    fun showFentonWeeklyReminderNotification(title: String, message: String) {
        if (!areAppNotificationsEnabled) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "growth")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_FENTON,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ROUTINE_ID)
            .setSmallIcon(R.drawable.ic_kangaroo_mascot)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_FENTON, builder.build())
        } catch (_: SecurityException) {
        }
    }

    fun showMissedReminderNotification(title: String, message: String, navigateDestination: String = "notification") {
        if (!areAppNotificationsEnabled) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", navigateDestination)
        }

        val notificationId = NOTIFICATION_ID_MISSED_BASE + (System.currentTimeMillis() % 1000).toInt()
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_MISSED_ID)
            .setSmallIcon(R.drawable.ic_kangaroo_mascot)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {
        }
    }

    fun showDelayedReminderNotification(
        title: String,
        message: String,
        navigateDestination: String = "screening_schedule",
        notificationId: Int = NOTIFICATION_ID_DELAYED_BASE
    ) {
        if (!areAppNotificationsEnabled) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", navigateDestination)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ROUTINE_ID)
            .setSmallIcon(R.drawable.ic_kangaroo_mascot)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {
        }
    }
}
