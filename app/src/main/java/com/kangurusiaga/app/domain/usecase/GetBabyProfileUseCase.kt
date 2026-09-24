package com.kangurusiaga.app.domain.usecase

import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.repository.BabyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBabyProfileUseCase @Inject constructor(
    private val babyRepository: BabyRepository
) {
    operator fun invoke(): Flow<Baby?> {
        return babyRepository.getActiveBaby()
    }
}
