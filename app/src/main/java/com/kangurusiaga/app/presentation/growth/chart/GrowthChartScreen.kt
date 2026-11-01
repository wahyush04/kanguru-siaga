package com.kangurusiaga.app.presentation.growth.chart

import android.content.Intent
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.GrowthParameter
import com.kangurusiaga.app.presentation.growth.components.FentonApiErrorDialog
import com.kangurusiaga.app.presentation.growth.components.GrowthIcons
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * GrowthChartScreen - Fenton Growth Chart (Laki-laki / Perempuan)
 * Faithful 1:1 Implementation of Google Stitch Screen:
 * projects/10808370107038581899/screens/d28b07eb3b934af1a98a28afd98704f7
 */
@Composable
fun GrowthChartScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToAboutFenton: () -> Unit,
    viewModel: GrowthChartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography
    val context = LocalContext.current

    val isBoy = uiState.baby?.gender != Gender.FEMALE
    val babyName = uiState.baby?.name?.takeIf { it.isNotBlank() } ?: "Data Bayi"

    // Derive avatar initials
    val initials = remember(babyName) {
        val cleanName = if (babyName.startsWith("Bayi ", ignoreCase = true)) {
            babyName.removePrefix("Bayi ").removePrefix("bayi ").trim()
        } else {
            babyName.trim()
        }
        val parts = cleanName.split(" ").filter { it.isNotBlank() }
        when {
            parts.size >= 2 -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
            parts.size == 1 && parts[0].length >= 2 -> parts[0].take(2).uppercase()
            parts.size == 1 -> parts[0].take(1).uppercase()
            else -> "BY"
        }
    }

    // Latest metric values with clean fallbacks from real data
    val currentWeightKg = uiState.latestWeight?.value
        ?: (uiState.baby?.birthWeightGram?.takeIf { it > 0 }?.let { it / 1000f })
    val currentLengthCm = uiState.latestLength?.value
        ?: uiState.baby?.birthLengthCm?.takeIf { it > 0f }
    val currentHeadCircCm = uiState.latestHeadCirc?.value
        ?: uiState.baby?.birthHeadCircumferenceCm?.takeIf { it > 0f }

    // Formatted last evaluation date
    val lastDateFormatted = remember(uiState.latestWeight, uiState.latestLength, uiState.latestHeadCirc) {
        val latestEpoch = listOfNotNull(
            uiState.latestWeight?.measurementDateEpochMillis,
            uiState.latestLength?.measurementDateEpochMillis,
            uiState.latestHeadCirc?.measurementDateEpochMillis
        ).maxOrNull()
        if (latestEpoch != null) {
            val sdf = SimpleDateFormat("d MMM", Locale("id", "ID"))
            sdf.format(Date(latestEpoch))
        } else {
            "-"
        }
    }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            // 1. TopBar with status bar padding to avoid notification bar overlap
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = GrowthIcons.Back,
                            contentDescription = stringResource(R.string.common_back),
                            tint = colors.textPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isBoy) "Grafik Pertumbuhan Fenton (Laki-laki)" else "Grafik Pertumbuhan Fenton (Perempuan)",
                            style = typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Standar Fenton 2025 • fentongrowth.ca",
                            style = typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = colors.info,
                            textAlign = TextAlign.Center
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onNavigateToAboutFenton,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Informasi Standar",
                                tint = colors.textSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Grafik Pertumbuhan Fenton: $babyName\nPMA: ${uiState.currentPmaWeeks.toInt()} Minggu\nBB: ${String.format(Locale.US, "%.3f", currentWeightKg)} kg\nPB: ${String.format(Locale.US, "%.1f", currentLengthCm)} cm\nLK: ${String.format(Locale.US, "%.1f", currentHeadCircCm)} cm"
                                    )
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Bagikan Data Pertumbuhan"))
                            },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileDownload,
                                contentDescription = "Unduh Grafik",
                                tint = colors.textSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(colors.cardBorder)
                )
            }
        },
        bottomBar = {
            // 6. Bottom Sticky CTA Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface)
                    .navigationBarsPadding()
                    .border(0.5.dp, colors.cardBorder)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = onNavigateToAdd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Catat Pengukuran Baru",
                        style = typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.onPrimary
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // 2. Patient Vital Context Card
            PatientVitalContextCard(
                initials = initials,
                babyName = babyName,
                isBoy = isBoy,
                gestationalAgeWeeksAtBirth = uiState.baby?.gestationalAgeWeeks ?: 0,
                birthWeightGrams = uiState.baby?.birthWeightGram ?: 0,
                currentPmaWeeks = uiState.currentPmaWeeks,
                weightKg = currentWeightKg,
                lengthCm = currentLengthCm,
                headCircCm = currentHeadCircCm,
                weightBadge = uiState.weightEvaluation?.percentileBadge ?: "-",
                lengthBadge = uiState.lengthEvaluation?.percentileBadge ?: "-",
                headCircBadge = uiState.headCircEvaluation?.percentileBadge ?: "-"
            )

            // 4. Interactive Fenton Chart Canvas Card
            Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                FentonChart(
                    selectedTab = uiState.selectedTab,
                    isBoy = isBoy,
                    weightMeasurements = uiState.weightMeasurementsAsc,
                    lengthMeasurements = uiState.lengthMeasurementsAsc,
                    headCircMeasurements = uiState.headCircMeasurementsAsc,
                    currentPmaWeeks = uiState.currentPmaWeeks,
                    babyName = babyName,
                    chartImageUrl = uiState.fentonChartImageUrl,
                    chartLocalPath = uiState.fentonChartLocalPath,
                    chartTimestamp = uiState.fentonChartTimestamp,
                    isLoading = uiState.isFentonApiLoading,
                    errorMessage = uiState.fentonApiErrorMessage,
                    onRetry = viewModel::retryFetchFentonChart
                )
            }

            // 5. Evaluasi Pertumbuhan Kejar (Catch-up Growth) Card
            Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                CatchUpGrowthEvaluationCard(
                    lastDate = lastDateFormatted,
                    weightKg = currentWeightKg,
                    weightBadge = uiState.weightEvaluation?.percentileBadge ?: "-",
                    weightGainRate = uiState.weightGainRateGramsPerDay,
                    lengthCm = currentLengthCm,
                    lengthBadge = uiState.lengthEvaluation?.percentileBadge ?: "-",
                    lengthGainRate = uiState.lengthGainRateCmPerWeek,
                    headCircCm = currentHeadCircCm,
                    headCircBadge = uiState.headCircEvaluation?.percentileBadge ?: "-",
                    headCircGainRate = uiState.headCircGainRateCmPerWeek
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Pop up dialog error bila terjadi kendala saat fetch API
    if (uiState.fentonApiErrorMessage != null) {
        FentonApiErrorDialog(
            errorMessage = uiState.fentonApiErrorMessage ?: "Gagal memuat grafik dari FentonGrowth.ca",
            onRetry = viewModel::retryFetchFentonChart,
            onDismiss = viewModel::dismissFentonApiError
        )
    }
}

