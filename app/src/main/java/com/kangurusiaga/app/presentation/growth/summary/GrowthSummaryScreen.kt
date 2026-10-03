package com.kangurusiaga.app.presentation.growth.summary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import com.kangurusiaga.app.core.designsystem.theme.TextPrimary
import com.kangurusiaga.app.core.designsystem.theme.TextSecondary
import com.kangurusiaga.app.core.designsystem.theme.TextTertiary
import com.kangurusiaga.app.core.designsystem.theme.White
import com.kangurusiaga.app.presentation.growth.components.GrowthIcons
import java.util.Locale

@Composable
fun GrowthSummaryScreen(
    onNavigateBack: () -> Unit,
    viewModel: GrowthSummaryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val baby = uiState.baby

    Scaffold(
        containerColor = White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            // Header
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
                        contentDescription = "Kembali",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Ringkasan Pertumbuhan",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.size(36.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Baby Profile Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFF0F3))
                        .border(1.5.dp, Color(0xFFF1F5F9), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_kangaroo_mascot),
                        contentDescription = null,
                        modifier = Modifier.size(42.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = baby?.name ?: "Nirmala",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = uiState.chronologicalAgeDisplay.ifBlank { "Usia 12 minggu (koreksi)" },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    val birthGram = baby?.birthWeightGram ?: 1850
                    Text(
                        text = "Lahir $birthGram gram • Prematur",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextTertiary,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3-Column Summary Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1: Berat Badan
                val weightVal = uiState.latestWeight?.value?.let {
                    String.format(Locale("id", "ID"), "%.1f", it)
                } ?: "--"
                val weightP = uiState.latestWeight?.percentileBadge?.let { "Persentil $it" } ?: "-"
                val weightStatus = if (uiState.latestWeight != null) "Normal" else "Belum diukur"
                MetricSummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Berat Badan",
                    value = weightVal,
                    unit = "kg",
                    percentile = weightP,
                    status = weightStatus,
                    icon = GrowthIcons.Weight,
                    cardBg = GrowthCardRed,
                    cardBorder = Color(0xFFFFCCD5),
                    iconTint = BrandPink
                )

                // Card 2: Panjang Badan
                val lengthVal = uiState.latestLength?.value?.let {
                    String.format(Locale("id", "ID"), "%.1f", it)
                } ?: "--"
                val lengthP = uiState.latestLength?.percentileBadge?.let { "Persentil $it" } ?: "-"
                val lengthStatus = if (uiState.latestLength != null) "Normal" else "Belum diukur"
                MetricSummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Panjang Badan",
                    value = lengthVal,
                    unit = "cm",
                    percentile = lengthP,
                    status = lengthStatus,
                    icon = GrowthIcons.Length,
                    cardBg = GrowthCardGreen,
                    cardBorder = Color(0xFFDCF5E6),
                    iconTint = Color(0xFF10B981)
                )

                // Card 3: Lingkar Kepala
                val headVal = uiState.latestHead?.value?.let {
                    String.format(Locale("id", "ID"), "%.1f", it)
                } ?: "--"
                val headP = uiState.latestHead?.percentileBadge?.let { "Persentil $it" } ?: "-"
                val headStatus = if (uiState.latestHead != null) "Normal" else "Belum diukur"
                MetricSummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Lingkar Kepala",
                    value = headVal,
                    unit = "cm",
                    percentile = headP,
                    status = headStatus,
                    icon = GrowthIcons.Head,
                    cardBg = GrowthCardBlue,
                    cardBorder = Color(0xFFDFEEFF),
                    iconTint = Color(0xFF3B82F6)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Status Banner Fenton
            val babyName = baby?.name ?: "si kecil"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFFFDF3))
                    .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Secara umum, pertumbuhan $babyName sesuai dengan grafik Fenton.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF78350F),
                    lineHeight = 17.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Progress from Birth Section
            Text(
                text = "Perkembangan dari Lahir",
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Row 1: Berat Badan
            val birthWeightKg = (baby?.birthWeightGram ?: 1850) / 1000f
            val birthWeightGram = baby?.birthWeightGram ?: 1850
            val (weightGainStr, weightFromToStr) = if (uiState.latestWeight != null) {
                val curWeightKg = uiState.latestWeight!!.value
                val weightDiff = curWeightKg - birthWeightKg
                val gain = if (weightDiff >= 0) "+${String.format(Locale("id", "ID"), "%.2f", weightDiff)} kg"
                else "${String.format(Locale("id", "ID"), "%.2f", weightDiff)} kg"
                val curWeightGram = (curWeightKg * 1000).toInt()
                gain to "$birthWeightGram g → $curWeightGram g"
            } else {
                "-" to "$birthWeightGram g → -"
            }

            ProgressFromBirthRow(
                title = "Berat Badan",
                gainText = weightGainStr,
                fromToText = weightFromToStr,
                icon = GrowthIcons.Weight,
                iconBg = GrowthCardRed,
                iconTint = BrandPink
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: Panjang Badan
            val birthLen = baby?.birthLengthCm ?: 0f
            val (lenGainStr, lenFromToStr) = if (uiState.latestLength != null) {
                val curLen = uiState.latestLength!!.value
                val lenDiff = if (birthLen > 0f) curLen - birthLen else 0f
                val gain = if (lenDiff >= 0) "+${String.format(Locale("id", "ID"), "%.1f", lenDiff)} cm"
                else "${String.format(Locale("id", "ID"), "%.1f", lenDiff)} cm"
                val fromTo = if (birthLen > 0f) {
                    "${String.format(Locale("id", "ID"), "%.1f", birthLen)} cm → ${String.format(Locale("id", "ID"), "%.1f", curLen)} cm"
                } else {
                    "${String.format(Locale("id", "ID"), "%.1f", curLen)} cm"
                }
                gain to fromTo
            } else {
                "-" to if (birthLen > 0f) "${String.format(Locale("id", "ID"), "%.1f", birthLen)} cm → -" else "Belum diukur"
            }

            ProgressFromBirthRow(
                title = "Panjang Badan",
                gainText = lenGainStr,
                fromToText = lenFromToStr,
                icon = GrowthIcons.Length,
                iconBg = GrowthCardGreen,
                iconTint = Color(0xFF10B981)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Row 3: Lingkar Kepala
            val birthHead = baby?.birthHeadCircumferenceCm ?: 0f
            val (headGainStr, headFromToStr) = if (uiState.latestHead != null) {
                val curHead = uiState.latestHead!!.value
                val headDiff = if (birthHead > 0f) curHead - birthHead else 0f
                val gain = if (headDiff >= 0) "+${String.format(Locale("id", "ID"), "%.1f", headDiff)} cm"
                else "${String.format(Locale("id", "ID"), "%.1f", headDiff)} cm"
                val fromTo = if (birthHead > 0f) {
                    "${String.format(Locale("id", "ID"), "%.1f", birthHead)} cm → ${String.format(Locale("id", "ID"), "%.1f", curHead)} cm"
                } else {
                    "${String.format(Locale("id", "ID"), "%.1f", curHead)} cm"
                }
                gain to fromTo
            } else {
                "-" to if (birthHead > 0f) "${String.format(Locale("id", "ID"), "%.1f", birthHead)} cm → -" else "Belum diukur"
            }

            ProgressFromBirthRow(
                title = "Lingkar Kepala",
                gainText = headGainStr,
                fromToText = headFromToStr,
                icon = GrowthIcons.Head,
                iconBg = GrowthCardBlue,
                iconTint = Color(0xFF3B82F6)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MetricSummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    unit: String,
    percentile: String,
    status: String,
    icon: ImageVector,
    cardBg: Color,
    cardBorder: Color,
    iconTint: Color
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = unit,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = percentile,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF16A34A)
        )
        Text(
            text = status,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF16A34A)
        )
    }
}

@Composable
private fun ProgressFromBirthRow(
    title: String,
    gainText: String,
    fromToText: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(White)
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFEAF7EE))
                    .border(1.dp, Color(0xFFD1F2DC), RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = gainText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF16A34A)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = fromToText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextTertiary
            )
        }
    }
}
