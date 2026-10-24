package com.kangurusiaga.app.domain.usecase

import com.kangurusiaga.app.data.local.entity.GrowthRecordEntity
import com.kangurusiaga.app.data.local.fenton.FentonReferenceData
import com.kangurusiaga.app.data.repository.FentonGrowthRepository
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.GrowthMeasurement
import com.kangurusiaga.app.domain.model.GrowthParameter
import com.kangurusiaga.app.domain.repository.GrowthRepository
import javax.inject.Inject

data class BatchRowData(
    val pmaWeeks: Int,
    val weightGrams: Double? = null,
    val headCircumferenceCm: Double? = null,
    val lengthCm: Double? = null
)

class SaveBatchGrowthRecordsUseCase @Inject constructor(
    private val fentonGrowthRepository: FentonGrowthRepository,
    private val growthRepository: GrowthRepository
) {
    suspend operator fun invoke(
        babyId: Long,
        gender: Gender,
        birthDateEpochMillis: Long,
        gestationalAgeWeeksAtBirth: Int,
        rows: List<BatchRowData>,
        measurementDateEpochMillis: Long,
        clinicalNotes: String? = null,
        enableReminder: Boolean = true
    ): Result<Int> {
        return try {
            val validRows = rows.filter { row ->
                row.pmaWeeks in 22..50 && (row.weightGrams != null || row.headCircumferenceCm != null || row.lengthCm != null)
            }

            if (validRows.isEmpty()) {
                return Result.failure(IllegalArgumentException("Tidak ada data pengukuran yang valid untuk disimpan"))
            }

            var savedCount = 0

            for (row in validRows) {
                val clampedPma = row.pmaWeeks.coerceIn(22, 50)
                val chronologicalWeeks = (clampedPma - gestationalAgeWeeksAtBirth).coerceAtLeast(0).toFloat()

                // 1. Evaluate percentile using FentonReferenceData
                var weightPercentileBadge: String? = null
                var weightZScore: Double? = null

                if (row.weightGrams != null && row.weightGrams > 0) {
                    val weightKg = (row.weightGrams / 1000.0).toFloat()
                    val eval = FentonReferenceData.evaluatePercentile(
                        parameter = GrowthParameter.WEIGHT,
                        gender = gender,
                        pmaWeeks = clampedPma.toFloat(),
                        value = weightKg
                    )
                    weightPercentileBadge = eval.percentileBadge
                    weightZScore = eval.zScore.toDouble()

                    // Also save into individual GrowthMeasurement for single parameter charts
                    val weightMeasurement = GrowthMeasurement(
                        babyId = babyId,
                        parameter = GrowthParameter.WEIGHT,
                        measurementDateEpochMillis = measurementDateEpochMillis,
                        chronologicalAgeWeeks = chronologicalWeeks,
                        pmaWeeks = clampedPma.toFloat(),
                        value = weightKg,
                        unit = GrowthParameter.WEIGHT.unit,
                        note = clinicalNotes?.ifBlank { null },
                        percentileBadge = weightPercentileBadge
                    )
                    growthRepository.insertMeasurement(weightMeasurement)
                }

                if (row.lengthCm != null && row.lengthCm > 0) {
                    val eval = FentonReferenceData.evaluatePercentile(
                        parameter = GrowthParameter.LENGTH,
                        gender = gender,
                        pmaWeeks = clampedPma.toFloat(),
                        value = row.lengthCm.toFloat()
                    )
                    val lengthMeasurement = GrowthMeasurement(
                        babyId = babyId,
                        parameter = GrowthParameter.LENGTH,
                        measurementDateEpochMillis = measurementDateEpochMillis,
                        chronologicalAgeWeeks = chronologicalWeeks,
                        pmaWeeks = clampedPma.toFloat(),
                        value = row.lengthCm.toFloat(),
                        unit = GrowthParameter.LENGTH.unit,
                        note = clinicalNotes?.ifBlank { null },
                        percentileBadge = eval.percentileBadge
                    )
                    growthRepository.insertMeasurement(lengthMeasurement)
                }

                if (row.headCircumferenceCm != null && row.headCircumferenceCm > 0) {
                    val eval = FentonReferenceData.evaluatePercentile(
                        parameter = GrowthParameter.HEAD_CIRCUMFERENCE,
                        gender = gender,
                        pmaWeeks = clampedPma.toFloat(),
                        value = row.headCircumferenceCm.toFloat()
                    )
                    val headMeasurement = GrowthMeasurement(
                        babyId = babyId,
                        parameter = GrowthParameter.HEAD_CIRCUMFERENCE,
                        measurementDateEpochMillis = measurementDateEpochMillis,
                        chronologicalAgeWeeks = chronologicalWeeks,
                        pmaWeeks = clampedPma.toFloat(),
                        value = row.headCircumferenceCm.toFloat(),
                        unit = GrowthParameter.HEAD_CIRCUMFERENCE.unit,
                        note = clinicalNotes?.ifBlank { null },
                        percentileBadge = eval.percentileBadge
                    )
                    growthRepository.insertMeasurement(headMeasurement)
                }

                // 2. Save comprehensive Fenton Record to baby_growth_records
                val recordEntity = GrowthRecordEntity(
                    babyId = babyId,
                    recordedAt = measurementDateEpochMillis,
                    pmaWeeks = clampedPma,
                    pmaDays = 0,
                    weightGrams = row.weightGrams,
                    lengthCm = row.lengthCm,
                    headCircumferenceCm = row.headCircumferenceCm,
                    calculatedWeightZScore = weightZScore,
                    calculatedWeightPercentile = weightPercentileBadge
                )
                fentonGrowthRepository.saveNewRecord(recordEntity)
                savedCount++
            }

            Result.success(savedCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
