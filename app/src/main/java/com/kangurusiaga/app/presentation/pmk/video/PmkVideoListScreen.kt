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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.presentation.home.HomeBottomBar
import com.kangurusiaga.app.presentation.home.HomeTab

/**
 * Screen: Kanguru Siaga - Video Edukasi PMK (Menu Pertama)
 * Khusus menampilkan list video perawatan metode kanguru (PMK):
 * 1. Pengertian PMK
 * 2. Manfaat PMK untuk bayi BBLR
 * 3. Persiapan sebelum PMK
 * Sesuai referensi Google Stitch: projects/10808370107038581899/screens/9739596850790247066
 */
@Composable
fun PmkVideoListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToEducation: () -> Unit = {},
    onNavigateToPmk: () -> Unit = onNavigateBack,
    onNavigateToAlarm: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    // Khusus 3 video PMK sesuai spesifikasi Menu Pertama
    val pmkVideos = PmkVideoDataSource.videos.filter { it.id in 1..3 }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = {
            Surface(
                color = colors.background,
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(colors.surface)
                            .border(1.dp, colors.cardBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back),
                            tint = colors.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = stringResource(R.string.pmk_video_list_title),
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 38.dp)
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
                        HomeTab.PMK -> { /* already here */ }
                        HomeTab.EDUKASI -> onNavigateToEducation()
                        HomeTab.ALARM -> onNavigateToAlarm()
                        HomeTab.PROFIL -> onNavigateToProfile()
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
                items = pmkVideos,
                key = { it.id }
            ) { video ->
                PmkVideoModuleCard(
                    video = video,
                    onClick = { onNavigateToDetail(video.id) }
                )
            }
        }
    }
}

@Composable
private fun PmkVideoModuleCard(
    video: PmkVideoItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail with duration badge
            Box(
                modifier = Modifier
                    .size(width = 100.dp, height = 68.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.surfaceVariant)
            ) {
                Image(
                    painter = painterResource(id = video.thumbnailRes),
                    contentDescription = video.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Duration badge overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 6.dp, bottom = 6.dp)
                        .background(
                            color = colors.heroDurationBadgeBg,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = video.duration,
                        style = typography.labelSmall,
                        color = colors.heroDurationBadgeText,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Video Title
            Text(
                text = video.title,
                style = typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 6.dp)
            )

            // Chevron Right Icon
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.pmk_video_cd_open),
                tint = colors.textTertiary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
