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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.kangurusiaga.app.domain.model.PmkCaregiver
import com.kangurusiaga.app.domain.model.PmkPauseReason
import com.kangurusiaga.app.domain.model.TimerStatus
import com.kangurusiaga.app.presentation.pmk.components.PmkCaregiverHandoverSheet
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
    onConfirmFinish: () -> Unit,
    onToggleNightMode: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val timerState = uiState.timerState
    val handoverSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val pauseSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(bottom = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    // Navigation Bar (1:1 with Stitch reference)
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
                                if (timerState.isRunning) {
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
                                        text = uiState.baby?.name ?: "Nirmala",
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
                                            text = if (timerState.isPaused) "JEDA" else "PMK KONTINU AKTIF",
                                            style = KanguruTheme.typography.labelSmall,
                                            fontSize = 9.sp,
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
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.85f),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Kontak Kulit: Menempel di Dada",
                                        style = KanguruTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Target Ideal",
                                style = KanguruTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.Black.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "20 Jam",
                                        style = KanguruTheme.typography.bodySmall,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                        }
                    }
                }
            }

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
                    // 1. Continuous Stopwatch Hero Card
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFFFFF5F7),
                                        Color(0xFFFFF9F9),
                                        Color.White
                                    )
                                )
                            )
                            .border(1.dp, Color(0xFFFFE2E7), RoundedCornerShape(24.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        // Watermark Heart in bottom right corner
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = colors.primary.copy(alpha = 0.07f),
                            modifier = Modifier
                                .size(88.dp)
                                .align(Alignment.BottomEnd)
                                .offset(x = 12.dp, y = 12.dp)
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Live Indicator Pulsing Badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFFECFDF5))
                                    .border(1.dp, Color(0xFFA7F3D0).copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 12.dp, vertical = 3.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .alpha(if (timerState.isRunning) pulseAlpha else 1f)
                                        .background(Color(0xFF10B981))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (timerState.isPaused) "Jeda Sementara (${timerState.pauseReason ?: "Perawatan"})" else "Pemantauan Kontinu Berjalan",
                                    style = KanguruTheme.typography.labelSmall,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF047857)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Large Digital Stopwatch Display (HH : mm : ss)
                            Text(
                                text = if (timerState.isPaused) timerState.formattedPauseTime else timerState.formattedTime,
                                fontSize = 36.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                softWrap = false,
                                color = if (timerState.isPaused) colors.warning else Color(0xFF0F172A),
                                letterSpacing = 0.5.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (timerState.isPaused) "DURASI JEDA PERAWATAN" else "TOTAL KONTAK KULIT HARI INI",
                                    style = KanguruTheme.typography.labelSmall,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.5.sp,
                                    color = colors.textTertiary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color(0xFFFFE8EC))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${timerState.todayPercentage}% Menuju Ideal",
                                        style = KanguruTheme.typography.labelSmall,
                                        fontSize = 10.sp,
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
                                        .background(Color(0xFFF1F5F9))
                                        .border(1.dp, Color(0xFFFFE2E7), RoundedCornerShape(5.dp))
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
                                                    listOf(Color(0xFFFF758F), Color(0xFFFF5C77))
                                                )
                                            )
                                    )

                                    // Min 8 Jam Marker (33.3%)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(1.5.dp)
                                            .offset(x = totalW * 0.333f)
                                            .background(Color(0xFFFBBF24))
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
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E293B)
                                        )
                                        Text(
                                            text = " / 24j",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                    Text(
                                        text = "Target: 20 Jam (83%)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.primary
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                HorizontalDivider(
                                    color = Color(0xFFF1F5F9),
                                    thickness = 1.dp
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "★",
                                            fontSize = 10.sp,
                                            color = colors.primary
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "Target Rekomendasi: ",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = colors.primary
                                        )
                                        Text(
                                            text = "≥20 Jam / Hari",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.primary
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFFF8FAFC))
                                            .border(0.5.dp, Color(0xFFF1F5F9), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Kemenkes / WHO",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF94A3B8)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. Timeline Ritme 24 Jam Component (Positioned directly below Hero)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(onClick = onNavigateToHistory)
                    ) {
                        PmkTimeline24HourBar(timeline = uiState.timeline)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3. Ergonomic Action Controls
                    when (timerState.status) {
                        TimerStatus.RUNNING -> {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    shadowElevation = 3.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
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
                                            .padding(horizontal = 16.dp, vertical = 14.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.White.copy(alpha = 0.25f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Pause,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            Column {
                                                Text(
                                                    text = "Jeda Sementara PMK",
                                                    style = KanguruTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = "Mandi, Menyusu, atau Kebutuhan Perawatan Singkat",
                                                    style = KanguruTheme.typography.bodySmall,
                                                    fontSize = 10.sp,
                                                    color = Color.White.copy(alpha = 0.9f)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Continuity pill
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(colors.primaryContainer)
                                        .border(1.dp, colors.primary.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(colors.primary)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Mode PMK Kontinu 24 Jam: Estafet Bergantian • Hanya Jeda Singkat Medis/Perawatan",
                                        style = KanguruTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        color = colors.textPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        TimerStatus.PAUSED -> {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    shadowElevation = 3.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
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
                                            .padding(horizontal = 16.dp, vertical = 14.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.White.copy(alpha = 0.25f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.PlayArrow,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            Column {
                                                Text(
                                                    text = "Lanjutkan PMK Kontinu",
                                                    style = KanguruTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = "Kembali ke posisi kontak kulit dada-ke-dada",
                                                    style = KanguruTheme.typography.bodySmall,
                                                    fontSize = 10.sp,
                                                    color = Color.White.copy(alpha = 0.9f)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Continuity pill
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color(0xFFFFF5F7))
                                        .border(1.dp, Color(0xFFFFE2E7), RoundedCornerShape(20.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(colors.primary)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Mode PMK Kontinu 24 Jam: Estafet Bergantian • Hanya Jeda Singkat Medis/Perawatan",
                                        style = KanguruTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        color = colors.textPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        TimerStatus.IDLE -> {
                            Column(modifier = Modifier.fillMaxWidth()) {
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
                                            .padding(horizontal = 16.dp, vertical = 14.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.White.copy(alpha = 0.25f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Pause,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            Column {
                                                Text(
                                                    text = "Jeda Sementara PMK",
                                                    style = KanguruTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = "Mandi, Menyusu, atau Kebutuhan Perawatan Singkat",
                                                    style = KanguruTheme.typography.bodySmall,
                                                    fontSize = 10.sp,
                                                    color = Color.White.copy(alpha = 0.9f)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Continuity pill
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color(0xFFFFF5F7))
                                        .border(1.dp, Color(0xFFFFE2E7), RoundedCornerShape(20.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(colors.primary)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Mode PMK Kontinu 24 Jam: Estafet Bergantian • Hanya Jeda Singkat Medis/Perawatan",
                                        style = KanguruTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        color = colors.textPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        TimerStatus.COMPLETED -> Unit
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Yang Melakukan PMK (Caregiver selection box)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFFF9F2))
                            .border(1.dp, Color(0xFFFED7AA), RoundedCornerShape(16.dp))
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
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF78350F)
                                )
                                Text(
                                    text = "Bergantian kontak kulit tanpa putus",
                                    fontSize = 9.sp,
                                    color = Color(0xFF9A3412)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFEEAD8))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Kontinu",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF92400E)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PmkCaregiver.entries.forEach { caregiver ->
                                val isSelected = timerState.currentCaregiver == caregiver
                                Box(modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) Color(0xFFFFF5F7) else Color.White)
                                            .border(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) Color(0xFFFF5C77) else Color(0xFFE2E8F0),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable {
                                                if (timerState.isActive) {
                                                    if (!isSelected) {
                                                        onOpenHandoverSheet(caregiver)
                                                    }
                                                } else {
                                                    onSelectHandoverCaregiver(caregiver)
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
                                                    .background(if (isSelected) Color(0xFFFFE8EC) else Color(0xFFF1F5F9)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = caregiver.iconEmoji, fontSize = 14.sp)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = caregiver.shortName,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color(0xFFFF5C77) else Color(0xFF334155)
                                            )
                                            Text(
                                                text = if (isSelected) "Aktif" else "Siaga",
                                                fontSize = 9.sp,
                                                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                                                color = if (isSelected) Color(0xFFFF5C77).copy(alpha = 0.8f) else Color(0xFF94A3B8)
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
                                                .background(Color(0xFFFF5C77)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "✓",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 5. Evaluasi & Catatan Kenyamanan (Stitch: session-notes-input)
                    var sessionNotes by remember { mutableStateOf("") }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Evaluasi & Catatan Kenyamanan",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF334155)
                            )
                            Text(
                                text = "Maks. 200 karakter",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF8FAFC))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            androidx.compose.foundation.text.BasicTextField(
                                value = sessionNotes,
                                onValueChange = { if (it.length <= 200) sessionNotes = it },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 11.sp,
                                    color = Color(0xFF1E293B),
                                    lineHeight = 16.sp
                                ),
                                decorationBox = { innerTextField ->
                                    if (sessionNotes.isEmpty()) {
                                        Text(
                                            text = "Tambahkan catatan sesi PMK (misal: bayi tenang, suhu hangat)...",
                                            fontSize = 11.sp,
                                            color = Color(0xFF94A3B8),
                                            lineHeight = 16.sp
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 6. Continuous Clinical Tips Card (Stitch: continuous-clinical-guideline)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFFF9F2))
                            .border(1.dp, Color(0xFFFDE6D2), RoundedCornerShape(16.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEEAD8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "💡", fontSize = 12.sp)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Rekomendasi IDAI & WHO: ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD9531E)
                            )
                            Text(
                                text = "PMK Kontinu tanpa putus (>20 jam/hari) menurunkan risiko hipotermia hingga 70% dan mempercepat kenaikan berat badan BBLR. Bergantianlah antar anggota keluarga.",
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                color = Color(0xFF78350F)
                            )
                        }
                    }

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

    if (uiState.showFinishConfirmDialog) {
        AlertDialog(
            onDismissRequest = onCloseFinishDialog,
            title = {
                Text(
                    text = "Konfirmasi Selesai Sesi PMK",
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            },
            text = {
                Text(
                    text = "Sesi kontak kulit hari ini akan disimpan ke buku riwayat dan statistik medis bayi. Lanjutkan?",
                    color = colors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirmFinish,
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                ) {
                    Text("Ya, Simpan Sesi")
                }
            },
            dismissButton = {
                TextButton(onClick = onCloseFinishDialog) {
                    Text("Batal")
                }
            },
            containerColor = colors.surface
        )
    }
}
