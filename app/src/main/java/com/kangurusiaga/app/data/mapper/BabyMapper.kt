package com.kangurusiaga.app.data.mapper

import com.kangurusiaga.app.data.local.entity.BabyEntity
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender

fun BabyEntity.toDomain(): Baby {
    val domainGender = when (gender.uppercase()) {
        "MALE" -> Gender.MALE
        "FEMALE" -> Gender.FEMALE
        else -> Gender.UNSPECIFIED
    }

    return Baby(
        id = id,
        name = name,
        gender = domainGender,
        birthDateEpochMillis = birthDateEpochMillis,
        birthWeightGram = birthWeightGram,
        birthLengthCm = birthLengthCm,
        birthHeadCircumferenceCm = birthHeadCircumferenceCm,
        photoUri = photoUri,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Baby.toEntity(isActive: Boolean = true): BabyEntity {
    return BabyEntity(
        id = id,
        name = name,
        gender = gender.name,
        birthDateEpochMillis = birthDateEpochMillis,
        birthWeightGram = birthWeightGram,
        birthLengthCm = birthLengthCm,
        birthHeadCircumferenceCm = birthHeadCircumferenceCm,
        photoUri = photoUri,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isActive = isActive
    )
}
