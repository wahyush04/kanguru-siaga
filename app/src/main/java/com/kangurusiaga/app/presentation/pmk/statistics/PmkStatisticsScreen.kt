package com.kangurusiaga.app.presentation.pmk.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
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
import com.kangurusiaga.app.domain.model.BabyResponseBreakdown
import com.kangurusiaga.app.domain.model.CaregiverDistribution
import com.kangurusiaga.app.domain.model.DailyDuration
import com.kangurusiaga.app.domain.model.PmkStatistics
import com.kangurusiaga.app.domain.model.StatsPeriod
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PmkStatisticsRoute(
    onNavigateBack: () -> Unit,
    onNavigateToManualLog: () -> Unit,
    onNavigateToWeeklyRecap: () -> Unit = {},
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
        onNavigateToWeeklyRecap = onNavigateToWeeklyRecap,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
fun PmkStatisticsScreen(
    uiState: PmkStatisticsUiState,
    onNavigateBack: () -> Unit,
    onPeriodSelected: (StatsPeriod) -> Unit,
    onNavigateToWeeklyRecap: () -> Unit = {},
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
                                    style = KanguruTheme.typography.titleLarge
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                val babyName = uiState.baby?.name ?: "Bayi"
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = babyName,
                                        style = KanguruTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                    val birthWeight = uiState.baby?.birthWeightGram
                                    if (birthWeight != null && birthWeight > 0) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(colors.primaryContainer)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = if (birthWeight < 2500) "BBLR ${birthWeight}g" else "${birthWeight}g",
                                                style = KanguruTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = colors.primary
                                            )
                                        }
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
                                    val dateText = if (stats.dateRangeText.isNotBlank()) {
                                        if (stats.period == StatsPeriod.THIS_WEEK || stats.period == StatsPeriod.LAST_WEEK) {
                                            "${stats.dateRangeText} (Minggu ke-${stats.babyChronologicalWeek})"
                                        } else {
                                            stats.dateRangeText
                                        }
                                    } else {
                                        uiState.selectedPeriod.title
                                    }
                                    Text(
                                        text = dateText,
                                        style = KanguruTheme.typography.labelSmall,
                                        color = colors.textSecondary
                                    )
                                }
                            }
                        }

                        // Star badge status
                        val badgeText = stats.complianceBadge.ifBlank {
                            if (stats.compliancePercentage >= 80) "Sangat Baik"
                            else if (stats.compliancePercentage >= 50) "Cukup"
                            else if (stats.totalDurationMinutes > 0) "Perlu Ditingkatkan"
                            else "Belum Ada"
                        }
                        val isBadgeGood = stats.compliancePercentage >= 80 || stats.complianceBadge == "Sangat Baik"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isBadgeGood) colors.successContainer else colors.surfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (isBadgeGood) colors.success else colors.primary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = badgeText,
                                    style = KanguruTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isBadgeGood) colors.onSuccessContainer else colors.textPrimary
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
                                    color = colors.textSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Button Akses Rekap Mingguan PMK (Sesuai Google Stitch)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(colors.primaryContainer.copy(alpha = 0.5f))
                            .border(1.dp, colors.primaryBorder.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                            .clickable { onNavigateToWeeklyRecap() }
                            .padding(horizontal = 10.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(colors.cardBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarViewWeek,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Rekap Mingguan PMK",
                                    style = KanguruTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = "Lihat riwayat perbandingan mingguan",
                                    style = KanguruTheme.typography.labelSmall,
                                    color = colors.textSecondary
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            Text(
                                text = "Buka",
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.primary
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // =========================================================================
            // 3 KEY PERFORMANCE INDICATORS (ROW 3-KOLOM SESUAI GOOGLE STITCH)
            // =========================================================================
            SummaryThreeCardsRow(
                stats = stats,
                selectedPeriod = uiState.selectedPeriod,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // =========================================================================
            // DAILY TREND INTERACTIVE BAR CHART (7 HARI - TARGET MIN. 8J)
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
            // RESPON BAYI SELAMA PMK (OBSERVASI KENYAMANAN 4 KATEGORI)
            // =========================================================================
            BabyResponseBreakdownCard(
                breakdown = stats.babyResponseBreakdown,
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
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = KanguruTheme.colors.onPrimary
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
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
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
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) colors.onPrimary else colors.textSecondary
        )
    }
}

@Composable
private fun SummaryThreeCardsRow(
    stats: PmkStatistics,
    selectedPeriod: StatsPeriod,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    Row(
        modifier = modifier.height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Card 1: Capaian PMK Selama 7 hari
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(colors.successContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = colors.success,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Capaian PMK\n${selectedPeriod.title}",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = colors.textSecondary
                    )
                }

                Column(modifier = Modifier.padding(top = 6.dp)) {
                    Text(
                        text = "${stats.targetAchievedDays} hari = sesuai target",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.success
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${stats.targetUnmetDays} hari = belum target",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = colors.warning
                    )
                }
            }
        }

        // Card 2: Total Minggu Ini
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(colors.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Total ${selectedPeriod.title}",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = colors.textSecondary,
                        minLines = 2,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column(modifier = Modifier.padding(top = 6.dp)) {
                    Text(
                        text = "${stats.totalDurationMinutes / 60} Jam",
                        style = KanguruTheme.typography.titleSmall,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${stats.consecutiveDays} hari berturut",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = colors.textSecondary
                    )
                }
            }
        }

        // Card 3: Target Mingguan
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                val isMet = stats.isWeeklyTargetMet
                Column {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (isMet) colors.successContainer else colors.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isMet) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (isMet) colors.success else colors.primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Target Mingguan",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = colors.textSecondary,
                        minLines = 2,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column(modifier = Modifier.padding(top = 6.dp)) {
                    Text(
                        text = if (isMet) "Tercapai" else "Tidak Tercapai",
                        style = KanguruTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isMet) "Sesuai Target" else "Belum Tercapai",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = if (isMet) colors.success else colors.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
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

    // 7 Days real items from dailyDurations
    val chartDays = remember(dailyDurations) {
        val daysList = if (dailyDurations.isNotEmpty()) {
            dailyDurations
        } else {
            listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min").mapIndexed { index, label ->
                DailyDuration(
                    dayLabel = label,
                    durationMinutes = 0,
                    isAboveTarget = false,
                    dayEpoch = index.toLong()
                )
            }
        }
        val maxDuration = daysList.maxOfOrNull { it.durationHours } ?: 0f
        daysList.map { d ->
            val hours = d.durationHours
            val h = d.durationMinutes / 60
            val m = d.durationMinutes % 60
            ChartDayItem(
                dayKey = d.dayEpoch.toString(),
                dayLabel = d.dayLabel,
                durationHours = hours,
                durationText = if (d.durationMinutes > 0) "${h} Jam ${m} Menit" else "0 Jam 0 Menit",
                note = if (hours >= 10f) "Capaian Tertinggi / Rekor!"
                       else if (hours >= 8f) "Target minimal tercapai!"
                       else if (hours > 0f) "Belum capai target (evaluasi rehat)"
                       else "Belum ada catatan PMK pada hari ini",
                isPeak = maxDuration > 0f && hours == maxDuration
            )
        }
    }

    var selectedDay by remember(chartDays) {
        mutableStateOf(chartDays.maxByOrNull { it.durationHours } ?: chartDays.first())
    }

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
                        text = "Evaluasi kepatuhan harian vs target minimal",
                        style = KanguruTheme.typography.labelSmall,
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
                            text = "Target Min. 8j",
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chart area with 8h dashed line + 7 vertical columns
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                // Dashed Line at 8h (8/24 = 33.3% height from bottom -> top is 66.7%)
                val guideLineColor = colors.primary.copy(alpha = 0.5f)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val lineY = size.height * (1f - (8f / 24f))
                    drawLine(
                        color = guideLineColor,
                        start = Offset(0f, lineY),
                        end = Offset(size.width, lineY),
                        strokeWidth = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                    )
                }

                // Target 8.0j Label
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(bottom = 24.dp)
                        .background(colors.cardBackground)
                        .padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = "Target 8.0j",
                        style = KanguruTheme.typography.labelSmall,
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
                        val isTargetMet = item.durationHours >= 8f
                        val heightFraction = if (item.durationHours > 0f) {
                            (item.durationHours / 24f).coerceIn(0.04f, 1f)
                        } else {
                            0.02f
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedDay = item }
                                .padding(horizontal = 2.dp)
                        ) {
                            // Value text
                            Text(
                                text = String.format(Locale.US, "%.1f", item.durationHours),
                                style = KanguruTheme.typography.labelSmall,
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
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = selectedDay.durationText,
                            style = KanguruTheme.typography.labelSmall,
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
                            text = if (selectedDay.durationHours == 0f) "Belum Ada Sesi"
                                   else if (selectedDay.isPeak) "Capaian Tertinggi"
                                   else if (selectedDay.durationHours >= 8f) "Target Terpenuhi"
                                   else "Belum Capai Target",
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedDay.durationHours >= 8f) colors.primary else colors.textSecondary
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
                        text = "${distribution.activeCaregiverCount} Pengasuh",
                        style = KanguruTheme.typography.labelSmall,
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
                if (distribution.totalMinutes > 0) {
                    if (distribution.ibuPercent > 0) {
                        Box(
                            modifier = Modifier
                                .weight(distribution.ibuPercent.toFloat())
                                .fillMaxSize()
                                .background(colors.caregiverIbu)
                        )
                    }
                    if (distribution.ayahPercent > 0) {
                        Box(
                            modifier = Modifier
                                .weight(distribution.ayahPercent.toFloat())
                                .fillMaxSize()
                                .background(colors.caregiverAyah)
                        )
                    }
                    if (distribution.pendampingPercent > 0) {
                        Box(
                            modifier = Modifier
                                .weight(distribution.pendampingPercent.toFloat())
                                .fillMaxSize()
                                .background(colors.caregiverPendamping)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3 Rows of Caregiver Details
            CaregiverRowDetail(
                name = "Ibu",
                roleBadge = "Utama",
                badgeBg = colors.primaryContainer,
                badgeColor = colors.primary,
                duration = distribution.ibuHoursFormatted,
                pct = "${distribution.ibuPercent}%",
                dotColor = colors.caregiverIbu
            )

            Spacer(modifier = Modifier.height(8.dp))

            CaregiverRowDetail(
                name = "Ayah",
                roleBadge = "Estafet Malam",
                badgeBg = colors.caregiverAyahContainer,
                badgeColor = colors.caregiverAyahText,
                duration = distribution.ayahHoursFormatted,
                pct = "${distribution.ayahPercent}%",
                dotColor = colors.caregiverAyah
            )

            Spacer(modifier = Modifier.height(8.dp))

            CaregiverRowDetail(
                name = "Pendamping",
                roleBadge = "Siang",
                badgeBg = colors.caregiverPendampingContainer,
                badgeColor = colors.caregiverPendampingText,
                duration = distribution.pendampingHoursFormatted,
                pct = "${distribution.pendampingPercent}%",
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
                        color = colors.onWarningContainer
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
                    fontWeight = FontWeight.Medium,
                    color = badgeColor
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = duration,
                style = KanguruTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = pct,
                style = KanguruTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                color = dotColor
            )
        }
    }
}

@Composable
private fun BabyResponseBreakdownCard(
    breakdown: BabyResponseBreakdown,
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
            // Header Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Respon Bayi Selama PMK",
                        style = KanguruTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Observasi kenyamanan, perilaku, dan stabilitas selama kontak kulit",
                        style = KanguruTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                if (breakdown.totalSessions > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.successContainer)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${breakdown.overallCalmPercentage}% Sangat Tenang",
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSuccessContainer
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.surfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Belum Ada Catatan",
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = colors.textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stacked Multi-Color Progress Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceVariant)
            ) {
                if (breakdown.totalSessions > 0) {
                    if (breakdown.deepSleepPercent > 0) {
                        Box(
                            modifier = Modifier
                                .weight(breakdown.deepSleepPercent.toFloat())
                                .fillMaxHeight()
                                .background(colors.success)
                        )
                    }
                    if (breakdown.activeFeedingPercent > 0) {
                        Box(
                            modifier = Modifier
                                .weight(breakdown.activeFeedingPercent.toFloat())
                                .fillMaxHeight()
                                .background(colors.primary)
                        )
                    }
                    if (breakdown.alertCalmPercent > 0) {
                        Box(
                            modifier = Modifier
                                .weight(breakdown.alertCalmPercent.toFloat())
                                .fillMaxHeight()
                                .background(colors.caregiverAyah)
                        )
                    }
                    if (breakdown.adjustingPercent > 0) {
                        Box(
                            modifier = Modifier
                                .weight(breakdown.adjustingPercent.toFloat())
                                .fillMaxHeight()
                                .background(colors.warning)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Rincian Kategori Respon (Grid 2 kolom)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResponseCategoryItem(
                    title = "Tidur Pulas",
                    percent = "${breakdown.deepSleepPercent}%",
                    sessionCount = "${breakdown.deepSleepSessions} sesi tercatat",
                    description = "Irama napas teratur, tubuh rileks di dekapan dada.",
                    indicatorColor = colors.success,
                    percentColor = colors.success,
                    modifier = Modifier.weight(1f)
                )
                ResponseCategoryItem(
                    title = "Menyusu Aktif",
                    percent = "${breakdown.activeFeedingPercent}%",
                    sessionCount = "${breakdown.activeFeedingSessions} sesi tercatat",
                    description = "Refleks hisap kuat, menyusu baik tanpa tersedak.",
                    indicatorColor = colors.primary,
                    percentColor = colors.primary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResponseCategoryItem(
                    title = "Tenang Waspada",
                    percent = "${breakdown.alertCalmPercent}%",
                    sessionCount = "${breakdown.alertCalmSessions} sesi tercatat",
                    description = "Kontak mata hangat, mendengar detak jantung.",
                    indicatorColor = colors.caregiverAyah,
                    percentColor = colors.caregiverAyahText,
                    modifier = Modifier.weight(1f)
                )
                ResponseCategoryItem(
                    title = "Penyesuaian",
                    percent = "${breakdown.adjustingPercent}%",
                    sessionCount = "${breakdown.adjustingSessions} sesi tercatat",
                    description = "Mudah tenang kembali setelah posisi disesuaikan.",
                    indicatorColor = colors.warning,
                    percentColor = colors.warning,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Kotak Edukasi Klinis
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.warningContainer)
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = colors.warning,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(top = 1.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Kontak kulit langsung (PMK) terbukti menurunkan stres bayi prematur, menstabilkan saturasi oksigen, serta mempercepat fase tidur lelap (deep sleep) yang krusial untuk perkembangan otak.",
                        style = KanguruTheme.typography.bodySmall,
                        color = colors.onWarningContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun ResponseCategoryItem(
    title: String,
    percent: String,
    sessionCount: String,
    description: String,
    indicatorColor: Color,
    percentColor: Color,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surfaceVariant)
            .padding(10.dp)
    ) {
        Column {
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
                            .background(indicatorColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title,
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }

                Text(
                    text = percent,
                    style = KanguruTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    color = percentColor
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = sessionCount,
                style = KanguruTheme.typography.labelSmall,
                color = colors.textTertiary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                style = KanguruTheme.typography.labelSmall,
                color = colors.textSecondary
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
    val eval = statistics.clinicalEvaluation
    val hasTempData = eval.averageTemperature > 0.0
    val isTempNormal = hasTempData && eval.averageTemperature in 36.5..37.5

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
                                color = colors.textSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = if (hasTempData) String.format(Locale.US, "%.1f°C", eval.averageTemperature) else "-°C",
                                style = KanguruTheme.typography.titleMedium,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (!hasTempData) "-" else if (isTempNormal) "Normal" else if (eval.averageTemperature < 36.5) "Rendah" else "Tinggi",
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (!hasTempData) colors.textSecondary else if (isTempNormal) colors.success else colors.warning
                            )
                        }
                        Text(
                            text = if (hasTempData) {
                                if (isTempNormal) "Aman dari hipotermia" else "Perhatikan suhu bayi"
                            } else {
                                "Belum ada data suhu"
                            },
                            style = KanguruTheme.typography.labelSmall,
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
                                color = colors.textSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = if (hasTempData) "${eval.normalTemperaturePercentage}%" else "-",
                                style = KanguruTheme.typography.titleMedium,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                color = if (hasTempData && eval.normalTemperaturePercentage >= 80) colors.success else colors.textPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Sesi",
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.textSecondary
                            )
                        }
                        Text(
                            text = if (hasTempData) "Rentang 36.5°C - 37.5°C" else "Belum ada rekaman suhu",
                            style = KanguruTheme.typography.labelSmall,
                            color = colors.textTertiary
                        )
                    }
                }
            }
        }
    }
}

