package com.kangurusiaga.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kangurusiaga.app.domain.model.InAppNotification
import com.kangurusiaga.app.domain.model.NotificationBadgeColor
import com.kangurusiaga.app.domain.model.NotificationCategory

@Entity(
    tableName = "in_app_notifications",
    foreignKeys = [
        ForeignKey(
            entity = BabyEntity::class,
            parentColumns = ["id"],
            childColumns = ["babyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("babyId"),
        Index("category"),
        Index("timestampEpoch")
    ]
)
data class InAppNotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val babyId: Long,
    val category: String, // NotificationCategory name
    val referenceId: Long? = null,
    val title: String,
    val message: String,
    val badgeLabel: String,
    val badgeColor: String = "PINK", // NotificationBadgeColor name
    val timeLabel: String = "",
    val targetInfo: String? = null,
    val isRead: Boolean = false,
    val isToday: Boolean = true,
    val primaryActionLabel: String,
    val secondaryActionLabel: String? = null,
    val timestampEpoch: Long = System.currentTimeMillis()
) {
    fun toDomain(): InAppNotification {
        val cat = try {
            NotificationCategory.valueOf(category)
        } catch (_: Exception) {
            NotificationCategory.PMK_REMINDER
        }
        val color = try {
            NotificationBadgeColor.valueOf(badgeColor)
        } catch (_: Exception) {
            NotificationBadgeColor.PINK
        }
        return InAppNotification(
            id = id,
            babyId = babyId,
            category = cat,
            referenceId = referenceId,
            title = title,
            message = message,
            badgeLabel = badgeLabel,
            badgeColor = color,
            timeLabel = timeLabel,
            targetInfo = targetInfo,
            isRead = isRead,
            isToday = isToday,
            primaryActionLabel = primaryActionLabel,
            secondaryActionLabel = secondaryActionLabel,
            timestampEpoch = timestampEpoch
        )
    }

    companion object {
        fun fromDomain(domain: InAppNotification): InAppNotificationEntity {
            return InAppNotificationEntity(
                id = domain.id,
                babyId = domain.babyId,
                category = domain.category.name,
                referenceId = domain.referenceId,
                title = domain.title,
                message = domain.message,
                badgeLabel = domain.badgeLabel,
                badgeColor = domain.badgeColor.name,
                timeLabel = domain.timeLabel,
                targetInfo = domain.targetInfo,
                isRead = domain.isRead,
                isToday = domain.isToday,
                primaryActionLabel = domain.primaryActionLabel,
                secondaryActionLabel = domain.secondaryActionLabel,
                timestampEpoch = domain.timestampEpoch
            )
        }
    }
}
