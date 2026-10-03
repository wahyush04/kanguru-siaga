package com.kangurusiaga.app.data.repository

import com.kangurusiaga.app.data.local.dao.BabyProfileDao
import com.kangurusiaga.app.data.local.dao.FentonLmsDao
import com.kangurusiaga.app.data.local.dao.GrowthRecordDao
import com.kangurusiaga.app.data.local.entity.BabyProfileEntity
import com.kangurusiaga.app.data.local.entity.FentonLmsEntity
import com.kangurusiaga.app.data.local.entity.GrowthRecordEntity
import com.kangurusiaga.app.data.local.fenton.FentonCurvePoint
import com.kangurusiaga.app.data.local.fenton.GrowthPercentileEvaluation
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.MetricType
import com.kangurusiaga.app.domain.util.FentonCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface FentonGrowthRepository {
    fun getReferenceCurves(metric: MetricType, gender: Gender): Flow<List<FentonLmsEntity>>
    fun getPatientRecords(babyId: Long): Flow<List<GrowthRecordEntity>>
    suspend fun saveNewRecord(record: GrowthRecordEntity): Result<Long>
    fun getLatestRecordFlow(babyId: Long): Flow<GrowthRecordEntity?>
    suspend fun getInitialRecord(babyId: Long): GrowthRecordEntity?
    suspend fun deleteRecord(record: GrowthRecordEntity)
    suspend fun deleteRecordById(recordId: Long)
    fun getCurvePoints(metric: MetricType, gender: Gender): Flow<List<FentonCurvePoint>>
    suspend fun getCurvePointsList(metric: MetricType, gender: Gender): List<FentonCurvePoint>
    suspend fun evaluateMeasurement(
        metric: MetricType,
        gender: Gender,
        pmaWeeks: Int,
        value: Double
    ): GrowthPercentileEvaluation
}

