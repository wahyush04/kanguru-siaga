package com.kangurusiaga.app.presentation.settings.info

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kangurusiaga.app.core.designsystem.theme.BrandBackground
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.TextPrimary
import com.kangurusiaga.app.core.designsystem.theme.TextSecondary
import com.kangurusiaga.app.core.designsystem.theme.TextTertiary
import com.kangurusiaga.app.core.designsystem.theme.White

@Composable
fun AboutAppScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = TextPrimary
                    )
                }

                Text(
                    text = "Tentang KANGURU SIAGA",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Header Branding Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.surface,
                    border = BorderStroke(1.dp, com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.cardBorder),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.primaryContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = BrandPink,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "KANGURU SIAGA",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            letterSpacing = 0.5.sp
                        )

                        Text(
                            text = "Versi 1.2.0 (Build 2026)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextTertiary,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Mewujudkan setiap bayi prematur dan BBLR tumbuh optimal, hangat, dan sehat melalui pemberdayaan keluarga dengan panduan praktis berbasis bukti ilmiah klinis.",
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // 4 Pilar Layanan Unggulan
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "4 PILAR LAYANAN UNGGULAN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    PillarCard(
                        title = "Perawatan Metode Kanguru (PMK)",
                        description = "Timer terintegrasi untuk melacak target kontak kulit skin-to-skin harian, tutorial video langkah demi langkah, dan pencatatan riwayat sesi.",
                        icon = Icons.Default.Favorite,
                        iconColor = BrandPink,
                        iconBg = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.primaryContainer
                    )

                    PillarCard(
                        title = "Panduan Edukasi Komprehensif BBLR",
                        description = "Modul perawatan esensial di rumah mulai dari termoregulasi suhu, kebersihan tali pusat, hingga teknik menyusui.",
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        iconColor = Color(0xFF059669),
                        iconBg = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.successContainer
                    )

                    PillarCard(
                        title = "Pemantauan Grafik Fenton Terkoreksi",
                        description = "Kalkulasi kurva pertumbuhan persentil khusus bayi prematur (Berat, Panjang Badan, Lingkar Kepala) sesuai usia gestasi koreksi.",
                        icon = Icons.Default.ShowChart,
                        iconColor = Color(0xFFE11D48),
                        iconBg = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.primaryContainer
                    )

                    PillarCard(
                        title = "Deteksi Dini Tanda Bahaya & Kegawatan",
                        description = "Triase cepat mengenali apnea, perubahan warna kulit, kejang, dan hipotermia untuk mempercepat tindakan rujukan medis.",
                        icon = Icons.Default.Warning,
                        iconColor = Color(0xFFD97706),
                        iconBg = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.warningContainer
                    )
                }

                // Rujukan & Standar Medis Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.surface,
                    border = BorderStroke(1.dp, com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.cardBorder),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.infoContainer, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.infoText,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "Rujukan & Standar Medis",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        StandardItem("Pedoman Klinis Ikatan Dokter Anak Indonesia (IDAI)")
                        HorizontalDivider(color = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.divider, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                        StandardItem("Pedoman Pelayanan Neonatal Esensial Kementerian Kesehatan RI")
                        HorizontalDivider(color = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.divider, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                        StandardItem("Fenton Preterm Growth Charts (Systematic Review & Meta-Analysis)")
                        HorizontalDivider(color = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.divider, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                        StandardItem("World Health Organization (WHO) Kangaroo Mother Care Practical Guide")
                    }
                }

                // Footer
                Text(
                    text = "© 2026 KANGURU SIAGA • Hak Cipta Dilindungi Undang-Undang",
                    fontSize = 11.sp,
                    color = TextTertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun PillarCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color,
    iconBg: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.surface,
        border = BorderStroke(1.dp, com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.cardBorder),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(iconBg, RoundedCornerShape(10.dp)),
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
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun StandardItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(BrandPink, CircleShape)
        )
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary
        )
    }
}
