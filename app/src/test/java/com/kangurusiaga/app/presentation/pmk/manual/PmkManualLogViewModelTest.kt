package com.kangurusiaga.app.presentation.pmk.manual

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.usecase.GetBabyProfileUseCase
import com.kangurusiaga.app.domain.usecase.SavePmkSessionUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PmkManualLogViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getBabyProfileUseCase: GetBabyProfileUseCase = mockk()
    private val savePmkSessionUseCase: SavePmkSessionUseCase = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun saveManualSession_savesSuccessfullyAndEmitsEvent() = runTest {
        val baby = Baby(
            id = 1L,
            name = "Aisyah Humaira",
            gender = Gender.FEMALE,
            birthDateEpochMillis = 1000L,
            birthWeightGram = 1850,
            birthLengthCm = 44f,
            birthHeadCircumferenceCm = 31f
        )

        every { getBabyProfileUseCase() } returns flowOf(baby)
        coEvery { savePmkSessionUseCase(any()) } returns 101L

        val viewModel = PmkManualLogViewModel(getBabyProfileUseCase, savePmkSessionUseCase)

        viewModel.events.test {
            viewModel.onDurationChanged(90)
            viewModel.onTemperatureChanged("37.0")
            viewModel.onResponseChanged("Tenang")
            viewModel.onNotesChanged("Bayi menyusu baik")

            viewModel.saveManualSession()
            testDispatcher.scheduler.advanceUntilIdle()

            val event = awaitItem()
            assertThat(event).isEqualTo(PmkManualLogEvent.SavedSuccess)
        }
    }
}
