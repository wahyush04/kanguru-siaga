package com.kangurusiaga.app.domain.usecase

import com.kangurusiaga.app.core.common.AppLogger
import com.kangurusiaga.app.data.local.dao.BabyProfileDao
import com.kangurusiaga.app.data.local.entity.BabyProfileEntity
import com.kangurusiaga.app.data.local.entity.GrowthRecordEntity
import com.kangurusiaga.app.data.local.fenton.FentonReferenceData
import com.kangurusiaga.app.data.repository.FentonGrowthRepository
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.GrowthMeasurement
import com.kangurusiaga.app.domain.model.GrowthParameter
import com.kangurusiaga.app.domain.repository.GrowthRepository
import javax.inject.Inject

class EnsureInitialGrowthDataUseCase @Inject constructor(
    private val growthRepository: GrowthRepository,
    private val fentonGrowthRepository: FentonGrowthRepository,
    private val babyProfileDao: BabyProfileDao
) {
    suspend operator fun invoke(baby: Baby): Result<Unit> {
        if (baby.birthWeightGram <= 0) {
            return Result.success(Unit)
        }

        return try {
            val birthWeightKg = baby.birthWeightGram / 1000f
            val pmaWeeks = baby.gestationalAgeWeeks.toFloat().coerceIn(22f, 50f)
            val pmaInt = baby.gestationalAgeWeeks.coerceIn(22, 50)

            // 1. Sinkronisasi entri ke baby_profiles (referensi evaluasi LMS Fenton)
            val fentonProfile = BabyProfileEntity(
                babyId = baby.id,
                name = baby.name,
                gender = if (baby.gender == Gender.FEMALE) "FEMALE" else "MALE",
                birthDate = baby.birthDateEpochMillis,
                gestationalAgeWeeksAtBirth = baby.gestationalAgeWeeks,
                gestationalAgeDaysAtBirth = 0,
                birthWeightGrams = baby.birthWeightGram.toDouble(),
                birthLengthCm = baby.birthLengthCm.toDouble(),
                birthHeadCircumferenceCm = baby.birthHeadCircumferenceCm.toDouble()
            )
            babyProfileDao.insertProfile(fentonProfile)

            // 2. Evaluasi persentil berat lahir berdasarkan kurva Fenton 2013
            val eval = FentonReferenceData.evaluatePercentile(
                parameter = GrowthParameter.WEIGHT,
                gender = baby.gender,
                pmaWeeks = pmaWeeks,
                value = birthWeightKg
            )

            // 3. Cek apakah sudah ada data awal berat lahir di Room growth_measurements
            val existing = growthRepository.getInitialBirthMeasurement(
                babyId = baby.id,
                parameter = GrowthParameter.WEIGHT,
                birthDate = baby.birthDateEpochMillis
            )

            if (existing == null) {
                val initialMeasurement = GrowthMeasurement(
                    babyId = baby.id,
                    parameter = GrowthParameter.WEIGHT,
                    measurementDateEpochMillis = baby.birthDateEpochMillis,
                    chronologicalAgeWeeks = 0f,
                    pmaWeeks = pmaWeeks,
                    value = birthWeightKg,
                    unit = GrowthParameter.WEIGHT.unit,
                    note = "Berat Lahir",
                    percentileBadge = eval.percentileBadge
                )
                growthRepository.insertMeasurement(initialMeasurement)
                AppLogger.d("EnsureInitialGrowthDataUseCase", "Data awal berat lahir (${birthWeightKg} kg) berhasil disimpan untuk babyId=${baby.id}")
            } else if (existing.note == "Berat Lahir" &&
                (existing.value != birthWeightKg || existing.measurementDateEpochMillis != baby.birthDateEpochMillis || existing.pmaWeeks != pmaWeeks || existing.percentileBadge != eval.percentileBadge)
            ) {
                val updated = existing.copy(
                    measurementDateEpochMillis = baby.birthDateEpochMillis,
                    pmaWeeks = pmaWeeks,
                    value = birthWeightKg,
                    percentileBadge = eval.percentileBadge,
                    updatedAt = System.currentTimeMillis()
                )
                growthRepository.updateMeasurement(updated)
                AppLogger.d("EnsureInitialGrowthDataUseCase", "Data awal berat lahir diperbarui menjadi ${birthWeightKg} kg")
            }

            // 4. Pastikan data tercatat di baby_growth_records (tabel riwayat Fenton)
            val initialFentonRecord = fentonGrowthRepository.getInitialRecord(baby.id)
            if (initialFentonRecord == null) {
                val fentonRecord = GrowthRecordEntity(
                    babyId = baby.id,
                    recordedAt = baby.birthDateEpochMillis,
                    pmaWeeks = pmaInt,
                    pmaDays = 0,
                    weightGrams = baby.birthWeightGram.toDouble(),
                    lengthCm = if (baby.birthLengthCm > 0f) baby.birthLengthCm.toDouble() else null,
                    headCircumferenceCm = if (baby.birthHeadCircumferenceCm > 0f) baby.birthHeadCircumferenceCm.toDouble() else null,
                    calculatedWeightPercentile = eval.percentileBadge
                )
                fentonGrowthRepository.saveNewRecord(fentonRecord)
            }

            // 5. Pastikan data panjang badan & lingkar kepala lahir jika diisi
            if (baby.birthLengthCm > 0f) {
                val existingLength = growthRepository.getInitialBirthMeasurement(
                    babyId = baby.id,
                    parameter = GrowthParameter.LENGTH,
                    birthDate = baby.birthDateEpochMillis
                )
                val evalLength = FentonReferenceData.evaluatePercentile(
                    parameter = GrowthParameter.LENGTH,
                    gender = baby.gender,
                    pmaWeeks = pmaWeeks,
                    value = baby.birthLengthCm
                )
                if (existingLength == null) {
                    growthRepository.insertMeasurement(
                        GrowthMeasurement(
                            babyId = baby.id,
                            parameter = GrowthParameter.LENGTH,
                            measurementDateEpochMillis = baby.birthDateEpochMillis,
                            chronologicalAgeWeeks = 0f,
                            pmaWeeks = pmaWeeks,
                            value = baby.birthLengthCm,
                            unit = GrowthParameter.LENGTH.unit,
                            note = "Panjang Lahir",
                            percentileBadge = evalLength.percentileBadge
                        )
                    )
                } else if (existingLength.note == "Panjang Lahir" &&
                    (existingLength.value != baby.birthLengthCm || existingLength.measurementDateEpochMillis != baby.birthDateEpochMillis || existingLength.pmaWeeks != pmaWeeks)
                ) {
                    growthRepository.updateMeasurement(
                        existingLength.copy(
                            measurementDateEpochMillis = baby.birthDateEpochMillis,
                            pmaWeeks = pmaWeeks,
                            value = baby.birthLengthCm,
                            percentileBadge = evalLength.percentileBadge,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
            }

            if (baby.birthHeadCircumferenceCm > 0f) {
                val existingHc = growthRepository.getInitialBirthMeasurement(
                    babyId = baby.id,
                    parameter = GrowthParameter.HEAD_CIRCUMFERENCE,
                    birthDate = baby.birthDateEpochMillis
                )
                val evalHc = FentonReferenceData.evaluatePercentile(
                    parameter = GrowthParameter.HEAD_CIRCUMFERENCE,
                    gender = baby.gender,
                    pmaWeeks = pmaWeeks,
                    value = baby.birthHeadCircumferenceCm
                )
                if (existingHc == null) {
                    growthRepository.insertMeasurement(
                        GrowthMeasurement(
                            babyId = baby.id,
                            parameter = GrowthParameter.HEAD_CIRCUMFERENCE,
                            measurementDateEpochMillis = baby.birthDateEpochMillis,
                            chronologicalAgeWeeks = 0f,
                            pmaWeeks = pmaWeeks,
                            value = baby.birthHeadCircumferenceCm,
                            unit = GrowthParameter.HEAD_CIRCUMFERENCE.unit,
                            note = "Lingkar Kepala Lahir",
                            percentileBadge = evalHc.percentileBadge
                        )
                    )
                } else if (existingHc.note == "Lingkar Kepala Lahir" &&
                    (existingHc.value != baby.birthHeadCircumferenceCm || existingHc.measurementDateEpochMillis != baby.birthDateEpochMillis || existingHc.pmaWeeks != pmaWeeks)
                ) {
                    growthRepository.updateMeasurement(
                        existingHc.copy(
                            measurementDateEpochMillis = baby.birthDateEpochMillis,
                            pmaWeeks = pmaWeeks,
                            value = baby.birthHeadCircumferenceCm,
                            percentileBadge = evalHc.percentileBadge,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            AppLogger.e("EnsureInitialGrowthDataUseCase", "Gagal memastikan data awal pertumbuhan: ${e.message}", e)
            Result.failure(e)
        }
    }
}
