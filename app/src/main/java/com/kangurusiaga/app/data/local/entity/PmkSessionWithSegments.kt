package com.kangurusiaga.app.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class PmkSessionWithSegments(
    @Embedded val session: PmkSessionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "session_id"
    )
    val segments: List<PmkSegmentEntity>
)
