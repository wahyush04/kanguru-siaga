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
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.ShowChart
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kangurusiaga.app.core.designsystem.theme.BrandBackground
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.White

data class GuidelineSource(
    val title: String,
    val badgeText: String,
    val badgeColor: Color,
    val badgeBg: Color,
    val icon: ImageVector,
    val iconColor: Color,
    val iconBg: Color,
    val items: List<String>
)

@Composable
fun ClinicalGuidelinesScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sources = listOf(
        GuidelineSource(
            title = "Ikatan Dokter Anak Indonesia (IDAI)",
            badgeText = "Organisasi Profesi Spesialis Anak",
            badgeColor = Color(0xFF2563EB),
            badgeBg = Color(0xFFEFF6FF),
            icon = Icons.AutoMirrored.Filled.MenuBook,
            iconColor = Color(0xFF2563EB),
            iconBg = Color(0xFFEFF6FF),
            items = listOf(
                "Pedoman Pelayanan Medis: Tata Laksana Bayi Berat Lahir Rendah (BBLR) & Prematuritas.",
                "Rekomendasi Asuhan Nutrisi: Bayi Prematur dan Panduan Perawatan Metode Kanguru (PMK).",
                "Konsensus Triase Neonatal & Tanda Bahaya Bayi Pulang Rawat."
            )
        ),
        GuidelineSource(
            title = "Kementerian Kesehatan RI",
            badgeText = "Kemenkes RI • Standar Pelayanan Nasional",
            badgeColor = Color(0xFF059669),
            badgeBg = Color(0xFFECFDF5),
            icon = Icons.Default.Apartment,
            iconColor = Color(0xFF059669),
            iconBg = Color(0xFFECFDF5),
            items = listOf(
                "Buku Saku Pelayanan Neonatal Esensial: Protokol pencegahan hipotermia dan infeksi rumah tangga.",
                "Pedoman Teknis PMK: Panduan kontak kulit-ke-kulit (skin-to-skin) berkelanjutan di faskes & rawat jalan.",
                "SOP Perawatan Tali Pusat Kering Terbuka & Sanitasi Bayi Rentan."
            )
        ),
        GuidelineSource(
            title = "Fenton Preterm Growth Charts",
            badgeText = "Standar Internasional Pemantauan BBLR",
            badgeColor = BrandPink,
            badgeBg = Color(0xFFFFF1F2),
            icon = Icons.Default.ShowChart,
            iconColor = BrandPink,
            iconBg = Color(0xFFFFF1F2),
            items = listOf(
                "Fenton TR, et al. Preterm Growth Charts: Evaluasi parameter Z-Score dan Persentil (22–50 minggu PMA).",
                "Cole's LMS Method: Normalisasi statistik distribusi berat, panjang badan, dan lingkar kepala.",
                "Indikator klinis pemantauan Catch-up Growth selama masa rawat jalan."
            )
        ),
        GuidelineSource(
            title = "World Health Organization (WHO)",
            badgeText = "Badan Kesehatan Dunia • Pedoman Global",
            badgeColor = Color(0xFFD97706),
            badgeBg = Color(0xFFFFFBEB),
            icon = Icons.Default.Public,
            iconColor = Color(0xFFD97706),
            iconBg = Color(0xFFFFFBEB),
            items = listOf(
                "WHO Guidelines on Maternal and Newborn Health for Improved Outcomes.",
                "Kangaroo Mother Care: A practical guide for low birth weight infants.",
                "Thermal Control of the Newborn: Pencegahan morbiditas hipotermia dini pada bayi prematur."
            )
        )
    )

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
                        tint = Color(0xFF1E293B)
                    )
                }

                Text(
                    text = "Sumber Informasi & Panduan Klinis",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "PEDOMAN & LANDASAN TEORI RUJUKAN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                sources.forEach { source ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = White,
                        border = BorderStroke(1.dp, Color(0xFFF1ECE6)),
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(source.iconBg, RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = source.icon,
                                        contentDescription = null,
                                        tint = source.iconColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = source.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = source.badgeBg,
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Text(
                                            text = source.badgeText,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = source.badgeColor,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            source.items.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .padding(top = 6.dp)
                                            .background(BrandPink, CircleShape)
                                    )
                                    Text(
                                        text = item,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp,
                                        color = Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
