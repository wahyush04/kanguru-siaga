package com.kangurusiaga.app.presentation.pmk.timer

import android.annotation.SuppressLint
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import com.kangurusiaga.app.core.designsystem.theme.TimerDisplay
import com.kangurusiaga.app.domain.model.PmkCaregiver
import com.kangurusiaga.app.domain.model.PmkPauseReason
import com.kangurusiaga.app.domain.model.PmkTimerState
import com.kangurusiaga.app.domain.model.TimerStatus
import com.kangurusiaga.app.presentation.pmk.components.PmkCaregiverHandoverSheet
import com.kangurusiaga.app.presentation.pmk.components.PmkFinishSessionSheet
import com.kangurusiaga.app.presentation.pmk.components.PmkPauseSheet

@Composable
fun PmkTimerRoute(
    onNavigateBack: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToGuide: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PmkTimerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.refreshToday()
        viewModel.events.collect { event ->
            when (event) {
                is PmkTimerUiEvent.SessionCompleted -> {
                    onNavigateToHistory()
                }
                is PmkTimerUiEvent.Message -> {
                    snackbarHostState.showSnackbar(event.text)
                }
            }
        }
    }

    PmkTimerScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNavigateToHistory = onNavigateToHistory,
        onNavigateToGuide = onNavigateToGuide,
        onStart = { viewModel.startContinuousTimer() },
        onOpenPauseSheet = { viewModel.openPauseSheet() },
        onClosePauseSheet = { viewModel.closePauseSheet() },
        onSelectPauseReason = { viewModel.selectPauseReason(it) },
        onPauseDetailsChange = { temp, behav, notes -> viewModel.updatePauseDetails(temp, behav, notes) },
        onConfirmPause = { viewModel.confirmPause() },
        onResume = { viewModel.resumeTimer() },
        onOpenHandoverSheet = { viewModel.openCaregiverHandoverSheet(it) },
        onCloseHandoverSheet = { viewModel.closeCaregiverHandoverSheet() },
        onSelectHandoverCaregiver = { viewModel.selectHandoverCaregiver(it) },
        onSelectInitialCaregiver = { viewModel.selectInitialCaregiver(it) },
        onNotesChange = { viewModel.updateNotes(it) },
        onHandoverDetailsChange = { temp, resp, notes -> viewModel.updateHandoverDetails(temp, resp, notes) },
        onConfirmHandover = { viewModel.confirmCaregiverHandover() },
        onRequestFinish = { viewModel.requestFinishSession() },
        onCloseFinishDialog = { viewModel.closeFinishConfirmDialog() },
        onFinishDetailsChange = { temp, resp, notes -> viewModel.updateFinishDetails(temp, resp, notes) },
        onConfirmFinish = { viewModel.confirmFinishSession() },
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PmkTimerScreen(
    uiState: PmkTimerUiState,
    onNavigateBack: () -> Unit,
    onNavigateToHistory: () -> Unit = {},
    onNavigateToGuide: () -> Unit,
    onStart: () -> Unit,
    onOpenPauseSheet: () -> Unit,
    onClosePauseSheet: () -> Unit,
    onSelectPauseReason: (PmkPauseReason) -> Unit,
    onPauseDetailsChange: (String, String, String) -> Unit,
    onConfirmPause: () -> Unit,
    onResume: () -> Unit,
    onOpenHandoverSheet: (PmkCaregiver?) -> Unit,
    onCloseHandoverSheet: () -> Unit,
    onSelectHandoverCaregiver: (PmkCaregiver) -> Unit,
    onSelectInitialCaregiver: (PmkCaregiver) -> Unit = {},
    onNotesChange: (String) -> Unit = {},
    onHandoverDetailsChange: (String, String, String) -> Unit,
    onConfirmHandover: () -> Unit,
    onRequestFinish: () -> Unit,
    onCloseFinishDialog: () -> Unit,
    onFinishDetailsChange: (String, String, String) -> Unit = { _, _, _ -> },
    onConfirmFinish: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val timerState = uiState.timerState
    val handoverSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val pauseSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val finishSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            colors.pinkGradientStart,
            colors.pinkGradientMid,
            colors.pinkGradientEnd
        )
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        snackbarHost = { com.kangurusiaga.app.core.designsystem.component.KanguruSnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
                .padding(bottom = innerPadding.calculateBottomPadding().coerceAtLeast(0.dp))
                .verticalScroll(rememberScrollState())
        ) {
            // =========================================================================
            // 1. TOP HEADER (Navigation Bar: Back, "Mulai PMK", Riwayat)
            // =========================================================================
            PmkTopNavigationBar(
                onNavigateBack = onNavigateBack,
                onNavigateToHistory = onNavigateToHistory
            )

            // =========================================================================
            // 2. WHITE SURFACE CARD BODY (Google Stitch: Mulai PMK Varian Kasidig)
            // =========================================================================
            Surface(
                color = colors.cardBackground,
                shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Quick Action: Tombol Panduan (Pill Button di Kanan Atas)
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = colors.primaryContainer,
                            border = androidx.compose.foundation.BorderStroke(1.dp, colors.primaryBorder),
                            onClick = onNavigateToGuide,
                            shadowElevation = 0.dp
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                    contentDescription = "Panduan",
                                    tint = colors.primary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Panduan",
                                    style = KanguruTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // 3. Circular Progress Ring Gauge (24 Jam & 3x8 Jam Milestones)
                    PmkCircularGaugeSection(
                        todayProgress = timerState.todayProgress
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // 4. Timer Display, Status Badges & WHO Guideline Tag
                    PmkTimerDisplaySection(
                        timerState = timerState
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 5. Session Control Action Buttons (Grid 2 Kolom: Selesai & Jeda / Lanjut / Mulai)
                    PmkSessionActionControls(
                        timerState = timerState,
                        onStart = onStart,
                        onOpenPauseSheet = onOpenPauseSheet,
                        onResume = onResume,
                        onRequestFinish = onRequestFinish
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // 6. Yang Melakukan PMK (Caregiver Selection Card: Ibu, Ayah, Pendamping)
                    PmkCaregiverCard(
                        currentCaregiver = timerState.currentCaregiver,
                        isActive = timerState.isActive,
                        onSelectCaregiver = onSelectInitialCaregiver,
                        onOpenHandoverSheet = onOpenHandoverSheet
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 7. Catatan Sesi (Opsional, Maks 200 karakter)
                    PmkNotesInputField(
                        initialNotes = timerState.notes,
                        onNotesChange = onNotesChange
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // 8. Kotak Tips Medis (Continuous KMC Medical Tip)
                    PmkMedicalTipsBanner()

                    Spacer(modifier = Modifier.height(16.dp))

                    // 9. iOS Home Indicator
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 128.dp, height = 4.dp)
                                .clip(CircleShape)
                                .background(colors.dragHandle)
                        )
                    }
                }
            }
        }
    }

    // Modal Bottom Sheets (Dipertahankan 100%)
    if (uiState.showCaregiverHandoverSheet) {
        PmkCaregiverHandoverSheet(
            sheetState = handoverSheetState,
            selectedCaregiver = uiState.handoverCaregiver,
            temperatureInput = uiState.handoverTemperature,
            responseInput = uiState.handoverResponse,
            notesInput = uiState.handoverNotes,
            onSelectCaregiver = onSelectHandoverCaregiver,
            onTemperatureChange = { onHandoverDetailsChange(it, uiState.handoverResponse, uiState.handoverNotes) },
            onResponseChange = { onHandoverDetailsChange(uiState.handoverTemperature, it, uiState.handoverNotes) },
            onNotesChange = { onHandoverDetailsChange(uiState.handoverTemperature, uiState.handoverResponse, it) },
            onConfirm = onConfirmHandover,
            onDismiss = onCloseHandoverSheet
        )
    }

    if (uiState.showPauseSheet) {
        PmkPauseSheet(
            sheetState = pauseSheetState,
            selectedReason = uiState.pauseReason,
            temperatureInput = uiState.pauseTemperature,
            behaviorInput = uiState.pauseBehavior,
            notesInput = uiState.pauseNotes,
            onSelectReason = onSelectPauseReason,
            onTemperatureChange = { onPauseDetailsChange(it, uiState.pauseBehavior, uiState.pauseNotes) },
            onBehaviorChange = { onPauseDetailsChange(uiState.pauseTemperature, it, uiState.pauseNotes) },
            onNotesChange = { onPauseDetailsChange(uiState.pauseTemperature, uiState.pauseBehavior, it) },
            onConfirm = onConfirmPause,
            onDismiss = onClosePauseSheet
        )
    }

    if (uiState.showFinishSessionSheet || uiState.showFinishConfirmDialog) {
        PmkFinishSessionSheet(
            sheetState = finishSheetState,
            formattedActiveDuration = timerState.formattedContinuousTime,
            caregiver = timerState.currentCaregiver,
            formattedPauseDuration = timerState.formattedPauseTime,
            formattedTodayAccumulation = timerState.formattedTodayTotal,
            todayPercentage = timerState.todayPercentage,
            temperatureInput = uiState.finishTemperature,
            responseInput = uiState.finishResponse,
            notesInput = uiState.finishNotes,
            onTemperatureChange = { onFinishDetailsChange(it, uiState.finishResponse, uiState.finishNotes) },
            onResponseChange = { onFinishDetailsChange(uiState.finishTemperature, it, uiState.finishNotes) },
            onNotesChange = { onFinishDetailsChange(uiState.finishTemperature, uiState.finishResponse, it) },
            onConfirm = onConfirmFinish,
            onDismiss = onCloseFinishDialog
        )
    }
}

