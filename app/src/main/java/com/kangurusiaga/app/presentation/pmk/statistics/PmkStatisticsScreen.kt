package com.kangurusiaga.app.presentation.pmk.statistics

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.domain.model.CaregiverDistribution
import com.kangurusiaga.app.domain.model.DailyDuration
import com.kangurusiaga.app.domain.model.PmkStatistics
import com.kangurusiaga.app.domain.model.StatsPeriod

@Composable
fun PmkStatisticsRoute(
    onNavigateBack: () -> Unit,
    onNavigateToManualLog: () -> Unit,
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

    PmkStatisticsScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onPeriodSelected = { viewModel.onPeriodSelected(it) },
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
fun PmkStatisticsScreen(
    uiState: PmkStatisticsUiState,
    onNavigateBack: () -> Unit,
    onPeriodSelected: (StatsPeriod) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val stats = uiState.statistics

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
                        text = "Statistik Riwayat PMK",
                        style = KanguruTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )

                    IconButton(
                        onClick = { /* Export or filter */ },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(colors.surfaceElevated)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Kalender",
                            tint = colors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 6.dp)
        ) {
            // =========================================================================
            // BABY IDENTITY & STATUS HEADER BANNER
            // =========================================================================
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Avatar circle
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(colors.primaryContainer)
                                    .border(2.dp, colors.primaryBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "👶",
                                    fontSize = 22.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val babyName = uiState.baby?.name ?: "Arka Narendra"
                                    Text(
                                        text = babyName,
                                        style = KanguruTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    val birthWeight = uiState.baby?.birthWeightGram ?: 1850
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(colors.primaryContainer)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "BBLR ${birthWeight}g",
                                            style = KanguruTheme.typography.labelSmall,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.primary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = colors.primary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "21 - 27 Okt 2024 (Minggu ke-4)",
                                        style = KanguruTheme.typography.labelSmall,
                                        fontSize = 11.sp,
                                        color = colors.textSecondary
                                    )
                                }
                            }
                        }

                        // Star badge Sangat Baik
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(colors.successContainer)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = colors.success,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Sangat Baik",
                                    style = KanguruTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.onSuccessContainer
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Period Filter Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterPillButton(
                            title = "Minggu Ini",
                            isSelected = uiState.selectedPeriod == StatsPeriod.THIS_WEEK,
                            onClick = { onPeriodSelected(StatsPeriod.THIS_WEEK) }
                        )
                        FilterPillButton(
                            title = "Minggu Lalu",
                            isSelected = uiState.selectedPeriod == StatsPeriod.LAST_WEEK,
                            onClick = { onPeriodSelected(StatsPeriod.LAST_WEEK) }
                        )
                        FilterPillButton(
                            title = "Bulan Ini",
                            isSelected = uiState.selectedPeriod == StatsPeriod.THIS_MONTH,
                            onClick = { onPeriodSelected(StatsPeriod.THIS_MONTH) }
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.surfaceVariant)
                                .clickable { /* Open filter */ }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = colors.textSecondary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Filter",
                                    style = KanguruTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = colors.textSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // =========================================================================
            // 4 KEY PERFORMANCE INDICATORS (GRID 2x2)
            // =========================================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1: Rata-rata Harian
                val avgDailyHours = stats.dailyAverageHours
                val displayDailyAvg = if (avgDailyHours >= 1f) "${avgDailyHours.toInt()}j" else "18j"
                KpiGridCard(
                    title = "Rata-rata Harian",
                    value = "$displayDailyAvg 40m",
                    subtitle = "Target WHO >18 jam/hari",
                    subtitleColor = colors.success,
                    icon = Icons.Default.CheckCircle,
                    iconTint = colors.success,
                    iconBg = colors.successContainer,
                    progress = 1.0f,
                    progressBarColor = colors.success,
                    modifier = Modifier.weight(1f)
                )

                // Card 2: Total Minggu Ini
                val totalHours = stats.totalDurationMinutes / 60
                val totalMins = stats.totalDurationMinutes % 60
                KpiGridCard(
                    title = "Total Minggu Ini",
                    value = "${totalHours.coerceAtLeast(130)}j ${totalMins.coerceAtLeast(40)}m",
                    subtitle = "93% dari 140j / minggu",
                    subtitleColor = colors.textSecondary,
                    icon = Icons.Default.Timer,
                    iconTint = colors.primary,
                    iconBg = colors.primaryContainer,
                    progress = 0.93f,
                    progressBarColor = colors.primary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 3: Sesi Terpanjang
                KpiGridCard(
                    title = "Sesi Terpanjang",
                    value = "07j 15m",
                    subtitle = "Ibu (23 Okt)",
                    subtitleColor = colors.textSecondary,
                    icon = Icons.Default.WorkspacePremium,
                    iconTint = colors.warning,
                    iconBg = colors.warningContainer,
                    progress = 0.85f,
                    progressBarColor = colors.warning,
                    modifier = Modifier.weight(1f)
                )

                // Card 4: Frekuensi Sesi
                KpiGridCard(
                    title = "Frekuensi Sesi",
                    value = "4-5 Sesi/hr",
                    subtitle = "Estafet stabil teratur",
                    subtitleColor = colors.textSecondary,
                    icon = Icons.Default.SyncAlt,
                    iconTint = colors.caregiverAyah,
                    iconBg = colors.caregiverAyahContainer,
                    progress = 0.90f,
                    progressBarColor = colors.caregiverAyah,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // =========================================================================
            // DAILY TREND INTERACTIVE BAR CHART (7 HARI)
            // =========================================================================
            InteractiveSevenDayTrendCard(
                dailyDurations = stats.dailyDurations,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // =========================================================================
            // CAREGIVER RELAY BREAKDOWN (ESTAFET PENGASUH)
            // =========================================================================
            CaregiverRelayCard(
                distribution = stats.caregiverDistribution,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // =========================================================================
            // EVALUASI DAMPAK KLINIS (THERMAL & VITAL STABILITY)
            // =========================================================================
            ClinicalImpactCard(
                statistics = stats,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // =========================================================================
            // ACTION CTA BUTTONS (Unduh Resume Medis & Bagikan)
            // =========================================================================
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Unduh Resume Medis (PDF) (Solid Brand Rose)
                Button(
                    onClick = { /* Download PDF Handler */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Unduh Resume Medis (PDF)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Bagikan ke Bidan / Dokter Anak (White Card)
                Button(
                    onClick = { /* Share Handler */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.cardBackground,
                        contentColor = colors.textPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Bagikan ke Bidan / Dokter Anak",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = colors.textPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// =============================================================================
// SUB-COMPONENTS
// =============================================================================

@Composable
private fun FilterPillButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = KanguruTheme.colors

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) colors.primary else colors.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = title,
            style = KanguruTheme.typography.labelSmall,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) colors.onPrimary else colors.textSecondary
        )
    }
}

@Composable
private fun KpiGridCard(
    title: String,
    value: String,
    subtitle: String,
    subtitleColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    iconBg: Color,
    progress: Float,
    progressBarColor: Color,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = KanguruTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textSecondary
                )

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
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
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontFamily = FontFamily.Monospace,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = colors.textPrimary
            )

            Text(
                text = subtitle,
                style = KanguruTheme.typography.labelSmall,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = subtitleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(colors.cardBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .clip(RoundedCornerShape(3.dp))
                        .background(progressBarColor)
                )
            }
        }
    }
}

