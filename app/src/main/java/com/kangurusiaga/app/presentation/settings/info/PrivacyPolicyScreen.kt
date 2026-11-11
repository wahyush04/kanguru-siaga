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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.EnhancedEncryption
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LockPerson
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhotoCameraFront
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RuleFolder
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VolunteerActivism
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
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
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ==========================================
            // DOCUMENT HEADER CARD
            // ==========================================
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = colors.surface,
                border = BorderStroke(1.dp, colors.cardBorder),
                shadowElevation = 1.dp
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Ambient corner accent matching Stitch top-right subtle rose circle
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 24.dp, y = (-24).dp)
                            .size(112.dp)
                            .clip(CircleShape)
                            .background(colors.legalRoseTintBg.copy(alpha = 0.7f))
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = colors.legalRoseTintBg
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Policy,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "BAGIAN II • KEBIJAKAN PRIVASI",
                                    style = KanguruTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Kebijakan Privasi Aplikasi Kanguru Siaga",
                            style = KanguruTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Privacy Policy & Pelindungan Data Pribadi Bayi & Pengguna",
                            style = KanguruTheme.typography.bodyMedium,
                            color = colors.textSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Metadata pill box
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = colors.background
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                PrivacyMetaRow(
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

                                PrivacyMetaRow(
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

                                PrivacyMetaRow(
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

                                HorizontalDivider(color = colors.divider)

                                Row(
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
                                        text = "Kepatuhan: Sesuai UU No. 27/2022 (UU PDP), Standar Etika Medis Perinatologi, serta Perlindungan Khusus Data Anak & Kesehatan.",
                                        style = KanguruTheme.typography.bodySmall,
                                        color = colors.textSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Developer Commitment Banner
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = colors.legalTealSurfaceBg
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Surface(
                                    modifier = Modifier.size(32.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    color = colors.surface,
                                    shadowElevation = 0.5.dp
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.VolunteerActivism,
                                            contentDescription = null,
                                            tint = colors.legalTealAccent,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "Komitmen Pengembang untuk Anda dan Buah Hati",
                                        style = KanguruTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.legalTealAccent
                                    )
                                    Text(
                                        text = buildAnnotatedString {
                                            append("Kami di KuliTech memegang prinsip bahwa data kesehatan bayi premature serta BBLR (Berat Badan Lahir Rendah) adalah data paling sensitif. Aplikasi Kanguru Siaga dirancang dengan filosofi ")
                                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = colors.textPrimary)) {
                                                append("Offline-First")
                                            }
                                            append(" dan ")
                                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = colors.textPrimary)) {
                                                append("Privacy by Design")
                                            }
                                            append(": Anda adalah pemilik mutlak data buah hati Anda.")
                                        },
                                        style = KanguruTheme.typography.bodySmall,
                                        color = colors.textSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // SECTION 1
            // ==========================================
            PrivacyNumberedSection(
                number = "1",
                title = "Komitmen Privasi & Dasar Hukum (UU PDP)",
                subtitle = "Integritas dan keamanan digital bayi prematur serta BBLR adalah prioritas mutlak kami. Pemrosesan data spesifik tunduk pada ketentuan perundang-undangan Republik Indonesia."
            ) {
                PrivacyHighlightItemCard(
                    icon = Icons.Default.MedicalServices,
                    iconTint = colors.primary,
                    iconBoxColor = colors.legalRoseTintBg,
                    title = "Kategori Data Pribadi Spesifik (Pasal 4 ayat 2)",
                    description = AnnotatedString("Data antropometri, rekam medis klinis bayi, dan data kelahiran diklasifikasikan sebagai data pribadi yang bersifat spesifik dan mendapatkan proteksi keamanan enkripsi tingkat tinggi.")
                )
                PrivacyHighlightItemCard(
                    icon = Icons.Default.LockPerson,
                    iconTint = colors.legalTealAccent,
                    iconBoxColor = colors.legalTealSurfaceBg,
                    title = "Persetujuan Eksplisit (Explicit Consent)",
                    description = AnnotatedString("Persetujuan tegas diperoleh saat onboarding serta sebelum data antropometri dikirimkan secara selektif ke modul kalkulasi daring FentonGrowth.ca.")
                )
                PrivacyHighlightItemCard(
                    icon = Icons.Default.Block,
                    iconTint = colors.primary,
                    iconBoxColor = colors.legalRoseTintBg,
                    title = "Bebas Penjualan Data & Tanpa Iklan",
                    description = buildAnnotatedString {
                        append("Kami ")
                        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = colors.primary)) {
                            append("TIDAK PERNAH")
                        }
                        append(" menjual, memperdagangkan, menyewakan, atau memberikan data identitas atau rekam medis bayi kepada pihak ketiga, broker data, asuransi, ataupun jaringan periklanan.")
                    }
                )
                PrivacyHighlightItemCard(
                    icon = Icons.Default.DevicesOther,
                    iconTint = colors.textPrimary,
                    iconBoxColor = colors.legalSectionCardBg,
                    title = "Offline-First & Privacy by Design",
                    description = AnnotatedString("100% catatan sesi harian Metode Kanguru (KMC), jadwal nutrisi, berkas skrining medis, dan foto buah hati tersimpan lokal pada ruang sandboxed internal perangkat gawai Anda.")
                )
            }

            // ==========================================
            // SECTION 2
            // ==========================================
            PrivacyNumberedSection(
                number = "2",
                title = "Informasi & Data yang Dikumpulkan",
                subtitle = "Aplikasi Kanguru Siaga memproses parameter berikut sesuai kebutuhan kalkulasi klinis persentil pertumbuhan:"
            ) {
                PrivacyDataCategoryCard(
                    icon = Icons.Default.Badge,
                    iconTint = colors.primary,
                    title = "Profil Identitas Kelahiran Bayi",
                    badgeText = "Lokal + Cloud",
                    isLocalHighlight = false,
                    description = buildAnnotatedString {
                        append("Nama panggilan, tanggal & jam lahir, usia gestasi saat lahir (minggu + hari), jenis kelamin biologis, berat/panjang lahir. Disimpan di Room SQLite lokal & sinkronisasi 1x saat onboarding ke koleksi Firestore ")
                        appendCodePill("baby_onboarding_profiles", colors.textPrimary, colors.legalSectionCardBg)
                        append(".")
                    }
                )
                PrivacyDataCategoryCard(
                    icon = Icons.Default.Straighten,
                    iconTint = colors.legalTealAccent,
                    title = "Data Antropometri Berkala",
                    badgeText = "Lokal + API Opsional",
                    isLocalHighlight = false,
                    description = AnnotatedString("Berat badan harian, panjang badan, lingkar kepala berkala. Disimpan lokal di Room SQLite dan berkas CSV. Hanya dikirim ke endpoint kalkulasi FentonGrowth.ca API jika pengguna memberikan konfirmasi eksplisit untuk menghasilkan grafik persentil.")
                )
                PrivacyDataCategoryCard(
                    icon = Icons.Default.Timer,
                    iconTint = colors.primary,
                    title = "Aktivitas PMK & Pemantauan Nutrisi",
                    badgeText = "100% Lokal",
                    isLocalHighlight = true,
                    description = AnnotatedString("Durasi sesi kontak kulit-ke-kulit (skin-to-skin), target harian jam PMK, jadwal nutrisi enteral (ASI / OGT / NGT), dan alarm terjadwal. Sepenuhnya tersimpan di database lokal perangkat Anda dan tidak pernah dikirim ke Cloud Firestore.")
                )
                PrivacyDataCategoryCard(
                    icon = Icons.Default.FolderSpecial,
                    iconTint = colors.primary,
                    title = "Skrining Medis & Lampiran Berkas",
                    badgeText = "100% Lokal",
                    isLocalHighlight = true,
                    description = buildAnnotatedString {
                        append("Catatan skrining esensial BBLR (ROP mata, BERA pendengaran, USG kepala). Berkas lampiran PDF atau foto hasil lab disimpan aman dalam direktori privat gawai ")
                        appendCodePill("screening_attachments/", colors.textPrimary, colors.legalSectionCardBg)
                        append(" (maks. 10MB per berkas).")
                    }
                )
                PrivacyDataCategoryCard(
                    icon = Icons.Default.PhotoCameraFront,
                    iconTint = colors.primary,
                    title = "Media Foto Profil Bayi",
                    badgeText = "100% Lokal",
                    isLocalHighlight = true,
                    description = buildAnnotatedString {
                        append("Foto buah hati untuk kartu profil disimpan pada direktori terproteksi ")
                        appendCodePill("baby_photos/", colors.textPrimary, colors.legalSectionCardBg)
                        append(" di memori perangkat Anda. ")
                        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = colors.primary)) {
                            append("Foto tidak pernah diunggah, ditransmisikan, atau disimpan pada server cloud kami.")
                        }
                    }
                )
                PrivacyDataCategoryCard(
                    icon = Icons.Default.Analytics,
                    iconTint = colors.textSecondary,
                    title = "Data Diagnostik & Crashlytics",
                    badgeText = "Anonim",
                    isLocalHighlight = false,
                    description = AnnotatedString("Firebase Analytics & Crashlytics merekam log galat terenkripsi tanpa data nama bayi ataupun informasi rekam medis pribadi, semata-mata untuk menjaga kestabilan aplikasi saat sesi pemantauan kritis di ruang perawatan atau rumah.")
                )
            }

            // ==========================================
            // SECTION 3
            // ==========================================
            PrivacyNumberedSection(
                number = "3",
                title = "Arsitektur Penyimpanan: Lokal & Cloud"
            ) {
                PrivacyHighlightItemCard(
                    icon = Icons.Default.PhoneAndroid,
                    iconTint = colors.legalTealAccent,
                    iconBoxColor = colors.legalTealSurfaceBg,
                    title = "Penyimpanan Lokal Sandbox (Offline-First)",
                    description = AnnotatedString("Data tersimpan di Room SQLite, Jetpack DataStore, dan berkas privat sandboxed internal perangkat. Aplikasi beroperasi 100% normal dan lancar bahkan saat perangkat dalam Mode Pesawat atau tanpa akses internet.")
                )
                PrivacyHighlightItemCard(
                    icon = Icons.Default.CloudSync,
                    iconTint = colors.primary,
                    iconBoxColor = colors.legalSectionCardBg,
                    title = "Sinkronisasi Cloud Terbatas (Google Firestore)",
                    description = buildAnnotatedString {
                        append("Hanya 15 atribut data profil onboarding terkirim saat pendaftaran awal. Dilindungi oleh Firebase Security Rules (CEL) berbasis ")
                        withStyle(
                            SpanStyle(
                                fontStyle = FontStyle.Italic,
                                fontFamily = FontFamily.Monospace,
                                color = colors.textPrimary
                            )
                        ) {
                            append("create-only")
                        }
                        append(": klien tidak memiliki akses membaca, memperbarui, atau menghapus data publik secara bebas.")
                    }
                )
            }

            // ==========================================
            // SECTION 4
            // ==========================================
            PrivacyNumberedSection(
                number = "4",
                title = "Tujuan Pengolahan Data"
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = colors.surface,
                    border = BorderStroke(1.dp, colors.cardBorder),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PrivacyCheckRow(
                            title = "Fungsionalitas Klinis Neonatus",
                            description = "Otomatisasi kalkulasi usia kronologis, Usia Koreksi (Corrected Age), dan Usia Postmenstrual (PMA) untuk memetakan pertumbuhan kurva Fenton 2013 secara akurat."
                        )
                        PrivacyCheckRow(
                            title = "Alarm & Manajemen Waktu Kritis",
                            description = "Menghasilkan pengingat berkala pemberian nutrisi ASI per 2-3 jam demi kepatuhan klinis serta mencatat target durasi Metode Kanguru."
                        )
                        PrivacyCheckRow(
                            title = "Kenyamanan Visual & Personal",
                            description = "Mengatur preferensi kenyamanan tampilan mata (mode redup malam di ruang NICU) serta skala ukuran font kenyamanan mata orang tua."
                        )
                        PrivacyCheckRow(
                            title = "Evaluasi Anonim Kesehatan Bayi",
                            description = "Pengembangan algoritma komplikasi hipotermia pada bayi berat lahir rendah secara teragregasi tanpa data identitas perorangan."
                        )
                    }
                }
            }

            // ==========================================
            // SECTION 5
            // ==========================================
            PrivacyNumberedSection(
                number = "5",
                title = "Layanan Pihak Ketiga & Sub-prosesor"
            ) {
                PrivacyHighlightItemCard(
                    icon = Icons.Default.Hub,
                    iconTint = colors.textPrimary,
                    iconBoxColor = colors.legalSectionCardBg,
                    title = "Google Firebase (Cloud Platform)",
                    description = AnnotatedString("Menyediakan infrastruktur Firestore, Firebase Crashlytics, dan Firebase Analytics yang tersertifikasi kepatuhan ISO 27001 dan SOC 2 Type II untuk integritas infrastruktur cloud.")
                )
                PrivacyHighlightItemCard(
                    icon = Icons.AutoMirrored.Filled.ShowChart,
                    iconTint = colors.primary,
                    iconBoxColor = colors.legalRoseTintBg,
                    title = "FentonGrowth.ca API",
                    description = AnnotatedString("Layanan kalkulasi persentil Z-score kurva pertumbuhan bayi preterm internasional. Data yang dikirim hanya berupa usia gestasi, jenis kelamin, dan angka antropometri saat pengguna menekan tombol konfirmasi pembuatan grafik.")
                )
            }

            // ==========================================
            // SECTION 6
            // ==========================================
            PrivacyNumberedSection(
                number = "6",
                title = "Izin Akses Perangkat (Device Permissions)",
                subtitle = "Setiap izin sistem Android diminta secara transparan dan berlandaskan kebutuhan fungsional medis yang esensial:"
            ) {
                PrivacyPermissionItemCard(
                    permission = "POST_NOTIFICATIONS (Android 13+)",
                    category = "Sistem",
                    isRosePermission = true,
                    isTealCategory = false,
                    description = "Menampilkan banner timer sesi Kangaroo Mother Care yang sedang berjalan dan pengingat jadwal minum ASI tepat waktu."
                )
                PrivacyPermissionItemCard(
                    permission = "SCHEDULE_EXACT_ALARM",
                    category = "Alarm Presisi",
                    isRosePermission = true,
                    isTealCategory = true,
                    description = "Memicu pengingat pemberian nutrisi ASI per 2-3 jam, target sesi PMK, dan alarm skrining pada waktu presisi jika diaktifkan; bila izin belum diberikan pada Android 14+, aplikasi otomatis beralih ke pengingat standar tanpa crash."
                )
                PrivacyPermissionItemCard(
                    permission = "INTERNET & ACCESS_NETWORK_STATE",
                    category = "Jaringan",
                    isRosePermission = false,
                    isTealCategory = false,
                    description = "Mendeteksi ketersediaan sambungan online untuk sinkronisasi profil awal, pelaporan crash anonim, dan akses grafik FentonGrowth."
                )
                PrivacyPermissionItemCard(
                    permission = "RECEIVE_BOOT_COMPLETED & VIBRATE",
                    category = "Alarm",
                    isRosePermission = false,
                    isTealCategory = false,
                    description = "Menjadwalkan ulang pengingat nutrisi bayi setelah perangkat dinyalakan ulang dan memberikan getaran haptik saat alarm berbunyi."
                )
                PrivacyPermissionItemCard(
                    permission = "WRITE_EXTERNAL_STORAGE (Android ≤ 9)",
                    category = "Legacy",
                    isRosePermission = false,
                    isTealCategory = false,
                    isMutedPermission = true,
                    description = "Diperlukan hanya pada perangkat Android versi 9 ke bawah untuk menyimpan berkas ekspor grafik pertumbuhan ke direktori berkas pengguna."
                )

                // Privacy-Preserving Features Callout
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = colors.legalTealSurfaceBg
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = colors.legalTealAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Fitur Ramah Privasi (Zero Invasive Permissions)",
                                style = KanguruTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.legalTealAccent
                            )
                            Text(
                                text = buildAnnotatedString {
                                    append("Pengambilan foto menggunakan ")
                                    appendCodeText("TakePicture FileProvider", colors.textPrimary)
                                    append(" dan pemilihan dari galeri melalui ")
                                    appendCodeText("Android Photo Picker", colors.textPrimary)
                                    append(", serta ekspor PDF menggunakan ")
                                    appendCodeText("Storage Access Framework", colors.textPrimary)
                                    append(" tanpa perlu meminta akses izin CAMERA atau izin penyimpanan perangkat secara menyeluruh.")
                                },
                                style = KanguruTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }

            // ==========================================
            // SECTION 7
            // ==========================================
            PrivacyNumberedSection(
                number = "7",
                title = "Langkah Keamanan & Enkripsi"
            ) {
                PrivacyHighlightItemCard(
                    icon = Icons.Default.EnhancedEncryption,
                    iconTint = colors.legalTealAccent,
                    iconBoxColor = colors.legalTealSurfaceBg,
                    title = "Enkripsi Data dalam Transit (TLS 1.3 / HTTPS)",
                    description = AnnotatedString("Seluruh data komunikasi yang keluar ditransmisikan menggunakan protokol keamanan TLS 1.3 terenkripsi untuk mencegah intersepsi jaringan (man-in-the-middle).")
                )
                PrivacyHighlightItemCard(
                    icon = Icons.Default.Security,
                    iconTint = colors.primary,
                    iconBoxColor = colors.legalRoseTintBg,
                    title = "Android Application Sandbox",
                    description = AnnotatedString("Database Room dan berkas dokumen bayi tersimpan di direktori memori internal privat gawai yang terlindungi oleh sistem operasi Android dan tidak dapat diakses langsung oleh aplikasi lain.")
                )
                PrivacyHighlightItemCard(
                    icon = Icons.Default.RuleFolder,
                    iconTint = colors.textPrimary,
                    iconBoxColor = colors.legalSectionCardBg,
                    title = "Aturan Database Granular (CEL Rules)",
                    description = AnnotatedString("Penyaringan ketat berbasis Common Expression Language pada tingkat server untuk memverifikasi validitas data input klinis dan memblokir upaya injeksi ilegal.")
                )
            }

            // ==========================================
            // SECTION 8
            // ==========================================
            PrivacyNumberedSection(
                number = "8",
                title = "Hak-Hak Pengguna (Berdasarkan UU PDP)",
                subtitle = "Sebagai subjek data pribadi, Anda memiliki hak penuh sesuai ketentuan UU RI No. 27 Tahun 2022:"
            ) {
                PrivacySimpleRightCard(
                    icon = Icons.Default.EditNote,
                    iconTint = colors.primary,
                    title = "Hak Akses & Pembaruan",
                    description = "Anda dapat melihat, merevisi, dan memperbarui seluruh data profil bayi dan data antropometri kapan saja secara langsung melalui menu pengaturan aplikasi."
                )
                PrivacySimpleRightCard(
                    icon = Icons.Default.DeleteForever,
                    iconTint = colors.primary,
                    title = "Hak Penghapusan Data (Right to Erasure)",
                    description = "Hapus seluruh data lokal melalui opsi “Hapus Semua Data” di aplikasi, atau dengan mencopot pemasangan (uninstall) aplikasi dari perangkat Anda."
                )
                PrivacySimpleRightCard(
                    icon = Icons.Default.Cancel,
                    iconTint = colors.legalTealAccent,
                    title = "Hak Menarik Persetujuan",
                    description = "Anda berhak membatalkan persetujuan pemrosesan notifikasi dan izin alarm kapan saja melalui menu Pengaturan Aplikasi gawai Anda."
                )
            }

            // ==========================================
            // SECTION 9
            // ==========================================
            PrivacyNumberedSection(
                number = "9",
                title = "Privasi Anak & Bayi (Children's Privacy)"
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = colors.surface,
                    border = BorderStroke(1.dp, colors.cardBorder),
                    shadowElevation = 1.dp
                ) {
                    Text(
                        text = "Aplikasi Kanguru Siaga ditujukan khusus untuk digunakan oleh orang tua, wali sah, atau tenaga medis profesional dewasa. Aplikasi tidak dirancang untuk dioperasikan secara mandiri oleh anak-anak. Seluruh pencatatan data anak dilakukan di bawah kendali dan otorisasi penuh orang tua atau wali yang berwenang.",
                        style = KanguruTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            // ==========================================
            // SECTION 10
            // ==========================================
            PrivacyNumberedSection(
                number = "10",
                title = "Retensi & Kebijakan Penghapusan Data Cloud"
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = colors.surface,
                    border = BorderStroke(1.dp, colors.cardBorder),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Jangka Waktu Retensi Data",
                            style = KanguruTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PrivacyRetentionCell(
                                    label = "Data Lokal",
                                    value = "Selama Terpasang",
                                    modifier = Modifier.weight(1f)
                                )
                                PrivacyRetentionCell(
                                    label = "Cloud Firestore",
                                    value = "Maks. 24 Bulan",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PrivacyRetentionCell(
                                    label = "Crashlytics",
                                    value = "90 Hari",
                                    modifier = Modifier.weight(1f)
                                )
                                PrivacyRetentionCell(
                                    label = "Firebase Analytics",
                                    value = "14 Bulan",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Deletion Request Guide Box
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
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Prosedur Permohonan Penghapusan Data Cloud",
                                style = KanguruTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.primary
                            )
                        }

                        Text(
                            text = buildAnnotatedString {
                                append("Kirimkan email ke ")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = colors.textPrimary)) {
                                    append("support@kulitech.my.id")
                                }
                                append(" dengan subjek:")
                            },
                            style = KanguruTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(6.dp),
                            color = colors.surface
                        ) {
                            Text(
                                text = "Permohonan Penghapusan Data Cloud - Kanguru Siaga",
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace,
                                color = colors.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }

                        Text(
                            text = buildAnnotatedString {
                                append("Sertakan nama panggilan bayi dan perkiraan tanggal registrasi onboarding untuk verifikasi kecocokan. Proses verifikasi dan penghapusan permanen akan diselesaikan paling lambat dalam ")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = colors.textPrimary)) {
                                    append("14 hari kerja")
                                }
                                append(".")
                            },
                            style = KanguruTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }
            }

            // ==========================================
            // SECTION 11
            // ==========================================
            PrivacyNumberedSection(
                number = "11",
                title = "Perubahan Kebijakan Privasi"
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = colors.surface,
                    border = BorderStroke(1.dp, colors.cardBorder),
                    shadowElevation = 1.dp
                ) {
                    Text(
                        text = "Kami dapat memperbarui Kebijakan Privasi ini dari waktu ke waktu untuk menyesuaikan perkembangan teknis maupun regulasi perlindungan data. Setiap pembaruan substansial akan diberitahukan melalui banner dalam aplikasi dan dicantumkan pada tanggal berlaku di bagian atas dokumen ini.",
                        style = KanguruTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            // ==========================================
            // SECTION 12
            // ==========================================
            PrivacyNumberedSection(
                number = "12",
                title = "Kontak & Layanan Pengaduan"
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = colors.surface,
                    border = BorderStroke(1.dp, colors.cardBorder),
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Tim Pengembang Kanguru Siaga (KuliTech)",
                            style = KanguruTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )

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
                                }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mail,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "support@kulitech.my.id",
                                style = KanguruTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = colors.primary
                            )
                        }

                        PrivacyContactMetaRow(
                            icon = Icons.Default.Category,
                            text = "Pendamping Pengasuhan & Tumbuh Kembang Bayi"
                        )

                        PrivacyContactMetaRow(
                            icon = Icons.Default.Public,
                            text = "Wilayah Hukum: Republik Indonesia"
                        )
                    }
                }
            }

            // ==========================================
            // WARM TAGLINE FOOTER
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
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
                        color = colors.primary
                    )
                }
                Text(
                    text = "Menjaga Hangat, Mengawal Tumbuh",
                    style = KanguruTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    color = colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun PrivacyMetaRow(
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
private fun PrivacyNumberedSection(
    number: String,
    title: String,
    subtitle: String? = null,
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
                    .background(colors.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number,
                    style = KanguruTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.onPrimary
                )
            }
            Text(
                text = title,
                style = KanguruTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
        }

        if (subtitle != null) {
            Text(
                text = subtitle,
                style = KanguruTheme.typography.bodySmall,
                color = colors.textSecondary
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            content()
        }
    }
}

@Composable
private fun PrivacyHighlightItemCard(
    icon: ImageVector,
    iconTint: Color,
    iconBoxColor: Color,
    title: String,
    description: AnnotatedString
) {
    val colors = KanguruTheme.colors
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = colors.surface,
        border = BorderStroke(1.dp, colors.cardBorder),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBoxColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
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
}

@Composable
private fun PrivacyDataCategoryCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    badgeText: String,
    isLocalHighlight: Boolean,
    description: AnnotatedString
) {
    val colors = KanguruTheme.colors
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = colors.surface,
        border = BorderStroke(1.dp, colors.cardBorder),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
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

                Surface(
                    shape = CircleShape,
                    color = if (isLocalHighlight) colors.legalTealSurfaceBg else colors.legalSectionCardBg,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = badgeText,
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = if (isLocalHighlight) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (isLocalHighlight) colors.legalTealAccent else colors.textSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
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
private fun PrivacyCheckRow(
    title: String,
    description: String
) {
    val colors = KanguruTheme.colors
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = colors.legalTealAccent,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(18.dp)
        )
        Column(
            modifier = Modifier.weight(1f),
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

@Composable
private fun PrivacyPermissionItemCard(
    permission: String,
    category: String,
    isRosePermission: Boolean,
    isTealCategory: Boolean,
    isMutedPermission: Boolean = false,
    description: String
) {
    val colors = KanguruTheme.colors
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = colors.surface,
        border = BorderStroke(1.dp, colors.cardBorder),
        shadowElevation = 1.dp
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
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (isRosePermission) colors.legalRoseTintBg else colors.legalSectionCardBg,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(
                        text = permission,
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        color = when {
                            isRosePermission -> colors.primary
                            isMutedPermission -> colors.textSecondary
                            else -> colors.textPrimary
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = category,
                    style = KanguruTheme.typography.labelMedium,
                    fontWeight = if (isTealCategory) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (isTealCategory) colors.legalTealAccent else colors.textSecondary,
                    modifier = Modifier.padding(start = 8.dp)
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
private fun PrivacySimpleRightCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    description: String
) {
    val colors = KanguruTheme.colors
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = colors.surface,
        border = BorderStroke(1.dp, colors.cardBorder),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(20.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
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
}

@Composable
private fun PrivacyRetentionCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = colors.legalSectionCardBg
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = KanguruTheme.typography.labelMedium,
                color = colors.textSecondary
            )
            Text(
                text = value,
                style = KanguruTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
        }
    }
}

@Composable
private fun PrivacyContactMetaRow(
    icon: ImageVector,
    text: String
) {
    val colors = KanguruTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = text,
            style = KanguruTheme.typography.bodySmall,
            color = colors.textSecondary
        )
    }
}

private fun AnnotatedString.Builder.appendCodePill(
    text: String,
    textColor: Color,
    bgColor: Color
) {
    withStyle(
        SpanStyle(
            fontFamily = FontFamily.Monospace,
            color = textColor,
            background = bgColor
        )
    ) {
        append(" $text ")
    }
}

private fun AnnotatedString.Builder.appendCodeText(
    text: String,
    textColor: Color
) {
    withStyle(
        SpanStyle(
            fontFamily = FontFamily.Monospace,
            color = textColor
        )
    ) {
        append(text)
    }
}
