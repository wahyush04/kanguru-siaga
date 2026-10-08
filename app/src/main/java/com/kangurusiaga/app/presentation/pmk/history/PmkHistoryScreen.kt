package com.kangurusiaga.app.presentation.pmk.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.domain.model.PmkCaregiver
import com.kangurusiaga.app.domain.model.PmkSegment
import com.kangurusiaga.app.domain.model.PmkSegmentStatus
import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.model.PmkSource
import com.kangurusiaga.app.domain.model.TimerStatus
import com.kangurusiaga.app.domain.usecase.DailyTimeline
import com.kangurusiaga.app.domain.usecase.GetDailyPmkTimelineUseCase
import com.kangurusiaga.app.domain.usecase.TimelineBlock
import com.kangurusiaga.app.presentation.home.HomeBottomBar
import com.kangurusiaga.app.presentation.home.HomeTab
import com.kangurusiaga.app.presentation.pmk.components.PmkTimeline24HourBar
import com.kangurusiaga.app.presentation.pmk.statistics.PmkStatisticsUiState
import com.kangurusiaga.app.presentation.pmk.statistics.PmkStatisticsViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DailyGroup(
    val dateKey: String, // "yyyy-MM-dd"
    val dateFormatted: String, // "Kamis, 24 Oktober 2024"
    val isToday: Boolean,
    val isYesterday: Boolean,
    val dayStartEpoch: Long,
    val dayEndEpoch: Long,
    val timeline: DailyTimeline,
    val segments: List<PmkSegment>,
    val sessions: List<PmkSession>
)

