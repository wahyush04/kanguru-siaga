package com.kangurusiaga.app.presentation.home

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.BrandBackground
import com.kangurusiaga.app.core.designsystem.theme.BrandCardBorder
import com.kangurusiaga.app.core.designsystem.theme.BrandLightPink
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.core.designsystem.theme.BrandSoftAmber
import com.kangurusiaga.app.core.designsystem.theme.BrandSoftBlue
import com.kangurusiaga.app.core.designsystem.theme.BrandSoftGreen
import com.kangurusiaga.app.core.designsystem.theme.BrandTextAmber
import com.kangurusiaga.app.core.designsystem.theme.BrandTextGreen
import com.kangurusiaga.app.core.designsystem.theme.TextPrimary
import com.kangurusiaga.app.core.designsystem.theme.TextSecondary
import com.kangurusiaga.app.core.designsystem.theme.White
import com.kangurusiaga.app.domain.model.Baby
import kotlinx.coroutines.launch

@Composable
fun HomeRoute(
    onNavigateToPmk: () -> Unit,
    onNavigateToProfileSetup: () -> Unit = {},
    onNavigateToEducation: () -> Unit = {},
    onNavigateToEmergency: () -> Unit = {},
    onNavigateToAlarm: () -> Unit = {},
    onNavigateToGrowth: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onNavigateToPmk = onNavigateToPmk,
        onNavigateToProfileSetup = onNavigateToProfileSetup,
        onNavigateToEducation = onNavigateToEducation,
        onNavigateToEmergency = onNavigateToEmergency,
        onNavigateToAlarm = onNavigateToAlarm,
        onNavigateToGrowth = onNavigateToGrowth,
        onNavigateToProfile = onNavigateToProfile,
        onOpenEmergency = viewModel::openEmergencyDialog,
        onCloseEmergency = viewModel::closeEmergencyDialog,
        onOpenNotification = viewModel::openNotificationSheet,
        onCloseNotification = viewModel::closeNotificationSheet,
        onShowInfo = viewModel::showInfoMessage,
        onClearInfo = viewModel::clearInfoMessage,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onNavigateToPmk: () -> Unit,
    onNavigateToProfileSetup: () -> Unit = {},
    onNavigateToEducation: () -> Unit = {},
    onNavigateToEmergency: () -> Unit = {},
    onNavigateToAlarm: () -> Unit = {},
    onNavigateToGrowth: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onOpenEmergency: () -> Unit = {},
    onCloseEmergency: () -> Unit = {},
    onOpenNotification: () -> Unit = {},
    onCloseNotification: () -> Unit = {},
    onShowInfo: (String) -> Unit = {},
    onClearInfo: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.infoMessage) {
        uiState.infoMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            onClearInfo()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            HomeBottomBar(
                currentTab = HomeTab.BERANDA,
                onTabSelected = { tab ->
                    when (tab) {
                        HomeTab.BERANDA -> { /* already here */ }
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
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BrandPink)
                    }
                }
                uiState.activeBaby != null -> {
                    HomeContent(
                        baby = uiState.activeBaby,
                        ageFormatted = uiState.babyAgeFormatted,
                        weightFormatted = uiState.babyWeightFormatted,
                        todaySessionsCount = uiState.todaySessionsCount,
                        todayTargetSessions = uiState.todayTargetSessions,
                        todayProgress = uiState.todayProgressFraction,
                        unreadNotificationsCount = uiState.unreadNotificationsCount,
                        onNavigateToPmk = onNavigateToPmk,
                        onNavigateToEducation = onNavigateToEducation,
                        onNavigateToEmergency = onNavigateToEmergency,
                        onNavigateToAlarm = onNavigateToAlarm,
                        onNavigateToGrowth = onNavigateToGrowth,
                        onOpenEmergency = onOpenEmergency,
                        onOpenNotification = onOpenNotification,
                        onShowInfo = onShowInfo,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                }
                else -> {
                    EmptyHomeContent(
                        onNavigateToProfileSetup = onNavigateToProfileSetup,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    )
                }
            }
        }
    }

    // Emergency Modal Bottom Sheet
    if (uiState.showEmergencyDialog) {
        EmergencyBottomSheet(
            onDismiss = onCloseEmergency,
            onCallEmergency = {
                val callIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:119")
                }
                context.startActivity(callIntent)
            }
        )
    }

    // Notification Dialog Sheet
    if (uiState.showNotificationSheet) {
        NotificationBottomSheet(
            onDismiss = onCloseNotification,
            onNavigateToPmk = {
                onCloseNotification()
                onNavigateToPmk()
            }
        )
    }
}

