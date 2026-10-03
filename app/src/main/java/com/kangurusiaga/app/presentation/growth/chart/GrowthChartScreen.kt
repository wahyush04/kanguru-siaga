package com.kangurusiaga.app.presentation.growth.chart

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kangurusiaga.app.core.designsystem.theme.BrandBackground
import com.kangurusiaga.app.core.designsystem.theme.BrandCardBorder
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.GrowthTipBg
import com.kangurusiaga.app.core.designsystem.theme.GrowthTipBorder
import com.kangurusiaga.app.core.designsystem.theme.GrowthTipText
import com.kangurusiaga.app.core.designsystem.theme.TextPrimary
import com.kangurusiaga.app.core.designsystem.theme.TextSecondary
import com.kangurusiaga.app.core.designsystem.theme.TextTertiary
import com.kangurusiaga.app.core.designsystem.theme.White
import com.kangurusiaga.app.data.local.fenton.FentonReferenceData
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.GrowthMeasurement
import com.kangurusiaga.app.domain.model.GrowthParameter
import com.kangurusiaga.app.presentation.growth.components.GrowthIcons
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrowthChartScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAdd: (GrowthParameter) -> Unit,
    onNavigateToAboutFenton: () -> Unit,
    viewModel: GrowthChartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val parameter = uiState.parameter
    val isBoy = uiState.baby?.gender != com.kangurusiaga.app.domain.model.Gender.FEMALE

    var measurementToDelete by remember { mutableStateOf<GrowthMeasurement?>(null) }

    if (measurementToDelete != null) {
        AlertDialog(
            onDismissRequest = { measurementToDelete = null },
            title = { Text("Hapus Data Pengukuran?", fontWeight = FontWeight.Bold) },
            text = { Text("Data pengukuran ini akan dihapus secara permanen.") },
            confirmButton = {
                Button(
                    onClick = {
                        measurementToDelete?.let { viewModel.deleteMeasurement(it.id) }
                        measurementToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Hapus", color = White)
                }
            },
            dismissButton = {
                TextButton(onClick = { measurementToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFFCFCFD)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = GrowthIcons.Back,
                        contentDescription = "Kembali",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = parameter.displayName,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.size(36.dp))
            }

            // Segmented Tabs: Grafik vs Data
            TabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = White,
                contentColor = BrandPink,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab]),
                        height = 2.5.dp,
                        color = BrandPink
                    )
                },
                divider = {
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                }
            ) {
                Tab(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    text = {
                        Text(
                            text = "Grafik",
                            fontSize = 14.sp,
                            fontWeight = if (uiState.selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (uiState.selectedTab == 0) BrandPink else TextTertiary
                        )
                    }
                )
                Tab(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    text = {
                        Text(
                            text = "Data",
                            fontSize = 14.sp,
                            fontWeight = if (uiState.selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (uiState.selectedTab == 1) BrandPink else TextTertiary
                        )
                    }
                )
            }

            // Tab Content
            if (uiState.selectedTab == 0) {
                // Grafik Tab Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    // Filter Selector & Help Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(White)
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Usia Gestasi Pasca-Menstruasi (PMA)",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                            Icon(
                                imageVector = GrowthIcons.ArrowDown,
                                contentDescription = null,
                                tint = Color(0xFF6B7280),
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        IconButton(
                            onClick = onNavigateToAboutFenton,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = GrowthIcons.Help,
                                contentDescription = "Informasi Usia Koreksi",
                                tint = TextTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Current Metrics Card
                    val latest = uiState.latestMeasurement
                    val baby = uiState.baby
                    val percentileClassification = remember(latest, parameter, isBoy) {
                        if (latest != null) {
                            val gender = if (isBoy) Gender.MALE else Gender.FEMALE
                            FentonReferenceData.evaluatePercentile(parameter, gender, latest.pmaWeeks, latest.value)
                        } else null
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(White)
                            .border(1.dp, BrandCardBorder, RoundedCornerShape(18.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        // Left: Current Value & Delta from birth
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = parameter.displayName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )
                            Text(
                                text = "Saat ini",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal,
                                color = TextTertiary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.Bottom) {
                                val valueStr = if (latest != null) {
                                    String.format(Locale("id", "ID"), "%.1f", latest.value)
                                } else "--"
                                Text(
                                    text = valueStr,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = parameter.unit,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(bottom = 3.dp)
                                )
                            }

                            // Delta from birth pill
                            if (latest != null && baby != null) {
                                val (deltaStr, deltaPositive) = when (parameter) {
                                    GrowthParameter.WEIGHT -> {
                                        val birthKg = baby.birthWeightGram / 1000f
                                        val diff = latest.value - birthKg
                                        val grams = (diff * 1000).toInt()
                                        val str = if (diff >= 0) "+$grams g dari lahir" else "$grams g dari lahir"
                                        Pair(str, diff >= 0)
                                    }
                                    GrowthParameter.LENGTH -> {
                                        val diff = latest.value - baby.birthLengthCm
                                        val str = if (diff >= 0) "+${String.format(Locale("id", "ID"), "%.1f", diff)} cm dari lahir"
                                        else "${String.format(Locale("id", "ID"), "%.1f", diff)} cm dari lahir"
                                        Pair(str, diff >= 0)
                                    }
                                    GrowthParameter.HEAD_CIRCUMFERENCE -> {
                                        val diff = latest.value - baby.birthHeadCircumferenceCm
                                        val str = if (diff >= 0) "+${String.format(Locale("id", "ID"), "%.1f", diff)} cm dari lahir"
                                        else "${String.format(Locale("id", "ID"), "%.1f", diff)} cm dari lahir"
                                        Pair(str, diff >= 0)
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (deltaPositive) Color(0xFFEAF7EE) else Color(0xFFFEECEB))
                                        .border(
                                            1.dp,
                                            if (deltaPositive) Color(0xFFD1F2DC) else Color(0xFFFED7D7),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = (if (deltaPositive) "↑ " else "") + deltaStr,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (deltaPositive) Color(0xFF16A34A) else Color(0xFFDC2626)
                                    )
                                }
                            }
                        }

                        // Right: Percentile Badge
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "Persentil",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextTertiary
                            )
                            Text(
                                text = percentileClassification?.percentileBadge ?: latest?.percentileBadge ?: "P50",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = BrandPink
                            )
                            Text(
                                text = percentileClassification?.statusText ?: "Normal",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF16A34A)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val babyName = baby?.name ?: "si kecil"

                    // Fenton Growth Chart Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(White)
                            .border(1.dp, BrandCardBorder, RoundedCornerShape(18.dp))
                            .padding(14.dp)
                    ) {
                        FentonChart(
                            parameter = parameter,
                            isBoy = isBoy,
                            measurements = uiState.measurementsAsc,
                            babyName = babyName
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Clinical / Developmental Insight Card
                    val pName = percentileClassification?.percentileBadge ?: "P50"
                    val pCat = percentileClassification?.statusText?.lowercase(Locale.ROOT) ?: "normal"

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(GrowthTipBg)
                            .border(1.dp, GrowthTipBorder, RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEF3C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = GrowthIcons.Lightbulb,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "${parameter.displayName} $babyName berada pada persentil $pName ($pCat) sesuai grafik Fenton.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF4A3B24),
                            lineHeight = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Primary Action CTA Button: + Tambah Data [Parameter]
                    OutlinedButton(
                        onClick = { onNavigateToAdd(parameter) },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = White,
                            contentColor = BrandPink
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, BrandPink),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "+ Tambah Data ${parameter.displayName}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandPink
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else {
                // Data Tab Content (History Table)
                val dateFormatter = remember { SimpleDateFormat("d MMM yyyy", Locale("id", "ID")) }

                if (uiState.measurementsDesc.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Belum ada riwayat pengukuran",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Mulai tambahkan data pengukuran untuk melihat riwayat pertumbuhan si kecil.",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Normal,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onNavigateToAdd(parameter) },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPink),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("+ Tambah Data", color = White)
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(White)
                            .padding(horizontal = 20.dp)
                    ) {
                        items(uiState.measurementsDesc, key = { it.id }) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { measurementToDelete = item }
                                    .padding(vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Date
                                Text(
                                    text = dateFormatter.format(item.measurementDateEpochMillis),
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary,
                                    modifier = Modifier.width(100.dp)
                                )

                                // Age in weeks
                                val weeksInt = item.chronologicalAgeWeeks.toInt()
                                val ageText = if (weeksInt > 0) "$weeksInt minggu" else "${(item.chronologicalAgeWeeks * 7).toInt()} hari"
                                Text(
                                    text = ageText,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = TextSecondary,
                                    modifier = Modifier.weight(1f)
                                )

                                // Value
                                val valText = "${String.format(Locale("id", "ID"), "%.1f", item.value)} ${item.unit}"
                                Text(
                                    text = valText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(end = 8.dp)
                                )

                                // Percentile Pill
                                val isNormal = item.percentileBadge in listOf("P25", "P50", "P75")
                                val pillBg = if (isNormal) Color(0xFFEAF7EE) else Color(0xFFFEECEB)
                                val pillText = if (isNormal) Color(0xFF16A34A) else Color(0xFFEF4444)

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(pillBg)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = item.percentileBadge ?: "P50",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = pillText
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Icon(
                                    imageVector = GrowthIcons.ChevronRight,
                                    contentDescription = null,
                                    tint = Color(0xFFCBD5E1),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onNavigateToAdd(parameter) },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPink),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text("+ Tambah Data ${parameter.displayName}", fontWeight = FontWeight.Bold, color = White)
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}
