package com.kangurusiaga.app.presentation.onboarding

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.BrandDarkPink
import com.kangurusiaga.app.core.designsystem.theme.BrandPink

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
    val creamBg = Color(0xFFFAF6F0)
    val titleGreen = Color(0xFF3A4D39)
    val subtitleSlate = Color(0xFF64748B)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(creamBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Center Mascot & Title Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Mascot container with soft warm organic circle
                Box(
                    modifier = Modifier.size(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFFFF0E8),
                                        Color(0xFFFBE4D8),
                                        Color(0x00FAF6F0)
                                    )
                                )
                            )
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_kangaroo_mascot),
                        contentDescription = "Maskot Kanguru Siaga",
                        modifier = Modifier.size(200.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Typography Section
                Text(
                    text = "KANGURU\nSIAGA",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleGreen,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Pendamping perawatan BBLR di rumah",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = subtitleSlate,
                    textAlign = TextAlign.Center
                )
            }

            // Bottom Actions & Progress
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mini bar indicator matching Stitch
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFFDE2E4))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.33f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(BrandPink)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Mulai CTA Button
                Button(
                    onClick = onStartClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandPink
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Mulai",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Mulai",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
