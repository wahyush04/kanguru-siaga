package com.kangurusiaga.app.presentation.pmk.video

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.activity.compose.BackHandler
import androidx.annotation.RawRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.ui.res.stringResource
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.BrandBackground
import com.kangurusiaga.app.core.designsystem.theme.BrandLightPink
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.CardBorder
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.core.designsystem.theme.TextPrimary
import com.kangurusiaga.app.core.designsystem.theme.TextSecondary
import com.kangurusiaga.app.core.designsystem.theme.White
import kotlinx.coroutines.delay
import java.util.Locale

/**
 * Controller class managing the lifecycle and playback of Android MediaPlayer.
 * Supports play/pause, seek, buffering, playback speed, volume/mute, and fullscreen.
 */
class VideoPlayerState(
    private val context: Context,
    @RawRes private val videoRes: Int,
    fallbackDurationSeconds: Int,
    autoPlay: Boolean = false
) {
    var isPlaying by mutableStateOf(autoPlay)
    var isPrepared by mutableStateOf(false)
    var isBuffering by mutableStateOf(false)
    var bufferPercentage by mutableIntStateOf(0)
    var hasStarted by mutableStateOf(autoPlay)
    var isCompleted by mutableStateOf(false)
    var hasError by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    var currentPositionMs by mutableIntStateOf(0)
    var totalDurationMs by mutableIntStateOf(fallbackDurationSeconds * 1000)

    var isDragging by mutableStateOf(false)
    var dragFraction by mutableFloatStateOf(0f)

    var playbackSpeed by mutableFloatStateOf(1.0f)
    var isMuted by mutableStateOf(false)
    var isFullscreen by mutableStateOf(false)
    var controlsVisible by mutableStateOf(true)

    private var mediaPlayer: MediaPlayer? = null
    private var surface: Surface? = null
    private var textureView: TextureView? = null
    private var videoWidth = 0
    private var videoHeight = 0

    fun bindTextureView(tv: TextureView) {
        textureView = tv
        updateTextureTransform()
    }

    fun setSurface(surfaceTexture: SurfaceTexture?) {
        if (surfaceTexture != null) {
            val s = Surface(surfaceTexture)
            surface = s
            initMediaPlayer(s)
        } else {
            surface?.release()
            surface = null
            releasePlayer()
        }
    }

    private fun initMediaPlayer(s: Surface) {
        releasePlayer()
        try {
            val mp = MediaPlayer()
            val afd = context.resources.openRawResourceFd(videoRes)
            if (afd != null) {
                mp.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
            } else {
                val uri = Uri.parse("android.resource://${context.packageName}/$videoRes")
                mp.setDataSource(context, uri)
            }
            mp.setSurface(s)
            mp.setOnPreparedListener { player ->
                isPrepared = true
                hasError = false
                if (player.duration > 0) {
                    totalDurationMs = player.duration
                }
                applyVolume(isMuted)
                // Strictly do not auto-play when opening the screen.
                // Do NOT call setPlaybackParams here when paused, as Android MediaPlayer
                // will implicitly start playback if playbackParams are applied.
                if (isPlaying) {
                    if (playbackSpeed != 1.0f) {
                        applyPlaybackSpeed(playbackSpeed)
                    }
                    player.start()
                } else {
                    try {
                        if (player.isPlaying) {
                            player.pause()
                        }
                    } catch (_: Exception) {}
                }
            }
            mp.setOnBufferingUpdateListener { _, percent ->
                bufferPercentage = percent
            }
            mp.setOnInfoListener { _, what, _ ->
                when (what) {
                    MediaPlayer.MEDIA_INFO_BUFFERING_START -> isBuffering = true
                    MediaPlayer.MEDIA_INFO_BUFFERING_END -> isBuffering = false
                    MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                        isBuffering = false
                        if (isPlaying) {
                            hasStarted = true
                        }
                    }
                }
                false
            }
            mp.setOnVideoSizeChangedListener { _, w, h ->
                videoWidth = w
                videoHeight = h
                updateTextureTransform()
            }
            mp.setOnCompletionListener {
                isPlaying = false
                isCompleted = true
                currentPositionMs = totalDurationMs
                controlsVisible = true
            }
            mp.setOnErrorListener { _, what, extra ->
                Log.e("PmkVideo", "MediaPlayer error: what=$what, extra=$extra")
                isPlaying = false
                isPrepared = false
                hasError = true
                errorMessage = context.getString(R.string.pmk_video_error_playback)
                true
            }
            mp.prepareAsync()
            mediaPlayer = mp
        } catch (e: Exception) {
            Log.e("PmkVideo", "Error initializing MediaPlayer", e)
            hasError = true
            errorMessage = context.getString(R.string.pmk_video_error_general)
        }
    }

    fun updateTextureTransform() {
        val tv = textureView ?: return
        val vw = tv.width
        val vh = tv.height
        if (vw == 0 || vh == 0 || videoWidth == 0 || videoHeight == 0) return

        val videoRatio = videoWidth.toFloat() / videoHeight.toFloat()
        val viewRatio = vw.toFloat() / vh.toFloat()

        val matrix = Matrix()
        if (viewRatio > videoRatio) {
            // View is wider than video (letterbox left and right)
            val scale = videoRatio / viewRatio
            matrix.setScale(scale, 1f, vw / 2f, vh / 2f)
        } else {
            // View is taller than video (letterbox top and bottom)
            val scale = viewRatio / videoRatio
            matrix.setScale(1f, scale, vw / 2f, vh / 2f)
        }
        tv.setTransform(matrix)
    }

    fun togglePlayPause() {
        val mp = mediaPlayer
        if (mp == null || !isPrepared) {
            isPlaying = !isPlaying
            if (isPlaying) hasStarted = true
            return
        }

        try {
            if (isPlaying) {
                mp.pause()
                isPlaying = false
                controlsVisible = true
            } else {
                if (isCompleted || (currentPositionMs >= totalDurationMs - 300 && totalDurationMs > 0)) {
                    mp.seekTo(0)
                    currentPositionMs = 0
                    isCompleted = false
                }
                if (playbackSpeed != 1.0f) {
                    applyPlaybackSpeed(playbackSpeed)
                }
                mp.start()
                isPlaying = true
                hasStarted = true
                hasError = false
            }
        } catch (e: Exception) {
            Log.e("PmkVideo", "Error toggling play/pause", e)
        }
    }

    fun seekTo(fraction: Float) {
        val mp = mediaPlayer ?: return
        if (!isPrepared || totalDurationMs <= 0) return
        val targetMs = (fraction * totalDurationMs).toInt().coerceIn(0, totalDurationMs)
        try {
            mp.seekTo(targetMs)
            currentPositionMs = targetMs
            isCompleted = false
            if (!isPlaying) {
                try {
                    if (mp.isPlaying) mp.pause()
                } catch (_: Exception) {}
            }
        } catch (e: Exception) {
            Log.e("PmkVideo", "Error in seekTo", e)
        }
    }

    fun updatePosition() {
        val mp = mediaPlayer ?: return
        if (isPrepared && isPlaying && !isDragging) {
            try {
                currentPositionMs = mp.currentPosition
            } catch (_: Exception) {
                // Ignore transient state queries
            }
        }
    }

    fun pause() {
        val mp = mediaPlayer ?: return
        if (isPrepared) {
            try {
                if (mp.isPlaying) {
                    mp.pause()
                }
                isPlaying = false
                controlsVisible = true
            } catch (_: Exception) {
                // Ignore
            }
        }
    }

    private val speedOptions = listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
    fun cyclePlaybackSpeed() {
        val nextIndex = (speedOptions.indexOf(playbackSpeed) + 1) % speedOptions.size
        playbackSpeed = speedOptions[nextIndex]
        applyPlaybackSpeed(playbackSpeed)
    }

    private fun applyPlaybackSpeed(speed: Float) {
        val mp = mediaPlayer ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isPrepared) {
            try {
                val wasPlaying = isPlaying
                val params = mp.playbackParams
                params.speed = speed
                mp.playbackParams = params
                // MediaPlayer.setPlaybackParams() automatically starts playback in Android!
                // If the player was not playing, immediately pause to avoid unwanted auto-play.
                if (!wasPlaying) {
                    mp.pause()
                }
            } catch (e: Exception) {
                Log.e("PmkVideo", "Error applying speed", e)
            }
        }
    }

    fun toggleMute() {
        isMuted = !isMuted
        applyVolume(isMuted)
    }

    private fun applyVolume(muted: Boolean) {
        val mp = mediaPlayer ?: return
        try {
            val vol = if (muted) 0f else 1f
            mp.setVolume(vol, vol)
        } catch (e: Exception) {
            Log.e("PmkVideo", "Error setting volume", e)
        }
    }

    fun toggleFullscreen(activity: Activity?) {
        isFullscreen = !isFullscreen
        activity?.let { act ->
            val windowInsetsController = WindowCompat.getInsetsController(act.window, act.window.decorView)
            if (isFullscreen) {
                act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                windowInsetsController.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            } else {
                act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }
        controlsVisible = true
    }

    fun retry() {
        hasError = false
        errorMessage = null
        surface?.let { initMediaPlayer(it) }
    }

    private fun releasePlayer() {
        try {
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) mp.stop()
                mp.reset()
                mp.release()
            }
        } catch (_: Exception) {
            // Ignore
        }
        mediaPlayer = null
        isPrepared = false
        isPlaying = false
    }

    fun release() {
        releasePlayer()
        surface?.release()
        surface = null
        textureView = null
    }
}

