package com.kangurusiaga.app.presentation.pmk.timer

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.domain.model.PmkCaregiver
import com.kangurusiaga.app.domain.model.PmkPauseReason
import com.kangurusiaga.app.domain.model.PmkTimerState
import com.kangurusiaga.app.domain.model.TimerStatus
import com.kangurusiaga.app.presentation.pmk.components.PmkCaregiverHandoverSheet
import com.kangurusiaga.app.presentation.pmk.components.PmkFinishSessionSheet
import com.kangurusiaga.app.presentation.pmk.components.PmkPauseSheet
import com.kangurusiaga.app.presentation.pmk.components.PmkTimeline24HourBar

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
        onHandoverDetailsChange = { temp, resp, notes -> viewModel.updateHandoverDetails(temp, resp, notes) },
        onConfirmHandover = { viewModel.confirmCaregiverHandover() },
        onRequestFinish = { viewModel.requestFinishSession() },
        onCloseFinishDialog = { viewModel.closeFinishConfirmDialog() },
        onFinishDetailsChange = { temp, resp, notes -> viewModel.updateFinishDetails(temp, resp, notes) },
        onConfirmFinish = { viewModel.confirmFinishSession() },
        onToggleNightMode = { viewModel.toggleNightModeDim() },
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
    onHandoverDetailsChange: (String, String, String) -> Unit,
    onConfirmHandover: () -> Unit,
    onRequestFinish: () -> Unit,
    onCloseFinishDialog: () -> Unit,
    onFinishDetailsChange: (String, String, String) -> Unit = { _, _, _ -> },
    onConfirmFinish: () -> Unit,
    onToggleNightMode: () -> Unit,
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

    val dimModifier = if (uiState.isNightModeDim) {
        Modifier.alpha(0.85f)
    } else Modifier

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .then(dimModifier),
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
            // TOP HEADER (Brand Pink Gradient Hero)
            // =========================================================================
            PmkTimerTopHeader(
                babyName = uiState.baby?.name ?: "Nirmala",
                isRunning = timerState.isRunning,
                isPaused = timerState.isPaused,
                onNavigateBack = onNavigateBack,
                onNavigateToGuide = onNavigateToGuide
            )

            // =========================================================================
            // WHITE SURFACE CARD BODY (Continuous PMK Dashboard)
            // =========================================================================
            Surface(
                color = colors.cardBackground,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    // 1. Continuous Stopwatch Hero Card (Controls are integrated inside)
                    PmkTimerHeroCard(
                        timerState = timerState,
                        onStart = onStart,
                        onOpenPauseSheet = onOpenPauseSheet,
                        onResume = onResume,
                        onRequestFinish = onRequestFinish
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. Timeline Ritme 24 Jam Component
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(onClick = onNavigateToHistory)
                    ) {
                        PmkTimeline24HourBar(timeline = uiState.timeline)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 4. Yang Melakukan PMK (Caregiver selection box)
                    PmkCaregiverSelector(
                        currentCaregiver = timerState.currentCaregiver,
                        isActive = timerState.isActive,
                        onSelectCaregiver = onSelectHandoverCaregiver,
                        onOpenHandoverSheet = onOpenHandoverSheet
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 5. Evaluasi & Catatan Kenyamanan
                    PmkSessionNotesCard()

                    Spacer(modifier = Modifier.height(10.dp))

                    // 6. Continuous Clinical Tips Card
                    PmkClinicalTipsCard()

                    Spacer(modifier = Modifier.height(16.dp))

                    // 7. iOS Home Indicator
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
                                .background(Color(0xFFCBD5E1))
                        )
                    }
                }
            }
        }
    }

    // Modal Bottom Sheets
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
            formattedActiveDuration = timerState.formattedTime,
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

