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
import androidx.compose.material.icons.automirrored.filled.Rule
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Vaccines
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme

@Composable
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = {
            Surface(
                color = colors.background,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
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
                        modifier = Modifier.weight(1f)
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Privacy Policy",
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
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Document Container Paper Sheet
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = colors.surface,
                border = BorderStroke(1.dp, colors.cardBorder),
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(22.dp)
                ) {
                    // ==========================================
                    // HEADER DOKUMEN
                    // ==========================================
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(
                            shape = CircleShape,
                            color = colors.legalRoseTintBg
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "BAGIAN II: KEBIJAKAN PRIVASI",
                                    style = KanguruTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.primary
                                )
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Kebijakan Privasi Aplikasi Kanguru Siaga",
                                style = KanguruTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "Privacy Policy & Pelindungan Data Pribadi",
                                style = KanguruTheme.typography.titleSmall,
                                color = colors.textSecondary
                            )
                        }

                        // Metadata Bar
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = colors.background
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarToday,
                                            contentDescription = null,
                                            tint = colors.primary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Text(
                                            text = "Tanggal Berlaku: 9 November 2026",
                                            style = KanguruTheme.typography.bodySmall,
                                            color = colors.textSecondary
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = colors.surface,
                                        shadowElevation = 0.5.dp
                                    ) {
                                        Text(
                                            text = "Versi 1.0",
                                            style = KanguruTheme.typography.labelSmall,
                                            color = colors.textSecondary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(
                                            SpanStyle(
                                                fontWeight = FontWeight.SemiBold,
                                                color = colors.textPrimary
                                            )
                                        ) {
                                            append("Landasan Hukum: ")
                                        }
                                        append("Sesuai Undang-Undang Republik Indonesia No. 27 Tahun 2022 tentang Pelindungan Data Pribadi (UU PDP).")
                                    },
                                    style = KanguruTheme.typography.bodySmall,
                                    color = colors.textSecondary
                                )
                            }
                        }
                    }

                    // ==========================================
                    // PENGANTAR KOMITMEN
                    // ==========================================
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = colors.legalTealSurfaceBg
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = colors.legalTealAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Komitmen Pengembang untuk Anda dan Buah Hati",
                                    style = KanguruTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.legalTealText
                                )
                            }
                            Text(
                                text = "Kami di Kanguru Siaga menghargai dan menjunjung tinggi privasi Anda serta si kecil. Kebijakan Privasi ini menjelaskan bagaimana kami mengumpulkan, menyimpan, melindungi, dan mengolah informasi data pribadi Anda saat menggunakan Aplikasi Kanguru Siaga, sesuai dengan Undang-Undang RI No. 27 Tahun 2022 tentang Pelindungan Data Pribadi (UU PDP).",
                                style = KanguruTheme.typography.bodyMedium,
                                color = colors.legalTealText
                            )
                        }
                    }

                    // ==========================================
                    // PASAL 1: KOMITMEN PRIVASI UTAMA
                    // ==========================================
                    PrivacySection(number = "1", title = "Komitmen Privasi Utama") {
                        Text(
                            text = "Integritas dan keselamatan digital bayi premature maupun BBLR (Bayi Berat Lahir Rendah) adalah prioritas mutlak kami. Prinsip dasar kami meliputi:",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            PrivacyIconCard(
                                icon = Icons.Default.Block,
                                iconTint = colors.primary,
                                text = buildAnnotatedString {
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("Tidak Ada Penjualan Data: ")
                                    }
                                    append("Kami ")
                                    withStyle(
                                        SpanStyle(
                                            fontWeight = FontWeight.Bold,
                                            color = colors.primary
                                        )
                                    ) {
                                        append("TIDAK PERNAH")
                                    }
                                    append(" menjual, memperdagangkan, menyewakan, atau memberikan data pribadi atau data catatan kesehatan bayi Anda kepada pihak ketiga, broker data, atau jaringan periklanan komersial mana pun.")
                                }
                            )
                            PrivacyIconCard(
                                icon = Icons.Default.LockClock,
                                iconTint = colors.legalTealAccent,
                                text = privacyBoldPrefix(
                                    "Privasi Sejak Desain (Privacy by Design): ",
                                    "Sebagian besar data pemantauan dan operasional esensial tersimpan secara lokal langsung di memori ponsel pintar Anda sendiri."
                                )
                            )
                            PrivacyIconCard(
                                icon = Icons.Default.NoAccounts,
                                iconTint = colors.legalTealAccent,
                                text = privacyBoldPrefix(
                                    "Akses Bebas Akun: ",
                                    "Aplikasi dapat dioperasikan penuh tanpa kewajiban registrasi akun sosial media, alamat email, ataupun verifikasi nomor ponsel pribadi."
                                )
                            )
                        }
                    }

                    // ==========================================
                    // PASAL 2: INFORMASI & DATA YANG DIKUMPULKAN
                    // ==========================================
                    PrivacySection(number = "2", title = "Informasi & Data yang Kami Kumpulkan") {
                        Text(
                            text = "Untuk memberikan kalkulasi pertumbuhan neonatus yang presisi dan panduan Perawatan Metode Kanguru (PMK), kami memproses rincian parameter berikut:",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            PrivacyDataCard(
                                icon = Icons.Default.ChildCare,
                                title = "Identitas Kelahiran Bayi",
                                description = "Nama panggilan anak, tanggal & jam kelahiran, usia gestasi saat lahir (minggu & hari), berat badan lahir (gram), panjang badan lahir (cm), lingkar kepala lahir (cm), dan jenis kelamin biologis."
                            )
                            PrivacyDataCard(
                                icon = Icons.Default.Straighten,
                                title = "Data Antropometri Berkala",
                                description = "Catatan berkala berat badan rutin (gram), panjang badan (cm), lingkar kepala (cm), tanggal dan waktu penimbangan, serta catatan observasi perkembangan klinis orang tua."
                            )
                            PrivacyDataCard(
                                icon = Icons.Default.HourglassBottom,
                                title = "Data Aktivitas PMK (Kangaroo Mother Care)",
                                description = "Waktu mulai dan selesai kontak kulit-ke-kulit (skin-to-skin), durasi per sesi pemantauan, akumulasi total durasi harian, serta mode PMK (kontinu atau intermiten)."
                            )
                            PrivacyDataCard(
                                icon = Icons.Default.Vaccines,
                                title = "Data Nutrisi & Pemantauan Medis Khusus",
                                description = "Jadwal pemberian asupan (ASI / Susu Formula Khusus / Sonde OGT / NGT), target volume cairan per 24 jam, serta daftar centang skrining esensial BBLR (ROP, BERA, USG Kepala)."
                            )

                            // Special Media Card (Rose Tint)
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                color = colors.legalRoseTintBg
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AddAPhoto,
                                            contentDescription = null,
                                            tint = colors.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "Media (Foto Bayi) - Penegasan Khusus",
                                            style = KanguruTheme.typography.labelLarge,
                                            fontWeight = FontWeight.SemiBold,
                                            color = colors.primary
                                        )
                                    }
                                    Text(
                                        text = buildAnnotatedString {
                                            append("Foto profil anak yang dipilih dari galeri atau kamera Anda ")
                                            withStyle(
                                                SpanStyle(
                                                    fontWeight = FontWeight.Bold,
                                                    color = colors.primary
                                                )
                                            ) {
                                                append("disimpan 100% secara lokal")
                                            }
                                            append(" pada penyimpanan sandboxed ponsel Anda. Berkas foto ")
                                            withStyle(
                                                SpanStyle(
                                                    fontWeight = FontWeight.Bold,
                                                    textDecoration = TextDecoration.Underline
                                                )
                                            ) {
                                                append("TIDAK PERNAH diunggah, ditransmisikan, atau disimpan")
                                            }
                                            append(" pada server cloud kami.")
                                        },
                                        style = KanguruTheme.typography.bodySmall,
                                        color = colors.textPrimary
                                    )
                                }
                            }

                            PrivacyDataCard(
                                icon = Icons.Default.Devices,
                                title = "Data Teknis & Diagnostik Anonim",
                                description = "Tipe model ponsel, versi sistem operasi Android, dan log crash teknis terenkripsi melalui Firebase Crashlytics untuk stabilitas tanpa identifier pribadi."
                            )
                        }
                    }

                    // ==========================================
                    // PASAL 3: ARSITEKTUR PENYIMPANAN
                    // ==========================================
                    PrivacySection(number = "3", title = "Arsitektur Penyimpanan: Lokal & Cloud") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            PrivacyStorageCard(
                                icon = Icons.Default.Smartphone,
                                title = "Penyimpanan Lokal (Offline-First Architecture)",
                                description = "Menggunakan basis data SQLite/Room terisolasi berstandar Sandbox Android. Riwayat sesi PMK harian, grafik pertumbuhan, jadwal pengingat, dan foto tersimpan eksklusif di ruang memori internal aplikasi. Seluruh fungsi inti beroperasi normal tanpa koneksi internet maupun saat mode pesawat."
                            )
                            PrivacyStorageCard(
                                icon = Icons.Default.CloudSync,
                                title = "Sinkronisasi Cloud Terbatas (Google Cloud Firestore)",
                                description = "Saat ponsel online, data identifikasi awal dan parameter pertumbuhan dapat disinkronkan secara aman melalui Cloud Firestore Security Rules berspesifikasi ketat (validasi klinis batas wajar, izin create-only terproteksi, serta akses baca terbuka/publik ditutup seutuhnya)."
                            )
                        }
                    }

                    // ==========================================
                    // PASAL 4: TUJUAN PENGOLAHAN & PENGGUNAAN DATA
                    // ==========================================
                    PrivacySection(number = "4", title = "Tujuan Pengolahan & Penggunaan Data") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            PrivacyCheckBullet(
                                prefix = "Fungsionalitas Klinis: ",
                                body = "Mengotomatisasi kalkulasi usia koreksi (Corrected Age/PMA), memetakan persentil kurva pertumbuhan Fenton 2025, dan mengkalkulasi pencapaian target harian PMK."
                            )
                            PrivacyCheckBullet(
                                prefix = "Alarm & Manajemen Waktu: ",
                                body = "Menghasilkan pengingat berkala untuk jadwal pemberian nutrisi per 2-3 jam serta batas pergantian posisi skin-to-skin."
                            )
                            PrivacyCheckBullet(
                                prefix = "Kustomisasi Pribadi: ",
                                body = "Mengatur kenyamanan visual (mode redup malam di ruang NICU) dan preferensi pengingat suara hening."
                            )
                            PrivacyCheckBullet(
                                prefix = "Riset Ilmiah Anonim: ",
                                body = "Pengembangan studi kesehatan pencegahan komplikasi stunting dan hipotermia pada BBLR secara teragregasi tanpa data identitas perorangan."
                            )
                        }
                    }

                    // ==========================================
                    // PASAL 5: BEBAS IKLAN & PIHAK KETIGA
                    // ==========================================
                    PrivacySection(
                        number = "5",
                        title = "Bebas Iklan & Keterlibatan Pihak Ketiga"
                    ) {
                        Text(
                            text = "Aplikasi Kanguru Siaga dirancang murni sebagai sarana pendampingan medis suportif:",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            PrivacyIconCard(
                                icon = Icons.Default.Verified,
                                iconTint = colors.legalTealAccent,
                                text = privacyBoldPrefix(
                                    "Bebas Iklan Komersial: ",
                                    "Tidak ada library pelacak pihak ketiga (third-party tracking SDKs) untuk profil penargetan iklan."
                                )
                            )
                            PrivacyIconCard(
                                icon = Icons.Default.CloudDone,
                                iconTint = colors.legalTealAccent,
                                text = privacyBoldPrefix(
                                    "Sub-prosesor Resmi Berstandar Global: ",
                                    "Pemrosesan infrastruktur didukung oleh Google Cloud Platform (enkripsi ISO 27001 / SOC 2) dan fungsionalitas algoritma FentonGrowth.ca untuk validasi standar persentil."
                                )
                            )
                        }
                    }

                    // ==========================================
                    // PASAL 6: IZIN AKSES PERANGKAT
                    // ==========================================
                    PrivacySection(
                        number = "6",
                        title = "Izin Akses Perangkat (Device Permissions)"
                    ) {
                        Text(
                            text = "Setiap permintaan izin sistem operasi Android digunakan secara eksklusif untuk tujuan fungsional:",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            PrivacyPermissionCard(
                                permission = "POST_NOTIFICATIONS (Android 13+)",
                                reason = "Menampilkan banner waktu nyata saat sesi timer PMK sedang berjalan di latar belakang dan notifikasi pengingat minum susu."
                            )
                            PrivacyPermissionCard(
                                permission = "SCHEDULE_EXACT_ALARM",
                                reason = "Memicu alarm audio/getar pada waktu presisi demi kepatuhan pemberian nutrisi terjadwal bayi kecil."
                            )
                            PrivacyPermissionCard(
                                permission = "CAMERA",
                                reason = "Mengambil foto avatar si kecil langsung dari perangkat atas inisiatif pengguna. Foto tidak pernah diunggah."
                            )
                            PrivacyPermissionCard(
                                permission = "READ_MEDIA_IMAGES / READ_EXTERNAL_STORAGE",
                                reason = "Memilih foto bayi dari album galeri untuk keperluan personalisasi kartu profil di perangkat."
                            )
                            PrivacyPermissionCard(
                                permission = "WRITE_EXTERNAL_STORAGE (Android 9 ke bawah)",
                                reason = "Menyimpan berkas ringkasan ekspor grafik laporan (format PDF/gambar) ke direktori unduhan pengguna."
                            )
                            PrivacyPermissionCard(
                                permission = "INTERNET & ACCESS_NETWORK_STATE",
                                reason = "Mengecek konektivitas jaringan untuk pengiriman laporan eror anonim serta sinkronisasi awan opsional."
                            )
                        }
                    }

                    // ==========================================
                    // PASAL 7: KEAMANAN & ENKRIPSI
                    // ==========================================
                    PrivacySection(
                        number = "7",
                        title = "Langkah-Langkah Keamanan & Enkripsi"
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            PrivacySecurityCard(
                                icon = Icons.Default.Lock,
                                title = "Enkripsi Data dalam Transit",
                                description = "Seluruh komunikasi data keluar menggunakan protokol terenkripsi kuat TLS 1.3 / HTTPS dengan perlindungan man-in-the-middle."
                            )
                            PrivacySecurityCard(
                                icon = Icons.Default.Shield,
                                title = "Android Application Sandbox",
                                description = "Data lokal disimpan dalam kontainer memori internal privat yang tidak dapat diakses langsung oleh aplikasi lain pada perangkat."
                            )
                            PrivacySecurityCard(
                                icon = Icons.AutoMirrored.Filled.Rule,
                                title = "Aturan Database Granular (CEL)",
                                description = "Penyaringan ketat Common Expression Language pada tingkat server untuk menolak input data di luar standar klinis bayi."
                            )
                        }
                    }

                    // ==========================================
                    // PASAL 8: HAK-HAK PENGGUNA (UU PDP)
                    // ==========================================
                    PrivacySection(
                        number = "8",
                        title = "Hak-Hak Pengguna (Berdasarkan UU PDP)"
                    ) {
                        Text(
                            text = "Sebagai subjek data pribadi, Anda memiliki hak hukum penuh sesuai ketentuan UU RI No. 27 Tahun 2022:",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            PrivacyRightCard(
                                title = "Hak Akses & Pembaruan",
                                description = "Anda dapat melihat dan menyunting riwayat berat badan, panjang badan, dan profil identitas kapan pun melalui menu \"Edit Profil Bayi\"."
                            )
                            PrivacyRightCard(
                                title = "Hak Penghapusan (Right to Erasure)",
                                description = "Anda dapat menghapus seluruh data catatan dengan tombol \"Bersihkan Riwayat\", melakukan reset melalui pengaturan Android, atau menghapus pemasangan aplikasi (uninstall)."
                            )
                            PrivacyRightCard(
                                title = "Hak Menarik Persetujuan",
                                description = "Anda berhak mencabut izin notifikasi dan kamera kapan saja melalui menu Pengaturan Aplikasi di perangkat Anda."
                            )
                        }
                    }

                    // ==========================================
                    // PASAL 9 & 10
                    // ==========================================
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        PrivacySection(
                            number = "9",
                            title = "Privasi Anak & Bayi (Children's Privacy)"
                        ) {
                            Text(
                                text = "Aplikasi Kanguru Siaga ditujukan untuk digunakan oleh orang tua, wali sah, atau tenaga medis profesional dewasa. Aplikasi tidak dirancang untuk dioperasikan secara mandiri oleh anak-anak. Seluruh pencatatan data anak dilakukan atas kendali dan otorisasi penuh orang tua atau wali yang berwenang.",
                                style = KanguruTheme.typography.bodyMedium,
                                color = colors.textPrimary
                            )
                        }

                        PrivacySection(
                            number = "10",
                            title = "Retensi & Penghapusan Data"
                        ) {
                            Text(
                                text = "Data lokal tetap tersimpan di perangkat selama aplikasi terpasang atau hingga pengguna membersihkan data aplikasi secara manual. Data analitik anonim agregat disimpan maksimal selama masa evaluasi berkala untuk perbaikan algoritma, setelah itu dihapus secara permanen.",
                                style = KanguruTheme.typography.bodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    }

                    // ==========================================
                    // PASAL 11: KONTAK & LAYANAN PENGADUAN
                    // ==========================================
                    PrivacySection(number = "11", title = "Kontak & Layanan Pengaduan") {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = colors.background
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Tim Pengembang Kanguru Siaga",
                                    style = KanguruTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textPrimary
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable {
                                                try {
                                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                                        data = Uri.parse("mailto:kulitech.id@gmail.com")
                                                    }
                                                    context.startActivity(intent)
                                                } catch (_: Exception) {
                                                }
                                            },
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Mail,
                                            contentDescription = null,
                                            tint = colors.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "kulitech.id@gmail.com",
                                            style = KanguruTheme.typography.bodySmall,
                                            color = colors.textSecondary
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Gavel,
                                            contentDescription = null,
                                            tint = colors.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "Wilayah Hukum: Republik Indonesia",
                                            style = KanguruTheme.typography.bodySmall,
                                            color = colors.textSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ==========================================
                    // PENUTUP & KONFIRMASI
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
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = colors.legalTealAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Pernyataan Persetujuan Pengguna",
                                    style = KanguruTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textPrimary
                                )
                            }
                            Text(
                                text = "Dengan memasang, membuka, atau menggunakan Aplikasi Kanguru Siaga, Anda menyatakan bahwa Anda telah membaca, memahami, dan menyetujui seluruh ketentuan dalam Kebijakan Privasi ini demi keselamatan dan pemantauan optimal buah hati Anda.",
                                style = KanguruTheme.typography.bodyMedium,
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PrivacySection(
    number: String,
    title: String,
    content: @Composable () -> Unit
) {
    val colors = KanguruTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(colors.legalRoseTintBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number,
                    style = KanguruTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }
            Text(
                text = title,
                style = KanguruTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
        }
        content()
    }
}

