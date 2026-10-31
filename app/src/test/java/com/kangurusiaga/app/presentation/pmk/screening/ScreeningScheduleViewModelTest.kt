package com.kangurusiaga.app.presentation.pmk.screening

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.core.notification.ScreeningReminderScheduler
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.PrematureScreening
import com.kangurusiaga.app.domain.model.ScreeningType
import com.kangurusiaga.app.domain.repository.BabyRepository
import com.kangurusiaga.app.domain.repository.GrowthRepository
import com.kangurusiaga.app.domain.repository.PrematureScreeningRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class ScreeningScheduleViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val babyRepository: BabyRepository = mockk(relaxed = true)
    private val screeningRepository: PrematureScreeningRepository = mockk(relaxed = true)
    private val growthRepository: GrowthRepository = mockk(relaxed = true)
    private val screeningReminderScheduler: ScreeningReminderScheduler = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { growthRepository.getLatestMeasurement(any(), any()) } returns flowOf(null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadData_ensuresDefaultsAndLoadsScreenings() = runTest {
        val birthCal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 12, 0, 0, 0)
        }
        val baby = Baby(
            id = 1L,
            name = "By. Ny. Arini",
            birthDateEpochMillis = birthCal.timeInMillis,
            birthWeightGram = 1800,
            gestationalAgeWeeks = 31
        )

        val screeningsList = listOf(
            PrematureScreening(
                id = 1L,
                babyId = 1L,
                type = ScreeningType.USG_HEAD,
                title = "USG Kepala",
                categoryBadge = "Neurosonografi",
                targetTimingLabel = "Usia 2 Minggu Kronologis",
                targetDateEpoch = birthCal.timeInMillis + (14L * 24 * 3600 * 1000L),
                targetDateFormatted = "26 Okt 2026",
                indication = "Deteksi IVH",
                clinicalGoal = "Tujuan klinis USG",
                clinicOrLab = "Poli Anak"
            ),
            PrematureScreening(
                id = 2L,
                babyId = 1L,
                type = ScreeningType.ROP_EYE,
                title = "Retinopati Prematuritas (ROP)",
                categoryBadge = "Retina & Penglihatan",
                targetTimingLabel = "Usia Koreksi 0 Bulan",
                targetDateEpoch = birthCal.timeInMillis + (63L * 24 * 3600 * 1000L),
                targetDateFormatted = "14 Des 2026",
                indication = "Deteksi ROP",
                clinicalGoal = "Cegah kebutaan",
                clinicOrLab = "Poli Mata"
            )
        )

        every { babyRepository.getActiveBaby() } returns flowOf(baby)
        every { screeningRepository.getScreeningsForBaby(1L) } returns flowOf(screeningsList)
        coEvery { screeningRepository.ensureDefaultScreenings(any(), any(), any()) } returns Unit

        val viewModel = ScreeningScheduleViewModel(babyRepository, screeningRepository, growthRepository)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.baby?.name).isEqualTo("By. Ny. Arini")
            assertThat(state.babyAgeDisplay).isEqualTo("Prematur 31 Mgg")
            assertThat(state.screenings).hasSize(2)
            assertThat(state.screenings[0].title).isEqualTo("USG Kepala")
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { screeningRepository.ensureDefaultScreenings(1L, birthCal.timeInMillis, 31) }
    }

    @Test
    fun selectTab_switchesBetweenRemindersAndEvaluation() = runTest {
        every { babyRepository.getActiveBaby() } returns flowOf(null)
        val viewModel = ScreeningScheduleViewModel(babyRepository, screeningRepository, growthRepository)

        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.selectedTab).isEqualTo(ScreeningTab.REMINDERS)
        viewModel.selectTab(ScreeningTab.EVALUATION)
        assertThat(viewModel.uiState.value.selectedTab).isEqualTo(ScreeningTab.EVALUATION)
    }

    @Test
    fun recordModal_openUpdateAndSave() = runTest {
        val screening = PrematureScreening(
            id = 1L,
            babyId = 1L,
            type = ScreeningType.USG_HEAD,
            title = "USG Kepala",
            categoryBadge = "Neurosonografi",
            targetTimingLabel = "Usia 2 Minggu Kronologis",
            targetDateEpoch = 1000L,
            indication = "Deteksi IVH",
            clinicalGoal = "Tujuan klinis",
            clinicOrLab = "Poli Anak"
        )
        every { babyRepository.getActiveBaby() } returns flowOf(null)
        coEvery { screeningRepository.updateScreening(any()) } returns Unit

        val viewModel = ScreeningScheduleViewModel(babyRepository, screeningRepository, growthRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.openRecordModal(screening)
        assertThat(viewModel.uiState.value.recordModalState.isOpen).isTrue()

        viewModel.updateRecordHospital("RS Hermina")
        viewModel.updateRecordDoctor("dr. Hendra")
        viewModel.updateRecordResult("Normal tidak ada perdarahan")
        viewModel.updateRecordCompleted(true)

        viewModel.saveScreeningResult()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.recordModalState.isOpen).isFalse()
        coVerify {
            screeningRepository.updateScreening(match {
                it.hospitalName == "RS Hermina" &&
                        it.doctorName == "dr. Hendra" &&
                        it.clinicalResult == "Normal tidak ada perdarahan" &&
                        it.isCompleted
            })
        }
    }

    @Test
    fun toggleAlarm_updatesScreeningAndTriggersScheduler() = runTest {
        val screening = PrematureScreening(
            id = 10L,
            babyId = 1L,
            type = ScreeningType.USG_HEAD,
            title = "USG Kepala",
            categoryBadge = "Neurosonografi",
            targetTimingLabel = "Usia 2 Minggu",
            targetDateEpoch = 1000L,
            isAlarmActive = false,
            indication = "",
            clinicalGoal = "",
            clinicOrLab = ""
        )
        every { babyRepository.getActiveBaby() } returns flowOf(null)
        coEvery { screeningRepository.updateScreening(any()) } returns Unit

        val viewModel = ScreeningScheduleViewModel(
            babyRepository,
            screeningRepository,
            growthRepository,
            screeningReminderScheduler
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleAlarm(screening, isAlarm1 = true)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify {
            screeningRepository.updateScreening(match { it.id == 10L && it.isAlarmActive })
        }
        verify {
            screeningReminderScheduler.schedule(match { it.id == 10L && it.isAlarmActive })
        }
    }
}
