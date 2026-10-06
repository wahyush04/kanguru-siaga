package com.kangurusiaga.app.presentation.education

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.BrandBackground
import com.kangurusiaga.app.core.designsystem.theme.BrandLightPink
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.CardBorder
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.core.designsystem.theme.TextPrimary
import com.kangurusiaga.app.core.designsystem.theme.TextSecondary
import com.kangurusiaga.app.core.designsystem.theme.TextTertiary
import com.kangurusiaga.app.core.designsystem.theme.White
import com.kangurusiaga.app.presentation.home.HomeBottomBar
import com.kangurusiaga.app.presentation.home.HomeTab

/**
 * Screen: Kanguru Siaga - Edukasi (Edukasi & Panduan)
 * Source of Truth: Google Stitch Design
 * Screen ID: projects/10808370107038581899/screens/22e90a7470904effb5adc5d1e99ededf
 *
 * Central education hub for the Edukasi bottom navigation tab.
 * Acts as entry points to:
 * 1. Perawatan Metode Kanguru (PMK) -> PMK Module Hub
 * 2. Perawatan Bayi BBLR -> 9 BBLR Learning Modules
 * 3. Tanda Kegawatan pada BBLR -> 7 Danger Signs & Triage
 */
@Composable
fun EducationCenterScreen(
    onNavigateToPmk: () -> Unit,
    onNavigateToBblrEducation: () -> Unit,
    onNavigateToEmergencyWarning: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToAlarm: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // Search state
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Design System Colors matching Google Stitch HTML
    val isDark = KanguruTheme.colors.isDark
    val bgCream = BrandBackground
    val brandCoral = BrandPink
    val brandRoseSoft = if (isDark) BrandPink.copy(alpha = 0.2f) else Color(0xFFFFE4E8)
    val slate900 = TextPrimary
    val slate800 = TextPrimary
    val slate600 = TextSecondary
    val slate500 = TextSecondary
    val slate400 = TextTertiary
    val slate200 = CardBorder
    val slate100 = CardBorder

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(bgCream),
        containerColor = bgCream,
        bottomBar = {
            HomeBottomBar(
                currentTab = HomeTab.EDUKASI,
                onTabSelected = { tab ->
                    when (tab) {
                        HomeTab.BERANDA -> onNavigateToHome()
                        HomeTab.PMK -> onNavigateToPmk()
                        HomeTab.EDUKASI -> { /* Already here */ }
                        HomeTab.ALARM -> onNavigateToAlarm()
                        HomeTab.PROFIL -> onNavigateToProfile()
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header Section: Tag, Title, Subtitle, Search Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Small Pill Tag: Pusat Edukasi
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(brandRoseSoft)
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.edu_center_pill_tag),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = brandCoral
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.edu_center_header_title),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = slate900,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(R.string.edu_center_header_subtitle),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = slate500,
                        lineHeight = 17.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Search / Quick Action Button
                IconButton(
                    onClick = {
                        isSearchActive = !isSearchActive
                        if (!isSearchActive) searchQuery = ""
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(KanguruTheme.colors.surface)
                        .border(1.dp, if (isSearchActive) brandRoseSoft else slate200, RoundedCornerShape(14.dp))
                        .shadow(2.dp, RoundedCornerShape(14.dp))
                ) {
                    Icon(
                        imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = if (isSearchActive) stringResource(R.string.edu_center_search_close_cd) else stringResource(R.string.edu_center_search_cd),
                        tint = if (isSearchActive) brandCoral else slate600,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Expandable Search Bar
            AnimatedVisibility(
                visible = isSearchActive,
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(200))
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.edu_center_search_placeholder),
                            fontSize = 12.5.sp,
                            color = slate400
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = KanguruTheme.colors.surface,
                        unfocusedContainerColor = KanguruTheme.colors.surface,
                        focusedBorderColor = brandCoral,
                        unfocusedBorderColor = slate200,
                        focusedTextColor = slate800,
                        unfocusedTextColor = slate800
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
                )
            }

            // Hero Illustration Banner: "Belajar Merawat Si Kecil Tercinta"
            HeroIllustrationBanner(
                brandCoral = brandCoral,
                brandRoseSoft = brandRoseSoft,
                slate900 = slate900,
                slate600 = slate600
            )

            // Section Header: "PILIH TOPIK PEMBELAJARAN"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.edu_center_topic_section),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = slate400,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = stringResource(R.string.edu_center_modules_available),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = brandCoral
                )
            }

            // Filter items based on search query
            val query = searchQuery.trim().lowercase()
            val showPmk = query.isEmpty() || "pmk perawatan metode kanguru skin to skin video timer kontak kulit".contains(query)
            val showBblr = query.isEmpty() || "bblr bayi berat lahir rendah kehangatan asi tali pusat kebersihan panduan".contains(query)
            val showEmergency = query.isEmpty() || "kegawatan bahaya darurat triase rujukan siaga 24 jam tanda bahaya".contains(query)

            // Primary Education Menu Cards
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Menu Item 1: Perawatan Metode Kanguru (PMK)
                if (showPmk) {
                    EducationTopicCard(
                        title = stringResource(R.string.edu_center_pmk_title),
                        description = stringResource(R.string.edu_center_pmk_desc),
                        tagText = stringResource(R.string.edu_center_pmk_tag),
                        tagIcon = Icons.Default.SmartDisplay,
                        bulletText = stringResource(R.string.edu_center_pmk_bullet),
                        iconVector = Icons.Default.VolunteerActivism,
                        iconGradientColors = if (isDark) listOf(brandCoral.copy(alpha = 0.25f), brandCoral.copy(alpha = 0.15f)) else listOf(Color(0xFFFFE8EC), Color(0xFFFFD8DF)),
                        iconTint = brandCoral,
                        tagBgColor = if (isDark) brandCoral.copy(alpha = 0.2f) else Color(0xFFFFF0F2),
                        tagTextColor = brandCoral,
                        tagBorderColor = if (isDark) brandCoral.copy(alpha = 0.35f) else Color(0xFFFFE4E8),
                        cardBorderColor = slate100,
                        onClick = onNavigateToPmk
                    )
                }

                // Menu Item 2: Perawatan Bayi BBLR
                if (showBblr) {
                    val tealColor = Color(0xFF0D9488)
                    EducationTopicCard(
                        title = stringResource(R.string.edu_center_bblr_title),
                        description = stringResource(R.string.edu_center_bblr_desc),
                        tagText = stringResource(R.string.edu_center_bblr_tag),
                        tagIcon = Icons.AutoMirrored.Filled.MenuBook,
                        bulletText = stringResource(R.string.edu_center_bblr_bullet),
                        iconVector = Icons.AutoMirrored.Filled.MenuBook,
                        iconGradientColors = if (isDark) listOf(tealColor.copy(alpha = 0.25f), tealColor.copy(alpha = 0.15f)) else listOf(Color(0xFFE2F7F2), Color(0xFFCBF1E8)),
                        iconTint = tealColor,
                        tagBgColor = if (isDark) tealColor.copy(alpha = 0.2f) else Color(0xFFECFDF5),
                        tagTextColor = if (isDark) Color(0xFF34D399) else Color(0xFF047857),
                        tagBorderColor = if (isDark) tealColor.copy(alpha = 0.35f) else Color(0xFFD1FAE5),
                        cardBorderColor = slate100,
                        onClick = onNavigateToBblrEducation
                    )
                }

                // Menu Item 3: Tanda Kegawatan pada BBLR
                if (showEmergency) {
                    val amberColor = Color(0xFFD97706)
                    EducationTopicCard(
                        title = stringResource(R.string.edu_center_emergency_title),
                        description = stringResource(R.string.edu_center_emergency_desc),
                        tagText = stringResource(R.string.edu_center_emergency_tag),
                        tagIcon = Icons.Default.Warning,
                        bulletText = stringResource(R.string.edu_center_emergency_bullet),
                        iconVector = Icons.Default.Warning,
                        iconGradientColors = if (isDark) listOf(amberColor.copy(alpha = 0.25f), amberColor.copy(alpha = 0.15f)) else listOf(Color(0xFFFEF4DC), Color(0xFFFDE8B5)),
                        iconTint = amberColor,
                        tagBgColor = if (isDark) amberColor.copy(alpha = 0.2f) else Color(0xFFFEF3C7),
                        tagTextColor = if (isDark) Color(0xFFFBBF24) else Color(0xFF92400E),
                        tagBorderColor = if (isDark) amberColor.copy(alpha = 0.35f) else Color(0xFFFDE68A),
                        cardBorderColor = if (isDark) amberColor.copy(alpha = 0.35f) else Color(0xFFFEF08A).copy(alpha = 0.4f),
                        onClick = onNavigateToEmergencyWarning
                    )
                }

                if (!showPmk && !showBblr && !showEmergency) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.edu_center_no_results, searchQuery),
                            fontSize = 13.sp,
                            color = slate500,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Emergency Hotline Banner
            EmergencyHotlineBanner(
                onContactClick = {
                    dialEmergency(context)
                }
            )

            // Bottom spacing for comfortable scroll above navigation bar
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Hero Illustration Banner with warm pastel gradient, IDAI tag, and embrace illustration.
 */
