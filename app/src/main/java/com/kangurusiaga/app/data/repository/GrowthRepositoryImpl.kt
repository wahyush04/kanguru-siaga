package com.kangurusiaga.app.data.repository

import com.kangurusiaga.app.core.common.AppLogger
import com.kangurusiaga.app.data.local.dao.GrowthMeasurementDao
import com.kangurusiaga.app.data.mapper.toDomain
import com.kangurusiaga.app.data.mapper.toEntity
import com.kangurusiaga.app.data.remote.firestore.BabyFirestoreSyncManager
import com.kangurusiaga.app.domain.model.GrowthMeasurement
import com.kangurusiaga.app.domain.model.GrowthParameter
import com.kangurusiaga.app.domain.repository.GrowthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class GrowthRepositoryImpl @Inject constructor(
    private val dao: GrowthMeasurementDao,
    private val firestoreSyncManager: Provider<BabyFirestoreSyncManager>
) : GrowthRepository {

    override fun getMeasurementsAsc(babyId: Long, parameter: GrowthParameter): Flow<List<GrowthMeasurement>> {
        return dao.getMeasurementsForParameterAsc(babyId, parameter.name).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getMeasurementsDesc(babyId: Long, parameter: GrowthParameter): Flow<List<GrowthMeasurement>> {
        return dao.getMeasurementsForParameterDesc(babyId, parameter.name).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getAllMeasurementsDesc(babyId: Long): Flow<List<GrowthMeasurement>> {
        return dao.getAllMeasurementsForBaby(babyId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getLatestMeasurement(babyId: Long, parameter: GrowthParameter): Flow<GrowthMeasurement?> {
        return dao.getLatestMeasurement(babyId, parameter.name).map { it?.toDomain() }
    }

    override suspend fun getInitialBirthMeasurement(babyId: Long, parameter: GrowthParameter, birthDate: Long): GrowthMeasurement? {
        return dao.getInitialBirthMeasurement(babyId, parameter.name, birthDate)?.toDomain()
    }

    override suspend fun insertMeasurement(measurement: GrowthMeasurement): Long {
        val entity = measurement.toEntity()
        val id = dao.insertMeasurement(entity)
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                firestoreSyncManager.get().syncGrowthMeasurement(entity.copy(id = id))
            } catch (e: Exception) {
                AppLogger.e("GrowthRepository", "Gagal auto-sync growth measurement: ${e.message}", e)
            }
        }
        return id
    }

    override suspend fun updateMeasurement(measurement: GrowthMeasurement) {
        val entity = measurement.toEntity()
        dao.updateMeasurement(entity)
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                firestoreSyncManager.get().syncGrowthMeasurement(entity)
            } catch (e: Exception) {
                AppLogger.e("GrowthRepository", "Gagal auto-sync growth measurement update: ${e.message}", e)
            }
        }
    }

    override suspend fun deleteMeasurement(id: Long) {
        dao.deleteById(id)
    }
}
