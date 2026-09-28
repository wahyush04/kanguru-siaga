package com.kangurusiaga.app.fake

import com.kangurusiaga.app.domain.model.FeedingMethod
import com.kangurusiaga.app.domain.model.FeedingSchedule
import com.kangurusiaga.app.domain.model.RepeatType
import com.kangurusiaga.app.domain.repository.FeedingScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeFeedingScheduleRepository : FeedingScheduleRepository {

    private val schedulesFlow = MutableStateFlow<Map<Long, FeedingSchedule>>(emptyMap())
    private var nextId = 1L

    override fun observeSchedules(): Flow<List<FeedingSchedule>> {
        return schedulesFlow.asStateFlow().map { it.values.toList() }
    }

    override suspend fun getScheduleById(id: Long): FeedingSchedule? {
        return schedulesFlow.value[id]
    }

    override suspend fun getAllEnabledSchedules(): List<FeedingSchedule> {
        return schedulesFlow.value.values.filter { it.isEnabled }
    }

    override suspend fun addSchedule(schedule: FeedingSchedule): Long {
        val id = if (schedule.id > 0) schedule.id else nextId++
        val newSchedule = schedule.copy(id = id)
        schedulesFlow.value = schedulesFlow.value + (id to newSchedule)
        return id
    }

    override suspend fun updateSchedule(schedule: FeedingSchedule) {
        if (schedulesFlow.value.containsKey(schedule.id)) {
            schedulesFlow.value = schedulesFlow.value + (schedule.id to schedule)
        }
    }

    override suspend fun deleteSchedule(schedule: FeedingSchedule) {
        deleteScheduleById(schedule.id)
    }

    override suspend fun deleteScheduleById(id: Long) {
        schedulesFlow.value = schedulesFlow.value - id
    }

    override suspend fun setEnabled(id: Long, isEnabled: Boolean) {
        val existing = schedulesFlow.value[id] ?: return
        schedulesFlow.value = schedulesFlow.value + (id to existing.copy(isEnabled = isEnabled))
    }

    override suspend fun seedDefaultSchedulesIfEmpty() {
        if (schedulesFlow.value.isNotEmpty()) return

        val defaultSlots = listOf(
            Pair(6, 0), Pair(8, 0), Pair(10, 0), Pair(12, 0),
            Pair(14, 0), Pair(16, 0), Pair(18, 0), Pair(20, 0),
            Pair(22, 0), Pair(0, 0), Pair(2, 0), Pair(4, 0)
        )

        val newMap = mutableMapOf<Long, FeedingSchedule>()
        defaultSlots.forEach { (hour, minute) ->
            val id = nextId++
            val isEnabled = !(hour == 18 && minute == 0)
            newMap[id] = FeedingSchedule(
                id = id,
                hour = hour,
                minute = minute,
                volumeMl = 30,
                method = FeedingMethod.OGT_NGT,
                note = null,
                isEnabled = isEnabled,
                reminderEnabled = true,
                reminderOffsetMinutes = 10,
                repeatType = RepeatType.DAILY
            )
        }
        schedulesFlow.value = newMap
    }

    override suspend fun deleteAll() {
        schedulesFlow.value = emptyMap()
    }
}