/**
 * Top App Navigation Bar matching Google Stitch design:
 * - Back button on the left (unobtrusive circular translucent container)
 * - Centered screen title "Mulai PMK"
 * - History button on the right (circular translucent container)
 */
@Composable
private fun PmkTopNavigationBar(
    onNavigateBack: () -> Unit,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.common_back),
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = "Mulai PMK",
            style = KanguruTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        IconButton(
            onClick = onNavigateToHistory,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f))
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = "Riwayat PMK",
                tint = Color.White,
                modifier = Modifier.size(19.dp)
            )
        }
    }
}

/**
 * 24-Hour Circular Gauge Arc matching Google Stitch:
 * - 252° horseshoe arch from bottom-left (0h) to bottom-right (24h)
 * - Ticks at 8h and 16h with amber markers
 * - Inner illustration of mother & baby kangaroo care
 * - 4 floating tick badges: 0 Jam, 8 Jam, 16 Jam, 24 Jam
 */
@Composable
private fun PmkCircularGaugeSection(
    todayProgress: Float,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val trackColor = colors.pmkGaugeTrack
    val progressGradient = listOf(colors.pinkGradientStart, colors.pinkGradientMid)
    val amberColor = colors.warning

    Box(
        modifier = modifier.size(264.dp),
        contentAlignment = Alignment.Center
    ) {
        // Canvas Horseshoe Arc
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidthPx = 16.dp.toPx()
            val diameter = size.minDimension - strokeWidthPx - 16.dp.toPx()
            val topLeftOffset = Offset(
                (size.width - diameter) / 2f,
                (size.height - diameter) / 2f
            )
            val arcSize = Size(diameter, diameter)

            // Start angle 144°, sweep 252°
            val startAngle = 144f
            val totalSweep = 252f

            // 1. Background Arc Track
            drawArc(
                color = trackColor,
                startAngle = startAngle,
                sweepAngle = totalSweep,
                useCenter = false,
                topLeft = topLeftOffset,
                size = arcSize,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )

            // 2. Active Progress Arc
            val activeSweep = totalSweep * todayProgress.coerceIn(0f, 1f)
            if (activeSweep > 0f) {
                drawArc(
                    brush = Brush.linearGradient(progressGradient),
                    startAngle = startAngle,
                    sweepAngle = activeSweep,
                    useCenter = false,
                    topLeft = topLeftOffset,
                    size = arcSize,
                    style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                )
            }

            // 3. Orange Period Divider Strips at 8h (33.3%) and 16h (66.6%)
            // Located directly within the progress bar track
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = diameter / 2f
            val tickWidth = 3.5.dp.toPx()

            listOf(8f / 24f, 16f / 24f).forEach { fraction ->
                val angleDeg = startAngle + totalSweep * fraction
                val angleRad = Math.toRadians(angleDeg.toDouble())
                // Radial strip positioned across/within the progress bar track
                val innerR = radius - strokeWidthPx / 2f
                val outerR = radius + strokeWidthPx / 2f

                val startX = center.x + (innerR * kotlin.math.cos(angleRad)).toFloat()
                val startY = center.y + (innerR * kotlin.math.sin(angleRad)).toFloat()
                val endX = center.x + (outerR * kotlin.math.cos(angleRad)).toFloat()
                val endY = center.y + (outerR * kotlin.math.sin(angleRad)).toFloat()

                drawLine(
                    color = amberColor,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = tickWidth,
                    cap = StrokeCap.Round
                )
            }
        }

        // Center Mother & Baby Illustration
        Image(
            painter = painterResource(id = R.drawable.il_pmk_timer_center),
            contentDescription = "Ibu dan Bayi PMK",
            modifier = Modifier.size(138.dp),
            contentScale = ContentScale.Fit
        )

        // 4 Tick Badges around the Arc
        PmkTickBadge(
            text = "0 Jam",
            isAmber = false,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 6.dp, bottom = 24.dp)
        )

        PmkTickBadge(
            text = "8 Jam",
            isAmber = true,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 22.dp, top = 16.dp)
        )

        PmkTickBadge(
            text = "16 Jam",
            isAmber = true,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 22.dp, top = 16.dp)
        )

        PmkTickBadge(
            text = "24 Jam",
            isAmber = false,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 6.dp, bottom = 24.dp)
        )
    }
}

