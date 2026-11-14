package com.kangurusiaga.app.core.notification

import android.app.AlarmManager
import android.content.Context
import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.data.local.dao.BabyDao
import com.kangurusiaga.app.data.local.dao.FeedingScheduleDao
import com.kangurusiaga.app.data.local.dao.PmkReminderDao
import com.kangurusiaga.app.data.local.dao.PrematureScreeningDao
import com.kangurusiaga.app.data.local.dao.ReminderScheduleDao
import com.kangurusiaga.app.data.local.entity.ReminderScheduleEntity
import com.kangurusiaga.app.domain.model.ReminderType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.Calendar
import java.util.concurrent.TimeUnit

class AlarmSchedulerTest {

    private val context: Context = mockk(relaxed = true)
    private val alarmManager: AlarmManager = mockk(relaxed = true)
    private val reminderScheduleDao: ReminderScheduleDao = mockk(relaxed = true)
    private val pmkReminderDao: PmkReminderDao = mockk(relaxed = true)
    private val feedingScheduleDao: FeedingScheduleDao = mockk(relaxed = true)
    private val prematureScreeningDao: PrematureScreeningDao = mockk(relaxed = true)
    private val notificationManager: KanguruNotificationManager = mockk(relaxed = true)
    private val babyDao: BabyDao = mockk(relaxed = true)

    private lateinit var scheduler: AlarmSchedulerImpl

    @Before
    fun setUp() {
        every { context.getSystemService(Context.ALARM_SERVICE) } returns alarmManager
        coEvery { pmkReminderDao.getAllReminders() } returns emptyList()
        coEvery { feedingScheduleDao.getAllSchedules() } returns emptyList()
        coEvery { prematureScreeningDao.getAllIncomplete() } returns emptyList()
        coEvery { reminderScheduleDao.getMissedReminders(any()) } returns emptyList()

        scheduler = AlarmSchedulerImpl(
            context = context,
            reminderScheduleDao = reminderScheduleDao,
            pmkReminderDao = pmkReminderDao,
            feedingScheduleDao = feedingScheduleDao,
            prematureScreeningDao = prematureScreeningDao,
            notificationManager = notificationManager,
            babyDao = babyDao
        )
    }

    @Test
    fun computeNextDailyTrigger_hasZeroSecondsAndMilliseconds() {
        val trigger = AlarmSchedulerImpl.computeNextDailyTrigger(hour = 8, minute = 30)
        val cal = Calendar.getInstance().apply { timeInMillis = trigger }

        assertThat(cal.get(Calendar.HOUR_OF_DAY)).isEqualTo(8)
        assertThat(cal.get(Calendar.MINUTE)).isEqualTo(30)
        assertThat(cal.get(Calendar.SECOND)).isEqualTo(0)
        assertThat(cal.get(Calendar.MILLISECOND)).isEqualTo(0)
    }

    @Test
    fun computeNextWeeklyTrigger_advancesStrictlyBySevenDays() {
        val startCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = startCal.timeInMillis

        val nextWeekly = AlarmSchedulerImpl.computeNextWeeklyTrigger(
            hour = 9,
            minute = 0,
            startFromEpoch = startTime
        )

        val diffDays = TimeUnit.MILLISECONDS.toDays(nextWeekly - startTime)
        assertThat(diffDays % 7L).isEqualTo(0L)
        assertThat(nextWeekly).isGreaterThan(System.currentTimeMillis())
    }

    @Test
    fun syncAndScheduleNextAlarm_schedulesSingleEarliestActiveAlarm() = runTest {
        val now = System.currentTimeMillis()
        val futureTrigger = now + TimeUnit.HOURS.toMillis(2)

        val nextEarliest = ReminderScheduleEntity(
            id = "PMK_1",
            type = ReminderType.PMK.name,
            referenceId = 1L,
            subIndex = 0,
            label = "Estafet Sesi Pagi",
            timeHour = 7,
            timeMinute = 0,
            isEnabled = true,
            nextTriggerMillis = futureTrigger
        )

        coEvery { reminderScheduleDao.getNextEarliestActive(any()) } returns nextEarliest

        scheduler.syncAndScheduleNextAlarm()

        coVerify(exactly = 1) {
            reminderScheduleDao.getNextEarliestActive(any())
        }
    }

