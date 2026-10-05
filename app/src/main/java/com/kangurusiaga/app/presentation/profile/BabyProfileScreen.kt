package com.kangurusiaga.app.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kangurusiaga.app.core.designsystem.theme.BrandBackground
import com.kangurusiaga.app.core.designsystem.theme.BrandDarkPink
import com.kangurusiaga.app.core.designsystem.theme.BrandLightPink
import com.kangurusiaga.app.core.designsystem.theme.BrandPeach
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.BrandSoftBlue
import com.kangurusiaga.app.core.designsystem.theme.BrandSoftGreen
import com.kangurusiaga.app.core.designsystem.theme.BrandTextGreen
import com.kangurusiaga.app.core.designsystem.theme.White
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.presentation.growth.components.GrowthIcons
import com.kangurusiaga.app.presentation.home.HomeBottomBar
import com.kangurusiaga.app.presentation.home.HomeTab
import java.util.Locale

@Composable
fun BabyProfileRoute(
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToPmk: () -> Unit,
    onNavigateToEducation: () -> Unit,
    onNavigateToAlarm: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BabyProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BabyProfileScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToHome = onNavigateToHome,
        onNavigateToPmk = onNavigateToPmk,
        onNavigateToEducation = onNavigateToEducation,
        onNavigateToAlarm = onNavigateToAlarm,
        modifier = modifier
    )
}

