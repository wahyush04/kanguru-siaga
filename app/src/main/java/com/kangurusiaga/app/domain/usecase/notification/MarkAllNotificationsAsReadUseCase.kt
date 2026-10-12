package com.kangurusiaga.app.domain.usecase.notification

import com.kangurusiaga.app.domain.repository.NotificationRepository
import javax.inject.Inject

class MarkAllNotificationsAsReadUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(babyId: Long) {
        repository.markAllAsRead(babyId)
    }
}
