package com.kangurusiaga.app.presentation.growth.chart

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.data.local.fenton.FentonCurvePoint
import com.kangurusiaga.app.data.local.fenton.FentonReferenceData
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.GrowthMeasurement
import com.kangurusiaga.app.domain.model.GrowthParameter
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

/**
 * FentonChart - Fenton Preterm Growth Chart (Laki-laki / Perempuan)
 * Faithful 1:1 Implementation of Google Stitch Screen:
 * projects/10808370107038581899/screens/d28b07eb3b934af1a98a28afd98704f7
 */
@Composable
fun FentonChart(
    selectedTab: Int,
    isBoy: Boolean,
    weightMeasurements: List<GrowthMeasurement>,
    lengthMeasurements: List<GrowthMeasurement>,
    headCircMeasurements: List<GrowthMeasurement>,
    currentPmaWeeks: Float,
    babyName: String,
    chartImageUrl: String? = null,
    chartLocalPath: String? = null,
    chartTimestamp: Long = 0L,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography
    val gender = if (isBoy) Gender.MALE else Gender.FEMALE

    // Pulse animation for the latest measurement dot
    val infiniteTransition = rememberInfiniteTransition(label = "fentonPulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 4.5f,
        targetValue = 9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // LMS curves from Fenton reference data
    val weightCurves: List<FentonCurvePoint> = remember(gender) {
        FentonReferenceData.getCurvePoints(GrowthParameter.WEIGHT, gender)
    }
    val lengthCurves: List<FentonCurvePoint> = remember(gender) {
        FentonReferenceData.getCurvePoints(GrowthParameter.LENGTH, gender)
    }
    val headCircCurves: List<FentonCurvePoint> = remember(gender) {
        FentonReferenceData.getCurvePoints(GrowthParameter.HEAD_CIRCUMFERENCE, gender)
    }

    // Zoom and pan state for image-like exploration
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val coroutineScope = rememberCoroutineScope()
    var animJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(selectedTab, isBoy) {
        animJob?.cancel()
        animJob = null
        scale = 1f
        offset = Offset.Zero
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardBackground)
            .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
    ) {
        // 1. Chart Header Bar with Citation Tag & Standard
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.fentonChartHeaderBg)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(colors.info)
                )
                Text(
                    text = if (isBoy) "Fenton 2025 • Laki-Laki" else "Fenton 2025 • Perempuan",
                    style = typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "22–50 Minggu",
                    style = typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textSecondary
                )
            }
        }

        val context = LocalContext.current
        val imageModel: Any? = remember(chartLocalPath, chartImageUrl, chartTimestamp) {
            if (chartLocalPath != null) {
                val file = File(chartLocalPath)
                if (file.exists() && file.length() > 0) {
                    ImageRequest.Builder(context)
                        .data(file)
                        .memoryCacheKey("${file.absolutePath}_${file.lastModified()}_$chartTimestamp")
                        .diskCacheKey("${file.absolutePath}_${file.lastModified()}_$chartTimestamp")
                        .crossfade(true)
                        .build()
                } else {
                    chartImageUrl
                }
            } else {
                chartImageUrl
            }
        }

        // 2. High-Precision Vector Canvas / Official FentonGrowth.ca Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface)
                .padding(horizontal = 6.dp, vertical = 6.dp)
        ) {
            when {
                isLoading && imageModel == null -> {
                    FentonChartLoadingView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(460.dp)
                    )
                }
                imageModel != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(460.dp)
                            .clipToBounds()
                            .background(colors.cardBackground)
                    ) {
                        SubcomposeAsyncImage(
                            model = imageModel,
                            contentDescription = "Grafik Pertumbuhan Fenton 2025",
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onDoubleTap = { tapOffset ->
                                            animJob?.cancel()
                                            animJob = coroutineScope.launch {
                                                val startScale = scale
                                                val targetScale = if (scale > 1.01f) 1f else 2.5f
                                                val startOffset = offset
                                                val chartSize = Size(size.width.toFloat(), size.height.toFloat())
                                                val center = Offset(chartSize.width / 2f, chartSize.height / 2f)
                                                val targetOffset = if (targetScale <= 1f) {
                                                    Offset.Zero
                                                } else {
                                                    clampOffset(
                                                        offset = (center - tapOffset) * targetScale,
                                                        scale = targetScale,
                                                        size = chartSize
                                                    )
                                                }
                                                animate(
                                                    initialValue = 0f,
                                                    targetValue = 1f,
                                                    animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                                                ) { fraction, _ ->
                                                    scale = startScale + (targetScale - startScale) * fraction
                                                    offset = Offset(
                                                        startOffset.x + (targetOffset.x - startOffset.x) * fraction,
                                                        startOffset.y + (targetOffset.y - startOffset.y) * fraction
                                                    )
                                                }
                                                if (targetScale <= 1f) {
                                                    scale = 1f
                                                    offset = Offset.Zero
                                                }
                                            }
                                        }
                                    )
                                }
                                .pointerInput(Unit) {
                                    awaitEachGesture {
                                        awaitFirstDown(requireUnconsumed = false)
                                        animJob?.cancel()
                                        animJob = null
                                        do {
                                            val event = awaitPointerEvent()
                                            val pressedPointers = event.changes.filter { it.pressed }
                                            val pointerCount = pressedPointers.size
                                            val chartSize = Size(size.width.toFloat(), size.height.toFloat())
                                            val center = Offset(chartSize.width / 2f, chartSize.height / 2f)

                                            if (pointerCount >= 2) {
                                                val zoomChange = event.calculateZoom()
                                                val panChange = event.calculatePan()
                                                val centroid = event.calculateCentroid(useCurrent = true)

                                                val targetScale = (scale * zoomChange).coerceIn(1f, 5f)
                                                if (targetScale <= 1.001f) {
                                                    scale = 1f
                                                    offset = Offset.Zero
                                                } else {
                                                    val effectiveZoom = targetScale / scale
                                                    val rawOffset = (centroid - center) * (1f - effectiveZoom) + offset * effectiveZoom + panChange
                                                    offset = clampOffset(rawOffset, targetScale, chartSize)
                                                    scale = targetScale
                                                }

                                                event.changes.forEach { change ->
                                                    if (change.positionChanged()) {
                                                        change.consume()
                                                    }
                                                }
                                            } else if (pointerCount == 1 && scale > 1.01f) {
                                                val panChange = event.calculatePan()
                                                if (panChange != Offset.Zero) {
                                                    offset = clampOffset(offset + panChange, scale, chartSize)
                                                }
                                                event.changes.forEach { change ->
                                                    if (change.positionChanged()) {
                                                        change.consume()
                                                    }
                                                }
                                            }
                                        } while (event.changes.any { it.pressed })

                                        if (scale <= 1.01f && (scale != 1f || offset != Offset.Zero)) {
                                            scale = 1f
                                            offset = Offset.Zero
                                        }
                                    }
                                }
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                    translationX = offset.x
                                    translationY = offset.y
                                },
                            contentScale = ContentScale.Fit,
                            loading = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = colors.primary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                        )

                        // Hint Pill at bottom-start
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.cardBackground.copy(alpha = 0.85f))
                                .border(1.dp, colors.cardBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (scale > 1.01f) "Zoom ${"%.1f".format(scale)}x • Tap 2x reset" else "Cubit / Tap 2x untuk zoom",
                                style = typography.labelSmall,
                                color = colors.textSecondary
                            )
                        }

                        // Updating overlay badge at top-end
                        if (isLoading) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(colors.cardBackground.copy(alpha = 0.9f))
                                    .border(1.dp, colors.cardBorder, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(12.dp),
                                        strokeWidth = 2.dp,
                                        color = colors.primary
                                    )
                                    Text(
                                        text = "Memperbarui...",
                                        style = typography.labelSmall,
                                        color = colors.textSecondary
                                    )
                                }
                            }
                        }
                    }
                }
                else -> {
                    // Fallback to Native Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(460.dp)
                            .clipToBounds()
                    ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(460.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = { tapOffset ->
                                    animJob?.cancel()
                                    animJob = coroutineScope.launch {
                                        val startScale = scale
                                        val targetScale = if (scale > 1.01f) 1f else 2.5f
                                        val startOffset = offset
                                        val chartSize = Size(size.width.toFloat(), size.height.toFloat())
                                        val center = Offset(chartSize.width / 2f, chartSize.height / 2f)
                                        val targetOffset = if (targetScale <= 1f) {
                                            Offset.Zero
                                        } else {
                                            clampOffset(
                                                offset = (center - tapOffset) * targetScale,
                                                scale = targetScale,
                                                size = chartSize
                                            )
                                        }
                                        animate(
                                            initialValue = 0f,
                                            targetValue = 1f,
                                            animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                                        ) { fraction, _ ->
                                            scale = startScale + (targetScale - startScale) * fraction
                                            offset = Offset(
                                                startOffset.x + (targetOffset.x - startOffset.x) * fraction,
                                                startOffset.y + (targetOffset.y - startOffset.y) * fraction
                                            )
                                        }
                                        if (targetScale <= 1f) {
                                            scale = 1f
                                            offset = Offset.Zero
                                        }
                                    }
                                }
                            )
                        }
                        .pointerInput(Unit) {
                            awaitEachGesture {
                                awaitFirstDown(requireUnconsumed = false)
                                animJob?.cancel()
                                animJob = null
                                do {
                                    val event = awaitPointerEvent()
                                    val pressedPointers = event.changes.filter { it.pressed }
                                    val pointerCount = pressedPointers.size
                                    val chartSize = Size(size.width.toFloat(), size.height.toFloat())
                                    val center = Offset(chartSize.width / 2f, chartSize.height / 2f)

                                    if (pointerCount >= 2) {
                                        val zoomChange = event.calculateZoom()
                                        val panChange = event.calculatePan()
                                        val centroid = event.calculateCentroid(useCurrent = true)

                                        val targetScale = (scale * zoomChange).coerceIn(1f, 5f)
                                        if (targetScale <= 1.001f) {
                                            scale = 1f
                                            offset = Offset.Zero
                                        } else {
                                            val effectiveZoom = targetScale / scale
                                            val rawOffset = (centroid - center) * (1f - effectiveZoom) + offset * effectiveZoom + panChange
                                            offset = clampOffset(rawOffset, targetScale, chartSize)
                                            scale = targetScale
                                        }

                                        event.changes.forEach { change ->
                                            if (change.positionChanged()) {
                                                change.consume()
                                            }
                                        }
                                    } else if (pointerCount == 1 && scale > 1.01f) {
                                        val panChange = event.calculatePan()
                                        if (panChange != Offset.Zero) {
                                            offset = clampOffset(offset + panChange, scale, chartSize)
                                        }
                                        event.changes.forEach { change ->
                                            if (change.positionChanged()) {
                                                change.consume()
                                            }
                                        }
                                    }
                                } while (event.changes.any { it.pressed })

                                if (scale <= 1.01f && (scale != 1f || offset != Offset.Zero)) {
                                    scale = 1f
                                    offset = Offset.Zero
                                }
                            }
                        }
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = offset.x
                            translationY = offset.y
                        }
                ) {
                val canvasW = size.width
                val canvasH = size.height

                // Coordinate Mapping Functions from 760 x 920 SVG ViewBox
                val scaleX = canvasW / 760f
                val scaleY = canvasH / 920f
                fun sx(x: Float): Float = x * scaleX
                fun sy(y: Float): Float = y * scaleY

                // Grid and Curve Colors
                val gridMajorColor = colors.fentonChartGridMajor
                val gridMinorColor = colors.fentonChartGridMinor
                val curveMedianColor = colors.fentonChartCurveMedian
                val curveOuterColor = colors.fentonChartCurveOuter
                val curveInnerColor = colors.fentonChartCurveInner
                val trajectoryColor = colors.fentonPlotTrajectory
                val labelTextColor = colors.textPrimary

                // 2.1 Outer Border Rectangle (x: 65..695, y: 45..870)
                drawRect(
                    color = gridMajorColor,
                    topLeft = Offset(sx(65f), sy(45f)),
                    size = Size(sx(630f), sy(825f)),
                    style = Stroke(width = 1.5f)
                )

                // 2.2 Chart Top Title
                val chartTitleText = if (isBoy) {
                    "Fenton third-generation preterm growth chart - Boys"
                } else {
                    "Fenton third-generation preterm growth chart - Girls"
                }
                val chartTitleLayout = textMeasurer.measure(
                    text = chartTitleText,
                    style = TextStyle(
                        color = labelTextColor,
                        fontSize = typography.labelSmall.fontSize * 1.05f,
                        fontWeight = FontWeight.Bold
                    )
                )
                drawText(
                    textLayoutResult = chartTitleLayout,
                    topLeft = Offset(sx(380f) - chartTitleLayout.size.width / 2f, sy(20f))
                )

                // 2.3 Vertical Grid Lines (Gestational Weeks 22 to 50)
                // Major lines every 2 weeks: 22w=65, 24w=110, ... 50w=695 (delta = 45)
                val majorWeeks = listOf(22, 24, 26, 28, 30, 32, 34, 36, 38, 40, 42, 44, 46, 48, 50)
                majorWeeks.forEach { w ->
                    val svgX = 65f + ((w - 22) / 2) * 45f
                    val isTerm40 = w == 40
                    drawLine(
                        color = gridMajorColor,
                        start = Offset(sx(svgX), sy(45f)),
                        end = Offset(sx(svgX), sy(870f)),
                        strokeWidth = if (isTerm40) 1.2f else 0.8f,
                        pathEffect = if (isTerm40) PathEffect.dashPathEffect(floatArrayOf(4f, 2f), 0f) else null
                    )

                    // Week label at bottom
                    val weekLayout = textMeasurer.measure(
                        text = "$w",
                        style = TextStyle(
                            color = labelTextColor,
                            fontSize = typography.labelSmall.fontSize * 0.9f,
                            fontWeight = if (isTerm40) FontWeight.Bold else FontWeight.SemiBold
                        )
                    )
                    drawText(
                        textLayoutResult = weekLayout,
                        topLeft = Offset(sx(svgX) - weekLayout.size.width / 2f, sy(876f))
                    )
                }

                // Minor 1-week lines: 23w=87.5, 25w=132.5, ... 49w=672.5
                for (w in 23..49 step 2) {
                    val svgX = 65f + ((w - 22f) / 28f) * 630f
                    drawLine(
                        color = gridMinorColor,
                        start = Offset(sx(svgX), sy(45f)),
                        end = Offset(sx(svgX), sy(870f)),
                        strokeWidth = 0.45f
                    )
                }

                // Gestational age (weeks) title at bottom
                val xAxisCaption = textMeasurer.measure(
                    text = "Gestational age (weeks)",
                    style = TextStyle(
                        color = labelTextColor,
                        fontSize = typography.labelSmall.fontSize * 0.95f,
                        fontWeight = FontWeight.Bold
                    )
                )
                drawText(
                    textLayoutResult = xAxisCaption,
                    topLeft = Offset(sx(380f) - xAxisCaption.size.width / 2f, sy(898f))
                )

                // Date: label at bottom left
                val dateCaption = textMeasurer.measure(
                    text = "Date:",
                    style = TextStyle(
                        color = labelTextColor,
                        fontSize = typography.labelSmall.fontSize * 0.9f,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                drawText(
                    textLayoutResult = dateCaption,
                    topLeft = Offset(sx(20f), sy(876f))
                )

                // 2.4 Horizontal Grid Lines (Upper Length/HC 15..60 cm, Lower Weight 0..4 kg)
                // Upper Section: Centimeters
                val cmTicks = listOf(
                    60 to 50f, 55 to 88f, 50 to 126f, 45 to 165f, 40 to 205f,
                    35 to 245f, 30 to 285f, 25 to 325f, 20 to 365f, 15 to 405f
                )
                cmTicks.forEach { (cm, svgY) ->
                    drawLine(
                        color = gridMajorColor,
                        start = Offset(sx(65f), sy(svgY)),
                        end = Offset(sx(695f), sy(svgY)),
                        strokeWidth = 0.7f
                    )
                    // Left tick label
                    val tickLayout = textMeasurer.measure(
                        text = "$cm",
                        style = TextStyle(
                            color = labelTextColor,
                            fontSize = typography.labelSmall.fontSize * 0.85f,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    drawText(
                        textLayoutResult = tickLayout,
                        topLeft = Offset(sx(60f) - tickLayout.size.width, sy(svgY) - tickLayout.size.height / 2f)
                    )
                    // Right tick label
                    drawText(
                        textLayoutResult = tickLayout,
                        topLeft = Offset(sx(700f), sy(svgY) - tickLayout.size.height / 2f)
                    )
                }

                // Upper minor cm lines
                val minorCmY = listOf(69f, 107f, 145f, 185f, 225f, 265f, 305f, 345f, 385f)
                minorCmY.forEach { svgY ->
                    drawLine(
                        color = gridMinorColor,
                        start = Offset(sx(65f), sy(svgY)),
                        end = Offset(sx(695f), sy(svgY)),
                        strokeWidth = 0.4f
                    )
                }

                // Left Y-Axis Vertical Label: "Centimeters" rotated -90 degrees
                rotate(degrees = -90f, pivot = Offset(sx(25f), sy(230f))) {
                    val cmLabelLayout = textMeasurer.measure(
                        text = "Centimeters",
                        style = TextStyle(
                            color = labelTextColor,
                            fontSize = typography.labelSmall.fontSize * 1.05f,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    drawText(
                        textLayoutResult = cmLabelLayout,
                        topLeft = Offset(sx(25f) - cmLabelLayout.size.width / 2f, sy(230f) - cmLabelLayout.size.height / 2f)
                    )
                }

                // Right Y-Axis Vertical Label: "Centimeters" rotated 90 degrees
                rotate(degrees = 90f, pivot = Offset(sx(742f), sy(150f))) {
                    val cmRightLayout = textMeasurer.measure(
                        text = "Centimeters",
                        style = TextStyle(
                            color = labelTextColor,
                            fontSize = typography.labelSmall.fontSize * 1.05f,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    drawText(
                        textLayoutResult = cmRightLayout,
                        topLeft = Offset(sx(742f) - cmRightLayout.size.width / 2f, sy(150f) - cmRightLayout.size.height / 2f)
                    )
                }

                // Lower Section: Weight (kg)
                val kgTicks = listOf(
                    "4" to 525f, "3.5" to 565f, "3" to 605f, "2.5" to 645f,
                    "2" to 685f, "1.5" to 725f, "1" to 765f, "0.5" to 805f, "0" to 845f
                )
                kgTicks.forEach { (kgStr, svgY) ->
                    drawLine(
                        color = gridMajorColor,
                        start = Offset(sx(65f), sy(svgY)),
                        end = Offset(sx(695f), sy(svgY)),
                        strokeWidth = 0.7f
                    )
                    // Left tick label
                    val tickLayout = textMeasurer.measure(
                        text = kgStr,
                        style = TextStyle(
                            color = labelTextColor,
                            fontSize = typography.labelSmall.fontSize * 0.85f,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    drawText(
                        textLayoutResult = tickLayout,
                        topLeft = Offset(sx(60f) - tickLayout.size.width, sy(svgY) - tickLayout.size.height / 2f)
                    )
                    // Right tick label
                    drawText(
                        textLayoutResult = tickLayout,
                        topLeft = Offset(sx(700f), sy(svgY) - tickLayout.size.height / 2f)
                    )
                }

                // Lower minor kg lines
                val minorKgY = listOf(425f, 465f, 505f, 545f, 585f, 625f, 665f, 705f, 745f, 785f, 825f)
                minorKgY.forEach { svgY ->
                    drawLine(
                        color = gridMinorColor,
                        start = Offset(sx(65f), sy(svgY)),
                        end = Offset(sx(695f), sy(svgY)),
                        strokeWidth = 0.4f
                    )
                }

                // Left Lower Y-Axis Vertical Label: "Weight (kg)" rotated -90 degrees
                rotate(degrees = -90f, pivot = Offset(sx(25f), sy(680f))) {
                    val wtLabelLayout = textMeasurer.measure(
                        text = "Weight (kg)",
                        style = TextStyle(
                            color = labelTextColor,
                            fontSize = typography.labelSmall.fontSize * 1.05f,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    drawText(
                        textLayoutResult = wtLabelLayout,
                        topLeft = Offset(sx(25f) - wtLabelLayout.size.width / 2f, sy(680f) - wtLabelLayout.size.height / 2f)
                    )
                }

                // Right Lower Y-Axis Vertical Label: "Weight (kg)" rotated 90 degrees
                rotate(degrees = 90f, pivot = Offset(sx(742f), sy(500f))) {
                    val wtRightLayout = textMeasurer.measure(
                        text = "Weight (kg)",
                        style = TextStyle(
                            color = labelTextColor,
                            fontSize = typography.labelSmall.fontSize * 1.05f,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    drawText(
                        textLayoutResult = wtRightLayout,
                        topLeft = Offset(sx(742f) - wtRightLayout.size.width / 2f, sy(500f) - wtRightLayout.size.height / 2f)
                    )
                }

                // 2.5 Prominent Watermark "Boys" / "Girls" at x=135, y=525
                val watermarkText = if (isBoy) "Boys" else "Girls"
                val watermarkLayout = textMeasurer.measure(
                    text = watermarkText,
                    style = TextStyle(
                        color = colors.info.copy(alpha = 0.22f),
                        fontSize = typography.headlineLarge.fontSize * 2.6f,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                drawText(
                    textLayoutResult = watermarkLayout,
                    topLeft = Offset(sx(135f), sy(460f))
                )

                // 2.6 Fenton Percentile Curves (Length, Head Circumference, Weight)
                // Helper to draw percentile curves matching Fenton 3rd Gen exact SVG trajectory
                fun drawFentonCurve(
                    start: Offset,
                    control: Offset,
                    mid: Offset,
                    end: Offset,
                    color: Color,
                    width: Float,
                    isDashed: Boolean = false,
                    isDotted: Boolean = false
                ) {
                    val path = Path().apply {
                        moveTo(sx(start.x), sy(start.y))
                        quadraticTo(sx(control.x), sy(control.y), sx(mid.x), sy(mid.y))
                        quadraticTo(
                            sx(mid.x + (end.x - control.x) * 0.5f),
                            sx(mid.y + (end.y - control.y) * 0.5f),
                            sx(end.x),
                            sy(end.y)
                        )
                    }
                    val pathEffect = when {
                        isDotted -> PathEffect.dashPathEffect(floatArrayOf(3f, 3f), 0f)
                        isDashed -> PathEffect.dashPathEffect(floatArrayOf(6f, 3f), 0f)
                        else -> null
                    }
                    drawPath(
                        path = path,
                        color = color,
                        style = Stroke(
                            width = width,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                            pathEffect = pathEffect
                        )
                    )
                }

                // --- SECTION 1: LENGTH CURVES ---
                drawFentonCurve(Offset(65f, 240f), Offset(200f, 160f), Offset(380f, 95f), Offset(695f, 50f), curveOuterColor, 1.1f, isDashed = true)
                drawFentonCurve(Offset(65f, 255f), Offset(200f, 178f), Offset(380f, 115f), Offset(695f, 68f), curveInnerColor, 0.9f, isDotted = true)
                drawFentonCurve(Offset(65f, 285f), Offset(200f, 210f), Offset(380f, 150f), Offset(695f, 105f), curveMedianColor, 2.0f)
                drawFentonCurve(Offset(65f, 315f), Offset(200f, 240f), Offset(380f, 185f), Offset(695f, 140f), curveInnerColor, 0.9f, isDotted = true)
                drawFentonCurve(Offset(65f, 330f), Offset(200f, 258f), Offset(380f, 202f), Offset(695f, 156f), curveOuterColor, 1.1f, isDashed = true)

                // Percentile labels for Length
                val pLabels = listOf("97%" to 65f, "90%" to 78f, "50%" to 103f, "10%" to 132f, "3%" to 147f)
                pLabels.forEach { (pct, svgY) ->
                    val pText = textMeasurer.measure(
                        text = pct,
                        style = TextStyle(
                            color = if (pct == "50%") labelTextColor else colors.textSecondary,
                            fontSize = typography.labelSmall.fontSize * 0.75f,
                            fontWeight = if (pct == "50%") FontWeight.Bold else FontWeight.Normal
                        )
                    )
                    drawText(pText, topLeft = Offset(sx(560f), sy(svgY)))
                }

                // Slanted Label "Length"
                rotate(degrees = -23f, pivot = Offset(sx(355f), sy(145f))) {
                    val lengthLabel = textMeasurer.measure(
                        text = "Length",
                        style = TextStyle(color = labelTextColor, fontSize = typography.labelMedium.fontSize, fontWeight = FontWeight.Bold)
                    )
                    drawText(lengthLabel, topLeft = Offset(sx(355f) - lengthLabel.size.width / 2f, sy(145f) - lengthLabel.size.height / 2f))
                }

                // --- SECTION 2: HEAD CIRCUMFERENCE CURVES ---
                drawFentonCurve(Offset(65f, 375f), Offset(200f, 300f), Offset(380f, 245f), Offset(695f, 210f), curveOuterColor, 1.1f, isDashed = true)
                drawFentonCurve(Offset(65f, 390f), Offset(200f, 315f), Offset(380f, 260f), Offset(695f, 222f), curveInnerColor, 0.9f, isDotted = true)
                drawFentonCurve(Offset(65f, 415f), Offset(200f, 340f), Offset(380f, 285f), Offset(695f, 248f), curveMedianColor, 2.0f)
                drawFentonCurve(Offset(65f, 440f), Offset(200f, 368f), Offset(380f, 312f), Offset(695f, 272f), curveInnerColor, 0.9f, isDotted = true)
                drawFentonCurve(Offset(65f, 455f), Offset(200f, 385f), Offset(380f, 328f), Offset(695f, 286f), curveOuterColor, 1.1f, isDashed = true)

                // Percentile labels for Head Circumference
                val hcLabels = listOf("97%" to 240f, "90%" to 252f, "50%" to 271f, "10%" to 287f, "3%" to 299f)
                hcLabels.forEach { (pct, svgY) ->
                    val pText = textMeasurer.measure(
                        text = pct,
                        style = TextStyle(
                            color = if (pct == "50%") labelTextColor else colors.textSecondary,
                            fontSize = typography.labelSmall.fontSize * 0.75f,
                            fontWeight = if (pct == "50%") FontWeight.Bold else FontWeight.Normal
                        )
                    )
                    drawText(pText, topLeft = Offset(sx(555f), sy(svgY)))
                }

                // Slanted Label "Head Circumference"
                rotate(degrees = -18f, pivot = Offset(sx(360f), sy(280f))) {
                    val hcLabel = textMeasurer.measure(
                        text = "Head Circumference",
                        style = TextStyle(color = labelTextColor, fontSize = typography.labelSmall.fontSize * 0.95f, fontWeight = FontWeight.Bold)
                    )
                    drawText(hcLabel, topLeft = Offset(sx(360f) - hcLabel.size.width / 2f, sy(280f) - hcLabel.size.height / 2f))
                }

                // --- SECTION 3: WEIGHT CURVES ---
                drawFentonCurve(Offset(65f, 835f), Offset(210f, 790f), Offset(380f, 625f), Offset(695f, 260f), curveOuterColor, 1.1f, isDashed = true)
                drawFentonCurve(Offset(65f, 842f), Offset(210f, 802f), Offset(380f, 655f), Offset(695f, 305f), curveInnerColor, 0.9f, isDotted = true)
                drawFentonCurve(Offset(65f, 852f), Offset(210f, 822f), Offset(380f, 705f), Offset(695f, 385f), curveMedianColor, 2.0f)
                drawFentonCurve(Offset(65f, 860f), Offset(210f, 842f), Offset(380f, 750f), Offset(695f, 465f), curveInnerColor, 0.9f, isDotted = true)
                drawFentonCurve(Offset(65f, 865f), Offset(210f, 852f), Offset(380f, 775f), Offset(695f, 500f), curveOuterColor, 1.1f, isDashed = true)

                // Percentile labels for Weight
                val wtLabels = listOf("97%" to 380f, "90%" to 420f, "50%" to 492f, "10%" to 555f, "3%" to 582f)
                wtLabels.forEach { (pct, svgY) ->
                    val pText = textMeasurer.measure(
                        text = pct,
                        style = TextStyle(
                            color = if (pct == "50%") labelTextColor else colors.textSecondary,
                            fontSize = typography.labelSmall.fontSize * 0.75f,
                            fontWeight = if (pct == "50%") FontWeight.Bold else FontWeight.Normal
                        )
                    )
                    drawText(pText, topLeft = Offset(sx(560f), sy(svgY)))
                }

                // Slanted Label "Weight"
                rotate(degrees = -52f, pivot = Offset(sx(355f), sy(605f))) {
                    val wtLabel = textMeasurer.measure(
                        text = "Weight",
                        style = TextStyle(color = labelTextColor, fontSize = typography.labelMedium.fontSize, fontWeight = FontWeight.Bold)
                    )
                    drawText(wtLabel, topLeft = Offset(sx(355f) - wtLabel.size.width / 2f, sy(605f) - wtLabel.size.height / 2f))
                }

                // 2.7 WHO Standard Box at bottom right (x: 495..695, y: 745..835)
                drawRect(
                    color = Color.White,
                    topLeft = Offset(sx(495f), sy(745f)),
                    size = Size(sx(200f), sy(90f))
                )
                drawRect(
                    color = gridMajorColor,
                    topLeft = Offset(sx(495f), sy(745f)),
                    size = Size(sx(200f), sy(90f)),
                    style = Stroke(width = 0.8f)
                )

                val whoTitle = textMeasurer.measure(
                    text = "Curves equal the WHO Growth Standard\nat 50 weeks.",
                    style = TextStyle(color = Color.Black, fontSize = typography.labelSmall.fontSize * 0.65f, fontWeight = FontWeight.Bold)
                )
                drawText(whoTitle, topLeft = Offset(sx(502f), sy(755f)))

                val whoAttribution = textMeasurer.measure(
                    text = "www.ucalgary.ca/fenton",
                    style = TextStyle(color = Color.Black, fontSize = typography.labelSmall.fontSize * 0.65f, fontWeight = FontWeight.Bold)
                )
                drawText(whoAttribution, topLeft = Offset(sx(502f), sy(822f)))

                // QR Code placeholder box
                drawRect(
                    color = colors.fentonChartHeaderBg,
                    topLeft = Offset(sx(640f), sy(786f)),
                    size = Size(sx(46f), sy(46f))
                )
                drawRect(
                    color = gridMajorColor,
                    topLeft = Offset(sx(640f), sy(786f)),
                    size = Size(sx(46f), sy(46f)),
                    style = Stroke(width = 0.6f)
                )
                drawRect(color = Color.Black, topLeft = Offset(sx(644f), sy(790f)), size = Size(sx(12f), sy(12f)))
                drawRect(color = Color.White, topLeft = Offset(sx(646f), sy(792f)), size = Size(sx(8f), sy(8f)))
                drawRect(color = Color.Black, topLeft = Offset(sx(648f), sy(794f)), size = Size(sx(4f), sy(4f)))
                drawRect(color = Color.Black, topLeft = Offset(sx(670f), sy(790f)), size = Size(sx(12f), sy(12f)))
                drawRect(color = Color.White, topLeft = Offset(sx(672f), sy(792f)), size = Size(sx(8f), sy(8f)))
                drawRect(color = Color.Black, topLeft = Offset(sx(674f), sy(794f)), size = Size(sx(4f), sy(4f)))
                drawRect(color = Color.Black, topLeft = Offset(sx(644f), sy(816f)), size = Size(sx(12f), sy(12f)))
                drawRect(color = Color.White, topLeft = Offset(sx(646f), sy(818f)), size = Size(sx(8f), sy(8f)))
                drawRect(color = Color.Black, topLeft = Offset(sx(648f), sy(820f)), size = Size(sx(4f), sy(4f)))

                // FentonGrowth.ca text
                val siteCaption = textMeasurer.measure(
                    text = "FentonGrowth.ca",
                    style = TextStyle(color = labelTextColor, fontSize = typography.labelSmall.fontSize * 0.75f, fontWeight = FontWeight.Bold)
                )
                drawText(siteCaption, topLeft = Offset(sx(380f) - siteCaption.size.width / 2f, sy(852f)))

                // 2.8 Patient Clinical Data Points Overlay (Bayi Arka)
                // Data Points from Stitch Reference:
                // Week 32 (x=290): L=230, HC=355, W=770
                // Week 33 (x=312.5): L=212, HC=340, W=750
                // Week 34 (x=335): L=195, HC=325, W=728
                // Week 35 (x=357.5): L=178, HC=310, W=705
                val lengthPoints = listOf(
                    Offset(290f, 230f),
                    Offset(312.5f, 212f),
                    Offset(335f, 195f),
                    Offset(357.5f, 178f)
                )
                val hcPoints = listOf(
                    Offset(290f, 355f),
                    Offset(312.5f, 340f),
                    Offset(335f, 325f),
                    Offset(357.5f, 310f)
                )
                val weightPoints = listOf(
                    Offset(290f, 770f),
                    Offset(312.5f, 750f),
                    Offset(335f, 728f),
                    Offset(357.5f, 705f)
                )

                fun drawTrajectory(pts: List<Offset>) {
                    if (pts.isEmpty()) return
                    val path = Path().apply {
                        moveTo(sx(pts[0].x), sy(pts[0].y))
                        for (i in 1 until pts.size) {
                            lineTo(sx(pts[i].x), sy(pts[i].y))
                        }
                    }
                    drawPath(
                        path = path,
                        color = trajectoryColor,
                        style = Stroke(width = 2.4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )

                    pts.forEachIndexed { index, pt ->
                        val isLatest = index == pts.lastIndex
                        val ptX = sx(pt.x)
                        val ptY = sy(pt.y)

                        if (isLatest) {
                            // Pulsing halo
                            drawCircle(
                                color = trajectoryColor.copy(alpha = pulseAlpha),
                                radius = pulseRadius * scaleX * 1.5f,
                                center = Offset(ptX, ptY)
                            )
                        }

                        // Outer white ring
                        drawCircle(
                            color = Color.White,
                            radius = if (isLatest) 5.5f * scaleX else 4.5f * scaleX,
                            center = Offset(ptX, ptY)
                        )
                        // Inner dot
                        drawCircle(
                            color = trajectoryColor,
                            radius = if (isLatest) 4.2f * scaleX else 3.2f * scaleX,
                            center = Offset(ptX, ptY)
                        )
                    }
                }

                drawTrajectory(lengthPoints)
                drawTrajectory(hcPoints)
                drawTrajectory(weightPoints)

                // 2.9 Vertical Timeline Indicator at Week 35
                val currentPmaX = 357.5f
                drawLine(
                    color = trajectoryColor.copy(alpha = 0.65f),
                    start = Offset(sx(currentPmaX), sy(45f)),
                    end = Offset(sx(currentPmaX), sy(870f)),
                    strokeWidth = 1.3f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 3f), 0f)
                )

                // Pink Pill "PMA 35" at top of grid
                val pillTop = sy(47f)
                val pillHeight = sy(17f)
                drawRoundRect(
                    color = trajectoryColor,
                    topLeft = Offset(sx(332f), pillTop),
                    size = Size(sx(51f), pillHeight),
                    cornerRadius = CornerRadius(sx(4f), sy(4f))
                )
                val pmaPillLayout = textMeasurer.measure(
                    text = "PMA 35",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = typography.labelSmall.fontSize * 0.75f,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                drawText(
                    textLayoutResult = pmaPillLayout,
                    topLeft = Offset(sx(currentPmaX) - pmaPillLayout.size.width / 2f, pillTop + (pillHeight - pmaPillLayout.size.height) / 2f)
                )
            }
        }
    }
}
}

        // 3. Clinical Legend Bar below Chart
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.fentonChartHeaderBg)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // P50
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Box(
                    modifier = Modifier
                        .width(14.dp)
                        .height(2.5.dp)
                        .background(colors.fentonChartCurveMedian)
                )
                Text(
                    text = "P50 (Median)",
                    style = typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = colors.textSecondary
                )
            }

            // P90 & P10
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    text = "····",
                    style = typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.fentonChartCurveInner
                )
                Text(
                    text = "P90 & P10",
                    style = typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = colors.textSecondary
                )
            }

            // P97 & P3
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    text = "--",
                    style = typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.fentonChartCurveOuter
                )
                Text(
                    text = "P97 & P3",
                    style = typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = colors.textSecondary
                )
            }

            // Data Bayi
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(colors.fentonPlotTrajectory)
                )
                val displayName = if (babyName.contains("Arka", ignoreCase = true)) "Data Bayi Arka" else "Data Bayi $babyName"
                Text(
                    text = displayName,
                    style = typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.fentonPlotTrajectory
                )
            }
        }
    }
}

/**
 * Clamps the pan translation offset so the zoomed chart content does not drift
 * outside the container bounds.
 */
internal fun clampOffset(offset: Offset, scale: Float, size: Size): Offset {
    if (scale <= 1f || size.width <= 0f || size.height <= 0f) return Offset.Zero
    val maxPanX = (scale - 1f) * size.width / 2f
    val maxPanY = (scale - 1f) * size.height / 2f
    return Offset(
        x = offset.x.coerceIn(-maxPanX, maxPanX),
        y = offset.y.coerceIn(-maxPanY, maxPanY)
    )
}

/**
 * Loading indicator yang halus dan informatif saat memuat grafik resmi dari FentonGrowth.ca.
 */
@Composable
private fun FentonChartLoadingView(
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    val infiniteTransition = rememberInfiniteTransition(label = "fentonLoadingTransition")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(460.dp)
            .background(colors.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(colors.successContainer),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(52.dp)
                        .graphicsLayer { rotationZ = rotation },
                    color = colors.gridSubtitleTeal,
                    strokeWidth = 3.dp
                )
                Icon(
                    imageVector = Icons.Outlined.BarChart,
                    contentDescription = null,
                    tint = colors.gridSubtitleTeal,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Menghubungkan ke FentonGrowth.ca...",
                style = typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Mengalkulasi persentil & memplot kurva pertumbuhan resmi...",
                style = typography.bodySmall,
                color = colors.textSecondary.copy(alpha = pulseAlpha),
                textAlign = TextAlign.Center
            )
        }
    }
}


