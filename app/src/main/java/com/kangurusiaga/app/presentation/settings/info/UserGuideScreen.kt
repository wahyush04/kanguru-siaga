package com.kangurusiaga.app.presentation.settings.info

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.BrandBackground
import com.kangurusiaga.app.core.designsystem.theme.BrandCardBorder
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.core.designsystem.theme.TextPrimary
import com.kangurusiaga.app.core.designsystem.theme.TextSecondary
import com.kangurusiaga.app.core.designsystem.theme.TextTertiary
import com.kangurusiaga.app.core.designsystem.theme.White

private data class FeatureCardData(
    val id: String,
    val tag: String,
    val title: String,
    val icon: ImageVector,
    val accentColor: Color,
    val iconBgColor: Color,
    val iconBorderColor: Color,
    val subheader: String
)

@Composable
fun UserGuideScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showInfoDialog by remember { mutableStateOf(false) }

    val expandedStates = remember {
        mutableStateMapOf<String, Boolean>().apply {
            put("pmk", true) // Default open first card as seen in Stitch
        }
    }

    val colors = KanguruTheme.colors
    val featureCards = remember(colors) {
        listOf(
            FeatureCardData(
                id = "pmk",
                tag = "METODE KANGGURU",
                title = "Perawatan Metode Kanguru (PMK)",
                icon = Icons.Default.Favorite,
                accentColor = colors.categoryRose,
                iconBgColor = colors.categoryRoseContainer,
                iconBorderColor = colors.categoryRoseBorder,
                subheader = "Langkah Penerapan PMK di Rumah:"
            ),
            FeatureCardData(
                id = "fenton",
                tag = "PERTUMBUHAN",
                title = "Pemantauan Grafik Fenton",
                icon = Icons.AutoMirrored.Filled.ShowChart,
                accentColor = colors.success,
                iconBgColor = colors.successContainer,
                iconBorderColor = colors.successBorder,
                subheader = "Cara Memantau Kurva Tumbuh Kembang:"
            ),
            FeatureCardData(
                id = "feeding",
                tag = "NUTRISI & JADWAL",
                title = "Pengingat & Alarm ASI (OGT/NGT)",
                icon = Icons.Default.LocalDrink,
                accentColor = colors.info,
                iconBgColor = colors.infoContainer,
                iconBorderColor = colors.infoBorder,
                subheader = "Panduan Pemberian ASI BBLR:"
            ),
            FeatureCardData(
                id = "emergency",
                tag = "KESELAMATAN KLINIS",
                title = "Triase Tanda Bahaya & Kegawatan",
                icon = Icons.Default.Warning,
                accentColor = colors.categoryOrange,
                iconBgColor = colors.categoryOrangeContainer,
                iconBorderColor = colors.categoryOrangeBorder,
                subheader = "Kenali 6 Tanda Kegawatan BBLR:"
            ),
            FeatureCardData(
                id = "profile",
                tag = "IDENTITAS & RIWAYAT",
                title = "Pengaturan & Profil Bayi",
                icon = Icons.Default.Person,
                accentColor = colors.categoryPurple,
                iconBgColor = colors.categoryPurpleContainer,
                iconBorderColor = colors.categoryPurpleBorder,
                subheader = "Kelola data penting anak Anda di menu Profil:"
            )
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
            // TOP APP BAR (Stitch Layout)
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
                        contentDescription = "Kembali",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Centered Title
                Text(
                    text = stringResource(R.string.user_guide_title),
                    style = KanguruTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                // Info Action Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { showInfoDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Informasi Panduan",
                        tint = TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // ==========================================
            // SCROLLABLE CONTENT
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. HERO INTRODUCTION BANNER
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = KanguruTheme.colors.surface),
                    border = BorderStroke(1.dp, KanguruTheme.colors.cardBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        KanguruTheme.colors.surface,
                                        KanguruTheme.colors.surface,
                                        KanguruTheme.colors.categoryRoseContainer
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Tag Badge
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = KanguruTheme.colors.primaryContainer,
                                border = BorderStroke(1.dp, KanguruTheme.colors.categoryRoseBorder)
                            ) {
                                Text(
                                    text = stringResource(R.string.user_guide_hero_badge),
                                    style = KanguruTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandPink,
                                    letterSpacing = 0.5.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            // Headline with highlighted brand text
                            Text(
                                text = buildAnnotatedString {
                                    append("Cara Mengoptimalkan ")
                                    withStyle(SpanStyle(color = BrandPink)) {
                                        append("Kanguru Siaga")
                                    }
                                },
                                style = KanguruTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )

                            // Description
                            Text(
                                text = stringResource(R.string.user_guide_hero_desc),
                                style = KanguruTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // 2. PETUNJUK FITUR UTAMA HEADER
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 2.dp, end = 2.dp, top = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.user_guide_features_title),
                        style = KanguruTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = KanguruTheme.colors.primaryContainer,
                        border = BorderStroke(1.dp, KanguruTheme.colors.categoryRoseBorder)
                    ) {
                        Text(
                            text = stringResource(R.string.user_guide_features_badge),
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = BrandPink,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                        )
                    }
                }

                // 3. CORE FEATURE ACCORDIONS (5 FITUR INTI)
                featureCards.forEach { card ->
                    val isExpanded = expandedStates[card.id] == true

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = KanguruTheme.colors.surface),
                        border = BorderStroke(1.dp, KanguruTheme.colors.cardBorder),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                    ) {
                        Column {
                            // Header Clickable
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expandedStates[card.id] = !isExpanded }
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Feature Accent Icon Container
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(card.iconBgColor)
                                            .border(1.dp, card.iconBorderColor, RoundedCornerShape(14.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = card.icon,
                                            contentDescription = null,
                                            tint = card.accentColor,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = card.tag,
                                            style = KanguruTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = card.accentColor,
                                            letterSpacing = 0.5.sp
                                        )
                                        Spacer(modifier = Modifier.height(1.dp))
                                        Text(
                                            text = card.title,
                                            style = KanguruTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = if (isExpanded) "Tutup" else "Buka",
                                    tint = TextTertiary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // Expandable Content Body
                            AnimatedVisibility(
                                visible = isExpanded,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                ) {
                                    HorizontalDivider(
                                        color = KanguruTheme.colors.divider,
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )

                                    Text(
                                        text = card.subheader,
                                        style = KanguruTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    // Render content tailored per card type
                                    when (card.id) {
                                        "pmk" -> {
                                            PmkGuideContent()
                                        }
                                        "fenton" -> {
                                            FentonGuideContent()
                                        }
                                        "feeding" -> {
                                            FeedingGuideContent()
                                        }
                                        "emergency" -> {
                                            EmergencyGuideContent()
                                        }
                                        "profile" -> {
                                            ProfileGuideContent()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. FAQ SECTION (TANYA JAWAB)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 2.dp, end = 2.dp, top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                        contentDescription = null,
                        tint = BrandPink,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = stringResource(R.string.user_guide_faq_title),
                        style = KanguruTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    FaqCardItem(
                        question = "Apakah data bayi saya tersimpan dengan aman?",
                        answer = "Ya, seluruh data medis dan pertumbuhan tersimpan secara aman di memori lokal ponsel Anda. Aplikasi ini tidak membagikan data sensitif anak tanpa izin Anda."
                    )
                    FaqCardItem(
                        question = "Bagaimana jika sesi PMK terlewat dicatat di timer?",
                        answer = "Jangan khawatir. Ayah Bunda dapat membuka menu Riwayat PMK lalu menekan tombol \"Catat Sesi Manual\" untuk memasukkan durasi yang sudah dilakukan sebelumnya."
                    )
                    FaqCardItem(
                        question = "Apakah aplikasi ini menggantikan anjuran dokter?",
                        answer = "Tidak. Kanguru Siaga dirancang sebagai asisten pencatatan mandiri keluarga di rumah. Segala keputusan medis dan rencana terapi tetap mengacu pada instruksi Dokter Spesialis Anak (Sp.A)."
                    )
                }

                // 5. SUPPORT & FEEDBACK CARD ("Butuh Bantuan Lebih Lanjut?")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = KanguruTheme.colors.surface),
                    border = BorderStroke(1.dp, KanguruTheme.colors.cardBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        KanguruTheme.colors.categoryRoseContainer,
                                        KanguruTheme.colors.categoryOrangeContainer
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Support Icon Circle
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(BrandPink.copy(alpha = 0.14f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SupportAgent,
                                    contentDescription = null,
                                    tint = BrandPink,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            // Text Content
                            Text(
                                text = stringResource(R.string.user_guide_support_title),
                                style = KanguruTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = stringResource(R.string.user_guide_support_desc),
                                style = KanguruTheme.typography.bodySmall,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            // Action Buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:119"))
                                        context.startActivity(dialIntent)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 42.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = BrandPink,
                                        contentColor = White
                                    )
                                ) {
                                    Text(
                                        text = stringResource(R.string.user_guide_support_btn_contact),
                                        style = KanguruTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                            data = Uri.parse("mailto:support@kangurusiaga.id?subject=Masukan%20Aplikasi%20Kanguru%20Siaga")
                                        }
                                        runCatching { context.startActivity(emailIntent) }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 42.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, KanguruTheme.colors.cardBorder),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = KanguruTheme.colors.surface,
                                        contentColor = TextPrimary
                                    )
                                ) {
                                    Text(
                                        text = stringResource(R.string.user_guide_support_btn_feedback),
                                        style = KanguruTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Info Dialog
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.user_guide_info_dialog_title),
                    style = KanguruTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.user_guide_info_dialog_desc),
                    style = KanguruTheme.typography.bodySmall,
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text(
                        text = "Tutup",
                        color = BrandPink,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = KanguruTheme.colors.surface
        )
    }
}

/**
 * PMK Accordion Content with 4 Steps and Amber Guideline Callout Box.
 */
@Composable
private fun PmkGuideContent() {
    val steps = listOf(
        "Siapkan baju berkancing depan atau kain gendong elastis khusus PMK yang bersih dan nyaman.",
        "Buka menu \"Timer PMK\" di bilah navigasi utama lalu tekan tombol Mulai PMK.",
        "Pantau durasi kontak kulit dada-ke-dada (skin-to-skin) minimal 60 menit per sesi agar suhu tubuh bayi stabil.",
        "Selesaikan sesi dan masukkan catatan penting (suhu tubuh, bayi pulas/menangis) sebelum menyimpan data."
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        steps.forEachIndexed { index, text ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(KanguruTheme.colors.categoryRoseContainer)
                        .border(1.dp, KanguruTheme.colors.categoryRoseBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (index + 1).toString(),
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = BrandPink
                    )
                }

                Text(
                    text = text,
                    style = KanguruTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Amber Alert Callout Box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(KanguruTheme.colors.warningContainer)
                .border(1.dp, KanguruTheme.colors.warningBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = KanguruTheme.colors.warning,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Pastikan posisi jalan napas bayi selalu tegak & leher tidak tertekuk saat kontak PMK.",
                style = KanguruTheme.typography.labelSmall,
                color = KanguruTheme.colors.onWarningContainer,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Fenton Curve Guide Content with Emerald Bullets.
 */
@Composable
private fun FentonGuideContent() {
    val items = listOf(
        "Timbang berat badan, ukur panjang badan, dan lingkar kepala secara rutin setiap minggu saat kontrol/posyandu.",
        "Sistem secara otomatis menghitung Usia Koreksi Prematur untuk memastikan penilaian akurat.",
        "Amati tren garis persentil (P10, P50, P90). Jika kurva mendatar atau turun di bawah garis P10 berturut-turut, konsultasikan dengan dokter."
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { text ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "•",
                    style = KanguruTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = KanguruTheme.colors.success
                )
                Text(
                    text = text,
                    style = KanguruTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Feeding Guide Content with Sky Blue Bullets.
 */
@Composable
private fun FeedingGuideContent() {
    val items = listOf(
        "Atur interval pemberian ASI rutin setiap 2 hingga 3 jam sekali sesuai anjuran medis guna mencegah dehidrasi & hipoglikemia.",
        "Aktifkan alarm suara agar tidak terlewat terutama di waktu malam.",
        "Catat volume ASI yang masuk (ml) dan metode pemberian (langsung menetek / cup-feeder / selang OGT/NGT)."
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { text ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "•",
                    style = KanguruTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = KanguruTheme.colors.info
                )
                Text(
                    text = text,
                    style = KanguruTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Emergency Triage Guide Content with 6 Symptoms Grid & Red Alert.
 */
@Composable
private fun EmergencyGuideContent() {
    val symptoms = listOf(
        "1. Napas cepat / retraksi dada",
        "2. Kulit biru / pucat dingin",
        "3. Suhu <36°C atau >37.5°C",
        "4. Sangat lemas / sulit bangun",
        "5. Kejang / gerakan berulang",
        "6. Muntah hijau / perut kembung"
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // 2-Column Grid (3 rows)
        for (i in symptoms.indices step 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Col 1
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(KanguruTheme.colors.errorContainer)
                        .border(1.dp, KanguruTheme.colors.errorBorder, RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 7.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = symptoms[i],
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = KanguruTheme.colors.onErrorContainer
                    )
                }

                // Col 2
                if (i + 1 < symptoms.size) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(KanguruTheme.colors.errorContainer)
                            .border(1.dp, KanguruTheme.colors.errorBorder, RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 7.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = symptoms[i + 1],
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = KanguruTheme.colors.onErrorContainer
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Red urgent notice
        Text(
            text = "⚠️ Jika menemukan salah satu tanda di atas, jangan tunda: segera bawa bayi ke Unit Gawat Darurat (UGD/IGD) rumah sakit terdekat.",
            style = KanguruTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = KanguruTheme.colors.error
        )
    }
}

/**
 * Profile & Settings Guide Content with Purple Bullets.
 */
@Composable
private fun ProfileGuideContent() {
    val items = listOf(
        "Lengkapi data tanggal lahir dan usia gestasi saat lahir (minggu) agar perhitungan usia koreksi berjalan presisi.",
        "Perbarui foto si kecil dan nama panggilan untuk tampilan beranda yang personal.",
        "Cadangkan (ekspor) ringkasan riwayat harian dalam format ringkas untuk diperlihatkan saat jadwal kontrol dokter."
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { text ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "•",
                    style = KanguruTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = KanguruTheme.colors.categoryPurple
                )
                Text(
                    text = text,
                    style = KanguruTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * FAQ Question & Answer Card Item.
 */
@Composable
private fun FaqCardItem(
    question: String,
    answer: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = KanguruTheme.colors.surface),
        border = BorderStroke(1.dp, KanguruTheme.colors.cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = question,
                style = KanguruTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = answer,
                style = KanguruTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}
