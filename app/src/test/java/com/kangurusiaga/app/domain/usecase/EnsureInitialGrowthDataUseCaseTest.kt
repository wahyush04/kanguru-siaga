package com.kangurusiaga.app.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.data.local.dao.BabyProfileDao
import com.kangurusiaga.app.data.repository.FentonGrowthRepository
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.GrowthMeasurement
import com.kangurusiaga.app.domain.model.GrowthParameter
import com.kangurusiaga.app.domain.repository.GrowthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class EnsureInitialGrowthDataUseCaseTest {

    private val growthRepository: GrowthRepository = mockk(relaxed = true)
    private val fentonGrowthRepository: FentonGrowthRepository = mockk(relaxed = true)
    private val babyProfileDao: BabyProfileDao = mockk(relaxed = true)

    private lateinit var useCase: EnsureInitialGrowthDataUseCase

    @Before
    fun setUp() {
        useCase = EnsureInitialGrowthDataUseCase(
            growthRepository = growthRepository,
            fentonGrowthRepository = fentonGrowthRepository,
            babyProfileDao = babyProfileDao
        )
    }

    @Test
    fun invoke_whenBirthWeightIsZero_skipsAndReturnsSuccess() = runTest {
        val baby = Baby(
            id = 1L,
            name = "Bayi",
            gender = Gender.MALE,
            birthDateEpochMillis = System.currentTimeMillis() - 100000,
            birthWeightGram = 0,
            gestationalAgeWeeks = 32
        )

        val result = useCase(baby)

        assertThat(result.isSuccess).isTrue()
        coVerify(exactly = 0) { growthRepository.insertMeasurement(any()) }
    }

    @Test
    fun invoke_whenInitialMeasurementDoesNotExist_insertsInitialWeightMeasurement() = runTest {
        val birthDate = System.currentTimeMillis() - 86400000L * 7
        val baby = Baby(
            id = 10L,
            name = "Rizky",
            gender = Gender.MALE,
            birthDateEpochMillis = birthDate,
            birthWeightGram = 1800,
            gestationalAgeWeeks = 32
        )

        coEvery {
            growthRepository.getInitialBirthMeasurement(10L, GrowthParameter.WEIGHT, birthDate)
        } returns null
        coEvery { fentonGrowthRepository.getInitialRecord(10L) } returns null

        val result = useCase(baby)

        assertThat(result.isSuccess).isTrue()
        coVerify(exactly = 1) {
            growthRepository.insertMeasurement(match {
                it.babyId == 10L &&
                        it.parameter == GrowthParameter.WEIGHT &&
                        it.value == 1.8f &&
                        it.note == "Berat Lahir" &&
                        it.chronologicalAgeWeeks == 0f &&
                        it.pmaWeeks == 32f
            })
        }
        coVerify(exactly = 1) {
            babyProfileDao.insertProfile(match {
                it.babyId == 10L && it.birthWeightGrams == 1800.0
            })
        }
        coVerify(exactly = 1) {
            fentonGrowthRepository.saveNewRecord(match {
                it.babyId == 10L && it.weightGrams == 1800.0 && it.pmaWeeks == 32
            })
        }
    }

    @Test
    fun invoke_whenInitialMeasurementExistsWithDifferentWeight_updatesExistingMeasurement() = runTest {
        val birthDate = System.currentTimeMillis() - 86400000L * 7
        val baby = Baby(
            id = 10L,
            name = "Rizky",
            gender = Gender.MALE,
            birthDateEpochMillis = birthDate,
            birthWeightGram = 2000,
            gestationalAgeWeeks = 32
        )

        val existingMeasurement = GrowthMeasurement(
            id = 55L,
            babyId = 10L,
            parameter = GrowthParameter.WEIGHT,
            measurementDateEpochMillis = birthDate,
            chronologicalAgeWeeks = 0f,
            pmaWeeks = 32f,
            value = 1.8f,
            unit = "kg",
            note = "Berat Lahir",
            percentileBadge = "P10"
        )

        coEvery {
            growthRepository.getInitialBirthMeasurement(10L, GrowthParameter.WEIGHT, birthDate)
        } returns existingMeasurement

        val result = useCase(baby)

        assertThat(result.isSuccess).isTrue()
        coVerify(exactly = 0) { growthRepository.insertMeasurement(any()) }
        coVerify(exactly = 1) {
            growthRepository.updateMeasurement(match {
                it.id == 55L && it.value == 2.0f && it.note == "Berat Lahir"
            })
        }
    }

    @Test
    fun invoke_whenInitialMeasurementExistsWithSameWeight_doesNotUpdate() = runTest {
        val birthDate = System.currentTimeMillis() - 86400000L * 7
        val baby = Baby(
            id = 10L,
            name = "Rizky",
            gender = Gender.MALE,
            birthDateEpochMillis = birthDate,
            birthWeightGram = 1800,
            gestationalAgeWeeks = 32
        )

        val eval = com.kangurusiaga.app.data.local.fenton.FentonReferenceData.evaluatePercentile(
            GrowthParameter.WEIGHT,
            Gender.MALE,
            32f,
            1.8f
        )

        val existingMeasurement = GrowthMeasurement(
            id = 55L,
            babyId = 10L,
            parameter = GrowthParameter.WEIGHT,
            measurementDateEpochMillis = birthDate,
            chronologicalAgeWeeks = 0f,
            pmaWeeks = 32f,
            value = 1.8f,
            unit = "kg",
            note = "Berat Lahir",
            percentileBadge = eval.percentileBadge
        )

        coEvery {
            growthRepository.getInitialBirthMeasurement(10L, GrowthParameter.WEIGHT, birthDate)
        } returns existingMeasurement

        val result = useCase(baby)

        assertThat(result.isSuccess).isTrue()
        coVerify(exactly = 0) { growthRepository.insertMeasurement(any()) }
        coVerify(exactly = 0) { growthRepository.updateMeasurement(any()) }
    }

    @Test
    fun invoke_whenBirthLengthAndHeadCircumferenceProvided_insertsInitialMeasurements() = runTest {
        val birthDate = System.currentTimeMillis() - 86400000L * 7
        val baby = Baby(
            id = 15L,
            name = "Nirmala",
            gender = Gender.FEMALE,
            birthDateEpochMillis = birthDate,
            birthWeightGram = 1850,
            birthLengthCm = 42f,
            birthHeadCircumferenceCm = 30f,
            gestationalAgeWeeks = 32
        )

        coEvery {
            growthRepository.getInitialBirthMeasurement(15L, any(), birthDate)
        } returns null

        val result = useCase(baby)

        assertThat(result.isSuccess).isTrue()
        // Cek Berat Lahir
        coVerify(exactly = 1) {
            growthRepository.insertMeasurement(match {
                it.babyId == 15L &&
                        it.parameter == GrowthParameter.WEIGHT &&
                        it.value == 1.85f &&
                        it.note == "Berat Lahir"
            })
        }
        // Cek Panjang Lahir
        coVerify(exactly = 1) {
            growthRepository.insertMeasurement(match {
                it.babyId == 15L &&
                        it.parameter == GrowthParameter.LENGTH &&
                        it.value == 42f &&
                        it.note == "Panjang Lahir" &&
                        it.unit == "cm"
            })
        }
        // Cek Lingkar Kepala Lahir
        coVerify(exactly = 1) {
            growthRepository.insertMeasurement(match {
                it.babyId == 15L &&
                        it.parameter == GrowthParameter.HEAD_CIRCUMFERENCE &&
                        it.value == 30f &&
                        it.note == "Lingkar Kepala Lahir" &&
                        it.unit == "cm"
            })
        }
    }
}
