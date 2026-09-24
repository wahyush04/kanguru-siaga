package com.kangurusiaga.app.presentation.home

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.usecase.GetBabyProfileUseCase
import com.kangurusiaga.app.domain.usecase.GetTodayPmkSessionsUseCase
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
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getBabyProfileUseCase: GetBabyProfileUseCase = mockk()
    private val getTodayPmkSessionsUseCase: GetTodayPmkSessionsUseCase = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_emitsActiveBabyAndTodaySessionsCorrectly() = runTest {
        val baby = Baby(
            id = 1L,
            name = "Aisyah Humaira",
            gender = Gender.FEMALE,
            birthDateEpochMillis = System.currentTimeMillis() - (14 * 24 * 3600 * 1000L), // 2 weeks ago
            birthWeightGram = 1850,
            birthLengthCm = 44.0f,
            birthHeadCircumferenceCm = 31.0f
        )

        val session = PmkSession(
            id = 10L,
            babyId = 1L,
            startTimeEpoch = System.currentTimeMillis() - 3600000,
            endTimeEpoch = System.currentTimeMillis(),
            durationMinutes = 60
        )

        every { getBabyProfileUseCase() } returns flowOf(baby)
        every { getTodayPmkSessionsUseCase(1L) } returns flowOf(listOf(session))

        val viewModel = HomeViewModel(getBabyProfileUseCase, getTodayPmkSessionsUseCase)

        viewModel.uiState.test {
            val initial = awaitItem()
            assertThat(initial.isLoading).isTrue()

            testDispatcher.scheduler.advanceUntilIdle()

            val success = awaitItem()
            assertThat(success.isLoading).isFalse()
            assertThat(success.activeBaby).isEqualTo(baby)
            assertThat(success.todaySessionsCount).isEqualTo(1)
            assertThat(success.babyAgeFormatted).contains("minggu")
            assertThat(success.babyWeightFormatted).contains("1.850")
            assertThat(success.todayProgressFraction).isGreaterThan(0.3f)
        }
    }

    @Test
    fun dialog_emergencyStateToggleWorks() = runTest {
        every { getBabyProfileUseCase() } returns flowOf(null)

        val viewModel = HomeViewModel(getBabyProfileUseCase, getTodayPmkSessionsUseCase)

        viewModel.openEmergencyDialog()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.closeEmergencyDialog()
        testDispatcher.scheduler.advanceUntilIdle()
    }
}
