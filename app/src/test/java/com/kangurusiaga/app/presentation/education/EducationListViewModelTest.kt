package com.kangurusiaga.app.presentation.education

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.domain.model.education.EducationModule
import com.kangurusiaga.app.domain.usecase.education.GetEducationModulesUseCase
import com.kangurusiaga.app.domain.usecase.education.GetFavoriteEducationModulesUseCase
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
class EducationListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getEducationModulesUseCase: GetEducationModulesUseCase = mockk()
    private val getFavoriteEducationModulesUseCase: GetFavoriteEducationModulesUseCase = mockk()
    private val toggleEducationFavoriteUseCase: ToggleEducationFavoriteUseCase = mockk()

    private val sampleModule1 = EducationModule(
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

    private val sampleModule2 = EducationModule(
        id = "bblr_02",
        order = 2,
        title = "Menjaga Kehangatan Bayi",
        category = "Perawatan Termal",
        readingTimeMinutes = 6,
        themeColor = "amber",
        heroDrawable = "il_edu_module_2",
        heroTag = "Prioritas",
        sections = emptyList(),
        isBookmarked = true,
        isCompleted = true
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_emitsAllModulesAndFavoritesCorrectly() = runTest {
        every { getEducationModulesUseCase() } returns flowOf(listOf(sampleModule1, sampleModule2))
        every { getFavoriteEducationModulesUseCase() } returns flowOf(listOf(sampleModule2))

        val viewModel = EducationListViewModel(
            getEducationModulesUseCase = getEducationModulesUseCase,
            getFavoriteEducationModulesUseCase = getFavoriteEducationModulesUseCase,
            toggleEducationFavoriteUseCase = toggleEducationFavoriteUseCase
        )

        viewModel.uiState.test {
            val initial = awaitItem()
            assertThat(initial.isLoading).isTrue()

            testDispatcher.scheduler.advanceUntilIdle()

            val loaded = awaitItem()
            assertThat(loaded.isLoading).isFalse()
            assertThat(loaded.allModules).hasSize(2)
            assertThat(loaded.favoriteModules).hasSize(1)
            assertThat(loaded.selectedTab).isEqualTo(EducationTab.DAFTAR_MATERI)
            assertThat(loaded.displayedModules).hasSize(2)
        }
    }

    @Test
    fun selectTab_switchesDisplayedModulesToFavorites() = runTest {
        every { getEducationModulesUseCase() } returns flowOf(listOf(sampleModule1, sampleModule2))
        every { getFavoriteEducationModulesUseCase() } returns flowOf(listOf(sampleModule2))

        val viewModel = EducationListViewModel(
            getEducationModulesUseCase = getEducationModulesUseCase,
            getFavoriteEducationModulesUseCase = getFavoriteEducationModulesUseCase,
            toggleEducationFavoriteUseCase = toggleEducationFavoriteUseCase
        )

        viewModel.uiState.test {
            awaitItem() // initial
            testDispatcher.scheduler.advanceUntilIdle()
            val loaded = awaitItem()
            assertThat(loaded.displayedModules).hasSize(2)

            viewModel.selectTab(EducationTab.MATERI_FAVORIT)
            testDispatcher.scheduler.advanceUntilIdle()

            val tabSwitched = awaitItem()
            assertThat(tabSwitched.selectedTab).isEqualTo(EducationTab.MATERI_FAVORIT)
            assertThat(tabSwitched.displayedModules).hasSize(1)
            assertThat(tabSwitched.displayedModules.first().id).isEqualTo("bblr_02")
        }
    }

    @Test
    fun toggleBookmark_callsUseCase() = runTest {
        every { getEducationModulesUseCase() } returns flowOf(listOf(sampleModule1))
        every { getFavoriteEducationModulesUseCase() } returns flowOf(emptyList())
        coEvery { toggleEducationFavoriteUseCase("bblr_01") } returns Unit

        val viewModel = EducationListViewModel(
            getEducationModulesUseCase = getEducationModulesUseCase,
            getFavoriteEducationModulesUseCase = getFavoriteEducationModulesUseCase,
            toggleEducationFavoriteUseCase = toggleEducationFavoriteUseCase
        )

        viewModel.toggleBookmark("bblr_01")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { toggleEducationFavoriteUseCase("bblr_01") }
    }
}
