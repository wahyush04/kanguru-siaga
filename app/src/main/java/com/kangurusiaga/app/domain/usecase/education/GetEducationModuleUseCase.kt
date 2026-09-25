package com.kangurusiaga.app.domain.usecase.education

import com.kangurusiaga.app.domain.model.education.EducationModule
import com.kangurusiaga.app.domain.repository.EducationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEducationModuleUseCase @Inject constructor(
    private val repository: EducationRepository
) {
    operator fun invoke(moduleId: String): Flow<EducationModule?> {
        return repository.getModule(moduleId)
    }
}
