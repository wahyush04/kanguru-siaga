package com.kangurusiaga.app.domain.usecase

import com.kangurusiaga.app.core.common.AppDispatchers
import com.kangurusiaga.app.core.common.AppLogger
import com.kangurusiaga.app.core.common.CrashlyticsLogger
import com.kangurusiaga.app.core.common.Dispatcher
import com.kangurusiaga.app.data.remote.firestore.BabyFirestoreSyncManager
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.repository.BabyRepository
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
            userPreferencesRepository.setOnboardingCompleted(true)
            AppLogger.d("SaveBabyProfileUseCase", "Profil bayi '${sanitizedBaby.name}' berhasil disimpan ke Room DB (id=$babyId)")

            // 2. Kirim data ke Cloud Firestore secara asinkron (non-blocking) di latar belakang
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