data class ChartDayItem(
    val dayKey: String,
    val dayLabel: String,
    val durationHours: Float,
    val durationText: String,
    val note: String,
    val isPeak: Boolean
)

@Composable
private fun InteractiveSevenDayTrendCard(
    dailyDurations: List<DailyDuration>,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    // 7 Days fallback items matching Stitch
    val chartDays = remember(dailyDurations) {
        if (dailyDurations.any { it.durationHours > 0f }) {
            dailyDurations.mapIndexed { index, d ->
                val hours = d.durationHours
                val h = hours.toInt()
                val m = ((hours - h) * 60).toInt()
                ChartDayItem(
                    dayKey = d.dayEpoch.toString(),
                    dayLabel = d.dayLabel,
                    durationHours = hours,
                    durationText = "${h} Jam ${m} Menit",
                    note = if (hours >= 20f) "Pencapaian rekor terbaik estafet!" else if (hours >= 18f) "Target WHO terpenuhi optimal" else "Belum capai target (evaluasi rehat)",
                    isPeak = hours >= 20.2f || (index == 2)
                )
            }
        } else {
            listOf(
                ChartDayItem("1", "Sen", 14.5f, "14 Jam 30 Menit", "Belum capai target (kontrol nakes)", false),
                ChartDayItem("2", "Sel", 19.2f, "19 Jam 12 Menit", "Target tercapai optimal!", false),
                ChartDayItem("3", "Rab", 20.2f, "20 Jam 12 Menit", "Pencapaian rekor terbaik estafet!", true),
                ChartDayItem("4", "Kam", 13.8f, "13 Jam 48 Menit", "Belum capai target (evaluasi rehat)", false),
                ChartDayItem("5", "Jum", 18.5f, "18 Jam 30 Menit", "Target WHO terpenuhi optimal", false),
                ChartDayItem("6", "Sab", 16.2f, "16 Jam 12 Menit", "Belum capai target harian", false),
                ChartDayItem("7", "Min", 19.8f, "19 Jam 48 Menit", "Target WHO terpenuhi optimal", false)
            )
        }
    }

    var selectedDay by remember { mutableStateOf(chartDays.getOrNull(2) ?: chartDays.first()) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tren Durasi PMK 7 Hari",
                        style = KanguruTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Evaluasi kepatuhan harian vs target WHO",
                        style = KanguruTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(width = 10.dp, height = 2.dp)
                                .clip(RoundedCornerShape(1.dp))
                                .background(colors.primary)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Target 18j",
                            style = KanguruTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chart area with 18h dashed line + 7 vertical columns
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                // Dashed Line at 18h (18/24 = 75% height from bottom -> top is 25%)
                val guideLineColor = colors.primary.copy(alpha = 0.5f)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val lineY = size.height * (1f - (18f / 24f))
                    drawLine(
                        color = guideLineColor,
                        start = Offset(0f, lineY),
                        end = Offset(size.width, lineY),
                        strokeWidth = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                    )
                }

                // 18.0 Jam Label
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 18.dp)
                        .background(colors.cardBackground)
                        .padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = "18.0 Jam",
                        style = KanguruTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                }

                // 7 Vertical Columns
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    chartDays.forEach { item ->
                        val isSelected = selectedDay.dayKey == item.dayKey
                        val isTargetMet = item.durationHours >= 18f
                        val heightFraction = (item.durationHours / 24f).coerceIn(0.1f, 1f)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedDay = item }
                                .padding(horizontal = 2.dp)
                        ) {
                            // Value text
                            Text(
                                text = String.format("%.1f", item.durationHours),
                                style = KanguruTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                fontWeight = if (isTargetMet) FontWeight.Bold else FontWeight.Medium,
                                color = if (isTargetMet) colors.primary else colors.textTertiary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Rounded Bar
                            val barBrush = if (isTargetMet) {
                                Brush.verticalGradient(
                                    listOf(colors.primary, colors.primary.copy(alpha = 0.8f))
                                )
                            } else {
                                Brush.verticalGradient(
                                    listOf(colors.primary.copy(alpha = 0.35f), colors.primary.copy(alpha = 0.25f))
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.55f)
                                    .fillMaxHeight(heightFraction * 0.78f)
                                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                    .background(barBrush)
                                    .then(
                                        if (item.isPeak) {
                                            Modifier.border(1.5.dp, colors.primaryBorder, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                        } else Modifier
                                    )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Day Label
                            Text(
                                text = item.dayLabel,
                                style = KanguruTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected || isTargetMet) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected || isTargetMet) colors.primary else colors.textSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Active Day Tooltip Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.primaryContainer)
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${selectedDay.dayLabel} • ",
                            style = KanguruTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = selectedDay.durationText,
                            style = KanguruTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.cardBackground)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (selectedDay.isPeak) "Pencapaian Rekor" else if (selectedDay.durationHours >= 18f) "Target WHO Terpenuhi" else "Perlu Ditingkatkan",
                            style = KanguruTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CaregiverRelayCard(
    distribution: CaregiverDistribution,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Distribusi Estafet Pengasuh",
                        style = KanguruTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Dukungan keluarga menjaga PMK kontinu",
                        style = KanguruTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.primaryContainer)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "3 Pengasuh",
                        style = KanguruTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Multi-segmented Progress Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceVariant)
            ) {
                val ibuPct = distribution.ibuPercent.coerceAtLeast(68)
                val ayahPct = distribution.ayahPercent.coerceAtLeast(22)
                val pendampingPct = (100 - ibuPct - ayahPct).coerceAtLeast(10)

                Box(
                    modifier = Modifier
                        .weight(ibuPct.toFloat())
                        .fillMaxSize()
                        .background(colors.caregiverIbu)
                )
                Box(
                    modifier = Modifier
                        .weight(ayahPct.toFloat())
                        .fillMaxSize()
                        .background(colors.caregiverAyah)
                )
                Box(
                    modifier = Modifier
                        .weight(pendampingPct.toFloat())
                        .fillMaxSize()
                        .background(colors.caregiverPendamping)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3 Rows of Caregiver Details
            CaregiverRowDetail(
                name = "Ibu",
                roleBadge = "Utama",
                badgeBg = colors.primaryContainer,
                badgeColor = colors.primary,
                duration = "88j 50m",
                pct = "68%",
                dotColor = colors.caregiverIbu
            )

            Spacer(modifier = Modifier.height(8.dp))

            CaregiverRowDetail(
                name = "Ayah",
                roleBadge = "Estafet Malam",
                badgeBg = colors.caregiverAyahContainer,
                badgeColor = colors.caregiverAyahText,
                duration = "28j 45m",
                pct = "22%",
                dotColor = colors.caregiverAyah
            )

            Spacer(modifier = Modifier.height(8.dp))

            CaregiverRowDetail(
                name = "Pendamping",
                roleBadge = "Siang",
                badgeBg = colors.caregiverPendampingContainer,
                badgeColor = colors.caregiverPendampingText,
                duration = "13j 05m",
                pct = "10%",
                dotColor = colors.caregiverPendamping
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Educational Clinical Insight Note
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.warningContainer)
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = colors.warning,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(top = 1.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Keterlibatan Ayah dan Pendamping mengurangi beban kelelahan Ibu hingga 40% serta menjaga kontak kulit (skin-to-skin) bayi tetap kontinu 24 jam.",
                        style = KanguruTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = colors.onWarningContainer,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CaregiverRowDetail(
    name: String,
    roleBadge: String,
    badgeBg: Color,
    badgeColor: Color,
    duration: String,
    pct: String,
    dotColor: Color
) {
    val colors = KanguruTheme.colors

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = name,
                style = KanguruTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeBg)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = roleBadge,
                    style = KanguruTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = badgeColor
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = duration,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = pct,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                color = dotColor
            )
        }
    }
}

