package com.kangurusiaga.app.presentation.growth.chart

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.domain.model.GrowthMeasurement
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

/**
 * FentonChart - Menampilkan grafik resmi terverifikasi dari FentonGrowth.ca.
 * Seluruh kurva dihitung dan diplot langsung dari server resmi Fenton.
 * Mendukung interaksi Zoom in / Zoom out (pinch gesture) dan double tap untuk zoom.
 */
@Composable
fun FentonChart(
    selectedTab: Int,
    isBoy: Boolean,
    weightMeasurements: List<GrowthMeasurement> = emptyList(),
    lengthMeasurements: List<GrowthMeasurement> = emptyList(),
    headCircMeasurements: List<GrowthMeasurement> = emptyList(),
    currentPmaWeeks: Float = 35f,
    babyName: String = "",
    chartImageUrl: String? = null,
    chartLocalPath: String? = null,
    chartTimestamp: Long = 0L,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    // Zoom and pan state for image exploration
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

        // 2. Official FentonGrowth.ca Image Canvas / Loading View
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface)
                .padding(horizontal = 6.dp, vertical = 6.dp)
        ) {
            if (imageModel != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(460.dp)
                        .clipToBounds()
                        .background(colors.cardBackground)
                ) {
                    SubcomposeAsyncImage(
                        model = imageModel,
                        contentDescription = "Grafik Pertumbuhan Fenton",
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
                            FentonChartLoadingView(modifier = Modifier.fillMaxSize())
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
            } else {
                // Selalu tampilkan tampilan loading resmi jika belum ada gambar grafik yang ditampilkan
                FentonChartLoadingView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(460.dp)
                )
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
