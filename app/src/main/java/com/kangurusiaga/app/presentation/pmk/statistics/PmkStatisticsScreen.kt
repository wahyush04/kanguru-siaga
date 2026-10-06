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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WbSunny
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.BrandBackground
import com.kangurusiaga.app.core.designsystem.theme.BrandLightPink
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.CardBorder
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.core.designsystem.theme.TextPrimary
import com.kangurusiaga.app.core.designsystem.theme.TextSecondary
import com.kangurusiaga.app.core.designsystem.theme.TextTertiary
import com.kangurusiaga.app.core.designsystem.theme.White
import com.kangurusiaga.app.domain.model.DailyDuration
import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.model.PmkStatistics
import com.kangurusiaga.app.domain.model.StatsPeriod
import com.kangurusiaga.app.presentation.home.HomeBottomBar
import com.kangurusiaga.app.presentation.home.HomeTab
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        onPeriodSelected = viewModel::onPeriodSelected,
        onNavigateToManualLog = onNavigateToManualLog,
        onDeleteSession = viewModel::deleteSession,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PmkStatisticsScreen(
    uiState: PmkStatisticsUiState,
    onNavigateBack: () -> Unit,
    onPeriodSelected: (StatsPeriod) -> Unit,
    onNavigateToManualLog: () -> Unit,
    onDeleteSession: (Long) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(
                color = BrandBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back),
                            tint = TextPrimary
                        )
                    }

                    Text(
                        text = stringResource(R.string.pmk_stats_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    IconButton(onClick = { /* Unduh laporan ringkasan */ }) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = stringResource(R.string.pmk_stats_cd_download),
                            tint = TextSecondary
                        )
                    }
                }
            }
        },
        bottomBar = {
            HomeBottomBar(
                currentTab = HomeTab.PMK,
                onTabSelected = { tab ->
                    if (tab == HomeTab.BERANDA) {
                        onNavigateBack()
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BrandPink)
            }
        } else {
            val stats = uiState.statistics
            val babyName = uiState.baby?.name ?: "Bayi"

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Period Selector Segmented Buttons
                Surface(
                    color = KanguruTheme.colors.surfaceVariant,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatsPeriod.entries.forEach { period ->
                            val isSelected = period == uiState.selectedPeriod
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) BrandPink else Color.Transparent)
                                    .clickable { onPeriodSelected(period) }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = period.title,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) White else TextSecondary
                                )
                            }
                        }
                    }
                }

                // Date Range & Verified Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = BrandPink,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stats.dateRangeText,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary,
                            fontSize = 11.5.sp
                        )
                    }

                    Surface(
                        color = KanguruTheme.colors.successContainer,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, KanguruTheme.colors.successBorder)
                    ) {
                        Text(
                            text = stringResource(R.string.pmk_stats_verified_badge),
                            color = KanguruTheme.colors.onSuccessContainer,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                // 2. Compliance Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorder, RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(containerColor = KanguruTheme.colors.surface),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.pmk_stats_compliance_title),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${stats.compliancePercentage}%",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Surface(
                                        color = if (stats.compliancePercentage >= 80) KanguruTheme.colors.success else KanguruTheme.colors.warning,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = stats.complianceBadge,
                                            color = White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(BrandPink.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = BrandPink,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Progress Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stats.totalDurationFormatted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = stats.targetDurationFormatted,
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { (stats.compliancePercentage / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = BrandPink,
                            trackColor = KanguruTheme.colors.surfaceVariant,
                            strokeCap = StrokeCap.Round
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Motivational Tip
                        Surface(
                            color = KanguruTheme.colors.surfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SentimentVerySatisfied,
                                    contentDescription = null,
                                    tint = BrandPink,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stats.motivationalTip,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary,
                                    fontSize = 11.5.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                // 3. Key Metrics Grid (2x2)
                val isDark = KanguruTheme.colors.isDark
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = stringResource(R.string.pmk_stats_total_duration),
                        value = "${stats.totalDurationMinutes / 60}j ${stats.totalDurationMinutes % 60}m",
                        badge = "Optimal",
                        badgeBg = if (isDark) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFFECFDF5),
                        badgeColor = if (isDark) Color(0xFF34D399) else Color(0xFF059669),
                        modifier = Modifier.weight(1f)
                    )

                    MetricCard(
                        title = stringResource(R.string.pmk_stats_avg_session),
                        value = "${stats.averageMinutesPerSession} Menit",
                        badge = "Min 60m",
                        badgeBg = if (isDark) Color(0xFFF59E0B).copy(alpha = 0.2f) else Color(0xFFFFF7ED),
                        badgeColor = if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = stringResource(R.string.pmk_stats_completed_sessions),
                        value = "${stats.totalSessions} Sesi",
                        badge = "Tuntas",
                        badgeBg = if (isDark) Color(0xFF8B5CF6).copy(alpha = 0.2f) else Color(0xFFF3E8FF),
                        badgeColor = if (isDark) Color(0xFFA78BFA) else Color(0xFF7E22CE),
                        modifier = Modifier.weight(1f)
                    )

                    MetricCard(
                        title = stringResource(R.string.pmk_stats_longest_session),
                        value = "${stats.longestSessionMinutes} Menit",
                        badge = "Terbaik",
                        badgeBg = if (isDark) Color(0xFFF59E0B).copy(alpha = 0.2f) else Color(0xFFFEF3C7),
                        badgeColor = if (isDark) Color(0xFFFDE68A) else Color(0xFFB45309),
                        modifier = Modifier.weight(1f)
                    )
                }

                // 4. Daily PMK Bar Chart
                DailyBarChartSection(dailyDurations = stats.dailyDurations)

                // 5. Time Distribution Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorder, RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(containerColor = KanguruTheme.colors.surface),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.pmk_stats_distribution_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Text(
                                text = stringResource(R.string.pmk_stats_distribution_most_productive, stats.timeDistribution.mostProductiveTime),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandPink
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Pagi
                        TimeDistributionItem(
                            label = stringResource(R.string.pmk_stats_time_morning),
                            count = stats.timeDistribution.morningCount,
                            percent = stats.timeDistribution.morningPercent,
                            fillColor = Color(0xFFFBBF24)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Siang
                        TimeDistributionItem(
                            label = stringResource(R.string.pmk_stats_time_afternoon),
                            count = stats.timeDistribution.afternoonCount,
                            percent = stats.timeDistribution.afternoonPercent,
                            fillColor = BrandPink
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Malam
                        TimeDistributionItem(
                            label = stringResource(R.string.pmk_stats_time_evening),
                            count = stats.timeDistribution.eveningCount,
                            percent = stats.timeDistribution.eveningPercent,
                            fillColor = Color(0xFF818CF8)
                        )
                    }
                }

                // 6. Observasi Klinis Bayi
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorder, RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(containerColor = KanguruTheme.colors.surface),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BrandPink.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🩺", fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = stringResource(R.string.pmk_stats_clinical_title, babyName),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                color = KanguruTheme.colors.surfaceVariant,
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = stringResource(R.string.pmk_stats_avg_temp),
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = "${stats.averageTemperature} °C",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Surface(
                                        color = KanguruTheme.colors.successContainer,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.pmk_stats_temp_status),
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = KanguruTheme.colors.onSuccessContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Surface(
                                color = KanguruTheme.colors.surfaceVariant,
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = stringResource(R.string.pmk_stats_calmness),
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = "${stats.calmnessPercentage}%",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Surface(
                                        color = KanguruTheme.colors.errorContainer,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.pmk_stats_calmness_status),
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = KanguruTheme.colors.onErrorContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = BrandPink,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stats.clinicalNote,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // 7. Action Buttons
                Button(
                    onClick = onNavigateToManualLog,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPink),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(imageVector = Icons.Default.AddCircle, contentDescription = null, tint = White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.pmk_stats_btn_add_new), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = White)
                }

                OutlinedButton(
                    onClick = {
                        // Inform user that PDF export is not in this phase
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = BrandPink,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.pmk_stats_btn_download_pdf), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    badge: String,
    badgeBg: Color,
    badgeColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(1.dp, CardBorder, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = KanguruTheme.colors.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = badge,
                        color = badgeColor,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )

            Text(
                text = title,
                fontSize = 11.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun DailyBarChartSection(
    dailyDurations: List<DailyDuration>
) {
    val isDark = KanguruTheme.colors.isDark
    val standardBarColor = if (isDark) BrandPink.copy(alpha = 0.35f) else Color(0xFFFFCCD5)
    val dashedLineColor = BrandPink.copy(alpha = 0.4f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = KanguruTheme.colors.surface),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Durasi Harian (Menit)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Garis putus-putus: Target 60m",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(BrandPink)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "≥ Target", fontSize = 10.sp, color = TextSecondary)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(standardBarColor)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Standar", fontSize = 10.sp, color = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Bars Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                val maxMinutes = (dailyDurations.maxOfOrNull { it.durationMinutes } ?: 120).coerceAtLeast(100)

                // Dashed Target Line at 60 minutes
                val targetFraction = (60f / maxMinutes.toFloat()).coerceIn(0f, 1f)

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val lineY = size.height * (1f - targetFraction)
                    drawLine(
                        color = dashedLineColor,
                        start = Offset(0f, lineY),
                        end = Offset(size.width, lineY),
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                    )
                }

                // 7 Bar columns
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Bottom
                ) {
                    dailyDurations.forEach { day ->
                        val barFraction = (day.durationMinutes.toFloat() / maxMinutes.toFloat()).coerceIn(0.05f, 1f)
                        val barColor = if (day.isAboveTarget) BrandPink else standardBarColor

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Text(
                                text = if (day.durationMinutes > 0) "${day.durationMinutes}" else "-",
                                fontSize = 10.sp,
                                fontWeight = if (day.isAboveTarget) FontWeight.Bold else FontWeight.Medium,
                                color = if (day.isAboveTarget) BrandPink else TextSecondary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Box(
                                modifier = Modifier
                                    .width(26.dp)
                                    .fillMaxHeight(barFraction * 0.78f)
                                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                    .background(barColor)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = day.dayLabel,
                                fontSize = 10.5.sp,
                                fontWeight = if (day.isAboveTarget) FontWeight.Bold else FontWeight.Normal,
                                color = if (day.isAboveTarget) BrandPink else TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeDistributionItem(
    label: String,
    count: Int,
    percent: Int,
    fillColor: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )
            Text(
                text = "$count Sesi ($percent%)",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (percent / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = fillColor,
            trackColor = KanguruTheme.colors.surfaceVariant,
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
private fun SessionHistoryItem(
    session: PmkSession,
    onDelete: () -> Unit
) {
    val sdfDate = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    val formattedTime = sdfDate.format(Date(session.startTimeEpoch))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = KanguruTheme.colors.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${session.durationMinutes} Menit",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = if (session.source.name == "TIMER") BrandLightPink else KanguruTheme.colors.surfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = session.source.name,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (session.source.name == "TIMER") BrandPink else TextSecondary,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = formattedTime,
                    fontSize = 11.sp,
                    color = TextTertiary
                )

                if (session.babyTemperature != null || session.babyResponse != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    val details = buildString {
                        if (session.babyTemperature != null) append("Suhu: ${session.babyTemperature}°C  ")
                        if (session.babyResponse != null) append("Respon: ${session.babyResponse}")
                    }
                    Text(
                        text = details,
                        fontSize = 10.5.sp,
                        color = TextSecondary
                    )
                }

                if (!session.notes.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "\"${session.notes}\"",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Hapus",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
