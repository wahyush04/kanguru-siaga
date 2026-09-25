package com.kangurusiaga.app.presentation.education

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.domain.model.education.EducationModule
import com.kangurusiaga.app.domain.usecase.education.GetEducationModuleUseCase
import com.kangurusiaga.app.domain.usecase.education.GetEducationModulesUseCase
import com.kangurusiaga.app.domain.usecase.education.MarkEducationCompletedUseCase
import com.kangurusiaga.app.domain.usecase.education.RecordEducationLastReadUseCase
import com.kangurusiaga.app.domain.usecase.education.ToggleEducationFavoriteUseCase
import io.mockk.coEvery
import io.mockk.coVerify
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
class EducationDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getEducationModuleUseCase: GetEducationModuleUseCase = mockk()
    private val getEducationModulesUseCase: GetEducationModulesUseCase = mockk()
    private val toggleEducationFavoriteUseCase: ToggleEducationFavoriteUseCase = mockk()
    private val markEducationCompletedUseCase: MarkEducationCompletedUseCase = mockk()
    private val recordEducationLastReadUseCase: RecordEducationLastReadUseCase = mockk()

    private val module1 = EducationModule(
        id = "bblr_01",
        order = 1,
        title = "Pengertian BBLR",
        category = "Dasar BBLR",
        readingTimeMinutes = 5,
        themeColor = "rose",
        heroDrawable = "il_edu_module_1",
        heroTag = "Penting",
        sections = emptyList(),
        isBookmarked = false,
        isCompleted = false
    )

    private val module2 = EducationModule(
        id = "bblr_02",
        order = 2,
        title = "Menjaga Kehangatan Bayi",
        category = "Perawatan Termal",
        readingTimeMinutes = 6,
        themeColor = "amber",
        heroDrawable = "il_edu_module_2",
        heroTag = "Prioritas",
        sections = emptyList(),
        isBookmarked = false,
        isCompleted = false
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { recordEducationLastReadUseCase(any()) } returns Unit
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_loadsModuleAndCalculatesNextModuleCorrectly() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("moduleId" to "bblr_01"))

        every { getEducationModuleUseCase("bblr_01") } returns flowOf(module1)
        every { getEducationModulesUseCase() } returns flowOf(listOf(module1, module2))

        val viewModel = EducationDetailViewModel(
            savedStateHandle = savedStateHandle,
            getEducationModuleUseCase = getEducationModuleUseCase,
            getEducationModulesUseCase = getEducationModulesUseCase,
            toggleEducationFavoriteUseCase = toggleEducationFavoriteUseCase,
            markEducationCompletedUseCase = markEducationCompletedUseCase,
            recordEducationLastReadUseCase = recordEducationLastReadUseCase
        )

        viewModel.uiState.test {
            val initial = awaitItem()
            assertThat(initial.isLoading).isTrue()

            testDispatcher.scheduler.advanceUntilIdle()

            val loaded = awaitItem()
            assertThat(loaded.isLoading).isFalse()
            assertThat(loaded.module?.id).isEqualTo("bblr_01")
            assertThat(loaded.nextModuleId).isEqualTo("bblr_02")
            assertThat(loaded.nextModuleOrder).isEqualTo(2)
            assertThat(loaded.nextModuleTitle).isEqualTo("Menjaga Kehangatan Bayi")
        }

        coVerify(exactly = 1) { recordEducationLastReadUseCase("bblr_01") }
    }

    @Test
    fun uiState_lastModuleHasNoNextModule() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("moduleId" to "bblr_02"))

        every { getEducationModuleUseCase("bblr_02") } returns flowOf(module2)
        every { getEducationModulesUseCase() } returns flowOf(listOf(module1, module2))

        val viewModel = EducationDetailViewModel(
            savedStateHandle = savedStateHandle,
            getEducationModuleUseCase = getEducationModuleUseCase,
            getEducationModulesUseCase = getEducationModulesUseCase,
            toggleEducationFavoriteUseCase = toggleEducationFavoriteUseCase,
            markEducationCompletedUseCase = markEducationCompletedUseCase,
            recordEducationLastReadUseCase = recordEducationLastReadUseCase
        )

        viewModel.uiState.test {
            awaitItem() // initial
            testDispatcher.scheduler.advanceUntilIdle()
            val loaded = awaitItem()
            assertThat(loaded.module?.id).isEqualTo("bblr_02")
            assertThat(loaded.nextModuleId).isNull()
            assertThat(loaded.nextModuleTitle).isNull()
        }
    }

    @Test
    fun toggleBookmark_invokesUseCase() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("moduleId" to "bblr_01"))
        every { getEducationModuleUseCase("bblr_01") } returns flowOf(module1)
        every { getEducationModulesUseCase() } returns flowOf(listOf(module1, module2))
        coEvery { toggleEducationFavoriteUseCase("bblr_01") } returns Unit

        val viewModel = EducationDetailViewModel(
            savedStateHandle = savedStateHandle,
            getEducationModuleUseCase = getEducationModuleUseCase,
            getEducationModulesUseCase = getEducationModulesUseCase,
            toggleEducationFavoriteUseCase = toggleEducationFavoriteUseCase,
            markEducationCompletedUseCase = markEducationCompletedUseCase,
            recordEducationLastReadUseCase = recordEducationLastReadUseCase
        )

        viewModel.toggleBookmark()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { toggleEducationFavoriteUseCase("bblr_01") }
    }

    @Test
    fun markCompleted_invokesUseCase() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("moduleId" to "bblr_01"))
        every { getEducationModuleUseCase("bblr_01") } returns flowOf(module1)
        every { getEducationModulesUseCase() } returns flowOf(listOf(module1, module2))
        coEvery { markEducationCompletedUseCase("bblr_01") } returns Unit

        val viewModel = EducationDetailViewModel(
            savedStateHandle = savedStateHandle,
            getEducationModuleUseCase = getEducationModuleUseCase,
            getEducationModulesUseCase = getEducationModulesUseCase,
            toggleEducationFavoriteUseCase = toggleEducationFavoriteUseCase,
            markEducationCompletedUseCase = markEducationCompletedUseCase,
            recordEducationLastReadUseCase = recordEducationLastReadUseCase
        )

        viewModel.markCompleted()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { markEducationCompletedUseCase("bblr_01") }
    }
}
