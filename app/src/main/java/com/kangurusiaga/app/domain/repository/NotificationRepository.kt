package com.kangurusiaga.app.domain.repository

import com.kangurusiaga.app.domain.model.InAppNotification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getNotifications(babyId: Long): Flow<List<InAppNotification>>
    fun getUnreadCount(babyId: Long): Flow<Int>
    suspend fun markAllAsRead(babyId: Long)
    suspend fun markAsRead(notificationId: Long)
    suspend fun syncNotifications(babyId: Long)
    suspend fun insertOrUpdate(notification: InAppNotification): Long
    suspend fun cleanupOldNotifications()
}