/**
 * Section 2: Patient Vital Context Card
 */
@Composable
private fun PatientVitalContextCard(
    initials: String,
    babyName: String,
    isBoy: Boolean,
    gestationalAgeWeeksAtBirth: Int,
    birthWeightGrams: Int,
    currentPmaWeeks: Float,
    weightKg: Float?,
    lengthCm: Float?,
    headCircCm: Float?,
    weightBadge: String,
    lengthBadge: String,
    headCircBadge: String
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    val patientGradient = Brush.horizontalGradient(
        listOf(
            colors.fentonPatientGradientStart,
            colors.fentonPatientGradientMid,
            colors.fentonPatientGradientEnd
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
            .background(patientGradient)
            .border(1.dp, colors.fentonPatientCardBorder, RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
            .padding(16.dp)
    ) {
        // Patient Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Initials Circle
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(colors.fentonPatientMetricCardBg)
                        .border(1.dp, colors.fentonPatientMetricCardBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        style = typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.fentonPatientSubtitleText
                    )
                }

                // Name & Gender Tag
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = babyName,
                            style = typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(colors.fentonPatientMetricCardBg)
                                .border(0.8.dp, colors.fentonPatientMetricCardBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isBoy) "♂ Laki-laki" else "♀ Perempuan",
                                style = typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.fentonPatientSubtitleText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    val formattedBirthWeight = if (birthWeightGrams > 0) String.format(Locale("id", "ID"), "%,d", birthWeightGrams) else "-"
                    val birthSummary = if (gestationalAgeWeeksAtBirth > 0 && birthWeightGrams > 0) {
                        "Lahir: $gestationalAgeWeeksAtBirth mg • BB Lahir: $formattedBirthWeight g"
                    } else {
                        "Data lahir belum dicatat"
                    }
                    Text(
                        text = birthSummary,
                        style = typography.bodySmall,
                        color = colors.fentonPatientSubtitleText
                    )
                }
            }

            // PMA Saat Ini Glass Card
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.fentonPmaBadgeBg)
                    .border(1.dp, colors.fentonPatientMetricCardBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "PMA SAAT INI",
                        style = typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = colors.fentonPatientSubtitleText
                    )
                    Text(
                        text = if (currentPmaWeeks > 0) "${currentPmaWeeks.toInt()} Minggu" else "-",
                        style = typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.fentonPmaBadgeText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3 Balanced Metric Quick Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Card 1: Berat Badan
            PatientMetricCard(
                modifier = Modifier.weight(1f),
                title = "Berat Badan",
                value = weightKg?.let { String.format(Locale.US, "%.3f", it) } ?: "-",
                unit = if (weightKg != null) "kg" else "",
                badgeText = if (weightBadge.isNotBlank() && weightBadge != "-") weightBadge else "-"
            )

            // Card 2: Panjang Badan
            PatientMetricCard(
                modifier = Modifier.weight(1f),
                title = "Panjang Badan",
                value = lengthCm?.let { String.format(Locale.US, "%.1f", it) } ?: "-",
                unit = if (lengthCm != null) "cm" else "",
                badgeText = if (lengthBadge.isNotBlank() && lengthBadge != "-") lengthBadge else "-"
            )

            // Card 3: Lingkar Kepala
            PatientMetricCard(
                modifier = Modifier.weight(1f),
                title = "Lingkar Kepala",
                value = headCircCm?.let { String.format(Locale.US, "%.1f", it) } ?: "-",
                unit = if (headCircCm != null) "cm" else "",
                badgeText = if (headCircBadge.isNotBlank() && headCircBadge != "-") headCircBadge else "-"
            )
        }
    }
}

