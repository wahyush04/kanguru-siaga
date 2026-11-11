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
                                        text = "11 November 2026",
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
                                            text = "Versi 1.1",
                                            style = KanguruTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = colors.primary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = boldPrefixText(
                                        "Pengembang: ",
                                        "KuliTech (Tim Pengembang Kanguru Siaga)"
                                    ),
                                    style = KanguruTheme.typography.bodySmall,
                                    color = colors.textPrimary
                                )

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
                                        text = "Kepatuhan: Sesuai UU No. 27 Tahun 2022 (UU PDP), Standar Etika Medis Perinatologi, serta Kebijakan Data Safety, Health Apps, dan Alarm Google Play Console.",
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
                            text = "Ketentuan Penggunaan ini merupakan perjanjian hukum yang mengikat antara Anda selaku pengguna (“Pengguna”, “Anda”) dan KuliTech selaku tim pengembang resmi Aplikasi Kanguru Siaga (“Pengembang”, “Kami”). Penggunaan Aplikasi juga tunduk pada Kebijakan Privasi yang merupakan satu kesatuan yang tidak terpisahkan dari dokumen ini.",
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
                                    text = "Seluruh konten, kalkulasi persentil/Z-score, penghitungan usia koreksi, timer kontak, kurva pertumbuhan, pengingat minum ASI, dan panduan edukasi disediakan semata-mata untuk tujuan informasi umum, pemantauan mandiri di rumah (home monitoring), serta alat bantu edukatif — dan tidak boleh digunakan untuk mendiagnosis penyakit, menentukan resep obat, atau mengubah rencana terapi medis tanpa persetujuan dokter.",
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
                            text = "Kanguru Siaga dikembangkan untuk memberdayakan keluarga dengan bayi prematur dan Bayi Berat Lahir Rendah (BBLR) melalui fungsionalitas utama:",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            TermsIconBulletItem(
                                icon = Icons.Default.CheckCircle,
                                iconTint = colors.primary,
                                text = AnnotatedString("Mendukung orang tua dan keluarga dalam menerapkan Perawatan Metode Kanguru (PMK / Kangaroo Mother Care) secara konsisten dan terarah di rumah.")
                            )
                            TermsIconBulletItem(
                                icon = Icons.Default.CheckCircle,
                                iconTint = colors.primary,
                                text = AnnotatedString("Memfasilitasi pencatatan durasi kontak kulit-ke-kulit (skin-to-skin contact) harian guna mencapai target medis (minimal 8 jam/hari hingga 24 jam kontinu).")
                            )
                            TermsIconBulletItem(
                                icon = Icons.Default.CheckCircle,
                                iconTint = colors.primary,
                                text = AnnotatedString("Membantu memvisualisasikan tren catch-up growth bayi prematur menggunakan kurva Fenton Preterm Growth Chart (2013/2025) dan rujukan Buku KIA Khusus Bayi Kecil (Kemenkes RI, 2021).")
                            )
                            TermsIconBulletItem(
                                icon = Icons.Default.CheckCircle,
                                iconTint = colors.primary,
                                text = AnnotatedString("Meningkatkan literasi keluarga mengenai teknik pemberian nutrisi (ASI/OGT/NGT), higiene, skrining lanjutan bayi prematur, serta pencegahan komplikasi hipotermia.")
                            )
                        }
                    }

                    // ==========================================
                    // 4. KELAYAKAN PENGGUNA
                    // ==========================================
                    TermsSection(number = "4.", title = "Kelayakan Pengguna") {
                        Text(
                            text = "Aplikasi ini ditujukan bagi kategori pengguna berikut:",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            TermsLetteredItem(
                                letter = "a.",
                                text = "Orang tua kandung, wali sah, keluarga, atau pengasuh yang merawat bayi prematur atau Bayi Berat Lahir Rendah (BBLR)."
                            )
                            TermsLetteredItem(
                                letter = "b.",
                                text = "Tenaga kesehatan (dokter spesialis anak, dokter umum, bidan, perawat perinatologi/NICU) yang mendampingi edukasi keluarga pasca-rawat inap."
                            )
                            TermsLetteredItem(
                                letter = "c.",
                                text = "Pengguna wajib berusia minimal 18 tahun atau telah cakap hukum menurut peraturan perundang-undangan di Republik Indonesia."
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
                            text = "Pengguna berhak menggunakan fitur-fitur berikut sesuai dengan peruntukannya:",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            TermsFeatureCard(
                                title = "Pencatatan Identitas & Profil Bayi",
                                description = "Pengisian data biologis kelahiran bayi (nama, tanggal lahir, usia gestasi, berat, panjang, lingkar kepala, dan foto opsional) untuk personalisasi kurva dan usia kronologis/koreksi."
                            )
                            TermsFeatureCard(
                                title = "Timer & Pencatatan Sesi PMK",
                                description = "Penggunaan pengatur waktu (timer) kontinu/intermiten dan input sesi manual untuk menghitung akumulasi jam dekapan harian beserta rekapitulasi mingguan."
                            )
                            TermsFeatureCard(
                                title = "Grafik Pertumbuhan Fenton",
                                description = "Plotting data antropometri ke dalam grafik standar internasional berbasis Usia Pascamenstruasi (PMA 22–50 minggu), baik melalui data referensi lokal maupun sinkronisasi grafik/Z-score resmi FentonGrowth.ca."
                            )
                            TermsFeatureCard(
                                title = "Jadwal & Alarm Pemberian ASI",
                                description = "Pengingat audio-getar interval nutrisi rutin setiap 2–3 jam (termasuk bagi bayi dengan selang OGT/NGT) untuk membantu mencegah hipoglikemia neonatus."
                            )
                            TermsFeatureCard(
                                title = "Katalog Video & Modul Edukasi",
                                description = "Pembelajaran teknik memandikan, posisi dekapan kanguru yang aman, tanda kegawatan, menyusui, dan pengukuran antropometri."
                            )
                            TermsFeatureCard(
                                title = "Penapisan & Lembar Hasil Skrining Prematur",
                                description = "Pencatatan jadwal pengingat alarm untuk 4 skrining wajib IDAI/Kemenkes (USG Kepala, Retinopati Prematuritas/ROP, Pendengaran OAE/BERA, dan Fungsi Tiroid) maupun skrining spesialis tambahan kustom, lengkap dengan catatan temuan medis dan lampiran berkas di penyimpanan lokal perangkat."
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
                                    "Kebenaran, keakuratan, dan validitas data yang dimasukkan ke dalam Aplikasi (seperti tanggal lahir, berat badan, panjang badan, lingkar kepala, dan usia kehamilan). Kesalahan input dapat menyebabkan kurva pertumbuhan dan kalkulasi usia menjadi tidak akurat."
                                )
                            )
                            TermsIconBulletItem(
                                icon = Icons.Default.Check,
                                iconTint = colors.legalTealText,
                                text = boldPrefixText(
                                    "Keamanan Perangkat Fisik: ",
                                    "Karena data riwayat operasional, foto profil bayi, dan lampiran hasil medis tersimpan secara lokal di memori ponsel pintar Anda, Anda bertanggung jawab menjaga keamanan fisik perangkat dan akses kunci layar (PIN/Kata Sandi/Biometrik) agar tidak diakses pihak yang tidak berhak."
                                )
                            )
                        }
                    }

                    // ==========================================
                    // 7. HAK KEKAYAAN INTELEKTUAL
                    // ==========================================
                    TermsSection(number = "7.", title = "Hak Kekayaan Intelektual") {
                        Text(
                            text = "Seluruh kode sumber, arsitektur perangkat lunak, antarmuka visual (UI), pengalaman pengguna (UX), merek “Kanguru Siaga”, maskot ilustrasi, teks materi edukasi, serta video panduan adalah kekayaan intelektual milik KuliTech (Tim Pengembang Kanguru Siaga) atau pemegang lisensi resminya dan dilindungi oleh Undang-Undang Hak Cipta Republik Indonesia.",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Kurva dan metodologi pertumbuhan mengacu pada Fenton Preterm Growth Chart (Hak Cipta © Dr. Tanis Fenton / University of Calgary) dan Buku KIA Khusus Bayi Kecil (Hak Cipta © Kementerian Kesehatan Republik Indonesia). Penggunaan standar ini dalam Aplikasi semata-mata bersifat akademis, edukatif klinis, dan non-komersial untuk keselamatan bayi.",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Pengguna dilarang menyalin, memodifikasi, merekayasa balik (reverse engineering), mendekompilasi, mendistribusikan ulang, atau mengeksploitasi materi dalam Aplikasi untuk tujuan komersial tanpa izin tertulis dari Kami.",
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
                            text = "Dalam menggunakan Aplikasi ini, Pengguna dilarang:",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            TermsIconBulletItem(
                                icon = Icons.Default.Close,
                                iconTint = colors.error,
                                text = AnnotatedString("Memasukkan data palsu, identitas yang bukan merupakan anak dalam pengasuhan sah Anda, atau muatan berbahaya secara sengaja.")
                            )
                            TermsIconBulletItem(
                                icon = Icons.Default.Close,
                                iconTint = colors.error,
                                text = AnnotatedString("Mengganggu, meretas, melumpuhkan, atau mencoba membongkar serta menembus integritas server, database Cloud Firestore, API eksternal, atau infrastruktur Aplikasi.")
                            )
                            TermsIconBulletItem(
                                icon = Icons.Default.Close,
                                iconTint = colors.error,
                                text = AnnotatedString("Menggunakan alat otomatis (bots, scrapers, crawlers) untuk mengambil data atau konten dari Aplikasi tanpa izin tertulis.")
                            )
                            TermsIconBulletItem(
                                icon = Icons.Default.Close,
                                iconTint = colors.error,
                                text = AnnotatedString("Menggunakan Aplikasi untuk tindakan melanggar hukum di wilayah Republik Indonesia.")
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
                            text = "Aplikasi disediakan atas dasar “SEBAGAIMANA ADANYA” (AS IS) dan “SEBAGAIMANA TERSEDIA” (AS AVAILABLE) tanpa jaminan tersurat maupun tersirat apa pun.",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Sejauh diizinkan oleh hukum yang berlaku, KuliTech tidak bertanggung jawab atas kerugian fisik, cedera, komplikasi kesehatan, kematian, atau kerugian material/immaterial yang timbul akibat:",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            TermsLetteredItem(
                                letter = "a.",
                                text = "Keterlambatan mencari bantuan medis profesional atau anggapan keliru bahwa aplikasi merupakan alat pemantau tanda-tanda vital otomatis."
                            )
                            TermsLetteredItem(
                                letter = "b.",
                                text = "Kesalahan penafsiran mandiri terhadap grafik pertumbuhan, persentil, Z-score, atau materi edukasi."
                            )
                            TermsLetteredItem(
                                letter = "c.",
                                text = "Kegagalan fungsi atau tidak berbunyinya alarm pengingat akibat perangkat mati, pencabutan izin sistem oleh pengguna, atau pembatasan latar belakang ekstrem oleh produsen perangkat."
                            )
                            TermsLetteredItem(
                                letter = "d.",
                                text = "Kehilangan data lokal yang disebabkan oleh kerusakan perangkat, factory reset, atau penghapusan aplikasi (uninstall) oleh Pengguna."
                            )
                        }
                    }

                    // ==========================================
                    // 10. PEMBARUAN & MODIFIKASI LAYANAN
                    // ==========================================
                    TermsSection(number = "10.", title = "Pembaruan & Modifikasi Layanan") {
                        Text(
                            text = "Kami berhak untuk sewaktu-waktu memperbarui, menambah fitur, memperbaiki celah (bug), atau mengubah Ketentuan Penggunaan ini demi penyelarasan pedoman klinis terbaru dan peningkatan mutu teknis. Pembaruan ketentuan akan diinformasikan melalui pembaruan Aplikasi pada Google Play Store serta pembaruan dokumen di dalam menu Pengaturan > Legalitas & Privasi > Ketentuan Penggunaan.",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )
                    }

                    // ==========================================
                    // 11. HUKUM YANG MENGATUR & YURISDIKSI
                    // ==========================================
                    TermsSection(number = "11.", title = "Hukum yang Mengatur & Yurisdiksi") {
                        Text(
                            text = "Ketentuan Penggunaan ini diatur oleh dan ditafsirkan sesuai dengan hukum Negara Republik Indonesia. Segala perselisihan yang timbul akan diupayakan penyelesaiannya melalui musyawarah untuk mufakat dalam jangka waktu 30 (tiga puluh) hari kalender, dan apabila tidak tercapai kesepakatan, diselesaikan melalui yurisdiksi pengadilan negeri yang berwenang di Republik Indonesia.",
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
                                text = "Jika Anda memiliki pertanyaan, klarifikasi, atau masukan mengenai Ketentuan Penggunaan ini, silakan hubungi pengembang resmi kami:",
                                style = KanguruTheme.typography.bodySmall,
                                color = colors.textPrimary
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "KuliTech (Tim Pengembang Kanguru Siaga)",
                                    style = KanguruTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = "Kesehatan & Kebugaran Maternal & Neonatal • Republik Indonesia",
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
                                                    data = Uri.parse("mailto:support@kulitech.my.id")
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
                                        text = "support@kulitech.my.id",
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

