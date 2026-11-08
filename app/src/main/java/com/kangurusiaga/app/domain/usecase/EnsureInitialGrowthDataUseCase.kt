package com.kangurusiaga.app.domain.usecase

import android.content.Context
import com.kangurusiaga.app.core.common.AppLogger
import com.kangurusiaga.app.data.local.dao.BabyDao
import com.kangurusiaga.app.data.local.dao.BabyProfileDao
import com.kangurusiaga.app.data.local.dao.GrowthMeasurementDao
import com.kangurusiaga.app.data.local.dao.GrowthRecordDao
import com.kangurusiaga.app.data.local.entity.BabyProfileEntity
import com.kangurusiaga.app.data.local.entity.GrowthRecordEntity
import com.kangurusiaga.app.data.local.fenton.FentonReferenceData
import com.kangurusiaga.app.data.repository.FentonGrowthRepository
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.GrowthMeasurement
import com.kangurusiaga.app.domain.model.GrowthParameter
import com.kangurusiaga.app.domain.repository.GrowthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.io.File
import javax.inject.Inject

class EnsureInitialGrowthDataUseCase @Inject constructor(
    private val growthRepository: GrowthRepository,
    private val fentonGrowthRepository: FentonGrowthRepository,
    private val babyProfileDao: BabyProfileDao,
    private val babyDao: BabyDao,
    private val growthMeasurementDao: GrowthMeasurementDao,
    private val growthRecordDao: GrowthRecordDao,
    @ApplicationContext private val context: Context
) {
    /**
     * Membersihkan data mock/sample bayi lama (misal "Bayi Arka Narendra" dummy) yang sebelumnya
     * di-generate otomatis saat proses pengembangan, agar Room DB dan cache bersih untuk produksi.
     */
    suspend fun purgeMockDataIfPresent() {
        try {
            val allBabies = babyDao.getAllBabies()
            val mockBabies = allBabies.filter { it.name.contains("Arka Narendra", ignoreCase = true) }
            val realBabies = allBabies.filterNot { it.name.contains("Arka Narendra", ignoreCase = true) }

            for (mockBaby in mockBabies) {
                AppLogger.i("EnsureInitialGrowthDataUseCase", "Membersihkan mock baby: ${mockBaby.name} (id=${mockBaby.id})")
                growthMeasurementDao.deleteAllForBaby(mockBaby.id)
                growthRecordDao.deleteAllForBaby(mockBaby.id)
                babyProfileDao.deleteProfileByBabyId(mockBaby.id)
                babyDao.deleteBabyById(mockBaby.id)

                // Hapus file cache grafik & zscore CSV mock
                try {
                    File(context.cacheDir, "fenton_chart_${mockBaby.id}.jpg").delete()
                    File(context.filesDir, "fenton_zscore_${mockBaby.id}.csv").delete()
                } catch (_: Exception) {}
            }

            // Memastikan hanya ada 1 profil bayi di dalam aplikasi
            if (realBabies.isNotEmpty()) {
                val singleBaby = realBabies.last()
                for (other in realBabies.filter { it.id != singleBaby.id }) {
                    growthMeasurementDao.deleteAllForBaby(other.id)
                    growthRecordDao.deleteAllForBaby(other.id)
                    babyProfileDao.deleteProfileByBabyId(other.id)
                    babyDao.deleteBabyById(other.id)
                }
                babyDao.updateBaby(singleBaby.copy(isActive = true))
                babyDao.deleteAllExcept(singleBaby.id)
                babyProfileDao.deleteAllExcept(singleBaby.id)
                cleanupDuplicateBirthMeasurements(
                    babyId = singleBaby.id,
                    birthPma = singleBaby.gestationalAgeWeeks,
                    birthWeightGram = singleBaby.birthWeightGram,
                    birthLengthCm = singleBaby.birthLengthCm,
                    birthHeadCircumferenceCm = singleBaby.birthHeadCircumferenceCm
                )
                AppLogger.i("EnsureInitialGrowthDataUseCase", "Memastikan satu-satunya profil bayi: ${singleBaby.name} (id=${singleBaby.id})")
            }
        } catch (e: Exception) {
            AppLogger.e("EnsureInitialGrowthDataUseCase", "Gagal membersihkan data mock: ${e.message}", e)
        }
    }

    /**
     * Menghapus duplikasi pencatatan pengukuran yang bernilai identik dengan data lahir pada PMA selain PMA lahir.
     */
    suspend fun cleanupDuplicateBirthMeasurements(
        babyId: Long,
        birthPma: Int,
        birthWeightGram: Int,
        birthLengthCm: Float,
        birthHeadCircumferenceCm: Float
    ) {
        try {
            val allMeasurements = growthMeasurementDao.getAllMeasurementsForBaby(babyId).first()
            for (m in allMeasurements) {
                if (m.note == "Data Saat Ini" && m.pmaWeeks.toInt() != birthPma) {
                    val isDuplicateWeight = m.parameter == GrowthParameter.WEIGHT.name &&
                            (m.value == birthWeightGram / 1000f || (m.value * 1000f).toInt() == birthWeightGram)
                    val isDuplicateLength = m.parameter == GrowthParameter.LENGTH.name && m.value == birthLengthCm
                    val isDuplicateHead = m.parameter == GrowthParameter.HEAD_CIRCUMFERENCE.name && m.value == birthHeadCircumferenceCm
                    if (isDuplicateWeight || isDuplicateLength || isDuplicateHead) {
                        growthMeasurementDao.deleteById(m.id)
                        AppLogger.d("EnsureInitialGrowthDataUseCase", "Menghapus duplikasi measurement ${m.parameter} saat ini (id=${m.id})")
                    }
                }
            }

            val allRecords = growthRecordDao.getAllRecordsFlow(babyId).first()
            for (r in allRecords) {
                if (r.pmaWeeks != birthPma) {
                    val isDuplicate = (r.weightGrams == null || r.weightGrams.toInt() == birthWeightGram) &&
                            (r.lengthCm == null || r.lengthCm.toFloat() == birthLengthCm) &&
                            (r.headCircumferenceCm == null || r.headCircumferenceCm.toFloat() == birthHeadCircumferenceCm)
                    if (isDuplicate) {
                        growthRecordDao.deleteRecordById(r.recordId)
                        AppLogger.d("EnsureInitialGrowthDataUseCase", "Menghapus duplikasi fenton record saat ini (recordId=${r.recordId})")
                    }
                }
            }
        } catch (e: Exception) {
            AppLogger.e("EnsureInitialGrowthDataUseCase", "Gagal membersihkan duplikasi data saat ini: ${e.message}", e)
        }
    }

    suspend operator fun invoke(baby: Baby): Result<Unit> {
        if (baby.birthWeightGram <= 0) {
            return Result.success(Unit)
        }

        return try {
            cleanupDuplicateBirthMeasurements(
                babyId = baby.id,
                birthPma = baby.gestationalAgeWeeks,
                birthWeightGram = baby.birthWeightGram,
                birthLengthCm = baby.birthLengthCm,
                birthHeadCircumferenceCm = baby.birthHeadCircumferenceCm
            )
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
            babyProfileDao.deleteAllExcept(baby.id)

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
