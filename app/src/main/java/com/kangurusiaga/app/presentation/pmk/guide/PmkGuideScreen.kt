package com.kangurusiaga.app.presentation.pmk.guide

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Diversity1
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import kotlinx.coroutines.launch

@Composable
fun PmkGuideScreen(
    onNavigateBack: () -> Unit,
    onNavigateToTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    var isBookmarked by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = {
            Surface(
                color = colors.background,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(colors.surfaceElevated)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back),
                            tint = colors.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "PANDUAN KLINIS",
                            style = KanguruTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primary
                        )
                        Text(
                            text = "Panduan PMK Kontinu",
                            style = KanguruTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }

                    IconButton(
                        onClick = { isBookmarked = !isBookmarked },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(colors.surfaceElevated)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Simpan Panduan",
                            tint = colors.primary,
                            modifier = Modifier.size(20.dp)
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
                .padding(horizontal = 20.dp, vertical = 6.dp)
        ) {
            // =========================================================================
            // HEADER BANNER EDUKATIF
            // =========================================================================
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.primaryBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.primary)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PMK KONTINU MANDIRI",
                                    style = KanguruTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "WHO & IDAI",
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Pedoman Pelaksanaan PMK Kontinu di Rumah",
                        style = KanguruTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.textPrimary,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "PMK Kontinu adalah kontak kulit-ke-kulit (skin-to-skin) tanpa henti selama 24 jam dengan target ≥20 jam per hari. Bayi hanya dilepas sebentar untuk perawatan esensial seperti ganti popok atau mandi lap cepat.",
                        style = KanguruTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Target Meter Pill
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.surfaceVariant)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(colors.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Target Dekapan Harian",
                                    style = KanguruTheme.typography.labelSmall,
                                    fontSize = 11.sp,
                                    color = colors.textSecondary
                                )
                                Text(
                                    text = "≥ 20 Jam / Hari",
                                    style = KanguruTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(colors.successContainer)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Target Ideal",
                                style = KanguruTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.onSuccessContainer
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // =========================================================================
            // VISUAL CONTEXT BANNER
            // =========================================================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.surfaceElevated)
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(colors.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🤱", fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Kenyamanan & Detak Jantung Ibu",
                                style = KanguruTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(colors.surfaceVariant)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "24 Jam",
                                    style = KanguruTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textSecondary
                                )
                            }
                        }
                        Text(
                            text = "Menjaga suhu stabil, saturasi oksigen, & berat badan naik teratur.",
                            style = KanguruTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            color = colors.textSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // =========================================================================
            // PILAR 01: PRINSIP ESTAFET KELUARGA
            // =========================================================================
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.warningContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Diversity1,
                                contentDescription = null,
                                tint = colors.warning,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "PILAR 01",
                                style = KanguruTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = colors.warning
                            )
                            Text(
                                text = "Prinsip Estafet Keluarga (Kunci Sukses 24 Jam)",
                                style = KanguruTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Ibu butuh waktu mandi, makan, pulih, dan tidur lelap. Keterlibatan Ayah dan anggota keluarga terlatih sangat penting agar kontak kulit tetap berjalan tanpa terputus.",
                        style = KanguruTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4 Visual Schedule Cards
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        EstafetSlotItem(
                            time = "06:00 - 12:00",
                            title = "Ibu Kandung",
                            desc = "Sesi pagi hangat, stimulasi ASI & menyusui",
                            icon = Icons.Default.WaterDrop,
                            iconTint = colors.primary,
                            bgColor = colors.primaryContainer
                        )

                        EstafetSlotItem(
                            time = "12:00 - 16:00",
                            title = "Nenek / Pendamping",
                            desc = "Sesi siang (Ibu tidur nyenyak & makan siang)",
                            icon = Icons.Default.Favorite,
                            iconTint = colors.warning,
                            bgColor = colors.warningContainer
                        )

                        EstafetSlotItem(
                            time = "16:00 - 21:00",
                            title = "Ayah",
                            desc = "Bonding sore hari pasca pulang beraktivitas",
                            icon = Icons.Default.Face,
                            iconTint = colors.caregiverAyah,
                            bgColor = colors.caregiverAyahContainer
                        )

                        EstafetSlotItem(
                            time = "21:00 - 06:00",
                            title = "Ibu / Ayah Bergantian",
                            desc = "Sesi malam (posisi tidur semi-duduk 30°–45°)",
                            icon = Icons.Default.Bedtime,
                            iconTint = colors.info,
                            bgColor = colors.infoContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // =========================================================================
            // PILAR 02: POSISI DEKAPAN & KEAMANAN SALURAN NAPAS (M-SHAPE)
            // =========================================================================
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChildCare,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "PILAR 02",
                                    style = KanguruTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = colors.primary
                                )
                                Text(
                                    text = "Posisi Dekapan & Keamanan Napas",
                                    style = KanguruTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.successContainer)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "M-Shape",
                                style = KanguruTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.onSuccessContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 5 Numbered Steps
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        NumberedStepItem(
                            stepNumber = "1",
                            title = "Kontak Kulit-ke-Kulit Penuh",
                            desc = "Dada bayi menempel langsung ke dada telanjang orang tua. Bayi hanya mengenakan popok, topi katun hangat, dan kaus kaki."
                        )
                        NumberedStepItem(
                            stepNumber = "2",
                            title = "Jalan Napas Terbuka (Sniffing Position)",
                            desc = "Kepala bayi menoleh ke satu sisi dengan dagu agak tengadah (sedikit ekstensi). Dagu TIDAK boleh menekuk menempel ke dada bayi sendiri."
                        )
                        NumberedStepItem(
                            stepNumber = "3",
                            title = "Kaki Katak (Frog / M-Shape)",
                            desc = "Kedua paha tertekuk dan membuka merangkul perut/dada pengasuh, kedua tangan terlipat santai di atas dada bayi."
                        )
                        NumberedStepItem(
                            stepNumber = "4",
                            title = "Kain / Selendang Pengikat Kencang",
                            desc = "Kain selendang menopang dari bawah bokong bayi hingga cuping telinga. Bayi tidak akan merosot jika pengasuh berdiri santai."
                        )
                        NumberedStepItem(
                            stepNumber = "5",
                            title = "Tidur Semi-Duduk (30° - 45°)",
                            desc = "Orang tua dilarang tidur telentang datar 180°. Sangga punggung dengan 3–4 bantal tebal untuk mencegah risiko asfiksia dan tertindih."
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // =========================================================================
            // PILAR 03: ATURAN JEDA KONTAK KULIT
            // =========================================================================
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.successContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PauseCircle,
                                contentDescription = null,
                                tint = colors.success,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "PILAR 03",
                                style = KanguruTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = colors.success
                            )
                            Text(
                                text = "Aturan Jeda Kontak Kulit",
                                style = KanguruTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Setiap menit di luar dada menurunkan kestabilan suhu tubuh bayi prematur. Jeda hanya diizinkan maksimal 15–30 menit untuk kondisi tertentu:",
                        style = KanguruTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3 Mini Boxes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MiniActionBox(
                            icon = Icons.Default.CleaningServices,
                            iconTint = colors.primary,
                            title = "Ganti Popok",
                            desc = "<5 Menit",
                            modifier = Modifier.weight(1f)
                        )
                        MiniActionBox(
                            icon = Icons.Default.Bathtub,
                            iconTint = colors.warning,
                            title = "Lap Hangat",
                            desc = "Bukan Mandi Bak",
                            modifier = Modifier.weight(1f)
                        )
                        MiniActionBox(
                            icon = Icons.Default.Thermostat,
                            iconTint = colors.success,
                            title = "Cek Vital",
                            desc = "Suhu & Nafas",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Warning Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.warningContainer)
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = colors.warning,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 1.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Hindari memandikan bayi dengan merendam di bak air dingin sampai berat badan mencapai 2.500 gram.",
                                style = KanguruTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = colors.onWarningContainer,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // =========================================================================
            // PILAR 04: PEMANTAUAN VITAL MANDIRI
            // =========================================================================
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.caregiverAyahContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonitorHeart,
                                contentDescription = null,
                                tint = colors.caregiverAyah,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "PILAR 04",
                                style = KanguruTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = colors.caregiverAyah
                            )
                            Text(
                                text = "Pemantauan Vital Mandiri",
                                style = KanguruTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        VitalParamRow(
                            icon = Icons.Default.Thermostat,
                            iconTint = colors.primary,
                            title = "Suhu Tubuh Normal",
                            badge = "36,5°C - 37,5°C",
                            desc = "Raba tengkuk dan telapak kaki bayi; harus terasa hangat merata."
                        )
                        VitalParamRow(
                            icon = Icons.Default.Air,
                            iconTint = colors.caregiverAyah,
                            title = "Frekuensi Napas",
                            badge = "40 - 60 x/menit",
                            desc = "Bernapas tenang, dinding dada naik turun tanpa tarikan cekung ke dalam."
                        )
                        VitalParamRow(
                            icon = Icons.Default.Palette,
                            iconTint = colors.success,
                            title = "Warna Bibir & Tubuh",
                            badge = "Pink Segar",
                            desc = "Kemerahan segar di seluruh tubuh, tidak pucat pasi atau kebiruan."
                        )
                        VitalParamRow(
                            icon = Icons.Default.Restaurant,
                            iconTint = colors.warning,
                            title = "Refleks Menghisap (Direct Latch)",
                            badge = "Menyusu Aktif",
                            desc = "Bayi aktif mencari puting saat lapar dan dapat disusui tanpa turun dari dekapan."
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // =========================================================================
            // PILAR 05: KARTU TANDA BAHAYA & RUJUKAN CEPAT
            // =========================================================================
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = colors.errorContainer),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.errorBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(colors.error),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Tanda Bahaya Klinis",
                                style = KanguruTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.onErrorContainer
                            )
                            Text(
                                text = "Hentikan mandiri dan bawa ke Faskes / RS jika:",
                                style = KanguruTheme.typography.labelSmall,
                                fontSize = 11.sp,
                                color = colors.onErrorContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    listOf(
                        "Suhu dingin (<36,0°C) meski didekap atau demam tinggi (>37,5°C)",
                        "Berhenti bernapas (apnea) lebih dari 20 detik atau napas tersengal",
                        "Wajah, bibir, lidah, atau ujung jari tampak kebiruan (sianosis)",
                        "Bayi terkulai lemas, tidak merespons sentuhan, atau muntah terus-menerus"
                    ).forEach { dangerSign ->
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 6.dp)
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(colors.error)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = dangerSign,
                                style = KanguruTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = colors.onErrorContainer,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Ambulance Button (119)
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:119"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = colors.error
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            tint = colors.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Panggil Ambulans Gawat Darurat (119)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = colors.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // =========================================================================
            // TANYA BIDAN INTERAKTIF STRIP
            // =========================================================================
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(colors.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SupportAgent,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Ragu dengan Posisi Dekapan?",
                                style = KanguruTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "Konsultasikan foto/video dekapan Anda",
                                style = KanguruTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = colors.textSecondary
                            )
                        }
                    }

                    Button(
                        onClick = { /* Tanya Bidan Action */ },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primaryContainer,
                            contentColor = colors.primary
                        ),
                        elevation = null,
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = "Tanya Bidan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = colors.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // =========================================================================
            // STICKY BOTTOM ACTIONS: MULAI TIMER & KEMBALI KE ATAS
            // =========================================================================
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Mulai Timer PMK Kontinu
                Button(
                    onClick = onNavigateToTimer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mulai Timer PMK Kontinu",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Kembali ke Atas
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            coroutineScope.launch {
                                scrollState.animateScrollTo(0)
                            }
                        }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = colors.textSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Kembali ke Atas",
                        style = KanguruTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// =============================================================================
