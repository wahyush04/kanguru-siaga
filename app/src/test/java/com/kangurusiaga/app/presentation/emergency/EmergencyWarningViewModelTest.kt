package com.kangurusiaga.app.presentation.emergency

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.domain.model.emergency.EmergencySection
import com.kangurusiaga.app.domain.model.emergency.EmergencyWarningModule
import com.kangurusiaga.app.domain.usecase.emergency.GetEmergencyWarningModuleUseCase
import com.kangurusiaga.app.domain.usecase.emergency.GetEmergencyWarningModulesUseCase
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
class EmergencyWarningViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getEmergencyWarningModulesUseCase: GetEmergencyWarningModulesUseCase = mockk()
    private val getEmergencyWarningModuleUseCase: GetEmergencyWarningModuleUseCase = mockk()

    private val sampleModule = EmergencyWarningModule(
        id = "emergency_01",
        order = 1,
        title = "Gangguan Pernapasan",
        category = "Tanda Kegawatan BBLR",
        iconType = "lungs",
        themeColor = "rose",
        heroDrawable = "il_emergency_01",
        heroTag = "PEMANTAUAN RESPIRATORI BBLR",
        sections = listOf(
            EmergencySection.SymptomsList(
                title = "Tanda yang perlu diperhatikan",
                items = listOf("Napas terlihat cepat atau sulit")
            ),
            EmergencySection.ActionAlert(
                title = "Apa yang harus dilakukan?",
                message = "Segera hubungi tenaga kesehatan."
            )
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { getEmergencyWarningModulesUseCase() } returns flowOf(listOf(sampleModule))
        every { getEmergencyWarningModuleUseCase("emergency_01") } returns flowOf(sampleModule)
        every { getEmergencyWarningModuleUseCase("unknown") } returns flowOf(null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun listViewModel_loadsModulesSuccessfully() = runTest {
        val viewModel = EmergencyWarningListViewModel(getEmergencyWarningModulesUseCase)

        viewModel.uiState.test {
            val initialState = awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()

            val loadedState = awaitItem()
            assertThat(loadedState.isLoading).isFalse()
            assertThat(loadedState.modules).hasSize(1)
            assertThat(loadedState.modules.first().id).isEqualTo("emergency_01")
            assertThat(loadedState.modules.first().title).isEqualTo("Gangguan Pernapasan")
        }
    }

    @Test
    fun detailViewModel_loadsDetailSuccessfully() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("moduleId" to "emergency_01"))
        val viewModel = EmergencyWarningDetailViewModel(savedStateHandle, getEmergencyWarningModuleUseCase)

        viewModel.uiState.test {
            val initialState = awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()

            val loadedState = awaitItem()
            assertThat(loadedState.isLoading).isFalse()
            assertThat(loadedState.module).isNotNull()
            assertThat(loadedState.module?.title).isEqualTo("Gangguan Pernapasan")
            assertThat(loadedState.module?.sections).hasSize(2)
        }
    }

    @Test
    fun detailViewModel_withUnknownId_showsErrorMessage() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("moduleId" to "unknown"))
        val viewModel = EmergencyWarningDetailViewModel(savedStateHandle, getEmergencyWarningModuleUseCase)

        viewModel.uiState.test {
            val initialState = awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()

            val loadedState = awaitItem()
            assertThat(loadedState.isLoading).isFalse()
            assertThat(loadedState.module).isNull()
            assertThat(loadedState.errorMessage).isEqualTo("Modul tidak ditemukan")
        }
    }
}
