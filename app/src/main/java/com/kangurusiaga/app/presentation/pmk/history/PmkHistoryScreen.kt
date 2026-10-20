package com.kangurusiaga.app.presentation.pmk.history

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.presentation.pmk.statistics.PmkStatisticsUiState
import com.kangurusiaga.app.presentation.pmk.statistics.PmkStatisticsViewModel

/**
 * Screen ID: 9970837237722903241
 * Title: Kanguru Siaga - Riwayat PMK (Rekap Harian Expandable)
 */
@Composable
fun PmkHistoryRoute(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: () -> Unit,
    onNavigateToDailyDetail: (String) -> Unit,
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
        onNavigateToDailyDetail = onNavigateToDailyDetail,
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
    onNavigateToDailyDetail: (String) -> Unit,
    onNavigateToManualLog: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToEducation: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    // Group sessions into daily summaries
    val dailyGroups = remember(uiState.sessions) {
        groupSessionsByDay(uiState.sessions)
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
                            .border(1.dp, colors.cardBorder, CircleShape)
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
                        onClick = { /* Filter or calendar report */ },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(colors.surfaceElevated)
                            .border(1.dp, colors.cardBorder, CircleShape)
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
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
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
                                fontWeight = FontWeight.Bold,
                                color = colors.onSuccessContainer
                            )
                        }
                    }
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
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
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
                        border = BorderStroke(1.dp, colors.cardBorder),
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
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }
                }
            }

            // =========================================================================
            // GROUPED DAILY RECORDS (Linear 24h Progress Summary Cards)
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
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(dailyGroups, key = { it.dateKey }) { dailyGroup ->
                    DailyRecordCard(
                        dailyGroup = dailyGroup,
                        onClick = {
                            onNavigateToDailyDetail(dailyGroup.dateKey)
                        }
                    )
                }
            }

            // =========================================================================
            // BOTTOM MOTIVATION BANNER (Dekapan Hangat Bunda & Ayah)
            // =========================================================================
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.pmkMotivationCardBg),
                    border = BorderStroke(1.dp, colors.pmkDoctorNoteBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(colors.pmkMotivationHeartBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = colors.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Dekapan Hangat Bunda & Ayah",
                                style = KanguruTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Kontak >20 jam per hari mempercepat kenaikan berat badan bayi hingga 25% lebih optimal.",
                                style = KanguruTheme.typography.labelSmall,
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// SUB-COMPONENTS
// =============================================================================

/**
 * Stitch Screen: Kanguru Siaga - Riwayat PMK (Rekap Harian Expandable)
 * Screen ID: 9970837237722903241
 */
@Composable
private fun DailyRecordCard(
    dailyGroup: DailyGroup,
    onClick: () -> Unit
) {
    val colors = KanguruTheme.colors
    val timeline = dailyGroup.timeline
    val activeHours = timeline.totalActiveMinutes / 60
    val activeMinutes = timeline.totalActiveMinutes % 60
    val isTargetMet = timeline.totalActiveMinutes >= (8 * 60)
    val percentOf8h = ((timeline.totalActiveMinutes.toFloat() / (8 * 60f)) * 100).toInt()

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        border = BorderStroke(1.dp, colors.cardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Top Row: Day Label + Date + Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    val dayLabel = when {
                        dailyGroup.isToday -> "HARI INI"
                        dailyGroup.isYesterday -> "KEMARIN"
                        else -> null
                    }
                    if (dayLabel != null) {
                        Text(
                            text = dayLabel,
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (dailyGroup.isToday) colors.pmkTargetMetMarker else colors.textTertiary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                    Text(
                        text = dailyGroup.dateFormatted,
                        style = KanguruTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }

                // Target Met / Unmet Badge
                if (isTargetMet) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.pmkDoctorNoteBg)
                            .border(1.dp, colors.pmkDoctorNoteBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Target Terpenuhi ✓",
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.pmkTargetMetMarker
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.pmkPauseDashedBg)
                            .border(1.dp, colors.pmkPauseDashedBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Target Tidak Terpenuhi",
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.pmkCaregiverPendamping
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Inner Progress Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surfaceVariant.copy(alpha = 0.5f))
                    .padding(12.dp)
            ) {
                Column {
                    // Title row: "Progres PMK Harian" + Badge + Duration
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Progres PMK Harian",
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            if (isTargetMet) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(colors.pmkDoctorNoteBg)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "≥ 8 Jam (Terpenuhi)",
                                        style = KanguruTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.pmkTargetMetMarker
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(colors.pmkPauseDashedBg)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "≤ 8 Jam ($percentOf8h%)",
                                        style = KanguruTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.pmkCaregiverPendamping
                                    )
                                }
                            }
                        }

                        val durationText = "${activeHours}j ${activeMinutes}m / 8j"
                        Text(
                            text = durationText,
                            style = KanguruTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Linear 24-Hour Progress Bar with 8h Target Divider (at 33.3%)
                    PmkDailyProgressBar(
                        activeMinutes = timeline.totalActiveMinutes,
                        isTargetMet = isTargetMet
                    )
                }
            }
        }
    }
}

