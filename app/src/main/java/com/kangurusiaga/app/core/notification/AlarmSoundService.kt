package com.kangurusiaga.app.core.notification

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.kangurusiaga.app.MainActivity
import com.kangurusiaga.app.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmSoundService : Service() {

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    private var mediaPlayer: MediaPlayer? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private val handler = Handler(Looper.getMainLooper())
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    private var currentReminderId: String = ""
    private var currentType: String = "PMK"
    private var currentLabel: String = ""

    private val autoStopRunnable = Runnable {
        stopAlarm()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        val powerManager = getSystemService(Context.POWER_SERVICE) as? PowerManager
        wakeLock = powerManager?.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "KanguruSiaga:AlarmSoundWakeLock"
        )?.apply {
            setReferenceCounted(false)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: ACTION_STOP_ALARM
        currentReminderId = intent?.getStringExtra(EXTRA_REMINDER_ID) ?: ""
        currentType = intent?.getStringExtra(EXTRA_TYPE) ?: "PMK"
        currentLabel = intent?.getStringExtra(EXTRA_LABEL) ?: "Pengingat"

        when (action) {
            ACTION_START_ALARM -> {
                startAlarm(intent)
            }
            ACTION_STOP_ALARM -> {
                stopAlarm()
            }
            ACTION_SNOOZE_ALARM -> {
                snoozeAlarm()
            }
            else -> {
                stopAlarm()
            }
        }
        return START_NOT_STICKY
    }

    private fun startAlarm(intent: Intent?) {
        // 1. Acquire partial wake lock for safety (max 5 minutes)
        try {
            wakeLock?.acquire(AUTO_STOP_DELAY_MS)
        } catch (_: Exception) {
        }

        // 2. Schedule auto-stop after 5 minutes to protect battery
        handler.removeCallbacks(autoStopRunnable)
        handler.postDelayed(autoStopRunnable, AUTO_STOP_DELAY_MS)

        // 3. Start audio playback with USAGE_ALARM
        startAudioPlayback()

        // 4. Build and start foreground notification with "Matikan" & "Tunda 10 Menit"
        val notification = buildForegroundNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                KanguruNotificationManager.NOTIFICATION_ID_ALARM_SOUND,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(KanguruNotificationManager.NOTIFICATION_ID_ALARM_SOUND, notification)
        }
    }

    private fun startAudioPlayback() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null

            mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound_1)?.apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                isLooping = true
                start()
            }
        } catch (_: Exception) {
        }
    }

    private fun buildForegroundNotification(): android.app.Notification {
        val navigateDestination = if (currentType == "PMK") "pmk_timer" else "feeding"
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", navigateDestination)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            this,
            KanguruNotificationManager.NOTIFICATION_ID_ALARM_SOUND,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Stop Action ("Matikan")
        val stopIntent = Intent(this, AlarmSoundService::class.java).apply {
            action = ACTION_STOP_ALARM
            putExtra(EXTRA_REMINDER_ID, currentReminderId)
            putExtra(EXTRA_TYPE, currentType)
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            REQUEST_CODE_STOP,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Snooze Action ("Tunda 10 Menit")
        val snoozeIntent = Intent(this, AlarmSoundService::class.java).apply {
            action = ACTION_SNOOZE_ALARM
            putExtra(EXTRA_REMINDER_ID, currentReminderId)
            putExtra(EXTRA_TYPE, currentType)
            putExtra(EXTRA_LABEL, currentLabel)
        }
        val snoozePendingIntent = PendingIntent.getService(
            this,
            REQUEST_CODE_SNOOZE,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (currentType == "PMK") {
            "Waktunya Sesi PMK 🦘"
        } else {
            "Waktunya Pemberian ASI 🍼"
        }

        val message = if (currentType == "PMK") {
            "Sesi kontak kulit ($currentLabel). Ketuk untuk buka timer PMK."
        } else {
            "Jadwal minum ASI ($currentLabel). Ketuk untuk lihat detail pemberian."
        }

        return NotificationCompat.Builder(this, KanguruNotificationManager.CHANNEL_ALARM_URGENT_ID)
            .setSmallIcon(R.drawable.ic_kangaroo_mascot)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(contentPendingIntent)
            .setOngoing(true)
            .addAction(0, getString(R.string.alarm_action_dismiss), stopPendingIntent)
            .addAction(0, getString(R.string.alarm_action_snooze), snoozePendingIntent)
            .build()
    }

    private fun snoozeAlarm() {
        val reminderIdToSnooze = currentReminderId
        stopAudioAndWakeLock()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()

        if (reminderIdToSnooze.isNotBlank()) {
            serviceScope.launch {
                try {
                    alarmScheduler.snoozeReminder(reminderIdToSnooze, SNOOZE_DURATION_MINUTES)
                } catch (_: Exception) {
                }
            }
        }
    }

    private fun stopAlarm() {
        stopAudioAndWakeLock()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun stopAudioAndWakeLock() {
        handler.removeCallbacks(autoStopRunnable)
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
        } catch (_: Exception) {
        }
        try {
            mediaPlayer?.release()
        } catch (_: Exception) {
        }
        mediaPlayer = null

        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        } catch (_: Exception) {
        }
    }

    override fun onDestroy() {
        stopAudioAndWakeLock()
        super.onDestroy()
    }

    companion object {
        const val ACTION_START_ALARM = "com.kangurusiaga.app.action.START_ALARM"
        const val ACTION_STOP_ALARM = "com.kangurusiaga.app.action.STOP_ALARM"
        const val ACTION_SNOOZE_ALARM = "com.kangurusiaga.app.action.SNOOZE_ALARM"

        const val EXTRA_REMINDER_ID = "extra_reminder_id"
        const val EXTRA_TYPE = "extra_type"
        const val EXTRA_LABEL = "extra_label"
        const val EXTRA_EXTRA_DATA = "extra_extra_data"

        private const val REQUEST_CODE_STOP = 9101
        private const val REQUEST_CODE_SNOOZE = 9102

        private const val AUTO_STOP_DELAY_MS = 5 * 60 * 1000L // 5 minutes
        private const val SNOOZE_DURATION_MINUTES = 10
    }
}
