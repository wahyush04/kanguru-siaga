package com.kangurusiaga.app.presentation.notification

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AddChart
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.remember
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.KanguruColors
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.domain.model.GrowthParameter
import com.kangurusiaga.app.domain.model.InAppNotification
import com.kangurusiaga.app.domain.model.NotificationBadgeColor
import com.kangurusiaga.app.domain.model.NotificationCategory
import com.kangurusiaga.app.presentation.home.HomeBottomBar
import com.kangurusiaga.app.presentation.home.HomeTab

@Composable
fun NotificationRoute(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToPmk: () -> Unit,
    onNavigateToEducation: () -> Unit,
    onNavigateToAlarm: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToPmkTimer: () -> Unit,
    onNavigateToFeeding: () -> Unit,
    onNavigateToGrowthAdd: (GrowthParameter) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NotificationScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNavigateToHome = onNavigateToHome,
        onNavigateToPmk = onNavigateToPmk,
        onNavigateToEducation = onNavigateToEducation,
        onNavigateToAlarm = onNavigateToAlarm,
        onNavigateToProfile = onNavigateToProfile,
        onMarkAllAsRead = viewModel::markAllAsRead,
        onNotificationClick = { notification ->
            viewModel.markAsRead(notification.id)
            when (notification.category) {
                NotificationCategory.PMK_PAUSE,
                NotificationCategory.PMK_REMINDER -> onNavigateToPmkTimer()
                NotificationCategory.FEEDING_ALARM -> onNavigateToFeeding()
                NotificationCategory.GROWTH_WEIGHT -> onNavigateToGrowthAdd(GrowthParameter.WEIGHT)
                NotificationCategory.GROWTH_LENGTH -> onNavigateToGrowthAdd(GrowthParameter.LENGTH)
                NotificationCategory.GROWTH_HEAD -> onNavigateToGrowthAdd(GrowthParameter.HEAD_CIRCUMFERENCE)
            }
        },
        onPrimaryAction = { notification ->
            viewModel.markAsRead(notification.id)
            when (notification.category) {
                NotificationCategory.PMK_PAUSE,
                NotificationCategory.PMK_REMINDER -> onNavigateToPmkTimer()
                NotificationCategory.FEEDING_ALARM -> {
                    // Confirmation marked as read
                }
                NotificationCategory.GROWTH_WEIGHT -> onNavigateToGrowthAdd(GrowthParameter.WEIGHT)
                NotificationCategory.GROWTH_LENGTH -> onNavigateToGrowthAdd(GrowthParameter.LENGTH)
                NotificationCategory.GROWTH_HEAD -> onNavigateToGrowthAdd(GrowthParameter.HEAD_CIRCUMFERENCE)
            }
        },
        onSecondaryAction = { notification ->
            viewModel.markAsRead(notification.id)
            if (notification.category == NotificationCategory.FEEDING_ALARM) {
                onNavigateToFeeding()
            }
        },
        modifier = modifier
    )
}