private fun badgeTextWithFallback(rawBadge: String, fallback: String): String {
    return if (rawBadge.isBlank() || rawBadge.contains("P50")) fallback else rawBadge
}

@Composable
private fun PatientMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    unit: String,
    badgeText: String
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.fentonPatientMetricCardBg)
            .border(1.dp, colors.fentonPatientMetricCardBorder, RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = typography.labelSmall,
            color = colors.fentonPatientSubtitleText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                style = typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = unit,
                style = typography.labelSmall,
                color = colors.fentonPatientSubtitleText
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(colors.fentonEvaluationTargetBg)
                .border(0.8.dp, colors.fentonEvaluationTargetBorder, RoundedCornerShape(10.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = badgeText,
                style = typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colors.badgeEmeraldText,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Section 5: Evaluasi Pertumbuhan Kejar (Catch-up Growth) Card
 */
@Composable
private fun CatchUpGrowthEvaluationCard(
    lastDate: String,
    weightKg: Float?,
    weightBadge: String,
    weightGainRate: Float?,
    lengthCm: Float?,
    lengthBadge: String,
    lengthGainRate: Float?,
    headCircCm: Float?,
    headCircBadge: String,
    headCircGainRate: Float?
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    val summary = remember(
        weightKg, weightBadge, weightGainRate,
        lengthCm, lengthBadge, lengthGainRate,
        headCircCm, headCircBadge, headCircGainRate
    ) {
        CatchUpGrowthEvaluator.evaluate(
            weightKg = weightKg,
            weightBadge = weightBadge,
            weightGainRateGramsPerDay = weightGainRate,
            lengthCm = lengthCm,
            lengthBadge = lengthBadge,
            lengthGainRateCmPerWeek = lengthGainRate,
            headCircCm = headCircCm,
            headCircBadge = headCircBadge,
            headCircGainRateCmPerWeek = headCircGainRate
        )
    }

    val indicatorColor = when (summary.overallLevel) {
        GrowthEvaluationLevel.OPTIMAL -> colors.success
        GrowthEvaluationLevel.NORMAL, GrowthEvaluationLevel.BASELINE -> colors.textTertiary
        GrowthEvaluationLevel.WARNING -> colors.fentonEvaluationWarningBadgeBg
        GrowthEvaluationLevel.DANGER -> colors.fentonEvaluationDangerBadgeBg
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardBackground)
            .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Card Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(indicatorColor)
                )
                Text(
                    text = "Evaluasi Pertumbuhan Kejar\n(Catch-up Growth)",
                    style = typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.surfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "Terakhir: $lastDate",
                    style = typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = colors.textSecondary
                )
            }
        }

        // Item 1: Berat Badan
        CatchUpMetricRow(item = summary.weightItem)

        // Item 2: Panjang Badan
        CatchUpMetricRow(item = summary.lengthItem)

        // Item 3: Lingkar Kepala
        CatchUpMetricRow(item = summary.headCircItem)

        // Physician Note Callout
        val noteCardBg = when (summary.overallLevel) {
            GrowthEvaluationLevel.DANGER -> colors.fentonEvaluationDangerBg
            GrowthEvaluationLevel.WARNING -> colors.fentonEvaluationWarningBg
            else -> colors.pediatricNoteCardBg
        }
        val noteCardBorder = when (summary.overallLevel) {
            GrowthEvaluationLevel.DANGER -> colors.fentonEvaluationDangerBorder
            GrowthEvaluationLevel.WARNING -> colors.fentonEvaluationWarningBorder
            else -> colors.pediatricNoteCardBorder
        }
        val noteTitleColor = when (summary.overallLevel) {
            GrowthEvaluationLevel.DANGER -> colors.fentonEvaluationDangerBadgeBg
            GrowthEvaluationLevel.WARNING -> colors.fentonEvaluationWarningBadgeBg
            else -> colors.pediatricNoteTitle
        }
        val noteIcon = if (summary.overallLevel == GrowthEvaluationLevel.DANGER || summary.overallLevel == GrowthEvaluationLevel.WARNING) {
            Icons.Default.Warning
        } else {
            Icons.Default.Info
        }
        val noteIconTint = when (summary.overallLevel) {
            GrowthEvaluationLevel.DANGER -> colors.fentonEvaluationDangerBadgeBg
            GrowthEvaluationLevel.WARNING -> colors.fentonEvaluationWarningBadgeBg
            else -> colors.pediatricNoteIconTint
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(noteCardBg)
                .border(1.dp, noteCardBorder, RoundedCornerShape(10.dp))
                .padding(10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = noteIcon,
                    contentDescription = null,
                    tint = noteIconTint,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(top = 2.dp)
                )
                Column {
                    Text(
                        text = summary.clinicalNoteTitle,
                        style = typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = noteTitleColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = summary.clinicalNoteMessage,
                        style = typography.bodySmall,
                        color = colors.pediatricNoteSubtitle,
                        lineHeight = typography.bodySmall.lineHeight
                    )
                }
            }
        }
    }
}