@Composable
private fun HeroIllustrationBanner(
    brandCoral: Color,
    brandRoseSoft: Color,
    slate900: Color,
    slate600: Color
) {
    val isDark = KanguruTheme.colors.isDark
    val surfaceColor = KanguruTheme.colors.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isDark) CardBorder else brandRoseSoft, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isDark) {
                        Brush.linearGradient(
                            listOf(
                                KanguruTheme.colors.surface,
                                KanguruTheme.colors.surfaceVariant
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            listOf(
                                Color(0xFFFFF7F6),
                                Color(0xFFFFF0F2),
                                Color(0xFFF0FDF4)
                            )
                        )
                    }
                )
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Text Column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // IDAI Tag with pulsing dot
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(surfaceColor.copy(alpha = 0.95f))
                            .border(1.dp, if (isDark) CardBorder else brandRoseSoft, CircleShape)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PulsingEmeraldDot()
                        Text(
                            text = stringResource(R.string.edu_center_idai_tag),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = slate900
                        )
                    }

                    // Main Headline
                    Column {
                        Text(
                            text = stringResource(R.string.edu_center_hero_title_1),
                            fontSize = 16.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = slate900,
                            letterSpacing = (-0.3).sp
                        )
                        Text(
                            text = stringResource(R.string.edu_center_hero_title_2),
                            fontSize = 16.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = brandCoral,
                            letterSpacing = (-0.3).sp
                        )
                    }

                    // Subtext description
                    Text(
                        text = stringResource(R.string.edu_center_hero_desc),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = slate600,
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Mother & Baby Kangaroo Embrace Illustration with frame & heart badge
                Box(
                    modifier = Modifier
                        .size(94.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(86.dp)
                            .rotate(2.5f)
                            .shadow(4.dp, RoundedCornerShape(18.dp))
                            .clip(RoundedCornerShape(18.dp))
                            .background(surfaceColor)
                            .border(2.dp, surfaceColor, RoundedCornerShape(18.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.il_welcome_mother_baby),
                            contentDescription = stringResource(R.string.edu_center_hero_img_cd),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Decorative floating heart badge at bottom-left
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .size(24.dp)
                            .shadow(3.dp, CircleShape)
                            .clip(CircleShape)
                            .background(surfaceColor)
                            .border(1.dp, if (isDark) CardBorder else brandRoseSoft, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = brandCoral,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Reusable Topic Card matching Stitch layout & custom shadows.
 */
@Composable
private fun EducationTopicCard(
    title: String,
    description: String,
    tagText: String,
    tagIcon: ImageVector,
    bulletText: String,
    iconVector: ImageVector,
    iconGradientColors: List<Color>,
    iconTint: Color,
    tagBgColor: Color,
    tagTextColor: Color,
    tagBorderColor: Color,
    cardBorderColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = KanguruTheme.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Icon Container Box
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.linearGradient(iconGradientColors)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Description & Details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = description,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Tag Pill
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(tagBgColor)
                            .border(1.dp, tagBorderColor, CircleShape)
                            .padding(horizontal = 8.dp, vertical = 2.5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = tagIcon,
                            contentDescription = null,
                            tint = tagTextColor,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = tagText,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = tagTextColor
                        )
                    }

                    // Bullet Text
                    Text(
                        text = bulletText,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextTertiary
                    )
                }
            }

            // Chevron Right
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Emergency Hotline Banner matching Stitch CSS.
 */
@Composable
private fun EmergencyHotlineBanner(
    onContactClick: () -> Unit
) {
    val isDark = KanguruTheme.colors.isDark
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isDark) KanguruTheme.colors.infoBorder else Color(0xFFDBEAFE), RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isDark) {
                        Brush.horizontalGradient(
                            listOf(
                                KanguruTheme.colors.infoContainer,
                                KanguruTheme.colors.surface
                            )
                        )
                    } else {
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFFEFF6FF),
                                Color(0xFFEEF2FF)
                            )
                        )
                    }
                )
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF3B82F6)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = stringResource(R.string.edu_center_hotline_title),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = stringResource(R.string.edu_center_hotline_desc),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onContactClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB),
                    contentColor = White
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.heightIn(min = 34.dp)
            ) {
                Text(
                    text = stringResource(R.string.edu_center_hotline_btn),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Pulsing emerald green dot for "Standar IDAI & Kemenkes".
 */
@Composable
private fun PulsingEmeraldDot() {
    val transition = rememberInfiniteTransition(label = "pulse_emerald")
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
            .size(7.dp)
            .clip(CircleShape)
            .background(Color(0xFF10B981).copy(alpha = alpha))
    )
}

/**
 * Helper to launch dialer with emergency hotline.
 */
private fun dialEmergency(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:119")
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, context.getString(R.string.edu_center_hotline_toast), Toast.LENGTH_LONG).show()
    }
}
