package com.kangurusiaga.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kangurusiaga.app.data.local.dao.BabyDao
import com.kangurusiaga.app.data.local.dao.PmkReminderDao
import com.kangurusiaga.app.data.local.dao.PmkSessionDao
import com.kangurusiaga.app.data.local.entity.BabyEntity
import com.kangurusiaga.app.data.local.entity.PmkReminderEntity
import com.kangurusiaga.app.data.local.entity.PmkSessionEntity

@Database(
    entities = [
        BabyEntity::class,
        PmkSessionEntity::class,
        PmkReminderEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class KanguruDatabase : RoomDatabase() {
    abstract fun babyDao(): BabyDao
    abstract fun pmkSessionDao(): PmkSessionDao
    abstract fun pmkReminderDao(): PmkReminderDao

    companion object {
        const val DATABASE_NAME = "kanguru_siaga_db"
    }
}
