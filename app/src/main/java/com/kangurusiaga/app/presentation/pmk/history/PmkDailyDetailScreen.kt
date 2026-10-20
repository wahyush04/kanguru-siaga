package com.kangurusiaga.app.presentation.pmk.history

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.domain.model.PmkCaregiver
import com.kangurusiaga.app.domain.model.PmkSegment
import com.kangurusiaga.app.domain.model.PmkSegmentStatus
import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.usecase.DailyTimeline
import com.kangurusiaga.app.presentation.pmk.components.PmkTimeline24HourBar
import com.kangurusiaga.app.presentation.pmk.statistics.PmkStatisticsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Screen ID: 1503587457644ee482323e3e0c3af30c
 * Title: Kanguru Siaga - Detail Riwayat PMK Harian
 */
@Composable
fun PmkDailyDetailRoute(
    dateKey: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PmkStatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val dailyGroups = remember(uiState.sessions) {
        groupSessionsByDay(uiState.sessions)
    }

    val selectedGroup = remember(dailyGroups, dateKey) {
        dailyGroups.firstOrNull { it.dateKey == dateKey }
            ?: dailyGroups.firstOrNull()
    }

    PmkDailyDetailScreen(
        dailyGroup = selectedGroup,
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@Composable
fun PmkDailyDetailScreen(
    dailyGroup: DailyGroup?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val context = LocalContext.current

    val dateFormatted = dailyGroup?.dateFormatted ?: "Detail Riwayat PMK"
    val timeline = dailyGroup?.timeline
    val totalActiveMinutes = timeline?.totalActiveMinutes ?: 0
    val totalActiveHours = totalActiveMinutes / 60
    val totalActiveRemMinutes = totalActiveMinutes % 60
    val isTargetMet = totalActiveMinutes >= (8 * 60)

    val sessions = dailyGroup?.sessions ?: emptyList()
    val segments = dailyGroup?.segments ?: emptyList()

    val chronologicalSegments = remember(segments, sessions) {
        if (segments.isNotEmpty()) {
            segments.sortedBy { it.startTimeEpoch }
        } else {
            sessions.map { s ->
                PmkSegment(
                    id = s.id,
                    sessionId = s.id,
                    caregiver = s.currentCaregiver,
                    startTimeEpoch = s.startTimeEpoch,
                    endTimeEpoch = s.endTimeEpoch,
                    durationMinutes = s.activeDurationMinutes,
                    status = PmkSegmentStatus.ACTIVE,
                    babyTemperature = s.babyTemperature,
                    babyResponse = s.babyResponse,
                    notes = s.notes
                )
            }.sortedBy { it.startTimeEpoch }
        }
    }

    val activeCount = chronologicalSegments.count { it.status == PmkSegmentStatus.ACTIVE }.coerceAtLeast(sessions.size)
    val pauseCount = chronologicalSegments.count { it.status == PmkSegmentStatus.PAUSED }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = {
            Surface(
                color = colors.cardBackground,
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, colors.cardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(colors.surfaceElevated)
                            .border(1.dp, colors.cardBorder, CircleShape)
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
                            text = "Detail Riwayat PMK",
                            style = KanguruTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = dateFormatted,
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = colors.textSecondary
                        )
                    }

                    IconButton(
                        onClick = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Laporan PMK Kanguru Siaga ($dateFormatted): Total kontak $totalActiveHours jam $totalActiveRemMinutes menit (${if (isTargetMet) "Target Terpenuhi ✓" else "Perlu Ditingkatkan"})."
                                )
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Bagikan Laporan PMK"))
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(colors.surfaceElevated)
                            .border(1.dp, colors.cardBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Bagikan Laporan",
                            tint = colors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // =========================================================================
            // CARD 1: CAPAIAN HARIAN (Target Minimal IDAI & WHO 8 Jam)
            // =========================================================================
            item {
                DailyTargetCard(
                    totalActiveHours = totalActiveHours,
                    totalActiveMinutes = totalActiveRemMinutes,
                    isTargetMet = isTargetMet
                )
            }

            // =========================================================================
            // CARD 2: TIMELINE RITME 24 JAM
            // =========================================================================
            item {
                PmkTimeline24HourSection(
                    timeline = timeline,
                    totalActiveHours = totalActiveHours,
                    totalActiveMinutes = totalActiveRemMinutes
                )
            }

            // =========================================================================
            // CARD 3: DISTRIBUSI PENGASUH (Estafet PMK Tanpa Putus)
            // =========================================================================
            item {
                CaregiverDistributionCard(segments = chronologicalSegments)
            }

            // =========================================================================
            // CARD 4: PEMANTAUAN SUHU BAYI OPTIMAL
            // =========================================================================
            item {
                OptimalTemperatureBanner(sessions = sessions)
            }

            // =========================================================================
            // CARD 5: RINCIAN SESI HARI INI (Log Kronologis Sesi & Jeda)
            // =========================================================================
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Rincian Sesi Hari Ini",
                            style = KanguruTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Total $activeCount sesi kontak kulit aktif dan $pauseCount jeda singkat",
                            style = KanguruTheme.typography.labelSmall,
                            color = colors.textSecondary
                        )
                    }
                    Text(
                        text = "$activeCount Sesi",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                }
            }

            // Chronological active and pause cards
            if (chronologicalSegments.isEmpty()) {
                item {
                    Text(
                        text = "Belum ada rincian sesi kontak pada hari ini.",
                        style = KanguruTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                    )
                }
            } else {
                var currentActiveIndex = 0
                items(chronologicalSegments, key = { it.id.toString() + "_" + it.startTimeEpoch }) { segment ->
                    if (segment.status == PmkSegmentStatus.ACTIVE) {
                        currentActiveIndex++
                        ActiveSessionCard(
                            sessionNumber = currentActiveIndex,
                            segment = segment
                        )
                    } else {
                        PauseSegmentCard(
                            segment = segment
                        )
                    }
                }
            }

            // =========================================================================
            // CARD 6: CATATAN EVALUASI DOKTER ANAK
            // =========================================================================
            item {
                DoctorEvaluationNoteCard(
                    totalActiveHours = totalActiveHours,
                    totalActiveMinutes = totalActiveRemMinutes,
                    isTargetMet = isTargetMet
                )
            }

            // =========================================================================
            // TOMBOL AKSI BAWAH: Kembali ke Daftar Riwayat PMK
            // =========================================================================
            item {
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "← Kembali ke Daftar Riwayat PMK",
                        style = KanguruTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.onPrimary
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

// =============================================================================
// SUB-COMPONENTS
// =============================================================================

@Composable
private fun DailyTargetCard(
    totalActiveHours: Int,
    totalActiveMinutes: Int,
    isTargetMet: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val targetMinutes = 8 * 60
    val activeMinutes = (totalActiveHours * 60) + totalActiveMinutes
    val percentOfTarget = ((activeMinutes.toFloat() / targetMinutes.toFloat()) * 100).toInt()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        border = BorderStroke(1.dp, colors.cardBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Capaian Harian + Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(colors.pmkDoctorNoteBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "CAPAIAN HARIAN",
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textTertiary
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${totalActiveHours}j ${totalActiveMinutes}m",
                                style = KanguruTheme.typography.titleLarge,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.ExtraBold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = " / 24j",
                                style = KanguruTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textSecondary,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }
                }

                // Badge Status
                if (isTargetMet) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.pmkDoctorNoteBg)
                            .border(1.dp, colors.pmkDoctorNoteBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "• Target Terpenuhi ✓",
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.pmkTargetMetMarker
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.pmkPauseDashedBg)
                            .border(1.dp, colors.pmkPauseDashedBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "• Belum Optimal",
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.pmkCaregiverPendamping
                        )
                    }
                }
            }

            // Target Minimal IDAI & WHO (8 Jam) Progress
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Target Minimal IDAI & WHO (8 Jam)",
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textSecondary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isTargetMet) colors.pmkDoctorNoteBg else colors.pmkPauseDashedBg)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "$percentOfTarget%",
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isTargetMet) colors.pmkTargetMetMarker else colors.pmkCaregiverPendamping
                            )
                        }
                    }

                    Text(
                        text = "${totalActiveHours}j ${totalActiveMinutes}m",
                        style = KanguruTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }

                // 24-Hour Progress Bar with Scale Marks
                val activeFraction = (activeMinutes.toFloat() / (24 * 60f)).coerceIn(0f, 1f)
                val target8hFraction = 8f / 24f
                val target20hFraction = 20f / 24f

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(colors.surfaceVariant)
                ) {
                    val totalWidth = maxWidth

                    // Active Fill
                    if (activeFraction > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(activeFraction)
                                .fillMaxHeight()
                                .background(if (isTargetMet) colors.pmkTargetMetMarker else colors.pmkCaregiverPendamping)
                        )
                    }

                    // 8-Hour Marker Line
                    val marker8X = totalWidth * target8hFraction
                    Box(
                        modifier = Modifier
                            .padding(start = (marker8X - 1.dp).coerceAtLeast(0.dp))
                            .width(2.dp)
                            .fillMaxHeight()
                            .background(
                                if (activeFraction >= target8hFraction) colors.cardBackground.copy(alpha = 0.85f)
                                else colors.pmkTargetUnmetMarker
                            )
                    )

                    // 20-Hour Marker Line
                    val marker20X = totalWidth * target20hFraction
                    Box(
                        modifier = Modifier
                            .padding(start = (marker20X - 1.dp).coerceAtLeast(0.dp))
                            .width(2.dp)
                            .fillMaxHeight()
                            .background(
                                if (activeFraction >= target20hFraction) colors.cardBackground.copy(alpha = 0.85f)
                                else colors.cardBorder
                            )
                    )
                }

                // Axis scale
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "0 Jam",
                        style = KanguruTheme.typography.labelSmall,
                        color = colors.textTertiary
                    )
                    Text(
                        text = "8 Jam (Target Dasar ✓)",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isTargetMet) colors.primary else colors.pmkCaregiverPendamping
                    )
                    Text(
                        text = "20 Jam (Ideal)",
                        style = KanguruTheme.typography.labelSmall,
                        color = colors.textTertiary
                    )
                    Text(
                        text = "24 Jam",
                        style = KanguruTheme.typography.labelSmall,
                        color = colors.textTertiary
                    )
                }
            }
        }
    }
}