// SUB-COMPONENTS
// =============================================================================

@Composable
private fun EstafetSlotItem(
    time: String,
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    bgColor: Color
) {
    val colors = KanguruTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = time,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = iconTint,
            modifier = Modifier.width(90.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = KanguruTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                fontSize = 12.sp
            )
            Text(
                text = desc,
                style = KanguruTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = colors.textSecondary,
                lineHeight = 14.sp
            )
        }

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun NumberedStepItem(
    stepNumber: String,
    title: String,
    desc: String
) {
    val colors = KanguruTheme.colors

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(colors.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber,
                style = KanguruTheme.typography.labelSmall,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primary
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = KanguruTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                style = KanguruTheme.typography.labelSmall,
                fontSize = 11.sp,
                color = colors.textSecondary,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun MiniActionBox(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    desc: String,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(colors.surfaceVariant)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = KanguruTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            fontSize = 10.sp
        )
        Text(
            text = desc,
            style = KanguruTheme.typography.labelSmall,
            color = colors.textSecondary,
            fontSize = 9.sp
        )
    }
}

@Composable
private fun VitalParamRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    badge: String,
    desc: String
) {
    val colors = KanguruTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.surfaceVariant)
            .padding(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 1.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = KanguruTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    fontSize = 12.sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(colors.cardBackground)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = iconTint
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                style = KanguruTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = colors.textSecondary,
                lineHeight = 14.sp
            )
        }
    }
}
