package com.kangurusiaga.app.presentation.pmk.timer

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.zIndex
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.BrandBackground
import com.kangurusiaga.app.core.designsystem.theme.BrandDarkPink
import com.kangurusiaga.app.core.designsystem.theme.BrandLightPink
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.BrandPinkAccent
import com.kangurusiaga.app.core.designsystem.theme.BrandPinkBorder
import com.kangurusiaga.app.core.designsystem.theme.BrandPinkGradientEnd
import com.kangurusiaga.app.core.designsystem.theme.BrandPinkGradientMid
import com.kangurusiaga.app.core.designsystem.theme.BrandPinkGradientStart
import com.kangurusiaga.app.core.designsystem.theme.BrandPinkTrack
import com.kangurusiaga.app.core.designsystem.theme.BrandSoftAmber
import com.kangurusiaga.app.core.designsystem.theme.BrandSoftGreen
import com.kangurusiaga.app.core.designsystem.theme.BrandTextAmber
import com.kangurusiaga.app.core.designsystem.theme.BrandTextGreen
import com.kangurusiaga.app.core.designsystem.theme.TextPrimary
import com.kangurusiaga.app.core.designsystem.theme.TextSecondary
import com.kangurusiaga.app.core.designsystem.theme.TextTertiary
import com.kangurusiaga.app.core.designsystem.theme.White
import com.kangurusiaga.app.domain.model.TimerStatus

/**
 * Screen: Kanguru Siaga - Mulai PMK New (Timer Varian Kasidig)
 * Source of Truth: Google Stitch Design
 * Screen ID: projects/10808370107038581899/screens/e460c98189414387a9b28dfd2e534523
 */
