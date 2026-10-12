package com.kangurusiaga.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kangurusiaga.app.data.local.entity.InAppNotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InAppNotificationDao {

    @Query("SELECT * FROM in_app_notifications WHERE babyId = :babyId ORDER BY timestampEpoch DESC")
    fun getNotifications(babyId: Long): Flow<List<InAppNotificationEntity>>

    @Query("SELECT COUNT(*) FROM in_app_notifications WHERE babyId = :babyId AND isRead = 0")
    fun getUnreadCount(babyId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(notification: InAppNotificationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAll(notifications: List<InAppNotificationEntity>)

    @Query("UPDATE in_app_notifications SET isRead = 1 WHERE babyId = :babyId")
    suspend fun markAllAsRead(babyId: Long)

    @Query("UPDATE in_app_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("DELETE FROM in_app_notifications WHERE babyId = :babyId AND category = :category")
    suspend fun deleteByCategory(babyId: Long, category: String)

    @Query("DELETE FROM in_app_notifications WHERE timestampEpoch < :retentionThresholdEpoch")
    suspend fun deleteOlderThan(retentionThresholdEpoch: Long)
}
