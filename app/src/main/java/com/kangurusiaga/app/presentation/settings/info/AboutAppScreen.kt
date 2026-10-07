package com.kangurusiaga.app.presentation.settings.info

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.BrandBackground
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.core.designsystem.theme.TextPrimary
import com.kangurusiaga.app.core.designsystem.theme.TextSecondary
import com.kangurusiaga.app.core.designsystem.theme.TextTertiary

private data class PillarItem(
    val title: String,
    val description: AnnotatedString,
    val icon: ImageVector,
    val iconColor: Color,
    val iconBg: Color
)

@Composable
fun AboutAppScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pillars = remember {
        listOf(
            PillarItem(
                title = "Perawatan Metode Kanguru (PMK)",
                description = buildAnnotatedString {
                    append("Timer terintegrasi untuk melacak target kontak kulit ")
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append("skin-to-skin")
                    }
                    append(" harian, tutorial video langkah demi langkah, dan pencatatan riwayat sesi.")
                },
                icon = Icons.Default.Favorite,
                iconColor = BrandPink,
                iconBg = Color(0xFFFFF0F2)
            ),
            PillarItem(
                title = "Panduan Edukasi Komprehensif BBLR",
                description = buildAnnotatedString {
                    append("9 modul perawatan esensial di rumah mulai dari termoregulasi suhu, kebersihan tali pusat, hingga teknik menyusui.")
                },
                icon = Icons.AutoMirrored.Filled.MenuBook,
                iconColor = Color(0xFF059669),
                iconBg = Color(0xFFECFDF5)
            ),
            PillarItem(
                title = "Pemantauan Grafik Fenton Terkoreksi",
                description = buildAnnotatedString {
                    append("Kalkulasi kurva pertumbuhan persentil khusus bayi prematur (Berat, Panjang Badan, Lingkar Kepala) sesuai usia gestasi koreksi.")
                },
                icon = Icons.AutoMirrored.Filled.ShowChart,
                iconColor = Color(0xFFF43F5E),
                iconBg = Color(0xFFFFF1F2)
            ),
            PillarItem(
                title = "Deteksi Dini Tanda Bahaya & Kegawatan",
                description = buildAnnotatedString {
                    append("Triase cepat mengenali apnea, perubahan warna kulit, kejang, dan hipotermia untuk mempercepat tindakan rujukan medis.")
                },
                icon = Icons.Default.Warning,
                iconColor = Color(0xFFD97706),
                iconBg = Color(0xFFFFFBEB)
            )
        )
    }

    val primaryTextColor = TextPrimary

    val referenceStandards = remember(primaryTextColor) {
        listOf(
            buildAnnotatedString {
                append("Pedoman Klinis Ikatan Dokter Anak Indonesia (")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = primaryTextColor)) {
                    append("IDAI")
                }
                append(")")
            },
            buildAnnotatedString {
                append("Buku Pedoman Pelayanan Neonatal Esensial ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = primaryTextColor)) {
                    append("Kemenkes RI")
                }
            },
            buildAnnotatedString {
                append("Standar Pertumbuhan Prematur ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = primaryTextColor)) {
                    append("Fenton Growth Charts (2013)")
                }
            },
            buildAnnotatedString {
                append("World Health Organization (")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = primaryTextColor)) {
                    append("WHO")
                }
                append(") Kangaroo Mother Care Guide")
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ==========================================
            // TOP APP BAR NAVIGATION (Stitch Layout)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Back Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(KanguruTheme.colors.surface)
                        .border(1.dp, KanguruTheme.colors.cardBorder, CircleShape)
                        .clickable(onClick = onNavigateBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.common_back),
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Centered Title
                Text(
                    text = stringResource(R.string.about_app_title),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                // Spacer for balance
                Spacer(modifier = Modifier.size(40.dp))
            }

            HorizontalDivider(
                color = KanguruTheme.colors.cardBorder.copy(alpha = 0.5f),
                thickness = 1.dp
            )

            // ==========================================
            // SCROLLABLE CONTENT
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // ==========================================
                // 1. HERO IDENTITY CARD
                // ==========================================
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = KanguruTheme.colors.surface,
                    border = BorderStroke(1.dp, KanguruTheme.colors.cardBorder),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Official Kangaroo Mascot Logo
                        Image(
                            painter = painterResource(id = R.drawable.ic_kangaroo_mascot),
                            contentDescription = stringResource(R.string.about_app_title),
                            modifier = Modifier.size(92.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "KANGURU SIAGA",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary,
                            letterSpacing = 0.5.sp
                        )

                        Text(
                            text = stringResource(R.string.about_app_subtitle).uppercase(),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BrandPink,
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                        )

                        // Version Pill Badge
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFF0F2),
                            border = BorderStroke(1.dp, BrandPink.copy(alpha = 0.25f)),
                            modifier = Modifier.heightIn(min = 24.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(BrandPink, CircleShape)
                                )
                                Text(
                                    text = stringResource(R.string.about_app_version),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = BrandPink
                                )
                            }
                        }

                        // App Introduction Paragraph
                        val aboutDesc = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = primaryTextColor)) {
                                append("Kanguru Siaga")
                            }
                            append(" adalah aplikasi pendamping kesehatan digital yang dirancang khusus untuk memandu orang tua dan keluarga dalam merawat Bayi Berat Lahir Rendah (BBLR) secara mandiri, aman, dan penuh kasih sayang setelah kepulangan dari ruang perawatan intensif neonatus (NICU/Perinatologi).")
                        }
                        Text(
                            text = aboutDesc,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.padding(top = 12.dp, start = 4.dp, end = 4.dp)
                        )
                    }
                }

                // ==========================================
                // 2. VISI & KOMITMEN KAMI CARD
                // ==========================================
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = KanguruTheme.colors.surface,
                    border = BorderStroke(1.dp, KanguruTheme.colors.cardBorder),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFFFFF0F2), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = BrandPink,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = stringResource(R.string.about_app_vision_title),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Text(
                            text = stringResource(R.string.about_app_vision_desc),
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = TextSecondary
                        )
                    }
                }

                // ==========================================
                // 3. 4 PILAR LAYANAN UNGGULAN
                // ==========================================
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = stringResource(R.string.about_app_pillars_title).uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextTertiary,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    pillars.forEach { pillar ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = KanguruTheme.colors.surface,
                            border = BorderStroke(1.dp, KanguruTheme.colors.cardBorder),
                            shadowElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(pillar.iconBg, RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = pillar.icon,
                                        contentDescription = null,
                                        tint = pillar.iconColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = pillar.title,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = pillar.description,
                                        fontSize = 11.5.sp,
                                        lineHeight = 16.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 4. RUJUKAN & STANDAR MEDIS CARD
                // ==========================================
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = KanguruTheme.colors.surface,
                    border = BorderStroke(1.dp, KanguruTheme.colors.cardBorder),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = stringResource(R.string.about_app_references_title),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            referenceStandards.forEach { ref ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 6.dp)
                                            .size(6.dp)
                                            .background(BrandPink, CircleShape)
                                    )
                                    Text(
                                        text = ref,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp,
                                        color = TextSecondary,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 5. TIM PENGEMBANG & APRESIASI CARD
                // ==========================================
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFFFFF0F2).copy(alpha = 0.8f),
                                    KanguruTheme.colors.surface
                                )
                            )
                        )
                        .border(
                            BorderStroke(1.dp, BrandPink.copy(alpha = 0.2f)),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.about_app_dev_team_note),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = stringResource(R.string.about_app_dev_team_name),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = stringResource(R.string.about_app_copyright),
                            fontSize = 10.5.sp,
                            color = TextTertiary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