@Composable
private fun PmkTickBadge(
    text: String,
    isAmber: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.94f))
            .border(
                1.dp,
                if (isAmber) colors.warningBorder else colors.outlineVariant,
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 7.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = KanguruTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (isAmber) colors.warning else colors.textTertiary
        )
    }
}

/**
 * Timer Display & Status Section matching Google Stitch:
 * - Target Status Pill: "Melampaui Target (≥ 8 Jam)" or "Target Periode X"
 * - Big Monospace Digital Timer (e.g. 17:45:00)
 * - Pulsing Active Indicator (Sedang berlangsung...)
 * - Minimal 8 - 24Jam (IDAI & WHO) Medical Guideline Tag
 */
@Composable
private fun PmkTimerDisplaySection(
    timerState: PmkTimerState,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val transition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Target Status Badge
        val hasPassed8Hours = timerState.todayTotalActiveSeconds >= 8 * 3600L
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(if (hasPassed8Hours) colors.successContainer else colors.primaryContainer)
                .border(
                    1.dp,
                    if (hasPassed8Hours) colors.successBorder else colors.primaryBorder,
                    CircleShape
                )
                .padding(horizontal = 14.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (hasPassed8Hours) {
                    "Melampaui Target (≥ 8 Jam)"
                } else {
                    "Target ${timerState.periodLabel} (${timerState.periodRangeLabel})"
                },
                style = KanguruTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (hasPassed8Hours) colors.onSuccessContainer else colors.primary
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Large Digital Stopwatch Display - always displays continuous daily PMK active time
        val displayedTime = timerState.formattedContinuousTime

        Text(
            text = displayedTime,
            style = TimerDisplay.copy(
                color = if (timerState.isPaused) colors.warning else colors.textPrimary
            ),
            maxLines = 1,
            softWrap = false
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Status Indicator with Pulsing Dot
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            val dotColor = when {
                timerState.isRunning -> colors.primary
                timerState.isPaused -> colors.warning
                else -> colors.textTertiary
            }
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .alpha(if (timerState.isRunning) pulseAlpha else 1f)
                    .background(dotColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = when {
                    timerState.isRunning -> "Sedang berlangsung..."
                    timerState.isPaused -> "Jeda Sementara (${timerState.formattedPauseTime}) • ${timerState.pauseReason ?: "Perawatan"}"
                    else -> if (timerState.todayTotalActiveSeconds > 0) {
                        "Siap Melanjutkan PMK (${timerState.periodLabel})"
                    } else {
                        "Siap Memulai ${timerState.periodLabel}"
                    }
                },
                style = KanguruTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = colors.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Medical Guideline Tag (IDAI & WHO)
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(colors.successContainer)
                .border(1.dp, colors.successBorder, CircleShape)
                .padding(horizontal = 10.dp, vertical = 3.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Minimal 8 - 24Jam ",
                    style = KanguruTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSuccessContainer
                )
                Text(
                    text = "(IDAI & WHO)",
                    style = KanguruTheme.typography.labelSmall,
                    fontWeight = FontWeight.Normal,
                    color = colors.textTertiary
                )
            }
        }
    }
}

