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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.Brush
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
import com.kangurusiaga.app.presentation.growth.components.FentonInternetConfirmationDialog
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch

@Composable
fun HomeRoute(
    onNavigateToPmk: () -> Unit,
    onNavigateToProfileSetup: () -> Unit = {},
    onNavigateToEducation: () -> Unit = {},
    onNavigateToEducationList: () -> Unit = {},
    onNavigateToEmergency: () -> Unit = {},
    onNavigateToAlarm: () -> Unit = {},
    onNavigateToGrowth: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToTimer: () -> Unit = onNavigateToPmk,
    onNavigateToNotification: () -> Unit = {},
    onNavigateToPmkVideo: () -> Unit = onNavigateToEducation,
    onNavigateToBblrCare: () -> Unit = onNavigateToEducationList,
    onNavigateToBblrEmergency: () -> Unit = onNavigateToEmergency,
    onNavigateToBathingVideo: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onNavigateToPmk = onNavigateToPmk,
        onNavigateToProfileSetup = onNavigateToProfileSetup,
        onNavigateToEducation = onNavigateToEducation,
        onNavigateToEducationList = onNavigateToEducationList,
        onNavigateToEmergency = onNavigateToEmergency,
        onNavigateToAlarm = onNavigateToAlarm,
        onNavigateToGrowth = onNavigateToGrowth,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToTimer = onNavigateToTimer,
        onNavigateToNotification = onNavigateToNotification,
        onNavigateToPmkVideo = onNavigateToPmkVideo,
        onNavigateToBblrCare = onNavigateToBblrCare,
        onNavigateToBblrEmergency = onNavigateToBblrEmergency,
        onNavigateToBathingVideo = onNavigateToBathingVideo,
        onOpenEmergency = viewModel::openEmergencyDialog,
        onCloseEmergency = viewModel::closeEmergencyDialog,
        onOpenNotification = onNavigateToNotification,
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
    onNavigateToEducationList: () -> Unit = {},
    onNavigateToEmergency: () -> Unit = {},
    onNavigateToAlarm: () -> Unit = {},
    onNavigateToGrowth: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToTimer: () -> Unit = onNavigateToPmk,
    onNavigateToNotification: () -> Unit = {},
    onNavigateToPmkVideo: () -> Unit = {},
    onNavigateToBblrCare: () -> Unit = {},
    onNavigateToBblrEmergency: () -> Unit = {},
    onNavigateToBathingVideo: () -> Unit = {},
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
    var showFentonConfirmationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.infoMessage) {
        uiState.infoMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            onClearInfo()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = KanguruTheme.colors.background,
        snackbarHost = { com.kangurusiaga.app.core.designsystem.component.KanguruSnackbarHost(snackbarHostState) },
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
                        pmkProgressFormatted = uiState.formattedTodayPmkProgress,
                        unreadNotificationsCount = uiState.unreadNotificationsCount,
                        onNavigateToPmk = onNavigateToPmk,
                        onNavigateToEducation = onNavigateToEducation,
                        onNavigateToEducationList = onNavigateToEducationList,
                        onNavigateToEmergency = onNavigateToEmergency,
                        onNavigateToAlarm = onNavigateToAlarm,
                        onNavigateToGrowth = { showFentonConfirmationDialog = true },
                        onNavigateToTimer = onNavigateToTimer,
                        onNavigateToPmkVideo = onNavigateToPmkVideo,
                        onNavigateToBblrCare = onNavigateToBblrCare,
                        onNavigateToBblrEmergency = onNavigateToBblrEmergency,
                        onNavigateToBathingVideo = onNavigateToBathingVideo,
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

    // Fenton Cloud Integration & Appreciation Confirmation Pop-Up
    if (showFentonConfirmationDialog) {
        FentonInternetConfirmationDialog(
            onConfirm = {
                showFentonConfirmationDialog = false
                onNavigateToGrowth()
            },
            onDismiss = {
                showFentonConfirmationDialog = false
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
    pmkProgressFormatted: String,
    unreadNotificationsCount: Int,
    onNavigateToPmk: () -> Unit,
    onNavigateToEducation: () -> Unit = {},
    onNavigateToEducationList: () -> Unit = {},
    onNavigateToEmergency: () -> Unit = {},
    onNavigateToAlarm: () -> Unit,
    onNavigateToGrowth: () -> Unit = {},
    onNavigateToTimer: () -> Unit = onNavigateToPmk,
    onNavigateToPmkVideo: () -> Unit = {},
    onNavigateToBblrCare: () -> Unit = {},
    onNavigateToBblrEmergency: () -> Unit = {},
    onNavigateToBathingVideo: () -> Unit = {},
    onOpenEmergency: () -> Unit,
    onOpenNotification: () -> Unit,
    onShowInfo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // 1. Header Sapaan & Notifikasi
        HomeHeaderSection(
            babyName = baby.name,
            ageFormatted = ageFormatted,
            weightFormatted = weightFormatted,
            onOpenNotification = onOpenNotification
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Hero Banner Status Bayi ("Bayi Hebat")
        HeroBabyStatusCard(
            baby = baby,
            onClick = onNavigateToTimer
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Main Feature Menu Grid (2 Baris x 3 Kolom)
        HomeFeatureMenuGrid(
            onNavigateToPmkVideo = onNavigateToPmkVideo,
            onNavigateToBblrCare = onNavigateToBblrCare,
            onOpenEmergency = onNavigateToBblrEmergency,
            onNavigateToPmkTimer = onNavigateToPmk,
            onNavigateToBathingVideo = onNavigateToBathingVideo,
            onNavigateToAsiAlarm = onNavigateToAlarm
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 4. Kartu Horizontal Pertumbuhan Bayi (Grafik Fenton)
        HomeGrowthBannerCard(
            onClick = onNavigateToGrowth
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 5. Widget Target PMK Hari Ini
        HomeDailyActivityCard(
            progressFraction = todayProgress,
            durationFormatted = pmkProgressFormatted,
            onClick = onNavigateToTimer
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun HomeHeaderSection(
    babyName: String,
    ageFormatted: String,
    weightFormatted: String,
    onOpenNotification: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.home_hello, babyName),
                style = KanguruTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = KanguruTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.home_welcome_app),
                style = KanguruTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = KanguruTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$ageFormatted – $weightFormatted",
                style = KanguruTheme.typography.bodySmall,
                fontWeight = FontWeight.Normal,
                color = KanguruTheme.colors.textSecondary
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Notification Bell Button with Red Alert Badge (unclipped, top-most)
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(44.dp),
            contentAlignment = Alignment.Center
        ) {
            // Circle Bell Button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(KanguruTheme.colors.surface)
                    .border(1.dp, KanguruTheme.colors.cardBorder, CircleShape)
                    .clickable(onClick = onOpenNotification),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = stringResource(R.string.home_cd_notifications),
                    tint = KanguruTheme.colors.gridSubtitleRose,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Red dot indicator - rendered on top, unclipped
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(11.dp)
                    .clip(CircleShape)
                    .background(KanguruTheme.colors.gridSubtitleRose)
                    .border(2.dp, KanguruTheme.colors.surface, CircleShape)
            )
        }
    }
}

@Composable
private fun HeroBabyStatusCard(
    baby: Baby,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, KanguruTheme.colors.heroBannerBorder, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            KanguruTheme.colors.heroBannerBgStart,
                            KanguruTheme.colors.heroBannerBgMid,
                            KanguruTheme.colors.heroBannerBgEnd
                        )
                    )
                )
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Baby Image Container Squircle
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(KanguruTheme.colors.surface.copy(alpha = 0.9f))
                        .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (baby.photoUri != null) {
                        AsyncImage(
                            model = Uri.parse(baby.photoUri),
                            contentDescription = baby.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(18.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_kangaroo_mascot),
                            contentDescription = baby.name,
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                ) {
                    // Badge: ● PMK Aktif
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(KanguruTheme.colors.heroBadgeBg)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(KanguruTheme.colors.gridSubtitleTeal)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = stringResource(R.string.home_hero_pmk_active),
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = KanguruTheme.colors.heroBadgeText
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.home_hero_baby_title),
                        style = KanguruTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = KanguruTheme.colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = stringResource(R.string.home_hero_baby_subtitle),
                        style = KanguruTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = KanguruTheme.colors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeFeatureMenuGrid(
    onNavigateToPmkVideo: () -> Unit,
    onNavigateToBblrCare: () -> Unit,
    onOpenEmergency: () -> Unit,
    onNavigateToPmkTimer: () -> Unit,
    onNavigateToBathingVideo: () -> Unit,
    onNavigateToAsiAlarm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HomeFeatureGridCard(
                title = stringResource(R.string.home_grid_pmk_video_title),
                subtitle = stringResource(R.string.home_grid_pmk_video_sub),
                subtitleColor = KanguruTheme.colors.gridSubtitleRose,
                outerGradient = listOf(
                    KanguruTheme.colors.gridIconRoseOuterStart,
                    KanguruTheme.colors.gridIconRoseOuterEnd
                ),
                innerGradient = listOf(
                    KanguruTheme.colors.gridIconRoseStart,
                    KanguruTheme.colors.gridIconRoseEnd
                ),
                icon = Icons.Default.PlayArrow,
                onClick = onNavigateToPmkVideo,
                modifier = Modifier.weight(1f)
            )

            HomeFeatureGridCard(
                title = stringResource(R.string.home_grid_bblr_care_title),
                subtitle = stringResource(R.string.home_grid_bblr_care_sub),
                subtitleColor = KanguruTheme.colors.gridSubtitleTeal,
                outerGradient = listOf(
                    KanguruTheme.colors.gridIconTealOuterStart,
                    KanguruTheme.colors.gridIconTealOuterEnd
                ),
                innerGradient = listOf(
                    KanguruTheme.colors.gridIconTealStart,
                    KanguruTheme.colors.gridIconTealEnd
                ),
                icon = Icons.AutoMirrored.Filled.MenuBook,
                onClick = onNavigateToBblrCare,
                modifier = Modifier.weight(1f)
            )

            HomeFeatureGridCard(
                title = stringResource(R.string.home_grid_emergency_title),
                subtitle = stringResource(R.string.home_grid_emergency_sub),
                subtitleColor = KanguruTheme.colors.gridSubtitleAmber,
                outerGradient = listOf(
                    KanguruTheme.colors.gridIconAmberOuterStart,
                    KanguruTheme.colors.gridIconAmberOuterEnd
                ),
                innerGradient = listOf(
                    KanguruTheme.colors.gridIconAmberStart,
                    KanguruTheme.colors.gridIconAmberEnd
                ),
                icon = Icons.Default.Warning,
                onClick = onOpenEmergency,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HomeFeatureGridCard(
                title = stringResource(R.string.home_grid_pmk_timer_title),
                subtitle = stringResource(R.string.home_grid_pmk_timer_sub),
                subtitleColor = KanguruTheme.colors.gridSubtitlePink,
                outerGradient = listOf(
                    KanguruTheme.colors.gridIconPinkOuterStart,
                    KanguruTheme.colors.gridIconPinkOuterEnd
                ),
                innerGradient = listOf(
                    KanguruTheme.colors.gridIconPinkStart,
                    KanguruTheme.colors.gridIconPinkEnd
                ),
                icon = Icons.Default.AccessTime,
                onClick = onNavigateToPmkTimer,
                modifier = Modifier.weight(1f)
            )

            HomeFeatureGridCard(
                title = stringResource(R.string.home_grid_bathing_title),
                subtitle = stringResource(R.string.home_grid_bathing_sub),
                subtitleColor = KanguruTheme.colors.gridSubtitleBlue,
                outerGradient = listOf(
                    KanguruTheme.colors.gridIconBlueOuterStart,
                    KanguruTheme.colors.gridIconBlueOuterEnd
                ),
                innerGradient = listOf(
                    KanguruTheme.colors.gridIconBlueStart,
                    KanguruTheme.colors.gridIconBlueEnd
                ),
                icon = Icons.Default.WaterDrop,
                onClick = onNavigateToBathingVideo,
                modifier = Modifier.weight(1f)
            )

            HomeFeatureGridCard(
                title = stringResource(R.string.home_grid_asi_alarm_title),
                subtitle = stringResource(R.string.home_grid_asi_alarm_sub),
                subtitleColor = KanguruTheme.colors.gridSubtitlePurple,
                outerGradient = listOf(
                    KanguruTheme.colors.gridIconPurpleOuterStart,
                    KanguruTheme.colors.gridIconPurpleOuterEnd
                ),
                innerGradient = listOf(
                    KanguruTheme.colors.gridIconPurpleStart,
                    KanguruTheme.colors.gridIconPurpleEnd
                ),
                icon = Icons.Default.Alarm,
                onClick = onNavigateToAsiAlarm,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun HomeFeatureGridCard(
    title: String,
    subtitle: String,
    subtitleColor: Color,
    outerGradient: List<Color>,
    innerGradient: List<Color>,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(128.dp)
            .border(1.dp, KanguruTheme.colors.cardBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = KanguruTheme.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Nested Gradient Icon Container
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(outerGradient)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Brush.linearGradient(innerGradient)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Text Labels - vertically centered in remaining card space to eliminate white space gap
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 4.dp)
            ) {
                Text(
                    text = title,
                    style = KanguruTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = KanguruTheme.colors.textPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 3
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = KanguruTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = subtitleColor,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun HomeGrowthBannerCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, KanguruTheme.colors.cardBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = KanguruTheme.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // BarChart Icon Container
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                KanguruTheme.colors.gridIconTealOuterStart,
                                KanguruTheme.colors.gridIconTealOuterEnd
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    KanguruTheme.colors.gridIconTealStart,
                                    KanguruTheme.colors.gridIconTealEnd
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.home_growth_card_title),
                    style = KanguruTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = KanguruTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.home_growth_card_sub),
                    style = KanguruTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = KanguruTheme.colors.gridSubtitleTeal
                )
            }

            // Circular chevron right button
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(KanguruTheme.colors.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = KanguruTheme.colors.textSecondary,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
private fun HomeDailyActivityCard(
    progressFraction: Float,
    durationFormatted: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, KanguruTheme.colors.cardBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = KanguruTheme.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(KanguruTheme.colors.gridSubtitleRose)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.home_target_pmk_title),
                        style = KanguruTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = KanguruTheme.colors.textPrimary
                    )
                }

                Text(
                    text = stringResource(R.string.home_target_pmk_min_label),
                    style = KanguruTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = KanguruTheme.colors.gridSubtitleRose
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Custom Gradient Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape)
                    .background(KanguruTheme.colors.outlineVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = progressFraction.coerceIn(0.04f, 1f))
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    KanguruTheme.colors.gridIconRoseStart,
                                    KanguruTheme.colors.gridIconPinkStart
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(
                color = KanguruTheme.colors.divider.copy(alpha = 0.6f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Footer Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Durasi: ",
                        style = KanguruTheme.typography.bodySmall,
                        color = KanguruTheme.colors.textSecondary
                    )
                    Text(
                        text = durationFormatted,
                        style = KanguruTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = KanguruTheme.colors.textPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = KanguruTheme.colors.gridSubtitleTeal,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.home_target_pmk_target_met),
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = KanguruTheme.colors.gridSubtitleTeal
                    )
                }
            }
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
                        color = TextSecondary
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
                    .heightIn(min = 52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KanguruTheme.colors.emergencyAction),
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
                    style = KanguruTheme.typography.titleSmall,
                    color = White
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
            Text(text = icon, style = KanguruTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
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
                        Text(text = "🦘", style = KanguruTheme.typography.titleLarge)
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
                            color = TextSecondary
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
                    val contentColor = if (isSelected) KanguruTheme.colors.gridSubtitleRose else KanguruTheme.colors.textSecondary
                    val icon: ImageVector = when (tab) {
                        HomeTab.BERANDA -> Icons.Default.Home
                        HomeTab.PMK -> Icons.Default.FavoriteBorder
                        HomeTab.EDUKASI -> Icons.AutoMirrored.Filled.MenuBook
                        HomeTab.ALARM -> Icons.Default.Alarm
                        HomeTab.PROFIL -> Icons.Default.Person
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onTabSelected(tab) }
                            .padding(vertical = 4.dp)
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
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
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
                .heightIn(min = 50.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.home_btn_setup_profile),
                style = KanguruTheme.typography.titleSmall,
                color = KanguruTheme.colors.onPrimary
            )
        }
    }
}
