package com.kangurusiaga.app.presentation.pmk.video

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.annotation.RawRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.White
import kotlinx.coroutines.delay

/**
 * Controller class managing the lifecycle and playback of Android MediaPlayer.
 * Encapsulates playback state to eliminate race conditions and MediaPlayer native errors.
 */
class VideoPlayerState(
    private val context: Context,
    @RawRes private val videoRes: Int,
    fallbackDurationSeconds: Int
) {
    var isPlaying by mutableStateOf(false)
    var isPrepared by mutableStateOf(false)
    var hasStarted by mutableStateOf(false)
    var currentPositionMs by mutableIntStateOf(0)
    var totalDurationMs by mutableIntStateOf(fallbackDurationSeconds * 1000)
    var isDragging by mutableStateOf(false)
    var dragFraction by mutableFloatStateOf(0f)

    private var mediaPlayer: MediaPlayer? = null
    private var surface: Surface? = null

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
                if (player.duration > 0) {
                    totalDurationMs = player.duration
                }
                if (isPlaying) {
                    player.start()
                }
            }
            mp.setOnCompletionListener {
                isPlaying = false
                currentPositionMs = totalDurationMs
            }
            mp.setOnErrorListener { _, what, extra ->
                Log.e("PmkVideo", "MediaPlayer error: what=$what, extra=$extra")
                isPlaying = false
                isPrepared = false
                true
            }
            mp.prepareAsync()
            mediaPlayer = mp
        } catch (e: Exception) {
            Log.e("PmkVideo", "Error initializing MediaPlayer", e)
        }
    }

    fun togglePlayPause() {
        val mp = mediaPlayer ?: return
        if (!isPrepared) return

        try {
            if (isPlaying) {
                mp.pause()
                isPlaying = false
            } else {
                // If reached end, restart from beginning
                if (currentPositionMs >= totalDurationMs - 300 && totalDurationMs > 0) {
                    mp.seekTo(0)
                    currentPositionMs = 0
                }
                mp.start()
                isPlaying = true
                hasStarted = true
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
        } catch (e: Exception) {
            Log.e("PmkVideo", "Error in seekTo", e)
        }
    }

    fun updatePosition() {
        val mp = mediaPlayer ?: return
        if (isPrepared && isPlaying && !isDragging) {
            try {
                currentPositionMs = mp.currentPosition
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    fun pause() {
        val mp = mediaPlayer ?: return
        if (isPrepared && isPlaying) {
            try {
                mp.pause()
                isPlaying = false
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    private fun releasePlayer() {
        try {
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) mp.stop()
                mp.reset()
                mp.release()
            }
        } catch (e: Exception) {
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
    }
}

/**
 * Screen: Kanguru Siaga - Detail Pemutar Video PMK
 * Stitch Screen ID: projects/10808370107038581899/screens/4d5792ceaa7d4c3f991d507feb72e5df
 */
@Composable
fun PmkVideoScreen(
    videoId: Int = 4,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val video = remember(videoId) { PmkVideoDataSource.getVideoById(videoId) }
    var isBookmarked by remember { mutableStateOf(false) }

    val playerState = remember(videoId) {
        VideoPlayerState(
            context = context,
            videoRes = video.videoRes,
            fallbackDurationSeconds = video.durationSeconds
        )
    }

    // Periodic position updater loop
    LaunchedEffect(playerState.isPlaying, playerState.isDragging) {
        while (playerState.isPlaying && !playerState.isDragging) {
            playerState.updatePosition()
            delay(200)
        }
    }

    DisposableEffect(videoId) {
        onDispose {
            playerState.release()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(White),
        containerColor = White,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = innerPadding.calculateBottomPadding() + 24.dp)
        ) {
            // Video Player Container (Height 320dp, matches Stitch)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                // Native TextureView video player (renders in OpenGL ES texture layer without punching holes)
                AndroidView(
                    factory = { ctx ->
                        TextureView(ctx).apply {
                            surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                                override fun onSurfaceTextureAvailable(st: SurfaceTexture, width: Int, height: Int) {
                                    playerState.setSurface(st)
                                }

                                override fun onSurfaceTextureSizeChanged(st: SurfaceTexture, width: Int, height: Int) {}

                                override fun onSurfaceTextureDestroyed(st: SurfaceTexture): Boolean {
                                    playerState.setSurface(null)
                                    return true
                                }

                                override fun onSurfaceTextureUpdated(st: SurfaceTexture) {}
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Poster overlay if not yet started
                if (!playerState.hasStarted) {
                    Image(
                        painter = painterResource(id = video.thumbnailRes),
                        contentDescription = video.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Gradient Overlay for controls visibility
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.6f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.75f)
                                )
                            )
                        )
                        .clickable { playerState.togglePlayPause() }
                )

                // Top Controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            playerState.pause()
                            onNavigateBack()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.35f))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { isBookmarked = !isBookmarked },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.35f))
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Simpan Video",
                                tint = if (isBookmarked) BrandPink else White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = { /* Fullscreen */ },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.35f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "Perbesar Layar",
                                tint = White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // Center Play/Pause Button
                val isEnded = playerState.currentPositionMs >= playerState.totalDurationMs && playerState.totalDurationMs > 0
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(White.copy(alpha = 0.85f))
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
                            isEnded -> "Putar Ulang"
                            playerState.isPlaying -> "Jeda"
                            else -> "Putar Video"
                        },
                        tint = BrandPink,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Bottom Timeline Scrubber (Interactive Seeker)
                val currentMs = if (playerState.isDragging) {
                    (playerState.dragFraction * playerState.totalDurationMs).toInt()
                } else {
                    playerState.currentPositionMs
                }
                val totalMs = if (playerState.totalDurationMs > 0) playerState.totalDurationMs else video.durationSeconds * 1000

                val curSec = currentMs / 1000
                val totSec = totalMs / 1000
                val curTimeStr = String.format("%02d:%02d", curSec / 60, curSec % 60)
                val totTimeStr = String.format("%02d:%02d", totSec / 60, totSec % 60)

                val progressFraction = if (totalMs > 0) {
                    (playerState.currentPositionMs.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)
                } else 0f

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = curTimeStr,
                        color = White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Slider(
                        value = if (playerState.isDragging) playerState.dragFraction else progressFraction,
                        onValueChange = { newFrac ->
                            playerState.isDragging = true
                            playerState.dragFraction = newFrac
                        },
                        onValueChangeFinished = {
                            playerState.seekTo(playerState.dragFraction)
                            playerState.isDragging = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = BrandPink,
                            activeTrackColor = BrandPink,
                            inactiveTrackColor = White.copy(alpha = 0.35f)
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = totTimeStr,
                        color = White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Body Content Below Video
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Video Title
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    letterSpacing = (-0.3).sp,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Video Description
                Text(
                    text = video.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B),
                    fontSize = 13.5.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Poin Penting Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFFFE2E6), RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF6F6)),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFE4E8)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "📌", fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Poin Penting",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE63956),
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        video.keyPoints.forEach { point ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 6.dp)
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1E293B))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = point,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF334155),
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
