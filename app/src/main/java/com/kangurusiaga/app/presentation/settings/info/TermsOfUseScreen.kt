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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ContactSupport
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CodeOff
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.NoDrinks
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
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
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ==========================================
            // DOCUMENT HEADER CARD
            // ==========================================
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = colors.surface,
                border = BorderStroke(1.dp, colors.cardBorder),
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = colors.legalTealSurfaceBg
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = colors.legalTealAccent,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "BAGIAN I • DOKUMEN RESMI",
                                style = KanguruTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.legalTealAccent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Ketentuan Penggunaan Aplikasi Kanguru Siaga",
                        style = KanguruTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Terms of Use Agreement",
                        style = KanguruTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = colors.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Metadata Grid
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = colors.background
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            TermsMetaRow(
                                icon = Icons.Default.CalendarToday,
                                label = "Tanggal Berlaku",
                                trailing = {
                                    Text(
                                        text = "11 November 2026",
                                        style = KanguruTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.textPrimary
                                    )
                                }
                            )

                            TermsMetaRow(
                                icon = Icons.Default.Sell,
                                label = "Versi",
                                trailing = {
                                    Surface(
                                        shape = CircleShape,
                                        color = colors.legalRoseTintBg
                                    ) {
                                        Text(
                                            text = "v1.1",
                                            style = KanguruTheme.typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = colors.primary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            )

                            TermsMetaRow(
                                icon = Icons.Default.Code,
                                label = "Pengembang",
                                trailing = {
                                    Text(
                                        text = "KuliTech",
                                        style = KanguruTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.textPrimary
                                    )
                                }
                            )

                            Row(
                                modifier = Modifier.padding(top = 4.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Policy,
                                    contentDescription = null,
                                    tint = colors.legalTealAccent,
                                    modifier = Modifier
                                        .padding(top = 2.dp)
                                        .size(18.dp)
                                )
                                Text(
                                    text = "Kepatuhan: Sesuai UU No. 27/2022 (UU PDP), Standar Etika Medis Perinatologi, Kebijakan Google Play Health Apps & Exact Alarm.",
                                    style = KanguruTheme.typography.bodySmall,
                                    color = colors.textSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quote / Intro Notice Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = colors.legalSectionCardBg
                    ) {
                        Text(
                            text = "“Selamat datang di Kanguru Siaga. Dengan mengunduh, memasang (install), mengakses, atau menggunakan aplikasi mobile Kanguru Siaga (“Aplikasi”), Anda menyatakan bahwa Anda telah membaca, memahami, menyetujui, dan mengikatkan diri secara hukum pada seluruh syarat dan ketentuan yang tercantum dalam Ketentuan Penggunaan ini. Jika Anda tidak menyetujui sebagian atau seluruh ketentuan ini, mohon untuk tidak menggunakan Aplikasi ini.”",
                            style = KanguruTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = colors.textSecondary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            // ==========================================
            // PASAL 1: PENERIMAAN KETENTUAN
            // ==========================================
            TermsArticleCard(number = "1.", title = "Penerimaan Ketentuan") {
                Text(
                    text = "Ketentuan Penggunaan ini merupakan perjanjian hukum yang sah dan mengikat antara Anda sebagai pengguna (“Pengguna” atau “Anda”) dan KuliTech selaku tim pengembang resmi Kanguru Siaga (“KuliTech” atau “Kami”). Perjanjian ini mengatur hak, kewajiban, batasan lisensi, serta alokasi tanggung jawab para pihak dalam memanfaatkan seluruh fitur, materi edukasi, kalkulator klinis pendukung, dan ekosistem digital Kanguru Siaga.",
                    style = KanguruTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }

            // ==========================================
            // PASAL 2: PENYANGKALAN MEDIS PENTING
            // ==========================================
            TermsArticleCard(
                number = "2.",
                title = "Penyangkalan Medis Penting (Medical Disclaimer)"
            ) {
                // Critical Alert Card
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
                                style = KanguruTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.primary
                            )
                        }

                        Text(
                            text = "APLIKASI KANGURU SIAGA BUKAN PERANGKAT MEDIS (MEDICAL DEVICE) DAN BUKAN PENGGANTI DOKTER, DOKTER SPESIALIS ANAK, ATAU TENAGA MEDIS PROFESIONAL.",
                            style = KanguruTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.legalRoseHeadingText
                        )

                        Text(
                            text = "Seluruh konten, penghitungan usia koreksi, timer kontak, kurva pertumbuhan, pengingat minum ASI, dan panduan edukasi disediakan semata-mata untuk tujuan informasi umum, pemantauan mandiri di rumah (home monitoring), serta sarana pendukung edukatif bagi orang tua.",
                            style = KanguruTheme.typography.bodySmall,
                            color = colors.legalRoseHeadingText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Kondisi Kegawatan Darurat Bayi (Tanda Bahaya)",
                        style = KanguruTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                }

                Text(
                    text = "Jangan pernah menunda tindakan medis darurat berdasarkan data di Aplikasi. Segera bawa bayi ke Unit Gawat Darurat (UGD) atau fasilitas kesehatan terdekat jika Anda mengamati salah satu dari tanda bahaya berikut:",
                    style = KanguruTheme.typography.bodySmall,
                    color = colors.textSecondary
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TermsDangerFlagCard(
                        icon = Icons.Default.CrisisAlert,
                        title = "Apnea atau Henti Napas",
                        description = "Bayi berhenti bernapas lebih dari 20 detik atau napas megap-megap/merintih (grunting)."
                    )
                    TermsDangerFlagCard(
                        icon = Icons.Default.Palette,
                        title = "Sianosis (Kebiruan)",
                        description = "Bibir, lidah, gusi, kuku, atau seluruh tubuh bayi membiru atau tampak sangat pucat kelabu."
                    )
                    TermsDangerFlagCard(
                        icon = Icons.Default.DeviceThermostat,
                        title = "Instabilitas Suhu Ekstrem",
                        description = "Suhu ketiak di bawah 36,0°C (hipotermia berat/sedang) atau demam di atas 37,5°C yang tidak merespons penghangatan."
                    )
                    TermsDangerFlagCard(
                        icon = Icons.Default.Bedtime,
                        title = "Letargis & Penurunan Kesadaran",
                        description = "Bayi tampak sangat lunglai, tidak aktif bergerak saat dirangsang, atau mengalami kejang/gerakan abnormal."
                    )
                    TermsDangerFlagCard(
                        icon = Icons.Default.NoDrinks,
                        title = "Intoleransi Minum Berat",
                        description = "Malas menyusu total, muntah kehijauan berulang, atau perut membuncit tegang."
                    )
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = colors.legalTealSurfaceBg
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                                append("Jika terdapat perbedaan antara rekomendasi dalam aplikasi ini dengan instruksi langsung dokter spesialis anak atau tim neonatologi rumah sakit, ")
                                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                                    append("instruksi dokter spesialis Anda yang wajib diprioritaskan sepenuhnya")
                                }
                                append(".")
                            },
                            style = KanguruTheme.typography.bodySmall,
                            color = colors.legalTealText
                        )
                    }
                }
            }

            // ==========================================
            // PASAL 3: TUJUAN & HAKIKAT APLIKASI
            // ==========================================
            TermsArticleCard(number = "3.", title = "Tujuan & Hakikat Aplikasi") {
                Text(
                    text = "Kanguru Siaga dirancang untuk memberdayakan keluarga dengan bayi prematur dan Berat Badan Lahir Rendah (BBLR) pasca-rawat inap NICU melalui fungsionalitas utama:",
                    style = KanguruTheme.typography.bodySmall,
                    color = colors.textSecondary
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TermsCheckRow(
                        icon = Icons.Default.CheckCircle,
                        iconTint = colors.primary,
                        text = AnnotatedString("Mendukung dan memotivasi penerapan Perawatan Metode Kanguru (PMK / Kangaroo Mother Care) berdurasi 8 hingga 24 jam sehari di rumah.")
                    )
                    TermsCheckRow(
                        icon = Icons.Default.CheckCircle,
                        iconTint = colors.primary,
                        text = AnnotatedString("Memfasilitasi pencatatan log harian kontak kulit-ke-kulit (skin-to-skin contact) antara bayi dan orang tua/pengasuh.")
                    )
                    TermsCheckRow(
                        icon = Icons.Default.CheckCircle,
                        iconTint = colors.primary,
                        text = AnnotatedString("Membantu visualisasi tren pertumbuhan antropometri menggunakan referensi standar kurva Fenton Preterm Growth Chart (2013/2025) & Buku KIA Khusus Bayi Kecil (Kemenkes RI, 2021).")
                    )
                    TermsCheckRow(
                        icon = Icons.Default.CheckCircle,
                        iconTint = colors.primary,
                        text = AnnotatedString("Meningkatkan literasi manajemen nutrisi enteral (ASI perah, OGT/NGT) serta pencegahan dini risiko hipotermia dan infeksi nosokomial.")
                    )
                }
            }

            // ==========================================
            // PASAL 4: KELAYAKAN PENGGUNA
            // ==========================================
            TermsArticleCard(number = "4.", title = "Kelayakan Pengguna") {
                Text(
                    text = "Aplikasi ini ditujukan secara khusus untuk kategori pengguna berikut:",
                    style = KanguruTheme.typography.bodySmall,
                    color = colors.textSecondary
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TermsLetteredCard(
                        letter = "a.",
                        text = "Orang tua kandung, anggota keluarga, atau pengasuh sah (caregiver) dari bayi prematur atau BBLR."
                    )
                    TermsLetteredCard(
                        letter = "b.",
                        text = "Tenaga kesehatan pendamping (dokter spesialis anak, bidan, perawat perinatologi/NICU) yang memonitor pasien pasca-pulang."
                    )
                    TermsLetteredCard(
                        letter = "c.",
                        text = "Individu yang telah berusia minimal 18 tahun atau telah cakap menurut hukum yang berlaku di Republik Indonesia untuk menyetujui perjanjian yang mengikat."
                    )
                }
            }

            // ==========================================
            // PASAL 5: FITUR UTAMA & PANDUAN PENGGUNAAN YANG SAH
            // ==========================================
            TermsArticleCard(
                number = "5.",
                title = "Fitur Utama & Panduan Penggunaan yang Sah"
            ) {
                Text(
                    text = "Pengguna berhak memanfaatkan fitur-fitur yang disediakan sesuai peruntukan alaminya:",
                    style = KanguruTheme.typography.bodySmall,
                    color = colors.textSecondary
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TermsFeatureHighlightCard(
                        icon = Icons.Default.ChildCare,
                        iconTint = colors.primary,
                        title = "Pencatatan Profil Biologis Bayi",
                        description = "Input akurat data identitas, tanggal lahir, usia gestasi kelahiran (minggu), berat, panjang badan, dan lingkar kepala."
                    )
                    TermsFeatureHighlightCard(
                        icon = Icons.Default.Timer,
                        iconTint = colors.legalTealAccent,
                        title = "Timer & Log Sesi PMK",
                        description = "Alat bantu pewaktu sesi dekapan kangguru dengan rekapitulasi durasi kumulatif harian dan grafik kepatuhan."
                    )
                    TermsFeatureHighlightCard(
                        icon = Icons.AutoMirrored.Filled.ShowChart,
                        iconTint = colors.primary,
                        title = "Grafik Pertumbuhan Fenton Berbasis PMA",
                        description = "Visualisasi tren kenaikan berat, panjang, dan lingkar kepala berdasarkan Postmenstrual Age (22-50 minggu)."
                    )
                    TermsFeatureHighlightCard(
                        icon = Icons.Default.Alarm,
                        iconTint = colors.legalTealAccent,
                        title = "Jadwal & Alarm Nutrisi ASI",
                        description = "Pemberitahuan interval jadwal minum (tiap 2-3 jam) untuk mencegah risiko hipoglikemia neonatus via Google Play Exact Alarm policy."
                    )
                    TermsFeatureHighlightCard(
                        icon = Icons.Default.SmartDisplay,
                        iconTint = colors.primary,
                        title = "Katalog Video & Modul Edukasi",
                        description = "Materi audiovisual teknik posisi PMK, cara memerah dan menyimpan ASI, serta stimulasi tumbuh kembang bayi prematur."
                    )
                    TermsFeatureHighlightCard(
                        icon = Icons.Default.Checklist,
                        iconTint = colors.legalTealAccent,
                        title = "Penapisan & Lembar Hasil Skrining Prematuritas",
                        description = "Pencatatan tanggal janji temu penting skrining mata (ROP), fungsi pendengaran (OAE/BERA), USG kepala, serta skrining kustom tambahan."
                    )
                }
            }

            // ==========================================
            // PASAL 6: KEWAJIBAN & TANGGUNG JAWAB PENGGUNA ATAS DATA
            // ==========================================
            TermsArticleCard(
                number = "6.",
                title = "Kewajiban & Tanggung Jawab Pengguna atas Data"
            ) {
                Text(
                    text = "Pengguna bertanggung jawab penuh atas:",
                    style = KanguruTheme.typography.bodySmall,
                    color = colors.textSecondary
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TermsCheckRow(
                        icon = Icons.Default.Check,
                        iconTint = colors.legalTealAccent,
                        text = termsBoldPrefix(
                            "Keakuratan Input Data: ",
                            "Kebenaran tanggal lahir, usia kehamilan lahir, dan angka timbangan yang dimasukkan secara mandiri oleh orang tua. Kesalahan input data berdampak langsung pada ketepatan kalkulasi kurva dan usia koreksi.",
                            prefixColor = colors.textPrimary
                        )
                    )
                    TermsCheckRow(
                        icon = Icons.Default.Check,
                        iconTint = colors.legalTealAccent,
                        text = termsBoldPrefix(
                            "Keamanan Akses Perangkat: ",
                            "Menjaga keamanan fisik telepon genggam, kode kunci layar, PIN, atau autentikasi biometrik agar data riwayat medis anak tidak diakses oleh pihak yang tidak berhak.",
                            prefixColor = colors.textPrimary
                        )
                    )
                }
            }

            // ==========================================
            // PASAL 7: HAK KEKAYAAN INTELEKTUAL
            // ==========================================
            TermsArticleCard(number = "7.", title = "Hak Kekayaan Intelektual") {
                Text(
                    text = buildAnnotatedString {
                        append("Seluruh kode sumber, desain UI/UX, tata letak antarmuka, maskot visual, grafik ilustrasi, video demonstrasi, modul teks, dan merek dagang “Kanguru Siaga” adalah milik eksklusif ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = colors.textPrimary)) {
                            append("KuliTech")
                        }
                        append(" atau pemegang lisensi resminya.")
                    },
                    style = KanguruTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
                Text(
                    text = "Referensi ilmiah kurva Fenton Growth Chart dilindungi hak cipta Dr. Tanis Fenton / University of Calgary, dan instrumen Buku KIA Bayi Kecil dilindungi oleh Kementerian Kesehatan Republik Indonesia. Keduanya diintegrasikan murni untuk tujuan ilmiah, edukatif klinis, non-komersial, serta peningkatan angka keselamatan bayi di Indonesia.",
                    style = KanguruTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }

            // ==========================================
            // PASAL 8: LARANGAN PENGGUNAAN
            // ==========================================
            TermsArticleCard(
                number = "8.",
                title = "Larangan Penggunaan (Prohibited Conduct)"
            ) {
                Text(
                    text = "Dalam menggunakan Aplikasi ini, Anda dilarang keras untuk:",
                    style = KanguruTheme.typography.bodySmall,
                    color = colors.textSecondary
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TermsProhibitedCard(
                        icon = Icons.Default.Block,
                        text = "Memasukkan data biologis palsu atau identitas yang bukan merupakan anak dalam pengasuhan sah Anda."
                    )
                    TermsProhibitedCard(
                        icon = Icons.Default.CodeOff,
                        text = "Melakukan rekayasa balik (reverse engineering), dekompilasi, atau mencoba membongkar kode sumber Aplikasi."
                    )
                    TermsProhibitedCard(
                        icon = Icons.Default.QrCode2,
                        text = "Melakukan serangan siber, upaya penetrasi tidak sah ke server/database Cloud Firestore, atau mengganggu kestabilan infrastruktur sistem."
                    )
                    TermsProhibitedCard(
                        icon = Icons.Default.SmartToy,
                        text = "Menggunakan script otomatis, spider, bot, atau scraper untuk mengambil database materi edukasi atau data aplikasi tanpa izin tertulis."
                    )
                }
            }

            // ==========================================
            // PASAL 9: BATASAN TANGGUNG JAWAB
            // ==========================================
            TermsArticleCard(
                number = "9.",
                title = "Batasan Tanggung Jawab (Limitation of Liability)"
            ) {
                Text(
                    text = "LAYANAN DAN KONTEN DISEDIAKAN BERDASARKAN PRINSIP “SEBAGAIMANA ADANYA” (AS IS) DAN “SEBAGAIMANA TERSEDIA” (AS AVAILABLE) TANPA JAMINAN APAPUN, BAIK TERSURAT MAUPUN TERSIRAT.",
                    style = KanguruTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Text(
                    text = "Sepanjang diizinkan oleh hukum yang berlaku, KuliTech tidak bertanggung jawab atas:",
                    style = KanguruTheme.typography.bodySmall,
                    color = colors.textSecondary
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TermsLetteredCard(
                        letter = "a.",
                        text = "Keterlambatan atau kelalaian mencari pertolongan medis darurat karena asumsi keliru bahwa aplikasi berfungsi sebagai monitor keselamatan tanda-tanda vital otomatis.",
                        letterColor = colors.textSecondary
                    )
                    TermsLetteredCard(
                        letter = "b.",
                        text = "Interpretasi mandiri yang salah terhadap grafik pertumbuhan atau skor evaluasi mandiri.",
                        letterColor = colors.textSecondary
                    )
                    TermsLetteredCard(
                        letter = "c.",
                        text = "Kegagalan alarm atau notifikasi pengingat akibat manajemen daya baterai perangkat (battery optimization), pembaruan sistem operasi ponsel, atau perangkat mati.",
                        letterColor = colors.textSecondary
                    )
                    TermsLetteredCard(
                        letter = "d.",
                        text = "Kerusakan perangkat keras fisik, kehilangan memori lokal, atau kegagalan koneksi internet pada perangkat Pengguna.",
                        letterColor = colors.textSecondary
                    )
                }
            }

            // ==========================================
            // PASAL 10: PEMBARUAN & MODIFIKASI LAYANAN
            // ==========================================
            TermsArticleCard(number = "10.", title = "Pembaruan & Modifikasi Layanan") {
                Text(
                    text = "Kami berhak sewaktu-waktu memperbarui, menambah, mengubah, atau menghentikan fitur tertentu dalam Aplikasi guna menyelaraskan dengan pembaruan pedoman klinis WHO/Kemenkes RI terbaru atau perbaikan teknis. Jika terdapat perubahan substansial pada Ketentuan Penggunaan ini, pemberitahuan akan ditampilkan melalui pembaruan aplikasi dalam format tanggal revisi terbaru.",
                    style = KanguruTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }

            // ==========================================
            // PASAL 11: HUKUM YANG MENGATUR & YURISDIKSI
            // ==========================================
            TermsArticleCard(number = "11.", title = "Hukum yang Mengatur & Yurisdiksi") {
                Text(
                    text = "Ketentuan Penggunaan ini diatur oleh dan ditafsirkan sesuai dengan hukum yang berlaku di Negara Kesatuan Republik Indonesia. Setiap perselisihan yang timbul dari atau sehubungan dengan penggunaan Aplikasi ini akan diupayakan untuk diselesaikan terlebih dahulu melalui musyawarah untuk mufakat dalam waktu 30 (tiga puluh) hari kalender sebelum diajukan ke ranah pengadilan yurisdiksi Republik Indonesia.",
                    style = KanguruTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }

            // ==========================================
            // SUPPORT AND HELP DESK CARD
            // ==========================================
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = colors.legalSectionCardBg,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(colors.legalRoseTintBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ContactSupport,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Pertanyaan & Kontak Bantuan",
                                style = KanguruTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "Layanan Pengembang Resmi",
                                style = KanguruTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }
                    }

                    Text(
                        text = "Jika Anda memiliki pertanyaan, klarifikasi, atau masukan mengenai Ketentuan Penggunaan ini, silakan hubungi tim kami:",
                        style = KanguruTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = colors.surface
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            TermsSupportInfoRow(
                                label = "Pengembang",
                                value = "KuliTech",
                                isBoldValue = true
                            )
                            HorizontalDivider(color = colors.divider.copy(alpha = 0.6f))
                            TermsSupportInfoRow(
                                label = "Kategori",
                                value = "Pendamping Pengasuhan & Tumbuh Kembang Bayi",
                                isBoldValue = false
                            )
                            HorizontalDivider(color = colors.divider.copy(alpha = 0.6f))
                            TermsSupportInfoRow(
                                label = "Wilayah Hukum",
                                value = "Republik Indonesia",
                                isBoldValue = false
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        try {
                                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                                data = Uri.parse("mailto:support@kulitech.my.id")
                                            }
                                            context.startActivity(intent)
                                        } catch (_: Exception) {
                                        }
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mail,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "support@kulitech.my.id",
                                    style = KanguruTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.primary
                                )
                            }
                        }
                    }
                }
            }

            // ==========================================
            // FRIENDLY FOOTER TAGLINE
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Kanguru Siaga",
                        style = KanguruTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary
                    )
                }
                Text(
                    text = "Menjaga Hangat, Mengawal Tumbuh",
                    style = KanguruTheme.typography.bodySmall,
                    color = colors.textTertiary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun TermsMetaRow(
    icon: ImageVector,
    label: String,
    trailing: @Composable () -> Unit
) {
    val colors = KanguruTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.textSecondary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                style = KanguruTheme.typography.bodySmall,
                color = colors.textSecondary
            )
        }
        trailing()
    }
}

