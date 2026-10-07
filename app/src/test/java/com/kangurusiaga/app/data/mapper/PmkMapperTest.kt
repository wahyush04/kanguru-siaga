package com.kangurusiaga.app.data.mapper

import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.data.local.entity.PmkReminderEntity
import com.kangurusiaga.app.data.local.entity.PmkSegmentEntity
import com.kangurusiaga.app.data.local.entity.PmkSessionEntity
import com.kangurusiaga.app.data.local.entity.PmkSessionWithSegments
import com.kangurusiaga.app.domain.model.PmkCaregiver
import com.kangurusiaga.app.domain.model.PmkReminder
import com.kangurusiaga.app.domain.model.PmkSegment
import com.kangurusiaga.app.domain.model.PmkSegmentStatus
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
            targetDurationMinutes = 1200,
            source = "TIMER",
            currentCaregiver = "IBU",
            babyTemperature = 36.8,
            babyResponse = "Tidur Tenang",
            notes = "Bayi hangat dan tenang"
        )

        val domain = entity.toDomain()

        assertThat(domain.id).isEqualTo(5L)
        assertThat(domain.babyId).isEqualTo(1L)
        assertThat(domain.durationMinutes).isEqualTo(60)
        assertThat(domain.source).isEqualTo(PmkSource.TIMER)
        assertThat(domain.currentCaregiver).isEqualTo(PmkCaregiver.IBU)
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
            source = PmkSource.MANUAL,
            currentCaregiver = PmkCaregiver.AYAH
        )

        val entity = domain.toEntity()

        assertThat(entity.id).isEqualTo(7L)
        assertThat(entity.source).isEqualTo("MANUAL")
        assertThat(entity.currentCaregiver).isEqualTo("AYAH")
    }

    @Test
    fun pmkSegment_bidirectionalMapping_worksCorrectly() {
        val segment = PmkSegment(
            id = 12L,
            sessionId = 5L,
            caregiver = PmkCaregiver.AYAH,
            startTimeEpoch = 10000L,
            endTimeEpoch = 25000L,
            durationMinutes = 250,
            status = PmkSegmentStatus.ACTIVE,
            babyTemperature = 36.9,
            babyResponse = "Tenang & Rileks",
            notes = "Estafet siang"
        )

        val entity = segment.toEntity()
        val mappedBack = entity.toDomain()

        assertThat(mappedBack.id).isEqualTo(12L)
        assertThat(mappedBack.sessionId).isEqualTo(5L)
        assertThat(mappedBack.caregiver).isEqualTo(PmkCaregiver.AYAH)
        assertThat(mappedBack.status).isEqualTo(PmkSegmentStatus.ACTIVE)
        assertThat(mappedBack.babyTemperature).isEqualTo(36.9)
    }

    @Test
    fun pmkSessionWithSegments_toDomain_mapsCorrectly() {
        val sessionEntity = PmkSessionEntity(
            id = 1L,
            babyId = 10L,
            startTimeEpoch = 1000L,
            endTimeEpoch = 5000L,
            durationMinutes = 60,
            source = "TIMER",
            currentCaregiver = "IBU"
        )
        val segEntity = PmkSegmentEntity(
            id = 100L,
            sessionId = 1L,
            caregiver = "IBU",
            startTimeEpoch = 1000L,
            endTimeEpoch = 5000L,
            durationMinutes = 60,
            status = "ACTIVE"
        )

        val withSegments = PmkSessionWithSegments(
            session = sessionEntity,
            segments = listOf(segEntity)
        )

        val domain = withSegments.toDomain()
        assertThat(domain.segments).hasSize(1)
        assertThat(domain.segments[0].caregiver).isEqualTo(PmkCaregiver.IBU)
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
