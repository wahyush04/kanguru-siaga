package com.kangurusiaga.app.domain.repository

import com.kangurusiaga.app.domain.model.emergency.EmergencyWarningModule
import kotlinx.coroutines.flow.Flow

interface EmergencyWarningRepository {
    fun getModules(): Flow<List<EmergencyWarningModule>>
    fun getModule(moduleId: String): Flow<EmergencyWarningModule?>
}
