package com.kangurusiaga.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kangurusiaga.app.data.local.entity.BabyProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BabyProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: BabyProfileEntity): Long

    @Query("SELECT * FROM baby_profiles WHERE babyId = :babyId LIMIT 1")
    suspend fun getBabyById(babyId: Long): BabyProfileEntity?

    @Query("SELECT * FROM baby_profiles ORDER BY babyId DESC LIMIT 1")
    fun getActiveBabyProfileFlow(): Flow<BabyProfileEntity?>

    @Query("SELECT * FROM baby_profiles ORDER BY babyId DESC LIMIT 1")
    suspend fun getActiveBabyProfile(): BabyProfileEntity?
}
