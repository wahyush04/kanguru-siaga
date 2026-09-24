package com.kangurusiaga.app.domain.usecase

import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.repository.BabyRepository
import com.kangurusiaga.app.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class SaveBabyProfileUseCase @Inject constructor(
    private val babyRepository: BabyRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(baby: Baby): Result<Long> {
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
            val babyId = babyRepository.saveBaby(baby.copy(name = baby.name.trim()))
            userPreferencesRepository.setOnboardingCompleted(true)
            Result.success(babyId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