@Composable
private fun PrivacyIconCard(
    icon: ImageVector,
    iconTint: Color,
    text: AnnotatedString
) {
    val colors = KanguruTheme.colors
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = colors.legalSectionCardBg
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
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
}

@Composable
private fun PrivacyDataCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    val colors = KanguruTheme.colors
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = colors.legalSectionCardBg
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colors.primary,
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
private fun PrivacyStorageCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    val colors = KanguruTheme.colors
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = colors.legalSectionCardBg
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colors.legalTealAccent,
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
                style = KanguruTheme.typography.bodyMedium,
                color = colors.textSecondary
            )
        }
    }
}

@Composable
private fun PrivacyCheckBullet(
    prefix: String,
    body: String
) {
    val colors = KanguruTheme.colors
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(18.dp)
        )
        Text(
            text = privacyBoldPrefix(prefix, body),
            style = KanguruTheme.typography.bodyMedium,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PrivacyPermissionCard(
    permission: String,
    reason: String
) {
    val colors = KanguruTheme.colors
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = colors.legalSectionCardBg
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = permission,
                style = KanguruTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = colors.primary
            )
            Text(
                text = reason,
                style = KanguruTheme.typography.bodySmall,
                color = colors.textSecondary
            )
        }
    }
}

@Composable
private fun PrivacySecurityCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    val colors = KanguruTheme.colors
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = colors.legalSectionCardBg
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
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
                    style = KanguruTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Text(
                    text = description,
                    style = KanguruTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun PrivacyRightCard(
    title: String,
    description: String
) {
    val colors = KanguruTheme.colors
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = colors.legalSectionCardBg
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = KanguruTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            Text(
                text = description,
                style = KanguruTheme.typography.bodyMedium,
                color = colors.textSecondary
            )
        }
    }
}

private fun privacyBoldPrefix(prefix: String, body: String): AnnotatedString {
    return buildAnnotatedString {
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append(prefix)
        }
        append(body)
    }
}

