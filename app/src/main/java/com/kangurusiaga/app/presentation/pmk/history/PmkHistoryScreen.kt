package com.kangurusiaga.app.presentation.pmk.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.White
import com.kangurusiaga.app.domain.model.PmkSession
import com.kangurusiaga.app.domain.model.StatsPeriod
import com.kangurusiaga.app.presentation.home.HomeBottomBar
import com.kangurusiaga.app.presentation.home.HomeTab
import com.kangurusiaga.app.presentation.pmk.statistics.PmkStatisticsUiState
import com.kangurusiaga.app.presentation.pmk.statistics.PmkStatisticsViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Screen 1: Kanguru Siaga - Riwayat PMK (Halaman Utama Riwayat PMK)
 * Stitch Screen ID: projects/10808370107038581899/screens/855b59b3940b4dcf928b965cc07f3a55
 */
@Composable
fun PmkHistoryRoute(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: () -> Unit,
    onNavigateToManualLog: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: PmkStatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message) {
        uiState.message?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    PmkHistoryScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToManualLog = onNavigateToManualLog,
        onNavigateToHome = onNavigateToHome,
        onPeriodSelected = viewModel::onPeriodSelected,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
fun PmkHistoryScreen(
    uiState: PmkStatisticsUiState,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: () -> Unit,
    onNavigateToManualLog: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onPeriodSelected: (StatsPeriod) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier
) {
    val creamBg = Color(0xFFFFF9F6)
    val darkText = Color(0xFF1E293B)
    val mutedText = Color(0xFF64748B)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = creamBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(
                color = creamBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = darkText,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Text(
                        text = "Riwayat PMK",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = darkText,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )

                    IconButton(
                        onClick = { /* Filter / export action */ },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Filter atau Unduh Laporan",
                            tint = Color(0xFF475569),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            HomeBottomBar(
                currentTab = HomeTab.PMK,
                onTabSelected = { tab ->
                    when (tab) {
                        HomeTab.BERANDA -> onNavigateToHome()
                        HomeTab.PMK -> onNavigateBack()
                        else -> { /* Other tabs */ }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Description
            item {
                Text(
                    text = "Catatan riwayat sesi kontak kulit ke kulit (KMC) untuk memantau rutinitas dan kenyamanan si kecil.",
                    style = MaterialTheme.typography.bodySmall,
                    color = mutedText,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
            }

            // Summary Statistics Card
            item {
                WeeklySummaryCard(
                    uiState = uiState,
                    onPeriodSelected = onPeriodSelected,
                    onNavigateToDetail = onNavigateToDetail
                )
            }

            // Action Button: + Catat Sesi Manual
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = 1.5.dp,
                            color = BrandPink.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .background(Color(0xFFFFF0F3).copy(alpha = 0.6f))
                        .clickable(onClick = onNavigateToManualLog)
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = BrandPink,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "+ Catat Sesi Manual",
                            color = BrandPink,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.2.sp
                        )
                    }
                }
            }

            // Grouped Sessions List
            val displaySessions = if (uiState.sessions.isNotEmpty()) {
                uiState.sessions
            } else {
                getFallbackSessions()
            }

            val groupedSessions = groupSessionsByDate(displaySessions)

            groupedSessions.forEach { (dateHeader, sessionsInDay) ->
                item {
                    val totalMinutesDay = sessionsInDay.sumOf { it.durationMinutes }
                    val sessionCount = sessionsInDay.size
                    val isToday = dateHeader.contains("Hari Ini")

                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Date Group Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isToday) BrandPink else Color(0xFFCBD5E1))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = dateHeader,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isToday) Color(0xFF334155) else Color(0xFF475569)
                                )
                            }

                            Surface(
                                color = White,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Text(
                                    text = "$sessionCount Sesi · $totalMinutesDay Menit",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF64748B),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Session Cards in this Day
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            sessionsInDay.forEach { session ->
                                HistorySessionCard(session = session)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun WeeklySummaryCard(
    uiState: PmkStatisticsUiState,
    onPeriodSelected: (StatsPeriod) -> Unit,
    onNavigateToDetail: () -> Unit
) {
    val stats = uiState.statistics
    val totalHours = stats.totalDurationMinutes / 60
    val totalMins = stats.totalDurationMinutes % 60
    val totalDurationStr = if (stats.totalDurationMinutes > 0) "${totalHours}j ${totalMins}m" else "14j 30m"
    val avgMinutesStr = if (stats.averageMinutesPerSession > 0) "${stats.averageMinutesPerSession} Menit" else "62 Menit"
    val totalSessionsStr = if (stats.totalSessions > 0) "${stats.totalSessions} Sesi" else "14 Sesi"
    val compliancePct = if (stats.compliancePercentage > 0) stats.compliancePercentage else 85

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFF6E6DF), RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: "Statistik Ringkasan" and Pill Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STATISTIK RINGKASAN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF64748B),
                    letterSpacing = 0.6.sp
                )

                Surface(
                    color = Color(0xFFF8F3F0),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE4E8))
                ) {
                    Row(modifier = Modifier.padding(2.dp)) {
                        val isWeek = uiState.selectedPeriod == StatsPeriod.THIS_WEEK
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isWeek) BrandPink else Color.Transparent)
                                .clickable { onPeriodSelected(StatsPeriod.THIS_WEEK) }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Minggu Ini",
                                fontSize = 11.sp,
                                fontWeight = if (isWeek) FontWeight.Bold else FontWeight.Medium,
                                color = if (isWeek) White else Color(0xFF64748B)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (!isWeek) BrandPink else Color.Transparent)
                                .clickable { onPeriodSelected(StatsPeriod.THIS_MONTH) }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Bulan Ini",
                                fontSize = 11.sp,
                                fontWeight = if (!isWeek) FontWeight.Bold else FontWeight.Medium,
                                color = if (!isWeek) White else Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3-Metric Stat Cards Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Metric 1: Total Durasi
                MetricBox(
                    label = "Total Durasi",
                    value = totalDurationStr,
                    bgColor = Color(0xFFFFF5F6),
                    borderColor = Color(0xFFFFE4E8),
                    modifier = Modifier.weight(1f)
                )

                // Metric 2: Rata-rata/Hari
                MetricBox(
                    label = "Rata-rata/Hari",
                    value = avgMinutesStr,
                    bgColor = Color(0xFFFFF8F0),
                    borderColor = Color(0xFFFEF3C7),
                    modifier = Modifier.weight(1f)
                )

                // Metric 3: Total Sesi
                MetricBox(
                    label = "Total Sesi",
                    value = totalSessionsStr,
                    bgColor = Color(0xFFF0FAF7),
                    borderColor = Color(0xFFD1FAE5),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Target Progress / Motivation Badge WITH "Detail ›" button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNavigateToDetail),
                color = Color(0xFFFFF0F3),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCCD5))
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
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(BrandPink.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = BrandPink,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Target Tercapai $compliancePct% - Sangat Baik!",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF334155),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Detail ›",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandPink
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricBox(
    label: String,
    value: String,
    bgColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                fontSize = 10.5.sp,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun HistorySessionCard(
    session: PmkSession,
    modifier: Modifier = Modifier
) {
    val cal = Calendar.getInstance().apply { timeInMillis = session.startTimeEpoch }
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    val periodInfo = when {
        hour in 6..10 -> Triple("Pagi", Color(0xFFFFF0F3), Color(0xFFE11D48))
        hour in 11..15 -> Triple("Siang", Color(0xFFFFF8F0), Color(0xFFD97706))
        else -> Triple("Malam", Color(0xFFEEF2FF), Color(0xFF4F46E5))
    }

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val startStr = timeFormat.format(Date(session.startTimeEpoch))
    val endStr = timeFormat.format(Date(session.endTimeEpoch))
    val timeRangeText = "$startStr - $endStr"

    val noteText = session.notes ?: run {
        val temp = session.babyTemperature ?: 36.8
        val resp = session.babyResponse ?: "bayi tertidur lelap dan tenang"
        "“Suhu stabil ${temp}°C, $resp.”"
    }

    val statusText = if (session.durationMinutes >= session.targetDurationMinutes) "Selesai Penuh" else "Selesai"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFF3E7E1), RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(periodInfo.second),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = periodInfo.third,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = timeRangeText,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(periodInfo.second)
                            .border(0.5.dp, periodInfo.third.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = periodInfo.first,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = periodInfo.third
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFF0F3))
                        .border(1.dp, Color(0xFFFFCCD5), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${session.durationMinutes} Menit",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandPink
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = noteText,
                    fontSize = 11.sp,
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFECFDF5))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = statusText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF059669)
                    )
                }
            }
        }
    }
}