private data class MetricRowStyle(
    val bg: Color,
    val border: Color,
    val badgeBg: Color,
    val badgeText: Color,
    val subtitleColor: Color
)

@Composable
private fun CatchUpMetricRow(
    item: MetricEvaluationItem
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    val style = when (item.level) {
        GrowthEvaluationLevel.OPTIMAL -> MetricRowStyle(
            bg = colors.fentonEvaluationTargetBg,
            border = colors.fentonEvaluationTargetBorder,
            badgeBg = colors.fentonEvaluationTargetBadgeBg,
            badgeText = colors.fentonEvaluationTargetBadgeText,
            subtitleColor = colors.fentonEvaluationTargetBadgeBg
        )
        GrowthEvaluationLevel.WARNING -> MetricRowStyle(
            bg = colors.fentonEvaluationWarningBg,
            border = colors.fentonEvaluationWarningBorder,
            badgeBg = colors.fentonEvaluationWarningBadgeBg,
            badgeText = colors.fentonEvaluationWarningBadgeText,
            subtitleColor = colors.fentonEvaluationWarningBadgeBg
        )
        GrowthEvaluationLevel.DANGER -> MetricRowStyle(
            bg = colors.fentonEvaluationDangerBg,
            border = colors.fentonEvaluationDangerBorder,
            badgeBg = colors.fentonEvaluationDangerBadgeBg,
            badgeText = colors.fentonEvaluationDangerBadgeText,
            subtitleColor = colors.fentonEvaluationDangerBadgeBg
        )
        GrowthEvaluationLevel.NORMAL,
        GrowthEvaluationLevel.BASELINE -> MetricRowStyle(
            bg = colors.fentonEvaluationNormalRowBg,
            border = colors.cardBorder,
            badgeBg = colors.fentonEvaluationNormalBadgeBg,
            badgeText = colors.fentonEvaluationNormalBadgeText,
            subtitleColor = colors.textSecondary
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(style.bg)
            .border(1.dp, style.border, RoundedCornerShape(10.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.subtitle,
                style = typography.bodySmall,
                color = style.subtitleColor
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(style.badgeBg)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = item.badgeText,
                style = typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = style.badgeText
            )
        }
    }
}
