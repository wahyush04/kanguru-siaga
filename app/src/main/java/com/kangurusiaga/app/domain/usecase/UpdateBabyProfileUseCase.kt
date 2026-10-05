package com.kangurusiaga.app.domain.usecase

import com.kangurusiaga.app.data.local.dao.BabyProfileDao
import com.kangurusiaga.app.data.local.entity.BabyProfileEntity
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.repository.BabyRepository
import javax.inject.Inject

class UpdateBabyProfileUseCase @Inject constructor(
    private val babyRepository: BabyRepository,
    private val babyProfileDao: BabyProfileDao
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

            // Synchronize to Fenton baby profile reference for accurate percentile evaluations
            val fentonEntity = BabyProfileEntity(
                babyId = sanitizedBaby.id,
                name = sanitizedBaby.name,
                gender = if (sanitizedBaby.gender == Gender.FEMALE) "FEMALE" else "MALE",
                birthDate = sanitizedBaby.birthDateEpochMillis,
                gestationalAgeWeeksAtBirth = sanitizedBaby.gestationalAgeWeeks,
                gestationalAgeDaysAtBirth = 0,
                birthWeightGrams = sanitizedBaby.birthWeightGram.toDouble(),
                birthLengthCm = sanitizedBaby.birthLengthCm.toDouble(),
                birthHeadCircumferenceCm = sanitizedBaby.birthHeadCircumferenceCm.toDouble()
            )
            babyProfileDao.insertProfile(fentonEntity)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
