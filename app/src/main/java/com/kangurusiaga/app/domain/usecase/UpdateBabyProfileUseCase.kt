package com.kangurusiaga.app.domain.usecase

import com.kangurusiaga.app.data.local.dao.BabyProfileDao
import com.kangurusiaga.app.data.local.entity.BabyProfileEntity
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.repository.BabyRepository
import javax.inject.Inject

class UpdateBabyProfileUseCase @Inject constructor(
    private val babyRepository: BabyRepository,
    private val babyProfileDao: BabyProfileDao,
    private val ensureInitialGrowthDataUseCase: EnsureInitialGrowthDataUseCase
) {
    suspend operator fun invoke(baby: Baby): Result<Unit> {
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
            val sanitizedBaby = baby.copy(
                name = baby.name.trim(),
                updatedAt = System.currentTimeMillis()
            )
            babyRepository.updateBaby(sanitizedBaby)

            // Synchronize initial birth growth data & Fenton profile
            ensureInitialGrowthDataUseCase(sanitizedBaby)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