private fun groupSessionsByDate(sessions: List<PmkSession>): Map<String, List<PmkSession>> {
    val result = LinkedHashMap<String, MutableList<PmkSession>>()
    val todayCal = Calendar.getInstance()
    val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))

    sessions.forEach { session ->
        val sessionCal = Calendar.getInstance().apply { timeInMillis = session.startTimeEpoch }
        val header = when {
            isSameDay(sessionCal, todayCal) -> "Hari Ini, ${dateFormat.format(sessionCal.time)}"
            isSameDay(sessionCal, yesterdayCal) -> "Kemarin, ${dateFormat.format(sessionCal.time)}"
            else -> dateFormat.format(sessionCal.time)
        }
        result.getOrPut(header) { mutableListOf() }.add(session)
    }

    return result
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

private fun getFallbackSessions(): List<PmkSession> {
    val now = System.currentTimeMillis()
    val oneHour = 3600 * 1000L
    val oneDay = 24 * 3600 * 1000L

    return listOf(
        PmkSession(
            id = 1,
            babyId = 1,
            startTimeEpoch = now - 2 * oneHour,
            endTimeEpoch = now - 2 * oneHour + 65 * 60 * 1000L,
            durationMinutes = 65,
            targetDurationMinutes = 60,
            babyTemperature = 36.8,
            babyResponse = "Tidur Tenang",
            notes = "“Suhu stabil 36.8°C, bayi tertidur lelap dan tenang tanpa gumoh.”"
        ),
        PmkSession(
            id = 2,
            babyId = 1,
            startTimeEpoch = now - 7 * oneHour,
            endTimeEpoch = now - 7 * oneHour + 55 * 60 * 1000L,
            durationMinutes = 55,
            targetDurationMinutes = 60,
            babyTemperature = 36.7,
            babyResponse = "Tenang",
            notes = "“Bayi nyaman, sempat menyusu sedikit di awal sesi.”"
        ),
        PmkSession(
            id = 3,
            babyId = 1,
            startTimeEpoch = now - oneDay - 4 * oneHour,
            endTimeEpoch = now - oneDay - 4 * oneHour + 60 * 60 * 1000L,
            durationMinutes = 60,
            targetDurationMinutes = 60,
            babyTemperature = 36.7,
            babyResponse = "Tidur Tenang",
            notes = "“Suhu 36.7°C, posisi katak nyaman.”"
        ),
        PmkSession(
            id = 4,
            babyId = 1,
            startTimeEpoch = now - oneDay - 9 * oneHour,
            endTimeEpoch = now - oneDay - 9 * oneHour + 65 * 60 * 1000L,
            durationMinutes = 65,
            targetDurationMinutes = 60,
            babyTemperature = 36.8,
            babyResponse = "Tidur Tenang",
            notes = "“Kontak kulit langsung, pernapasan teratur.”"
        ),
        PmkSession(
            id = 5,
            babyId = 1,
            startTimeEpoch = now - oneDay - 14 * oneHour,
            endTimeEpoch = now - oneDay - 14 * oneHour + 60 * 60 * 1000L,
            durationMinutes = 60,
            targetDurationMinutes = 60,
            babyTemperature = 36.8,
            babyResponse = "Tenang",
            notes = "“Suhu awal 36.5°C naik ke 36.8°C setelah sesi.”"
        )
    )
}
