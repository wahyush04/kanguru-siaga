package com.kangurusiaga.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kangurusiaga.app.data.local.dao.BabyDao
import com.kangurusiaga.app.data.local.entity.BabyEntity

@Database(
    entities = [BabyEntity::class],
    version = 1,
    exportSchema = false
)
abstract class KanguruDatabase : RoomDatabase() {
    abstract fun babyDao(): BabyDao

    companion object {
        const val DATABASE_NAME = "kanguru_siaga_db"
    }
}
