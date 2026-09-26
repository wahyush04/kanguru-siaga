package com.kangurusiaga.app.presentation.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kangurusiaga.app.R
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

// Organic blob shape matching Stitch CSS border-radius: 42% 58% 63% 37% / 45% 42% 58% 55%
private val OrganicBlobShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    moveTo(w * 0.42f, 0f)
    cubicTo(w * 0.75f, 0f, w, h * 0.25f, w, h * 0.58f)
    cubicTo(w, h * 0.85f, w * 0.70f, h, w * 0.37f, h)
    cubicTo(w * 0.15f, h, 0f, h * 0.75f, 0f, h * 0.45f)
    cubicTo(0f, h * 0.20f, w * 0.20f, 0f, w * 0.42f, 0f)
    close()
}

// Particle state for interactive tap feedback
private data class SplashBurstParticle(
    val id: Long,
    val startX: Float,
    val startY: Float,
    val tx: Float,
    val ty: Float,
    val rotation: Float,
    val iconType: Int, // 0 = heart, 1 = sparkle, 2 = leaf
    val progress: Animatable<Float, androidx.compose.animation.core.AnimationVector1D>
)

@Composable
fun SplashRoute(
    onNavigateToHome: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is SplashUiState.NavigateToHome) {
            onNavigateToHome()
        }
    }

    if (uiState is SplashUiState.ShowSplash) {
        SplashScreen(
            onStartClick = onNavigateToOnboarding,
            modifier = modifier
        )
    }
}

