package com.kangurusiaga.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kangurusiaga.app.data.local.entity.GrowthRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GrowthRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: GrowthRecordEntity): Long

    @Query("SELECT * FROM baby_growth_records WHERE babyId = :babyId ORDER BY pmaWeeks ASC, pmaDays ASC")
    fun getAllRecordsFlow(babyId: Long): Flow<List<GrowthRecordEntity>>

    @Query("SELECT * FROM baby_growth_records WHERE babyId = :babyId ORDER BY pmaWeeks DESC, pmaDays DESC LIMIT 1")
    fun getLatestRecordFlow(babyId: Long): Flow<GrowthRecordEntity?>

    @Query("SELECT * FROM baby_growth_records WHERE babyId = :babyId ORDER BY pmaWeeks ASC, pmaDays ASC LIMIT 1")
    suspend fun getInitialRecord(babyId: Long): GrowthRecordEntity?

    @Delete
    suspend fun deleteRecord(record: GrowthRecordEntity)

    @Query("DELETE FROM baby_growth_records WHERE recordId = :recordId")
    suspend fun deleteRecordById(recordId: Long)
}
