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
        currentWeightGram: Int = baby.birthWeightGram
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

            // 2. Simpan data awal berat lahir ke pertumbuhan
            ensureInitialGrowthDataUseCase(savedBaby)

            // 3. Jika berat saat ini diisi dan berbeda dari berat lahir, simpan sebagai pengukuran tambahan
            if (currentWeightGram > 0 && currentWeightGram != savedBaby.birthWeightGram) {
                try {
                    val currentKg = currentWeightGram / 1000f
                    val now = System.currentTimeMillis()
                    val diffMillis = (now - savedBaby.birthDateEpochMillis).coerceAtLeast(0)
                    val chronoWeeks = (diffMillis / (1000.0 * 60 * 60 * 24 * 7)).toFloat()
                    val currentPma = (savedBaby.gestationalAgeWeeks.toFloat() + chronoWeeks).coerceIn(22f, 50f)
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

            // 4. Kirim data ke Cloud Firestore secara asinkron (non-blocking) di latar belakang
            CoroutineScope(ioDispatcher + SupervisorJob()).launch {
                try {
                    AppLogger.d("SaveBabyProfileUseCase", "Memulai sinkronisasi background Firestore...")
                    babyFirestoreSyncManager.syncOnboardingBaby(sanitizedBaby, currentWeightGram)
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
