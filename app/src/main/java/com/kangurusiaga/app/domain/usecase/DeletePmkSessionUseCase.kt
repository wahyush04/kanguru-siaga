package com.kangurusiaga.app.domain.usecase

import com.kangurusiaga.app.domain.repository.PmkRepository
import javax.inject.Inject

class DeletePmkSessionUseCase @Inject constructor(
    private val pmkRepository: PmkRepository
) {
    suspend operator fun invoke(sessionId: Long) {
        pmkRepository.deleteSession(sessionId)
    }
}
