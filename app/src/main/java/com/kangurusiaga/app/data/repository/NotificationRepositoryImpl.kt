package com.kangurusiaga.app.data.repository

import com.kangurusiaga.app.data.local.dao.BabyDao
import com.kangurusiaga.app.data.local.dao.FeedingScheduleDao
import com.kangurusiaga.app.data.local.dao.GrowthMeasurementDao
import com.kangurusiaga.app.data.local.dao.InAppNotificationDao
import com.kangurusiaga.app.data.local.dao.PmkReminderDao
import com.kangurusiaga.app.data.local.dao.PmkSessionDao
import com.kangurusiaga.app.data.local.entity.InAppNotificationEntity
import com.kangurusiaga.app.domain.model.GrowthParameter
import com.kangurusiaga.app.domain.model.InAppNotification
import com.kangurusiaga.app.domain.model.NotificationBadgeColor
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

        val babyEntity = babyDao.getBabyById(babyId) ?: return@withContext
        val babyName = babyEntity.name
        val now = System.currentTimeMillis()

        val existingNotifications = notificationDao.getNotifications(babyId).firstOrNull() ?: emptyList()
        val readCategoryMap = existingNotifications.associate { it.category to it.isRead }

        // 1. PMK Pause Alert
        val isReadPause = readCategoryMap[NotificationCategory.PMK_PAUSE.name] ?: false
        notificationDao.insertOrUpdate(
            InAppNotificationEntity(
                id = existingNotifications.find { it.category == NotificationCategory.PMK_PAUSE.name }?.id ?: 0L,
                babyId = babyId,
                category = NotificationCategory.PMK_PAUSE.name,
                referenceId = null,
                title = "Jeda PMK Sudah 15 Menit",
                message = "Sesi jeda perawatan telah mencapai 15 menit. Yuk lanjutkan kembali kontak kulit agar suhu tubuh si kecil tetap terjaga optimal.",
                badgeLabel = "Peringatan Jeda",
                badgeColor = NotificationBadgeColor.AMBER.name,
                timeLabel = "Baru saja",
                targetInfo = null,
                isRead = isReadPause,
                isToday = true,
                primaryActionLabel = "Lanjut Sesi PMK",
                timestampEpoch = now
            )
        )

        // 2. PMK Continuous Mandiri Reminder
        val isReadPmk = readCategoryMap[NotificationCategory.PMK_REMINDER.name] ?: false
        notificationDao.insertOrUpdate(
            InAppNotificationEntity(
                id = existingNotifications.find { it.category == NotificationCategory.PMK_REMINDER.name }?.id ?: 0L,
                babyId = babyId,
                category = NotificationCategory.PMK_REMINDER.name,
                referenceId = null,
                title = "Lanjutkan PMK Kontinu Mandiri di Rumah",
                message = "Pertahankan dekapan kontak kulit kontinu 24 jam di rumah secara bergantian bersama keluarga/pengasuh untuk menjaga suhu stabil & tumbuh kembang optimal si kecil.",
                badgeLabel = "PMK Kontinu Mandiri",
                badgeColor = NotificationBadgeColor.PINK.name,
                timeLabel = "10 menit yang lalu",
                targetInfo = null,
                isRead = isReadPmk,
                isToday = true,
                primaryActionLabel = "Mulai PMK Kontinu",
                timestampEpoch = now - TimeUnit.MINUTES.toMillis(10)
            )
        )

        // 3. Feeding Schedule (ASI)
        val isReadFeeding = readCategoryMap[NotificationCategory.FEEDING_ALARM.name] ?: false
        notificationDao.insertOrUpdate(
            InAppNotificationEntity(
                id = existingNotifications.find { it.category == NotificationCategory.FEEDING_ALARM.name }?.id ?: 0L,
                babyId = babyId,
                category = NotificationCategory.FEEDING_ALARM.name,
                referenceId = null,
                title = "Waktunya Pemberian ASI (OGT/Oral)",
                message = "Jadwal minum ASI 30 ml untuk $babyName. Pastikan posisi bayi semi-elevasi 30°–45° untuk keamanan pencernaan.",
                badgeLabel = "Jadwal ASI 14:00",
                badgeColor = NotificationBadgeColor.BLUE.name,
                timeLabel = "25 menit yang lalu",
                targetInfo = null,
                isRead = isReadFeeding,
                isToday = true,
                primaryActionLabel = "Konfirmasi Selesai",
                secondaryActionLabel = "Buka Detail",
                timestampEpoch = now - TimeUnit.MINUTES.toMillis(25)
            )
        )

        // 4. Weekly Weight Measurement Reminder
        val isReadWeight = readCategoryMap[NotificationCategory.GROWTH_WEIGHT.name] ?: true
        notificationDao.insertOrUpdate(
            InAppNotificationEntity(
                id = existingNotifications.find { it.category == NotificationCategory.GROWTH_WEIGHT.name }?.id ?: 0L,
                babyId = babyId,
                category = NotificationCategory.GROWTH_WEIGHT.name,
                referenceId = null,
                title = "Jadwal Timbang Berat Badan Mingguan",
                message = "Sudah 1 minggu sejak penimbangan terakhir. Catat berat badan terbaru untuk memantau laju pertumbuhan kejar pada kurva Fenton.",
                badgeLabel = "Grafik Fenton (PMA)",
                badgeColor = NotificationBadgeColor.PINK.name,
                timeLabel = "1 jam yang lalu",
                targetInfo = "Target: +15–20 g/kg/hari",
                isRead = isReadWeight,
                isToday = true,
                primaryActionLabel = "Catat Berat Badan",
                timestampEpoch = now - TimeUnit.HOURS.toMillis(1)
            )
        )

        // 5. Length Measurement (Sebelumnya / Mingguan)
        val isReadLength = readCategoryMap[NotificationCategory.GROWTH_LENGTH.name] ?: true
        notificationDao.insertOrUpdate(
            InAppNotificationEntity(
                id = existingNotifications.find { it.category == NotificationCategory.GROWTH_LENGTH.name }?.id ?: 0L,
                babyId = babyId,
                category = NotificationCategory.GROWTH_LENGTH.name,
                referenceId = null,
                title = "Jadwal Ukur Panjang Badan Bayi",
                message = "Waktunya evaluasi panjang badan berkala si kecil menggunakan infantometer papan datar. Pastikan posisi bayi lurus dan nyaman.",
                badgeLabel = "Pantau Panjang",
                badgeColor = NotificationBadgeColor.GREEN.name,
                timeLabel = "Kemarin",
                targetInfo = "Infantometer mat / papan",
                isRead = isReadLength,
                isToday = false,
                primaryActionLabel = "Catat Panjang Badan",
                timestampEpoch = now - TimeUnit.DAYS.toMillis(1)
            )
        )

        // 6. Head Circumference Measurement (Sebelumnya / Mingguan)
        val isReadHead = readCategoryMap[NotificationCategory.GROWTH_HEAD.name] ?: true
        notificationDao.insertOrUpdate(
            InAppNotificationEntity(
                id = existingNotifications.find { it.category == NotificationCategory.GROWTH_HEAD.name }?.id ?: 0L,
                babyId = babyId,
                category = NotificationCategory.GROWTH_HEAD.name,
                referenceId = null,
                title = "Jadwal Ukur Lingkar Kepala Bayi",
                message = "Ukur lingkar kepala $babyName dengan pita ukur non-elastis melewati dahi & tonjolan belakang kepala untuk pantau perkembangan otak.",
                badgeLabel = "Lingkar Kepala",
                badgeColor = NotificationBadgeColor.BLUE.name,
                timeLabel = "2 hari yang lalu",
                targetInfo = "Pertumbuhan Lingkar Kepala",
                isRead = isReadHead,
                isToday = false,
                primaryActionLabel = "Catat Lingkar Kepala",
                timestampEpoch = now - TimeUnit.DAYS.toMillis(2)
            )
        )
    }
}