@Singleton
class FentonGrowthRepositoryImpl @Inject constructor(
    private val fentonLmsDao: FentonLmsDao,
    private val growthRecordDao: GrowthRecordDao,
    private val babyProfileDao: BabyProfileDao
) : FentonGrowthRepository {

    override fun getReferenceCurves(metric: MetricType, gender: Gender): Flow<List<FentonLmsEntity>> {
        val genderStr = if (gender == Gender.FEMALE) "FEMALE" else "MALE"
        return fentonLmsDao.getCurvesByMetricAndGender(metric.name, genderStr)
    }

    override fun getPatientRecords(babyId: Long): Flow<List<GrowthRecordEntity>> {
        return growthRecordDao.getAllRecordsFlow(babyId)
    }

    override fun getLatestRecordFlow(babyId: Long): Flow<GrowthRecordEntity?> {
        return growthRecordDao.getLatestRecordFlow(babyId)
    }

    override suspend fun getInitialRecord(babyId: Long): GrowthRecordEntity? {
        return growthRecordDao.getInitialRecord(babyId)
    }

    override suspend fun deleteRecord(record: GrowthRecordEntity) {
        growthRecordDao.deleteRecord(record)
    }

    override suspend fun deleteRecordById(recordId: Long) {
        growthRecordDao.deleteRecordById(recordId)
    }

    override suspend fun saveNewRecord(record: GrowthRecordEntity): Result<Long> {
        return try {
            // Find baby gender to perform accurate LMS lookup
            var babyProfile = babyProfileDao.getBabyById(record.babyId)
            if (babyProfile == null) {
                val defaultProfile = BabyProfileEntity(
                    babyId = record.babyId,
                    name = "Bayi",
                    gender = "MALE",
                    birthDate = System.currentTimeMillis(),
                    gestationalAgeWeeksAtBirth = 32,
                    gestationalAgeDaysAtBirth = 0,
                    birthWeightGrams = 1800.0,
                    birthLengthCm = 42.0,
                    birthHeadCircumferenceCm = 30.0
                )
                babyProfileDao.insertProfile(defaultProfile)
                babyProfile = defaultProfile
            }
            val genderStr = babyProfile?.gender?.uppercase() ?: "MALE"
            val clampedWeek = record.pmaWeeks.coerceIn(22, 50)

            var calculatedZScore: Double? = null
            var calculatedPercentile: String? = null
            var clinicalClass: String? = null

            // Prioritize calculating weight if present
            if (record.weightGrams != null && record.weightGrams > 0) {
                val lms = fentonLmsDao.getLmsByWeek(
                    metric = MetricType.WEIGHT.name,
                    gender = genderStr,
                    week = clampedWeek
                )

                if (lms != null) {
                    val z = FentonCalculator.calculateZScore(
                        x = record.weightGrams,
                        l = lms.l,
                        m = lms.m,
                        s = lms.s
                    )
                    calculatedZScore = z
                    calculatedPercentile = FentonCalculator.getPercentileBadge(z)
                    clinicalClass = FentonCalculator.getClinicalClassification(z)
                }
            } else if (record.lengthCm != null && record.lengthCm > 0) {
                val lms = fentonLmsDao.getLmsByWeek(
                    metric = MetricType.LENGTH.name,
                    gender = genderStr,
                    week = clampedWeek
                )
                if (lms != null) {
                    val z = FentonCalculator.calculateZScore(
                        x = record.lengthCm,
                        l = lms.l,
                        m = lms.m,
                        s = lms.s
                    )
                    calculatedZScore = z
                    calculatedPercentile = FentonCalculator.getPercentileBadge(z)
                    clinicalClass = FentonCalculator.getClinicalClassification(z)
                }
            } else if (record.headCircumferenceCm != null && record.headCircumferenceCm > 0) {
                val lms = fentonLmsDao.getLmsByWeek(
                    metric = MetricType.HEAD_CIRCUMFERENCE.name,
                    gender = genderStr,
                    week = clampedWeek
                )
                if (lms != null) {
                    val z = FentonCalculator.calculateZScore(
                        x = record.headCircumferenceCm,
                        l = lms.l,
                        m = lms.m,
                        s = lms.s
                    )
                    calculatedZScore = z
                    calculatedPercentile = FentonCalculator.getPercentileBadge(z)
                    clinicalClass = FentonCalculator.getClinicalClassification(z)
                }
            }

            val finalRecord = record.copy(
                calculatedWeightZScore = calculatedZScore ?: record.calculatedWeightZScore,
                calculatedWeightPercentile = calculatedPercentile ?: record.calculatedWeightPercentile,
                clinicalClassification = clinicalClass ?: record.clinicalClassification
            )

            val insertedId = growthRecordDao.insertRecord(finalRecord)
            Result.success(insertedId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurvePoints(metric: MetricType, gender: Gender): Flow<List<FentonCurvePoint>> {
        return getReferenceCurves(metric, gender).map { lmsList ->
            mapLmsToCurvePoints(metric, lmsList)
        }
    }

    override suspend fun getCurvePointsList(metric: MetricType, gender: Gender): List<FentonCurvePoint> {
        val genderStr = if (gender == Gender.FEMALE) "FEMALE" else "MALE"
        val lmsList = fentonLmsDao.getCurvesList(metric.name, genderStr)
        return mapLmsToCurvePoints(metric, lmsList)
    }

    override suspend fun evaluateMeasurement(
        metric: MetricType,
        gender: Gender,
        pmaWeeks: Int,
        value: Double
    ): GrowthPercentileEvaluation {
        val genderStr = if (gender == Gender.FEMALE) "FEMALE" else "MALE"
        val clampedWeek = pmaWeeks.coerceIn(22, 50)
        val lms = fentonLmsDao.getLmsByWeek(metric.name, genderStr, clampedWeek)

        if (lms == null) {
            return GrowthPercentileEvaluation(
                percentileBadge = "P50",
                statusText = "Normal",
                isNormal = true,
                differenceFromMedian = 0f,
                medianValue = value.toFloat()
            )
        }

        // Value conversion: if weight, input might be in kg, so convert to grams for LMS calculation
        val isWeight = metric == MetricType.WEIGHT
        val xGramsOrCm = if (isWeight && value < 100.0) value * 1000.0 else value

        val zScore = FentonCalculator.calculateZScore(xGramsOrCm, lms.l, lms.m, lms.s)
        val badge = FentonCalculator.getPercentileBadge(zScore)
        val statusText = FentonCalculator.getStatusDescription(zScore)
        val isNormal = zScore in FentonCalculator.Z_P10..FentonCalculator.Z_P90

        val medianDisplay = if (isWeight) (lms.m / 1000.0).toFloat() else lms.m.toFloat()
        val diffDisplay = (value.toFloat() - medianDisplay)

        return GrowthPercentileEvaluation(
            percentileBadge = badge,
            statusText = statusText,
            isNormal = isNormal,
            differenceFromMedian = diffDisplay,
            medianValue = medianDisplay
        )
    }

    private fun mapLmsToCurvePoints(metric: MetricType, lmsList: List<FentonLmsEntity>): List<FentonCurvePoint> {
        val divisor = if (metric == MetricType.WEIGHT) 1000.0 else 1.0

        return lmsList.map { entity ->
            val p3 = FentonCalculator.calculateValueFromZ(FentonCalculator.Z_P3, entity.l, entity.m, entity.s) / divisor
            val p10 = FentonCalculator.calculateValueFromZ(FentonCalculator.Z_P10, entity.l, entity.m, entity.s) / divisor
            val p50 = entity.m / divisor
            val p90 = FentonCalculator.calculateValueFromZ(FentonCalculator.Z_P90, entity.l, entity.m, entity.s) / divisor
            val p97 = FentonCalculator.calculateValueFromZ(FentonCalculator.Z_P97, entity.l, entity.m, entity.s) / divisor

            FentonCurvePoint(
                pmaWeeks = entity.week.toFloat(),
                p3 = p3.toFloat(),
                p10 = p10.toFloat(),
                p50 = p50.toFloat(),
                p90 = p90.toFloat(),
                p97 = p97.toFloat()
            )
        }
    }
}
