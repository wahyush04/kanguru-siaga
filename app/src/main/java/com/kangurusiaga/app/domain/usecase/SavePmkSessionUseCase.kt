package com.kangurusiaga.app.domain.usecase

import com.kangurusiaga.app.domain.model.PmkSegment
import com.kangurusiaga.app.domain.model.PmkSegmentStatus
import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.repository.PmkRepository
import javax.inject.Inject

class SavePmkSessionUseCase @Inject constructor(
    private val pmkRepository: PmkRepository
) {
    suspend operator fun invoke(session: PmkSession): Long {
        val sessionId = pmkRepository.saveSession(session)
        if (session.segments.isEmpty()) {
            val segment = PmkSegment(
                sessionId = sessionId,
                caregiver = session.currentCaregiver,
                startTimeEpoch = session.startTimeEpoch,
                endTimeEpoch = session.endTimeEpoch,
                durationMinutes = session.durationMinutes,
                status = PmkSegmentStatus.ACTIVE,
                babyTemperature = session.babyTemperature,
                babyResponse = session.babyResponse,
                notes = session.notes
            )
            pmkRepository.saveSegment(segment)
        } else {
            session.segments.forEach { seg ->
                pmkRepository.saveSegment(seg.copy(sessionId = sessionId))
            }
        }
        return sessionId
    }
}
