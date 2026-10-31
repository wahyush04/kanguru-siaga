package com.kangurusiaga.app.presentation.education

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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.domain.model.education.EducationModule
import com.kangurusiaga.app.presentation.education.components.EducationModuleCard
import com.kangurusiaga.app.presentation.pmk.video.PmkVideoDataSource
import com.kangurusiaga.app.presentation.pmk.video.PmkVideoItem

/**
 * Screen 2: Kanguru Siaga - Cara Perawatan Bayi Kecil (BBLR)
 * Reference: Google Stitch projects/10808370107038581899/screens/101d463be2264ea885a306015a5ee295
 *
 * Menu Kedua di Beranda:
 * 1. Video edukasi utama (Video ID 4: "Cara Perawatan Bayi Berat Lahir Rendah")
 * 2. 9 Modul Bacaan Lengkap BBLR
 * 3. Catatan Dokter Spesialis Anak (Sp.A) - Prinsip Emas Perawatan BBLR
 */
@Composable
fun BblrCareRoute(
    onNavigateBack: () -> Unit,
    onNavigateToVideo: (Int) -> Unit,
    onNavigateToModule: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EducationListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BblrCareScreen(
        modules = uiState.allModules,
        isLoading = uiState.isLoading,
        onNavigateBack = onNavigateBack,
        onNavigateToVideo = onNavigateToVideo,
        onNavigateToModule = onNavigateToModule,
        onToggleBookmark = viewModel::toggleBookmark,
        modifier = modifier
    )
}

@Composable
fun BblrCareScreen(
    modules: List<EducationModule>,
    isLoading: Boolean,
    onNavigateBack: () -> Unit,
    onNavigateToVideo: (Int) -> Unit,
    onNavigateToModule: (String) -> Unit,
    onToggleBookmark: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    // Video ID 4: "4. Cara Perawatan Bayi Berat Lahir Rendah"
    val heroVideo: PmkVideoItem = PmkVideoDataSource.getVideoById(4)

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

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.bblr_care_screen_title),
                            style = typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = colors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .background(colors.headerPillRoseBg, RoundedCornerShape(12.dp))
                                .border(1.dp, colors.headerPillRoseBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.bblr_care_pill_badge),
                                style = typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.headerPillRoseText
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colors.primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Hero Video Utama Card
                item(key = "hero_video_card") {
                    BblrHeroVideoCard(
                        video = heroVideo,
                        onWatchClick = { onNavigateToVideo(heroVideo.id) }
                    )
                }

                // 2. Section Header: Materi & Panduan Tertulis (9 Modul Lengkap)
                item(key = "modules_section_header") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.bblr_care_section_modules),
                                    style = typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                                Box(
                                    modifier = Modifier
                                        .background(colors.badgeEmeraldBg, RoundedCornerShape(12.dp))
                                        .border(1.dp, colors.badgeEmeraldBorder, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 7.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.bblr_care_badge_modules_count),
                                        style = typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.badgeEmeraldText
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.bblr_care_section_modules_sub),
                                style = typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }

                        Text(
                            text = stringResource(R.string.bblr_care_btn_download_pdf),
                            style = typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.primary,
                            modifier = Modifier
                                .clickable { /* download / view PDF action */ }
                                .padding(start = 8.dp, top = 4.dp, bottom = 4.dp)
                        )
                    }
                }

                // 3. 9 Modul BBLR Items (jarak antar item lebih rapat 8.dp)
                item(key = "modules_list") {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        modules.forEach { module ->
                            EducationModuleCard(
                                module = module,
                                onClick = { onNavigateToModule(module.id) },
                                onToggleBookmark = { onToggleBookmark(module.id) }
                            )
                        }
                    }
                }

                // 4. Dokter Spesialis Anak Insight Card
                item(key = "pediatrician_card") {
                    PediatricianInsightCard()
                }

                item(key = "bottom_spacer") {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun BblrHeroVideoCard(
    video: PmkVideoItem,
    onWatchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, colors.cardBorder, RoundedCornerShape(20.dp))
            .clickable(onClick = onWatchClick),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Video Thumbnail Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color(0xFF0F172A))
            ) {
                Image(
                    painter = painterResource(id = video.thumbnailRes),
                    contentDescription = video.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x33000000),
                                    Color(0x55000000),
                                    Color(0xEE090D16)
                                )
                            )
                        )
                )

                // Badges top-left: VIDEO + Duration
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(colors.heroTypeBadgeBg, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(colors.heroTypeBadgeText)
                            )
                            Text(
                                text = stringResource(R.string.bblr_care_hero_badge_video),
                                style = typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = colors.heroTypeBadgeText
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(colors.heroDurationBadgeBg, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = video.duration,
                            style = typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.heroDurationBadgeText
                        )
                    }
                }

                // Center glowing play button
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(colors.heroPlayButtonRing)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(colors.heroPlayButton)
                            .border(1.5.dp, colors.heroPlayButtonBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = stringResource(R.string.bblr_care_cta_watch),
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Bottom overlay title
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.bblr_care_hero_tag),
                        style = typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.bblr_care_hero_title),
                        style = typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Description and Doctor Verification Row
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Text(
                    text = stringResource(R.string.bblr_care_hero_desc),
                    style = typography.bodySmall,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = colors.pediatricNoteSubtitle,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stringResource(R.string.bblr_care_doctor_verified),
                            style = typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.pediatricNoteSubtitle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(onClick = onWatchClick)
                    ) {
                        Text(
                            text = stringResource(R.string.bblr_care_cta_watch),
                            style = typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.primary
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PediatricianInsightCard(
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, colors.pediatricNoteCardBorder, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = colors.pediatricNoteCardBg),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(colors.pediatricNoteIconContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = colors.pediatricNoteIconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = stringResource(R.string.bblr_care_doctor_note_title),
                        style = typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.pediatricNoteTitle
                    )
                    Text(
                        text = stringResource(R.string.bblr_care_doctor_note_subtitle),
                        style = typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = colors.pediatricNoteSubtitle
                    )
                }
            }

            Text(
                text = stringResource(R.string.bblr_care_doctor_note_quote),
                style = typography.bodySmall,
                color = colors.pediatricNoteQuoteText
            )
        }
    }
}
