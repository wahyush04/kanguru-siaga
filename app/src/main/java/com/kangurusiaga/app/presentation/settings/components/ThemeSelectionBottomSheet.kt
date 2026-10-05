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
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.White
import com.kangurusiaga.app.domain.model.settings.AppThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelectionBottomSheet(
    selectedTheme: AppThemeMode,
    onSelectTheme: (AppThemeMode) -> Unit,
    onApplyTheme: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                            text = "Tema Aplikasi",
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
                        text = "Pilih skema warna dan pencahayaan yang paling nyaman untuk mata Ayah & Bunda saat memantau si kecil.",
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

            // Body Options
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Option 1: Terang Hangat
                ThemeOptionCard(
                    title = "Terang Hangat",
                    badgeText = "Default • Ramah Bayi",
                    description = "Warna krem pastel hangat yang lembut, menenangkan dan tidak silau bagi mata bayi saat kontak kulit (PMK).",
                    icon = Icons.Default.LightMode,
                    iconColor = Color(0xFFF59E0B),
                    iconBgColor = Color(0xFFFEF3C7),
                    isSelected = selectedTheme == AppThemeMode.WARM_LIGHT,
                    swatches = listOf(Color(0xFFFFF9F6), Color(0xFFFF5C77), Color(0xFFF59E0B)),
                    onClick = { onSelectTheme(AppThemeMode.WARM_LIGHT) }
                )

                // Option 2: Mode Redup
                ThemeOptionCard(
                    title = "Mode Redup (Ruang Menyusui)",
                    badgeText = "Khusus Malam",
                    description = "Latar gelap lembut dengan kontras rendah, ideal digunakan saat memantau suhu, timer PMK, atau jadwal ASI tanpa mengganggu tidur bayi.",
                    icon = Icons.Default.NightlightRound,
                    iconColor = Color(0xFFFCD34D),
                    iconBgColor = Color(0xFF1E293B),
                    isSelected = selectedTheme == AppThemeMode.DIM_NURSING,
                    swatches = listOf(Color(0xFF1E293B), Color(0xFFFDA4AF), Color(0xFF475569)),
                    onClick = { onSelectTheme(AppThemeMode.DIM_NURSING) }
                )

                // Option 3: Otomatis Sistem
                ThemeOptionCard(
                    title = "Otomatis (Sistem HP)",
                    badgeText = "Dinamis",
                    description = "Secara otomatis beralih antara Terang Hangat di siang hari dan Mode Redup pada malam hari mengikuti setelan perangkat Anda.",
                    icon = Icons.Default.BrightnessMedium,
                    iconColor = Color(0xFF475569),
                    iconBgColor = Color(0xFFF1F5F9),
                    isSelected = selectedTheme == AppThemeMode.SYSTEM,
                    gradientSwatch = Brush.horizontalGradient(
                        listOf(Color(0xFFFFFBF8), Color(0xFFFF8598), Color(0xFF1E293B))
                    ),
                    onClick = { onSelectTheme(AppThemeMode.SYSTEM) }
                )

                // Clinical Eye Tip Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFFBEB),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFFFEF3C7), RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "Pencahayaan redup dengan kontras lembut terbukti klinis membantu menjaga kestabilan tidur bayi prematur dan kenyamanan visual orang tua.",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF78350F)
                        )
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
                    onClick = onApplyTheme,
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
                        text = "Terapkan Tema",
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeOptionCard(
    title: String,
    badgeText: String,
    description: String,
    icon: ImageVector,
    iconColor: Color,
    iconBgColor: Color,
    isSelected: Boolean,
    swatches: List<Color> = emptyList(),
    gradientSwatch: Brush? = null,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) Color(0xFFFFFBF8) else White,
        border = BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) BrandPink else Color(0xFFE2E8F0)
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBgColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (isSelected) Color(0xFFFEF3C7) else Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, if (isSelected) Color(0xFFFDE68A) else Color(0xFFE2E8F0))
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color(0xFF92400E) else Color(0xFF475569),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Swatches preview
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Palet:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF94A3B8)
                    )
                    if (gradientSwatch != null) {
                        Box(
                            modifier = Modifier
                                .size(width = 48.dp, height = 12.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(gradientSwatch)
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(100.dp))
                        )
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy((-4).dp)) {
                            swatches.forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(color, CircleShape)
                                        .border(1.dp, White, CircleShape)
                                )
                            }
                        }
                    }
                }
            }

            // Radio indicator
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
