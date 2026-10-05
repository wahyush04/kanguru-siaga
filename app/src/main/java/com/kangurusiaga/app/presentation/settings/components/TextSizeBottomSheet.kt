package com.kangurusiaga.app.presentation.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.White
import com.kangurusiaga.app.domain.model.settings.AppTextScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextSizeBottomSheet(
    selectedScale: AppTextScale,
    onSelectScale: (AppTextScale) -> Unit,
    onApplyScale: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val stepIndex = when (selectedScale) {
        AppTextScale.SMALL -> 0f
        AppTextScale.STANDARD -> 1f
        AppTextScale.LARGE -> 2f
        AppTextScale.EXTRA_LARGE -> 3f
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 44.dp, height = 4.dp)
                        .background(Color(0xFFE2E8F0), CircleShape)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Ukuran Teks",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = Color(0xFFFFF1F2),
                            border = BorderStroke(1.dp, Color(0xFFFFCCD5))
                        ) {
                            Text(
                                text = "Tampilan",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BrandPink,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Sesuaikan ukuran huruf teks panduan dan angka pemantauan agar nyaman dibaca saat merawat si kecil.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFF1F5F9), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Stepper Slider Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFAF8F6),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "A",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8)
                            )

                            Slider(
                                value = stepIndex,
                                onValueChange = { value ->
                                    val newScale = when (value.toInt()) {
                                        0 -> AppTextScale.SMALL
                                        1 -> AppTextScale.STANDARD
                                        2 -> AppTextScale.LARGE
                                        else -> AppTextScale.EXTRA_LARGE
                                    }
                                    onSelectScale(newScale)
                                },
                                valueRange = 0f..3f,
                                steps = 2,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 12.dp),
                                colors = SliderDefaults.colors(
                                    thumbColor = BrandPink,
                                    activeTrackColor = BrandPink,
                                    inactiveTrackColor = Color(0xFFE2E8F0)
                                )
                            )

                            Text(
                                text = "A",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Kecil",
                                fontSize = 11.sp,
                                color = if (selectedScale == AppTextScale.SMALL) BrandPink else Color(0xFF94A3B8),
                                fontWeight = if (selectedScale == AppTextScale.SMALL) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = "Sedang (Standar)",
                                fontSize = 11.sp,
                                color = if (selectedScale == AppTextScale.STANDARD) BrandPink else Color(0xFF94A3B8),
                                fontWeight = if (selectedScale == AppTextScale.STANDARD) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = "Besar",
                                fontSize = 11.sp,
                                color = if (selectedScale == AppTextScale.LARGE) BrandPink else Color(0xFF94A3B8),
                                fontWeight = if (selectedScale == AppTextScale.LARGE) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = "Ekstra",
                                fontSize = 11.sp,
                                color = if (selectedScale == AppTextScale.EXTRA_LARGE) BrandPink else Color(0xFF94A3B8),
                                fontWeight = if (selectedScale == AppTextScale.EXTRA_LARGE) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                // 4 Radio Option Cards
                AppTextScale.entries.forEach { scale ->
                    val isSelected = selectedScale == scale
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectScale(scale) },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) Color(0xFFFFF1F2).copy(alpha = 0.5f) else White,
                        border = BorderStroke(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) BrandPink else Color(0xFFE2E8F0)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(
                                        if (isSelected) BrandPink else Color(0xFFF1F5F9),
                                        RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = scale.percentageLabel,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) White else Color(0xFF475569)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = scale.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    if (scale == AppTextScale.STANDARD) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFFFFF1F2),
                                            border = BorderStroke(1.dp, Color(0xFFFFCCD5))
                                        ) {
                                            Text(
                                                text = "Direkomendasikan",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BrandPink,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = scale.description,
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B),
                                    lineHeight = 15.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            // Radio Indicator
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(
                                        if (isSelected) BrandPink else Color.Transparent,
                                        CircleShape
                                    )
                                    .border(
                                        if (isSelected) 0.dp else 2.dp,
                                        if (isSelected) Color.Transparent else Color(0xFFCBD5E1),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Live Preview Card (Scaled dynamically in preview)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFF7F2),
                    border = BorderStroke(1.dp, Color(0xFFF1E5DE))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = BrandPink,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "PRATINJAU TEKS KLINIS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandPink,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Text(
                                text = "Skala ${selectedScale.percentageLabel}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = White.copy(alpha = 0.85f),
                            border = BorderStroke(1.dp, Color(0xFFF1ECE6))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Suhu normal bayi BBLR: 36,5°C - 37,5°C",
                                    fontSize = (13 * selectedScale.scaleFactor).sp,
                                    lineHeight = (18 * selectedScale.scaleFactor).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Jaga kontak kulit-ke-kulit (Kangaroo Mother Care) secara teratur untuk menjaga kestabilan suhu tubuh si kecil.",
                                    fontSize = (11 * selectedScale.scaleFactor).sp,
                                    lineHeight = (16 * selectedScale.scaleFactor).sp,
                                    color = Color(0xFF475569)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                ) {
                    Text(
                        text = "Batal",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569)
                    )
                }

                Button(
                    onClick = onApplyScale,
                    modifier = Modifier
                        .weight(1.5f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPink)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Terapkan Ukuran",
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                }
            }
        }
    }
}