    @Test
    fun handleMissedAlarms_missedUnder30Minutes_showsInformationalNotificationAndAdvancesDailySlot() = runTest {
        val now = System.currentTimeMillis()
        val missedTime = now - TimeUnit.MINUTES.toMillis(15) // missed 15 minutes ago

        val missedPmk = ReminderScheduleEntity(
            id = "PMK_1",
            type = ReminderType.PMK.name,
            referenceId = 1L,
            subIndex = 0,
            label = "Estafet Sesi Pagi",
            timeHour = 7,
            timeMinute = 0,
            isEnabled = true,
            nextTriggerMillis = missedTime
        )

        coEvery { reminderScheduleDao.getMissedReminders(any()) } returns listOf(missedPmk)
        coEvery { reminderScheduleDao.getNextEarliestActive(any()) } returns null

        scheduler.syncAndScheduleNextAlarm()

        // Verify informational missed notification shown
        verify(exactly = 1) {
            notificationManager.showMissedReminderNotification(
                title = match { it.contains("Estafet Sesi Pagi") },
                message = any()
            )
        }

        // Verify nextTriggerMillis advanced
        coVerify(exactly = 1) {
            reminderScheduleDao.updateNextTrigger("PMK_1", any(), any())
        }
    }

    @Test
    fun handleMissedAlarms_missedScreening_showsDelayedNotificationAndDisables() = runTest {
        val now = System.currentTimeMillis()
        val missedTime = now - TimeUnit.DAYS.toMillis(2)

        val missedScreening = ReminderScheduleEntity(
            id = "SKRINING_1_1",
            type = ReminderType.SKRINING.name,
            referenceId = 1L,
            subIndex = 1,
            label = "USG Kepala",
            timeHour = 8,
            timeMinute = 0,
            targetDateMillis = missedTime,
            isEnabled = true,
            nextTriggerMillis = missedTime
        )

        coEvery { reminderScheduleDao.getMissedReminders(any()) } returns listOf(missedScreening)
        coEvery { reminderScheduleDao.getNextEarliestActive(any()) } returns null

        scheduler.syncAndScheduleNextAlarm()

        verify(exactly = 1) {
            notificationManager.showDelayedReminderNotification(
                title = match { it.contains("USG Kepala") },
                message = any(),
                navigateDestination = "screening_schedule"
            )
        }

        coVerify(exactly = 1) {
            reminderScheduleDao.updateEnabled("SKRINING_1_1", false, any())
        }
    }

    @Test
    fun snoozeReminder_insertsSnoozeScheduleWithTenMinutesDelay() = runTest {
        val original = ReminderScheduleEntity(
            id = "PMK_1",
            type = ReminderType.PMK.name,
            referenceId = 1L,
            subIndex = 0,
            label = "Estafet Sesi Pagi",
            timeHour = 7,
            timeMinute = 0,
            isEnabled = true,
            nextTriggerMillis = System.currentTimeMillis()
        )
        coEvery { reminderScheduleDao.getById("PMK_1") } returns original

        val slot = slot<ReminderScheduleEntity>()
        coEvery { reminderScheduleDao.insertOrUpdate(capture(slot)) } returns Unit
        coEvery { reminderScheduleDao.getNextEarliestActive(any()) } returns null

        scheduler.snoozeReminder("PMK_1", durationMinutes = 10)

        assertThat(slot.isCaptured).isTrue()
        val captured = slot.captured
        assertThat(captured.id).startsWith("SNOOZE_PMK_1")
        assertThat(captured.label).contains("Tunda")
        assertThat(captured.isEnabled).isTrue()
        val diffMs = captured.nextTriggerMillis - System.currentTimeMillis()
        assertThat(diffMs).isAtLeast(TimeUnit.MINUTES.toMillis(9))
    }
}
