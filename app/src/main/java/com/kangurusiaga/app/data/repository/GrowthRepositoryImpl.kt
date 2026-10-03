package com.kangurusiaga.app.data.repository

import com.kangurusiaga.app.data.local.dao.GrowthMeasurementDao
import com.kangurusiaga.app.data.mapper.toDomain
import com.kangurusiaga.app.data.mapper.toEntity
import com.kangurusiaga.app.domain.model.GrowthMeasurement
import com.kangurusiaga.app.domain.model.GrowthParameter
import com.kangurusiaga.app.domain.repository.GrowthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GrowthRepositoryImpl @Inject constructor(
    private val dao: GrowthMeasurementDao
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

    override suspend fun insertMeasurement(measurement: GrowthMeasurement): Long {
        return dao.insertMeasurement(measurement.toEntity())
    }

    override suspend fun deleteMeasurement(id: Long) {
        dao.deleteById(id)
    }
}
