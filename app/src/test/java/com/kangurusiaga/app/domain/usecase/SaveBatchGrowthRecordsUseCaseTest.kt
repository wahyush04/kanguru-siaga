package com.kangurusiaga.app.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.data.local.entity.GrowthRecordEntity
import com.kangurusiaga.app.data.repository.FentonGrowthRepository
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.repository.GrowthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SaveBatchGrowthRecordsUseCaseTest {

    private val fentonGrowthRepository: FentonGrowthRepository = mockk(relaxed = true)
    private val growthRepository: GrowthRepository = mockk(relaxed = true)

    private lateinit var useCase: SaveBatchGrowthRecordsUseCase

    @Before
    fun setUp() {
        useCase = SaveBatchGrowthRecordsUseCase(
            fentonGrowthRepository = fentonGrowthRepository,
            growthRepository = growthRepository
        )
    }

    @Test
    fun invoke_withValidBatchRows_savesToBothRepositoriesSuccessfully() = runTest {
        coEvery { fentonGrowthRepository.saveNewRecord(any()) } returns Result.success(1L)
        coEvery { growthRepository.insertMeasurement(any()) } returns 1L

        val rows = listOf(
            BatchRowData(pmaWeeks = 34, weightGrams = 1400.0, headCircumferenceCm = 26.0, lengthCm = 40.0),
            BatchRowData(pmaWeeks = 35, weightGrams = 1840.0, headCircumferenceCm = 27.0, lengthCm = 41.0)
        )

        val result = useCase(
            babyId = 1L,
            gender = Gender.MALE,
            birthDateEpochMillis = System.currentTimeMillis() - 86400000L * 21,
            gestationalAgeWeeksAtBirth = 32,
            rows = rows,
            measurementDateEpochMillis = System.currentTimeMillis(),
            clinicalNotes = "HMF 2x/hari",
            enableReminder = true
        )

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(2)

        coVerify(exactly = 2) { fentonGrowthRepository.saveNewRecord(any()) }
        // 2 rows * 3 metrics (weight, length, head) = 6 individual measurements
        coVerify(exactly = 6) { growthRepository.insertMeasurement(any()) }
    }

    @Test
    fun invoke_withEmptyRows_returnsFailure() = runTest {
        val rows = listOf(
            BatchRowData(pmaWeeks = 34, weightGrams = null, headCircumferenceCm = null, lengthCm = null)
        )

        val result = useCase(
            babyId = 1L,
            gender = Gender.MALE,
            birthDateEpochMillis = System.currentTimeMillis() - 86400000L * 21,
            gestationalAgeWeeksAtBirth = 32,
            rows = rows,
            measurementDateEpochMillis = System.currentTimeMillis()
        )

        assertThat(result.isFailure).isTrue()
        coVerify(exactly = 0) { fentonGrowthRepository.saveNewRecord(any()) }
    }
}