/**
 * 24-Hour Linear Progress Bar with 8h Target Divider (at 33.33%) and Scale Labels
 */
@Composable
private fun PmkDailyProgressBar(
    activeMinutes: Int,
    isTargetMet: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val activeFraction = (activeMinutes.toFloat() / (24 * 60f)).coerceIn(0f, 1f)
    val target8hFraction = 8f / 24f // 0.3333f

    Column(modifier = modifier.fillMaxWidth()) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(colors.cardBorder)
        ) {
            val totalWidth = maxWidth

            // Active Progress Fill
            if (activeFraction > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(activeFraction)
                        .fillMaxHeight()
                        .background(if (isTargetMet) colors.pmkTargetMetMarker else colors.pmkCaregiverPendamping)
                )
            }

            // 8-Hour Target Divider Marker Line at 33.33%
            val markerX = totalWidth * target8hFraction
            Box(
                modifier = Modifier
                    .padding(start = (markerX - 1.dp).coerceAtLeast(0.dp))
                .width(2.dp)
                .fillMaxHeight()
                .background(
                    if (activeFraction >= target8hFraction) {
                        colors.cardBackground.copy(alpha = 0.85f)
                    } else {
                        colors.pmkTargetUnmetMarker
                    }
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Scale Labels: 0 Jam, 8 Jam (Target / ★), 16 Jam, 24 Jam
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val totalWidth = maxWidth

            // 0 Jam (Start)
            Text(
                text = "0 Jam",
                style = KanguruTheme.typography.labelSmall,
                color = colors.textTertiary,
                modifier = Modifier.align(Alignment.CenterStart)
            )

            // 8 Jam (Target) (at ~33.3%)
            val targetText = if (isTargetMet) "8 Jam ★" else "8 Jam (Target)"
            val targetColor = if (isTargetMet) colors.pmkTargetMetMarker else colors.textSecondary
            Text(
                text = targetText,
                style = KanguruTheme.typography.labelSmall,
                fontWeight = if (isTargetMet) FontWeight.Bold else FontWeight.Medium,
                color = targetColor,
                modifier = Modifier
                    .padding(start = (totalWidth * target8hFraction - 24.dp).coerceAtLeast(36.dp))
            )

            // 16 Jam (at ~66.7%)
            Text(
                text = "16 Jam",
                style = KanguruTheme.typography.labelSmall,
                color = colors.textTertiary,
                modifier = Modifier
                    .padding(start = (totalWidth * (16f / 24f) - 18.dp).coerceAtLeast(70.dp))
            )

            // 24 Jam (End)
            Text(
                text = "24 Jam",
                style = KanguruTheme.typography.labelSmall,
                color = colors.textTertiary,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}
