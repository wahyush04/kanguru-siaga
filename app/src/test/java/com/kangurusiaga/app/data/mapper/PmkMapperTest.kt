package com.kangurusiaga.app.data.mapper

import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.data.local.entity.PmkReminderEntity
import com.kangurusiaga.app.data.local.entity.PmkSessionEntity
import com.kangurusiaga.app.domain.model.PmkReminder
import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.model.PmkSource
import org.junit.Test

class PmkMapperTest {

    @Test
    fun pmkSessionEntity_toDomain_mapsCorrectly() {
        val entity = PmkSessionEntity(
            id = 5L,
            babyId = 1L,
            startTimeEpoch = 1000L,
            endTimeEpoch = 4600L,
            durationMinutes = 60,
            targetDurationMinutes = 60,
            source = "TIMER",
            babyTemperature = 36.8,
            babyResponse = "Tidur Tenang",
            notes = "Bayi hangat dan tenang"
        )

        val domain = entity.toDomain()

        assertThat(domain.id).isEqualTo(5L)
        assertThat(domain.babyId).isEqualTo(1L)
        assertThat(domain.durationMinutes).isEqualTo(60)
        assertThat(domain.source).isEqualTo(PmkSource.TIMER)
        assertThat(domain.babyTemperature).isEqualTo(36.8)
        assertThat(domain.babyResponse).isEqualTo("Tidur Tenang")
        assertThat(domain.notes).isEqualTo("Bayi hangat dan tenang")
    }

    @Test
    fun pmkSession_toEntity_mapsCorrectly() {
        val domain = PmkSession(
            id = 7L,
            babyId = 2L,
            startTimeEpoch = 2000L,
            endTimeEpoch = 5600L,
            durationMinutes = 60,
            source = PmkSource.MANUAL
        )

        val entity = domain.toEntity()

        assertThat(entity.id).isEqualTo(7L)
        assertThat(entity.source).isEqualTo("MANUAL")
    }

    @Test
    fun pmkReminder_mapping_bidirectionalCorrectness() {
        val reminder = PmkReminder(
            id = 3L,
            babyId = 1L,
            timeHour = 8,
            timeMinute = 30,
            label = "Pagi",
            targetMinutes = 60,
            isEnabled = true
        )

        val entity = reminder.toEntity()
        val mappedBack = entity.toDomain()

        assertThat(mappedBack).isEqualTo(reminder)
        assertThat(reminder.formattedTime).isEqualTo("08:30")
    }
}
