package com.kangurusiaga.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kangurusiaga.app.data.local.entity.FentonLmsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FentonLmsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lmsList: List<FentonLmsEntity>)

    @Query("SELECT * FROM fenton_lms_reference WHERE metric = :metric AND gender = :gender AND week = :week LIMIT 1")
    suspend fun getLmsByWeek(metric: String, gender: String, week: Int): FentonLmsEntity?

    @Query("SELECT * FROM fenton_lms_reference WHERE metric = :metric AND gender = :gender ORDER BY week ASC")
    fun getCurvesByMetricAndGender(metric: String, gender: String): Flow<List<FentonLmsEntity>>

    @Query("SELECT * FROM fenton_lms_reference WHERE metric = :metric AND gender = :gender ORDER BY week ASC")
    suspend fun getCurvesList(metric: String, gender: String): List<FentonLmsEntity>

    @Query("SELECT COUNT(*) FROM fenton_lms_reference")
    suspend fun getCount(): Int
}
