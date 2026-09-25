package com.kangurusiaga.app.domain.model.education

data class EducationModule(
    val id: String,
    val order: Int,
    val title: String,
    val category: String,
    val readingTimeMinutes: Int,
    val themeColor: String,
    val heroDrawable: String,
    val heroTag: String,
    val sections: List<EducationSection>,
    val isBookmarked: Boolean = false,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val lastReadAt: Long? = null
)

sealed interface EducationSection {
    data class Overview(
        val title: String,
        val paragraphs: List<String>
    ) : EducationSection

    data class ClassificationList(
        val title: String,
        val standardBadge: String,
        val items: List<ClassificationItem>
    ) : EducationSection

    data class ClassificationItem(
        val title: String,
        val subtitle: String,
        val badgeText: String,
        val themeColor: String
    )

    data class IconList(
        val title: String,
        val subtitle: String,
        val items: List<IconListItem>
    ) : EducationSection

    data class IconListItem(
        val iconType: String,
        val title: String,
        val description: String,
        val themeColor: String
    )

    data class StepList(
        val title: String,
        val items: List<StepItem>
    ) : EducationSection

    data class StepItem(
        val number: Int,
        val title: String,
        val description: String
    )

    data class ProtocolBox(
        val title: String,
        val badgeText: String,
        val header: String,
        val items: List<ProtocolItem>
    ) : EducationSection

    data class ProtocolItem(
        val title: String,
        val description: String
    )

    data class WarningBox(
        val title: String,
        val items: List<WarningItem>
    ) : EducationSection

    data class WarningItem(
        val title: String,
        val description: String
    )

    data class CalloutBox(
        val title: String,
        val message: String,
        val themeColor: String
    ) : EducationSection

    data class ScreeningBox(
        val title: String,
        val items: List<ScreeningItem>
    ) : EducationSection

    data class ScreeningItem(
        val title: String,
        val description: String
    )
}