@Composable
fun PmkTimerRoute(
    onNavigateBack: () -> Unit,
    onNavigateToHistory: () -> Unit = {},
    onNavigateToGuide: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: PmkTimerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is PmkTimerUiEvent.SessionCompleted -> {
                    snackbarHostState.showSnackbar("Sesi PMK (${event.session.durationMinutes} menit) berhasil disimpan!")
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
        onStart = viewModel::startTimer,
        onPause = viewModel::pauseTimer,
        onResume = viewModel::resumeTimer,
        onRequestFinish = viewModel::requestFinishSession,
        onOpenTargetDialog = viewModel::openTargetDialog,
        onCloseTargetDialog = viewModel::closeTargetDialog,
        onUpdateTargetMinutes = viewModel::updateTargetMinutes,
        onCloseObservationDialog = viewModel::closeObservationDialog,
        onUpdateObservationInput = viewModel::updateObservationInput,
        onConfirmFinish = viewModel::confirmFinishSession,
        onNotesChanged = viewModel::setNotes,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
fun PmkTimerScreen(
    uiState: PmkTimerUiState,
    onNavigateBack: () -> Unit,
    onNavigateToHistory: () -> Unit = {},
    onNavigateToGuide: () -> Unit = {},
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onRequestFinish: () -> Unit,
    onOpenTargetDialog: () -> Unit,
    onCloseTargetDialog: () -> Unit,
    onUpdateTargetMinutes: (Int) -> Unit,
    onCloseObservationDialog: () -> Unit,
    onUpdateObservationInput: (String, String) -> Unit,
    onConfirmFinish: () -> Unit,
    onNotesChanged: (String) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier
) {
    val timerState = uiState.timerState

    // Theme Color Tokens
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val outlineColor = MaterialTheme.colorScheme.outline

    val headerGradientColors = listOf(
        BrandPinkGradientStart,
        BrandPinkGradientMid,
        BrandPinkGradientEnd
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = headerGradientColors
                )
            )
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    // Top Navigation Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Back Button
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.common_back),
                                tint = onPrimaryColor
                            )
                        }

                        // Screen Title
                        Text(
                            text = stringResource(R.string.pmk_timer_title),
                            fontSize = 17.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = onPrimaryColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )

                        // Riwayat PMK Action Button
                        Surface(
                            onClick = onNavigateToHistory,
                            shape = CircleShape,
                            color = onPrimaryColor.copy(alpha = 0.22f),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = stringResource(R.string.pmk_timer_cd_history),
                                    tint = onPrimaryColor,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            // Main White Card Surface
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
                color = surfaceColor,
                shadowElevation = 10.dp
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Scrollable Main Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp, vertical = 18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(18.dp))

                        // Circular Progress Timer Gauge
                        Box(
                            modifier = Modifier
                                .size(240.dp)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val progress = timerState.progress
                            val sweepAngle = (progress * 360f).coerceIn(0f, 360f)

                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val strokeWidth = 17.dp.toPx()
                                val radius = (size.minDimension - strokeWidth) / 2
                                val center = Offset(size.width / 2, size.height / 2)
                                val topLeft = Offset(center.x - radius, center.y - radius)
                                val arcSize = Size(radius * 2, radius * 2)

                                // Inactive Track Ring
                                drawArc(
                                    color = BrandPinkTrack,
                                    startAngle = 0f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    topLeft = topLeft,
                                    size = arcSize,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )

                                // Active Progress Arc
                                if (sweepAngle > 0f) {
                                    drawArc(
                                        color = primaryColor,
                                        startAngle = -90f,
                                        sweepAngle = sweepAngle,
                                        useCenter = false,
                                        topLeft = topLeft,
                                        size = arcSize,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )
                                }
                            }

                            // Floating Accent Diamond Indicator on Track
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .offset(x = 24.dp, y = 56.dp)
                                    .size(11.dp)
                                    .rotate(45f)
                                    .background(BrandPinkAccent.copy(alpha = 0.85f), RoundedCornerShape(2.dp))
                            )

                            // Central Illustration: Kangaroo Mother Care (PMK)
                            Box(
                                modifier = Modifier
                                    .size(152.dp)
                                    .clip(CircleShape)
                                    .background(BrandLightPink.copy(alpha = 0.5f))
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.il_pmk_timer_center),
                                    contentDescription = "Ibu dan Bayi PMK",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        // Timer Display Numbers
                        Text(
                            text = timerState.formattedTime,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = onSurfaceColor,
                            letterSpacing = (-0.5).sp,
                            modifier = Modifier.padding(top = 16.dp)
                        )

                        // Active Status Indicator Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            when (timerState.status) {
                                TimerStatus.RUNNING -> {
                                    PulsingCoralDot()
                                    Text(
                                        text = stringResource(R.string.pmk_timer_status_running),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = onSurfaceVariant
                                    )
                                }
                                TimerStatus.PAUSED -> {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(BrandTextAmber)
                                    )
                                    Text(
                                        text = stringResource(R.string.pmk_timer_status_paused),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = BrandTextAmber
                                    )
                                }
                                TimerStatus.COMPLETED -> {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(BrandTextGreen)
                                    )
                                    Text(
                                        text = stringResource(R.string.pmk_timer_status_completed),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = BrandTextGreen
                                    )
                                }
                                TimerStatus.IDLE -> {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(TextTertiary)
                                    )
                                    Text(
                                        text = stringResource(R.string.pmk_timer_status_idle),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Session Control Buttons
                        when (timerState.status) {
                            TimerStatus.IDLE -> {
                                Button(
                                    onClick = onStart,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = primaryColor,
                                        contentColor = onPrimaryColor
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(R.string.pmk_timer_btn_start),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            TimerStatus.RUNNING -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    // Button Jeda
                                    Button(
                                        onClick = onPause,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(54.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = primaryContainer,
                                            contentColor = primaryColor
                                        ),
                                        border = BorderStroke(1.dp, BrandPinkBorder),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Pause,
                                            contentDescription = null,
                                            tint = primaryColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = stringResource(R.string.pmk_timer_btn_pause),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = primaryColor
                                        )
                                    }

                                    // Button Selesai
                                    Button(
                                        onClick = onRequestFinish,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(54.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = primaryColor,
                                            contentColor = onPrimaryColor
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(13.dp)
                                                .background(onPrimaryColor, RoundedCornerShape(3.dp))
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = stringResource(R.string.pmk_timer_btn_finish),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            TimerStatus.PAUSED -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    // Button Lanjut
                                    Button(
                                        onClick = onResume,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(54.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = primaryContainer,
                                            contentColor = primaryColor
                                        ),
                                        border = BorderStroke(1.dp, BrandPinkBorder),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = primaryColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = stringResource(R.string.pmk_timer_btn_resume),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = primaryColor
                                        )
                                    }

                                    // Button Selesai
                                    Button(
                                        onClick = onRequestFinish,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(54.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = primaryColor,
                                            contentColor = onPrimaryColor
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(13.dp)
                                                .background(onPrimaryColor, RoundedCornerShape(3.dp))
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = stringResource(R.string.pmk_timer_btn_finish),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            TimerStatus.COMPLETED -> {
                                Button(
                                    onClick = onStart,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = primaryColor,
                                        contentColor = onPrimaryColor
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(R.string.pmk_timer_btn_new_session),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Target Duration Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = surfaceColor),
                            border = BorderStroke(1.dp, BrandPinkBorder.copy(alpha = 0.7f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AccessTime,
                                            contentDescription = null,
                                            tint = primaryColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = stringResource(R.string.pmk_timer_target_label),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = onSurfaceVariant
                                        )
                                        Text(
                                            text = stringResource(R.string.pmk_timer_target_sub),
                                            fontSize = 11.sp,
                                            color = TextTertiary
                                        )
                                    }
                                }

                                Surface(
                                    onClick = onOpenTargetDialog,
                                    shape = RoundedCornerShape(12.dp),
                                    color = primaryContainer,
                                    border = BorderStroke(1.dp, BrandPinkBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "${timerState.targetDurationMinutes}",
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = onSurfaceColor
                                        )
                                        Text(
                                            text = stringResource(R.string.pmk_timer_unit_minutes),
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = primaryColor
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = stringResource(R.string.pmk_timer_cd_edit_duration),
                                            tint = primaryColor,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Session Notes Input
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = stringResource(R.string.pmk_timer_notes_label),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = onSurfaceColor
                                    )
                                    Text(
                                        text = stringResource(R.string.pmk_timer_notes_optional),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = TextTertiary
                                    )
                                }
                                Text(
                                    text = stringResource(R.string.pmk_timer_notes_max_char),
                                    fontSize = 11.sp,
                                    color = TextTertiary
                                )
                            }

                            OutlinedTextField(
                                value = timerState.notes,
                                onValueChange = { if (it.length <= 200) onNotesChanged(it) },
                                placeholder = {
                                    Text(
                                        text = stringResource(R.string.pmk_timer_notes_placeholder),
                                        fontSize = 12.sp,
                                        color = TextTertiary,
                                        lineHeight = 17.sp
                                    )
                                },
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 12.5.sp,
                                    color = onSurfaceColor,
                                    lineHeight = 18.sp
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = primaryColor,
                                    unfocusedBorderColor = BrandPinkBorder.copy(alpha = 0.7f),
                                    focusedContainerColor = surfaceColor,
                                    unfocusedContainerColor = surfaceColor
                                ),
                                minLines = 2,
                                maxLines = 3
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Medical Guideline Tips Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = BrandSoftAmber),
                            border = BorderStroke(1.dp, BrandTextAmber.copy(alpha = 0.25f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(BrandTextAmber.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        tint = BrandTextAmber,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }

                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(
                                            SpanStyle(
                                                fontWeight = FontWeight.Bold,
                                                color = BrandTextAmber
                                            )
                                        ) {
                                            append(stringResource(R.string.pmk_timer_tip_title))
                                        }
                                        withStyle(
                                            SpanStyle(
                                                fontWeight = FontWeight.Normal,
                                                color = onSurfaceColor
                                            )
                                        ) {
                                            append(stringResource(R.string.pmk_timer_tip_desc))
                                        }
                                    },
                                    fontSize = 11.5.sp,
                                    lineHeight = 17.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Panduan Badge Button (Top Right of Card, layered above scrollable content)
                    Surface(
                        onClick = onNavigateToGuide,
                        shape = CircleShape,
                        color = primaryContainer,
                        border = BorderStroke(1.dp, BrandPinkBorder),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 16.dp, end = 20.dp)
                            .zIndex(10f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(13.5.dp)
                            )
                            Text(
                                text = stringResource(R.string.pmk_timer_badge_guide),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = primaryColor
                            )
                        }
                    }
                }
            }
        }
    }

    // Target Duration Picker Dialog
    if (uiState.showTargetDialog) {
        TargetDurationDialog(
            currentTarget = timerState.targetDurationMinutes,
            onSelect = onUpdateTargetMinutes,
            onDismiss = onCloseTargetDialog
        )
    }

    // Observation Input Dialog when finishing session
    if (uiState.showObservationDialog) {
        ObservationDialog(
            temperature = uiState.inputTemperature,
            response = uiState.inputResponse,
            onUpdate = onUpdateObservationInput,
            onConfirm = onConfirmFinish,
            onDismiss = onCloseObservationDialog
        )
    }
}

