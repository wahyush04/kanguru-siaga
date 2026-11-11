package com.kangurusiaga.app.domain.usecase

import com.kangurusiaga.app.core.common.AppDispatchers
import com.kangurusiaga.app.core.common.AppLogger
import com.kangurusiaga.app.core.common.CrashlyticsLogger
import com.kangurusiaga.app.core.common.Dispatcher
import com.kangurusiaga.app.data.remote.firestore.BabyFirestoreSyncManager
import com.kangurusiaga.app.data.local.entity.GrowthRecordEntity
import com.kangurusiaga.app.data.local.fenton.FentonReferenceData
import com.kangurusiaga.app.data.repository.FentonGrowthRepository
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.GrowthMeasurement
import com.kangurusiaga.app.domain.model.GrowthParameter
import com.kangurusiaga.app.domain.repository.BabyRepository
import com.kangurusiaga.app.domain.repository.GrowthRepository
import com.kangurusiaga.app.domain.repository.UserPreferencesRepository
import com.kangurusiaga.app.domain.util.PmaCalculator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class SaveBabyProfileUseCase @Inject constructor(
    private val babyRepository: BabyRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val babyFirestoreSyncManager: BabyFirestoreSyncManager,
    private val ensureInitialGrowthDataUseCase: EnsureInitialGrowthDataUseCase,
    private val growthRepository: GrowthRepository,
    private val fentonGrowthRepository: FentonGrowthRepository,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        baby: Baby,
        currentWeightGram: Int? = null,
        currentLengthCm: Float? = null,
        currentHeadCircumferenceCm: Float? = null
    ): Result<Long> {
        if (baby.name.trim().isBlank()) {
            return Result.failure(IllegalArgumentException("Nama bayi belum diisi"))
        }
        if (baby.birthWeightGram <= 0) {
            return Result.failure(IllegalArgumentException("Masukkan berat lahir yang valid"))
        }
        if (baby.birthDateEpochMillis <= 0 || baby.birthDateEpochMillis > System.currentTimeMillis()) {
            return Result.failure(IllegalArgumentException("Periksa kembali tanggal lahir"))
        }

        return try {
            val sanitizedBaby = baby.copy(name = baby.name.trim())
            // 1. Simpan ke basis data lokal (Room) sebagai Single Source of Truth
            val babyId = babyRepository.saveBaby(sanitizedBaby)
            val savedBaby = sanitizedBaby.copy(id = babyId)
            userPreferencesRepository.setOnboardingCompleted(true)
            AppLogger.d("SaveBabyProfileUseCase", "Profil bayi berhasil disimpan ke Room DB (id=$babyId)")

            // 2. Simpan data awal kelahiran (Berat, Panjang, Lingkar Kepala) ke pertumbuhan
            ensureInitialGrowthDataUseCase(savedBaby)

            val now = System.currentTimeMillis()
            val ageCalc = PmaCalculator.calculate(
                birthDateEpochMillis = savedBaby.birthDateEpochMillis,
                measurementDateEpochMillis = now,
                gestationalAgeAtBirthWeeks = savedBaby.gestationalAgeWeeks
            )
            val currentPma = ageCalc.pmaWeeks.coerceIn(22f, 50f)
            val currentPmaInt = currentPma.toInt().coerceIn(22, 50)
            val currentPmaDays = (((currentPma - currentPmaInt) * 7).toInt()).coerceIn(0, 6)
            val chronoWeeks = ageCalc.chronologicalWeeks

            val hasExplicitCurrentData = (currentWeightGram != null && currentWeightGram > 0 && currentWeightGram != savedBaby.birthWeightGram) ||
                    (currentLengthCm != null && currentLengthCm > 0f && currentLengthCm != savedBaby.birthLengthCm) ||
                    (currentHeadCircumferenceCm != null && currentHeadCircumferenceCm > 0f && currentHeadCircumferenceCm != savedBaby.birthHeadCircumferenceCm)

            // 3. Simpan data pengukuran saat ini HANYA jika pengguna secara eksplisit memasukkan data saat ini yang berbeda dari data lahir
            if (hasExplicitCurrentData) {
                // Simpan berat saat ini
                if (currentWeightGram != null && currentWeightGram > 0) {
                    try {
                        val currentKg = currentWeightGram / 1000f
                        val currentEval = FentonReferenceData.evaluatePercentile(
                            parameter = GrowthParameter.WEIGHT,
                            gender = savedBaby.gender,
                            pmaWeeks = currentPma,
                            value = currentKg
                        )
                        growthRepository.insertMeasurement(
                            GrowthMeasurement(
                                babyId = babyId,
                                parameter = GrowthParameter.WEIGHT,
                                measurementDateEpochMillis = now,
                                chronologicalAgeWeeks = chronoWeeks,
                                pmaWeeks = currentPma,
                                value = currentKg,
                                unit = GrowthParameter.WEIGHT.unit,
                                note = "Data Saat Ini",
                                percentileBadge = currentEval.percentileBadge
                            )
                        )
                    } catch (e: Exception) {
                        AppLogger.e("SaveBabyProfileUseCase", "Gagal menyimpan berat saat ini: ${e.message}", e)
                    }
                }

                // Simpan panjang saat ini
                if (currentLengthCm != null && currentLengthCm > 0f) {
                    try {
                        val currentEvalLength = FentonReferenceData.evaluatePercentile(
                            parameter = GrowthParameter.LENGTH,
                            gender = savedBaby.gender,
                            pmaWeeks = currentPma,
                            value = currentLengthCm
                        )
                        growthRepository.insertMeasurement(
                            GrowthMeasurement(
                                babyId = babyId,
                                parameter = GrowthParameter.LENGTH,
                                measurementDateEpochMillis = now,
                                chronologicalAgeWeeks = chronoWeeks,
                                pmaWeeks = currentPma,
                                value = currentLengthCm,
                                unit = GrowthParameter.LENGTH.unit,
                                note = "Data Saat Ini",
                                percentileBadge = currentEvalLength.percentileBadge
                            )
                        )
                    } catch (e: Exception) {
                        AppLogger.e("SaveBabyProfileUseCase", "Gagal menyimpan panjang saat ini: ${e.message}", e)
                    }
                }

                // Simpan lingkar kepala saat ini
                if (currentHeadCircumferenceCm != null && currentHeadCircumferenceCm > 0f) {
                    try {
                        val currentEvalHc = FentonReferenceData.evaluatePercentile(
                            parameter = GrowthParameter.HEAD_CIRCUMFERENCE,
                            gender = savedBaby.gender,
                            pmaWeeks = currentPma,
                            value = currentHeadCircumferenceCm
                        )
                        growthRepository.insertMeasurement(
                            GrowthMeasurement(
                                babyId = babyId,
                                parameter = GrowthParameter.HEAD_CIRCUMFERENCE,
                                measurementDateEpochMillis = now,
                                chronologicalAgeWeeks = chronoWeeks,
                                pmaWeeks = currentPma,
                                value = currentHeadCircumferenceCm,
                                unit = GrowthParameter.HEAD_CIRCUMFERENCE.unit,
                                note = "Data Saat Ini",
                                percentileBadge = currentEvalHc.percentileBadge
                            )
                        )
                    } catch (e: Exception) {
                        AppLogger.e("SaveBabyProfileUseCase", "Gagal menyimpan lingkar kepala saat ini: ${e.message}", e)
                    }
                }

                // Simpan juga ke baby_growth_records (Fenton Growth Record)
                try {
                    val weightEval = if (currentWeightGram != null && currentWeightGram > 0) {
                        FentonReferenceData.evaluatePercentile(
                            parameter = GrowthParameter.WEIGHT,
                            gender = savedBaby.gender,
                            pmaWeeks = currentPma,
                            value = currentWeightGram / 1000f
                        )
                    } else null

                    fentonGrowthRepository.saveNewRecord(
                        GrowthRecordEntity(
                            babyId = babyId,
                            recordedAt = now,
                            pmaWeeks = currentPmaInt,
                            pmaDays = currentPmaDays,
                            weightGrams = if (currentWeightGram != null && currentWeightGram > 0) currentWeightGram.toDouble() else null,
                            lengthCm = if (currentLengthCm != null && currentLengthCm > 0f) currentLengthCm.toDouble() else null,
                            headCircumferenceCm = if (currentHeadCircumferenceCm != null && currentHeadCircumferenceCm > 0f) currentHeadCircumferenceCm.toDouble() else null,
                            calculatedWeightPercentile = weightEval?.percentileBadge,
                            calculatedWeightZScore = weightEval?.zScore?.toDouble()
                        )
                    )
                } catch (e: Exception) {
                    AppLogger.e("SaveBabyProfileUseCase", "Gagal menyimpan Fenton Growth Record saat ini: ${e.message}", e)
                }
            }

            // 4. Kirim data ke Cloud Firestore secara asinkron (non-blocking) di latar belakang
            CoroutineScope(ioDispatcher + SupervisorJob()).launch {
                try {
                    AppLogger.d("SaveBabyProfileUseCase", "Memulai sinkronisasi background Firestore...")
                    babyFirestoreSyncManager.syncOnboardingBaby(
                        baby = sanitizedBaby,
                        currentWeightGram = currentWeightGram ?: sanitizedBaby.birthWeightGram,
                        currentLengthCm = currentLengthCm ?: sanitizedBaby.birthLengthCm,
                        currentHeadCircumferenceCm = currentHeadCircumferenceCm ?: sanitizedBaby.birthHeadCircumferenceCm
                    )
                } catch (e: Exception) {
                    AppLogger.e("SaveBabyProfileUseCase", "Exception pada background sync Firestore: ${e.message}", e)
                    CrashlyticsLogger.recordException(e)
                }
            }

            Result.success(babyId)
        } catch (e: Exception) {
            AppLogger.e("SaveBabyProfileUseCase", "Gagal menyimpan ke Room DB: ${e.message}", e)
            Result.failure(e)
        }
    }
}
