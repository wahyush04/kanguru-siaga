package com.kangurusiaga.app.data.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kangurusiaga.app.data.local.dao.BabyProfileDao
import com.kangurusiaga.app.data.local.dao.FentonLmsDao
import com.kangurusiaga.app.data.local.dao.GrowthRecordDao
import com.kangurusiaga.app.data.local.entity.BabyProfileEntity
import com.kangurusiaga.app.data.local.entity.FentonLmsEntity
import com.kangurusiaga.app.data.local.entity.GrowthRecordEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

@Database(
    entities = [
        FentonLmsEntity::class,
        BabyProfileEntity::class,
        GrowthRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun fentonLmsDao(): FentonLmsDao
    abstract fun growthRecordDao(): GrowthRecordDao
    abstract fun babyProfileDao(): BabyProfileDao

    companion object {
        private const val TAG = "AppDatabase"
        const val DATABASE_NAME = "fenton_growth.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .addCallback(DatabaseCallback(context.applicationContext))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val appContext: Context
        ) : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Log.d(TAG, "Database onCreate triggered, populating Fenton LMS dataset...")
                prepopulateLmsReference(appContext, db)
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // Ensure data exists in case pre-population was interrupted
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val cursor = db.query("SELECT COUNT(*) FROM fenton_lms_reference", emptyArray())
                        var count = 0
                        if (cursor.moveToFirst()) {
                            count = cursor.getInt(0)
                        }
                        cursor.close()
                        if (count == 0) {
                            Log.d(TAG, "fenton_lms_reference is empty onOpen, populating now...")
                            prepopulateLmsReference(appContext, db)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error checking fenton_lms_reference count onOpen", e)
                    }
                }
            }
        }

        fun prepopulateLmsReference(context: Context, db: SupportSQLiteDatabase) {
            try {
                val assetManager = context.assets
                val inputStream = assetManager.open("fenton_2013_lms.json")
                val reader = BufferedReader(InputStreamReader(inputStream))
                val jsonString = reader.use { it.readText() }
                val jsonArray = JSONArray(jsonString)

                val insertSql = """
                    INSERT OR REPLACE INTO fenton_lms_reference 
                    (metric, gender, week, l, m, s) 
                    VALUES (?, ?, ?, ?, ?, ?)
                """.trimIndent()

                val statement = db.compileStatement(insertSql)

                db.beginTransaction()
                try {
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        statement.bindString(1, obj.getString("metric"))
                        statement.bindString(2, obj.getString("gender"))
                        statement.bindLong(3, obj.getLong("week"))
                        statement.bindDouble(4, obj.getDouble("l"))
                        statement.bindDouble(5, obj.getDouble("m"))
                        statement.bindDouble(6, obj.getDouble("s"))
                        statement.executeInsert()
                        statement.clearBindings()
                    }
                    db.setTransactionSuccessful()
                    Log.d(TAG, "Successfully prepopulated ${jsonArray.length()} Fenton LMS rows.")
                } finally {
                    db.endTransaction()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to prepopulate Fenton LMS dataset from JSON", e)
            }
        }
    }
}
