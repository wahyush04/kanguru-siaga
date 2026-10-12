package com.kangurusiaga.app.domain.usecase.notification

import com.kangurusiaga.app.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUnreadNotificationCountUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    operator fun invoke(babyId: Long): Flow<Int> {
        return repository.getUnreadCount(babyId)
    }
}
