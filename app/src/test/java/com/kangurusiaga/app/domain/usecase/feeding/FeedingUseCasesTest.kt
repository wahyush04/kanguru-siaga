package com.kangurusiaga.app.domain.usecase.feeding

import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.domain.model.FeedingMethod
import com.kangurusiaga.app.domain.model.FeedingSchedule
import com.kangurusiaga.app.domain.model.RepeatType
import com.kangurusiaga.app.fake.FakeFeedingReminderScheduler
import com.kangurusiaga.app.fake.FakeFeedingScheduleRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class FeedingUseCasesTest {

    private lateinit var repository: FakeFeedingScheduleRepository
    private lateinit var scheduler: FakeFeedingReminderScheduler

    private lateinit var observeUseCase: ObserveFeedingSchedulesUseCase
    private lateinit var addUseCase: AddFeedingScheduleUseCase
    private lateinit var updateUseCase: UpdateFeedingScheduleUseCase
    private lateinit var deleteUseCase: DeleteFeedingScheduleUseCase
    private lateinit var toggleUseCase: ToggleFeedingScheduleUseCase

    @Before
    fun setup() {
        repository = FakeFeedingScheduleRepository()
        scheduler = FakeFeedingReminderScheduler()

        observeUseCase = ObserveFeedingSchedulesUseCase(repository)
        addUseCase = AddFeedingScheduleUseCase(repository, scheduler)
        updateUseCase = UpdateFeedingScheduleUseCase(repository, scheduler)
        deleteUseCase = DeleteFeedingScheduleUseCase(repository, scheduler)
        toggleUseCase = ToggleFeedingScheduleUseCase(repository, scheduler)
    }

    @Test
    fun observeFeedingSchedules_sortsIn24HourCaregiverCycle() = runTest {
        repository.addSchedule(FeedingSchedule(hour = 2, minute = 0, volumeMl = 30))
        repository.addSchedule(FeedingSchedule(hour = 6, minute = 0, volumeMl = 30))
        repository.addSchedule(FeedingSchedule(hour = 0, minute = 0, volumeMl = 30))
        repository.addSchedule(FeedingSchedule(hour = 14, minute = 0, volumeMl = 30))

        val result = observeUseCase().first()

        assertThat(result.map { it.formattedTime }).containsExactly(
            "06:00",
            "14:00",
            "00:00",
            "02:00"
        ).inOrder()
    }

    @Test
    fun addFeedingSchedule_validInput_insertsAndSchedulesReminder() = runTest {
        val schedule = FeedingSchedule(
            hour = 14,
            minute = 0,
            volumeMl = 30,
            method = FeedingMethod.OGT_NGT,
            isEnabled = true,
            reminderEnabled = true,
            repeatType = RepeatType.DAILY
        )

        val result = addUseCase(schedule)

        assertThat(result.isSuccess).isTrue()
        val newId = result.getOrThrow()
        assertThat(repository.getScheduleById(newId)).isNotNull()
        assertThat(scheduler.scheduledSchedules.containsKey(newId)).isTrue()
    }

    @Test
    fun addFeedingSchedule_reminderDisabled_insertsWithoutScheduling() = runTest {
        val schedule = FeedingSchedule(
            hour = 14,
            minute = 0,
            volumeMl = 30,
            isEnabled = true,
            reminderEnabled = false
        )

        val result = addUseCase(schedule)

        assertThat(result.isSuccess).isTrue()
        val newId = result.getOrThrow()
        assertThat(scheduler.scheduledSchedules.containsKey(newId)).isFalse()
    }

    @Test
    fun addFeedingSchedule_invalidVolume_failsValidation() = runTest {
        val schedule = FeedingSchedule(
            hour = 14,
            minute = 0,
            volumeMl = 0
        )

        val result = addUseCase(schedule)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("Jumlah ASI harus lebih dari 0 ml")
    }

    @Test
    fun addFeedingSchedule_invalidHourOrMinute_failsValidation() = runTest {
        val invalidHour = FeedingSchedule(hour = 25, minute = 0, volumeMl = 30)
        val invalidMinute = FeedingSchedule(hour = 10, minute = 65, volumeMl = 30)

        assertThat(addUseCase(invalidHour).isFailure).isTrue()
        assertThat(addUseCase(invalidMinute).isFailure).isTrue()
    }

    @Test
    fun updateFeedingSchedule_validInput_updatesAndReschedules() = runTest {
        val id = repository.addSchedule(
            FeedingSchedule(hour = 8, minute = 0, volumeMl = 30, reminderEnabled = true)
        )

        val updated = FeedingSchedule(
            id = id,
            hour = 8,
            minute = 30,
            volumeMl = 35,
            reminderEnabled = true
        )
        val result = updateUseCase(updated)

        assertThat(result.isSuccess).isTrue()
        val fromRepo = repository.getScheduleById(id)
        assertThat(fromRepo?.minute).isEqualTo(30)
        assertThat(fromRepo?.volumeMl).isEqualTo(35)
        assertThat(scheduler.scheduledSchedules[id]?.minute).isEqualTo(30)
    }

    @Test
    fun deleteFeedingSchedule_cancelsReminderAndDeletes() = runTest {
        val id = repository.addSchedule(
            FeedingSchedule(hour = 10, minute = 0, volumeMl = 30)
        )
        scheduler.schedule(FeedingSchedule(id = id, hour = 10, minute = 0, volumeMl = 30))

        val result = deleteUseCase(id)

        assertThat(result.isSuccess).isTrue()
        assertThat(repository.getScheduleById(id)).isNull()
        assertThat(scheduler.cancelledIds).contains(id)
    }

    @Test
    fun toggleFeedingSchedule_enabledToDisabled_cancelsReminder() = runTest {
        val id = repository.addSchedule(
            FeedingSchedule(hour = 12, minute = 0, volumeMl = 30, isEnabled = true, reminderEnabled = true)
        )
        scheduler.schedule(FeedingSchedule(id = id, hour = 12, minute = 0, volumeMl = 30))

        val result = toggleUseCase(id, false)

        assertThat(result.isSuccess).isTrue()
        assertThat(repository.getScheduleById(id)?.isEnabled).isFalse()
        assertThat(scheduler.cancelledIds).contains(id)
    }

    @Test
    fun toggleFeedingSchedule_disabledToEnabled_schedulesReminder() = runTest {
        val id = repository.addSchedule(
            FeedingSchedule(hour = 12, minute = 0, volumeMl = 30, isEnabled = false, reminderEnabled = true)
        )

        val result = toggleUseCase(id, true)

        assertThat(result.isSuccess).isTrue()
        assertThat(repository.getScheduleById(id)?.isEnabled).isTrue()
        assertThat(scheduler.scheduledSchedules.containsKey(id)).isTrue()
    }
}
