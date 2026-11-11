package com.kangurusiaga.app.core.database

import android.content.Context
import androidx.room.Room
import com.kangurusiaga.app.data.local.dao.BabyDao
import com.kangurusiaga.app.data.local.dao.EducationDao
import com.kangurusiaga.app.data.local.dao.PmkReminderDao
import com.kangurusiaga.app.data.local.dao.PmkSessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    internal val MIGRATION_10_11 = object : androidx.room.migration.Migration(10, 11) {
        override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE premature_screenings ADD COLUMN attachment_uri TEXT DEFAULT NULL")
        }
    }

    // Daftarkan setiap Migration baru (11 -> 12, dst.) di sini sebelum menaikkan version DB.
    internal val KANGURU_DB_MIGRATIONS: Array<androidx.room.migration.Migration> = arrayOf(
        MIGRATION_10_11
    )

    // Hanya versi pra-migrasi tanpa jalur migrasi eksplisit (1..9) yang diizinkan reset jika
    // ditemukan dari build dev lama; v10 memakai MIGRATION_10_11, dan v11+ wajib Migration eksplisit.
    internal val LEGACY_DESTRUCTIVE_FALLBACK_VERSIONS: IntArray = intArrayOf(
        1, 2, 3, 4, 5, 6, 7, 8, 9
    )

    @Provides
    @Singleton
    fun provideKanguruDatabase(
        @ApplicationContext context: Context
    ): KanguruDatabase {
        return Room.databaseBuilder(
            context,
            KanguruDatabase::class.java,
            KanguruDatabase.DATABASE_NAME
        )
            .addMigrations(*KANGURU_DB_MIGRATIONS)
            .fallbackToDestructiveMigrationFrom(*LEGACY_DESTRUCTIVE_FALLBACK_VERSIONS)
            .build()
    }

    @Provides
    @Singleton
    fun provideBabyDao(database: KanguruDatabase): BabyDao {
        return database.babyDao()
    }

    @Provides
    @Singleton
    fun providePmkSessionDao(database: KanguruDatabase): PmkSessionDao {
        return database.pmkSessionDao()
    }

    @Provides
    @Singleton
    fun providePmkReminderDao(database: KanguruDatabase): PmkReminderDao {
        return database.pmkReminderDao()
    }

    @Provides
    @Singleton
    fun provideEducationDao(database: KanguruDatabase): EducationDao {
        return database.educationDao()
    }

    @Provides
    @Singleton
    fun provideFeedingScheduleDao(database: KanguruDatabase): com.kangurusiaga.app.data.local.dao.FeedingScheduleDao {
        return database.feedingScheduleDao()
    }

    @Provides
    @Singleton
    fun provideGrowthMeasurementDao(database: KanguruDatabase): com.kangurusiaga.app.data.local.dao.GrowthMeasurementDao {
        return database.growthMeasurementDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): com.kangurusiaga.app.data.local.AppDatabase {
        return com.kangurusiaga.app.data.local.AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideFentonLmsDao(database: com.kangurusiaga.app.data.local.AppDatabase): com.kangurusiaga.app.data.local.dao.FentonLmsDao {
        return database.fentonLmsDao()
    }

    @Provides
    @Singleton
    fun provideGrowthRecordDao(database: com.kangurusiaga.app.data.local.AppDatabase): com.kangurusiaga.app.data.local.dao.GrowthRecordDao {
        return database.growthRecordDao()
    }

    @Provides
    @Singleton
    fun provideBabyProfileDao(database: com.kangurusiaga.app.data.local.AppDatabase): com.kangurusiaga.app.data.local.dao.BabyProfileDao {
        return database.babyProfileDao()
    }

    @Provides
    @Singleton
    fun provideInAppNotificationDao(database: KanguruDatabase): com.kangurusiaga.app.data.local.dao.InAppNotificationDao {
        return database.inAppNotificationDao()
    }

    @Provides
    @Singleton
    fun providePrematureScreeningDao(database: KanguruDatabase): com.kangurusiaga.app.data.local.dao.PrematureScreeningDao {
        return database.prematureScreeningDao()
    }
}