@Composable
private fun PmkTimeline24HourSection(
    timeline: DailyTimeline?,
    totalActiveHours: Int,
    totalActiveMinutes: Int,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        border = BorderStroke(1.dp, colors.cardBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Timeline Ritme 24 Jam",
                        style = KanguruTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Visualisasi kontak kulit aktif vs jeda perawatan",
                        style = KanguruTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.pmkDoctorNoteBg)
                        .border(1.dp, colors.pmkDoctorNoteBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${totalActiveHours}j ${totalActiveMinutes}m / 24j",
                        style = KanguruTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                }
            }

            // 24-Hour Timeline Bar Component
            PmkTimeline24HourBar(
                timeline = timeline,
                showLegend = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CaregiverDistributionCard(
    segments: List<PmkSegment>,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    // Compute duration per caregiver from active segments
    var ibuMinutes = 0
    var ayahMinutes = 0
    var pendampingMinutes = 0

    segments.filter { it.status == PmkSegmentStatus.ACTIVE }.forEach { segment ->
        val duration = segment.durationMinutes
        when (segment.caregiver) {
            PmkCaregiver.IBU -> ibuMinutes += duration
            PmkCaregiver.AYAH -> ayahMinutes += duration
            PmkCaregiver.PENDAMPING -> pendampingMinutes += duration
        }
    }

    val totalMin = (ibuMinutes + ayahMinutes + pendampingMinutes).coerceAtLeast(1)
    val ibuPct = (ibuMinutes * 100) / totalMin
    val ayahPct = (ayahMinutes * 100) / totalMin
    val pendampingPct = (pendampingMinutes * 100) / totalMin

    val activeCount = listOf(ibuMinutes, ayahMinutes, pendampingMinutes).count { it > 0 }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        border = BorderStroke(1.dp, colors.cardBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Distribusi Pengasuh",
                        style = KanguruTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Kontribusi estafet PMK tanpa putus",
                        style = KanguruTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.successContainer)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "$activeCount Pengasuh Terlibat",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSuccessContainer
                    )
                }
            }

            // Multi-segment stacked bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(colors.surfaceVariant)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    if (ibuMinutes > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(ibuPct.toFloat().coerceAtLeast(1f))
                                .background(colors.pmkCaregiverIbu)
                        )
                    }
                    if (ayahMinutes > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(ayahPct.toFloat().coerceAtLeast(1f))
                                .background(colors.pmkCaregiverAyah)
                        )
                    }
                    if (pendampingMinutes > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(pendampingPct.toFloat().coerceAtLeast(1f))
                                .background(colors.pmkCaregiverPendamping)
                        )
                    }
                }
            }

            // 3 Column Grid Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Ibu
                CaregiverMiniCard(
                    title = "Ibu",
                    dotColor = colors.pmkCaregiverIbu,
                    hours = ibuMinutes / 60,
                    minutes = ibuMinutes % 60,
                    percent = ibuPct,
                    bgColor = colors.pmkCaregiverIbuBg,
                    borderColor = colors.pmkCaregiverIbuBorder,
                    titleColor = colors.pmkCaregiverIbu,
                    modifier = Modifier.weight(1f)
                )

                // Ayah
                CaregiverMiniCard(
                    title = "Ayah",
                    dotColor = colors.pmkCaregiverAyah,
                    hours = ayahMinutes / 60,
                    minutes = ayahMinutes % 60,
                    percent = ayahPct,
                    bgColor = colors.pmkCaregiverAyahBg,
                    borderColor = colors.pmkCaregiverAyahBorder,
                    titleColor = colors.pmkCaregiverAyah,
                    modifier = Modifier.weight(1f)
                )

                // Pendamping
                CaregiverMiniCard(
                    title = "Pendamping",
                    dotColor = colors.pmkCaregiverPendamping,
                    hours = pendampingMinutes / 60,
                    minutes = pendampingMinutes % 60,
                    percent = pendampingPct,
                    bgColor = colors.pmkCaregiverPendampingBg,
                    borderColor = colors.pmkCaregiverPendampingBorder,
                    titleColor = colors.pmkCaregiverPendamping,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CaregiverMiniCard(
    title: String,
    dotColor: Color,
    hours: Int,
    minutes: Int,
    percent: Int,
    bgColor: Color,
    borderColor: Color,
    titleColor: Color,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    style = KanguruTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = "${hours}j ${minutes}m",
                style = KanguruTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Text(
                text = "$percent% Total",
                style = KanguruTheme.typography.labelSmall,
                color = colors.textSecondary
            )
        }
    }
}

