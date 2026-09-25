package com.kangurusiaga.app.domain.usecase.emergency

import com.kangurusiaga.app.domain.model.emergency.EmergencyWarningModule
import com.kangurusiaga.app.domain.repository.EmergencyWarningRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEmergencyWarningModulesUseCase @Inject constructor(
    private val repository: EmergencyWarningRepository
) {
    operator fun invoke(): Flow<List<EmergencyWarningModule>> {
        return repository.getModules()
    }
}
