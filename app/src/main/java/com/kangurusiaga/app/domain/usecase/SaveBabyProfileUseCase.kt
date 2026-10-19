package com.kangurusiaga.app.domain.usecase

import com.kangurusiaga.app.core.common.AppDispatchers
import com.kangurusiaga.app.core.common.AppLogger
import com.kangurusiaga.app.core.common.CrashlyticsLogger
import com.kangurusiaga.app.core.common.Dispatcher
import com.kangurusiaga.app.data.remote.firestore.BabyFirestoreSyncManager
import com.kangurusiaga.app.data.local.fenton.FentonReferenceData
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.GrowthMeasurement
import com.kangurusiaga.app.domain.model.GrowthParameter
import com.kangurusiaga.app.domain.repository.BabyRepository
import com.kangurusiaga.app.domain.repository.GrowthRepository
import com.kangurusiaga.app.domain.repository.UserPreferencesRepository
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
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        baby: Baby,
        currentWeightGram: Int = baby.birthWeightGram,
        currentLengthCm: Float = baby.birthLengthCm,
        currentHeadCircumferenceCm: Float = baby.birthHeadCircumferenceCm
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
            AppLogger.d("SaveBabyProfileUseCase", "Profil bayi '${sanitizedBaby.name}' berhasil disimpan ke Room DB (id=$babyId)")

            // 2. Simpan data awal kelahiran (Berat, Panjang, Lingkar Kepala) ke pertumbuhan
            ensureInitialGrowthDataUseCase(savedBaby)

            val now = System.currentTimeMillis()
            val diffMillis = (now - savedBaby.birthDateEpochMillis).coerceAtLeast(0)
            val chronoWeeks = (diffMillis / (1000.0 * 60 * 60 * 24 * 7)).toFloat()
            val currentPma = (savedBaby.gestationalAgeWeeks.toFloat() + chronoWeeks).coerceIn(22f, 50f)

            // 3. Jika berat saat ini diisi dan berbeda dari berat lahir, simpan sebagai pengukuran tambahan
            if (currentWeightGram > 0 && currentWeightGram != savedBaby.birthWeightGram) {
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
                            note = "Saat Pendaftaran",
                            percentileBadge = currentEval.percentileBadge
                        )
                    )
                } catch (e: Exception) {
                    AppLogger.e("SaveBabyProfileUseCase", "Gagal menyimpan berat saat ini: ${e.message}", e)
                }
            }

            // 4. Jika panjang saat ini diisi dan berbeda dari panjang lahir, simpan sebagai pengukuran tambahan
            if (currentLengthCm > 0f && currentLengthCm != savedBaby.birthLengthCm) {
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
                            note = "Saat Pendaftaran",
                            percentileBadge = currentEvalLength.percentileBadge
                        )
                    )
                } catch (e: Exception) {
                    AppLogger.e("SaveBabyProfileUseCase", "Gagal menyimpan panjang saat ini: ${e.message}", e)
                }
            }

            // 5. Jika lingkar kepala saat ini diisi dan berbeda dari lingkar kepala lahir, simpan sebagai pengukuran tambahan
            if (currentHeadCircumferenceCm > 0f && currentHeadCircumferenceCm != savedBaby.birthHeadCircumferenceCm) {
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
                            note = "Saat Pendaftaran",
                            percentileBadge = currentEvalHc.percentileBadge
                        )
                    )
                } catch (e: Exception) {
                    AppLogger.e("SaveBabyProfileUseCase", "Gagal menyimpan lingkar kepala saat ini: ${e.message}", e)
                }
            }

            // 6. Kirim data ke Cloud Firestore secara asinkron (non-blocking) di latar belakang
            CoroutineScope(ioDispatcher + SupervisorJob()).launch {
                try {
                    AppLogger.d("SaveBabyProfileUseCase", "Memulai sinkronisasi background Firestore...")
                    babyFirestoreSyncManager.syncOnboardingBaby(
                        baby = sanitizedBaby,
                        currentWeightGram = currentWeightGram,
                        currentLengthCm = currentLengthCm,
                        currentHeadCircumferenceCm = currentHeadCircumferenceCm
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
