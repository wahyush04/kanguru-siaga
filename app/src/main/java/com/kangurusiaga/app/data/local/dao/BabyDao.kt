package com.kangurusiaga.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kangurusiaga.app.data.local.entity.BabyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BabyDao {

    @Query("SELECT * FROM babies WHERE isActive = 1 LIMIT 1")
    fun getActiveBaby(): Flow<BabyEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBaby(baby: BabyEntity): Long

    @Update
    suspend fun updateBaby(baby: BabyEntity)

    @Delete
    suspend fun deleteBaby(baby: BabyEntity)
}