/**
 * Session Action Controls matching Google Stitch:
 * - When RUNNING: [ ■ Selesai ] (Solid Rose) and [ ❚❚ Jeda ] (Soft Pink)
 * - When PAUSED: [ ■ Selesai ] (Solid Rose) and [ ▶ Lanjut ] (Soft Pink)
 * - When IDLE: Full width primary button to start active period
 */
@Composable
private fun PmkSessionActionControls(
    timerState: PmkTimerState,
    onStart: () -> Unit,
    onOpenPauseSheet: () -> Unit,
    onResume: () -> Unit,
    onRequestFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    when (timerState.status) {
        TimerStatus.RUNNING -> {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Button 1: ■ Selesai
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = colors.primary,
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(onClick = onRequestFinish)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(13.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Selesai",
                            style = KanguruTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Button 2: ❚❚ Jeda
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = colors.pmkPauseButtonBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.pmkPauseButtonBorder),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(onClick = onOpenPauseSheet)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = "Jeda",
                            tint = colors.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Jeda",
                            style = KanguruTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.primary
                        )
                    }
                }
            }
        }
        TimerStatus.PAUSED -> {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Button 1: ■ Selesai
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = colors.primary,
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(onClick = onRequestFinish)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(13.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Selesai",
                            style = KanguruTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Button 2: ▶ Lanjut
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = colors.pmkPauseButtonBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.pmkPauseButtonBorder),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(onClick = onResume)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Lanjut",
                            tint = colors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Lanjut",
                            style = KanguruTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.primary
                        )
                    }
                }
            }
        }
        TimerStatus.IDLE, TimerStatus.COMPLETED -> {
            // Button: ▶ Mulai PMK
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = colors.primary,
                shadowElevation = 3.dp,
                modifier = modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = onStart)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Mulai",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (timerState.todayTotalActiveSeconds > 0L) {
                            if (timerState.todayTotalActiveSeconds % (8 * 3600L) == 0L && timerState.currentPeriod > 1) {
                                "Mulai PMK (${timerState.periodLabel})"
                            } else {
                                "Lanjutkan PMK"
                            }
                        } else {
                            "Mulai PMK"
                        },
                        style = KanguruTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Caregiver Selector matching Google Stitch:
 * - Title: "Yang Melakukan PMK"
 * - Subtitle: "Bergantian kontak kulit tanpa putus"
 * - Badge: "Kontinu"
 * - 3 Columns: Ibu, Ayah, Pendamping
 * - Selected active card has 2dp coral border, checkmark bubble, and soft pink background
 * - Tap interaction allowed during IDLE, PAUSED, and RUNNING (handover)
 */
