package com.kangurusiaga.app.domain.usecase.notification

import com.kangurusiaga.app.domain.repository.NotificationRepository
import javax.inject.Inject

class MarkNotificationAsReadUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(notificationId: Long) {
        repository.markAsRead(notificationId)
    }
}
