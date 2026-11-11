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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Update
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
                                            text = "Tanggal Berlaku: 11 November 2026",
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
                                            text = "Versi 1.1",
                                            style = KanguruTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = colors.primary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = privacyBoldPrefix(
                                        "Pengembang: ",
                                        "KuliTech (Tim Pengembang Kanguru Siaga)"
                                    ),
                                    style = KanguruTheme.typography.bodySmall,
                                    color = colors.textPrimary
                                )

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
                                        append("Sesuai Undang-Undang Republik Indonesia No. 27 Tahun 2022 tentang Pelindungan Data Pribadi (UU PDP) serta Kebijakan Data Safety, Health Apps, dan Alarm Google Play Console.")
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
                                text = "Kami di KuliTech selaku pengembang aplikasi Kanguru Siaga menghargai dan menjunjung tinggi privasi Anda serta buah hati Anda. Kebijakan Privasi ini menjelaskan secara transparan bagaimana kami mengumpulkan, memproses, menyimpan, mengirimkan kepada sub-prosesor tepercaya, melindungi, dan menghapus data pribadi Anda saat menggunakan Aplikasi Kanguru Siaga, sesuai UU PDP No. 27 Tahun 2022 dan standar transparansi Google Play Console.",
                                style = KanguruTheme.typography.bodyMedium,
                                color = colors.legalTealText
                            )
                        }
                    }

                    // ==========================================
                    // PASAL 1: KOMITMEN PRIVASI & DASAR HUKUM UU PDP
                    // ==========================================
                    PrivacySection(
                        number = "1",
                        title = "Komitmen Privasi & Dasar Hukum Pemrosesan Data Spesifik (UU PDP)"
                    ) {
                        Text(
                            text = "Integritas dan keselamatan digital bayi prematur maupun BBLR (Bayi Berat Lahir Rendah) adalah prioritas mutlak kami. Prinsip dasar pemrosesan data kami meliputi:",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            PrivacyIconCard(
                                icon = Icons.Default.VerifiedUser,
                                iconTint = colors.legalTealAccent,
                                text = privacyBoldPrefix(
                                    "Kategori Data Pribadi Spesifik (Pasal 4 ayat (2) UU PDP): ",
                                    "Informasi kelahiran, kondisi prematuritas/BBLR, parameter pertumbuhan antropometri, catatan skrining medis, dan data anak tergolong Data Pribadi yang Bersifat Spesifik."
                                )
                            )
                            PrivacyIconCard(
                                icon = Icons.Default.Verified,
                                iconTint = colors.legalTealAccent,
                                text = privacyBoldPrefix(
                                    "Dasar Hukum Persetujuan Eksplisit: ",
                                    "Seluruh pemrosesan data didasarkan pada persetujuan sah secara eksplisit (explicit consent) orang tua/wali yang berwenang — termasuk konfirmasi pada layar Konfirmasi Data Bayi saat onboarding (sebelum penyimpanan lokal maupun sinkronisasi Cloud Firestore) serta konfirmasi interaktif sebelum akses grafik online ke FentonGrowth.ca."
                                )
                            )
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
                                    append(" menjual, memperdagangkan, atau menyewakan data pribadi maupun data kesehatan bayi Anda kepada pihak ketiga, broker data, atau jaringan periklanan komersial mana pun.")
                                }
                            )
                            PrivacyIconCard(
                                icon = Icons.Default.NoAccounts,
                                iconTint = colors.legalTealAccent,
                                text = privacyBoldPrefix(
                                    "Desentralisasi Data (Offline-First & Bebas Akun): ",
                                    "Sebagian besar catatan harian, media foto, dan dokumen medis disimpan privat di memori internal perangkat dan dapat digunakan tanpa registrasi akun email maupun nomor telepon pribadi."
                                )
                            )
                        }
                    }

                    // ==========================================
                    // PASAL 2: INFORMASI & DATA YANG DIKUMPULKAN
                    // ==========================================
                    PrivacySection(number = "2", title = "Informasi & Data yang Kami Kumpulkan") {
                        Text(
                            text = "Berikut rincian kategori data yang diproses beserta tempat penyimpanan dan tujuannya:",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            PrivacyDataCard(
                                icon = Icons.Default.ChildCare,
                                title = "Identitas Kelahiran & Profil Onboarding Bayi",
                                description = "Nama panggilan/lengkap bayi, tanggal lahir (epoch & terformat), usia kehamilan lahir (minggu), berat lahir & berat saat onboarding (gram), panjang & lingkar kepala lahir/saat ini (cm), jenis kelamin, versi aplikasi, dan environment build. Disimpan lokal di Room SQLite serta disinkronkan satu kali saat onboarding (bila online) ke koleksi baby_onboarding_profiles di Cloud Firestore."
                            )
                            PrivacyDataCard(
                                icon = Icons.Default.Straighten,
                                title = "Data Antropometri & Pertumbuhan Berkala",
                                description = "Catatan berkala berat, panjang, dan lingkar kepala beserta tanggal pengukuran, kalkulasi persentil/Z-score, berkas CSV lokal (fenton_zscore_*.csv), dan cache gambar grafik. Disimpan lokal di perangkat; hanya dikirim ke FentonGrowth.ca API apabila pengguna mengonfirmasi fitur grafik/Z-score online."
                            )
                            PrivacyDataCard(
                                icon = Icons.Default.HourglassBottom,
                                title = "Data Aktivitas PMK, Nutrisi, & Pengingat",
                                description = "Waktu mulai/selesai kontak skin-to-skin, durasi sesi, target harian PMK, jadwal pengingat PMK, dan jadwal alarm minum ASI/OGT/NGT. Disimpan 100% lokal di perangkat (Room SQLite & DataStore) dan tidak dikirim ke Cloud Firestore."
                            )
                            PrivacyDataCard(
                                icon = Icons.Default.Vaccines,
                                title = "Data Skrining Medis & Lampiran Klinis",
                                description = "Jadwal 4 skrining wajib IDAI/Kemenkes & skrining tambahan kustom, catatan temuan/resume klinis dokter, serta berkas lampiran medis (foto cetak USG/retina atau file PDF resume lab maks. 10 MB per berkas di direktori internal screening_attachments/). Disimpan 100% lokal di perangkat dan TIDAK PERNAH diunggah ke Cloud Firestore atau server kami."
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
                                            text = "Media (Foto Profil Bayi) — 100% Lokal",
                                            style = KanguruTheme.typography.labelLarge,
                                            fontWeight = FontWeight.SemiBold,
                                            color = colors.primary
                                        )
                                    }
                                    Text(
                                        text = buildAnnotatedString {
                                            append("Foto profil bayi yang diambil via kamera sistem atau dipilih lewat Android Photo Picker ")
                                            withStyle(
                                                SpanStyle(
                                                    fontWeight = FontWeight.Bold,
                                                    color = colors.primary
                                                )
                                            ) {
                                                append("disimpan 100% secara lokal")
                                            }
                                            append(" di direktori internal sandbox baby_photos/ pada perangkat Anda. Berkas foto ")
                                            withStyle(
                                                SpanStyle(
                                                    fontWeight = FontWeight.Bold,
                                                    textDecoration = TextDecoration.Underline
                                                )
                                            ) {
                                                append("TIDAK PERNAH diunggah atau disimpan")
                                            }
                                            append(" pada server cloud kami.")
                                        },
                                        style = KanguruTheme.typography.bodySmall,
                                        color = colors.textPrimary
                                    )
                                }
                            }

                            PrivacyDataCard(
                                icon = Icons.Default.Analytics,
                                title = "Data Analitik Aplikasi (Firebase Analytics)",
                                description = "App Instance ID, event standar penggunaan layar/interaksi fitur, informasi perangkat (model, OS Android), perkiraan lokasi umum berbasis IP oleh Google, serta Advertising ID bawaan perangkat (kecuali dibatasi pengguna di setelan Android). Diproses oleh SDK Google Firebase Analytics untuk memantau kestabilan adopsi fitur secara agregat tanpa penayangan iklan."
                            )

                            PrivacyDataCard(
                                icon = Icons.Default.BugReport,
                                title = "Data Diagnostik & Log Error (Firebase Crashlytics)",
                                description = "Model perangkat, versi OS, versi aplikasi, stack trace crash/error non-fatal, ID dokumen Firestore hasil sinkronisasi, serta log breadcrumb lokal saat peristiwa sinkronisasi/error. Catatan transparansi: breadcrumb log status offline pada BabyFirestoreSyncManager dapat mencatat nama tampilan profil bayi apabila crash terjadi tepat setelah log tersebut."
                            )
                        }
                    }

                    // ==========================================
                    // PASAL 3: ARSITEKTUR PENYIMPANAN
                    // ==========================================
                    PrivacySection(
                        number = "3",
                        title = "Arsitektur Penyimpanan Data: Lokal & Cloud Firestore"
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            PrivacyStorageCard(
                                icon = Icons.Default.Smartphone,
                                title = "Penyimpanan Lokal Perangkat (Offline-First Architecture)",
                                description = "Menggunakan database Room (SQLite), DataStore Preferences, serta direktori penyimpanan internal privat Android Application Sandbox (filesDir dan cacheDir). Data yang 100% hanya tersimpan lokal meliputi: seluruh log sesi PMK & jadwal alarm PMK/nutrisi; seluruh pengukuran antropometri berkala, file CSV lokal (fenton_zscore_*.csv), & gambar cache grafik; seluruh jadwal & catatan temuan skrining plus berkas lampiran medis hingga 10 MB di folder screening_attachments/ (ScreeningAttachmentManager); foto profil bayi di folder baby_photos/ (PhotoStorageManager); serta preferensi tema/teks. Seluruh fitur harian tetap berjalan penuh secara offline."
                            )
                            PrivacyStorageCard(
                                icon = Icons.Default.CloudSync,
                                title = "Sinkronisasi Cloud Terbatas (Google Cloud Firestore)",
                                description = "Hanya saat onboarding setelah pengguna mengonfirmasi data di Konfirmasi Data Bayi dan perangkat online, Aplikasi mengirim satu kali secara non-blocking ke koleksi baby_onboarding_profiles (auto-generated Document ID). Daftar lengkap 15 field yang dikirim: babyName, birthDateEpochMillis, birthDateFormatted, gestationalAgeWeeks, birthWeightGram, currentWeightGram, birthLengthCm, currentLengthCm, birthHeadCircumferenceCm, currentHeadCircumferenceCm, gender, environment, appVersion, syncedAtEpochMillis, dan createdAtServer. Aturan keamanan firestore.rules bersifat create-only dari klien dengan validasi CEL, sedangkan read, update, dan delete ditutup penuh bagi publik/klien (hanya admin terautentikasi dengan klaim admin == true)."
                            )
                        }
                    }

                    // ==========================================
                    // PASAL 4: TUJUAN PENGOLAHAN & PENGGUNAAN DATA
                    // ==========================================
                    PrivacySection(number = "4", title = "Tujuan Pengolahan & Penggunaan Data") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            PrivacyCheckBullet(
                                prefix = "Fungsionalitas Klinis & Pemantauan Harian: ",
                                body = "Menghitung usia kronologis/koreksi/PMA, memetakan grafik pertumbuhan Kurva Fenton (2013/2025), menjadwalkan alarm pengingat ASI/OGT/NGT serta PMK, dan mengarsipkan lembar hasil skrining BBLR secara lokal."
                            )
                            PrivacyCheckBullet(
                                prefix = "Kustomisasi Pengalaman Pengguna: ",
                                body = "Menyimpan preferensi tema (Warm Light / Dark Nursing Mode) dan ukuran skala teks agar nyaman dibaca di ruang NICU maupun rumah."
                            )
                            PrivacyCheckBullet(
                                prefix = "Keandalan Sistem & Analitik Produk: ",
                                body = "Memantau rasio crash, diagnosa kegagalan sinkronisasi (Crashlytics), serta performa pemakaian fitur aplikasi (Firebase Analytics)."
                            )
                            PrivacyCheckBullet(
                                prefix = "Evaluasi Program & Riset Kesehatan Anonim: ",
                                body = "Data onboarding dan pertumbuhan dapat diagregasikan serta dianonimkan (menghapus nama bayi dan identitas langsung) untuk evaluasi klinis program perawatan BBLR dan publikasi ilmiah pencegahan komplikasi prematuritas."
                            )
                        }
                    }

                    // ==========================================
                    // PASAL 5: BEBAS PENJUALAN DATA, ANALITIK, & PIHAK KETIGA
                    // ==========================================
                    PrivacySection(
                        number = "5",
                        title = "Kebijakan Bebas Penjualan Data, Layanan Analitik, & Pihak Ketiga"
                    ) {
                        Text(
                            text = "Aplikasi Kanguru Siaga tidak menayangkan iklan pihak ketiga (ad networks) dan hanya bekerja dengan sub-prosesor tepercaya berikut:",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            PrivacyIconCard(
                                icon = Icons.Default.Block,
                                iconTint = colors.primary,
                                text = privacyBoldPrefix(
                                    "Tidak Ada Penayangan Iklan Komersial: ",
                                    "Aplikasi tidak menampilkan iklan pihak ketiga dan tidak pernah menjual data pengguna maupun data anak kepada pengiklan atau broker data."
                                )
                            )
                            PrivacyDataCard(
                                icon = Icons.Default.CloudDone,
                                title = "Google Firebase — Cloud Firestore",
                                description = "Penyimpanan profil onboarding bayi saat pendaftaran awal online pada infrastruktur Google Cloud berstandar keamanan global (ISO 27001 / SOC 2)."
                            )
                            PrivacyDataCard(
                                icon = Icons.Default.Analytics,
                                title = "Google Firebase — Firebase Analytics",
                                description = "Mengumpulkan App Instance ID, event penggunaan, info perangkat/OS, perkiraan lokasi kasar berbasis IP, serta Advertising ID Android. Kanguru Siaga tidak menggunakan Advertising ID untuk iklan tertarget. Anda dapat menyetel ulang atau menonaktifkan Advertising ID via Pengaturan Android > Google > Iklan (Ads)."
                            )
                            PrivacyDataCard(
                                icon = Icons.Default.BugReport,
                                title = "Google Firebase — Firebase Crashlytics",
                                description = "Pelaporan error aplikasi dan crash diagnostik otomatis, termasuk breadcrumb teknis sinkronisasi sebagaimana dijelaskan pada Pasal 2."
                            )
                            PrivacyDataCard(
                                icon = Icons.Default.Verified,
                                title = "FentonGrowth.ca API (https://fentongrowth.ca)",
                                description = "Layanan kalkulator pertumbuhan prematur University of Calgary / Dr. Tanis Fenton yang dipanggil HANYA setelah konfirmasi pengguna melalui dialog FentonInternetConfirmationDialog. Payload CSV yang dikirim berisi: Title (nama tampilan bayi bila terisi), Number (ID numerik internal database lokal), Sex (M/F), Date of birth (dd-MMM-yyyy), GA at birth (minggu), serta titik pengukuran pertumbuhan: GA (weeks), Weight (g), Head (cm), dan Length (cm)."
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
                            text = "Aplikasi hanya mendeklarasikan 8 izin yang dibutuhkan dan selaras persis dengan file AndroidManifest.xml:",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            PrivacyPermissionCard(
                                permission = "INTERNET",
                                reason = "Menghubungkan Aplikasi ke Google Firebase (Firestore, Analytics, Crashlytics) serta memanggil FentonGrowth.ca API saat memuat grafik/Z-score online. Fitur harian tetap berjalan offline."
                            )
                            PrivacyPermissionCard(
                                permission = "ACCESS_NETWORK_STATE",
                                reason = "Mengecek ketersediaan koneksi internet sebelum memicu sinkronisasi cloud atau unduhan grafik agar tidak gagal diam-diam."
                            )
                            PrivacyPermissionCard(
                                permission = "POST_NOTIFICATIONS",
                                reason = "Menampilkan notifikasi timer sesi PMK berjalan, pengingat jadwal minum ASI, serta pengingat jadwal skrining prematur (dapat dimatikan di Pengaturan)."
                            )
                            PrivacyPermissionCard(
                                permission = "SCHEDULE_EXACT_ALARM",
                                reason = "Menjadwalkan alarm pengingat pemberian nutrisi dan sesi PMK agar dipicu tepat waktu sesuai jadwal pengasuh."
                            )
                            PrivacyPermissionCard(
                                permission = "USE_EXACT_ALARM (Izin Sensitif Kebijakan Alarm Google Play)",
                                reason = "Memastikan pengingat nutrisi enteral (ASI/OGT/NGT per 2–3 jam pada bayi BBLR berisiko hipoglikemia/dehidrasi), target PMK, dan alarm skrining kritis tetap berbunyi presisi meski ponsel memasuki mode hemat daya/Doze Android. Digunakan murni untuk alarm klinis perawatan & nutrisi bayi."
                            )
                            PrivacyPermissionCard(
                                permission = "RECEIVE_BOOT_COMPLETED",
                                reason = "Memulihkan dan menjadwalkan ulang seluruh alarm PMK, alarm ASI, dan alarm skrining secara otomatis setelah ponsel dinyalakan ulang (reboot) atau aplikasi diperbarui."
                            )
                            PrivacyPermissionCard(
                                permission = "VIBRATE",
                                reason = "Memberikan getaran lembut saat notifikasi pengingat berbunyi sehingga pengasuh tetap sadar meski perangkat dalam mode senyap di ruang perawatan/NICU."
                            )
                            PrivacyPermissionCard(
                                permission = "WRITE_EXTERNAL_STORAGE (maxSdkVersion=\"28\" / Android ≤ 9)",
                                reason = "Menyimpan ekspor gambar Grafik Pertumbuhan Fenton ke Galeri pada Android 9 ke bawah. Pada Android 10+ (API 29+), Aplikasi memakai MediaStore modern tanpa izin penyimpanan legacy."
                            )

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                color = colors.legalTealSurfaceBg
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FolderOpen,
                                            contentDescription = null,
                                            tint = colors.legalTealAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "Kamera, Galeri, & Dokumen Tanpa Izin Penuh (Privacy-Preserving Pickers)",
                                            style = KanguruTheme.typography.labelLarge,
                                            fontWeight = FontWeight.SemiBold,
                                            color = colors.legalTealText
                                        )
                                    }
                                    Text(
                                        text = "Aplikasi TIDAK mendeklarasikan izin CAMERA, READ_MEDIA_IMAGES, maupun READ_EXTERNAL_STORAGE di AndroidManifest.xml:\n• Kamera Sistem (TakePicture via FileProvider): pemotretan diserahkan ke aplikasi kamera bawaan Android dengan URI terisolasi.\n• Android Photo Picker (PickVisualMedia): Aplikasi tidak dapat memindai seluruh galeri, hanya menerima akses sementara ke 1 foto pilihan Anda.\n• System Document Picker / SAF (OpenDocument): unggahan PDF/foto lampiran medis hanya mengakses 1 berkas pilihan Anda lalu menyalinnya ke ruang privat (screening_attachments/).",
                                        style = KanguruTheme.typography.bodySmall,
                                        color = colors.legalTealText
                                    )
                                }
                            }
                        }
                    }

                    // ==========================================
                    // PASAL 7: KEAMANAN & ENKRIPSI
                    // ==========================================
                    PrivacySection(
                        number = "7",
                        title = "Langkah-Langkah Keamanan & Enkripsi Data"
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            PrivacySecurityCard(
                                icon = Icons.Default.Lock,
                                title = "Enkripsi dalam Transit",
                                description = "Seluruh lalu lintas data antara Aplikasi, layanan Google Firebase, dan server FentonGrowth.ca menggunakan protokol terenkripsi HTTPS / TLS."
                            )
                            PrivacySecurityCard(
                                icon = Icons.Default.Shield,
                                title = "Isolasi Application Sandbox & FileProvider",
                                description = "Database Room, DataStore, foto profil bayi, Z-score CSV, dan lampiran PDF/foto medis berada di ruang penyimpanan internal privat yang tidak dapat dibaca aplikasi lain; pembukaan lampiran memakai izin baca sementara FileProvider."
                            )
                            PrivacySecurityCard(
                                icon = Icons.AutoMirrored.Filled.Rule,
                                title = "Penegakan Aturan Database Granular (CEL)",
                                description = "Koleksi Cloud Firestore dilindungi validasi skema dan batas klinis Common Expression Language (CEL), akses create-only dari klien onboarding, serta larangan read/update/delete oleh publik."
                            )
                        }
                    }

                    // ==========================================
                    // PASAL 8: HAK-HAK PENGGUNA (UU PDP)
                    // ==========================================
                    PrivacySection(
                        number = "8",
                        title = "Hak-Hak Pengguna atas Data Pribadi (UU PDP)"
                    ) {
                        Text(
                            text = "Sesuai UU No. 27 Tahun 2022 tentang Pelindungan Data Pribadi, Anda memiliki hak penuh atas data Anda:",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            PrivacyRightCard(
                                title = "Hak Akses & Pembaruan",
                                description = "Anda dapat melihat dan mengubah data profil bayi beserta riwayat pertumbuhan kapan pun melalui menu Pengaturan > Profil Bayi."
                            )
                            PrivacyRightCard(
                                title = "Hak Penghapusan Data Lokal (Right to Erasure)",
                                description = "Hapus log aktivitas & jadwal lama lewat Pengaturan > Hapus Riwayat Aktivitas; reset seluruh database lewat Pengaturan > Reset Data Aplikasi; atau lakukan uninstall untuk menghapus permanen Room SQLite, foto profil, Z-score CSV, dan seluruh lampiran dokumen medis dari perangkat."
                            )
                            PrivacyRightCard(
                                title = "Hak Penghapusan Data Cloud",
                                description = "Anda berhak meminta penghapusan dokumen onboarding bayi di Cloud Firestore melalui prosedur resmi pada Pasal 10."
                            )
                            PrivacyRightCard(
                                title = "Hak Menarik Persetujuan",
                                description = "Anda berhak mencabut izin notifikasi/alarm di Setelan Aplikasi Android, mereset atau membatasi Advertising ID pada setelan Google Android, atau berhenti menggunakan Aplikasi kapan saja."
                            )
                        }
                    }

                    // ==========================================
                    // PASAL 9: PRIVASI ANAK & BAYI
                    // ==========================================
                    PrivacySection(
                        number = "9",
                        title = "Privasi Anak & Bayi (Children's Privacy)"
                    ) {
                        Text(
                            text = "Subjek data dalam Aplikasi mencakup data bayi baru lahir dan anak-anak. Aplikasi tidak ditujukan untuk dioperasikan secara mandiri oleh anak-anak di bawah umur dan hanya boleh digunakan oleh orang tua kandung, wali hukum yang sah, atau tenaga kesehatan pendamping dewasa. Seluruh data anak diinput atas otorisasi penuh orang tua/wali yang sah dan kami tidak pernah mengumpulkan kontak pribadi langsung anak (email/telepon anak).",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textPrimary
                        )
                    }

                    // ==========================================
                    // PASAL 10: RETENSI & MEKANISME PENGHAPUSAN DATA
                    // ==========================================
                    PrivacySection(
                        number = "10",
                        title = "Retensi & Mekanisme Penghapusan Data (Data Deletion Policy)"
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            PrivacyRightCard(
                                title = "Masa Retensi Data Terukur",
                                description = "• Data Lokal Perangkat: tersimpan selama Aplikasi terpasang atau hingga pengguna menjalankan Hapus Riwayat Aktivitas, Reset Data Aplikasi, atau uninstall.\n• Profil Onboarding Cloud (baby_onboarding_profiles): disimpan maksimal 24 (dua puluh empat) bulan sejak tanggal pencatatan (createdAtServer), atau dihapus lebih awal atas permintaan, lalu dihapus permanen atau dianonimkan seutuhnya.\n• Firebase Crashlytics & Analytics: log Crashlytics mengikuti retensi standar 90 hari, sedangkan data agregat Analytics maksimal 14 bulan sebelum kedaluwarsa otomatis."
                            )

                            PrivacySecurityCard(
                                icon = Icons.Default.DeleteForever,
                                title = "Mekanisme Permohonan Penghapusan Data Cloud",
                                description = "Karena Aplikasi dapat dipakai tanpa akun/login dan dokumen baby_onboarding_profiles memakai auto-generated Document ID tanpa User ID tetap di perangkat, ajukan penghapusan data cloud dengan:\n1. Kirim email ke support@kulitech.my.id dengan subjek: \"Permohonan Penghapusan Data Cloud - Kanguru Siaga\".\n2. Sertakan informasi pencocokan: nama bayi saat onboarding (babyName), tanggal lahir (birthDateFormatted), perkiraan tanggal & waktu pendaftaran onboarding diselesaikan, serta data verifikasi (usia gestasi lahir, berat badan lahir, jenis kelamin).\n3. Tim administrator KuliTech memverifikasi kecocokan dan menghapus dokumen terkait secara permanen selambat-lambatnya dalam 14 (empat belas) hari kerja disertai email konfirmasi."
                            )
                        }
                    }

                    // ==========================================
                    // PASAL 11: PERUBAHAN KEBIJAKAN PRIVASI
                    // ==========================================
                    PrivacySection(
                        number = "11",
                        title = "Perubahan Kebijakan Privasi"
                    ) {
                        PrivacySecurityCard(
                            icon = Icons.Default.Update,
                            title = "Pembaruan Dokumen & Pemberitahuan Perubahan Material",
                            description = "Kami dapat memperbarui Kebijakan Privasi ini mengikuti pembaruan fitur, sub-prosesor, pedoman Google Play Console, maupun hukum yang berlaku:\n• Setiap pembaruan mencantumkan Tanggal Berlaku dan Versi Dokumen terbaru di atas halaman ini (Pengaturan > Legalitas & Privasi > Kebijakan Privasi).\n• Untuk perubahan material terkait Data Pribadi yang Bersifat Spesifik atau pihak ketiga baru, pemberitahuan disampaikan melalui rilis Google Play Store dan/atau di dalam Aplikasi sebelum berlaku efektif.\n• Dengan terus menggunakan Aplikasi setelah tanggal berlaku, Anda menyetujui pembaruan tersebut; jika tidak setuju, Anda berhak menghentikan pemakaian, menghapus data lokal, dan mengajukan penghapusan cloud sesuai Pasal 10."
                        )
                    }

                    // ==========================================
                    // PASAL 12: KONTAK & LAYANAN PENGADUAN
                    // ==========================================
                    PrivacySection(number = "12", title = "Kontak & Layanan Pengaduan") {
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
                                    text = "KuliTech (Tim Pengembang Kanguru Siaga)",
                                    style = KanguruTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textPrimary
                                )

                                Text(
                                    text = "Kesehatan & Kebugaran Maternal & Neonatal (Maternal & Infant Health)",
                                    style = KanguruTheme.typography.bodySmall,
                                    color = colors.textSecondary
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable {
                                                try {
                                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                                        data = Uri.parse("mailto:support@kulitech.my.id")
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
                                            text = "support@kulitech.my.id",
                                            style = KanguruTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = colors.primary
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
                                text = "Dengan melanjutkan dan menggunakan Aplikasi Kanguru Siaga, Anda mengakui bahwa Anda telah membaca, memahami, serta menyetujui Ketentuan Penggunaan dan Kebijakan Privasi ini secara sukarela dan sadar hukum.",
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

