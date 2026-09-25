package com.kangurusiaga.app.data.repository.emergency

import com.kangurusiaga.app.data.local.emergency.EmergencyContentDataSource
import com.kangurusiaga.app.domain.model.emergency.EmergencyWarningModule
import com.kangurusiaga.app.domain.repository.EmergencyWarningRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmergencyWarningRepositoryImpl @Inject constructor(
    private val dataSource: EmergencyContentDataSource
) : EmergencyWarningRepository {

    override fun getModules(): Flow<List<EmergencyWarningModule>> = flow {
        emit(dataSource.getModules())
    }

    override fun getModule(moduleId: String): Flow<EmergencyWarningModule?> = flow {
        emit(dataSource.getModuleById(moduleId))
    }
}
