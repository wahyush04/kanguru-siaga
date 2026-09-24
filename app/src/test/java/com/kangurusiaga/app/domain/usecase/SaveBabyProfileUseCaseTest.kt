package com.kangurusiaga.app.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.repository.BabyRepository
import com.kangurusiaga.app.domain.repository.UserPreferencesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SaveBabyProfileUseCaseTest {

    private val babyRepository: BabyRepository = mockk()
    private val userPreferencesRepository: UserPreferencesRepository = mockk(relaxed = true)
    private val useCase = SaveBabyProfileUseCase(babyRepository, userPreferencesRepository)

    @Test
    fun invoke_withBlankName_returnsFailure() = runTest {
        val baby = Baby(
            name = "   ",
            gender = Gender.FEMALE,
            birthDateEpochMillis = System.currentTimeMillis() - 1000,
            birthWeightGram = 2100
        )

        val result = useCase(baby)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Nama bayi belum diisi")
        coVerify(exactly = 0) { babyRepository.saveBaby(any()) }
    }

    @Test
    fun invoke_withInvalidWeight_returnsFailure() = runTest {
        val baby = Baby(
            name = "Bayi Sehat",
            gender = Gender.MALE,
            birthDateEpochMillis = System.currentTimeMillis() - 1000,
            birthWeightGram = 0
        )

        val result = useCase(baby)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Masukkan berat lahir yang valid")
        coVerify(exactly = 0) { babyRepository.saveBaby(any()) }
    }

    @Test
    fun invoke_withFutureBirthDate_returnsFailure() = runTest {
        val baby = Baby(
            name = "Bayi Sehat",
            gender = Gender.MALE,
            birthDateEpochMillis = System.currentTimeMillis() + 86400000L,
            birthWeightGram = 2200
        )

        val result = useCase(baby)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Periksa kembali tanggal lahir")
        coVerify(exactly = 0) { babyRepository.saveBaby(any()) }
    }

    @Test
    fun invoke_withValidData_savesBabyAndSetsOnboardingCompleted() = runTest {
        val baby = Baby(
            name = "  Aisyah Putri  ",
            gender = Gender.FEMALE,
            birthDateEpochMillis = System.currentTimeMillis() - 10000,
            birthWeightGram = 1950,
            photoUri = "file:///data/user/0/com.kangurusiaga.app/files/baby_photos/1.jpg"
        )

        coEvery { babyRepository.saveBaby(any()) } returns 101L

        val result = useCase(baby)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(101L)
        coVerify(exactly = 1) {
            babyRepository.saveBaby(match { it.name == "Aisyah Putri" && it.birthWeightGram == 1950 })
        }
        coVerify(exactly = 1) {
            userPreferencesRepository.setOnboardingCompleted(true)
        }
    }

    @Test
    fun invoke_whenRepositoryFails_returnsFailure() = runTest {
        val baby = Baby(
            name = "Budi",
            gender = Gender.MALE,
            birthDateEpochMillis = System.currentTimeMillis() - 10000,
            birthWeightGram = 2100
        )

        val expectedException = RuntimeException("DB Disk Full")
        coEvery { babyRepository.saveBaby(any()) } throws expectedException

        val result = useCase(baby)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(expectedException)
    }
}
