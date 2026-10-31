package com.kangurusiaga.app.core.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.kangurusiaga.app.data.local.dao.BabyDao
import com.kangurusiaga.app.domain.model.PrematureScreening
import com.kangurusiaga.app.domain.model.ScreeningType
import com.kangurusiaga.app.domain.repository.PrematureScreeningRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScreeningReminderSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val screeningRepository: PrematureScreeningRepository,
    private val babyDao: BabyDao
) : ScreeningReminderScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    override fun schedule(screening: PrematureScreening) {
        if (screening.isCompleted) {
            cancelAll(screening.id)
            return
        }

        // Alarm 1 (Primary Target Date)
        if (screening.isAlarmActive && screening.targetDateEpoch > 0) {
            val triggerTime = computeTriggerTime(screening.targetDateEpoch)
            if (triggerTime != null) {
                val pendingIntent = createPendingIntent(screening, alarmIndex = 1)
                scheduleAlarm(triggerTime, pendingIntent)
            } else {
                cancel(screening.id, alarmIndex = 1)
            }
        } else {
            cancel(screening.id, alarmIndex = 1)
        }

        // Alarm 2 (Dual Alarm for Hearing / OAE & BERA)
        if (screening.type == ScreeningType.HEARING_OAE && screening.targetDateEpoch2 != null) {
            if (screening.isAlarm2Active && screening.targetDateEpoch2 > 0) {
                val triggerTime2 = computeTriggerTime(screening.targetDateEpoch2)
                if (triggerTime2 != null) {
                    val pendingIntent2 = createPendingIntent(screening, alarmIndex = 2)
                    scheduleAlarm(triggerTime2, pendingIntent2)
                } else {
                    cancel(screening.id, alarmIndex = 2)
                }
            } else {
                cancel(screening.id, alarmIndex = 2)
            }
        }
    }

    override fun cancel(screeningId: Long, alarmIndex: Int) {
        val intent = Intent(context, ScreeningAlarmReceiver::class.java)
        val requestCode = (screeningId * 10 + alarmIndex).toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    override fun cancelAll(screeningId: Long) {
        cancel(screeningId, alarmIndex = 1)
        cancel(screeningId, alarmIndex = 2)
    }

    override fun rescheduleAllActive() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val baby = babyDao.getActiveBabyOnce() ?: babyDao.getFirstBaby()
                if (baby != null) {
                    val list = screeningRepository.getScreeningsForBaby(baby.id).firstOrNull() ?: emptyList()
                    list.forEach { screening ->
                        schedule(screening)
                    }
                }
            } catch (_: Exception) {
                // Silently handle error
            }
        }
    }

    private fun computeTriggerTime(targetEpoch: Long): Long? {
        if (targetEpoch <= 0) return null

        val now = System.currentTimeMillis()
        val targetCal = Calendar.getInstance().apply {
            timeInMillis = targetEpoch
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val triggerMillis = targetCal.timeInMillis
        if (triggerMillis > now) {
            return triggerMillis
        }

        val todayCal = Calendar.getInstance()
        val isSameDay = targetCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                targetCal.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR)

        if (isSameDay) {
            return now + 15_000L // 15 seconds from now if scheduled for today
        }

        return null
    }

    private fun createPendingIntent(screening: PrematureScreening, alarmIndex: Int): PendingIntent {
        val intent = Intent(context, ScreeningAlarmReceiver::class.java).apply {
            putExtra(ScreeningAlarmReceiver.EXTRA_SCREENING_ID, screening.id)
            putExtra(ScreeningAlarmReceiver.EXTRA_TITLE, screening.title)
            putExtra(ScreeningAlarmReceiver.EXTRA_CATEGORY, screening.categoryBadge)
            putExtra(ScreeningAlarmReceiver.EXTRA_CLINIC, screening.clinicOrLab)
            putExtra(ScreeningAlarmReceiver.EXTRA_ALARM_INDEX, alarmIndex)
        }

        val requestCode = (screening.id * 10 + alarmIndex).toInt()
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        return PendingIntent.getBroadcast(context, requestCode, intent, flags)
    }

    private fun scheduleAlarm(triggerAtMillis: Long, pendingIntent: PendingIntent) {
        if (alarmManager == null) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        } else {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }
}

