package com.kangurusiaga.app.data.repository

import com.kangurusiaga.app.data.local.dao.BabyDao
import com.kangurusiaga.app.data.local.dao.FeedingScheduleDao
import com.kangurusiaga.app.data.local.dao.GrowthMeasurementDao
import com.kangurusiaga.app.data.local.dao.InAppNotificationDao
import com.kangurusiaga.app.data.local.dao.PmkReminderDao
import com.kangurusiaga.app.data.local.dao.PmkSessionDao
import com.kangurusiaga.app.data.local.entity.InAppNotificationEntity
import com.kangurusiaga.app.domain.model.InAppNotification
import com.kangurusiaga.app.domain.model.NotificationCategory
import com.kangurusiaga.app.domain.repository.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: InAppNotificationDao,
    private val babyDao: BabyDao,
    private val pmkReminderDao: PmkReminderDao,
    private val pmkSessionDao: PmkSessionDao,
    private val feedingScheduleDao: FeedingScheduleDao,
    private val growthMeasurementDao: GrowthMeasurementDao
) : NotificationRepository {

    override fun getNotifications(babyId: Long): Flow<List<InAppNotification>> {
        return notificationDao.getNotifications(babyId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getUnreadCount(babyId: Long): Flow<Int> {
        return notificationDao.getUnreadCount(babyId)
    }

    override suspend fun markAllAsRead(babyId: Long) = withContext(Dispatchers.IO) {
        notificationDao.markAllAsRead(babyId)
    }

    override suspend fun markAsRead(notificationId: Long) = withContext(Dispatchers.IO) {
        notificationDao.markAsRead(notificationId)
    }

    override suspend fun insertOrUpdate(notification: InAppNotification): Long = withContext(Dispatchers.IO) {
        notificationDao.insertOrUpdate(InAppNotificationEntity.fromDomain(notification))
    }

    override suspend fun cleanupOldNotifications() = withContext(Dispatchers.IO) {
        val thirtyDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30)
        notificationDao.deleteOlderThan(thirtyDaysAgo)
    }

    override suspend fun syncNotifications(babyId: Long) = withContext(Dispatchers.IO) {
        cleanupOldNotifications()

        // Bersihkan notifikasi mock (yang tidak dihasilkan oleh alarm/sistem riil)
        notificationDao.deleteMockNotifications()
        notificationDao.deleteByCategory(babyId, NotificationCategory.GROWTH_WEIGHT.name)
        notificationDao.deleteByCategory(babyId, NotificationCategory.GROWTH_LENGTH.name)
        notificationDao.deleteByCategory(babyId, NotificationCategory.GROWTH_HEAD.name)

        val now = System.currentTimeMillis()
        val existingNotifications = notificationDao.getNotifications(babyId).firstOrNull() ?: emptyList()

        // Perbarui label waktu relatif dan status isToday secara dinamis untuk notifikasi riil yang ada
        existingNotifications.forEach { notif ->
            val diffMillis = (now - notif.timestampEpoch).coerceAtLeast(0L)
            val dynamicTimeLabel = when {
                diffMillis < TimeUnit.MINUTES.toMillis(2) -> "Baru saja"
                diffMillis < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diffMillis)} menit yang lalu"
                diffMillis < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diffMillis)} jam yang lalu"
                diffMillis < TimeUnit.DAYS.toMillis(2) -> "Kemarin"
                else -> "${TimeUnit.MILLISECONDS.toDays(diffMillis)} hari yang lalu"
            }
            val isToday = diffMillis < TimeUnit.DAYS.toMillis(1)
            if (notif.timeLabel != dynamicTimeLabel || notif.isToday != isToday) {
                notificationDao.insertOrUpdate(
                    notif.copy(timeLabel = dynamicTimeLabel, isToday = isToday)
                )
            }
        }
    }
}
