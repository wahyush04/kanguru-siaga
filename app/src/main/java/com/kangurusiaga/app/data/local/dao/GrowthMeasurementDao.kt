package com.kangurusiaga.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kangurusiaga.app.data.local.entity.GrowthMeasurementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GrowthMeasurementDao {

    @Query("SELECT * FROM growth_measurements WHERE babyId = :babyId AND parameter = :parameter ORDER BY measurementDateEpochMillis ASC")
    fun getMeasurementsForParameterAsc(babyId: Long, parameter: String): Flow<List<GrowthMeasurementEntity>>

    @Query("SELECT * FROM growth_measurements WHERE babyId = :babyId AND parameter = :parameter ORDER BY measurementDateEpochMillis DESC")
    fun getMeasurementsForParameterDesc(babyId: Long, parameter: String): Flow<List<GrowthMeasurementEntity>>

    @Query("SELECT * FROM growth_measurements WHERE babyId = :babyId ORDER BY measurementDateEpochMillis DESC")
    fun getAllMeasurementsForBaby(babyId: Long): Flow<List<GrowthMeasurementEntity>>

    @Query("SELECT * FROM growth_measurements WHERE babyId = :babyId AND parameter = :parameter ORDER BY measurementDateEpochMillis DESC LIMIT 1")
    fun getLatestMeasurement(babyId: Long, parameter: String): Flow<GrowthMeasurementEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(entity: GrowthMeasurementEntity): Long

    @Update
    suspend fun updateMeasurement(entity: GrowthMeasurementEntity)

    @Delete
    suspend fun deleteMeasurement(entity: GrowthMeasurementEntity)

    @Query("DELETE FROM growth_measurements WHERE id = :id")
    suspend fun deleteById(id: Long)
}