@Composable
fun SplashScreen(
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val activeParticles = remember { mutableStateListOf<SplashBurstParticle>() }

    // Helper function to trigger interactive particle bursts on tap
    val triggerBurst: (Float, Float) -> Unit = { x, y ->
        val count = 7
        for (i in 0 until count) {
            val angle = (i.toFloat() / count) * (2 * Math.PI.toFloat()) + (Random.nextFloat() * 0.4f - 0.2f)
            val distance = 35f + Random.nextFloat() * 50f
            val tx = cos(angle) * distance
            val ty = sin(angle) * distance - 30f // drift upward
            val rot = Random.nextFloat() * 60f - 30f
            val iconType = i % 3

            val particle = SplashBurstParticle(
                id = System.nanoTime() + i,
                startX = x,
                startY = y,
                tx = tx,
                ty = ty,
                rotation = rot,
                iconType = iconType,
                progress = Animatable(0f)
            )
            activeParticles.add(particle)

            coroutineScope.launch {
                particle.progress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = (700 + Random.nextInt(350)),
                        easing = FastOutSlowInEasing
                    )
                )
                activeParticles.remove(particle)
            }
        }
    }

    // Infinite transitions for ambient elements, mascot breathing, floating particles, and controls
    val infiniteTransition = rememberInfiniteTransition(label = "splash_infinite_loop")

    // 1. Ambient lighting pulse
    val amberPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.40f,
        targetValue = 0.70f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "amber_pulse"
    )
    val rosePulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rose_pulse"
    )

    // 2. Mascot breathing & aura glow
    val mascotScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.028f,
        animationSpec = infiniteRepeatable(
            animation = tween(2250, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot_breathe_scale"
    )
    val mascotTranslateY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2250, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot_breathe_y"
    )
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_scale"
    )
    val auraRotation by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_rotation"
    )
    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_alpha"
    )

    // 3. Floating particles (Hearts and Leaves)
    val heart1OffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heart1_offset_y"
    )
    val heart1Rotation by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heart1_rot"
    )

    val heart2OffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -24f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heart2_offset_y"
    )
    val heart2Rotation by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heart2_rot"
    )

    val leafSwayOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "leaf_sway_y"
    )
    val leafSwayRotation by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "leaf_sway_rot"
    )

    // 4. Progress bar fill & shimmer
    val progressFillFraction by infiniteTransition.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "progress_fill"
    )
    val shimmerOffsetFraction by infiniteTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress_shimmer"
    )

    // 5. Button pulse and arrow nudge
    val buttonPulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "button_pulse"
    )
    val arrowNudgeX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "arrow_nudge"
    )

    // Staggered entrance animation for title & subtitle
    val contentAlpha = remember { Animatable(0f) }
    val contentTranslationY = remember { Animatable(20f) }

    LaunchedEffect(Unit) {
        contentAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(800, easing = FastOutSlowInEasing)
        )
    }
    LaunchedEffect(Unit) {
        contentTranslationY.animateTo(
            targetValue = 0f,
            animationSpec = tween(800, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAF6F0))
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    triggerBurst(offset.x, offset.y)
                }
            }
    ) {
        // ==========================================
        // 1. DECORATIVE AMBIENT BACKGROUND LAYERS
        // ==========================================

        // Top-Right Soft Amber Ambient Glow
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-40).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFEF3C7).copy(alpha = amberPulseAlpha),
                            Color(0xFFFEF3C7).copy(alpha = amberPulseAlpha * 0.4f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Top-Left Soft Rose Ambient Glow
        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.TopStart)
                .offset(x = (-50).dp, y = 50.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFE4E6).copy(alpha = rosePulseAlpha),
                            Color(0xFFFFE4E6).copy(alpha = rosePulseAlpha * 0.4f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Bottom Organic Waves Canvas matching Stitch SVG specifications
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .align(Alignment.BottomCenter)
        ) {
            val w = size.width
            val h = size.height

            // Soft radial base tint at the bottom
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFBEBE1).copy(alpha = 0.9f),
                        Color(0xFFFBF3EB).copy(alpha = 0.5f),
                        Color.Transparent
                    ),
                    center = Offset(w / 2f, h),
                    radius = w * 0.9f
                ),
                topLeft = Offset.Zero,
                size = size
            )

            // Wave 1: Path D: M0,80 C110,130 260,20 390,90 L390,180 L0,180 Z (scaled)
            val path1 = Path().apply {
                moveTo(0f, h * (80f / 180f))
                cubicTo(
                    w * (110f / 390f), h * (130f / 180f),
                    w * (260f / 390f), h * (20f / 180f),
                    w, h * (90f / 180f)
                )
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(path1, color = Color(0xFFF8E5D8).copy(alpha = 0.65f))

            // Wave 2: Path D: M0,110 C140,50 280,140 390,110 L390,180 L0,180 Z (scaled)
            val path2 = Path().apply {
                moveTo(0f, h * (110f / 180f))
                cubicTo(
                    w * (140f / 390f), h * (50f / 180f),
                    w * (280f / 390f), h * (140f / 180f),
                    w, h * (110f / 180f)
                )
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(path2, color = Color(0xFFF3D8C8).copy(alpha = 0.40f))
        }

        // ==========================================
        // 2. FLOATING ACCENTS: HEARTS & LEAVES
        // ==========================================

        // Top-Left Floating Heart
        Icon(
            painter = painterResource(id = R.drawable.ic_splash_heart),
            contentDescription = null,
            tint = Color(0xFFFDA4AF).copy(alpha = 0.85f),
            modifier = Modifier
                .padding(start = 36.dp, top = 90.dp)
                .size(20.dp)
                .offset(y = heart1OffsetY.dp)
                .rotate(heart1Rotation)
        )

        // Top-Right Floating Tiny Heart
        Icon(
            painter = painterResource(id = R.drawable.ic_splash_heart),
            contentDescription = null,
            tint = Color(0xFFFB7185).copy(alpha = 0.80f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 44.dp, top = 140.dp)
                .size(16.dp)
                .offset(y = heart2OffsetY.dp)
                .rotate(heart2Rotation)
        )

        // Mid-Left Floating Small Heart
        Icon(
            painter = painterResource(id = R.drawable.ic_splash_heart),
            contentDescription = null,
            tint = Color(0xFFFECDD3).copy(alpha = 0.70f),
            modifier = Modifier
                .padding(start = 22.dp, top = 340.dp)
                .size(14.dp)
                .offset(y = (heart2OffsetY * 0.6f).dp)
                .rotate(-heart2Rotation)
        )

        // Right Side Pastel Leaf Element
        Icon(
            painter = painterResource(id = R.drawable.ic_splash_leaf_1),
            contentDescription = null,
            tint = Color(0xFF059669).copy(alpha = 0.35f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 22.dp, top = 240.dp)
                .size(26.dp)
                .offset(y = leafSwayOffsetY.dp)
                .rotate(leafSwayRotation)
        )

        // Left Side Floating Leaf
        Icon(
            painter = painterResource(id = R.drawable.ic_splash_leaf_2),
            contentDescription = null,
            tint = Color(0xFF059669).copy(alpha = 0.30f),
            modifier = Modifier
                .padding(start = 28.dp, top = 270.dp)
                .size(24.dp)
                .offset(y = (leafSwayOffsetY * 0.8f).dp)
                .rotate(-leafSwayRotation)
        )

        // ==========================================
        // 3. MAIN CENTER CONTENT AREA
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Central Mascot & Glowing Aura Container
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .graphicsLayer {
                        alpha = contentAlpha.value
                        translationY = contentTranslationY.value
                    }
            ) {
                Box(
                    modifier = Modifier
                        .size(270.dp)
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                triggerBurst(size.width / 2f, size.height / 2f)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Outer pulsating glowing aura
                    Box(
                        modifier = Modifier
                            .size(260.dp)
                            .scale(auraScale)
                            .rotate(auraRotation)
                            .alpha(auraAlpha)
                            .background(
                                Brush.sweepGradient(
                                    listOf(
                                        Color(0xFFFDE68A).copy(alpha = 0.45f),
                                        Color(0xFFFFE4E6).copy(alpha = 0.60f),
                                        Color(0xFFFED7AA).copy(alpha = 0.40f),
                                        Color(0xFFFDE68A).copy(alpha = 0.45f)
                                    )
                                ),
                                shape = OrganicBlobShape
                            )
                    )

                    // Inner soft warm peach aura
                    Box(
                        modifier = Modifier
                            .size(225.dp)
                            .scale(0.96f)
                            .rotate(-auraRotation)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        Color(0xFFFFF1F2).copy(alpha = 0.75f),
                                        Color(0xFFFEF3C7).copy(alpha = 0.65f),
                                        Color(0xFFFFEDD5).copy(alpha = 0.40f)
                                    )
                                ),
                                shape = OrganicBlobShape
                            )
                    )

                    // Soft inner white glow
                    Box(
                        modifier = Modifier
                            .size(175.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.50f))
                    )

                    // Kangaroo mascot image with gentle breathing animation
                    Image(
                        painter = painterResource(id = R.drawable.ic_kangaroo_mascot),
                        contentDescription = "Maskot Kanguru Siaga",
                        modifier = Modifier
                            .size(225.dp)
                            .graphicsLayer {
                                scaleX = mascotScale
                                scaleY = mascotScale
                                translationY = mascotTranslateY
                                transformOrigin = TransformOrigin(0.5f, 1f)
                            },
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Typography Section
                Text(
                    text = "KANGURU\nSIAGA",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF3A4D39),
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Pendamping perawatan BBLR di rumah",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }

            // ==========================================
            // 4. FOOTER PROGRESS & CTA CONTROLS
            // ==========================================
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated Progress Bar with Shimmer Highlight
                Box(
                    modifier = Modifier
                        .width(150.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFFDE2E4))
                ) {
                    // Progress fill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressFillFraction)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFFF4727F),
                                        Color(0xFFFB7185),
                                        Color(0xFFF4727F)
                                    )
                                )
                            )
                    ) {
                        // Shimmer sweep across the progress bar
                        val shimmerX = shimmerOffsetFraction * 150f
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.White.copy(alpha = 0.75f),
                                            Color.Transparent
                                        ),
                                        startX = shimmerX - 40f,
                                        endX = shimmerX + 40f
                                    )
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // CTA "Mulai" Button with Pulse, Shimmer Sweep, and Arrow Nudge
                Button(
                    onClick = {
                        onStartClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .graphicsLayer {
                            scaleX = buttonPulseScale
                            scaleY = buttonPulseScale
                        }
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(26.dp),
                            spotColor = Color(0xFFFF5C77)
                        ),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFFFF5C77),
                                        Color(0xFFF43F5E)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Button Shimmer Sweep Highlight
                        val btnShimmerX = shimmerOffsetFraction * 300f
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.White.copy(alpha = 0.30f),
                                            Color.Transparent
                                        ),
                                        startX = btnShimmerX - 80f,
                                        endX = btnShimmerX + 80f
                                    )
                                )
                        )

                        // Button Label and Nudging Arrow
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Mulai",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Mulai",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(18.dp)
                                    .offset { IntOffset(x = arrowNudgeX.roundToInt(), y = 0) }
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 5. INTERACTIVE BURST PARTICLES OVERLAY
        // ==========================================
        activeParticles.forEach { particle ->
            val p = particle.progress.value
            val currentX = particle.startX + particle.tx * p
            val currentY = particle.startY + particle.ty * p
            val currentAlpha = (1f - p).coerceIn(0f, 1f)
            val currentScale = (0.6f + 0.7f * p).coerceIn(0.5f, 1.4f)
            val currentRotation = particle.rotation * p

            Box(
                modifier = Modifier
                    .offset { IntOffset(currentX.roundToInt(), currentY.roundToInt()) }
                    .graphicsLayer {
                        alpha = currentAlpha
                        scaleX = currentScale
                        scaleY = currentScale
                        rotationZ = currentRotation
                    }
            ) {
                when (particle.iconType) {
                    0 -> Icon(
                        painter = painterResource(id = R.drawable.ic_splash_heart),
                        contentDescription = null,
                        tint = Color(0xFFFB7185),
                        modifier = Modifier.size(18.dp)
                    )
                    1 -> Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFBBF24))
                    )
                    else -> Icon(
                        painter = painterResource(id = R.drawable.ic_splash_leaf_1),
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