@Composable
private fun HomeContent(
    baby: Baby,
    ageFormatted: String,
    weightFormatted: String,
    todaySessionsCount: Int,
    todayTargetSessions: Int,
    todayProgress: Float,
    unreadNotificationsCount: Int,
    onNavigateToPmk: () -> Unit,
    onNavigateToEducation: () -> Unit = {},
    onNavigateToEmergency: () -> Unit = {},
    onNavigateToAlarm: () -> Unit,
    onNavigateToGrowth: () -> Unit = {},
    onOpenEmergency: () -> Unit,
    onOpenNotification: () -> Unit,
    onShowInfo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // 1. Header Profile Bayi
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Circular Photo / Avatar
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(BrandLightPink)
                        .border(1.5.dp, BrandCardBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (baby.photoUri != null) {
                        AsyncImage(
                            model = Uri.parse(baby.photoUri),
                            contentDescription = baby.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_kangaroo_mascot),
                            contentDescription = null,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = stringResource(R.string.home_hello),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = baby.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = (-0.3).sp
                    )
                    Text(
                        text = "$ageFormatted • $weightFormatted",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            // Notification button with badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable { onOpenNotification() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = stringResource(R.string.home_cd_notifications),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )

                if (unreadNotificationsCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 4.dp, end = 4.dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(BrandPink)
                            .border(2.dp, BrandBackground, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = unreadNotificationsCount.toString(),
                            color = White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 2. 4 Main Action Cards Grid (2x2)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Card 1: PMK
            ActionGridCard(
                title = stringResource(R.string.home_card_pmk_title),
                subtitle = stringResource(R.string.home_card_pmk_subtitle),
                iconRes = R.drawable.ic_pmk_mascot,
                containerColor = BrandLightPink,
                borderColor = KanguruTheme.colors.outline,
                onClick = onNavigateToPmk,
                modifier = Modifier.weight(1f)
            )

            // Card 2: Bayi BBLR
            ActionGridCard(
                title = stringResource(R.string.home_card_bblr_title),
                subtitle = stringResource(R.string.home_card_bblr_subtitle),
                iconRes = R.drawable.ic_card_bblr,
                containerColor = KanguruTheme.colors.successContainer,
                borderColor = KanguruTheme.colors.successBorder,
                onClick = onNavigateToEducation,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Card 3: Tanda Kegawatan
            ActionGridCard(
                title = stringResource(R.string.home_card_emergency_title),
                subtitle = stringResource(R.string.home_card_emergency_subtitle),
                iconRes = R.drawable.ic_card_emergency,
                containerColor = KanguruTheme.colors.warningContainer,
                borderColor = KanguruTheme.colors.warningBorder,
                onClick = onNavigateToEmergency,
                modifier = Modifier.weight(1f)
            )

            // Card 4: Alarm ASI
            ActionGridCard(
                title = stringResource(R.string.home_card_asi_title),
                subtitle = stringResource(R.string.home_card_asi_subtitle),
                iconRes = R.drawable.ic_card_asi,
                containerColor = KanguruTheme.colors.infoContainer,
                borderColor = KanguruTheme.colors.infoBorder,
                onClick = onNavigateToAlarm,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Pertumbuhan Bayi Card Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, KanguruTheme.colors.outline, RoundedCornerShape(24.dp))
                .clickable { onNavigateToGrowth() },
            colors = CardDefaults.cardColors(containerColor = BrandLightPink),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_card_growth),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.home_card_growth_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = stringResource(R.string.home_card_growth_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = BrandPink,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Aktivitas Hari Ini Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.home_section_today_activity),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = stringResource(R.string.home_see_all),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = BrandPink,
                modifier = Modifier.clickable { onNavigateToPmk() }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // PMK Activity Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, KanguruTheme.colors.cardBorder, RoundedCornerShape(20.dp))
                .clickable { onNavigateToPmk() },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Amber Icon Badge
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(KanguruTheme.colors.warningContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_kangaroo_mascot),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.home_pmk_activity_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = stringResource(R.string.home_pmk_activity_progress, todaySessionsCount, todayTargetSessions),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { todayProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = KanguruTheme.colors.success,
                        trackColor = MaterialTheme.colorScheme.outlineVariant,
                        strokeCap = StrokeCap.Round
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = BrandPink,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun ActionGridCard(
    title: String,
    subtitle: String,
    iconRes: Int,
    containerColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(164.dp)
            .border(1.dp, borderColor, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(46.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                fontSize = 10.5.sp,
                maxLines = 1
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmergencyBottomSheet(
    onDismiss: () -> Unit,
    onCallEmergency: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.home_emergency_sheet_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = stringResource(R.string.home_emergency_sheet_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.home_cd_close),
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Alert Box
            Surface(
                color = KanguruTheme.colors.errorContainer,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, KanguruTheme.colors.errorBorder)
            ) {
                Text(
                    text = stringResource(R.string.home_emergency_sheet_alert),
                    style = MaterialTheme.typography.bodySmall,
                    color = KanguruTheme.colors.onErrorContainer,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Danger Items List
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp))
            ) {
                DangerItemRow(
                    icon = "🚨",
                    title = stringResource(R.string.home_emergency_item_1_title),
                    subtitle = stringResource(R.string.home_emergency_item_1_sub)
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                DangerItemRow(
                    icon = "🚨",
                    title = stringResource(R.string.home_emergency_item_2_title),
                    subtitle = stringResource(R.string.home_emergency_item_2_sub)
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                DangerItemRow(
                    icon = "🚨",
                    title = stringResource(R.string.home_emergency_item_3_title),
                    subtitle = stringResource(R.string.home_emergency_item_3_sub)
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                DangerItemRow(
                    icon = "🚨",
                    title = stringResource(R.string.home_emergency_item_4_title),
                    subtitle = stringResource(R.string.home_emergency_item_4_sub)
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                DangerItemRow(
                    icon = "🚨",
                    title = stringResource(R.string.home_emergency_item_5_title),
                    subtitle = stringResource(R.string.home_emergency_item_5_sub)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Call ambulance button
            Button(
                onClick = onCallEmergency,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.home_btn_call_ambulance),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DangerItemRow(
    icon: String,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 12.sp
            )
        }
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            fontSize = 11.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationBottomSheet(
    onDismiss: () -> Unit,
    onNavigateToPmk: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.home_notif_sheet_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = stringResource(R.string.home_cd_close), tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToPmk() }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(BrandLightPink),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🦘", fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.home_notif_pmk_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = stringResource(R.string.home_notif_pmk_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

enum class HomeTab(val label: String) {
    BERANDA("Beranda"),
    PMK("PMK"),
    EDUKASI("Edukasi"),
    ALARM("Alarm"),
    PROFIL("Profil")
}

@Composable
fun HomeBottomBar(
    currentTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HomeTab.entries.forEach { tab ->
                    val isSelected = tab == currentTab
                    val contentColor = if (isSelected) BrandPink else MaterialTheme.colorScheme.onSurfaceVariant
                    val icon: ImageVector = when (tab) {
                        HomeTab.BERANDA -> Icons.Default.Home
                        HomeTab.PMK -> Icons.Default.VolunteerActivism
                        HomeTab.EDUKASI -> Icons.AutoMirrored.Filled.MenuBook
                        HomeTab.ALARM -> Icons.Default.Notifications
                        HomeTab.PROFIL -> Icons.Default.Person
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onTabSelected(tab) }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab.label,
                            tint = contentColor,
                            modifier = Modifier.size(22.dp)
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = tab.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = contentColor,
                            fontSize = 10.5.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyHomeContent(
    onNavigateToProfileSetup: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_kangaroo_mascot),
            contentDescription = null,
            modifier = Modifier.size(96.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.home_empty_title),
            style = MaterialTheme.typography.headlineSmall,
            color = BrandPink,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.home_empty_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onNavigateToProfileSetup,
            colors = ButtonDefaults.buttonColors(containerColor = BrandPink),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(50.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.home_btn_setup_profile),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}
