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

import com.kangurusiaga.app.data.local.dao.BabyDao
import com.kangurusiaga.app.data.local.entity.BabyEntity
import com.kangurusiaga.app.data.mapper.toDomain
import kotlinx.coroutines.flow.first

class EnsureInitialGrowthDataUseCase @Inject constructor(
    private val growthRepository: GrowthRepository,
    private val fentonGrowthRepository: FentonGrowthRepository,
    private val babyProfileDao: BabyProfileDao,
    private val babyDao: BabyDao
) {
    suspend fun ensureArkaNarendraSampleData(): Baby {
        val existing = babyDao.getBabyByName("%Arka%")
        val calendar = java.util.Calendar.getInstance()
        calendar.set(2026, java.util.Calendar.OCTOBER, 23, 10, 0, 0)
        val w35Date = calendar.timeInMillis
        val oneWeek = 7 * 24 * 60 * 60 * 1000L
        val birthEpoch = w35Date - (3 * oneWeek)

        if (existing == null || existing.birthDateEpochMillis != birthEpoch) {
            babyDao.deactivateAllBabies()
            val arka = BabyEntity(
                id = existing?.id ?: 0,
                name = "Bayi Arka Narendra",
                gender = "MALE",
                birthDateEpochMillis = birthEpoch,
                gestationalAgeWeeks = 32,
                birthWeightGram = 1850,
                birthLengthCm = 42.0f,
                birthHeadCircumferenceCm = 29.5f,
                isActive = true
            )
            babyDao.insertBaby(arka)
        }

        val activeEntity = babyDao.getActiveBabyOnce() ?: babyDao.getFirstBaby()!!
        val activeBaby = activeEntity.toDomain()

        val pmaWeeksList = listOf(32f, 33f, 34f, 35f)
        val weights = listOf(1.850f, 2.000f, 2.160f, 2.350f)
        val lengths = listOf(42.0f, 43.5f, 45.0f, 46.5f)
        val headCircs = listOf(29.5f, 30.6f, 31.8f, 32.8f)

        val currentW = growthRepository.getMeasurementsAsc(activeBaby.id, GrowthParameter.WEIGHT).first()
        val currentL = growthRepository.getMeasurementsAsc(activeBaby.id, GrowthParameter.LENGTH).first()
        val currentHc = growthRepository.getMeasurementsAsc(activeBaby.id, GrowthParameter.HEAD_CIRCUMFERENCE).first()

        for (i in pmaWeeksList.indices) {
            val pma = pmaWeeksList[i]
            val recordDate = birthEpoch + (i * oneWeek)

            if (currentW.none { it.pmaWeeks == pma }) {
                growthRepository.insertMeasurement(
                    GrowthMeasurement(
                        babyId = activeBaby.id,
                        parameter = GrowthParameter.WEIGHT,
                        measurementDateEpochMillis = recordDate,
                        chronologicalAgeWeeks = i.toFloat(),
                        pmaWeeks = pma,
                        value = weights[i],
                        unit = "kg",
                        note = if (i == 0) "Berat Lahir" else "Evaluasi Minggu $i",
                        percentileBadge = "P50 (Ideal)"
                    )
                )
            }

            if (currentL.none { it.pmaWeeks == pma }) {
                growthRepository.insertMeasurement(
                    GrowthMeasurement(
                        babyId = activeBaby.id,
                        parameter = GrowthParameter.LENGTH,
                        measurementDateEpochMillis = recordDate,
                        chronologicalAgeWeeks = i.toFloat(),
                        pmaWeeks = pma,
                        value = lengths[i],
                        unit = "cm",
                        note = if (i == 0) "Panjang Lahir" else "Evaluasi Minggu $i",
                        percentileBadge = "P50 (Ideal)"
                    )
                )
            }

            if (currentHc.none { it.pmaWeeks == pma }) {
                growthRepository.insertMeasurement(
                    GrowthMeasurement(
                        babyId = activeBaby.id,
                        parameter = GrowthParameter.HEAD_CIRCUMFERENCE,
                        measurementDateEpochMillis = recordDate,
                        chronologicalAgeWeeks = i.toFloat(),
                        pmaWeeks = pma,
                        value = headCircs[i],
                        unit = "cm",
                        note = if (i == 0) "Lingkar Kepala Lahir" else "Evaluasi Minggu $i",
                        percentileBadge = "P50 (Optimal)"
                    )
                )
            }

            try {
                val existingFenton = fentonGrowthRepository.getPatientRecords(activeBaby.id).first()
                if (existingFenton.none { it.pmaWeeks == pma.toInt() }) {
                    fentonGrowthRepository.saveNewRecord(
                        GrowthRecordEntity(
                            babyId = activeBaby.id,
                            recordedAt = recordDate,
                            pmaWeeks = pma.toInt(),
                            pmaDays = 0,
                            weightGrams = (weights[i] * 1000).toDouble(),
                            lengthCm = lengths[i].toDouble(),
                            headCircumferenceCm = headCircs[i].toDouble(),
                            calculatedWeightPercentile = "P50"
                        )
                    )
                }
            } catch (e: Exception) {
                AppLogger.w("EnsureInitialGrowthDataUseCase", "Gagal menyimpan record Fenton Arka week $pma: ${e.message}")
            }
        }

        invoke(activeBaby)
        return activeBaby
    }
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