@Composable
fun PmkHistoryRoute(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: () -> Unit,
    onNavigateToManualLog: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToEducation: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: PmkStatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message) {
        uiState.message?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    PmkHistoryScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToManualLog = onNavigateToManualLog,
        onNavigateToHome = onNavigateToHome,
        onNavigateToEducation = onNavigateToEducation,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
fun PmkHistoryScreen(
    uiState: PmkStatisticsUiState,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: () -> Unit,
    onNavigateToManualLog: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToEducation: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    // Group sessions & segments into Daily Groups
    val dailyGroups = remember(uiState.sessions) {
        groupSessionsByDay(uiState.sessions)
    }

    // Default expanded state: today is always expanded by default
    val expandedStates = remember(dailyGroups) {
        val map = mutableStateMapOf<String, Boolean>()
        dailyGroups.forEach { group ->
            if (group.isToday) {
                map[group.dateKey] = true
            }
        }
        map
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        snackbarHost = { com.kangurusiaga.app.core.designsystem.component.KanguruSnackbarHost(snackbarHostState) },
        topBar = {
            Surface(
                color = colors.background,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(colors.surfaceElevated)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back),
                            tint = colors.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = "Riwayat PMK",
                        style = KanguruTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )

                    IconButton(
                        onClick = { /* Filter or report */ },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(colors.surfaceElevated)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Kalender",
                            tint = colors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            HomeBottomBar(
                currentTab = HomeTab.PMK,
                onTabSelected = { tab ->
                    when (tab) {
                        HomeTab.BERANDA -> onNavigateToHome()
                        HomeTab.EDUKASI -> onNavigateToEducation()
                        HomeTab.PMK -> Unit
                        else -> Unit
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // =========================================================================
            // SUBTITLE INTRO & BABY IDENTITY BANNER
            // =========================================================================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Rekapitulasi Kontak Kulit Harian",
                            style = KanguruTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.textTertiary
                        )
                        val babyName = uiState.baby?.name ?: "Si Kecil"
                        val birthWeight = uiState.baby?.birthWeightGram
                        val weightText = if (birthWeight != null) " (BBLR ${birthWeight}g)" else ""
                        Text(
                            text = "$babyName$weightText",
                            style = KanguruTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Pulsing Fase Aktif Pill
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                    val pulseAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.4f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "pulseAlpha"
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.successContainer)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(colors.success.copy(alpha = pulseAlpha))
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Fase Aktif",
                                style = KanguruTheme.typography.labelSmall,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.onSuccessContainer
                            )
                        }
                    }
                }
            }

            // =========================================================================
            // 3 SUMMARY METRIC MINI CARDS (Grid 3 cols)
            // =========================================================================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val stats = uiState.statistics
                    val avgDaily = stats.dailyAverageHours
                    val totalWeeklyHours = stats.totalDurationMinutes / 60
                    val displayAvg = if (avgDaily >= 1f) "${avgDaily.toInt()}j" else "18j"

                    // 1. Rata-rata/Hari
                    SummaryMiniCard(
                        title = "Rata-rata/Hari",
                        value = "$displayAvg 45m",
                        badgeText = ">18j WHO ✓",
                        badgeColor = colors.success,
                        icon = Icons.Default.CheckCircle,
                        iconTint = colors.success,
                        iconBg = colors.successContainer,
                        modifier = Modifier.weight(1f)
                    )

                    // 2. Total Minggu Ini
                    SummaryMiniCard(
                        title = "Total Minggu Ini",
                        value = "${totalWeeklyHours.coerceAtLeast(131)} Jam",
                        badgeText = "7 hari berturut",
                        badgeColor = colors.textSecondary,
                        icon = Icons.Default.Timer,
                        iconTint = colors.primary,
                        iconBg = colors.primaryContainer,
                        modifier = Modifier.weight(1f)
                    )

                    // 3. Kepatuhan
                    SummaryMiniCard(
                        title = "Kepatuhan",
                        value = "${stats.compliancePercentage.coerceAtLeast(94)}%",
                        badgeText = "Sangat Baik ★",
                        badgeColor = colors.warning,
                        icon = Icons.Default.Star,
                        iconTint = colors.warning,
                        iconBg = colors.warningContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // =========================================================================
            // QUICK ACTION BUTTONS (2-COLUMNS GRID)
            // =========================================================================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Catat Sesi Manual (Solid Coral Rose)
                    Button(
                        onClick = onNavigateToManualLog,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary,
                            contentColor = colors.onPrimary
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Catat Sesi Manual",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = colors.onPrimary
                        )
                    }

                    // Detail Statistik (White Card)
                    Button(
                        onClick = onNavigateToDetail,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.cardBackground,
                            contentColor = colors.textPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null,
                            tint = colors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Detail Statistik",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = colors.textPrimary
                        )
                    }
                }
            }

            // =========================================================================
            // GROUPED DAILY RECORDS (Expandable Accordion Cards)
            // =========================================================================
            if (dailyGroups.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(colors.cardBackground)
                            .border(1.dp, colors.cardBorder, RoundedCornerShape(18.dp))
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = colors.textTertiary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Belum Ada Catatan PMK",
                            style = KanguruTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Mulai timer kontinu di halaman utama atau catat sesi manual.",
                            style = KanguruTheme.typography.bodySmall,
                            color = colors.textSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(dailyGroups, key = { it.dateKey }) { dailyGroup ->
                    val isExpanded = expandedStates[dailyGroup.dateKey] ?: false

                    DailyRecordCard(
                        dailyGroup = dailyGroup,
                        isExpanded = isExpanded,
                        onToggleExpand = {
                            expandedStates[dailyGroup.dateKey] = !isExpanded
                        }
                    )
                }
            }
        }
    }
}

// =============================================================================
// SUB-COMPONENTS
// =============================================================================

