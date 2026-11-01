package com.kangurusiaga.app.presentation.settings

import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.core.common.PhotoStorageManager
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.FeedingMethod
import com.kangurusiaga.app.domain.model.FeedingSchedule
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.PrematureScreening
import com.kangurusiaga.app.domain.model.RepeatType
import com.kangurusiaga.app.domain.model.ScreeningType
import com.kangurusiaga.app.domain.model.settings.AppSettings
import com.kangurusiaga.app.domain.repository.BabyRepository
import com.kangurusiaga.app.domain.repository.PrematureScreeningRepository
import com.kangurusiaga.app.domain.usecase.GetPmkRemindersUseCase
import com.kangurusiaga.app.domain.usecase.UpdateBabyProfileUseCase
import com.kangurusiaga.app.domain.usecase.feeding.ObserveFeedingSchedulesUseCase
import com.kangurusiaga.app.domain.usecase.settings.GetAppSettingsUseCase
import com.kangurusiaga.app.domain.usecase.settings.ToggleNotificationsUseCase
import com.kangurusiaga.app.domain.usecase.settings.UpdateAppThemeUseCase
import com.kangurusiaga.app.domain.usecase.settings.UpdateTextScaleUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val babyRepository: BabyRepository = mockk()
    private val getAppSettingsUseCase: GetAppSettingsUseCase = mockk()
    private val updateBabyProfileUseCase: UpdateBabyProfileUseCase = mockk()
    private val updateAppThemeUseCase: UpdateAppThemeUseCase = mockk()
    private val updateTextScaleUseCase: UpdateTextScaleUseCase = mockk()
    private val toggleNotificationsUseCase: ToggleNotificationsUseCase = mockk()
    private val getPmkRemindersUseCase: GetPmkRemindersUseCase = mockk()
    private val observeFeedingSchedulesUseCase: ObserveFeedingSchedulesUseCase = mockk()
    private val prematureScreeningRepository: PrematureScreeningRepository = mockk()
    private val photoStorageManager: PhotoStorageManager = mockk(relaxed = true)

    private val dummyBaby = Baby(
        id = 1L,
        name = "Nirmala",
        gender = Gender.FEMALE,
        birthDateEpochMillis = 1700000000000L,
        birthWeightGram = 1800,
        gestationalAgeWeeks = 32
    )

    private val screeningFlow = MutableStateFlow<List<PrematureScreening>>(emptyList())
    private val feedingFlow = MutableStateFlow<List<FeedingSchedule>>(emptyList())

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { babyRepository.getActiveBaby() } returns flowOf(dummyBaby)
        every { getAppSettingsUseCase() } returns flowOf(AppSettings())
        coEvery { getPmkRemindersUseCase(any()) } returns flowOf(emptyList())
        every { observeFeedingSchedulesUseCase() } returns feedingFlow
        coEvery { prematureScreeningRepository.ensureDefaultScreenings(any(), any(), any()) } returns Unit
        every { prematureScreeningRepository.getScreeningsForBaby(any()) } returns screeningFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): SettingsViewModel {
        return SettingsViewModel(
            babyRepository = babyRepository,
            getAppSettingsUseCase = getAppSettingsUseCase,
            updateBabyProfileUseCase = updateBabyProfileUseCase,
            updateAppThemeUseCase = updateAppThemeUseCase,
            updateTextScaleUseCase = updateTextScaleUseCase,
            toggleNotificationsUseCase = toggleNotificationsUseCase,
            getPmkRemindersUseCase = getPmkRemindersUseCase,
            observeFeedingSchedulesUseCase = observeFeedingSchedulesUseCase,
            prematureScreeningRepository = prematureScreeningRepository,
            photoStorageManager = photoStorageManager
        )
    }

    @Test
    fun screeningReminder_whenActiveAlarmsExist_showsAktif() = runTest(testDispatcher) {
        val activeScreening = PrematureScreening(
            id = 10L,
            babyId = 1L,
            type = ScreeningType.USG_HEAD,
            title = "USG Kepala",
            categoryBadge = "NEUROSONOGRAFI",
            targetTimingLabel = "Usia 7–14 Hari",
            targetDateEpoch = System.currentTimeMillis() + 86400000L,
            indication = "Skrining IVH",
            clinicalGoal = "Deteksi dini perdarahan intraventrikular",
            clinicOrLab = "Radiologi",
            isAlarmActive = true,
            isCompleted = false
        )
        screeningFlow.value = listOf(activeScreening)

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isScreeningReminderActive).isTrue()
        assertThat(state.screeningReminderBadgeText).isEqualTo("Aktif")
    }

    @Test
    fun screeningReminder_whenAlarmsDisabled_showsNonaktif() = runTest(testDispatcher) {
        val disabledScreening = PrematureScreening(
            id = 10L,
            babyId = 1L,
            type = ScreeningType.USG_HEAD,
            title = "USG Kepala",
            categoryBadge = "NEUROSONOGRAFI",
            targetTimingLabel = "Usia 7–14 Hari",
            targetDateEpoch = System.currentTimeMillis() + 86400000L,
            indication = "Skrining IVH",
            clinicalGoal = "Deteksi dini perdarahan intraventrikular",
            clinicOrLab = "Radiologi",
            isAlarmActive = false,
            isCompleted = false
        )
        screeningFlow.value = listOf(disabledScreening)

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isScreeningReminderActive).isFalse()
        assertThat(state.screeningReminderBadgeText).isEqualTo("Nonaktif")
    }

    @Test
    fun screeningReminder_whenAllScreeningsCompleted_showsNonaktif() = runTest(testDispatcher) {
        val completedScreening = PrematureScreening(
            id = 10L,
            babyId = 1L,
            type = ScreeningType.USG_HEAD,
            title = "USG Kepala",
            categoryBadge = "NEUROSONOGRAFI",
            targetTimingLabel = "Usia 7–14 Hari",
            targetDateEpoch = System.currentTimeMillis() + 86400000L,
            indication = "Skrining IVH",
            clinicalGoal = "Deteksi dini perdarahan intraventrikular",
            clinicOrLab = "Radiologi",
            isAlarmActive = true,
            isCompleted = true
        )
        screeningFlow.value = listOf(completedScreening)

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isScreeningReminderActive).isFalse()
        assertThat(state.screeningReminderBadgeText).isEqualTo("Nonaktif")
    }

    @Test
    fun feedingAlarm_whenActiveScheduleExists_showsAktif() = runTest(testDispatcher) {
        val activeSchedule = FeedingSchedule(
            id = 1L,
            hour = 8,
            minute = 0,
            volumeMl = 25,
            method = FeedingMethod.OGT_NGT,
            repeatType = RepeatType.DAILY,
            isEnabled = true
        )
        feedingFlow.value = listOf(activeSchedule)

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isFeedingAlarmActive).isTrue()
        assertThat(state.feedingAlarmBadgeText).isEqualTo("Aktif")
    }
}

