package com.kangurusiaga.app.data.repository

import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.data.local.dao.BabyProfileDao
import com.kangurusiaga.app.data.local.dao.FentonLmsDao
import com.kangurusiaga.app.data.local.dao.GrowthRecordDao
import com.kangurusiaga.app.data.local.entity.BabyProfileEntity
import com.kangurusiaga.app.data.local.entity.FentonLmsEntity
import com.kangurusiaga.app.data.local.entity.GrowthRecordEntity
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.MetricType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class FentonGrowthRepositoryTest {

    private val fentonLmsDao: FentonLmsDao = mockk(relaxed = true)
    private val growthRecordDao: GrowthRecordDao = mockk(relaxed = true)
    private val babyProfileDao: BabyProfileDao = mockk(relaxed = true)

    private lateinit var repository: FentonGrowthRepository

    @Before
    fun setUp() {
        repository = FentonGrowthRepositoryImpl(
            fentonLmsDao = fentonLmsDao,
            growthRecordDao = growthRecordDao,
            babyProfileDao = babyProfileDao
        )
    }

    @Test
    fun saveNewRecord_calculatesZScoreAndSgaAgaLgaStatusAutomatically() = runTest {
        val babyId = 1L
        val week = 28
        val babyProfile = BabyProfileEntity(
            babyId = babyId,
            name = "Bayi Uji",
            gender = "MALE",
            birthDate = 1000000L,
            gestationalAgeWeeksAtBirth = 28,
            gestationalAgeDaysAtBirth = 0,
            birthWeightGrams = 1100.0,
            birthLengthCm = 36.0,
            birthHeadCircumferenceCm = 26.0
        )

        coEvery { babyProfileDao.getBabyById(babyId) } returns babyProfile

        // LMS parameters for week 28 Male weight: L=1.36869, M=1078.75, S=0.20767
        val lms = FentonLmsEntity(
            id = 1,
            metric = "WEIGHT",
            gender = "MALE",
            week = week,
            l = 1.36869215,
            m = 1078.745738,
            s = 0.20767184
        )
        coEvery { fentonLmsDao.getLmsByWeek("WEIGHT", "MALE", week) } returns lms

        val recordSlot = slot<GrowthRecordEntity>()
        coEvery { growthRecordDao.insertRecord(capture(recordSlot)) } returns 42L

        // Record with weight exactly at median (1078.75 grams)
        val inputRecord = GrowthRecordEntity(
            babyId = babyId,
            recordedAt = 1000000L,
            pmaWeeks = week,
            pmaDays = 0,
            weightGrams = 1078.75
        )

        val result = repository.saveNewRecord(inputRecord)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(42L)

        // Verify calculations saved into captured entity
        val saved = recordSlot.captured
        assertThat(saved.calculatedWeightZScore).isNotNull()
        assertThat(saved.calculatedWeightZScore!!).isWithin(0.01).of(0.0)
        assertThat(saved.calculatedWeightPercentile).isEqualTo("P50")
        assertThat(saved.clinicalClassification).isEqualTo("AGA")
    }

    @Test
    fun saveNewRecord_classifiesSgaWhenWeightIsVeryLow() = runTest {
        val babyId = 2L
        val week = 28
        val babyProfile = BabyProfileEntity(
            babyId = babyId,
            name = "Bayi KMK",
            gender = "FEMALE",
            birthDate = 1000000L,
            gestationalAgeWeeksAtBirth = 28,
            gestationalAgeDaysAtBirth = 0,
            birthWeightGrams = 600.0,
            birthLengthCm = 32.0,
            birthHeadCircumferenceCm = 23.0
        )
        coEvery { babyProfileDao.getBabyById(babyId) } returns babyProfile

        // Female week 28 weight LMS
        val lms = FentonLmsEntity(
            id = 2,
            metric = "WEIGHT",
            gender = "FEMALE",
            week = week,
            l = 1.25,
            m = 1017.0,
            s = 0.21
        )
        coEvery { fentonLmsDao.getLmsByWeek("WEIGHT", "FEMALE", week) } returns lms

        val recordSlot = slot<GrowthRecordEntity>()
        coEvery { growthRecordDao.insertRecord(capture(recordSlot)) } returns 99L

        // Severe low weight: 600g (P10 is around 713g, P3 around 558g)
        val inputRecord = GrowthRecordEntity(
            babyId = babyId,
            recordedAt = 1000000L,
            pmaWeeks = week,
            pmaDays = 0,
            weightGrams = 600.0
        )

        val result = repository.saveNewRecord(inputRecord)
        assertThat(result.isSuccess).isTrue()

        val saved = recordSlot.captured
        assertThat(saved.clinicalClassification).isEqualTo("SGA")
    }

    @Test
    fun getCurvePoints_transformsLmsEntitiesToCurvePointsAccurately() = runTest {
        val lmsList = listOf(
            FentonLmsEntity(
                id = 1,
                metric = "WEIGHT",
                gender = "MALE",
                week = 23,
                l = 0.79707872,
                m = 592.5751,
                s = 0.15053941
            )
        )
        coEvery { fentonLmsDao.getCurvesByMetricAndGender("WEIGHT", "MALE") } returns flowOf(lmsList)

        val curvePoints = repository.getCurvePoints(MetricType.WEIGHT, Gender.MALE).first()

        assertThat(curvePoints).hasSize(1)
        val pt = curvePoints.first()
        assertThat(pt.pmaWeeks).isEqualTo(23f)
        // M = 592.5751 g -> 0.593 kg
        assertThat(pt.p50.toDouble()).isWithin(0.01).of(0.593)
        // P3 is ~ 0.430 kg
        assertThat(pt.p3.toDouble()).isWithin(0.01).of(0.430)
        // P97 is ~ 0.765 kg
        assertThat(pt.p97.toDouble()).isWithin(0.01).of(0.765)
    }
}
