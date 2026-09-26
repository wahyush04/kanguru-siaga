package com.kangurusiaga.app.presentation.pmk.video

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kangurusiaga.app.core.designsystem.theme.White
import com.kangurusiaga.app.presentation.home.HomeBottomBar
import com.kangurusiaga.app.presentation.home.HomeTab

/**
 * Screen: Kanguru Siaga - Video Edukasi PMK
 * Displays the list of educational video modules for PMK.
 * Stitch Screen ID: projects/10808370107038581899/screens/a4d9412fe5564a1796d5857c4d586db3
 */
@Composable
fun PmkVideoListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToEducation: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val creamBg = Color(0xFFFAF8F5)
    val darkText = Color(0xFF1E293B)
    val cardBorder = Color(0xFFF1EFEA)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = creamBg,
        topBar = {
            Surface(
                color = creamBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = darkText,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = "Video Edukasi PMK",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = darkText,
                        fontSize = 17.sp,
                        textAlign = TextAlign.Center,
                        letterSpacing = (-0.2).sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 36.dp)
                    )
                }
            }
        },
        bottomBar = {
            HomeBottomBar(
                currentTab = HomeTab.PMK,
                onTabSelected = { tab ->
                    when (tab) {
                        HomeTab.BERANDA -> onNavigateToHome()
                        HomeTab.PMK -> onNavigateBack()
                        HomeTab.EDUKASI -> onNavigateToEducation()
                        else -> { /* No-op for other uninitialized tabs */ }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(
                items = PmkVideoDataSource.videos,
                key = { it.id }
            ) { video ->
                PmkVideoModuleCard(
                    video = video,
                    onClick = { onNavigateToDetail(video.id) },
                    borderColor = cardBorder,
                    titleColor = darkText
                )
            }
        }
    }
}

@Composable
private fun PmkVideoModuleCard(
    video: PmkVideoItem,
    onClick: () -> Unit,
    borderColor: Color,
    titleColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail with duration tag
            Box(
                modifier = Modifier
                    .size(width = 100.dp, height = 68.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE2E8F0))
            ) {
                Image(
                    painter = painterResource(id = video.thumbnailRes),
                    contentDescription = video.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Duration badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 6.dp, bottom = 6.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.65f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = video.duration,
                        color = White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Video Title
            Text(
                text = video.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = titleColor,
                fontSize = 14.sp,
                lineHeight = 19.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 6.dp)
            )

            // Chevron Right
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Buka Video",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
