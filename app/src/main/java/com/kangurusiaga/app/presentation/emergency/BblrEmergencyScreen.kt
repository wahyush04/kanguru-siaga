package com.kangurusiaga.app.presentation.emergency

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.domain.model.emergency.EmergencySection
import com.kangurusiaga.app.domain.model.emergency.EmergencyWarningModule
import com.kangurusiaga.app.presentation.pmk.video.PmkVideoDataSource
import com.kangurusiaga.app.presentation.pmk.video.PmkVideoItem

/**
 * Screen 3: Kanguru Siaga - Tanda Kegawatan Bayi Kecil (BBLR)
 * Reference: Google Stitch projects/10808370107038581899/screens/9e90068e53a044de9f77285e49614b78
 *
 * Menu Ketiga di Beranda:
 * 1. Video edukasi utama (Video ID 5: "Tanda Kegawatan Bayi Berat Lahir Rendah")
 * 2. 7 Modul Materi & Panduan Triase Tanda Bahaya
 * 3. Protokol Cepat Tenaga Kesehatan (Standar IDAI & Kemenkes RI)
 * 4. Tombol Darurat Bawah: Hubungi Ambulans / Faskes Terdekat (119)
 */
@Composable
fun BblrEmergencyRoute(
    onNavigateBack: () -> Unit,
    onNavigateToVideo: (Int) -> Unit,
    onNavigateToModule: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EmergencyWarningListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BblrEmergencyScreen(
        modules = uiState.modules,
        isLoading = uiState.isLoading,
        onNavigateBack = onNavigateBack,
        onNavigateToVideo = onNavigateToVideo,
        onNavigateToModule = onNavigateToModule,
        modifier = modifier
    )
}

@Composable
fun BblrEmergencyScreen(
    modules: List<EmergencyWarningModule>,
    isLoading: Boolean,
    onNavigateBack: () -> Unit,
    onNavigateToVideo: (Int) -> Unit,
    onNavigateToModule: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography
    val context = LocalContext.current

    // Video ID 5: "5. Tanda Kegawatan Bayi Berat Lahir Rendah"
    val heroVideo: PmkVideoItem = PmkVideoDataSource.getVideoById(5)

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
                            text = stringResource(R.string.bblr_emergency_screen_title),
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
                                text = stringResource(R.string.bblr_emergency_pill_badge),
                                style = typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.headerPillRoseText
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = colors.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                // Sticky Hotline Call Button (119)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Button(
                        onClick = {
                            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:119"))
                            context.startActivity(dialIntent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        colors.emergencyStickyButtonStart,
                                        colors.emergencyStickyButtonEnd
                                    )
                                )
                            ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = colors.emergencyStickyButtonText
                        ),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = colors.emergencyStickyButtonText,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.bblr_emergency_sticky_call_button),
                                style = typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = colors.emergencyStickyButtonText
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
                    EmergencyHeroVideoCard(
                        video = heroVideo,
                        onWatchClick = { onNavigateToVideo(heroVideo.id) }
                    )
                }

                // 2. Section Header: Materi & Panduan Triase (7 Tanda Bahaya)
                item(key = "triase_section_header") {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.bblr_emergency_section_triase),
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
                                    text = stringResource(R.string.bblr_emergency_badge_triase_count),
                                    style = typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.badgeEmeraldText
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.bblr_emergency_section_triase_sub),
                            style = typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }

                // 3. 7 Danger Warning Modules (jarak antar item lebih rapat 8.dp)
                item(key = "triase_modules_list") {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        modules.forEach { module ->
                            EmergencyTriaseModuleCard(
                                module = module,
                                onClick = { onNavigateToModule(module.id) }
                            )
                        }
                    }
                }

                // 4. Protokol Cepat Tenaga Kesehatan Card
                item(key = "clinical_protocol_card") {
                    ClinicalProtocolCard()
                }

                item(key = "bottom_spacer") {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun EmergencyHeroVideoCard(
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
                                text = stringResource(R.string.bblr_emergency_hero_badge_video),
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
                        text = stringResource(R.string.bblr_emergency_hero_tag),
                        style = typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.bblr_emergency_hero_title),
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
                    text = stringResource(R.string.bblr_emergency_hero_desc),
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
private fun EmergencyTriaseModuleCard(
    module: EmergencyWarningModule,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val typography = KanguruTheme.typography

    // Resolved icon & color palette for each triase module
    val (iconBg, iconTint) = when (module.order) {
        1 -> colors.categoryTealContainer to colors.categoryTeal
        2 -> colors.categoryOrangeContainer to colors.categoryOrange
        3 -> colors.categoryPurpleContainer to colors.categoryPurple
        4 -> colors.categoryRoseContainer to colors.categoryRose
        5 -> colors.categoryOrangeContainer to colors.categoryOrange
        6 -> colors.errorContainer to colors.error
        else -> colors.categoryOrangeContainer to colors.categoryOrange
    }

    val iconVector = when (module.iconType) {
        "lungs" -> Icons.Default.VolunteerActivism
        "skin" -> Icons.Default.AcUnit
        "sleep" -> Icons.Default.Alarm
        "feeding" -> Icons.Default.LocalDrink
        "seizure" -> Icons.Default.Bolt
        "thermometer" -> Icons.Default.Thermostat
        else -> Icons.Default.Warning
    }

    // Snippet preview from symptoms
    val snippetText = when (module.order) {
        1 -> "Napas cepat (>60x/menit), tarikan dinding dada ke dalam, atau napas cuping hidung."
        2 -> "Bibir membiru (sianosis sentral), pucat pasi, atau kuning (ikterus) meluas ke kaki."
        3 -> "Tampak sangat lemas (letargis), lunglai, dan tidak merespons stimulasi fisik lembut."
        4 -> "Refleks hisap melemah drastis, tersedak berulang, atau muntah berwarna hijau/keruh."
        5 -> "Gerakan menghentak berulang, mata mendelik, atau gerakan mengecap-ngecap abnormal."
        6 -> "Hipotermia (<36,5°C) teraba dingin atau demam (>37,5°C) tidak turun dengan dekapan."
        else -> "Perut kembung tegang mengilat, tali pusat berbau busuk, atau perdarahan aktif."
    }

    val readingTime = when (module.order) {
        1, 3, 5, 6 -> "3 mnt baca"
        2, 7 -> "4 mnt baca"
        else -> "2 mnt baca"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, colors.cardBorder, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading Icon Container
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "MODUL 0${module.order}",
                        style = typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.primary
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = colors.textTertiary,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = readingTime,
                            style = typography.labelSmall,
                            color = colors.textTertiary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = module.title,
                    style = typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = snippetText,
                    style = typography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = colors.textTertiary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun ClinicalProtocolCard(
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
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
                        text = stringResource(R.string.bblr_emergency_protocol_title),
                        style = typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.pediatricNoteTitle
                    )
                    Text(
                        text = stringResource(R.string.bblr_emergency_protocol_subtitle),
                        style = typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = colors.pediatricNoteSubtitle
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.pediatricNoteQuoteCardBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = colors.pediatricNoteQuoteCardBg),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = stringResource(R.string.bblr_emergency_protocol_desc),
                    style = typography.bodySmall,
                    color = colors.pediatricNoteQuoteText,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}
