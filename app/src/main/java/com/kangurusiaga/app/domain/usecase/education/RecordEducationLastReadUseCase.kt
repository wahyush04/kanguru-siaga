package com.kangurusiaga.app.domain.usecase.education

import com.kangurusiaga.app.domain.repository.EducationRepository
import javax.inject.Inject

class RecordEducationLastReadUseCase @Inject constructor(
    private val repository: EducationRepository
) {
    suspend operator fun invoke(moduleId: String) {
        repository.recordLastRead(moduleId)
    }
}