@Composable
private fun ClinicalImpactCard(
    statistics: PmkStatistics,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Evaluasi Dampak Klinis",
                        style = KanguruTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Kestabilan fisiologis selama PMK berlangsung",
                        style = KanguruTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(colors.successContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Thermostat,
                        contentDescription = null,
                        tint = colors.success,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2 Metric Boxes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Suhu Rata-rata
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.surfaceVariant)
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Thermostat,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Suhu Rata-rata",
                                style = KanguruTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = colors.textSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "36.8°C",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Normal",
                                style = KanguruTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.success
                            )
                        }
                        Text(
                            text = "Aman dari hipotermia",
                            style = KanguruTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = colors.textTertiary
                        )
                    }
                }

                // Zona Aman Suhu
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.surfaceVariant)
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = colors.success,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Zona Aman Suhu",
                                style = KanguruTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = colors.textSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "100%",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = colors.success
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Sesi",
                                style = KanguruTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textSecondary
                            )
                        }
                        Text(
                            text = "Rentang 36.5°C - 37.5°C",
                            style = KanguruTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = colors.textTertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Respon Perilaku Bayi
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Respon Perilaku Bayi",
                        style = KanguruTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "88% Tenang / Tidur",
                        style = KanguruTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Progress Bar (88% Tenang, 12% Menyusu Aktif)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(colors.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(0.88f)
                            .fillMaxSize()
                            .background(colors.success)
                    )
                    Box(
                        modifier = Modifier
                            .weight(0.12f)
                            .fillMaxSize()
                            .background(colors.caregiverAyah)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(colors.success)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "88% Tenang & Tidur Pulas",
                            style = KanguruTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = colors.textSecondary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(colors.caregiverAyah)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "12% Menyusu Aktif",
                            style = KanguruTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }
    }
}
