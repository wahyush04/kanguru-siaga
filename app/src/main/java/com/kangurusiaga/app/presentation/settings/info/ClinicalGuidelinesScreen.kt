package com.kangurusiaga.app.presentation.settings.info

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Public
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

private sealed interface GuidelineContent {
    data class Bullets(val items: List<AnnotatedString>) : GuidelineContent
    data class Paragraph(val text: AnnotatedString) : GuidelineContent
}

private data class GuidelineSource(
    val title: String,
    val badgeText: String,
    val badgeColor: Color,
    val badgeBg: Color,
    val icon: ImageVector,
    val iconColor: Color,
    val iconBg: Color,
    val iconBorder: Color,
    val content: GuidelineContent
)

@Composable
fun ClinicalGuidelinesScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryTextColor = TextPrimary

    val colors = KanguruTheme.colors
    val sources = remember(primaryTextColor, colors) {
        listOf(
            // 1. IDAI
            GuidelineSource(
                title = "Ikatan Dokter Anak Indonesia (IDAI)",
                badgeText = "Organisasi Profesi Spesialis Anak",
                badgeColor = colors.info,
                badgeBg = colors.infoContainer,
                icon = Icons.AutoMirrored.Filled.MenuBook,
                iconColor = colors.info,
                iconBg = colors.infoContainer,
                iconBorder = colors.infoBorder,
                content = GuidelineContent.Bullets(
                    listOf(
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = primaryTextColor)) {
                                append("Pedoman Pelayanan Medis: ")
                            }
                            append("Tata Laksana Bayi Berat Lahir Rendah (BBLR) & Prematuritas.")
                        },
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = primaryTextColor)) {
                                append("Rekomendasi Asuhan Nutrisi: ")
                            }
                            append("Bayi Prematur dan Panduan Perawatan Metode Kanguru (PMK).")
                        },
                        buildAnnotatedString {
                            append("Konsensus Triase Neonatal & Tanda Bahaya Bayi Pulang Rawat.")
                        }
                    )
                )
            ),
            // 2. Kemenkes RI
            GuidelineSource(
                title = "Kementerian Kesehatan RI",
                badgeText = "Kemenkes RI • Standar Pelayanan Nasional",
                badgeColor = colors.onSuccessContainer,
                badgeBg = colors.successContainer,
                icon = Icons.Default.Apartment,
                iconColor = colors.success,
                iconBg = colors.successContainer,
                iconBorder = colors.successBorder,
                content = GuidelineContent.Bullets(
                    listOf(
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = primaryTextColor)) {
                                append("Buku Saku Pelayanan Neonatal Esensial: ")
                            }
                            append("Protokol pencegahan hipotermia dan infeksi rumah tangga.")
                        },
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = primaryTextColor)) {
                                append("Pedoman Teknis PMK: ")
                            }
                            append("Panduan kontak kulit-ke-kulit ")
                            withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                                append("(skin-to-skin)")
                            }
                            append(" berkelanjutan di faskes & rawat jalan.")
                        },
                        buildAnnotatedString {
                            append("SOP Perawatan Tali Pusat Kering Terbuka & Sanitasi Bayi Rentan.")
                        }
                    )
                )
            ),
            // 3. WHO
            GuidelineSource(
                title = "World Health Organization (WHO)",
                badgeText = "Konsensus Klinis Global & UNICEF",
                badgeColor = colors.categoryTealText,
                badgeBg = colors.categoryTealContainer,
                icon = Icons.Default.Public,
                iconColor = colors.categoryTeal,
                iconBg = colors.categoryTealContainer,
                iconBorder = colors.categoryTealBorder,
                content = GuidelineContent.Bullets(
                    listOf(
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = primaryTextColor)) {
                                append("WHO Guidelines (2022): ")
                            }
                            append("Recommendations for Care of the Preterm or Low-Birth-Weight Infant.")
                        },
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = primaryTextColor)) {
                                append("Kangaroo Mother Care: ")
                            }
                            append("A Practical Guide (Departemen Kesehatan Reproduksi & Riset WHO).")
                        }
                    )
                )
            ),
            // 4. Fenton Growth Charts (2013)
            GuidelineSource(
                title = "Fenton Growth Charts (2013)",
                badgeText = "Validasi Tanis R. Fenton & Jae H. Kim",
                badgeColor = colors.categoryRoseText,
                badgeBg = colors.categoryRoseContainer,
                icon = Icons.AutoMirrored.Filled.ShowChart,
                iconColor = colors.primary,
                iconBg = colors.categoryRoseContainer,
                iconBorder = colors.categoryRoseBorder,
                content = GuidelineContent.Paragraph(
                    buildAnnotatedString {
                        append("Grafik pertumbuhan persentil spesifik preterm untuk berat badan, panjang badan, dan lingkar kepala bayi prematur berdasarkan ")
                        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                            append("usia gestasi terkoreksi")
                        }
                        append(" dari minggu ke-22 hingga minggu ke-50.")
                    }
                )
            ),
            // 5. Protokol Nutrisi & Pemberian ASI OGT/NGT
            GuidelineSource(
                title = "Protokol Nutrisi & Pemberian ASI OGT/NGT",
                badgeText = "Panduan Asuhan Nutrisi Neonatus",
                badgeColor = colors.onWarningContainer,
                badgeBg = colors.warningContainer,
                icon = Icons.Default.LocalDrink,
                iconColor = colors.warning,
                iconBg = colors.warningContainer,
                iconBorder = colors.warningBorder,
                content = GuidelineContent.Paragraph(
                    buildAnnotatedString {
                        append("Tata cara higienis pemberian perahan ASI (ASIP) melalui selang sonde lambung, pencegahan aspirasi & distensi abdomen, serta panduan transisi bertahap menuju menyusu langsung ")
                        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                            append("(direct breastfeeding)")
                        }
                        append(".")
                    }
                )
            )
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandBackground,
        topBar = {
            Surface(
                color = BrandBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
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
                        text = stringResource(R.string.clinical_guidelines_title),
                        style = KanguruTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )

                    // Spacer to balance back button and preserve center alignment
                    Spacer(modifier = Modifier.size(40.dp))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

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
                // Section Title
                Text(
                    text = stringResource(R.string.clinical_guidelines_section_title).uppercase(),
                    style = KanguruTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextTertiary,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )

                // 5 Clinical Reference Cards
                sources.forEach { source ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = KanguruTheme.colors.surface,
                        border = BorderStroke(1.dp, KanguruTheme.colors.cardBorder),
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                // Leading Icon Box
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(source.iconBg, RoundedCornerShape(12.dp))
                                        .border(1.dp, source.iconBorder, RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = source.icon,
                                        contentDescription = null,
                                        tint = source.iconColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Header: Title & Category Badge
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = source.title,
                                        style = KanguruTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = source.badgeBg,
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .heightIn(min = 20.dp)
                                    ) {
                                        Text(
                                            text = source.badgeText,
                                            style = KanguruTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Medium,
                                            color = source.badgeColor,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            // Body Content: Bullets or Paragraph
                            when (val content = source.content) {
                                is GuidelineContent.Bullets -> {
                                    Column(
                                        modifier = Modifier.padding(top = 10.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        content.items.forEach { item ->
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
                                                    text = item,
                                                    style = KanguruTheme.typography.bodySmall,
                                                    color = TextSecondary,
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        }
                                    }
                                }

                                is GuidelineContent.Paragraph -> {
                                    Text(
                                        text = content.text,
                                        style = KanguruTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // MEDICAL REVIEW BOARD CARD (Ditelaah Oleh Tenaga Medis)
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
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(KanguruTheme.colors.successContainer, CircleShape)
                                    .border(1.dp, KanguruTheme.colors.successBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MedicalServices,
                                    contentDescription = null,
                                    tint = KanguruTheme.colors.success,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.clinical_guidelines_medical_review_title),
                                    style = KanguruTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = stringResource(R.string.clinical_guidelines_medical_review_subtitle),
                                    style = KanguruTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(KanguruTheme.colors.surfaceVariant, RoundedCornerShape(12.dp))
                                .border(1.dp, KanguruTheme.colors.cardBorder.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            val reviewNote = remember(primaryTextColor) {
                                buildAnnotatedString {
                                    append("Materi pada aplikasi ini ditinjau dan divalidasi berkala bersama ")
                                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = primaryTextColor)) {
                                        append("Dokter Spesialis Anak (Sp.A) konsultan neonatologi")
                                    }
                                    append(" serta perawat perinatologi berpengalaman untuk menjamin ketepatan edukasi bagi orang tua di rumah.")
                                }
                            }
                            Text(
                                text = reviewNote,
                                style = KanguruTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // ==========================================
                // FOOTER DISCLAIMER & VERSION
                // ==========================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp, bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.clinical_guidelines_footer_notice),
                        style = KanguruTheme.typography.labelSmall,
                        color = TextTertiary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    )
                    Text(
                        text = stringResource(R.string.clinical_guidelines_footer_version),
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = TextTertiary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
