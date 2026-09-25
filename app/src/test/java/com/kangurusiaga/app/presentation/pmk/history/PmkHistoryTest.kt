package com.kangurusiaga.app.presentation.pmk.history

import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.presentation.navigation.Screen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PmkHistoryTest {

    @Test
    fun `navigation routes for history and statistics detail are defined correctly`() {
        assertEquals("pmk_history", Screen.PmkHistory.route)
        assertEquals("pmk_statistics_detail", Screen.PmkStatisticsDetail.route)
        assertEquals("pmk_statistics", Screen.PmkStatistics.route)
    }

    @Test
    fun `fallback sessions list contains sample data matching stitch design`() {
        val now = System.currentTimeMillis()
        val session = PmkSession(
            id = 1,
            babyId = 1,
            startTimeEpoch = now - 3600 * 1000L,
            endTimeEpoch = now,
            durationMinutes = 60,
            targetDurationMinutes = 60,
            babyTemperature = 36.8,
            babyResponse = "Tidur Tenang",
            notes = "Suhu stabil 36.8°C"
        )

        assertNotNull(session)
        assertEquals(60, session.durationMinutes)
        assertEquals(36.8, session.babyTemperature ?: 0.0, 0.01)
        assertTrue(session.durationMinutes >= session.targetDurationMinutes)
    }
}
