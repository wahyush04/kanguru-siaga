package com.kangurusiaga.app.domain.usecase

import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.repository.PmkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPmkSessionsUseCase @Inject constructor(
    private val pmkRepository: PmkRepository
) {
    operator fun invoke(babyId: Long): Flow<List<PmkSession>> {
        return pmkRepository.getSessionsForBaby(babyId)
    }
}