@Composable
fun BabyProfileScreen(
    uiState: BabyProfileUiState,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToPmk: () -> Unit,
    onNavigateToEducation: () -> Unit,
    onNavigateToAlarm: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandBackground,
        bottomBar = {
            HomeBottomBar(
                currentTab = HomeTab.PROFIL,
                onTabSelected = { tab ->
                    when (tab) {
                        HomeTab.BERANDA -> onNavigateToHome()
                        HomeTab.PMK -> onNavigateToPmk()
                        HomeTab.EDUKASI -> onNavigateToEducation()
                        HomeTab.ALARM -> onNavigateToAlarm()
                        HomeTab.PROFIL -> { /* already on profile */ }
                    }
                }
            )
        }
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color(0xFF1E293B)
                    )
                }

                Text(
                    text = "Profil Bayi",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Pengaturan",
                        tint = Color(0xFF1E293B)
                    )
                }
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrandPink)
                }
            } else if (uiState.baby == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Data profil bayi belum tersedia.",
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                val baby = uiState.baby
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar & Core Identity Section
                    Box(
                        modifier = Modifier
                            .size(112.dp)
                            .shadow(6.dp, CircleShape)
                            .border(3.dp, White, CircleShape)
                            .clip(CircleShape)
                            .background(BrandPeach),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!baby.photoUri.isNullOrBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(baby.photoUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Foto ${baby.name}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = BrandPink,
                                modifier = Modifier.size(56.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = baby.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Lahir: ${uiState.formattedBirthDate}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Pill Chip Status
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Color(0xFFFFF1F2),
                        border = BorderStroke(1.dp, Color(0xFFFFCCD5))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(BrandPink, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            val statusTag = if (uiState.isBblr) "BBLR" else "Normal"
                            Text(
                                text = "Usia: ${uiState.chronologicalAgeWeeks} Minggu (Kronologis) • $statusTag",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BrandPink
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Growth Metrics Cards Row (Berat, Panjang, Lingkar Kepala)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Berat Badan Card
                        GrowthMetricCard(
                            modifier = Modifier.weight(1f),
                            bgColor = Color(0xFFFFF1F2),
                            borderColor = Color(0xFFFFE4E6),
                            icon = {
                                Icon(
                                    imageVector = GrowthIcons.Weight,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = BrandPink
                                )
                            },
                            value = "${uiState.weightMetric.valueFormatted} ${uiState.weightMetric.unit}",
                            percentile = uiState.weightMetric.percentileText,
                            status = uiState.weightMetric.statusText
                        )

                        // Panjang Badan Card
                        GrowthMetricCard(
                            modifier = Modifier.weight(1f),
                            bgColor = Color(0xFFF0FDF4),
                            borderColor = Color(0xFFDCFCE7),
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.SquareFoot,
                                    contentDescription = null,
                                    tint = Color(0xFF16A34A),
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            value = "${uiState.lengthMetric.valueFormatted} ${uiState.lengthMetric.unit}",
                            percentile = uiState.lengthMetric.percentileText,
                            status = uiState.lengthMetric.statusText
                        )

                        // Lingkar Kepala Card
                        GrowthMetricCard(
                            modifier = Modifier.weight(1f),
                            bgColor = Color(0xFFF0F9FF),
                            borderColor = Color(0xFFE0F2FE),
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.ChildCare,
                                    contentDescription = null,
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            value = "${uiState.headMetric.valueFormatted} ${uiState.headMetric.unit}",
                            percentile = uiState.headMetric.percentileText,
                            status = uiState.headMetric.statusText
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Read-Only Baby Info Section: Data Kelahiran & Biologis
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = White,
                        border = BorderStroke(1.dp, Color(0xFFF1ECE6)),
                        shadowElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = BrandPink,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "DATA KELAHIRAN & BIOLOGIS",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A),
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Row 1: Tanggal Lahir
                            InfoDataRow(
                                label = "Tanggal Lahir",
                                value = uiState.formattedBirthDate
                            )

                            HorizontalDivider(color = Color(0xFFF8FAFC), thickness = 1.dp)

                            // Row 2: Jenis Kelamin
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Jenis Kelamin",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF64748B)
                                )
                                val isFemale = baby.gender == Gender.FEMALE
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = if (isFemale) Color(0xFFFFF1F2) else Color(0xFFEBF6FF),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isFemale) Color(0xFFFFCCD5) else Color(0xFFBFDBFE)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isFemale) Icons.Default.Female else Icons.Default.Male,
                                            contentDescription = null,
                                            tint = if (isFemale) BrandPink else Color(0xFF2563EB),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = if (isFemale) "Perempuan" else "Laki-laki",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isFemale) BrandPink else Color(0xFF2563EB)
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = Color(0xFFF8FAFC), thickness = 1.dp)

                            // Row 3: Berat Lahir
                            val formattedBirthWeight = String.format(Locale("id", "ID"), "%,d", baby.birthWeightGram).replace(',', '.')
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Berat Lahir",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF64748B)
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "$formattedBirthWeight gram",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                    if (uiState.isBblr) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFFFFFBEB),
                                            border = BorderStroke(1.dp, Color(0xFFFDE68A))
                                        ) {
                                            Text(
                                                text = "BBLR",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFD97706),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(color = Color(0xFFF8FAFC), thickness = 1.dp)

                            // Row 4: Usia Gestasi saat Lahir
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Usia Gestasi saat Lahir",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF64748B)
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "${baby.gestationalAgeWeeks} Minggu",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (uiState.isPremature) Color(0xFFFFF1F2) else Color(0xFFF0FDF4),
                                        border = BorderStroke(
                                            1.dp,
                                            if (uiState.isPremature) Color(0xFFFFCCD5) else Color(0xFFDCFCE7)
                                        )
                                    ) {
                                        Text(
                                            text = if (uiState.isPremature) "Prematur" else "Aterm",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (uiState.isPremature) BrandPink else Color(0xFF16A34A),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = Color(0xFFF8FAFC), thickness = 1.dp)

                            // Row 5: Status Pertumbuhan
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Status Pertumbuhan",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF64748B)
                                )
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = uiState.growthStatusSummary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0D9488)
                                    )
                                    Text(
                                        text = "Kurva Fenton",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun InfoDataRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF64748B)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
    }
}

@Composable
private fun GrowthMetricCard(
    modifier: Modifier = Modifier,
    bgColor: Color,
    borderColor: Color,
    icon: @Composable () -> Unit,
    value: String,
    percentile: String,
    status: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(White, RoundedCornerShape(10.dp))
                    .shadow(1.dp, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = percentile,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF0D9488),
                maxLines = 1
            )

            Text(
                text = status,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0D9488),
                maxLines = 1
            )
        }
    }
}
