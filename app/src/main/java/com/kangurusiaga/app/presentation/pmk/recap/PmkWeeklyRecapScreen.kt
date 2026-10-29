package com.kangurusiaga.app.presentation.pmk.recap

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.domain.model.ClinicalResponseRow
import com.kangurusiaga.app.domain.model.CorrelationChartPoint
import com.kangurusiaga.app.domain.model.DailyActivityBar
import com.kangurusiaga.app.domain.model.FentonMetricCardData
import com.kangurusiaga.app.domain.model.FentonStatusType
import com.kangurusiaga.app.domain.model.FentonTriParameterItem
import com.kangurusiaga.app.domain.model.GrowthCorrelationData
import com.kangurusiaga.app.domain.model.WeeklyFentonSummary
import com.kangurusiaga.app.domain.model.WeeklyPmkEvaluation
import com.kangurusiaga.app.domain.model.WeeklyRecapData
import com.kangurusiaga.app.domain.model.WeeklyRecapTab

@Composable
fun PmkWeeklyRecapRoute(
    onNavigateBack: () -> Unit,
    onNavigateToFentonChart: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PmkWeeklyRecapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PmkWeeklyRecapScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onTabSelected = { viewModel.onTabSelected(it) },
        onNavigateToFentonChart = onNavigateToFentonChart,
        modifier = modifier
    )
}