@Composable
fun NotificationScreen(
    uiState: NotificationUiState,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToPmk: () -> Unit,
    onNavigateToEducation: () -> Unit,
    onNavigateToAlarm: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onMarkAllAsRead: () -> Unit,
    onNotificationClick: (InAppNotification) -> Unit,
    onPrimaryAction: (InAppNotification) -> Unit,
    onSecondaryAction: (InAppNotification) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val todayDateStr = remember {
        try {
            java.text.SimpleDateFormat("EEEE, d MMM", java.util.Locale("id", "ID")).format(java.util.Date())
        } catch (_: Exception) {
            "Hari Ini"
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = {
            NotificationTopAppBar(
                babyName = uiState.babyName,
                unreadCount = uiState.unreadCount,
                onNavigateBack = onNavigateBack,
                onMarkAllAsRead = onMarkAllAsRead
            )
        },
        bottomBar = {
            HomeBottomBar(
                currentTab = HomeTab.BERANDA,
                onTabSelected = { tab ->
                    when (tab) {
                        HomeTab.BERANDA -> onNavigateToHome()
                        HomeTab.PMK -> onNavigateToPmk()
                        HomeTab.EDUKASI -> onNavigateToEducation()
                        HomeTab.ALARM -> onNavigateToAlarm()
                        HomeTab.PROFIL -> onNavigateToProfile()
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = colors.primary)
                    }
                }
                uiState.todayNotifications.isEmpty() && uiState.earlierNotifications.isEmpty() -> {
                    NotificationEmptyState(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Section: HARI INI
                        if (uiState.todayNotifications.isNotEmpty()) {
                            item(key = "section_today_header") {
                                SectionHeader(
                                    title = "HARI INI",
                                    subtitle = todayDateStr
                                )
                            }
                            items(
                                items = uiState.todayNotifications,
                                key = { it.id }
                            ) { notification ->
                                NotificationCardItem(
                                    notification = notification,
                                    onClick = { onNotificationClick(notification) },
                                    onPrimaryAction = { onPrimaryAction(notification) },
                                    onSecondaryAction = { onSecondaryAction(notification) }
                                )
                            }
                        }

                        // Section: SEBELUMNYA
                        if (uiState.earlierNotifications.isNotEmpty()) {
                            item(key = "section_earlier_header") {
                                Spacer(modifier = Modifier.height(4.dp))
                                SectionHeader(
                                    title = "SEBELUMNYA",
                                    subtitle = "22 - 23 Sep"
                                )
                            }
                            items(
                                items = uiState.earlierNotifications,
                                key = { it.id }
                            ) { notification ->
                                NotificationCardItem(
                                    notification = notification,
                                    onClick = { onNotificationClick(notification) },
                                    onPrimaryAction = { onPrimaryAction(notification) },
                                    onSecondaryAction = { onSecondaryAction(notification) }
                                )
                            }
                        }

                        // Footer note
                        item(key = "footer_retention_note") {
                            Spacer(modifier = Modifier.height(4.dp))
                            NotificationRetentionFooter(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationTopAppBar(
    babyName: String,
    unreadCount: Int,
    onNavigateBack: () -> Unit,
    onMarkAllAsRead: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    Surface(
        color = colors.background,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Back Button in circular container
            Surface(
                onClick = onNavigateBack,
                shape = CircleShape,
                color = colors.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.common_back),
                        tint = colors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Title and Subtitle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Notifikasi",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                val subtext = if (babyName.isNotBlank()) "Kanguru Siaga • $babyName" else "Kanguru Siaga"
                Text(
                    text = subtext,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = colors.textTertiary
                )
            }

            // Mark all as read button
            TextButton(
                onClick = onMarkAllAsRead,
                enabled = unreadCount > 0
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = null,
                        tint = if (unreadCount > 0) colors.primary else colors.textTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Dibaca",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = if (unreadCount > 0) colors.primary else colors.textTertiary
                        ),
                        fontWeight = FontWeight.Bold,
                        color = if (unreadCount > 0) colors.primary else colors.textTertiary
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = colors.textTertiary
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = colors.textTertiary
        )
    }
}

@Composable
private fun NotificationCardItem(
    notification: InAppNotification,
    onClick: () -> Unit,
    onPrimaryAction: () -> Unit,
    onSecondaryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val hasTargetInfo = !notification.targetInfo.isNullOrBlank()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top Row: Category Icon Box + Content Column
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Category Icon
                CategoryIconBox(
                    category = notification.category,
                    colors = colors
                )

                // Title, Badge, and Message
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Header Row inside content column
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NotificationBadge(
                            badgeLabel = notification.badgeLabel,
                            badgeColor = notification.badgeColor,
                            isPulse = notification.category == NotificationCategory.PMK_PAUSE
                        )

                        if (hasTargetInfo) {
                            // Cards 4, 5, 6: Timestamp in top-right
                            Text(
                                text = notification.timeLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Normal,
                                color = colors.textTertiary
                            )
                        } else if (!notification.isRead) {
                            // Cards 1, 2, 3: Unread red dot in top-right
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(colors.primary)
                            )
                        }
                    }

                    // Title
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )

                    // Message
                    Text(
                        text = notification.message,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = colors.textSecondary
                    )
                }
            }

            // Bottom Row: Time / Target info on left + Action Buttons on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left side
                if (!hasTargetInfo) {
                    // Clock icon + time label
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = colors.textTertiary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = notification.timeLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textTertiary
                        )
                    }
                } else {
                    // Target metric info with corresponding icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        when (notification.category) {
                            NotificationCategory.GROWTH_WEIGHT -> {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            NotificationCategory.GROWTH_LENGTH -> {
                                Icon(
                                    painter = painterResource(R.drawable.ic_infantometer_mat),
                                    contentDescription = null,
                                    tint = colors.success,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            NotificationCategory.GROWTH_HEAD -> {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = colors.info,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            else -> {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    tint = colors.textSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Text(
                            text = notification.targetInfo.orEmpty(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = colors.textSecondary
                        )
                    }
                }

                // Right side: Action Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Secondary action button (e.g. "Buka Detail" for ASI)
                    notification.secondaryActionLabel?.let { secLabel ->
                        Button(
                            onClick = onSecondaryAction,
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.infoContainer,
                                contentColor = colors.info
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Text(
                                text = secLabel,
                                style = MaterialTheme.typography.labelMedium.copy(color = colors.info),
                                fontWeight = FontWeight.Bold,
                                color = colors.info
                            )
                        }
                    }

                    // Primary action button
                    val (btnContainerColor, btnContentColor) = getActionButtonColors(notification.category, colors)
                    val actionIcon = getActionIcon(notification.category)

                    Button(
                        onClick = onPrimaryAction,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = btnContainerColor,
                            contentColor = btnContentColor
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        if (actionIcon != null) {
                            Icon(
                                imageVector = actionIcon,
                                contentDescription = null,
                                tint = btnContentColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = notification.primaryActionLabel,
                            style = MaterialTheme.typography.labelMedium.copy(color = btnContentColor),
                            fontWeight = FontWeight.Bold,
                            color = btnContentColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryIconBox(
    category: NotificationCategory,
    colors: KanguruColors,
    modifier: Modifier = Modifier
) {
    val (containerColor, contentColor) = when (category) {
        NotificationCategory.PMK_PAUSE -> colors.warningContainer to colors.warning
        NotificationCategory.PMK_REMINDER -> colors.primaryContainer to colors.primary
        NotificationCategory.FEEDING_ALARM -> colors.infoContainer to colors.info
        NotificationCategory.GROWTH_WEIGHT -> colors.primaryContainer to colors.primary
        NotificationCategory.GROWTH_LENGTH -> colors.successContainer to colors.success
        NotificationCategory.GROWTH_HEAD -> colors.infoContainer to colors.info
    }

    Box(
        modifier = modifier
            .size(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        when (category) {
            NotificationCategory.PMK_PAUSE -> {
                Icon(
                    imageVector = Icons.Default.TimerOff,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            NotificationCategory.PMK_REMINDER -> {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            NotificationCategory.FEEDING_ALARM -> {
                Icon(
                    painter = painterResource(R.drawable.ic_baby_bottle),
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            NotificationCategory.GROWTH_WEIGHT -> {
                Icon(
                    painter = painterResource(R.drawable.ic_fenton_scale),
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            NotificationCategory.GROWTH_LENGTH -> {
                Icon(
                    imageVector = Icons.Default.Straighten,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            NotificationCategory.GROWTH_HEAD -> {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private fun getActionButtonColors(
    category: NotificationCategory,
    colors: KanguruColors
): Pair<Color, Color> {
    return when (category) {
        NotificationCategory.PMK_PAUSE -> colors.warning to colors.onPrimary
        NotificationCategory.PMK_REMINDER -> colors.primary to colors.onPrimary
        NotificationCategory.FEEDING_ALARM -> colors.info to colors.onPrimary
        NotificationCategory.GROWTH_WEIGHT -> colors.primary to colors.onPrimary
        NotificationCategory.GROWTH_LENGTH -> colors.successContainer to colors.success
        NotificationCategory.GROWTH_HEAD -> colors.infoContainer to colors.info
    }
}

private fun getActionIcon(category: NotificationCategory): ImageVector? {
    return when (category) {
        NotificationCategory.PMK_PAUSE -> Icons.Default.PlayArrow
        NotificationCategory.PMK_REMINDER -> Icons.Default.Timer
        NotificationCategory.FEEDING_ALARM -> null
        NotificationCategory.GROWTH_WEIGHT -> Icons.Default.AddChart
        NotificationCategory.GROWTH_LENGTH,
        NotificationCategory.GROWTH_HEAD -> Icons.Default.Edit
    }
}

@Composable
private fun NotificationBadge(
    badgeLabel: String,
    badgeColor: NotificationBadgeColor,
    isPulse: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val (bgColor, textColor) = when (badgeColor) {
        NotificationBadgeColor.AMBER -> colors.warningContainer to colors.warning
        NotificationBadgeColor.PINK -> colors.primaryContainer to colors.primary
        NotificationBadgeColor.BLUE -> colors.infoContainer to colors.info
        NotificationBadgeColor.GREEN -> colors.successContainer to colors.success
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (isPulse) {
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "alpha"
            )
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .alpha(alpha)
                    .clip(CircleShape)
                    .background(textColor)
            )
        }
        Text(
            text = badgeLabel,
            style = MaterialTheme.typography.labelSmall.copy(color = textColor),
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun NotificationEmptyState(
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(colors.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsOff,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Tidak Ada Notifikasi",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Semua pengingat telah selesai atau belum memiliki jadwal aktif.",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = colors.textTertiary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun NotificationRetentionFooter(
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(50),
            color = colors.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = colors.textTertiary,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "Notifikasi tersimpan otomatis selama 30 hari",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = colors.textTertiary
                )
            }
        }
    }
}