/**
 * Pulsing coral red dot for "Sedang berlangsung...".
 */
@Composable
private fun PulsingCoralDot() {
    val primaryColor = MaterialTheme.colorScheme.primary
    val transition = rememberInfiniteTransition(label = "pulse_coral")
    val alpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(primaryColor.copy(alpha = alpha))
    )
}

@Composable
private fun TargetDurationDialog(
    currentTarget: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val outlineColor = MaterialTheme.colorScheme.outline
    val backgroundColor = MaterialTheme.colorScheme.background

    val options = listOf(30, 45, 60, 90, 120)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.pmk_timer_target_dialog_title),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = onSurfaceColor
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                options.forEach { minutes ->
                    val isSelected = minutes == currentTarget
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(minutes) },
                        color = if (isSelected) primaryContainer else backgroundColor,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) primaryColor else outlineColor
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$minutes menit",
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) primaryColor else onSurfaceColor
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.pmk_timer_btn_close), color = primaryColor, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = surfaceColor,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun ObservationDialog(
    temperature: String,
    response: String,
    onUpdate: (String, String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val outlineColor = MaterialTheme.colorScheme.outline

    val responses = listOf("Tidur Tenang", "Tenang", "Gelisah", "Menangis")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.pmk_timer_obs_dialog_title),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = onSurfaceColor
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = stringResource(R.string.pmk_timer_obs_dialog_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = onSurfaceVariant
                )

                // Suhu Bayi
                OutlinedTextField(
                    value = temperature,
                    onValueChange = { onUpdate(it, response) },
                    label = { Text(stringResource(R.string.pmk_timer_obs_temp_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        focusedLabelColor = primaryColor,
                        unfocusedBorderColor = outlineColor
                    )
                )

                // Respon Bayi
                Column {
                    Text(
                        text = stringResource(R.string.pmk_timer_obs_response_label),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = onSurfaceColor
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        responses.take(2).forEach { item ->
                            val isSelected = item == response
                            FilterChip(
                                selected = isSelected,
                                onClick = { onUpdate(temperature, item) },
                                label = { Text(item, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = primaryContainer,
                                    selectedLabelColor = primaryColor
                                )
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        responses.drop(2).forEach { item ->
                            val isSelected = item == response
                            FilterChip(
                                selected = isSelected,
                                onClick = { onUpdate(temperature, item) },
                                label = { Text(item, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = primaryContainer,
                                    selectedLabelColor = primaryColor
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    contentColor = onPrimaryColor
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.pmk_timer_btn_save_session), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.alarm_btn_cancel), color = onSurfaceVariant)
            }
        },
        containerColor = surfaceColor,
        shape = RoundedCornerShape(24.dp)
    )
}
