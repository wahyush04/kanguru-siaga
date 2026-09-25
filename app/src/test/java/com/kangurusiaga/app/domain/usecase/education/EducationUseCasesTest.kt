package com.kangurusiaga.app.domain.usecase.education

import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.domain.model.education.EducationModule
import com.kangurusiaga.app.domain.repository.EducationRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class EducationUseCasesTest {

    private val educationRepository: EducationRepository = mockk()

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

    @Test
    fun getEducationModulesUseCase_returnsFlowFromRepository() = runTest {
        val useCase = GetEducationModulesUseCase(educationRepository)
        every { educationRepository.getModules() } returns flowOf(listOf(sampleModule1, sampleModule2))

        val result = useCase().first()

        assertThat(result).containsExactly(sampleModule1, sampleModule2).inOrder()
    }

    @Test
    fun getFavoriteEducationModulesUseCase_returnsOnlyFavoriteModules() = runTest {
        val useCase = GetFavoriteEducationModulesUseCase(educationRepository)
        every { educationRepository.getFavoriteModules() } returns flowOf(listOf(sampleModule2))

        val result = useCase().first()

        assertThat(result).containsExactly(sampleModule2)
        assertThat(result.first().isBookmarked).isTrue()
    }

    @Test
    fun getEducationModuleUseCase_returnsSpecificModule() = runTest {
        val useCase = GetEducationModuleUseCase(educationRepository)
        every { educationRepository.getModule("bblr_01") } returns flowOf(sampleModule1)

        val result = useCase("bblr_01").first()

        assertThat(result).isNotNull()
        assertThat(result?.id).isEqualTo("bblr_01")
        assertThat(result?.title).isEqualTo("Pengertian BBLR")
    }

    @Test
    fun toggleEducationFavoriteUseCase_delegatesToRepository() = runTest {
        val useCase = ToggleEducationFavoriteUseCase(educationRepository)
        coEvery { educationRepository.toggleBookmark("bblr_01") } returns Unit

        useCase("bblr_01")

        coVerify(exactly = 1) { educationRepository.toggleBookmark("bblr_01") }
    }

    @Test
    fun markEducationCompletedUseCase_delegatesToRepository() = runTest {
        val useCase = MarkEducationCompletedUseCase(educationRepository)
        coEvery { educationRepository.markCompleted("bblr_01") } returns Unit

        useCase("bblr_01")

        coVerify(exactly = 1) { educationRepository.markCompleted("bblr_01") }
    }

    @Test
    fun recordEducationLastReadUseCase_delegatesToRepository() = runTest {
        val useCase = RecordEducationLastReadUseCase(educationRepository)
        coEvery { educationRepository.recordLastRead("bblr_01") } returns Unit

        useCase("bblr_01")

        coVerify(exactly = 1) { educationRepository.recordLastRead("bblr_01") }
    }
}
