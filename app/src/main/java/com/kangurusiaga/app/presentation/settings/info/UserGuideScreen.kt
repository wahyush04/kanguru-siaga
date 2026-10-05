package com.kangurusiaga.app.presentation.settings.info

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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kangurusiaga.app.core.designsystem.theme.BrandBackground
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.White

data class GuideStep(
    val stepNumber: Int,
    val text: String
)

data class GuideFeature(
    val id: String,
    val tag: String,
    val title: String,
    val icon: ImageVector,
    val steps: List<GuideStep>
)

@Composable
fun UserGuideScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val expandedStates = remember {
        mutableStateMapOf<String, Boolean>().apply {
            put("pmk", true) // Default open first card
        }
    }

    val features = remember {
        listOf(
            GuideFeature(
                id = "pmk",
                tag = "METODE KANGGURU",
                title = "Perawatan Metode Kanguru (PMK)",
                icon = Icons.Default.VolunteerActivism,
                steps = listOf(
                    GuideStep(1, "Siapkan baju berkancing depan atau kain gendong elastis khusus PMK yang bersih dan nyaman."),
                    GuideStep(2, "Buka menu \"Timer PMK\" di bilah navigasi utama lalu tekan tombol Mulai PMK."),
                    GuideStep(3, "Posisikan bayi tegak (posisi kodok) di antara payudara ibu/ayah, kulit langsung menempel pada kulit (skin-to-skin)."),
                    GuideStep(4, "Pantau pernapasan, pastikan hidung tidak tertutup, dan evaluasi kenaikan suhu tubuh secara berkala.")
                )
            ),
            GuideFeature(
                id = "feeding",
                tag = "NUTRISI & ASI",
                title = "Alarm & Jadwal Minum ASI (OGT/NGT)",
                icon = Icons.Default.Alarm,
                steps = listOf(
                    GuideStep(1, "Akses menu \"Alarm\" pada bilah navigasi bawah."),
                    GuideStep(2, "Tambahkan jadwal pemberian minum rutin sesuai anjuran dokter (biasanya per 2–3 jam)."),
                    GuideStep(3, "Sesuaikan takaran volume ASI (ml) dan metode pemberian (sendok, pipet, cangkir, atau selang sonde OGT/NGT)."),
                    GuideStep(4, "Konfirmasi setiap kali pemberian minum selesai agar tercatat dalam ringkasan harian.")
                )
            ),
            GuideFeature(
                id = "growth",
                tag = "KURVA FENTON",
                title = "Pemantauan Pertumbuhan & Kurva Fenton",
                icon = Icons.Default.ShowChart,
                steps = listOf(
                    GuideStep(1, "Buka fitur \"Pertumbuhan Bayi\" dari beranda."),
                    GuideStep(2, "Masukkan data penimbangan rutin (Berat Badan, Panjang Badan, dan Lingkar Kepala)."),
                    GuideStep(3, "Grafik akan otomatis memplot capaian bayi terhadap Kurva Pertumbuhan Fenton berdasarkan Usia Pasca Menstruasi (PMA)."),
                    GuideStep(4, "Perhatikan tren kenaikan garis kurva untuk memastikan proses Catch-up Growth berjalan optimal.")
                )
            ),
            GuideFeature(
                id = "emergency",
                tag = "KESELAMATAN BAYI",
                title = "Pengenalan Tanda Bahaya Bayi BBLR",
                icon = Icons.Default.Warning,
                steps = listOf(
                    GuideStep(1, "Pelajari modul \"Tanda Kegawatan\" di halaman beranda atau edukasi."),
                    GuideStep(2, "Kenali gejala bahaya: napas cepat (>60x/menit), napas berhenti (apnea >20 detik), tubuh membiru, kejang, atau demam/hipotermia."),
                    GuideStep(3, "Jika mendapati satu tanda bahaya, segera gunakan tombol darurat untuk menghubungi SPGDT 119 atau bawa ke IGD terdekat.")
                )
            ),
            GuideFeature(
                id = "education",
                tag = "INFORMASI TERPERCAYA",
                title = "Pusat Edukasi & Panduan Praktis",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                steps = listOf(
                    GuideStep(1, "Masuk ke tab \"Edukasi\" di navigasi bawah."),
                    GuideStep(2, "Telusuri berbagai modul terverifikasi tentang perawatan BBLR di rumah, teknik menyusui, dan kebersihan tali pusat."),
                    GuideStep(3, "Simpan artikel favorit untuk dibaca kembali secara luring (offline) kapan saja.")
                )
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
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color(0xFF1E293B)
                    )
                }

                Text(
                    text = "Panduan Penggunaan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Hero Banner
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = White,
                    border = BorderStroke(1.dp, Color(0xFFF1ECE6)),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = Color(0xFFFFF1F2),
                            border = BorderStroke(1.dp, Color(0xFFFFCCD5))
                        ) {
                            Text(
                                text = "PANDUAN LENGKAP APLIKASI",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandPink,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Cara Mengoptimalkan Kanguru Siaga",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Panduan praktis langkah demi langkah untuk membantu Ayah dan Bunda memanfaatkan seluruh fitur pemantauan BBLR dengan mudah, aman, dan tepat.",
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Section Title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Petunjuk Fitur Utama",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Color(0xFFFFF1F2),
                        border = BorderStroke(1.dp, Color(0xFFFFCCD5))
                    ) {
                        Text(
                            text = "5 Fitur Inti",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandPink,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                // Feature Accordion List
                features.forEach { feature ->
                    val isExpanded = expandedStates[feature.id] == true
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = White,
                        border = BorderStroke(1.dp, Color(0xFFF1ECE6)),
                        shadowElevation = 1.dp
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expandedStates[feature.id] = !isExpanded }
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color(0xFFFFF1F2), RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = feature.icon,
                                            contentDescription = null,
                                            tint = BrandPink,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = feature.tag,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandPink,
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            text = feature.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E293B)
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8)
                                )
                            }

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
                                        color = Color(0xFFF8FAFC),
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )

                                    feature.steps.forEach { step ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(22.dp)
                                                    .background(Color(0xFFFFF1F2), CircleShape)
                                                    .border(1.dp, Color(0xFFFFCCD5), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = step.stepNumber.toString(),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = BrandPink
                                                )
                                            }

                                            Text(
                                                text = step.text,
                                                fontSize = 12.sp,
                                                lineHeight = 18.sp,
                                                color = Color(0xFF475569)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
