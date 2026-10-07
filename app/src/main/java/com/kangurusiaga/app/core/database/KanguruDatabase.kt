package com.kangurusiaga.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kangurusiaga.app.data.local.dao.BabyDao
import com.kangurusiaga.app.data.local.dao.EducationDao
import com.kangurusiaga.app.data.local.dao.FeedingScheduleDao
import com.kangurusiaga.app.data.local.dao.GrowthMeasurementDao
import com.kangurusiaga.app.data.local.dao.PmkReminderDao
import com.kangurusiaga.app.data.local.dao.PmkSessionDao
import com.kangurusiaga.app.data.local.entity.BabyEntity
import com.kangurusiaga.app.data.local.entity.EducationProgressEntity
import com.kangurusiaga.app.data.local.entity.FeedingScheduleEntity
import com.kangurusiaga.app.data.local.entity.GrowthMeasurementEntity
import com.kangurusiaga.app.data.local.entity.PmkReminderEntity
import com.kangurusiaga.app.data.local.entity.PmkSessionEntity

@Database(
    entities = [
        BabyEntity::class,
        PmkSessionEntity::class,
        com.kangurusiaga.app.data.local.entity.PmkSegmentEntity::class,
        PmkReminderEntity::class,
        EducationProgressEntity::class,
        FeedingScheduleEntity::class,
        GrowthMeasurementEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class KanguruDatabase : RoomDatabase() {
    abstract fun babyDao(): BabyDao
    abstract fun pmkSessionDao(): PmkSessionDao
    abstract fun pmkReminderDao(): PmkReminderDao
    abstract fun educationDao(): EducationDao
    abstract fun feedingScheduleDao(): FeedingScheduleDao
    abstract fun growthMeasurementDao(): GrowthMeasurementDao

    companion object {
        const val DATABASE_NAME = "kanguru_siaga_db"
    }
}