@Composable
private fun PmkTimerTopHeader(
    babyName: String,
    isRunning: Boolean,
    isPaused: Boolean,
    onNavigateBack: () -> Unit,
    onNavigateToGuide: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(bottom = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            // Navigation Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Back button
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

                // Center: Single-line Title
                Text(
                    text = "PMK Kontinu Mandiri",
                    style = KanguruTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Right: Panduan cKMC Button
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.2f),
                    onClick = onNavigateToGuide
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = "Panduan",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Panduan",
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Baby Identity Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_pmk_mascot),
                            contentDescription = "Bayi",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        if (isRunning) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .align(Alignment.BottomEnd)
                                    .clip(CircleShape)
                                    .background(colors.success)
                                    .border(1.5.dp, Color.White, CircleShape)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = babyName,
                                style = KanguruTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(alpha = 0.25f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isPaused) "JEDA" else "PMK KONTINU AKTIF",
                                    style = KanguruTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.FavoriteBorder,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = buildAnnotatedString {
                                    append("Kontak Kulit: ")
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("Menempel di Dada")
                                    }
                                },
                                style = KanguruTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.95f)
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Target Ideal",
                        style = KanguruTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.18f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "20 Jam",
                            style = KanguruTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PmkTimerHeroCard(
    timerState: PmkTimerState,
    onStart: () -> Unit,
    onOpenPauseSheet: () -> Unit,
    onResume: () -> Unit,
    onRequestFinish: () -> Unit,
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

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        colors.primaryContainer.copy(alpha = 0.25f),
                        colors.cardBackground,
                        colors.surface
                    )
                )
            )
            .border(1.dp, colors.cardBorder, RoundedCornerShape(24.dp))
            .padding(horizontal = 14.dp, vertical = 14.dp)
    ) {
        // Watermark Heart in bottom right corner
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = colors.primary.copy(alpha = 0.06f),
            modifier = Modifier
                .size(92.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 12.dp, y = 12.dp)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Live Indicator Pulsing Badge
            val (badgeBg, badgeBorder, badgeDot, badgeText, badgeLabel) = when {
                timerState.isPaused -> Tuple5(
                    colors.warningContainer,
                    colors.warningBorder,
                    colors.warning,
                    colors.onWarningContainer,
                    "Jeda Sementara (${timerState.pauseReason ?: "Perawatan"})"
                )
                timerState.isRunning -> Tuple5(
                    colors.successContainer,
                    colors.successBorder,
                    colors.success,
                    colors.onSuccessContainer,
                    "Pemantauan Kontinu Berjalan"
                )
                else -> Tuple5(
                    colors.surfaceVariant,
                    colors.cardBorder,
                    colors.textTertiary,
                    colors.textSecondary,
                    "Siap Memulai Sesi PMK"
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(badgeBg)
                    .border(1.dp, badgeBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .alpha(if (timerState.isRunning) pulseAlpha else 1f)
                        .background(badgeDot)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = badgeLabel,
                    style = KanguruTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = badgeText
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Large Digital Stopwatch Display (HH : mm : ss)
            Text(
                text = if (timerState.isPaused) timerState.formattedPauseTime else timerState.formattedTime,
                style = KanguruTheme.timerDisplay.copy(
                    color = if (timerState.isPaused) colors.warning else colors.textPrimary
                ),
                maxLines = 1,
                softWrap = false
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (timerState.isPaused) "DURASI JEDA PERAWATAN" else "TOTAL KONTAK KULIT HARI INI",
                    style = KanguruTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textTertiary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(colors.primaryContainer)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${timerState.todayPercentage}% Menuju Ideal",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar Section directly inside Hero (max-w-[280px])
            Column(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(colors.inputBackground)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(5.dp))
                        .padding(1.dp)
                ) {
                    val totalW = maxWidth
                    val fillFraction = timerState.todayProgress.coerceIn(0f, 1f)

                    // Filled Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = fillFraction)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(5.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(colors.pinkGradientStart, colors.pinkGradientMid)
                                )
                            )
                    )

                    // Min 8 Jam Marker (33.3%)
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.5.dp)
                            .offset(x = totalW * 0.333f)
                            .background(colors.warning)
                    )

                    // Target Ideal 20 Jam Marker (83.3%)
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.5.dp)
                            .offset(x = totalW * 0.833f)
                            .background(colors.primary)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = timerState.formattedTodayTotal,
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = " / 24j",
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = colors.textSecondary
                        )
                    }
                    Text(
                        text = "Target: 20 Jam (83%)",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.primary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(
                    color = colors.cardBorder.copy(alpha = 0.5f),
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(colors.warning)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Min. Target ≥ 8 Jam",
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.onWarningContainer
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(colors.primary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Rekomendasi: ≥20 Jam",
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.primary
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(colors.surface)
                            .border(0.5.dp, colors.outlineVariant, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Kemenkes / WHO",
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = colors.textTertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons Group (Stitch: PMK Kontinu Mandiri 1 V2)
            when (timerState.status) {
                TimerStatus.RUNNING -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Tombol Kiri: Selesai PMK
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = colors.surface,
                            border = BorderStroke(2.dp, colors.primary),
                            shadowElevation = 1.dp,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable(onClick = onRequestFinish)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(colors.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(colors.primary)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f, fill = false)) {
                                    Text(
                                        text = "Selesai PMK",
                                        style = KanguruTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.primary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Simpan Sesi",
                                        style = KanguruTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Medium,
                                        color = colors.primary.copy(alpha = 0.8f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        // Tombol Kanan: Jeda PMK
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            shadowElevation = 3.dp,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable(onClick = onOpenPauseSheet)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(colors.pinkGradientStart, colors.pinkGradientMid)
                                        )
                                    )
                                    .padding(horizontal = 10.dp, vertical = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Pause,
                                            contentDescription = "Jeda PMK",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f, fill = false)) {
                                        Text(
                                            text = "Jeda PMK",
                                            style = KanguruTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Mandi / Rawat",
                                            style = KanguruTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Normal,
                                            color = Color.White.copy(alpha = 0.9f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                TimerStatus.PAUSED -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Tombol Kiri: Selesai PMK
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = colors.surface,
                            border = BorderStroke(2.dp, colors.primary),
                            shadowElevation = 1.dp,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable(onClick = onRequestFinish)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(colors.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(colors.primary)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f, fill = false)) {
                                    Text(
                                        text = "Selesai PMK",
                                        style = KanguruTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.primary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Simpan Sesi",
                                        style = KanguruTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Medium,
                                        color = colors.primary.copy(alpha = 0.8f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        // Tombol Kanan: Lanjutkan PMK
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            shadowElevation = 3.dp,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable(onClick = onResume)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(colors.pinkGradientStart, colors.pinkGradientMid)
                                        )
                                    )
                                    .padding(horizontal = 10.dp, vertical = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Lanjutkan PMK",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f, fill = false)) {
                                        Text(
                                            text = "Lanjutkan PMK",
                                            style = KanguruTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Kontak Kulit",
                                            style = KanguruTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Normal,
                                            color = Color.White.copy(alpha = 0.9f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                TimerStatus.IDLE -> {
                    // Mulai PMK Kontinu - Full Width Primary
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 3.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(onClick = onStart)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(colors.pinkGradientStart, colors.pinkGradientMid)
                                    )
                                )
                                .padding(vertical = 14.dp, horizontal = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Mulai",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Mulai PMK Kontinu",
                                        style = KanguruTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Mulai pencatatan kontak kulit dada-ke-dada",
                                        style = KanguruTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }
                    }
                }
                TimerStatus.COMPLETED -> Unit
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Continuity pill di dalam Hero
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.cardBackground)
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .alpha(if (timerState.isRunning) pulseAlpha else 1f)
                        .background(colors.primary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Mode PMK Kontinu 24 Jam: Estafet Bergantian • Hanya Jeda Singkat Medis/Perawatan",
                    style = KanguruTheme.typography.labelSmall,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private data class Tuple5<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)

@Composable
private fun PmkCaregiverSelector(
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
            .clip(RoundedCornerShape(16.dp))
            .background(colors.secondaryContainer)
            .border(1.dp, colors.warningBorder, RoundedCornerShape(16.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Yang Melakukan PMK",
                    style = KanguruTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSecondaryContainer
                )
                Text(
                    text = "Bergantian kontak kulit tanpa putus",
                    style = KanguruTheme.typography.labelSmall,
                    color = colors.onWarningContainer
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.warningContainer)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "Kontinu",
                    style = KanguruTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onWarningContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PmkCaregiver.entries.forEach { caregiver ->
                val isSelected = currentCaregiver == caregiver
                Box(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) colors.primaryContainer.copy(alpha = 0.5f) else colors.surface)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) colors.primary else colors.outlineVariant,
                                shape = RoundedCornerShape(12.dp)
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
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) colors.primaryContainer else colors.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = caregiver.iconEmoji,
                                    style = KanguruTheme.typography.bodyMedium
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = caregiver.shortName,
                                style = KanguruTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) colors.primary else colors.textPrimary
                            )
                            Text(
                                text = if (isSelected) "Aktif" else "Siaga",
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                                color = if (isSelected) colors.primary else colors.textTertiary
                            )
                        }
                    }
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = 4.dp, y = (-4).dp)
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

@Composable
private fun PmkSessionNotesCard(
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    var sessionNotes by remember { mutableStateOf("") }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Evaluasi & Catatan Kenyamanan",
                style = KanguruTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
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
                .background(colors.surfaceVariant)
                .border(1.dp, colors.outlineVariant, RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            BasicTextField(
                value = sessionNotes,
                onValueChange = { if (it.length <= 200) sessionNotes = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = KanguruTheme.typography.bodySmall.copy(
                    color = colors.textPrimary
                ),
                decorationBox = { innerTextField ->
                    if (sessionNotes.isEmpty()) {
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

@Composable
private fun PmkClinicalTipsCard(
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.secondaryContainer)
            .border(1.dp, colors.warningBorder, RoundedCornerShape(16.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(colors.warningContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "💡", style = KanguruTheme.typography.labelSmall)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Rekomendasi IDAI & WHO: ",
                style = KanguruTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colors.warning
            )
            Text(
                text = "PMK Kontinu tanpa putus (>20 jam/hari) menurunkan risiko hipotermia hingga 70% dan mempercepat kenaikan berat badan BBLR. Bergantianlah antar anggota keluarga.",
                style = KanguruTheme.typography.labelSmall,
                color = colors.onSecondaryContainer
            )
        }
    }
}
