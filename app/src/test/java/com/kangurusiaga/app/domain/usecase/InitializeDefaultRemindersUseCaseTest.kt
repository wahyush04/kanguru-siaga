package com.kangurusiaga.app.domain.usecase

import com.kangurusiaga.app.core.notification.AlarmScheduler
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.repository.FeedingScheduleRepository
import com.kangurusiaga.app.domain.repository.PmkReminderRepository
import com.kangurusiaga.app.domain.repository.PrematureScreeningRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class InitializeDefaultRemindersUseCaseTest {

    private val pmkReminderRepository: PmkReminderRepository = mockk(relaxed = true)
    private val feedingScheduleRepository: FeedingScheduleRepository = mockk(relaxed = true)
    private val prematureScreeningRepository: PrematureScreeningRepository = mockk(relaxed = true)
    private val alarmScheduler: AlarmScheduler = mockk(relaxed = true)

    private val useCase = InitializeDefaultRemindersUseCase(
        pmkReminderRepository = pmkReminderRepository,
        feedingScheduleRepository = feedingScheduleRepository,
        prematureScreeningRepository = prematureScreeningRepository,
        alarmScheduler = alarmScheduler
    )

    private val dummyBaby = Baby(
        id = 7L,
        name = "Nirmala",
        gender = Gender.FEMALE,
        birthDateEpochMillis = 1_700_000_000_000L,
        birthWeightGram = 1850,
        gestationalAgeWeeks = 32
    )

    @Test
    fun invoke_seedsAllDefaultRemindersAndSchedulesNextAlarm() = runTest {
        useCase(dummyBaby)

        coVerifyOrder {
            pmkReminderRepository.seedDefaultRemindersIfEmpty(7L)
            feedingScheduleRepository.seedDefaultSchedulesIfEmpty()
            feedingScheduleRepository.resetAllOffsetToZero()
            prematureScreeningRepository.ensureDefaultScreenings(
                babyId = 7L,
                birthDateEpoch = 1_700_000_000_000L,
                gestationalAgeWeeks = 32
            )
            alarmScheduler.syncAndScheduleNextAlarm()
        }
    }

    @Test
    fun invoke_stillSchedulesNextAlarmWhenOneSeedFails() = runTest {
        coEvery { pmkReminderRepository.seedDefaultRemindersIfEmpty(any()) } throws RuntimeException("DB error")

        useCase(dummyBaby)

        coVerify(exactly = 1) { feedingScheduleRepository.seedDefaultSchedulesIfEmpty() }
        coVerify(exactly = 1) { prematureScreeningRepository.ensureDefaultScreenings(7L, 1_700_000_000_000L, 32) }
        coVerify(exactly = 1) { alarmScheduler.syncAndScheduleNextAlarm() }
    }
}