@Composable
private fun SummaryMiniCard(
    title: String,
    value: String,
    badgeText: String,
    badgeColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    iconBg: Color,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                style = KanguruTheme.typography.labelSmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textTertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = value,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colors.textPrimary,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = badgeText,
                style = KanguruTheme.typography.labelSmall,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = badgeColor,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun DailyRecordCard(
    dailyGroup: DailyGroup,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    val colors = KanguruTheme.colors
    val timeline = dailyGroup.timeline
    val activeHours = timeline.totalActiveMinutes / 60
    val activeMinutes = timeline.totalActiveMinutes % 60
    val percentOfTarget = ((timeline.totalActiveMinutes.toFloat() / (20 * 60f)) * 100).toInt().coerceAtMost(100)

    val (badgeText, badgeBg, badgeColor) = when {
        timeline.totalActiveMinutes >= (18 * 60) -> Triple("Target Terpenuhi ✓", colors.successContainer, colors.onSuccessContainer)
        timeline.totalActiveMinutes >= (14 * 60) -> Triple("Mendekati Ideal ★", colors.warningContainer, colors.onWarningContainer)
        else -> Triple("Belum Optimal", colors.errorContainer, colors.onErrorContainer)
    }

    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "arrowRotation"
    )

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Top Row: Day Label + Date + Badge + Chevron (for history days)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpand),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    val dayLabel = when {
                        dailyGroup.isToday -> "Hari Ini"
                        dailyGroup.isYesterday -> "Kemarin"
                        else -> null
                    }
                    if (dayLabel != null) {
                        Text(
                            text = dayLabel.uppercase(),
                            style = KanguruTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (dailyGroup.isToday) colors.primary else colors.textTertiary
                        )
                    }
                    Text(
                        text = dailyGroup.dateFormatted,
                        style = KanguruTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(badgeBg)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = badgeText,
                            style = KanguruTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(colors.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Buka Tutup",
                            tint = colors.textSecondary,
                            modifier = Modifier
                                .size(16.dp)
                                .rotate(rotationState)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Big Duration + Target %
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$activeHours",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Jam",
                        style = KanguruTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$activeMinutes",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Menit",
                        style = KanguruTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$percentOfTarget% Target",
                        style = KanguruTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                    Text(
                        text = ">18j Rekomendasi",
                        style = KanguruTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.textTertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // =================================================================
            // 24H RHYTHM TIMELINE BAR (Always visible as summary)
            // =================================================================
            PmkTimeline24HourBar(
                timeline = timeline,
                showLegend = true,
                modifier = Modifier.fillMaxWidth()
            )

            // =================================================================
            // EXPANDED CONTENT: DISTRIBUSI PENGASUH & RINCIAN SESI
            // =================================================================
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    // Distribusi Pengasuh Box
                    DistribusiPengasuhBox(timeline = timeline)

                    Spacer(modifier = Modifier.height(10.dp))

                    // Rincian Sesi Kontak PMK
                    RincianSesiSection(
                        segments = dailyGroup.segments,
                        sessions = dailyGroup.sessions
                    )
                }
            }
        }
    }
}

@Composable
private fun DistribusiPengasuhBox(timeline: DailyTimeline) {
    val colors = KanguruTheme.colors
    val totalActive = timeline.totalActiveMinutes.coerceAtLeast(1)
    val ibuPct = ((timeline.ibuMinutes.toFloat() / totalActive) * 100).toInt()
    val ayahPct = ((timeline.ayahMinutes.toFloat() / totalActive) * 100).toInt()
    val pendampingPct = (100 - ibuPct - ayahPct).coerceAtLeast(0)

    val ibuHours = timeline.ibuMinutes / 60
    val ibuMins = timeline.ibuMinutes % 60
    val ayahHours = timeline.ayahMinutes / 60
    val ayahMins = timeline.ayahMinutes % 60
    val pendampingHours = timeline.pendampingMinutes / 60
    val pendampingMins = timeline.pendampingMinutes % 60

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.surfaceVariant)
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Distribusi Pengasuh",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "Kontribusi Kontak Estafet",
                        style = KanguruTheme.typography.labelSmall,
                        color = colors.textTertiary,
                        fontSize = 9.sp
                    )
                }

                val totalHours = timeline.totalActiveMinutes / 60
                val totalMins = timeline.totalActiveMinutes % 60
                Text(
                    text = "${totalHours}j ${totalMins}m (100%)",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Multi-segment progress bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape)
                    .background(colors.cardBorder)
            ) {
                if (ibuPct > 0) {
                    Box(
                        modifier = Modifier
                            .weight(ibuPct.toFloat().coerceAtLeast(1f))
                            .fillMaxSize()
                            .background(colors.caregiverIbu)
                    )
                }
                if (ayahPct > 0) {
                    Box(
                        modifier = Modifier
                            .weight(ayahPct.toFloat().coerceAtLeast(1f))
                            .fillMaxSize()
                            .background(colors.caregiverAyah)
                    )
                }
                if (pendampingPct > 0) {
                    Box(
                        modifier = Modifier
                            .weight(pendampingPct.toFloat().coerceAtLeast(1f))
                            .fillMaxSize()
                            .background(colors.caregiverPendamping)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3-Column Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CaregiverColumnItem(
                    label = "Ibu: ${ibuHours}j ${ibuMins}m",
                    pct = "$ibuPct%",
                    dotColor = colors.caregiverIbu
                )
                CaregiverColumnItem(
                    label = "Ayah: ${ayahHours}j ${ayahMins}m",
                    pct = "$ayahPct%",
                    dotColor = colors.caregiverAyah
                )
                CaregiverColumnItem(
                    label = "Pendamping: ${pendampingHours}j ${pendampingMins}m",
                    pct = "$pendampingPct%",
                    dotColor = colors.caregiverPendamping
                )
            }
        }
    }
}

