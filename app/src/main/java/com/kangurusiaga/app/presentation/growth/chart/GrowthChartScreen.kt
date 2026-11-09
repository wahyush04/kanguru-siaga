package com.kangurusiaga.app.presentation.growth.chart

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.presentation.growth.components.FentonApiErrorDialog
import com.kangurusiaga.app.presentation.growth.components.GrowthIcons
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

    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.saveChartToGallery { success, message ->
                Toast.makeText(context, message, if (success) Toast.LENGTH_SHORT else Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, "Izin penyimpanan dibutuhkan untuk menyimpan ke galeri", Toast.LENGTH_SHORT).show()
        }
    }

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
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ||
                                    ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    viewModel.saveChartToGallery { success, message ->
                                        Toast.makeText(context, message, if (success) Toast.LENGTH_SHORT else Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                }
                            },
                            enabled = !uiState.isSavingToGallery,
                            modifier = Modifier.size(34.dp)
                        ) {
                            if (uiState.isSavingToGallery) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = colors.primary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.FileDownload,
                                    contentDescription = "Simpan Grafik ke Galeri",
                                    tint = colors.textSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
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
                currentPmaWeeks = uiState.currentPmaWeeks
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

            // 5. Evaluasi Pertumbuhan (Buku KIA)
            Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                KiaGrowthEvaluationCard()
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
 * Section 2: Patient Vital Context Card (Without BB, PB, LK cards per Stitch & User specification)
 */
@Composable
private fun PatientVitalContextCard(
    initials: String,
    babyName: String,
    isBoy: Boolean,
    gestationalAgeWeeksAtBirth: Int,
    birthWeightGrams: Int,
    currentPmaWeeks: Float
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
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f, fill = false)
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

                    val formattedBirthWeight = if (birthWeightGrams > 0) {
                        if (birthWeightGrams >= 1000) {
                            val kg = birthWeightGrams / 1000f
                            if (kg % 1f == 0f) "${kg.toInt()}" else String.format(Locale.US, "%.1f", kg)
                        } else {
                            "$birthWeightGrams g"
                        }
                    } else "-"
                    val birthSummary = if (gestationalAgeWeeksAtBirth > 0 && birthWeightGrams > 0) {
                        "Lahir: $gestationalAgeWeeksAtBirth mg • BB Lahir: $formattedBirthWeight"
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

            Spacer(modifier = Modifier.width(8.dp))

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
    }
}

/**
 * Section 5: Evaluasi Pertumbuhan (Buku KIA) - Native Implementation
 */
private enum class KiaTrendType {
    UPWARD, FLAT, DOWNWARD
}

@Composable
private fun KiaGrowthEvaluationCard(
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        border = BorderStroke(1.dp, colors.cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Pantau pertumbuhan bayi",
                style = typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Left Column: Grafik Pertumbuhan Bayi Card
                Card(
                    modifier = Modifier
                        .weight(0.95f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.kiaGrowthPreviewBg),
                    border = BorderStroke(1.5.dp, colors.kiaGrowthPreviewBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.surface),
                            border = BorderStroke(1.dp, colors.cardBorder),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 6.dp, vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.gambarcart),
                                    contentDescription = "Grafik Pertumbuhan Bayi",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Grafik Pertumbuhan\nBayi",
                            style = typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }

                // Right Column: Status Cards (Baik, Hati-hati, Bahaya!)
                Column(
                    modifier = Modifier
                        .weight(1.35f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KiaStatusCard(
                        title = "Baik",
                        titleColor = colors.kiaGrowthBaik,
                        trendType = KiaTrendType.UPWARD,
                        dotColor = colors.kiaGrowthBaik,
                        lineColor = colors.kiaGrowthTrendLine,
                        subtitle = null,
                        modifier = Modifier.weight(1f)
                    )

                    KiaStatusCard(
                        title = "Hati-hati",
                        titleColor = colors.kiaGrowthHatiHati,
                        trendType = KiaTrendType.FLAT,
                        dotColor = colors.kiaGrowthHatiHati,
                        lineColor = colors.kiaGrowthTrendLine,
                        subtitle = "Kurva mendatar",
                        modifier = Modifier.weight(1f)
                    )

                    KiaStatusCard(
                        title = "Bahaya!",
                        titleColor = colors.kiaGrowthBahaya,
                        trendType = KiaTrendType.DOWNWARD,
                        dotColor = colors.kiaGrowthBahaya,
                        lineColor = colors.kiaGrowthTrendLine,
                        subtitle = "Kurva tidak naik",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    tint = colors.textSecondary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Sumber: Buku KIA Khusus Bayi Kecil (Kemenkes RI, 2021)",
                    style = typography.labelSmall,
                    color = colors.textSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun KiaStatusCard(
    title: String,
    titleColor: Color,
    trendType: KiaTrendType,
    dotColor: Color,
    lineColor: Color,
    subtitle: String?,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        border = BorderStroke(1.dp, colors.cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = titleColor
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.dp),
                contentAlignment = Alignment.Center
            ) {
                KiaTrendLineCanvas(
                    trendType = trendType,
                    dotColor = dotColor,
                    lineColor = lineColor,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(22.dp)
                )
            }

            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = typography.labelSmall,
                    color = colors.textSecondary
                )
            } else {
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun KiaTrendLineCanvas(
    trendType: KiaTrendType,
    dotColor: Color,
    lineColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val (p1, p2, p3, p4) = when (trendType) {
            KiaTrendType.UPWARD -> listOf(
                Offset(w * 0.10f, h * 0.88f),
                Offset(w * 0.37f, h * 0.62f),
                Offset(w * 0.64f, h * 0.36f),
                Offset(w * 0.90f, h * 0.12f)
            )
            KiaTrendType.FLAT -> listOf(
                Offset(w * 0.10f, h * 0.50f),
                Offset(w * 0.37f, h * 0.50f),
                Offset(w * 0.63f, h * 0.50f),
                Offset(w * 0.90f, h * 0.50f)
            )
            KiaTrendType.DOWNWARD -> listOf(
                Offset(w * 0.10f, h * 0.22f),
                Offset(w * 0.37f, h * 0.18f),
                Offset(w * 0.64f, h * 0.55f),
                Offset(w * 0.90f, h * 0.88f)
            )
        }

        val strokeWidthPx = 2.dp.toPx()
        val dotRadiusPx = 3.8.dp.toPx()

        // Connecting lines
        drawLine(
            color = lineColor,
            start = p1,
            end = p2,
            strokeWidth = strokeWidthPx,
            cap = StrokeCap.Round
        )
        drawLine(
            color = lineColor,
            start = p2,
            end = p3,
            strokeWidth = strokeWidthPx,
            cap = StrokeCap.Round
        )
        drawLine(
            color = lineColor,
            start = p3,
            end = p4,
            strokeWidth = strokeWidthPx,
            cap = StrokeCap.Round
        )

        // 4 dots
        drawCircle(color = dotColor, radius = dotRadiusPx, center = p1)
        drawCircle(color = dotColor, radius = dotRadiusPx, center = p2)
        drawCircle(color = dotColor, radius = dotRadiusPx, center = p3)
        drawCircle(color = dotColor, radius = dotRadiusPx, center = p4)
    }
}
