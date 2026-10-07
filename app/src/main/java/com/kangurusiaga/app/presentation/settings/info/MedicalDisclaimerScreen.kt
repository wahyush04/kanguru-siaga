package com.kangurusiaga.app.presentation.settings.info

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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

@Composable
fun MedicalDisclaimerScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val primaryTextColor = TextPrimary

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
                    text = stringResource(R.string.disclaimer_title),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                // Placeholder for symmetrical balance
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
                // 1. HERO CARD: BUKAN PENGGANTI KONSULTASI MEDIS
                // ==========================================
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = KanguruTheme.colors.surface,
                    border = BorderStroke(1.dp, Color(0xFFFFD7DE)),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Badge Pill
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFF0F2),
                            border = BorderStroke(1.dp, Color(0xFFFFD7DE)),
                            modifier = Modifier.heightIn(min = 24.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = BrandPink,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = stringResource(R.string.disclaimer_badge).uppercase(),
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandPink,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = stringResource(R.string.disclaimer_hero_title),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            lineHeight = 23.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val heroDesc1 = remember(primaryTextColor) {
                            buildAnnotatedString {
                                append("Aplikasi ")
                                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = primaryTextColor)) {
                                    append("Kanguru Siaga")
                                }
                                append(" dirancang semata-mata sebagai media edukasi, pemantauan harian, dan alat bantu pencatatan mandiri bagi orang tua bayi BBLR (Bayi Berat Lahir Rendah) di rumah.")
                            }
                        }
                        Text(
                            text = heroDesc1,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = TextSecondary
                        )

                        HorizontalDivider(
                            color = Color(0xFFFFF0F2),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        val heroDesc2 = remember {
                            buildAnnotatedString {
                                append("Aplikasi ini ")
                                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = BrandPink)) {
                                    append("TIDAK MENGGANTIKAN")
                                }
                                append(" anjuran, pemeriksaan fisik langsung, diagnosis, ataupun pengawasan medis dari ")
                                withStyle(SpanStyle(fontWeight = FontWeight.Medium, color = primaryTextColor)) {
                                    append("dokter spesialis anak (Sp.A)")
                                }
                                append(", dokter umum, bidan, maupun tenaga perinatologi di fasilitas kesehatan.")
                            }
                        }
                        Text(
                            text = heroDesc2,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = TextSecondary
                        )
                    }
                }

                // ==========================================
                // 2. EMERGENCY ALERT SECTION (Situasi Kegawatan)
                // ==========================================
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFF4F5),
                    border = BorderStroke(1.dp, Color(0xFFFFCCD5)),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(BrandPink.copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = BrandPink,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.disclaimer_emergency_title),
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF881337)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            val emergencyText = remember {
                                buildAnnotatedString {
                                    append("Jika bayi mengalami sesak napas berat, kejang, henti napas (")
                                    withStyle(SpanStyle(fontStyle = FontStyle.Italic, fontWeight = FontWeight.Medium)) {
                                        append("apnea > 20 detik")
                                    }
                                    append("), kulit kebiruan (sianosis), atau suhu ekstrem yang tidak merespons penghangatan, ")
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("JANGAN MENUNGGU")
                                    }
                                    append(" arahan aplikasi. Segera bawa bayi ke Instalasi Gawat Darurat (IGD) Rumah Sakit terdekat!")
                                }
                            }
                            Text(
                                text = emergencyText,
                                fontSize = 11.5.sp,
                                lineHeight = 17.sp,
                                color = Color(0xFF4C0519)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Quick Call Action Chip
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFFDA4AF)),
                                modifier = Modifier
                                    .heightIn(min = 36.dp)
                                    .clickable {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:119")
                                        }
                                        context.startActivity(intent)
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Call,
                                        contentDescription = null,
                                        tint = Color(0xFFE11D48),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.disclaimer_emergency_btn),
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFBE123C)
                                    )
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 3. FEATURE BOUNDARIES SECTION (Batasan Penggunaan Fitur)
                // ==========================================
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = KanguruTheme.colors.surface,
                    border = BorderStroke(1.dp, KanguruTheme.colors.cardBorder),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Color(0xFFFFF8ED), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFFFDE4BE), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = Color(0xFFE67E22),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = stringResource(R.string.disclaimer_features_title),
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        HorizontalDivider(
                            color = KanguruTheme.colors.cardBorder.copy(alpha = 0.5f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        // Sub-item 1: Fenton
                        val fentonDesc = remember(primaryTextColor) {
                            buildAnnotatedString {
                                append("Kalkulasi persentil berat, panjang badan, dan lingkar kepala adalah perkiraan tren pemantauan pertumbuhan berdasarkan usia Gestasi Pasca Menstruasi (PMA). Hasil ini ")
                                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = primaryTextColor)) {
                                    append("bukan diagnosis klinis definitif")
                                }
                                append(" atas gangguan pertumbuhan atau malnutrisi tanpa pemeriksaan langsung oleh dokter anak.")
                            }
                        }
                        DisclaimerFeatureItem(
                            title = "Grafik Pertumbuhan Fenton (2013)",
                            description = fentonDesc
                        )

                        HorizontalDivider(
                            color = KanguruTheme.colors.cardBorder.copy(alpha = 0.4f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )

                        // Sub-item 2: PMK
                        val pmkDesc = remember {
                            buildAnnotatedString {
                                append("Durasi kontak kulit-ke-kulit (")
                                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                                    append("skin-to-skin")
                                }
                                append(") harus selalu mengutamakan kestabilan pernapasan, warna kulit, dan kenyamanan bayi. Jika bayi tampak rewel berat, merintih, atau lemas saat PMK, hentikan sesi dan lakukan evaluasi kondisi.")
                            }
                        }
                        DisclaimerFeatureItem(
                            title = "Timer & Target Perawatan Metode Kanguru (PMK)",
                            description = pmkDesc
                        )

                        HorizontalDivider(
                            color = KanguruTheme.colors.cardBorder.copy(alpha = 0.4f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )

                        // Sub-item 3: ASI & Sonde
                        val asiDesc = remember {
                            buildAnnotatedString {
                                append("Volume asupan yang dihitung kalkulator merupakan acuan umum asuhan nutrisi neonatus. Keputusan penambahan volume minum harian serta teknik pemberian ASI perah via OGT/NGT wajib mematuhi instruksi tertulis Dokter Penanggung Jawab Pasien (DPJP).")
                            }
                        }
                        DisclaimerFeatureItem(
                            title = "Jadwal & Estimasi Kebutuhan ASI / Sonde",
                            description = asiDesc
                        )
                    }
                }

                // ==========================================
                // 4. EVIDENCE ACCURACY SECTION (Akurasi & Pemutakhiran Klinis)
                // ==========================================
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = KanguruTheme.colors.surface,
                    border = BorderStroke(1.dp, KanguruTheme.colors.cardBorder),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFEFF6FF), RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0xFFDBEAFE), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.disclaimer_accuracy_title),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            val accuracyDesc = remember(primaryTextColor) {
                                buildAnnotatedString {
                                    append("Materi panduan disusun dengan merujuk pada protokol resmi Ikatan Dokter Anak Indonesia (IDAI), Kementerian Kesehatan Republik Indonesia, dan World Health Organization (WHO). Namun, ilmu kedokteran perinatologi senantiasa berkembang secara dinamis. Bila terdapat perbedaan rekomendasi spesifik antara aplikasi dan tim dokter rumah sakit tempat bayi dirawat, ")
                                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = primaryTextColor)) {
                                        append("selalu prioritaskan instruksi dokter rumah sakit Anda")
                                    }
                                    append(".")
                                }
                            }
                            Text(
                                text = accuracyDesc,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // ==========================================
                // 5. USER CONSENT SECTION (Pernyataan & Persetujuan Pengguna)
                // ==========================================
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = KanguruTheme.colors.surface,
                    border = BorderStroke(1.dp, KanguruTheme.colors.cardBorder),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFECFDF5), RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.disclaimer_consent_title),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Dengan melanjutkan penggunaan aplikasi Kanguru Siaga, Anda memahami dan menyetujui bahwa seluruh implementasi panduan di rumah dilakukan atas dasar kehati-hatian mandiri pengasuh, serta menyetujui batasan pertanggungjawaban pengembang dalam aspek klinis individual.",
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // ==========================================
                // 6. FOOTER
                // ==========================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.disclaimer_footer_note),
                        fontSize = 11.sp,
                        color = TextTertiary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.disclaimer_footer_version),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextTertiary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.disclaimer_footer_copyright),
                        fontSize = 10.sp,
                        color = TextTertiary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun DisclaimerFeatureItem(
    title: String,
    description: androidx.compose.ui.text.AnnotatedString
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(BrandPink, CircleShape)
            )
            Text(
                text = title,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            fontSize = 12.sp,
            lineHeight = 17.5.sp,
            color = TextSecondary,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}
