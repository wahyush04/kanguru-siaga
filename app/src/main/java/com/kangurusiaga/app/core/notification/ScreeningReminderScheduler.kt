package com.kangurusiaga.app.core.notification

import com.kangurusiaga.app.domain.model.PrematureScreening

interface ScreeningReminderScheduler {
    fun schedule(screening: PrematureScreening)
    fun cancel(screeningId: Long, alarmIndex: Int = 1)
    fun cancelAll(screeningId: Long)
    fun rescheduleAllActive()
}

