package com.kangurusiaga.app.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.data.remote.firestore.BabyFirestoreSyncManager
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.repository.BabyRepository
import com.kangurusiaga.app.domain.repository.UserPreferencesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SaveBabyProfileUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private val babyRepository: BabyRepository = mockk()
    private val userPreferencesRepository: UserPreferencesRepository = mockk(relaxed = true)
    private val babyFirestoreSyncManager: BabyFirestoreSyncManager = mockk(relaxed = true)

    private val useCase = SaveBabyProfileUseCase(
        babyRepository = babyRepository,
        userPreferencesRepository = userPreferencesRepository,
        babyFirestoreSyncManager = babyFirestoreSyncManager,
        ioDispatcher = testDispatcher
    )

    @Test
    fun invoke_withBlankName_returnsFailure() = runTest(testDispatcher) {
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
        coVerify(exactly = 0) { babyFirestoreSyncManager.syncOnboardingBaby(any(), any()) }
    }

    @Test
    fun invoke_withInvalidWeight_returnsFailure() = runTest(testDispatcher) {
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
        coVerify(exactly = 0) { babyFirestoreSyncManager.syncOnboardingBaby(any(), any()) }
    }

    @Test
    fun invoke_withFutureBirthDate_returnsFailure() = runTest(testDispatcher) {
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
        coVerify(exactly = 0) { babyFirestoreSyncManager.syncOnboardingBaby(any(), any()) }
    }

    @Test
    fun invoke_withValidData_savesBabyAndTriggersFirestoreSync() = runTest(testDispatcher) {
        val baby = Baby(
            name = "  Aisyah Putri  ",
            gender = Gender.FEMALE,
            birthDateEpochMillis = System.currentTimeMillis() - 10000,
            birthWeightGram = 1950,
            photoUri = "file:///data/user/0/com.kangurusiaga.app/files/baby_photos/1.jpg"
        )

        coEvery { babyRepository.saveBaby(any()) } returns 101L
        coEvery { babyFirestoreSyncManager.syncOnboardingBaby(any(), any()) } returns true

        val result = useCase(baby, currentWeightGram = 2100)
        advanceUntilIdle()

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(101L)
        coVerify(exactly = 1) {
            babyRepository.saveBaby(match { it.name == "Aisyah Putri" && it.birthWeightGram == 1950 })
        }
        coVerify(exactly = 1) {
            userPreferencesRepository.setOnboardingCompleted(true)
        }
        coVerify(exactly = 1) {
            babyFirestoreSyncManager.syncOnboardingBaby(
                match { it.name == "Aisyah Putri" },
                2100
            )
        }
    }

    @Test
    fun invoke_whenFirestoreFails_localSaveStillSucceedsNonBlocking() = runTest(testDispatcher) {
        val baby = Baby(
            name = "Aisyah Putri",
            gender = Gender.FEMALE,
            birthDateEpochMillis = System.currentTimeMillis() - 10000,
            birthWeightGram = 1950
        )

        coEvery { babyRepository.saveBaby(any()) } returns 102L
        coEvery { babyFirestoreSyncManager.syncOnboardingBaby(any(), any()) } throws RuntimeException("Network timeout")

        val result = useCase(baby)
        advanceUntilIdle()

        // Sisi lokal tetap sukses dan onboarding tetap selesai
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(102L)
        coVerify(exactly = 1) { userPreferencesRepository.setOnboardingCompleted(true) }
    }

    @Test
    fun invoke_whenRepositoryFails_returnsFailureAndSkipsFirestore() = runTest(testDispatcher) {
        val baby = Baby(
            name = "Budi",
            gender = Gender.MALE,
            birthDateEpochMillis = System.currentTimeMillis() - 10000,
            birthWeightGram = 2100
        )

        val expectedException = RuntimeException("DB Disk Full")
        coEvery { babyRepository.saveBaby(any()) } throws expectedException

        val result = useCase(baby)
        advanceUntilIdle()

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(expectedException)
        coVerify(exactly = 0) { babyFirestoreSyncManager.syncOnboardingBaby(any(), any()) }
    }
}
