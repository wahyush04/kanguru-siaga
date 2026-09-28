package com.kangurusiaga.app.presentation.feeding

import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.domain.model.FeedingMethod
import com.kangurusiaga.app.domain.model.FeedingSchedule
import com.kangurusiaga.app.domain.model.RepeatType
import com.kangurusiaga.app.domain.usecase.feeding.AddFeedingScheduleUseCase
import com.kangurusiaga.app.domain.usecase.feeding.DeleteFeedingScheduleUseCase
import com.kangurusiaga.app.domain.usecase.feeding.ObserveFeedingSchedulesUseCase
import com.kangurusiaga.app.domain.usecase.feeding.ToggleFeedingScheduleUseCase
import com.kangurusiaga.app.domain.usecase.feeding.UpdateFeedingScheduleUseCase
import com.kangurusiaga.app.fake.FakeFeedingReminderScheduler
import com.kangurusiaga.app.fake.FakeFeedingScheduleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AlarmViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: FakeFeedingScheduleRepository
    private lateinit var scheduler: FakeFeedingReminderScheduler

    private lateinit var observeUseCase: ObserveFeedingSchedulesUseCase
    private lateinit var addUseCase: AddFeedingScheduleUseCase
    private lateinit var updateUseCase: UpdateFeedingScheduleUseCase
    private lateinit var deleteUseCase: DeleteFeedingScheduleUseCase
    private lateinit var toggleUseCase: ToggleFeedingScheduleUseCase

    private lateinit var viewModel: AlarmViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        repository = FakeFeedingScheduleRepository()
        scheduler = FakeFeedingReminderScheduler()

        observeUseCase = ObserveFeedingSchedulesUseCase(repository)
        addUseCase = AddFeedingScheduleUseCase(repository, scheduler)
        updateUseCase = UpdateFeedingScheduleUseCase(repository, scheduler)
        deleteUseCase = DeleteFeedingScheduleUseCase(repository, scheduler)
        toggleUseCase = ToggleFeedingScheduleUseCase(repository, scheduler)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): AlarmViewModel {
        return AlarmViewModel(
            observeFeedingSchedulesUseCase = observeUseCase,
            addFeedingScheduleUseCase = addUseCase,
            updateFeedingScheduleUseCase = updateUseCase,
            deleteFeedingScheduleUseCase = deleteUseCase,
            toggleFeedingScheduleUseCase = toggleUseCase,
            repository = repository
        )
    }

    @Test
    fun init_seedsDefault12SchedulesAndSortsStartingFrom0600() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.schedules).hasSize(12)

        // Verify the 24-hour cycle order starting at 06:00
        val times = state.schedules.map { it.formattedTime }
        assertThat(times).containsExactly(
            "06:00", "08:00", "10:00", "12:00",
            "14:00", "16:00", "18:00", "20:00",
            "22:00", "00:00", "02:00", "04:00"
        ).inOrder()

        // 18:00 is toggled OFF by default as in Stitch design
        val slot18 = state.schedules.first { it.formattedTime == "18:00" }
        assertThat(slot18.isEnabled).isFalse()

        // 06:00 is toggled ON
        val slot06 = state.schedules.first { it.formattedTime == "06:00" }
        assertThat(slot06.isEnabled).isTrue()
    }

    @Test
    fun onToggleSchedule_updatesScheduleStateAndScheduler() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val schedule06 = viewModel.uiState.value.schedules.first { it.formattedTime == "06:00" }
        assertThat(schedule06.isEnabled).isTrue()

        viewModel.onToggleSchedule(schedule06.id, false)
        advanceUntilIdle()

        val updated = viewModel.uiState.value.schedules.first { it.id == schedule06.id }
        assertThat(updated.isEnabled).isFalse()
        assertThat(scheduler.cancelledIds).contains(schedule06.id)
    }

    @Test
    fun onSaveNewSchedule_addsScheduleSuccessfully() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onSaveNewSchedule(
            hour = 7,
            minute = 30,
            volumeMl = 25,
            method = FeedingMethod.CUP,
            note = "Minum cangkir",
            reminderEnabled = true,
            repeatType = RepeatType.DAILY
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.schedules).hasSize(13)
        val added = state.schedules.firstOrNull { it.hour == 7 && it.minute == 30 }
        assertThat(added).isNotNull()
        assertThat(added?.volumeMl).isEqualTo(25)
        assertThat(added?.method).isEqualTo(FeedingMethod.CUP)
        assertThat(state.isAddSheetVisible).isFalse()
        assertThat(state.userMessage).isEqualTo("Jadwal berhasil ditambahkan")
    }

    @Test
    fun onUpdateSchedule_updatesExistingSchedule() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val schedule06 = viewModel.uiState.value.schedules.first { it.formattedTime == "06:00" }
        viewModel.onOpenEditSchedule(schedule06)
        assertThat(viewModel.uiState.value.isEditSheetVisible).isTrue()
        assertThat(viewModel.uiState.value.selectedScheduleForEdit?.id).isEqualTo(schedule06.id)

        viewModel.onUpdateSchedule(
            scheduleId = schedule06.id,
            hour = 6,
            minute = 15,
            volumeMl = 35,
            method = FeedingMethod.SPOON,
            note = "Sendok pelan",
            reminderEnabled = true,
            repeatType = RepeatType.DAILY
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        val updated = state.schedules.first { it.id == schedule06.id }
        assertThat(updated.minute).isEqualTo(15)
        assertThat(updated.volumeMl).isEqualTo(35)
        assertThat(updated.method).isEqualTo(FeedingMethod.SPOON)
        assertThat(state.isEditSheetVisible).isFalse()
        assertThat(state.selectedScheduleForEdit).isNull()
        assertThat(state.userMessage).isEqualTo("Jadwal berhasil diperbarui")
    }

    @Test
    fun deleteSchedule_confirmationFlow_deletesSchedule() = runTest {
        viewModel = createViewModel()
        advanceUntilIdle()

        val schedule06 = viewModel.uiState.value.schedules.first { it.formattedTime == "06:00" }
        viewModel.onRequestDeleteSchedule(schedule06)

        assertThat(viewModel.uiState.value.schedulePendingDelete?.id).isEqualTo(schedule06.id)

        viewModel.onConfirmDeleteSchedule()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.schedulePendingDelete).isNull()
        assertThat(state.schedules.any { it.id == schedule06.id }).isFalse()
        assertThat(state.userMessage).isEqualTo("Jadwal berhasil dihapus")
        assertThat(scheduler.cancelledIds).contains(schedule06.id)
    }

    @Test
    fun dialogs_showAndDismissProperly() = runTest {
        viewModel = createViewModel()

        viewModel.onOpenInfoDialog()
        assertThat(viewModel.uiState.value.isInfoDialogVisible).isTrue()

        viewModel.onDismissInfoDialog()
        assertThat(viewModel.uiState.value.isInfoDialogVisible).isFalse()

        viewModel.onOpenAddSchedule()
        assertThat(viewModel.uiState.value.isAddSheetVisible).isTrue()

        viewModel.onCloseAddSchedule()
        assertThat(viewModel.uiState.value.isAddSheetVisible).isFalse()
    }
}
