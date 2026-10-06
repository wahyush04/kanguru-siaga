package com.kangurusiaga.app.presentation.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.BrandBackground
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.CardBorder
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.core.designsystem.theme.TextPrimary
import com.kangurusiaga.app.core.designsystem.theme.TextSecondary
import com.kangurusiaga.app.core.designsystem.theme.White
import kotlinx.coroutines.launch

@Composable
fun OnboardingRoute(
    onNavigateToSetup: () -> Unit,
    modifier: Modifier = Modifier,
    initialPage: Int = 0
) {
    OnboardingPagerScreen(
        onNavigateToSetup = onNavigateToSetup,
        initialPage = initialPage,
        modifier = modifier
    )
}

/**
 * Unified 2-step onboarding pager screen:
 * Step 0: Kanguru Siaga - Selamat Datang
 * Step 1: Kanguru Siaga - Yuk, Mulai dengan Data Bayi
 *
 * Features:
 * - Shared interactive dot step progress indicator (Pill + Dot).
 * - Smooth swipe or button navigation between steps.
 * - Dynamic button copy ("Lanjut" on Step 0, "Isi Data Bayi" on Step 1).
 * - "Lewati" button removed as per requirements.
 * - Back button on Step 1 returns to Step 0.
 */
@Composable
fun OnboardingPagerScreen(
    onNavigateToSetup: () -> Unit,
    modifier: Modifier = Modifier,
    initialPage: Int = 0
) {
    val pagerState = rememberPagerState(initialPage = initialPage.coerceIn(0, 1)) { 2 }
    val coroutineScope = rememberCoroutineScope()

    // Handle system back button when on step 1 to smoothly return to step 0
    BackHandler(enabled = pagerState.currentPage > 0) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(0)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Navigation Bar (Back button only visible on page 1)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                if (pagerState.currentPage > 0) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.onboarding_cd_back_welcome),
                            tint = TextPrimary
                        )
                    }
                }
            }

            // Pager Content: 2 Pages
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                when (page) {
                    0 -> WelcomePageContent()
                    1 -> ProfileIntroPageContent()
                }
            }

            // Shared Step Progress Indicator (Dot + Pill)
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                // Step 0 Indicator
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(if (pagerState.currentPage == 0) 24.dp else 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (pagerState.currentPage == 0) BrandPink else CardBorder)
                        .animateContentSize()
                )
                Spacer(modifier = Modifier.width(6.dp))
                // Step 1 Indicator
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(if (pagerState.currentPage == 1) 24.dp else 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (pagerState.currentPage == 1) BrandPink else CardBorder)
                        .animateContentSize()
                )
            }

            // Bottom CTA Action Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        if (pagerState.currentPage == 0) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        } else {
                            onNavigateToSetup()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = Color(0xFFFF5C77).copy(alpha = 0.35f)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPink),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (pagerState.currentPage == 0) stringResource(R.string.onboarding_btn_next) else stringResource(R.string.onboarding_btn_fill_baby_data),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = White,
                            letterSpacing = 0.5.sp
                        )
                        if (pagerState.currentPage == 1) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Step 0 Content: Kanguru Siaga - Selamat Datang
 */
@Composable
private fun WelcomePageContent() {
    val isDark = KanguruTheme.colors.isDark
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Mother & Baby Illustration with soft ambient circular aura
        Box(
            modifier = Modifier.size(280.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = if (isDark) {
                                listOf(Color(0xFF3B252C), Color(0xFF2C2520))
                            } else {
                                listOf(Color(0xFFFFF0F3), Color(0xFFFFF6EA))
                            }
                        )
                    )
            )
            Image(
                painter = painterResource(id = R.drawable.il_welcome_mother_baby),
                contentDescription = stringResource(R.string.onboarding_welcome_img_cd),
                modifier = Modifier.size(240.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Heading
        Text(
            text = stringResource(R.string.onboarding_welcome_title),
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 34.sp,
            letterSpacing = (-0.5).sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Subtitle
        Text(
            text = stringResource(R.string.onboarding_welcome_desc),
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

/**
 * Step 1 Content: Kanguru Siaga - Yuk, Mulai dengan Data Bayi
 */
@Composable
private fun ProfileIntroPageContent() {
    val isDark = KanguruTheme.colors.isDark
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Clipboard with baby illustration & soft background glow
        Box(
            modifier = Modifier.size(260.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(230.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = if (isDark) {
                                listOf(
                                    Color(0xFF4A252F).copy(alpha = 0.6f),
                                    Color(0xFF382F1E).copy(alpha = 0.4f),
                                    Color(0xFF2B2024).copy(alpha = 0.8f)
                                )
                            } else {
                                listOf(
                                    Color(0xFFFFE4E6).copy(alpha = 0.6f),
                                    Color(0xFFFEF3C7).copy(alpha = 0.4f),
                                    Color(0xFFFFF0F2).copy(alpha = 0.8f)
                                )
                            }
                        )
                    )
            )
            Image(
                painter = painterResource(id = R.drawable.il_clipboard_baby),
                contentDescription = stringResource(R.string.onboarding_profile_intro_img_cd),
                modifier = Modifier.size(210.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = stringResource(R.string.onboarding_profile_intro_title),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 34.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.onboarding_profile_intro_desc),
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
