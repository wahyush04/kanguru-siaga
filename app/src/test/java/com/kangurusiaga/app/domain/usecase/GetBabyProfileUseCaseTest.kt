package com.kangurusiaga.app.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.repository.BabyRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetBabyProfileUseCaseTest {

    private val babyRepository: BabyRepository = mockk()
    private val useCase = GetBabyProfileUseCase(babyRepository)

    @Test
    fun invoke_returnsActiveBabyFromRepository() = runTest {
        val expectedBaby = Baby(
            id = 1L,
            name = "Bayi Pintar",
            gender = Gender.MALE,
            birthDateEpochMillis = 1727136000000L,
            birthWeightGram = 2200,
            birthLengthCm = 45.0f,
            birthHeadCircumferenceCm = 32.0f
        )

        every { babyRepository.getActiveBaby() } returns flowOf(expectedBaby)

        val result = useCase().first()

        assertThat(result).isEqualTo(expectedBaby)
    }

    @Test
    fun invoke_returnsNullWhenNoActiveBaby() = runTest {
        every { babyRepository.getActiveBaby() } returns flowOf(null)

        val result = useCase().first()

        assertThat(result).isNull()
    }
}