@Composable
private fun PmkCaregiverCard(
    currentCaregiver: PmkCaregiver,
    isActive: Boolean,
    onSelectCaregiver: (PmkCaregiver) -> Unit,
    onOpenHandoverSheet: (PmkCaregiver) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(colors.secondaryContainer)
            .border(1.dp, colors.warningBorder, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = "Yang Melakukan PMK",
                    style = KanguruTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.pmkCaregiverTitleBrown
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Bergantian kontak kulit tanpa putus",
                    style = KanguruTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = colors.pmkCaregiverSubtitleOrange
                )
            }
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(colors.pmkCaregiverBadgeBg)
                    .padding(horizontal = 10.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "Kontinu",
                    style = KanguruTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.pmkCaregiverSubtitleOrange
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PmkCaregiver.entries.forEach { caregiver ->
                val isSelected = currentCaregiver == caregiver
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) colors.primaryContainer else colors.surface)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) colors.primary else colors.outlineVariant,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                if (isActive) {
                                    if (!isSelected) {
                                        onOpenHandoverSheet(caregiver)
                                    }
                                } else {
                                    onSelectCaregiver(caregiver)
                                }
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) colors.pinkGradientStart.copy(alpha = 0.25f) else colors.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = caregiver.iconEmoji,
                                    style = KanguruTheme.typography.bodyMedium
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = caregiver.shortName,
                                style = KanguruTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) colors.primary else colors.textPrimary
                            )
                            Text(
                                text = if (isSelected) "Aktif" else "Siaga",
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) colors.primary else colors.textTertiary
                            )
                        }
                    }

                    // Checkmark badge in the top right corner
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .align(Alignment.TopEnd)
                                .padding(top = 4.dp, end = 4.dp)
                                .clip(CircleShape)
                                .background(colors.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✓",
                                color = Color.White,
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Session Notes Input Field matching Google Stitch:
 * - Label: "Catatan (Opsional)" and "Maks. 200 karakter"
 * - Multi-line styled field with light border and placeholder
 */
@Composable
private fun PmkNotesInputField(
    initialNotes: String,
    onNotesChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    var notesText by remember(initialNotes) { mutableStateOf(initialNotes) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Catatan ",
                    style = KanguruTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Text(
                    text = "(Opsional)",
                    style = KanguruTheme.typography.labelSmall,
                    fontWeight = FontWeight.Normal,
                    color = colors.textTertiary
                )
            }
            Text(
                text = "Maks. 200 karakter",
                style = KanguruTheme.typography.labelSmall,
                color = colors.textTertiary
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surface)
                .border(1.dp, colors.primaryBorder, RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            BasicTextField(
                value = notesText,
                onValueChange = {
                    if (it.length <= 200) {
                        notesText = it
                        onNotesChange(it)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = KanguruTheme.typography.bodySmall.copy(
                    color = colors.textPrimary
                ),
                decorationBox = { innerTextField ->
                    if (notesText.isEmpty()) {
                        Text(
                            text = "Tambahkan catatan sesi PMK (misal: bayi tenang, suhu hangat)...",
                            style = KanguruTheme.typography.bodySmall,
                            color = colors.textTertiary
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}

/**
 * Medical Tips Banner matching Google Stitch:
 * - Amber-themed card with lightbulb icon
 * - "Tips PMK Kontinu:" bold label with IDAI & WHO guideline text
 */
@Composable
private fun PmkMedicalTipsBanner(
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.secondaryContainer)
            .border(1.dp, colors.warningBorder, RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(colors.pmkCaregiverBadgeBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "💡",
                style = KanguruTheme.typography.labelSmall
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Tips PMK Kontinu:",
                style = KanguruTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colors.pmkCaregiverSubtitleOrange
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Dianjurkan selama mungkin (minimal 8–24 jam/hari). Libatkan Ayah dan keluarga untuk bergantian menggendong agar si kecil tetap hangat dan aman tanpa putus kontak kulit.",
                style = KanguruTheme.typography.labelSmall,
                color = colors.pmkCaregiverTitleBrown,
                lineHeight = 16.sp
            )
        }
    }
}
