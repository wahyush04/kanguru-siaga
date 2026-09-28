package com.kangurusiaga.app.data.mapper

import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.data.local.entity.FeedingScheduleEntity
import com.kangurusiaga.app.domain.model.FeedingMethod
import com.kangurusiaga.app.domain.model.FeedingSchedule
import com.kangurusiaga.app.domain.model.RepeatType
import org.junit.Test

class FeedingScheduleMapperTest {

    @Test
    fun entityToDomain_mapsAllFieldsCorrectly() {
        val entity = FeedingScheduleEntity(
            id = 5L,
            hour = 14,
            minute = 30,
            volumeMl = 35,
            method = "CUP",
            note = "Minum perlahan",
            isEnabled = true,
            reminderEnabled = true,
            reminderOffsetMinutes = 10,
            repeatType = "DAILY",
            createdAt = 1000L,
            updatedAt = 2000L
        )

        val domain = entity.toDomain()

        assertThat(domain.id).isEqualTo(5L)
        assertThat(domain.hour).isEqualTo(14)
        assertThat(domain.minute).isEqualTo(30)
        assertThat(domain.volumeMl).isEqualTo(35)
        assertThat(domain.method).isEqualTo(FeedingMethod.CUP)
        assertThat(domain.note).isEqualTo("Minum perlahan")
        assertThat(domain.isEnabled).isTrue()
        assertThat(domain.reminderEnabled).isTrue()
        assertThat(domain.reminderOffsetMinutes).isEqualTo(10)
        assertThat(domain.repeatType).isEqualTo(RepeatType.DAILY)
        assertThat(domain.createdAt).isEqualTo(1000L)
        assertThat(domain.updatedAt).isEqualTo(2000L)
    }

    @Test
    fun domainToEntity_mapsAllFieldsCorrectly() {
        val domain = FeedingSchedule(
            id = 10L,
            hour = 8,
            minute = 0,
            volumeMl = 30,
            method = FeedingMethod.OGT_NGT,
            note = null,
            isEnabled = false,
            reminderEnabled = false,
            reminderOffsetMinutes = 15,
            repeatType = RepeatType.ONCE,
            createdAt = 3000L,
            updatedAt = 4000L
        )

        val entity = domain.toEntity()

        assertThat(entity.id).isEqualTo(10L)
        assertThat(entity.hour).isEqualTo(8)
        assertThat(entity.minute).isEqualTo(0)
        assertThat(entity.volumeMl).isEqualTo(30)
        assertThat(entity.method).isEqualTo("OGT_NGT")
        assertThat(entity.note).isNull()
        assertThat(entity.isEnabled).isFalse()
        assertThat(entity.reminderEnabled).isFalse()
        assertThat(entity.reminderOffsetMinutes).isEqualTo(15)
        assertThat(entity.repeatType).isEqualTo("ONCE")
        assertThat(entity.createdAt).isEqualTo(3000L)
        assertThat(entity.updatedAt).isEqualTo(4000L)
    }
}