@Composable
fun PmkWeeklyRecapScreen(
    uiState: PmkWeeklyRecapUiState,
    onNavigateBack: () -> Unit,
    onTabSelected: (WeeklyRecapTab) -> Unit,
    onNavigateToFentonChart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography
    val data = uiState.data
    val selectedTab = uiState.selectedTab

    val screenTitle = if (selectedTab == WeeklyRecapTab.REKAP_MINGGUAN) {
        "Rekap Mingguan PMK"
    } else {
        "Detail Rekap Mingguan"
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = {
            Surface(
                color = colors.background,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back),
                            tint = colors.textPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Text(
                        text = screenTitle,
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )

                    IconButton(
                        onClick = { /* Filter tanggal / Kalender */ },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Kalender",
                            tint = colors.textSecondary,
                            modifier = Modifier.size(20.dp)
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
                .padding(horizontal = 20.dp, vertical = 4.dp)
        ) {
            // =========================================================================
            // 1. EVALUATION HEADER & BABY PROFILE CARD
            // =========================================================================
            EvaluationHeaderBanner(
                selectedTab = selectedTab,
                pmaWeeksText = data.pmaWeeksText
            )

            Spacer(modifier = Modifier.height(10.dp))

            BabyProfileSummaryCard(
                data = data,
                selectedTab = selectedTab
            )

            Spacer(modifier = Modifier.height(12.dp))

            // =========================================================================
            // 2. SEGMENTED TAB SWITCHER (Rekap Mingguan PMK vs Korelasi Pertumbuhan)
            // =========================================================================
            WeeklyRecapSegmentedTabs(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )

            Spacer(modifier = Modifier.height(14.dp))

            // =========================================================================
            // 3. TAB CONTENT
            // =========================================================================
            if (selectedTab == WeeklyRecapTab.REKAP_MINGGUAN) {
                WeeklyRecapTabContent(
                    evaluations = data.weeklyEvaluations,
                    onNavigateToFentonChart = onNavigateToFentonChart
                )
            } else {
                GrowthCorrelationTabContent(
                    data = data.correlationData,
                    pmaWeeksText = data.pmaWeeksText
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

// =====================================================================================
// SUB-COMPONENTS: HEADER & PROFILE
// =====================================================================================

@Composable
private fun EvaluationHeaderBanner(
    selectedTab: WeeklyRecapTab,
    pmaWeeksText: String
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "EVALUASI PMK & PERTUMBUHAN",
                style = typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colors.primary
            )

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(colors.primaryContainer)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (selectedTab == WeeklyRecapTab.REKAP_MINGGUAN) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(colors.primary)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = "PMA $pmaWeeksText",
                        style = typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        val subtitle = if (selectedTab == WeeklyRecapTab.REKAP_MINGGUAN) {
            "Pemantauan Mingguan BBLR • Data Target"
        } else {
            "Pemantauan Mingguan BBLR • Korelasi Klinis"
        }

        Text(
            text = subtitle,
            style = typography.labelSmall,
            color = colors.textSecondary
        )
    }
}

@Composable
private fun BabyProfileSummaryCard(
    data: WeeklyRecapData,
    selectedTab: WeeklyRecapTab
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Identity row with baby photo
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val context = LocalContext.current
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(colors.primaryContainer)
                        .border(1.dp, colors.primaryBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (!data.babyPhotoUri.isNullOrBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(data.babyPhotoUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Foto ${data.babyName}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = "👶",
                            style = typography.titleMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = data.babyName,
                        style = typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Lahir ${formatGrams(data.birthWeightGram)}g ",
                            style = typography.labelSmall,
                            color = colors.textSecondary
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = if (selectedTab == WeeklyRecapTab.KORELASI_PERTUMBUHAN) colors.success else colors.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Kini ${formatGrams(data.currentWeightGram)}g",
                            style = typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == WeeklyRecapTab.KORELASI_PERTUMBUHAN) colors.success else colors.textPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3-Column Key Stats Box (Warm cream background)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.weeklyCardCreamBg)
                    .padding(vertical = 10.dp, horizontal = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    MetricColumnBox(
                        title = "Rata Kontak",
                        value = data.avgContactFormatted,
                        modifier = Modifier.weight(1f)
                    )

                    MetricColumnBox(
                        title = "Kepatuhan",
                        value = data.complianceFormatted,
                        modifier = Modifier.weight(1f),
                        valueColor = if (selectedTab == WeeklyRecapTab.REKAP_MINGGUAN) colors.warning else colors.success
                    )

                    MetricColumnBox(
                        title = "Kenaikan BB",
                        value = data.weightGainFormatted,
                        modifier = Modifier.weight(1f),
                        valueColor = if (selectedTab == WeeklyRecapTab.REKAP_MINGGUAN) colors.success else colors.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricColumnBox(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = KanguruTheme.colors.textPrimary
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = typography.labelSmall,
            color = colors.textSecondary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

// =====================================================================================
// SUB-COMPONENTS: SEGMENTED TABS
// =====================================================================================

@Composable
private fun WeeklyRecapSegmentedTabs(
    selectedTab: WeeklyRecapTab,
    onTabSelected: (WeeklyRecapTab) -> Unit
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            WeeklyRecapTab.entries.forEach { tab ->
                val isSelected = tab == selectedTab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) colors.primary else Color.Transparent)
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val iconVector = if (tab == WeeklyRecapTab.REKAP_MINGGUAN) {
                            Icons.Default.CalendarMonth
                        } else {
                            Icons.AutoMirrored.Filled.ShowChart
                        }
                        Icon(
                            imageVector = iconVector,
                            contentDescription = null,
                            tint = if (isSelected) colors.onPrimary else colors.textSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = tab.title,
                            style = typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) colors.onPrimary else colors.textSecondary
                        )
                    }
                }
            }
        }
    }
}

// =====================================================================================
// TAB 1: REKAP MINGGUAN PMK CONTENT
// =====================================================================================

@Composable
private fun WeeklyRecapTabContent(
    evaluations: List<WeeklyPmkEvaluation>,
    onNavigateToFentonChart: () -> Unit
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    Column(modifier = Modifier.fillMaxWidth()) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "DAFTAR EVALUASI MINGGUAN",
                style = typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Text(
                text = "Berdasarkan Sensor & Log",
                style = typography.labelSmall,
                color = colors.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Cards list
        if (evaluations.isEmpty()) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = colors.textSecondary,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "Belum Ada Evaluasi Mingguan",
                        style = typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Lakukan dan catat sesi PMK secara rutin setiap hari untuk melihat rekapitulasi mingguan dan evaluasi target durasi.",
                        style = typography.bodySmall,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        } else {
            evaluations.forEach { evaluation ->
                WeeklyEvaluationCard(evaluation = evaluation)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Bottom IDAI & WHO Recommendation Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(colors.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Rekomendasi IDAI & WHO",
                            style = typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        val recommendationText = buildAnnotatedString {
                            append("PMK minimal ")
                            withStyle(SpanStyle(color = colors.primary, fontWeight = FontWeight.Bold)) {
                                append("8–24 jam sehari")
                            }
                            append(" terbukti mempercepat kenaikan berat badan bayi BBLR ")
                            withStyle(SpanStyle(color = colors.textPrimary, fontWeight = FontWeight.Bold)) {
                                append("25–30g/hari")
                            }
                            append(", mencegah hipotermia, dan menstabilkan irama napas.")
                        }

                        Text(
                            text = recommendationText,
                            style = typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onNavigateToFentonChart,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.surfaceVariant,
                        contentColor = colors.textPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ShowChart,
                        contentDescription = null,
                        tint = colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Lihat Grafik Pertumbuhan Lengkap (Fenton)",
                        style = typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun WeeklyEvaluationCard(
    evaluation: WeeklyPmkEvaluation
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header row: Week title, Status chip, Target chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Minggu ${evaluation.weekNumber}",
                            style = typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (evaluation.isCurrentWeek) colors.primaryContainer else colors.surfaceVariant)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = evaluation.statusLabel,
                                style = typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (evaluation.isCurrentWeek) colors.primary else colors.textSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = evaluation.dateRangeText,
                        style = typography.labelSmall,
                        color = colors.textSecondary
                    )
                }

                // Target Status Badge
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(colors.warningContainer)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = colors.warning,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = evaluation.targetStatusBadge,
                            style = typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.onWarningContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Target Info Box in Cream Background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.weeklyCardCreamBg)
                    .padding(9.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Target: ${evaluation.targetHoursText}",
                            style = typography.labelSmall,
                            color = colors.textSecondary
                        )
                        Text(
                            text = "Capaian: ${evaluation.achievementText}",
                            style = typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.onWarningContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(colors.weeklyTargetBorder)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Bullet 1
                    val bullet1Text = buildAnnotatedString {
                        append("• ${evaluation.unmetDaysCount} hari belum capai target: ")
                        withStyle(SpanStyle(color = colors.onWarningContainer, fontWeight = FontWeight.Bold)) {
                            append(evaluation.unmetDaysDetail)
                        }
                    }
                    Text(
                        text = bullet1Text,
                        style = typography.labelSmall,
                        color = colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Bullet 2
                    Text(
                        text = evaluation.statusRequirementText,
                        style = typography.labelSmall,
                        color = colors.onWarningContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Daily activity header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dayRangeLabel = if (evaluation.dailyActivities.isNotEmpty()) {
                    "(${evaluation.dailyActivities.first().dayLabel} - ${evaluation.dailyActivities.last().dayLabel}):"
                } else {
                    "(Sen - Min):"
                }
                Text(
                    text = "Aktivitas Harian $dayRangeLabel",
                    style = typography.labelSmall,
                    color = colors.textSecondary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = evaluation.averageDailyHoursFormatted,
                    style = typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 7 Vertical Micro-Bars Chart
            DailyMiniBarsRow(dailyActivities = evaluation.dailyActivities)

            Spacer(modifier = Modifier.height(10.dp))

            // Embedded Fenton Summary Subcard
            FentonSummarySubCard(summary = evaluation.fentonSummary)
        }
    }
}

@Composable
private fun DailyMiniBarsRow(
    dailyActivities: List<DailyActivityBar>
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surfaceVariant)
            .padding(start = 8.dp, end = 8.dp, top = 6.dp, bottom = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            dailyActivities.forEach { item ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    // Duration text above bar
                    Text(
                        text = item.durationFormatted,
                        style = typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isAboveTarget) colors.primary else colors.warning
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Vertical Bar (Height proportional to hours, max 12h = 32dp)
                    val barFraction = (item.durationHours / 12f).coerceIn(0.20f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height((barFraction * 30).dp)
                            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                            .background(if (item.isAboveTarget) colors.primary else colors.weeklyBarAmber)
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    // Day label
                    Text(
                        text = item.dayLabel,
                        style = typography.labelSmall,
                        color = colors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun FentonSummarySubCard(
    summary: WeeklyFentonSummary
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colors.weeklyCardCreamBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.weeklyTargetBorder.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Header of subcard
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ShowChart,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Parameter Pertumbuhan (Fenton)",
                        style = typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }

                val (badgeBg, badgeText) = when (summary.statusType) {
                    FentonStatusType.NORMAL -> colors.successContainer to colors.onSuccessContainer
                    FentonStatusType.ALERT -> colors.primaryContainer to colors.primary
                    FentonStatusType.WARNING -> colors.warningContainer to colors.onWarningContainer
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeBg)
                        .border(
                            width = if (summary.statusType == FentonStatusType.ALERT) 1.dp else 0.dp,
                            color = if (summary.statusType == FentonStatusType.ALERT) colors.primaryBorder else Color.Transparent,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = summary.statusBadge,
                        style = typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = badgeText
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3 Column Grid for BB, PB, LK
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                summary.metrics.forEach { metric ->
                    FentonMetricSubItem(
                        metric = metric,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (!summary.warningNote.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.weeklyTargetBorder)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = summary.warningNote,
                        style = typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = colors.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun FentonMetricSubItem(
    metric: FentonMetricCardData,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(colors.cardBackground)
            .border(1.dp, colors.cardBorder, RoundedCornerShape(10.dp))
            .padding(vertical = 7.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Label
            Text(
                text = metric.label,
                style = typography.labelSmall,
                color = colors.textSecondary
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Value + Unit
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = metric.valueNumber,
                    style = typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Text(
                    text = metric.unit,
                    style = typography.labelSmall,
                    color = colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Delta Percentile Text
            val deltaColor = when (metric.statusType) {
                FentonStatusType.NORMAL -> colors.success
                FentonStatusType.WARNING -> colors.warning
                FentonStatusType.ALERT -> colors.primary
            }

            Text(
                text = metric.deltaPercentileText,
                style = typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = deltaColor
            )

            Spacer(modifier = Modifier.height(3.dp))

            // Status Badge Pill
            val (pillBg, pillText) = when (metric.statusType) {
                FentonStatusType.NORMAL -> colors.successContainer to colors.onSuccessContainer
                FentonStatusType.WARNING -> colors.warningContainer to colors.onWarningContainer
                FentonStatusType.ALERT -> colors.primaryContainer to colors.primary
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(pillBg)
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = metric.statusText,
                    style = typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = pillText
                )
            }
        }
    }
}

// =====================================================================================
// TAB 2: KORELASI PERTUMBUHAN CONTENT
// =====================================================================================

@Composable
private fun GrowthCorrelationTabContent(
    data: GrowthCorrelationData,
    pmaWeeksText: String
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    Column(modifier = Modifier.fillMaxWidth()) {
        // 1. Highlight Insight Card: Korelasi Positif Kuat (+0.92)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = colors.primaryContainer.copy(alpha = 0.45f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.primaryBorder.copy(alpha = 0.8f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(colors.cardBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "Korelasi Positif Kuat (${data.correlationCoefficient})",
                                style = typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "Faktor Dominan: ${data.dominantFactor}",
                                style = typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.primary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(colors.successContainer)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${data.statusBadge} ✓",
                            style = typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSuccessContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.primaryBorder.copy(alpha = 0.6f))
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = data.insightDescription,
                    style = typography.bodySmall,
                    color = colors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Dual-axis Chart Card: Durasi PMK vs Kenaikan BB
        DualAxisCorrelationChartCard(points = data.chartPoints)

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Evaluasi Tri-Parameter Fenton
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Evaluasi Tri-Parameter Fenton",
                        style = typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )

                    Text(
                        text = if (pmaWeeksText.startsWith("PMA", ignoreCase = true)) pmaWeeksText else "PMA $pmaWeeksText",
                        style = typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (data.triParameters.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada pengukuran antropometri Fenton.",
                            style = typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                } else {
                    data.triParameters.forEachIndexed { index, param ->
                        TriParameterItemRow(item = param)
                        if (index < data.triParameters.size - 1) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Tabel Analisis Respon Klinis
        ClinicalResponseTableCard(rows = data.clinicalResponseRows)

        Spacer(modifier = Modifier.height(14.dp))

        // 5. Rekomendasi Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(colors.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Rekomendasi",
                        style = typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceVariant)
                        .padding(12.dp)
                ) {
                    Text(
                        text = data.clinicalRecommendationQuote,
                        style = typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
            }
        }
    }
}

// =====================================================================================
// CHART: DUAL-AXIS PMK CONTACT VS WEIGHT GAIN
// =====================================================================================

@Composable
private fun DualAxisCorrelationChartCard(
    points: List<CorrelationChartPoint>
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Grafik Durasi PMK vs Kenaikan BB",
                        style = typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = if (points.isNotEmpty()) {
                            "Analisis tren ${points.size} minggu intervensi kanguru"
                        } else {
                            "Analisis tren intervensi kanguru"
                        },
                        style = typography.labelSmall,
                        color = colors.textSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Fenton Preterm",
                        style = typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (points.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceVariant.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ShowChart,
                            contentDescription = null,
                            tint = colors.textSecondary,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "Belum Ada Data Korelasi Mingguan",
                            style = typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Grafik korelasi akan terbentuk seiring berjalannya sesi PMK dan penimbangan berkala.",
                            style = typography.labelSmall,
                            color = colors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Chart Canvas area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    val primaryColor = colors.primary
                    val warningColor = colors.weeklyBarAmber
                    val chartAreaColor = colors.weeklyChartArea
                    val chartLineColor = colors.weeklyChartLine
                    val gridColor = colors.weeklyChartGrid
                    val textPrimaryColor = colors.textPrimary
                    val textSecondaryColor = colors.textSecondary

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val bottomY = h - 36.dp.toPx()
                        val topY = 24.dp.toPx()
                        val chartHeight = bottomY - topY

                        val rightLabelMargin = 64.dp.toPx()
                        val gridWidth = w - rightLabelMargin
                        val gridSteps = 3
                        for (i in 0..gridSteps) {
                            val y = topY + (chartHeight / gridSteps) * i
                            drawLine(
                                color = gridColor,
                                start = Offset(10.dp.toPx(), y),
                                end = Offset(gridWidth, y),
                                strokeWidth = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                            )
                        }

                        val maxHours = (points.maxOfOrNull { it.contactHours } ?: 10f).coerceAtLeast(10f)
                        val maxWeight = (points.maxOfOrNull { it.weightGainGram.toFloat() } ?: 200f).coerceAtLeast(200f)

                        val scalePaint = android.graphics.Paint().apply {
                            isAntiAlias = true
                            textAlign = android.graphics.Paint.Align.RIGHT
                            textSize = 9.5.dp.toPx()
                            color = textSecondaryColor.toArgb()
                        }

                        val scaleTexts = listOf(
                            "${maxWeight.toInt()}g / ${maxHours.toInt()}j",
                            "${(maxWeight * 0.75f).toInt()}g / ${(maxHours * 0.75f).toInt()}j",
                            "${(maxWeight * 0.5f).toInt()}g / ${(maxHours * 0.5f).toInt()}j"
                        )
                        for (i in 0..2) {
                            val y = topY + (chartHeight / gridSteps) * i
                            drawContext.canvas.nativeCanvas.drawText(
                                scaleTexts[i],
                                w - 4.dp.toPx(),
                                y + 4.dp.toPx(),
                                scalePaint
                            )
                        }

                        val colCount = points.size
                        val colWidth = (gridWidth / (colCount * 2.5f)).coerceIn(24.dp.toPx(), 40.dp.toPx())
                        val colXs = points.indices.map { i ->
                            gridWidth * ((i + 1f) / (colCount + 1f))
                        }

                        val weightPoints = points.mapIndexed { i, pt ->
                            val normalizedWeight = (pt.weightGainGram.toFloat() / maxWeight).coerceIn(0.08f, 0.95f)
                            val y = bottomY - chartHeight * normalizedWeight
                            Offset(colXs[i], y)
                        }

                        if (weightPoints.size > 1) {
                            val areaPath = Path().apply {
                                moveTo(weightPoints.first().x, weightPoints.first().y)
                                for (i in 0 until weightPoints.size - 1) {
                                    val p0 = weightPoints[i]
                                    val p1 = weightPoints[i + 1]
                                    cubicTo(
                                        x1 = (p0.x + p1.x) / 2f,
                                        y1 = p0.y,
                                        x2 = (p0.x + p1.x) / 2f,
                                        y2 = p1.y,
                                        x3 = p1.x,
                                        y3 = p1.y
                                    )
                                }
                                lineTo(weightPoints.last().x, bottomY)
                                lineTo(weightPoints.first().x, bottomY)
                                close()
                            }
                            drawPath(
                                path = areaPath,
                                color = chartAreaColor.copy(alpha = 0.45f)
                            )
                        }

                        points.forEachIndexed { i, pt ->
                            val normalizedHours = (pt.contactHours / maxHours).coerceIn(0.05f, 1f)
                            val bHeight = chartHeight * normalizedHours
                            val barTop = bottomY - bHeight
                            val barColor = if (pt.isAboveTarget) primaryColor else warningColor

                            drawRoundRect(
                                color = barColor,
                                topLeft = Offset(colXs[i] - colWidth / 2f, barTop),
                                size = Size(colWidth, bHeight),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
                            )
                        }

                        if (weightPoints.size > 1) {
                            val linePath = Path().apply {
                                moveTo(weightPoints.first().x, weightPoints.first().y)
                                for (i in 0 until weightPoints.size - 1) {
                                    val p0 = weightPoints[i]
                                    val p1 = weightPoints[i + 1]
                                    cubicTo(
                                        x1 = (p0.x + p1.x) / 2f,
                                        y1 = p0.y,
                                        x2 = (p0.x + p1.x) / 2f,
                                        y2 = p1.y,
                                        x3 = p1.x,
                                        y3 = p1.y
                                    )
                                }
                            }
                            drawPath(
                                path = linePath,
                                color = chartLineColor,
                                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        val weightPaint = android.graphics.Paint().apply {
                            isAntiAlias = true
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = 10.dp.toPx()
                            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                            color = chartLineColor.toArgb()
                        }

                        val durationPaint = android.graphics.Paint().apply {
                            isAntiAlias = true
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = 9.dp.toPx()
                            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                        }

                        val xTitlePaint = android.graphics.Paint().apply {
                            isAntiAlias = true
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = 10.dp.toPx()
                            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                            color = textPrimaryColor.toArgb()
                        }
                        val xSubPaint = android.graphics.Paint().apply {
                            isAntiAlias = true
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = 8.5.dp.toPx()
                            color = textSecondaryColor.toArgb()
                        }

                        points.forEachIndexed { i, pt ->
                            val dot = weightPoints[i]
                            val cx = colXs[i]

                            drawCircle(color = Color.White, radius = 5.dp.toPx(), center = dot)
                            drawCircle(color = chartLineColor, radius = 5.dp.toPx(), center = dot, style = Stroke(width = 2.5.dp.toPx()))

                            drawContext.canvas.nativeCanvas.drawText("${pt.weightGainGram}g", cx, dot.y - 8.dp.toPx(), weightPaint)

                            durationPaint.color = if (pt.isAboveTarget) primaryColor.toArgb() else warningColor.toArgb()
                            val hoursFormatted = String.format(java.util.Locale.US, "%.1fj", pt.contactHours)
                            drawContext.canvas.nativeCanvas.drawText(hoursFormatted, cx, dot.y - 20.dp.toPx(), durationPaint)

                            drawContext.canvas.nativeCanvas.drawText(pt.weekLabel, cx, bottomY + 14.dp.toPx(), xTitlePaint)
                            drawContext.canvas.nativeCanvas.drawText(pt.phaseLabel, cx, bottomY + 26.dp.toPx(), xSubPaint)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // <8j
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(colors.weeklyBarAmber)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "<8j (Kurang)",
                    style = typography.labelSmall,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.width(12.dp))

                // >=8j
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(colors.primary)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "≥8j (Optimal)",
                    style = typography.labelSmall,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Kenaikan BB
                Box(
                    modifier = Modifier
                        .width(12.dp)
                        .height(3.dp)
                        .clip(CircleShape)
                        .background(colors.weeklyChartLine)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Kenaikan BB (g/mgg)",
                    style = typography.labelSmall,
                    color = colors.textSecondary
                )
            }
        }
    }
}

// =====================================================================================
// TRI-PARAMETER ITEM ROW
// =====================================================================================

@Composable
private fun TriParameterItemRow(
    item: FentonTriParameterItem
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    val iconVector = when (item.title) {
        "Panjang Badan" -> Icons.Default.Straighten
        "Lingkar Kepala" -> Icons.Default.Psychology
        else -> Icons.Default.Scale
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.weeklyCardCreamBg)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.title,
                        style = typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.valueFormatted,
                        style = typography.labelSmall,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${item.deltaFormatted})",
                        style = typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.success
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "• ${item.percentileFormatted}",
                        style = typography.labelSmall,
                        color = colors.textSecondary
                    )
                }

                Spacer(modifier = Modifier.height(1.dp))

                Text(
                    text = item.subDescription,
                    style = typography.labelSmall,
                    color = colors.textSecondary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (item.isTeal) colors.categoryTealContainer
                        else colors.successContainer
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = item.statusBadge,
                    style = typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (item.isTeal) colors.categoryTealText else colors.onSuccessContainer
                )
            }
        }
    }
}

// =====================================================================================
// TABLE: CLINICAL RESPONSE
// =====================================================================================

@Composable
private fun ClinicalResponseTableCard(
    rows: List<ClinicalResponseRow>
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tabel Analisis Respon Klinis",
                    style = typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Text(
                    text = "Log Periodik",
                    style = typography.labelSmall,
                    color = colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Column Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PERIODE",
                    style = typography.labelSmall,
                    color = colors.textSecondary,
                    modifier = Modifier.weight(1.0f)
                )
                Text(
                    text = "RATA PMK",
                    style = typography.labelSmall,
                    color = colors.textSecondary,
                    modifier = Modifier.weight(1.2f)
                )
                Text(
                    text = "LAJU BB",
                    style = typography.labelSmall,
                    color = colors.textSecondary,
                    modifier = Modifier.weight(1.2f)
                )
                Text(
                    text = "SUHU",
                    style = typography.labelSmall,
                    color = colors.textSecondary,
                    modifier = Modifier.weight(1.1f)
                )
                Text(
                    text = "STATUS",
                    style = typography.labelSmall,
                    color = colors.textSecondary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1.1f)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(colors.divider)
            )

            // Rows
            if (rows.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada catatan respon klinis.",
                        style = typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
            } else {
                rows.forEach { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = row.periodLabel,
                            style = typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            modifier = Modifier.weight(1.0f)
                        )
                        Text(
                            text = row.avgPmkFormatted,
                            style = typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = if (row.isWarning) colors.warning else colors.textPrimary,
                            modifier = Modifier.weight(1.2f)
                        )
                        Text(
                            text = row.weightVelocityFormatted,
                            style = typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (row.isWarning) colors.textSecondary else colors.primary,
                            modifier = Modifier.weight(1.2f)
                        )
                        Text(
                            text = row.temperatureFormatted,
                            style = typography.bodySmall,
                            color = colors.textPrimary,
                            modifier = Modifier.weight(1.1f)
                        )

                        Box(
                            modifier = Modifier.weight(1.1f),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            val (badgeBg, badgeText) = when {
                                row.isOptimal -> colors.successContainer to colors.onSuccessContainer
                                else -> colors.warningContainer to colors.onWarningContainer
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(badgeBg)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = row.statusBadge,
                                    style = typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeText
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// =====================================================================================
// HELPER FORMATTER
// =====================================================================================

private fun formatGrams(grams: Int): String {
    return if (grams >= 1000) {
        String.format(java.util.Locale.US, "%,d", grams).replace(',', '.')
    } else {
        grams.toString()
    }
}
