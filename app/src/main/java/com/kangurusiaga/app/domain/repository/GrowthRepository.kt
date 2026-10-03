package com.kangurusiaga.app.domain.repository

import com.kangurusiaga.app.domain.model.GrowthMeasurement
import com.kangurusiaga.app.domain.model.GrowthParameter
import kotlinx.coroutines.flow.Flow

interface GrowthRepository {
    fun getMeasurementsAsc(babyId: Long, parameter: GrowthParameter): Flow<List<GrowthMeasurement>>
    fun getMeasurementsDesc(babyId: Long, parameter: GrowthParameter): Flow<List<GrowthMeasurement>>
    fun getAllMeasurementsDesc(babyId: Long): Flow<List<GrowthMeasurement>>
    fun getLatestMeasurement(babyId: Long, parameter: GrowthParameter): Flow<GrowthMeasurement?>
    suspend fun insertMeasurement(measurement: GrowthMeasurement): Long
    suspend fun deleteMeasurement(id: Long)
}