/**
 * Screen: Kanguru Siaga - Detail Video PMK (Landscape Player)
 * Single Source of Truth: Google Stitch Design
 * Screen ID: projects/10808370107038581899/screens/82f607ff1b384b7395bf982971dda8fa
 */
@Composable
fun PmkVideoScreen(
    videoId: Int = 1,
    autoPlay: Boolean = false,
    onNavigateBack: () -> Unit,
    onNavigateToTimer: () -> Unit = {},
    onNavigateToNextVideo: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val video = remember(videoId) { PmkVideoDataSource.getVideoById(videoId) }
    var isBookmarked by remember { mutableStateOf(false) }

    val playerState = remember(videoId) {
        VideoPlayerState(
            context = context,
            videoRes = video.videoRes,
            fallbackDurationSeconds = video.durationSeconds,
            autoPlay = autoPlay
        )
    }

    // Periodic position updater
    LaunchedEffect(playerState.isPlaying, playerState.isDragging) {
        while (playerState.isPlaying && !playerState.isDragging) {
            playerState.updatePosition()
            delay(200)
        }
    }

    // Auto-hide controls timer when playing
    LaunchedEffect(playerState.isPlaying, playerState.controlsVisible, playerState.isDragging) {
        if (playerState.isPlaying && playerState.controlsVisible && !playerState.isDragging) {
            delay(3500)
            playerState.controlsVisible = false
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    // Manage player lifecycle: pause on background, restore orientation and release on dispose
    DisposableEffect(lifecycleOwner, videoId) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
                    playerState.pause()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            if (playerState.isFullscreen) {
                activity?.let { act ->
                    act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    val controller = WindowCompat.getInsetsController(act.window, act.window.decorView)
                    controller.show(WindowInsetsCompat.Type.systemBars())
                }
            }
            playerState.release()
        }
    }

    // Intercept back press when in fullscreen
    BackHandler(enabled = playerState.isFullscreen) {
        playerState.toggleFullscreen(activity)
    }

    val brandCream = BrandBackground
    val brandBorder = CardBorder
    val brandDark = TextPrimary
    val brandWarmGray = TextSecondary
    val brandRose = BrandPink
    val brandRoseLight = BrandLightPink

    if (playerState.isFullscreen) {
        // FULLSCREEN LANDSCAPE VIEW
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            // Video strictly in 16:9 container, FIT / CONTAIN behavior
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(16f / 9f, matchHeightConstraintsFirst = true)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                VideoSurface(playerState = playerState)

                VideoControlsOverlay(
                    playerState = playerState,
                    video = video,
                    brandRose = brandRose,
                    onToggleFullscreen = { playerState.toggleFullscreen(activity) },
                    onNavigateBack = { playerState.toggleFullscreen(activity) },
                    isFullscreen = true
                )
            }
        }
    } else {
        // PORTRAIT VIEW (Matches Stitch Detail Video PMK Landscape Player Screen)
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .background(brandCream),
            containerColor = brandCream,
            topBar = {
                // Top Navigation Bar matching Stitch
                Surface(
                    color = brandCream,
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Back Button (w-9 h-9 rounded-full bg-white border)
                        IconButton(
                            onClick = {
                                playerState.pause()
                                onNavigateBack()
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(KanguruTheme.colors.surface)
                                .border(1.dp, brandBorder, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.pmk_video_cd_back_module),
                                tint = brandDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Center Badge (Pulsing rose dot + "Video Edukasi #X")
                        Row(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(brandRoseLight)
                                .border(1.dp, brandRose.copy(alpha = 0.2f), CircleShape)
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            PulsingDot(color = brandRose)
                            Text(
                                text = video.badgeText,
                                color = brandRose,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.3.sp
                            )
                        }

                        // Bookmark Button (w-9 h-9 rounded-full bg-white border)
                        IconButton(
                            onClick = { isBookmarked = !isBookmarked },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(KanguruTheme.colors.surface)
                                .border(1.dp, brandBorder, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = stringResource(R.string.pmk_video_cd_bookmark),
                                tint = if (isBookmarked) brandRose else brandDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            },
            bottomBar = {
                // Sticky Bottom Action Area matching Stitch
                Surface(
                    color = KanguruTheme.colors.surface,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, brandBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Primary CTA: Mulai Timer PMK Sekarang
                        Button(
                            onClick = {
                                playerState.pause()
                                onNavigateToTimer()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = brandRose,
                                contentColor = White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.pmk_video_btn_timer),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Secondary CTA: Lanjut ke Materi Selanjutnya (if available)
                        if (video.nextLessonId != null) {
                            Button(
                                onClick = {
                                    playerState.pause()
                                    onNavigateToNextVideo(video.nextLessonId)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .border(1.dp, brandBorder, RoundedCornerShape(12.dp)),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = KanguruTheme.colors.surface,
                                    contentColor = brandDark
                                )
                            ) {
                                Text(
                                    text = stringResource(R.string.pmk_video_btn_next),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = brandDark
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp),
                                    tint = brandRose
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
            ) {
                // 16:9 Landscape Video Player Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    VideoSurface(playerState = playerState)

                    // Poster thumbnail when not started
                    if (!playerState.hasStarted) {
                        Image(
                            painter = painterResource(id = video.thumbnailRes),
                            contentDescription = video.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    VideoControlsOverlay(
                        playerState = playerState,
                        video = video,
                        brandRose = brandRose,
                        onToggleFullscreen = { playerState.toggleFullscreen(activity) },
                        onNavigateBack = onNavigateBack,
                        isFullscreen = false
                    )
                }

                // Scrollable Content Below Video
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Category & Duration Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Category Badge
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(brandRoseLight)
                                .border(1.dp, brandRose.copy(alpha = 0.2f), CircleShape)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = video.category,
                                color = brandRose,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Duration Badge
                        Row(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(KanguruTheme.colors.surface)
                                .border(1.dp, brandBorder, CircleShape)
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = brandWarmGray,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = stringResource(R.string.pmk_video_duration_minutes, video.duration),
                                color = brandWarmGray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Title
                    Text(
                        text = video.title,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = brandDark,
                        lineHeight = 24.sp
                    )

                    // Description
                    Text(
                        text = video.description,
                        fontSize = 13.sp,
                        color = brandWarmGray,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Normal
                    )

                    // Clinical Key Points Card
                    ClinicalKeyPointsCard(
                        clinicalPoints = video.clinicalPoints,
                        calloutTip = video.calloutTip,
                        brandRose = brandRose,
                        brandRoseLight = brandRoseLight,
                        brandBorder = brandBorder,
                        brandDark = brandDark,
                        brandWarmGray = brandWarmGray,
                        brandCream = brandCream
                    )

                    // Bottom spacer for scroll comfort
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

/**
 * TextureView Video Surface enforcing FIT / CONTAIN rendering and aspect ratio preservation.
 */
@Composable
private fun VideoSurface(playerState: VideoPlayerState) {
    AndroidView(
        factory = { ctx ->
            TextureView(ctx).apply {
                surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(st: SurfaceTexture, width: Int, height: Int) {
                        playerState.bindTextureView(this@apply)
                        playerState.setSurface(st)
                    }

                    override fun onSurfaceTextureSizeChanged(st: SurfaceTexture, width: Int, height: Int) {
                        playerState.updateTextureTransform()
                    }

                    override fun onSurfaceTextureDestroyed(st: SurfaceTexture): Boolean {
                        playerState.setSurface(null)
                        return true
                    }

                    override fun onSurfaceTextureUpdated(st: SurfaceTexture) {}
                }
            }
        },
        update = { tv ->
            playerState.bindTextureView(tv)
        },
        modifier = Modifier.fillMaxSize()
    )
}

/**
 * Animated Video Controls Overlay with Scrubber, Timeline, Speed, Mute, Fullscreen, and Center Play.
 */
@Composable
private fun VideoControlsOverlay(
    playerState: VideoPlayerState,
    video: PmkVideoItem,
    brandRose: Color,
    onToggleFullscreen: () -> Unit,
    onNavigateBack: () -> Unit,
    isFullscreen: Boolean
) {
    val isEnded = playerState.isCompleted || (playerState.currentPositionMs >= playerState.totalDurationMs && playerState.totalDurationMs > 0)

    // Tap on background toggles controls visibility
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (playerState.hasStarted) {
                            playerState.controlsVisible = !playerState.controlsVisible
                        }
                    }
                )
            }
    ) {
        // Buffering Spinner
        if (playerState.isBuffering) {
            CircularProgressIndicator(
                color = brandRose,
                modifier = Modifier
                    .size(44.dp)
                    .align(Alignment.Center)
            )
        }

        // Error message & retry
        if (playerState.hasError) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = playerState.errorMessage ?: stringResource(R.string.pmk_video_error_general),
                    color = White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { playerState.retry() },
                    colors = ButtonDefaults.buttonColors(containerColor = brandRose)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.pmk_video_btn_retry),
                        tint = White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = stringResource(R.string.pmk_video_btn_retry), color = White, fontSize = 12.sp)
                }
            }
        }

        // Controls Overlay with smooth fade
        AnimatedVisibility(
            visible = playerState.controlsVisible && !playerState.hasError,
            enter = fadeIn(tween(250)),
            exit = fadeOut(tween(250)),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.75f),
                                Color.Black.copy(alpha = 0.25f),
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            ) {
                // In Fullscreen, show Top Bar with Back button and title
                if (isFullscreen) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
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
                                contentDescription = stringResource(R.string.pmk_video_cd_exit_fullscreen),
                                tint = White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = video.title,
                            color = White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Center Play Button with Soft Glowing Pulse (matching Stitch)
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (!playerState.isPlaying) {
                        PulseRingAnimation(color = brandRose)
                    }

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .shadow(12.dp, CircleShape)
                            .clip(CircleShape)
                            .background(brandRose)
                            .border(2.dp, White.copy(alpha = 0.85f), CircleShape)
                            .clickable { playerState.togglePlayPause() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                isEnded -> Icons.Default.Replay
                                playerState.isPlaying -> Icons.Default.Pause
                                else -> Icons.Default.PlayArrow
                            },
                            contentDescription = when {
                                isEnded -> stringResource(R.string.pmk_video_cd_replay)
                                playerState.isPlaying -> stringResource(R.string.pmk_video_cd_pause)
                                else -> stringResource(R.string.pmk_video_cd_play)
                            },
                            tint = White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Bottom Controls Bar: Scrubber, Timers, Speed, Mute, Fullscreen
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Custom Scrubber Progress Bar
                    val currentMs = if (playerState.isDragging) {
                        (playerState.dragFraction * playerState.totalDurationMs).toInt()
                    } else {
                        playerState.currentPositionMs
                    }
                    val totalMs = if (playerState.totalDurationMs > 0) playerState.totalDurationMs else video.durationSeconds * 1000
                    val progressFraction = if (totalMs > 0) {
                        (currentMs.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)
                    } else 0f
                    val bufferFraction = (playerState.bufferPercentage.toFloat() / 100f).coerceIn(0f, 1f)

                    VideoScrubber(
                        progressFraction = progressFraction,
                        bufferFraction = bufferFraction,
                        activeColor = brandRose,
                        onSeekFraction = { frac ->
                            playerState.seekTo(frac)
                        },
                        onDragFraction = { frac ->
                            playerState.isDragging = true
                            playerState.dragFraction = frac
                        },
                        onDragEnd = {
                            playerState.seekTo(playerState.dragFraction)
                            playerState.isDragging = false
                        }
                    )

                    // Control Row: Timestamps + Action Icons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Running Timestamps
                        val curSec = currentMs / 1000
                        val totSec = totalMs / 1000
                        val curTimeStr = String.format(Locale.getDefault(), "%02d:%02d", curSec / 60, curSec % 60)
                        val totTimeStr = String.format(Locale.getDefault(), "%02d:%02d", totSec / 60, totSec % 60)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = curTimeStr,
                                color = White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "/",
                                color = White.copy(alpha = 0.6f),
                                fontSize = 11.sp
                            )
                            Text(
                                text = totTimeStr,
                                color = White.copy(alpha = 0.75f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Right Action Controls: Speed, Mute, Fullscreen
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Playback Speed Button (Chip 1.0x)
                            val speedText = when (playerState.playbackSpeed) {
                                1.0f -> "1.0x"
                                1.25f -> "1.25x"
                                1.5f -> "1.5x"
                                2.0f -> "2.0x"
                                0.75f -> "0.75x"
                                else -> "${playerState.playbackSpeed}x"
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(White.copy(alpha = 0.2f))
                                    .clickable { playerState.cyclePlaybackSpeed() }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = speedText,
                                    color = White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Volume / Mute Button
                            IconButton(
                                onClick = { playerState.toggleMute() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (playerState.isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                    contentDescription = if (playerState.isMuted) stringResource(R.string.pmk_video_cd_unmute) else stringResource(R.string.pmk_video_cd_mute),
                                    tint = White.copy(alpha = 0.9f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Fullscreen Button
                            IconButton(
                                onClick = onToggleFullscreen,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                    contentDescription = if (isFullscreen) stringResource(R.string.pmk_video_cd_exit_fullscreen) else stringResource(R.string.pmk_video_cd_fullscreen),
                                    tint = White.copy(alpha = 0.9f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Custom Interactive Scrubber Progress Bar matching Stitch CSS.
 */
@Composable
private fun VideoScrubber(
    progressFraction: Float,
    bufferFraction: Float,
    activeColor: Color,
    onSeekFraction: (Float) -> Unit,
    onDragFraction: (Float) -> Unit,
    onDragEnd: () -> Unit
) {
    var barWidth by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    if (barWidth > 0) {
                        val fraction = (offset.x / barWidth).coerceIn(0f, 1f)
                        onSeekFraction(fraction)
                    }
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = onDragEnd,
                    onDragCancel = onDragEnd,
                    onDrag = { change, _ ->
                        change.consume()
                        if (barWidth > 0) {
                            val fraction = (change.position.x / barWidth).coerceIn(0f, 1f)
                            onDragFraction(fraction)
                        }
                    }
                )
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(6.dp)) {
            barWidth = size.width
            val h = size.height
            val r = CornerRadius(h / 2f, h / 2f)

            // Background track
            drawRoundRect(
                brush = SolidColor(Color.White.copy(alpha = 0.3f)),
                size = Size(size.width, h),
                cornerRadius = r
            )

            // Buffered portion
            if (bufferFraction > 0f) {
                drawRoundRect(
                    brush = SolidColor(Color.White.copy(alpha = 0.45f)),
                    size = Size(size.width * bufferFraction, h),
                    cornerRadius = r
                )
            }

            // Active played portion
            if (progressFraction > 0f) {
                drawRoundRect(
                    brush = SolidColor(activeColor),
                    size = Size(size.width * progressFraction, h),
                    cornerRadius = r
                )
            }

            // Scrubber thumb circle
            val thumbX = (size.width * progressFraction).coerceIn(0f, size.width)
            drawCircle(
                color = Color.White,
                radius = 6.dp.toPx(),
                center = Offset(thumbX, h / 2f)
            )
            drawCircle(
                color = activeColor,
                radius = 4.dp.toPx(),
                center = Offset(thumbX, h / 2f)
            )
        }
    }
}

/**
 * Pulsing Dot for "Video Edukasi #X" badge.
 */
@Composable
private fun PulsingDot(color: Color) {
    val transition = rememberInfiniteTransition(label = "pulse_dot")
    val alpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = alpha))
    )
}

/**
 * Pulse Ring effect for center play button.
 */
@Composable
private fun PulseRingAnimation(color: Color) {
    val transition = rememberInfiniteTransition(label = "pulse_ring")
    val scale by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale"
    )
    val alpha by transition.animateFloat(
        initialValue = 0.65f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .size(56.dp * scale)
            .clip(CircleShape)
            .background(color.copy(alpha = alpha))
    )
}

/**
 * Clinical Key Points Card matching Stitch CSS and layout.
 */
@Composable
private fun ClinicalKeyPointsCard(
    clinicalPoints: List<ClinicalPoint>,
    calloutTip: String?,
    brandRose: Color,
    brandRoseLight: Color,
    brandBorder: Color,
    brandDark: Color,
    brandWarmGray: Color,
    brandCream: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, brandBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = KanguruTheme.colors.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Heart-pulse icon + "Poin Penting Klinis" + "Standar Pedoman Klinis IDAI & Kemenkes RI"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(brandRoseLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = brandRose,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column {
                    Text(
                        text = stringResource(R.string.pmk_video_clinical_points_title),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = brandDark
                    )
                    Text(
                        text = stringResource(R.string.pmk_video_clinical_points_sub),
                        fontSize = 10.5.sp,
                        color = brandWarmGray
                    )
                }
            }

            HorizontalDivider(color = brandBorder.copy(alpha = 0.6f))

            // Clinical Points List
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                clinicalPoints.forEach { point ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Number circle badge
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(brandRoseLight)
                                .border(1.dp, brandRose.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = point.number.toString(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = brandRose
                            )
                        }

                        // Title & Description
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${point.title}:",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = brandDark,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = point.description,
                                fontSize = 12.sp,
                                color = brandWarmGray,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }

            // Nurse Callout Tip Box
            if (!calloutTip.isNullOrBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(brandCream)
                        .border(1.dp, brandBorder, RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = brandRose,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = calloutTip,
                        fontSize = 11.sp,
                        color = brandWarmGray,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
