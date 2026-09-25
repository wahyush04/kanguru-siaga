package com.kangurusiaga.app.domain.usecase.emergency

import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.domain.model.emergency.EmergencySection
import com.kangurusiaga.app.domain.model.emergency.EmergencyWarningModule
import com.kangurusiaga.app.domain.repository.EmergencyWarningRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class EmergencyWarningUseCaseTest {

    private val dummyModules = listOf(
        EmergencyWarningModule(
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
                    items = listOf("Napas cepat atau sulit", "Tarikan dada ke dalam")
                ),
                EmergencySection.ActionAlert(
                    title = "Apa yang harus dilakukan?",
                    message = "Segera hubungi faskes terdekat."
                )
            )
        ),
        EmergencyWarningModule(
            id = "emergency_02",
            order = 2,
            title = "Perubahan Warna Kulit",
            category = "Tanda Kegawatan BBLR",
            iconType = "skin",
            themeColor = "rose",
            heroDrawable = "il_emergency_02",
            heroTag = "Pemantauan Kulit & Perfusi BBLR",
            sections = listOf(
                EmergencySection.SymptomsList(
                    title = "Tanda yang perlu diperhatikan",
                    items = listOf("Kebiruan pada bibir", "Pucat keabu-abuan")
                )
            )
        )
    )

    private val fakeRepository = object : EmergencyWarningRepository {
        override fun getModules(): Flow<List<EmergencyWarningModule>> = flowOf(dummyModules)
        override fun getModule(moduleId: String): Flow<EmergencyWarningModule?> =
            flowOf(dummyModules.firstOrNull { it.id == moduleId })
    }

    private lateinit var getEmergencyWarningModulesUseCase: GetEmergencyWarningModulesUseCase
    private lateinit var getEmergencyWarningModuleUseCase: GetEmergencyWarningModuleUseCase

    @Before
    fun setUp() {
        getEmergencyWarningModulesUseCase = GetEmergencyWarningModulesUseCase(fakeRepository)
        getEmergencyWarningModuleUseCase = GetEmergencyWarningModuleUseCase(fakeRepository)
    }

    @Test
    fun getModules_returnsAllModulesFromRepository() = runTest {
        val result = getEmergencyWarningModulesUseCase().first()
        assertThat(result).hasSize(2)
        assertThat(result[0].id).isEqualTo("emergency_01")
        assertThat(result[1].id).isEqualTo("emergency_02")
    }

    @Test
    fun getModule_withValidId_returnsCorrectModule() = runTest {
        val result = getEmergencyWarningModuleUseCase("emergency_01").first()
        assertThat(result).isNotNull()
        assertThat(result?.title).isEqualTo("Gangguan Pernapasan")
        assertThat(result?.sections).hasSize(2)
    }

    @Test
    fun getModule_withInvalidId_returnsNull() = runTest {
        val result = getEmergencyWarningModuleUseCase("non_existent").first()
        assertThat(result).isNull()
    }
}
