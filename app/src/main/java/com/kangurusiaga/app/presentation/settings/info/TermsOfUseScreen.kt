package com.kangurusiaga.app.presentation.settings.info

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.automirrored.filled.ContactSupport
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme

@Composable
fun TermsOfUseScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val readProgress = if (scrollState.maxValue > 0) {
        (scrollState.value.toFloat() / scrollState.maxValue.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = {
            Surface(
                color = colors.background,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth().statusBarsPadding()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable(onClick = onNavigateBack),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = colors.textPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f),
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Terms Of Use",
                                style = KanguruTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.size(40.dp))
                    }

                    // Reading Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(colors.legalSectionCardBg)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(readProgress)
                                .background(colors.primary)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Document Paper Container
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = colors.surface,
                border = BorderStroke(1.dp, colors.cardBorder),
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // ==========================================
                    // META / DOCUMENT HEADER
                    // ==========================================
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = null,
                                tint = colors.legalTealText,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "BAGIAN I • DOKUMEN RESMI",
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.legalTealText
                            )
                        }

                        Text(
                            text = "Ketentuan Penggunaan Aplikasi Kanguru Siaga",
                            style = KanguruTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )

                        Text(
                            text = "Terms of Use Agreement",
                            style = KanguruTheme.typography.titleSmall,
                            color = colors.textSecondary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Metadata Badge Block
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = colors.legalSectionCardBg
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = colors.textSecondary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = "Tanggal Berlaku:",
                                        style = KanguruTheme.typography.labelSmall,
                                        color = colors.textSecondary
                                    )
                                    Text(
                                        text = "9 November 2026",
                                        style = KanguruTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.textPrimary
                                    )
                                    Text(
                                        text = "•",
                                        style = KanguruTheme.typography.labelSmall,
                                        color = colors.textTertiary
                                    )
                                    Surface(
                                        shape = CircleShape,
                                        color = colors.legalRoseTintBg
                                    ) {
                                        Text(
                                            text = "Versi 1.0",
                                            style = KanguruTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = colors.primary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = colors.clinicalEmeraldSolid,
                                        modifier = Modifier
                                            .padding(top = 2.dp)
                                            .size(16.dp)
                                    )
                                    Text(
                                        text = "Kepatuhan: Sesuai UU No. 27 Tahun 2022 (UU Perlindungan Data Pribadi) dan Standar Etika Medis Perinatologi.",
                                        style = KanguruTheme.typography.bodySmall,
                                        color = colors.textPrimary
                                    )
                                }
                            }
                        }
                    }

                    // ==========================================
                    // PENGANTAR PERJANJIAN
                    // ==========================================
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = colors.legalSectionCardBg
                    ) {
                        Text(
                            text = "“Selamat datang di Kanguru Siaga. Dengan mengunduh, memasang (install), mengakses, atau menggunakan aplikasi mobile Kanguru Siaga (“Aplikasi”), Anda menyatakan bahwa Anda telah membaca, memahami, menyetujui, dan mengikatkan diri secara hukum pada seluruh syarat dan ketentuan yang tercantum dalam Ketentuan Penggunaan ini. Jika Anda tidak menyetujui sebagian atau seluruh ketentuan ini, mohon untuk tidak menggunakan Aplikasi ini.”",
                            style = KanguruTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = colors.textPrimary,
                            modifier = Modifier.padding(14.dp)
                        )
                    }

                    // ==========================================
                    // 1. PENERIMAAN KETENTUAN
                    // ==========================================
                    TermsSection(number = "1.", title = "Penerimaan Ketentuan") {
                        Text(
                            text = "Ketentuan Penggunaan ini merupakan perjanjian hukum yang sah dan mengikat antara Anda sebagai pengguna (“Pengguna” atau “Anda”) dan Tim Pengembang Kanguru Siaga (“Kami”). Perjanjian ini mengatur hak, kewajiban, batasan lisensi, serta tanggung jawab para pihak dalam memanfaatkan seluruh fitur, materi edukasi, kalkulator klinis pendukung, dan ekosistem digital Kanguru Siaga.",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )
                    }

                    // ==========================================
                    // 2. PENYANGKALAN MEDIS PENTING
                    // ==========================================
                    TermsSection(
                        number = "2.",
                        title = "Penyangkalan Medis Penting (Medical Disclaimer)"
                    ) {
                        // Warning Callout Box
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = colors.legalRoseTintBg
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Error,
                                        contentDescription = null,
                                        tint = colors.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "PERINGATAN MEDIS UTAMA",
                                        style = KanguruTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.primary
                                    )
                                }

                                Text(
                                    text = "APLIKASI KANGURU SIAGA BUKAN PERANGKAT MEDIS (MEDICAL DEVICE) DAN BUKAN PENGGANTI DOKTER, DOKTER SPESIALIS ANAK, ATAU TENAGA MEDIS PROFESIONAL.",
                                    style = KanguruTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.legalRoseHeadingText
                                )

                                Text(
                                    text = "Seluruh konten, penghitungan usia koreksi, timer kontak, kurva pertumbuhan, pengingat minum ASI, dan panduan edukasi disediakan semata-mata untuk tujuan informasi umum, pemantauan mandiri di rumah (home monitoring), serta sarana pendukung edukatif bagi orang tua.",
                                    style = KanguruTheme.typography.bodyMedium,
                                    color = colors.textPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Kondisi Kegawatan Darurat Bayi (Tanda Bahaya)",
                            style = KanguruTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )

                        Text(
                            text = "Jangan pernah menunda tindakan medis darurat berdasarkan data di Aplikasi. Segera bawa bayi ke Unit Gawat Darurat (UGD) atau fasilitas kesehatan terdekat jika Anda mengamati salah satu dari tanda bahaya berikut:",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            TermsIconBulletItem(
                                icon = Icons.Default.Emergency,
                                iconTint = colors.error,
                                text = boldPrefixText(
                                    "Apnea atau Henti Napas: ",
                                    "Bayi berhenti bernapas lebih dari 20 detik atau napas megap-megap/merintih (grunting)."
                                )
                            )
                            TermsIconBulletItem(
                                icon = Icons.Default.Emergency,
                                iconTint = colors.error,
                                text = boldPrefixText(
                                    "Sianosis: ",
                                    "Bibir, lidah, gusi, atau tubuh bayi membiru atau tampak sangat pucat kelabu."
                                )
                            )
                            TermsIconBulletItem(
                                icon = Icons.Default.Emergency,
                                iconTint = colors.error,
                                text = boldPrefixText(
                                    "Instabilitas Suhu Ekstrem: ",
                                    "Suhu ketiak di bawah 36,0°C (hipotermia berat/sedang) atau demam di atas 37,5°C yang tidak merespons penghangatan."
                                )
                            )
                            TermsIconBulletItem(
                                icon = Icons.Default.Emergency,
                                iconTint = colors.error,
                                text = boldPrefixText(
                                    "Letargis & Penurunan Kesadaran: ",
                                    "Bayi tampak lunglai, tidak aktif bergerak saat dirangsang, atau mengalami kejang/gerakan abnormal."
                                )
                            )
                            TermsIconBulletItem(
                                icon = Icons.Default.Emergency,
                                iconTint = colors.error,
                                text = boldPrefixText(
                                    "Intoleransi Minum Berat: ",
                                    "Malas menyusu total, muntah kehijauan berulang, atau perut membuncit tegang."
                                )
                            )
                        }

                        // Teal Protocol Priority Box
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = colors.legalTealSurfaceBg
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalHospital,
                                    contentDescription = null,
                                    tint = colors.legalTealAccent,
                                    modifier = Modifier
                                        .padding(top = 2.dp)
                                        .size(20.dp)
                                )
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("Prioritas Protokol Medis: ")
                                        }
                                        append("Jika terdapat perbedaan antara rekomendasi dalam aplikasi ini dengan instruksi langsung dari dokter spesialis anak atau tim neonatologi rumah sakit yang merawat bayi Anda, ")
                                        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                                            append("instruksi dokter spesialis Anda yang wajib diprioritaskan sepenuhnya")
                                        }
                                        append(".")
                                    },
                                    style = KanguruTheme.typography.bodySmall,
                                    color = colors.textPrimary
                                )
                            }
                        }
                    }

                    // ==========================================
                    // 3. TUJUAN & HAKIKAT APLIKASI
                    // ==========================================
                    TermsSection(number = "3.", title = "Tujuan & Hakikat Aplikasi") {
                        Text(
                            text = "Kanguru Siaga dirancang untuk memberdayakan keluarga dengan bayi prematur dan Bayi Berat Lahir Rendah (BBLR) pasca-rawat inap NICU melalui fungsionalitas utama:",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            TermsIconBulletItem(
                                icon = Icons.Default.CheckCircle,
                                iconTint = colors.primary,
                                text = AnnotatedString("Mendukung dan memotivasi penerapan Perawatan Metode Kanguru (PMK / Kangaroo Mother Care) berdurasi 8 hingga 24 jam sehari di rumah.")
                            )
                            TermsIconBulletItem(
                                icon = Icons.Default.CheckCircle,
                                iconTint = colors.primary,
                                text = AnnotatedString("Memfasilitasi pencatatan log harian kontak kulit-ke-kulit (skin-to-skin contact) antara bayi dan orang tua/pengasuh.")
                            )
                            TermsIconBulletItem(
                                icon = Icons.Default.CheckCircle,
                                iconTint = colors.primary,
                                text = AnnotatedString("Membantu visualisasi tren pertumbuhan antropometri menggunakan referensi standar kurva Fenton Preterm Growth Chart (2013/2025) dan Buku KIA Khusus Bayi Kecil (Kemenkes RI, 2021).")
                            )
                            TermsIconBulletItem(
                                icon = Icons.Default.CheckCircle,
                                iconTint = colors.primary,
                                text = AnnotatedString("Meningkatkan literasi manajemen nutrisi enteral (ASI perah, OGT/NGT) serta pencegahan dini risiko hipotermia dan infeksi nosokomial.")
                            )
                        }
                    }

                    // ==========================================
                    // 4. KELAYAKAN PENGGUNA
                    // ==========================================
                    TermsSection(number = "4.", title = "Kelayakan Pengguna") {
                        Text(
                            text = "Aplikasi ini ditujukan secara khusus untuk kategori pengguna berikut:",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            TermsLetteredItem(
                                letter = "a.",
                                text = "Orang tua kandung, anggota keluarga, atau pengasuh sah (caregiver) dari bayi prematur atau BBLR."
                            )
                            TermsLetteredItem(
                                letter = "b.",
                                text = "Tenaga kesehatan pendamping (dokter spesialis anak, bidan, perawat perinatologi/NICU) yang memonitor pasien pasca-pulang."
                            )
                            TermsLetteredItem(
                                letter = "c.",
                                text = "Individu yang telah berusia minimal 18 tahun atau telah cakap menurut hukum yang berlaku di Republik Indonesia untuk menyetujui perjanjian yang mengikat."
                            )
                        }
                    }

                    // ==========================================
                    // 5. FITUR UTAMA & PANDUAN PENGGUNAAN YANG SAH
                    // ==========================================
                    TermsSection(
                        number = "5.",
                        title = "Fitur Utama & Panduan Penggunaan yang Sah"
                    ) {
                        Text(
                            text = "Pengguna berhak memanfaatkan fitur-fitur yang disediakan sesuai peruntukan alaminya:",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            TermsFeatureCard(
                                title = "Pencatatan Profil Biologis Bayi",
                                description = "Input akurat data identitas, tanggal lahir, usia gestasi kelahiran (minggu), berat, panjang badan, dan lingkar kepala."
                            )
                            TermsFeatureCard(
                                title = "Timer & Log Sesi PMK",
                                description = "Alat bantu pewaktu sesi dekapan kangguru dengan rekapitulasi durasi kumulatif harian."
                            )
                            TermsFeatureCard(
                                title = "Grafik Pertumbuhan Fenton Berbasis PMA",
                                description = "Visualisasi tren kenaikan berat, panjang, dan lingkar kepala berdasarkan Postmenstrual Age (22-50 minggu)."
                            )
                            TermsFeatureCard(
                                title = "Jadwal & Alarm Nutrisi ASI",
                                description = "Pemberitahuan interval jadwal minum (tiap 2-3 jam) untuk mencegah hipoglikemia neonatus."
                            )
                            TermsFeatureCard(
                                title = "Katalog Video & Modul Edukasi",
                                description = "Materi audiovisual teknik posisi PMK, cara memerah dan menyimpan ASI, serta stimulasi tumbuh kembang."
                            )
                            TermsFeatureCard(
                                title = "Pengingat Skrining Prematuritas",
                                description = "Pencatatan tanggal janji temu penting skrining mata (ROP), fungsi pendengaran (OAE/BERA), dan USG kepala."
                            )
                        }
                    }

                    // ==========================================
                    // 6. KEWAJIBAN & TANGGUNG JAWAB PENGGUNA
                    // ==========================================
                    TermsSection(
                        number = "6.",
                        title = "Kewajiban & Tanggung Jawab Pengguna atas Data"
                    ) {
                        Text(
                            text = "Pengguna bertanggung jawab penuh atas:",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            TermsIconBulletItem(
                                icon = Icons.Default.Check,
                                iconTint = colors.legalTealText,
                                text = boldPrefixText(
                                    "Keakuratan Input Data: ",
                                    "Kebenaran tanggal lahir, usia kehamilan lahir, dan angka timbangan yang dimasukkan secara mandiri oleh orang tua. Kesalahan input data berdampak langsung pada ketepatan kalkulasi kurva dan usia koreksi."
                                )
                            )
                            TermsIconBulletItem(
                                icon = Icons.Default.Check,
                                iconTint = colors.legalTealText,
                                text = boldPrefixText(
                                    "Keamanan Akses Perangkat: ",
                                    "Menjaga keamanan fisik telepon genggam, kode kunci layar, PIN, atau autentikasi biometrik agar data riwayat medis anak tidak diakses oleh pihak yang tidak berhak."
                                )
                            )
                        }
                    }

                    // ==========================================
                    // 7. HAK KEKAYAAN INTELEKTUAL
                    // ==========================================
                    TermsSection(number = "7.", title = "Hak Kekayaan Intelektual") {
                        Text(
                            text = "Seluruh kode sumber, desain UI/UX, tata letak antarmuka, maskot visual, grafik ilustrasi, video demonstrasi, modul teks, dan merek dagang “Kanguru Siaga” adalah milik eksklusif Tim Pengembang Kanguru Siaga atau pemegang lisensi resminya.",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Referensi ilmiah kurva Fenton Growth Chart dilindungi hak cipta Dr. Tanis Fenton / University of Calgary, dan instrumen Buku KIA Bayi Kecil dilindungi oleh Kementerian Kesehatan Republik Indonesia. Keduanya diintegrasikan murni untuk tujuan ilmiah, edukatif klinis, non-komersial, serta peningkatan angka keselamatan bayi di Indonesia.",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )
                    }

                    // ==========================================
                    // 8. LARANGAN PENGGUNAAN
                    // ==========================================
                    TermsSection(
                        number = "8.",
                        title = "Larangan Penggunaan (Prohibited Conduct)"
                    ) {
                        Text(
                            text = "Dalam menggunakan Aplikasi ini, Anda dilarang keras untuk:",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            TermsIconBulletItem(
                                icon = Icons.Default.Close,
                                iconTint = colors.error,
                                text = AnnotatedString("Memasukkan data biologis palsu atau identitas yang bukan merupakan anak dalam pengasuhan sah Anda.")
                            )
                            TermsIconBulletItem(
                                icon = Icons.Default.Close,
                                iconTint = colors.error,
                                text = AnnotatedString("Melakukan rekayasa balik (reverse engineering), dekompilasi, atau mencoba membongkar kode sumber Aplikasi.")
                            )
                            TermsIconBulletItem(
                                icon = Icons.Default.Close,
                                iconTint = colors.error,
                                text = AnnotatedString("Melakukan serangan siber, upaya penetrasi tidak sah ke server/database Cloud Firestore, atau mengganggu kestabilan infrastruktur sistem.")
                            )
                            TermsIconBulletItem(
                                icon = Icons.Default.Close,
                                iconTint = colors.error,
                                text = AnnotatedString("Menggunakan script otomatis, spider, bot, atau scraper untuk mengambil database materi edukasi atau data aplikasi tanpa izin tertulis.")
                            )
                        }
                    }

                    // ==========================================
                    // 9. BATASAN TANGGUNG JAWAB
                    // ==========================================
                    TermsSection(
                        number = "9.",
                        title = "Batasan Tanggung Jawab (Limitation of Liability)"
                    ) {
                        Text(
                            text = "LAYANAN DAN KONTEN DISEDIAKAN BERDASARKAN PRINSIP “SEBAGAIMANA ADANYA” (AS IS) DAN “SEBAGAIMANA TERSEDIA” (AS AVAILABLE) TANPA JAMINAN APAPUN, BAIK TERSURAT MAUPUN TERSIRAT.",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Sepanjang diizinkan oleh hukum yang berlaku, Tim Pengembang Kanguru Siaga tidak bertanggung jawab atas:",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            TermsLetteredItem(
                                letter = "a.",
                                text = "Keterlambatan atau kelalaian mencari pertolongan medis darurat karena asumsi keliru bahwa aplikasi berfungsi sebagai monitor keselamatan tanda-tanda vital otomatis."
                            )
                            TermsLetteredItem(
                                letter = "b.",
                                text = "Interpretasi mandiri yang salah terhadap grafik pertumbuhan atau skor evaluasi mandiri."
                            )
                            TermsLetteredItem(
                                letter = "c.",
                                text = "Kegagalan alarm atau notifikasi pengingat akibat manajemen daya baterai perangkat (battery optimization), pembaruan sistem operasi ponsel, atau perangkat mati."
                            )
                            TermsLetteredItem(
                                letter = "d.",
                                text = "Kerusakan perangkat keras fisik, kehilangan memori lokal, atau kegagalan koneksi internet pada perangkat Pengguna."
                            )
                        }
                    }

                    // ==========================================
                    // 10. PEMBARUAN & MODIFIKASI LAYANAN
                    // ==========================================
                    TermsSection(number = "10.", title = "Pembaruan & Modifikasi Layanan") {
                        Text(
                            text = "Kami berhak sewaktu-waktu memperbarui, menambah, mengubah, atau menghentikan fitur tertentu dalam Aplikasi guna menyelaraskan dengan pembaruan pedoman klinis WHO/Kemenkes RI terbaru atau perbaikan teknis. Jika terdapat perubahan substansial pada Ketentuan Penggunaan ini, pemberitahuan akan ditampilkan melalui pembaruan aplikasi dalam format tanggal revisi terbaru.",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )
                    }

                    // ==========================================
                    // 11. HUKUM YANG MENGATUR & YURISDIKSI
                    // ==========================================
                    TermsSection(number = "11.", title = "Hukum yang Mengatur & Yurisdiksi") {
                        Text(
                            text = "Ketentuan Penggunaan ini diatur oleh dan ditafsirkan sesuai dengan hukum yang berlaku di Negara Kesatuan Republik Indonesia. Setiap perselisihan yang timbul dari atau sehubungan dengan penggunaan Aplikasi ini akan diupayakan untuk diselesaikan terlebih dahulu melalui musyawarah untuk mufakat dalam waktu 30 (tiga puluh) hari kalender sebelum diajukan ke ranah pengadilan yurisdiksi Republik Indonesia.",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )
                    }

                    // ==========================================
                    // PENUTUP DOKUMEN & INFORMASI KONTAK
                    // ==========================================
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = colors.legalSectionCardBg
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ContactSupport,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Pertanyaan & Kontak Bantuan",
                                    style = KanguruTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                            }

                            Text(
                                text = "Jika Anda memiliki pertanyaan, klarifikasi, atau masukan mengenai Ketentuan Penggunaan ini, silakan hubungi tim kami:",
                                style = KanguruTheme.typography.bodySmall,
                                color = colors.textPrimary
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "Tim Pengembang Kanguru Siaga",
                                    style = KanguruTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = "Inisiatif Pendampingan Perawatan Metode Kanguru Indonesia",
                                    style = KanguruTheme.typography.bodySmall,
                                    color = colors.textSecondary
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            try {
                                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                                    data = Uri.parse("mailto:kulitech.id@gmail.com")
                                                }
                                                context.startActivity(intent)
                                            } catch (_: Exception) {
                                            }
                                        }
                                        .padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mail,
                                        contentDescription = null,
                                        tint = colors.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "kulitech.id@gmail.com",
                                        style = KanguruTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.primary
                                    )
                                }
                            }
                        }
                    }

                    // ==========================================
                    // FOOTER
                    // ==========================================
                    Text(
                        text = "Kanguru Siaga • Menjaga Hangat, Mengawal Tumbuh",
                        style = KanguruTheme.typography.bodySmall,
                        color = colors.textTertiary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TermsSection(
    number: String,
    title: String,
    content: @Composable () -> Unit
) {
    val colors = KanguruTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = number,
                style = KanguruTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.primary
            )
            Text(
                text = title,
                style = KanguruTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
        }
        content()
    }
}

@Composable
private fun TermsIconBulletItem(
    icon: ImageVector,
    iconTint: Color,
    text: AnnotatedString
) {
    val colors = KanguruTheme.colors
    Row(
        modifier = Modifier.padding(start = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(18.dp)
        )
        Text(
            text = text,
            style = KanguruTheme.typography.bodyMedium,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TermsLetteredItem(
    letter: String,
    text: String
) {
    val colors = KanguruTheme.colors
    Row(
        modifier = Modifier.padding(start = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = letter,
            style = KanguruTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = colors.textSecondary,
            modifier = Modifier.width(20.dp)
        )
        Text(
            text = text,
            style = KanguruTheme.typography.bodyMedium,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TermsFeatureCard(
    title: String,
    description: String
) {
    val colors = KanguruTheme.colors
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = colors.legalSectionCardBg
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = KanguruTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            Text(
                text = description,
                style = KanguruTheme.typography.bodySmall,
                color = colors.textSecondary
            )
        }
    }
}

private fun boldPrefixText(prefix: String, body: String): AnnotatedString {
    return buildAnnotatedString {
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append(prefix)
        }
        append(body)
    }
}