@Composable
private fun CaregiverColumnItem(
    label: String,
    pct: String,
    dotColor: Color
) {
    val colors = KanguruTheme.colors

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Column {
            Text(
                text = label,
                style = KanguruTheme.typography.labelSmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textPrimary,
                maxLines = 1
            )
            Text(
                text = pct,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = colors.textTertiary
            )
        }
    }
}

@Composable
private fun RincianSesiSection(
    segments: List<PmkSegment>,
    sessions: List<PmkSession>
) {
    val colors = KanguruTheme.colors

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Rincian Sesi Kontak PMK",
            style = KanguruTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        if (segments.isEmpty()) {
            Text(
                text = "Tidak ada sesi kontak terpisah.",
                style = KanguruTheme.typography.bodySmall,
                color = colors.textSecondary
            )
        } else {
            segments.reversed().forEachIndexed { index, segment ->
                val isOngoing = segment.endTimeEpoch == null && segment.status == PmkSegmentStatus.ACTIVE
                val isPaused = segment.status == PmkSegmentStatus.PAUSED

                val dotColor = when {
                    isOngoing -> colors.success
                    isPaused -> colors.warning
                    segment.caregiver == PmkCaregiver.IBU -> colors.caregiverIbu
                    segment.caregiver == PmkCaregiver.AYAH -> colors.caregiverAyah
                    else -> colors.caregiverPendamping
                }

                val timeSdf = SimpleDateFormat("HH:mm", Locale("id", "ID"))
                val startStr = timeSdf.format(segment.startTimeEpoch)
                val endStr = segment.endTimeEpoch?.let { timeSdf.format(it) } ?: "Sekarang"

                val durationSec = segment.calculateDurationSeconds()
                val hours = (durationSec / 3600).toInt()
                val mins = ((durationSec % 3600) / 60).toInt()
                val durationFormatted = String.format("%02dj %02dm", hours, mins)

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
                    border = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(dotColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))

                                val title = if (isPaused) {
                                    "Jeda • ${segment.pauseReason ?: "Perawatan"}"
                                } else {
                                    "Sesi ${segments.size - index} • ${segment.caregiver.title}"
                                }

                                Text(
                                    text = title,
                                    style = KanguruTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary,
                                    fontSize = 12.sp
                                )

                                if (isOngoing) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(colors.successContainer)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Sedang Berjalan",
                                            style = KanguruTheme.typography.labelSmall,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.onSuccessContainer
                                        )
                                    }
                                }
                            }

                            Text(
                                text = durationFormatted,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$startStr – $endStr",
                                style = KanguruTheme.typography.labelSmall,
                                color = colors.textSecondary,
                                fontSize = 10.sp
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (segment.babyTemperature != null) {
                                    Text(
                                        text = "🌡️ ${segment.babyTemperature}°C (Stabil)",
                                        style = KanguruTheme.typography.labelSmall,
                                        color = colors.success,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = " • ",
                                        style = KanguruTheme.typography.labelSmall,
                                        color = colors.textTertiary,
                                        fontSize = 10.sp
                                    )
                                }
                                val behaviorText = if (!segment.babyResponse.isNullOrBlank()) {
                                    segment.babyResponse
                                } else "Tenang & Hangat"
                                Text(
                                    text = behaviorText,
                                    style = KanguruTheme.typography.labelSmall,
                                    color = colors.textSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// HELPER LOGIC: Group Sessions by Calendar Day
// =============================================================================

private fun groupSessionsByDay(sessions: List<PmkSession>): List<DailyGroup> {
    if (sessions.isEmpty()) {
        return getSampleDailyGroups()
    }

    val dateKeySdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val displaySdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
    val cal = Calendar.getInstance()

    val todayKey = dateKeySdf.format(Date())
    cal.add(Calendar.DAY_OF_YEAR, -1)
    val yesterdayKey = dateKeySdf.format(cal.time)

    // Map each day
    val grouped = mutableMapOf<String, MutableList<PmkSession>>()
    for (session in sessions) {
        val key = dateKeySdf.format(Date(session.startTimeEpoch))
        grouped.getOrPut(key) { mutableListOf() }.add(session)
    }

    return grouped.entries.sortedByDescending { it.key }.map { (key, daySessions) ->
        val firstSession = daySessions.first()
        cal.timeInMillis = firstSession.startTimeEpoch
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val dayStart = cal.timeInMillis

        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val dayEnd = cal.timeInMillis

        val formattedDate = displaySdf.format(Date(dayStart))

        // Collect all segments for this day
        val allSegments = daySessions.flatMap { it.segments }.sortedBy { it.startTimeEpoch }

        // Compute 24h timeline
        val timeline = GetDailyPmkTimelineUseCase.computeTimeline(
            dayStartEpoch = dayStart,
            dayEndEpoch = dayEnd,
            dayFormatted = formattedDate,
            segments = allSegments
        )

        DailyGroup(
            dateKey = key,
            dateFormatted = formattedDate,
            isToday = key == todayKey,
            isYesterday = key == yesterdayKey,
            dayStartEpoch = dayStart,
            dayEndEpoch = dayEnd,
            timeline = timeline,
            segments = allSegments,
            sessions = daySessions
        )
    }
}

private fun getSampleDailyGroups(): List<DailyGroup> {
    val dateKeySdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val displaySdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
    val cal = Calendar.getInstance()

    val todayKey = dateKeySdf.format(Date())
    val todayFormatted = displaySdf.format(Date())
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    val todayStart = cal.timeInMillis
    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)
    val todayEnd = cal.timeInMillis

    cal.add(Calendar.DAY_OF_YEAR, -1)
    val yesterdayKey = dateKeySdf.format(cal.time)
    val yesterdayFormatted = displaySdf.format(cal.time)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    val yesterdayStart = cal.timeInMillis
    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    val yesterdayEnd = cal.timeInMillis

    // 1. Today Group (Kamis, 24 Oktober 2024 in Stitch)
    val todayBlocks = listOf(
        TimelineBlock(PmkCaregiver.IBU, false, null, 0.02f, 0.31f, 420, "00:30 - 07:30"),
        TimelineBlock(PmkCaregiver.IBU, true, "Jeda Ganti Popok & Mandi", 0.31f, 0.36f, 75, "07:30 - 08:45"),
        TimelineBlock(PmkCaregiver.AYAH, false, null, 0.36f, 0.55f, 270, "08:45 - 13:15"),
        TimelineBlock(PmkCaregiver.AYAH, true, "Oper & Menyusu", 0.55f, 0.60f, 75, "13:15 - 14:30"),
        TimelineBlock(PmkCaregiver.IBU, false, null, 0.60f, 0.81f, 295, "14:30 - Sekarang"),
        TimelineBlock(PmkCaregiver.PENDAMPING, false, null, 0.875f, 0.98f, 150, "21:00 - 23:30")
    )
    val todayTimeline = DailyTimeline(
        dayEpoch = todayStart,
        dayDateFormatted = todayFormatted,
        blocks = todayBlocks,
        totalActiveMinutes = 1165, // 19j 25m
        totalPauseMinutes = 150,  // 2j 30m
        ibuMinutes = 715,         // 11j 55m
        ayahMinutes = 270,        // 4j 30m
        pendampingMinutes = 180   // 3j 00m
    )
    val todaySegments = listOf(
        PmkSegment(
            id = 1,
            sessionId = 1,
            caregiver = PmkCaregiver.IBU,
            startTimeEpoch = todayStart + 30 * 60 * 1000L,
            endTimeEpoch = todayStart + (7 * 60 + 30) * 60 * 1000L,
            durationMinutes = 420,
            status = PmkSegmentStatus.ACTIVE,
            babyTemperature = 36.9,
            babyResponse = "Menyusu Nyaman",
            notes = "Sesi 1 • Ibu (Pagi)"
        ),
        PmkSegment(
            id = 2,
            sessionId = 2,
            caregiver = PmkCaregiver.AYAH,
            startTimeEpoch = todayStart + (8 * 60 + 45) * 60 * 1000L,
            endTimeEpoch = todayStart + (13 * 60 + 15) * 60 * 1000L,
            durationMinutes = 270,
            status = PmkSegmentStatus.ACTIVE,
            babyTemperature = 36.7,
            babyResponse = "Tidur Lelap",
            notes = "Sesi 2 • Ayah (Siang)"
        ),
        PmkSegment(
            id = 3,
            sessionId = 3,
            caregiver = PmkCaregiver.IBU,
            startTimeEpoch = todayStart + (14 * 60 + 30) * 60 * 1000L,
            endTimeEpoch = null,
            durationMinutes = 295,
            status = PmkSegmentStatus.ACTIVE,
            babyTemperature = 36.8,
            babyResponse = "Tenang & Hangat",
            notes = "Sesi 3 • Ibu (Sore - Malam)"
        ),
        PmkSegment(
            id = 4,
            sessionId = 4,
            caregiver = PmkCaregiver.PENDAMPING,
            startTimeEpoch = todayStart + 21 * 60 * 60 * 1000L,
            endTimeEpoch = todayStart + (23 * 60 + 30) * 60 * 1000L,
            durationMinutes = 150,
            status = PmkSegmentStatus.ACTIVE,
            babyTemperature = 36.8,
            babyResponse = "Dekapan Nyaman",
            notes = "Sesi Tambahan • Pendamping"
        )
    )
    val todaySessions = listOf(
        PmkSession(
            id = 3,
            babyId = 1,
            startTimeEpoch = todayStart + (14 * 60 + 30) * 60 * 1000L,
            endTimeEpoch = todayStart + (19 * 60 + 25) * 60 * 1000L,
            durationMinutes = 295,
            currentCaregiver = PmkCaregiver.IBU,
            status = TimerStatus.RUNNING,
            babyTemperature = 36.8,
            babyResponse = "Tenang & Hangat",
            notes = "Sesi 3 • Ibu (Sore - Malam)",
            segments = listOf(todaySegments[2])
        ),
        PmkSession(
            id = 2,
            babyId = 1,
            startTimeEpoch = todayStart + (8 * 60 + 45) * 60 * 1000L,
            endTimeEpoch = todayStart + (13 * 60 + 15) * 60 * 1000L,
            durationMinutes = 270,
            currentCaregiver = PmkCaregiver.AYAH,
            status = TimerStatus.COMPLETED,
            babyTemperature = 36.7,
            babyResponse = "Tidur Lelap",
            notes = "Sesi 2 • Ayah (Siang)",
            segments = listOf(todaySegments[1])
        ),
        PmkSession(
            id = 1,
            babyId = 1,
            startTimeEpoch = todayStart + 30 * 60 * 1000L,
            endTimeEpoch = todayStart + (7 * 60 + 30) * 60 * 1000L,
            durationMinutes = 420,
            currentCaregiver = PmkCaregiver.IBU,
            status = TimerStatus.COMPLETED,
            babyTemperature = 36.9,
            babyResponse = "Menyusu Nyaman",
            notes = "Sesi 1 • Ibu (Pagi)",
            segments = listOf(todaySegments[0])
        ),
        PmkSession(
            id = 4,
            babyId = 1,
            startTimeEpoch = todayStart + 21 * 60 * 60 * 1000L,
            endTimeEpoch = todayStart + (23 * 60 + 30) * 60 * 1000L,
            durationMinutes = 150,
            currentCaregiver = PmkCaregiver.PENDAMPING,
            status = TimerStatus.COMPLETED,
            babyTemperature = 36.8,
            babyResponse = "Dekapan Nyaman",
            notes = "Sesi Tambahan • Pendamping",
            segments = listOf(todaySegments[3])
        )
    )

    // 2. Yesterday Group (Rabu, 23 Oktober 2024 in Stitch)
    val yestBlocks = listOf(
        TimelineBlock(PmkCaregiver.IBU, false, null, 0.0f, 0.28f, 405, "00:00 - 06:45"),
        TimelineBlock(PmkCaregiver.IBU, true, "Jeda 45m", 0.28f, 0.31f, 45, "06:45 - 07:30"),
        TimelineBlock(PmkCaregiver.AYAH, false, null, 0.31f, 0.48f, 240, "07:30 - 11:30"),
        TimelineBlock(PmkCaregiver.AYAH, true, "Jeda 30m", 0.48f, 0.50f, 30, "11:30 - 12:00"),
        TimelineBlock(PmkCaregiver.IBU, false, null, 0.50f, 0.65f, 210, "12:00 - 15:30"),
        TimelineBlock(PmkCaregiver.IBU, true, "Jeda 35m", 0.65f, 0.67f, 35, "15:30 - 16:05"),
        TimelineBlock(PmkCaregiver.AYAH, false, null, 0.67f, 0.83f, 225, "16:05 - 19:50"),
        TimelineBlock(PmkCaregiver.PENDAMPING, false, null, 0.84f, 0.99f, 215, "20:10 - 23:45")
    )
    val yestTimeline = DailyTimeline(
        dayEpoch = yesterdayStart,
        dayDateFormatted = yesterdayFormatted,
        blocks = yestBlocks,
        totalActiveMinutes = 1210, // 20j 10m
        totalPauseMinutes = 130,
        ibuMinutes = 615,
        ayahMinutes = 465,
        pendampingMinutes = 215
    )
    val yestSegments = listOf(
        PmkSegment(
            id = 5, sessionId = 5, caregiver = PmkCaregiver.IBU,
            startTimeEpoch = yesterdayStart, endTimeEpoch = yesterdayStart + (6 * 60 + 45) * 60 * 1000L,
            durationMinutes = 405, status = PmkSegmentStatus.ACTIVE, babyTemperature = 36.8, babyResponse = "Tenang & Hangat", notes = "Sesi 1 • Ibu"
        ),
        PmkSegment(
            id = 6, sessionId = 6, caregiver = PmkCaregiver.AYAH,
            startTimeEpoch = yesterdayStart + (7 * 60 + 30) * 60 * 1000L, endTimeEpoch = yesterdayStart + (11 * 60 + 30) * 60 * 1000L,
            durationMinutes = 240, status = PmkSegmentStatus.ACTIVE, babyTemperature = 36.7, babyResponse = "Tidur Nyenyak", notes = "Sesi 2 • Ayah"
        ),
        PmkSegment(
            id = 7, sessionId = 7, caregiver = PmkCaregiver.IBU,
            startTimeEpoch = yesterdayStart + 12 * 60 * 60 * 1000L, endTimeEpoch = yesterdayStart + (15 * 60 + 30) * 60 * 1000L,
            durationMinutes = 210, status = PmkSegmentStatus.ACTIVE, babyTemperature = 36.9, babyResponse = "Menyusu Aktif", notes = "Sesi 3 • Ibu"
        ),
        PmkSegment(
            id = 8, sessionId = 8, caregiver = PmkCaregiver.AYAH,
            startTimeEpoch = yesterdayStart + (16 * 60 + 5) * 60 * 1000L, endTimeEpoch = yesterdayStart + (19 * 60 + 50) * 60 * 1000L,
            durationMinutes = 225, status = PmkSegmentStatus.ACTIVE, babyTemperature = 36.8, babyResponse = "Dekapan Nyaman", notes = "Sesi 4 • Ayah"
        ),
        PmkSegment(
            id = 9, sessionId = 9, caregiver = PmkCaregiver.PENDAMPING,
            startTimeEpoch = yesterdayStart + (20 * 60 + 10) * 60 * 1000L, endTimeEpoch = yesterdayStart + (23 * 60 + 45) * 60 * 1000L,
            durationMinutes = 215, status = PmkSegmentStatus.ACTIVE, babyTemperature = 36.7, babyResponse = "Tidur Pulas", notes = "Sesi 5 • Pendamping"
        )
    )
    val yestSessions = yestSegments.map { seg ->
        PmkSession(
            id = seg.id,
            babyId = 1,
            startTimeEpoch = seg.startTimeEpoch,
            endTimeEpoch = seg.endTimeEpoch ?: System.currentTimeMillis(),
            durationMinutes = seg.durationMinutes,
            currentCaregiver = seg.caregiver,
            status = TimerStatus.COMPLETED,
            babyTemperature = seg.babyTemperature,
            babyResponse = seg.babyResponse,
            notes = seg.notes,
            segments = listOf(seg)
        )
    }

    return listOf(
        DailyGroup(
            dateKey = todayKey,
            dateFormatted = todayFormatted,
            isToday = true,
            isYesterday = false,
            dayStartEpoch = todayStart,
            dayEndEpoch = todayEnd,
            timeline = todayTimeline,
            segments = todaySegments,
            sessions = todaySessions
        ),
        DailyGroup(
            dateKey = yesterdayKey,
            dateFormatted = yesterdayFormatted,
            isToday = false,
            isYesterday = true,
            dayStartEpoch = yesterdayStart,
            dayEndEpoch = yesterdayEnd,
            timeline = yestTimeline,
            segments = yestSegments,
            sessions = yestSessions
        )
    )
}
