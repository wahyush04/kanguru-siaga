package com.kangurusiaga.app.data.mapper

import com.google.common.truth.Truth.assertThat
import com.kangurusiaga.app.data.local.entity.BabyEntity
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import org.junit.Test

class BabyMapperTest {

    @Test
    fun toDomain_mapsEntityToDomainCorrectly() {
        val entity = BabyEntity(
            id = 1L,
            name = "Bayi Sehat",
            gender = "MALE",
            birthDateEpochMillis = 1727136000000L,
            birthWeightGram = 2100,
            birthLengthCm = 44.5f,
            birthHeadCircumferenceCm = 31.0f,
            photoUri = "content://photo/1",
            createdAt = 1727136000000L,
            updatedAt = 1727136100000L,
            isActive = true
        )

        val domain = entity.toDomain()

        assertThat(domain.id).isEqualTo(1L)
        assertThat(domain.name).isEqualTo("Bayi Sehat")
        assertThat(domain.gender).isEqualTo(Gender.MALE)
        assertThat(domain.birthWeightGram).isEqualTo(2100)
        assertThat(domain.birthLengthCm).isEqualTo(44.5f)
        assertThat(domain.birthHeadCircumferenceCm).isEqualTo(31.0f)
        assertThat(domain.photoUri).isEqualTo("content://photo/1")
        assertThat(domain.createdAt).isEqualTo(1727136000000L)
        assertThat(domain.updatedAt).isEqualTo(1727136100000L)
    }

    @Test
    fun toEntity_mapsDomainToEntityCorrectly() {
        val domain = Baby(
            id = 2L,
            name = "Bayi Ceria",
            gender = Gender.FEMALE,
            birthDateEpochMillis = 1727136000000L,
            birthWeightGram = 1950,
            birthLengthCm = 43.0f,
            birthHeadCircumferenceCm = 30.0f,
            photoUri = null,
            createdAt = 1727136000000L,
            updatedAt = 1727136100000L
        )

        val entity = domain.toEntity(isActive = true)

        assertThat(entity.id).isEqualTo(2L)
        assertThat(entity.name).isEqualTo("Bayi Ceria")
        assertThat(entity.gender).isEqualTo("FEMALE")
        assertThat(entity.birthWeightGram).isEqualTo(1950)
        assertThat(entity.birthLengthCm).isEqualTo(43.0f)
        assertThat(entity.photoUri).isNull()
        assertThat(entity.createdAt).isEqualTo(1727136000000L)
        assertThat(entity.updatedAt).isEqualTo(1727136100000L)
        assertThat(entity.isActive).isTrue()
    }
}
