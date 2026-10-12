package com.kangurusiaga.app.domain.usecase.notification

import com.kangurusiaga.app.domain.model.InAppNotification
import com.kangurusiaga.app.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    operator fun invoke(babyId: Long): Flow<List<InAppNotification>> {
        return repository.getNotifications(babyId)
    }
}
