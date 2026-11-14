package com.kangurusiaga.app.core.notification

import android.content.Context
import com.kangurusiaga.app.data.local.dao.BabyDao
import com.kangurusiaga.app.domain.model.PrematureScreening
import com.kangurusiaga.app.domain.repository.PrematureScreeningRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScreeningReminderSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val screeningRepository: PrematureScreeningRepository,
    private val babyDao: BabyDao,
    private val alarmScheduler: AlarmScheduler
) : ScreeningReminderScheduler {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun schedule(screening: PrematureScreening) {
        scope.launch {
            alarmScheduler.syncAndScheduleNextAlarm()
        }
    }

    override fun cancel(screeningId: Long, alarmIndex: Int) {
        scope.launch {
            alarmScheduler.syncAndScheduleNextAlarm()
        }
    }

    override fun cancelAll(screeningId: Long) {
        scope.launch {
            alarmScheduler.syncAndScheduleNextAlarm()
        }
    }

    override fun rescheduleAllActive() {
        scope.launch {
            alarmScheduler.syncAndScheduleNextAlarm()
        }
    }
}