@Composable
private fun TermsArticleCard(
    number: String,
    title: String,
    content: @Composable () -> Unit
) {
    val colors = KanguruTheme.colors
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = colors.surface,
        border = BorderStroke(1.dp, colors.cardBorder),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = number,
                    style = KanguruTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
                Text(
                    text = title,
                    style = KanguruTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
            content()
        }
    }
}

@Composable
private fun TermsDangerFlagCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    val colors = KanguruTheme.colors
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = colors.background
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(18.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = KanguruTheme.typography.labelMedium,
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
}

@Composable
private fun TermsCheckRow(
    icon: ImageVector,
    iconTint: Color,
    text: AnnotatedString
) {
    val colors = KanguruTheme.colors
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
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
            style = KanguruTheme.typography.bodySmall,
            color = colors.textSecondary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TermsLetteredCard(
    letter: String,
    text: String,
    letterColor: Color = KanguruTheme.colors.primary
) {
    val colors = KanguruTheme.colors
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = colors.background
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = letter,
                style = KanguruTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = letterColor
            )
            Text(
                text = text,
                style = KanguruTheme.typography.bodySmall,
                color = colors.textSecondary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TermsFeatureHighlightCard(
    icon: ImageVector,
    iconTint: Color,
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
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = title,
                    style = KanguruTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
            }
            Text(
                text = description,
                style = KanguruTheme.typography.bodySmall,
                color = colors.textSecondary
            )
        }
    }
}

@Composable
private fun TermsProhibitedCard(
    icon: ImageVector,
    text: String
) {
    val colors = KanguruTheme.colors
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = colors.background
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(18.dp)
            )
            Text(
                text = text,
                style = KanguruTheme.typography.bodySmall,
                color = colors.textSecondary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TermsSupportInfoRow(
    label: String,
    value: String,
    isBoldValue: Boolean
) {
    val colors = KanguruTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = KanguruTheme.typography.bodySmall,
            color = colors.textSecondary,
            modifier = Modifier.padding(end = 16.dp)
        )
        Text(
            text = value,
            style = KanguruTheme.typography.labelMedium,
            fontWeight = if (isBoldValue) FontWeight.SemiBold else FontWeight.Medium,
            color = colors.textPrimary,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f, fill = false)
        )
    }
}

private fun termsBoldPrefix(
    prefix: String,
    body: String,
    prefixColor: Color
): AnnotatedString {
    return buildAnnotatedString {
        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = prefixColor)) {
            append(prefix)
        }
        append(body)
    }
}