@Composable
private fun OptimalTemperatureBanner(
    sessions: List<PmkSession>,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val validTemps = sessions.mapNotNull { it.babyTemperature }
    val avgTemp = if (validTemps.isNotEmpty()) {
        String.format(Locale.US, "%.1f", validTemps.average())
    } else {
        "36.8"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.successContainer.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, colors.success.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(colors.success.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = colors.success,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Suhu Bayi Terjaga Optimal",
                        style = KanguruTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Rata-rata $avgTemp°C (Rentang normal: 36.5–37.5°C)",
                        style = KanguruTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.cardBackground)
                    .border(1.dp, colors.success.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Stabil",
                    style = KanguruTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.success
                )
            }
        }
    }
}

@Composable
private fun ActiveSessionCard(
    sessionNumber: Int,
    segment: PmkSegment,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale("id", "ID")) }

    val caregiverName = when (segment.caregiver) {
        PmkCaregiver.IBU -> "Ibu"
        PmkCaregiver.AYAH -> "Ayah"
        PmkCaregiver.PENDAMPING -> "Pendamping"
    }

    val (dotColor, tagBg, tagBorder, tagText) = when (segment.caregiver) {
        PmkCaregiver.IBU -> Quad(colors.pmkCaregiverIbu, colors.pmkCaregiverIbuBg, colors.pmkCaregiverIbuBorder, colors.pmkCaregiverIbu)
        PmkCaregiver.AYAH -> Quad(colors.pmkCaregiverAyah, colors.pmkCaregiverAyahBg, colors.pmkCaregiverAyahBorder, colors.pmkCaregiverAyah)
        PmkCaregiver.PENDAMPING -> Quad(colors.pmkCaregiverPendamping, colors.pmkCaregiverPendampingBg, colors.pmkCaregiverPendampingBorder, colors.pmkCaregiverPendamping)
    }

    val startWib = timeFormat.format(Date(segment.startTimeEpoch))
    val endWib = if (segment.endTimeEpoch != null) timeFormat.format(Date(segment.endTimeEpoch)) else "Sekarang"
    val durationHours = segment.durationMinutes / 60
    val durationMins = segment.durationMinutes % 60
    val durationText = "${durationHours}j ${durationMins}m"

    val temp = segment.babyTemperature
    val babyResponse = segment.babyResponse?.ifBlank { null }
        ?: "Bayi tenang, langsung menyusu dengan baik, pernapasan stabil teratur selama dekapan dada."

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        border = BorderStroke(1.dp, colors.cardBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Dot + Sesi N • Caregiver + Duration Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sesi $sessionNumber • $caregiverName",
                        style = KanguruTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(tagBg)
                        .border(1.dp, tagBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = durationText,
                        style = KanguruTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = tagText
                    )
                }
            }

            // Info Row: Waktu WIB & Suhu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Waktu: $startWib – $endWib WIB",
                    style = KanguruTheme.typography.labelSmall,
                    color = colors.textSecondary
                )
                Text(
                    text = "🌡️ ${temp ?: "36.8"}°C (Normal)",
                    style = KanguruTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.success
                )
            }

            // Respon Bayi Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(tagBg.copy(alpha = 0.4f))
                    .border(1.dp, tagBorder.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "Respon Bayi: $babyResponse",
                    style = KanguruTheme.typography.labelSmall,
                    color = colors.textPrimary
                )
            }
        }
    }
}

