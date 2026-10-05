package com.kangurusiaga.app.presentation.growth.hub

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.BrandBackground
import com.kangurusiaga.app.core.designsystem.theme.BrandCardBorder
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.GrowthCardBlue
import com.kangurusiaga.app.core.designsystem.theme.GrowthCardGreen
import com.kangurusiaga.app.core.designsystem.theme.GrowthCardRed
import com.kangurusiaga.app.core.designsystem.theme.GrowthTipBg
import com.kangurusiaga.app.core.designsystem.theme.GrowthTipBorder
import com.kangurusiaga.app.core.designsystem.theme.GrowthTipText
import com.kangurusiaga.app.core.designsystem.theme.TextPrimary
import com.kangurusiaga.app.core.designsystem.theme.TextSecondary
import com.kangurusiaga.app.core.designsystem.theme.White
import com.kangurusiaga.app.domain.model.GrowthParameter
import com.kangurusiaga.app.presentation.growth.components.GrowthIcons
import com.kangurusiaga.app.presentation.growth.components.SelectGrowthParameterBottomSheet

@Composable
fun GrowthHubScreen(
    onNavigateBack: () -> Unit,
    onNavigateToChart: (GrowthParameter) -> Unit,
    onNavigateToAdd: (GrowthParameter) -> Unit,
    onNavigateToSummary: () -> Unit,
    onNavigateToAboutFenton: () -> Unit,
    viewModel: GrowthHubViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSelectSheet by remember { mutableStateOf(false) }

    if (showSelectSheet) {
        SelectGrowthParameterBottomSheet(
            onDismissRequest = { showSelectSheet = false },
            onConfirm = { parameter ->
                showSelectSheet = false
                onNavigateToAdd(parameter)
            }
        )
    }

    Scaffold(
        containerColor = BrandBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Navigation Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = GrowthIcons.Back,
                        contentDescription = stringResource(R.string.common_back),
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.growth_hub_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.growth_hub_subtitle),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Hero Illustration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.il_pertumbuhan_bayi),
                    contentDescription = stringResource(R.string.growth_hub_img_cd),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3 Measurement Cards
            GrowthCardItem(
                title = stringResource(R.string.growth_hub_weight_title),
                subtitle = stringResource(R.string.growth_hub_weight_sub),
                iconVector = GrowthIcons.Weight,
                iconBg = GrowthCardRed,
                iconTint = BrandPink,
                onClick = { onNavigateToChart(GrowthParameter.WEIGHT) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            GrowthCardItem(
                title = stringResource(R.string.growth_hub_length_title),
                subtitle = stringResource(R.string.growth_hub_length_sub),
                iconVector = GrowthIcons.Length,
                iconBg = GrowthCardGreen,
                iconTint = Color(0xFF10B981),
                onClick = { onNavigateToChart(GrowthParameter.LENGTH) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            GrowthCardItem(
                title = stringResource(R.string.growth_hub_head_title),
                subtitle = stringResource(R.string.growth_hub_head_sub),
                iconVector = GrowthIcons.Head,
                iconBg = GrowthCardBlue,
                iconTint = Color(0xFF3B82F6),
                onClick = { onNavigateToChart(GrowthParameter.HEAD_CIRCUMFERENCE) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Secondary CTA: Ringkasan Pertumbuhan
            OutlinedButton(
                onClick = onNavigateToSummary,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = White,
                    contentColor = BrandPink
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(BrandPink.copy(alpha = 0.5f)),
                    width = 1.5.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(
                    imageVector = GrowthIcons.Trending,
                    contentDescription = null,
                    tint = BrandPink,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.growth_hub_btn_summary),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandPink
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Primary CTA: + Tambah Data Pertumbuhan
            Button(
                onClick = { showSelectSheet = true },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPink),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp), spotColor = BrandPink.copy(alpha = 0.4f))
            ) {
                Text(
                    text = "+",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.growth_hub_btn_add),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Educational Card: Tentang Grafik Fenton
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFFF9F2))
                    .border(1.dp, Color(0xFFFDECD7), RoundedCornerShape(16.dp))
                    .clickable { onNavigateToAboutFenton() }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEF2DC)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = GrowthIcons.Lightbulb,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.growth_hub_about_card_title),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = stringResource(R.string.growth_hub_about_card_desc),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextSecondary,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = GrowthIcons.ChevronRight,
                    contentDescription = null,
                    tint = BrandPink,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun GrowthCardItem(
    title: String,
    subtitle: String,
    iconVector: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(White)
            .border(1.dp, BrandCardBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
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

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF9CA3AF),
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Icon(
            imageVector = GrowthIcons.ChevronRight,
            contentDescription = null,
            tint = Color(0xFFCBD5E1),
            modifier = Modifier.size(18.dp)
        )
    }
}
