package com.kangurusiaga.app.core.database

import androidx.sqlite.db.SupportSQLiteDatabase
import com.kangurusiaga.app.data.local.AppDatabase
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class RoomMigrationConfigTest {

    @Test
    fun `migration 10 to 11 adds attachment_uri column to premature_screenings`() {
        val db = mockk<SupportSQLiteDatabase>(relaxed = true)

        DatabaseModule.MIGRATION_10_11.migrate(db)

        verify(exactly = 1) {
            db.execSQL("ALTER TABLE premature_screenings ADD COLUMN attachment_uri TEXT DEFAULT NULL")
        }
    }

    @Test
    fun `migration 11 to 12 creates reminder_schedules table with indices`() {
        val db = mockk<SupportSQLiteDatabase>(relaxed = true)

        DatabaseModule.MIGRATION_11_12.migrate(db)

        verify(atLeast = 1) {
            db.execSQL(match { it.contains("CREATE TABLE IF NOT EXISTS `reminder_schedules`") })
            db.execSQL(match { it.contains("index_reminder_schedules_is_enabled_next_trigger_millis") })
            db.execSQL(match { it.contains("index_reminder_schedules_type_reference_id") })
        }
    }

    @Test
    fun `kanguru database registers migrations without Room fallback conflict`() {
        assertEquals(10, DatabaseModule.MIGRATION_10_11.startVersion)
        assertEquals(11, DatabaseModule.MIGRATION_10_11.endVersion)
        assertEquals(11, DatabaseModule.MIGRATION_11_12.startVersion)
        assertEquals(12, DatabaseModule.MIGRATION_11_12.endVersion)
        assertTrue(DatabaseModule.KANGURU_DB_MIGRATIONS.contains(DatabaseModule.MIGRATION_10_11))
        assertTrue(DatabaseModule.KANGURU_DB_MIGRATIONS.contains(DatabaseModule.MIGRATION_11_12))

        val legacyVersions = DatabaseModule.LEGACY_DESTRUCTIVE_FALLBACK_VERSIONS.toSet()
        assertEquals((1..9).toSet(), legacyVersions)
        assertFalse(legacyVersions.contains(11))
        assertFalse(legacyVersions.contains(12))

        // Room throws IllegalArgumentException if any migration start/end version is also in fallbackToDestructiveMigrationFrom
        for (migration in DatabaseModule.KANGURU_DB_MIGRATIONS) {
            assertFalse(
                "Migration startVersion ${migration.startVersion} must not appear in fallbackToDestructiveMigrationFrom",
                legacyVersions.contains(migration.startVersion)
            )
            assertFalse(
                "Migration endVersion ${migration.endVersion} must not appear in fallbackToDestructiveMigrationFrom",
                legacyVersions.contains(migration.endVersion)
            )
        }
    }

    @Test
    fun `app database exposes migration array cleanly`() {
        assertEquals(0, AppDatabase.APP_DB_MIGRATIONS.size)
    }

    @Test
    fun `exported Room schema files exist for current database versions`() {
        val kanguruSchema = File("schemas/com.kangurusiaga.app.core.database.KanguruDatabase/12.json")
        val fentonSchema = File("schemas/com.kangurusiaga.app.data.local.AppDatabase/1.json")

        // Gradle test working dir can be either repo root or app/ depending on runner
        val kanguruResolved = if (kanguruSchema.exists()) kanguruSchema else {
            File("app/schemas/com.kangurusiaga.app.core.database.KanguruDatabase/12.json")
        }
        val fentonResolved = if (fentonSchema.exists()) fentonSchema else {
            File("app/schemas/com.kangurusiaga.app.data.local.AppDatabase/1.json")
        }

        assertTrue("Missing KanguruDatabase v12 schema JSON", kanguruResolved.exists())
        assertTrue("Missing AppDatabase v1 schema JSON", fentonResolved.exists())
    }
}

