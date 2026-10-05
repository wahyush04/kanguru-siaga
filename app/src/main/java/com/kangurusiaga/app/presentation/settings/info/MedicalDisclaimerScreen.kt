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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kangurusiaga.app.core.designsystem.theme.BrandBackground
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.TextPrimary
import com.kangurusiaga.app.core.designsystem.theme.TextSecondary
import com.kangurusiaga.app.core.designsystem.theme.White

@Composable
fun MedicalDisclaimerScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

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
                        tint = TextPrimary
                    )
                }

                Text(
                    text = "Disclaimer Medis",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
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
                // Hero Card: Bukan Pengganti Konsultasi Medis
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.surface,
                    border = BorderStroke(1.dp, com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.cardBorder),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.primaryContainer,
                            border = BorderStroke(1.dp, com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.primaryBorder)
                        ) {
                            Text(
                                text = "PEMBERITAHUAN PENTING & BATASAN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandPink,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Bukan Pengganti Konsultasi Medis Langsung",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Aplikasi Kanguru Siaga dirancang semata-mata sebagai media edukasi, pemantauan harian, dan alat bantu pencatatan mandiri bagi orang tua bayi BBLR (Bayi Berat Lahir Rendah) di rumah.",
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = TextSecondary
                        )

                        HorizontalDivider(
                            color = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.divider,
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        Text(
                            text = "Aplikasi ini TIDAK MENGGANTIKAN anjuran, pemeriksaan fisik langsung, diagnosis, ataupun pengawasan medis dari dokter spesialis anak (Sp.A), dokter umum, bidan, maupun tenaga perinatologi di fasilitas kesehatan.",
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Emergency Alert Section (Red/Coral card)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.errorContainer,
                    border = BorderStroke(1.dp, com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.errorBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.surfaceElevated, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = BrandPink,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = "Situasi Kegawatan & Darurat Medis",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.errorText
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Jika bayi mengalami sesak napas berat, kejang, henti napas (apnea > 20 detik), kulit kebiruan (sianosis), atau suhu ekstrem yang tidak merespons penghangatan, JANGAN MENUNGGU arahan aplikasi. Segera bawa bayi ke Instalasi Gawat Darurat (IGD) Rumah Sakit terdekat!",
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.errorText
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Call 119 button
                        Surface(
                            modifier = Modifier.clickable {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:119")
                                }
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.surface,
                            border = BorderStroke(1.dp, com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.errorBorder),
                            shadowElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = null,
                                    tint = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.errorText,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Kontak Darurat Neonatus / SPGDT: 119 / 112",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.errorText
                                )
                            }
                        }
                    }
                }

                // Feature Boundaries Section
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.surface,
                    border = BorderStroke(1.dp, com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.cardBorder),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.warningContainer, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.warningText,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = "Batasan Penggunaan Fitur Aplikasi",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Sub-item 1
                        DisclaimerItem(
                            title = "Grafik Pertumbuhan Fenton",
                            description = "Kalkulasi persentil berat, panjang badan, dan lingkar kepala adalah perkiraan tren pemantauan pertumbuhan berdasarkan Usia Pasca Menstruasi (PMA). Hasil ini bukan diagnosis klinis definitif atas gangguan pertumbuhan tanpa konfirmasi dokter spesialis anak."
                        )

                        HorizontalDivider(
                            color = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.divider,
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )

                        // Sub-item 2
                        DisclaimerItem(
                            title = "Timer & Target Perawatan Metode Kanguru (PMK)",
                            description = "Durasi kontak kulit-ke-kulit (skin-to-skin) harus selalu mengutamakan kestabilan pernapasan, warna kulit, dan kenyamanan bayi. Jika bayi tampak rewel berat, merintih, atau lemas saat PMK, segera hentikan sesi dan evaluasi kondisinya."
                        )

                        HorizontalDivider(
                            color = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.divider,
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )

                        // Sub-item 3
                        DisclaimerItem(
                            title = "Jadwal & Estimasi Kebutuhan ASI / Sonde",
                            description = "Jadwal alarm dan anjuran frekuensi minum adalah panduan umum. Kebutuhan kalori harian serta metode pemberian (sendok, pipet, cangkir, atau selang OGT/NGT) harus selalu merujuk pada instruksi dokter anak yang merawat si kecil."
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DisclaimerItem(
    title: String,
    description: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
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
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            fontSize = 12.sp,
            lineHeight = 17.sp,
            color = TextSecondary,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}
