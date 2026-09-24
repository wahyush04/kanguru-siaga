package com.kangurusiaga.app.presentation.onboarding

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.domain.usecase.GetOnboardingStatusUseCase
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
class SplashViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getOnboardingStatusUseCase: GetOnboardingStatusUseCase = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_whenOnboardingCompleted_emitsNavigateToHome() = runTest {
        every { getOnboardingStatusUseCase() } returns flowOf(true)

        val viewModel = SplashViewModel(getOnboardingStatusUseCase)

        viewModel.uiState.test {
            val initial = awaitItem()
            // May start at Loading or immediately receive NavigateToHome depending on dispatcher
            if (initial is SplashUiState.Loading) {
                testDispatcher.scheduler.advanceUntilIdle()
                val next = awaitItem()
                assertThat(next).isEqualTo(SplashUiState.NavigateToHome)
            } else {
                assertThat(initial).isEqualTo(SplashUiState.NavigateToHome)
            }
        }
    }

    @Test
    fun uiState_whenOnboardingNotCompleted_emitsShowSplash() = runTest {
        every { getOnboardingStatusUseCase() } returns flowOf(false)

        val viewModel = SplashViewModel(getOnboardingStatusUseCase)

        viewModel.uiState.test {
            val initial = awaitItem()
            if (initial is SplashUiState.Loading) {
                testDispatcher.scheduler.advanceUntilIdle()
                val next = awaitItem()
                assertThat(next).isEqualTo(SplashUiState.ShowSplash)
            } else {
                assertThat(initial).isEqualTo(SplashUiState.ShowSplash)
            }
        }
    }
}
