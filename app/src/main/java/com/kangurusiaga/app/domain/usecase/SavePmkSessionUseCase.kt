package com.kangurusiaga.app.domain.usecase

import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.repository.PmkRepository
import javax.inject.Inject

class SavePmkSessionUseCase @Inject constructor(
    private val pmkRepository: PmkRepository
) {
    suspend operator fun invoke(session: PmkSession): Long {
        return pmkRepository.saveSession(session)
    }
}
