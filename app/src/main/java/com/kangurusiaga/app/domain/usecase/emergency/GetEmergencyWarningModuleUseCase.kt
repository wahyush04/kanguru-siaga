package com.kangurusiaga.app.domain.usecase.emergency

import com.kangurusiaga.app.domain.model.emergency.EmergencyWarningModule
import com.kangurusiaga.app.domain.repository.EmergencyWarningRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEmergencyWarningModuleUseCase @Inject constructor(
    private val repository: EmergencyWarningRepository
) {
    operator fun invoke(moduleId: String): Flow<EmergencyWarningModule?> {
        return repository.getModule(moduleId)
    }
}