@Composable
private fun PauseSegmentCard(
    segment: PmkSegment,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale("id", "ID")) }
    val startWib = timeFormat.format(Date(segment.startTimeEpoch))
    val endWib = if (segment.endTimeEpoch != null) timeFormat.format(Date(segment.endTimeEpoch)) else "..."
    val pauseMinutes = segment.durationMinutes

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colors.pmkPauseDashedBg),
        border = BorderStroke(1.dp, colors.pmkPauseDashedBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = null,
                    tint = colors.pmkCaregiverPendamping,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Jeda: ${segment.pauseReason ?: "Mandi, ganti popok & ASI"}",
                    style = KanguruTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = colors.pmkCaregiverPendamping,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${pauseMinutes} Menit ($startWib - $endWib)",
                style = KanguruTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colors.pmkCaregiverPendamping
            )
        }
    }
}

@Composable
private fun DoctorEvaluationNoteCard(
    totalActiveHours: Int,
    totalActiveMinutes: Int,
    isTargetMet: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.pmkDoctorNoteBg),
        border = BorderStroke(1.dp, colors.pmkDoctorNoteBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier
                    .size(20.dp)
                    .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = "Catatan Evaluasi Dokter Anak:",
                    style = KanguruTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
                Text(
                    text = if (isTargetMet) {
                        "Pencapaian $totalActiveHours jam $totalActiveMinutes menit hari ini melampaui target minimum IDAI (8 jam). Pertahankan rotasi estafet bersama Ayah dan Pendamping agar kontak kehangatan kulit si kecil tetap terjaga stabil 24 jam."
                    } else {
                        "Pencapaian hari ini belum memenuhi target minimum IDAI & WHO (8 jam). Libatkan Ayah dan anggota keluarga lain secara bergantian agar durasi kontak kulit meningkat tanpa membuat Bunda kelelahan."
                    },
                    style = KanguruTheme.typography.labelSmall,
                    color = colors.textPrimary
                )
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
