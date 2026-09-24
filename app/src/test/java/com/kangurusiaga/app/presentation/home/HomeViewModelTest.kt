package com.kangurusiaga.app.presentation.home

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.usecase.GetBabyProfileUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
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

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_emitsActiveBabyCorrectly() = runTest {
        val baby = Baby(
            id = 1L,
            name = "Baby test",
            gender = Gender.FEMALE,
            birthDateEpochMillis = 1727136000000L,
            birthWeightGram = 2000,
            birthLengthCm = 44.0f,
            birthHeadCircumferenceCm = 31.0f
        )

        every { getBabyProfileUseCase() } returns flowOf(baby)

        val viewModel = HomeViewModel(getBabyProfileUseCase)

        viewModel.uiState.test {
            val initial = awaitItem()
            assertThat(initial.isLoading).isTrue()

            testDispatcher.scheduler.advanceUntilIdle()

            val success = awaitItem()
            assertThat(success.isLoading).isFalse()
            assertThat(success.activeBaby).isEqualTo(baby)
        }
    }

    @Test
    fun uiState_handlesErrorCorrectly() = runTest {
        every { getBabyProfileUseCase() } returns flow {
            throw RuntimeException("Database error")
        }

        val viewModel = HomeViewModel(getBabyProfileUseCase)

        viewModel.uiState.test {
            val initial = awaitItem()
            assertThat(initial.isLoading).isTrue()

            testDispatcher.scheduler.advanceUntilIdle()

            val errorState = awaitItem()
            assertThat(errorState.isLoading).isFalse()
            assertThat(errorState.userMessage).isEqualTo("Database error")
        }
    }
}
