package com.kangurusiaga.app.presentation.home

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.BrandBackground
import com.kangurusiaga.app.core.designsystem.theme.BrandCardBorder
import com.kangurusiaga.app.core.designsystem.theme.BrandLightPink
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.BrandSoftAmber
import com.kangurusiaga.app.core.designsystem.theme.BrandSoftBlue
import com.kangurusiaga.app.core.designsystem.theme.BrandSoftGreen
import com.kangurusiaga.app.core.designsystem.theme.BrandTextAmber
import com.kangurusiaga.app.core.designsystem.theme.BrandTextGreen
import com.kangurusiaga.app.core.designsystem.theme.TextPrimary
import com.kangurusiaga.app.core.designsystem.theme.TextSecondary
import com.kangurusiaga.app.core.designsystem.theme.White
import com.kangurusiaga.app.domain.model.Baby
import kotlinx.coroutines.launch

@Composable
fun HomeRoute(
    onNavigateToPmk: () -> Unit,
    onNavigateToProfileSetup: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onNavigateToPmk = onNavigateToPmk,
        onNavigateToProfileSetup = onNavigateToProfileSetup,
        onOpenEmergency = viewModel::openEmergencyDialog,
        onCloseEmergency = viewModel::closeEmergencyDialog,
        onOpenNotification = viewModel::openNotificationSheet,
        onCloseNotification = viewModel::closeNotificationSheet,
        onShowInfo = viewModel::showInfoMessage,
        onClearInfo = viewModel::clearInfoMessage,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onNavigateToPmk: () -> Unit,
    onNavigateToProfileSetup: () -> Unit = {},
    onOpenEmergency: () -> Unit,
    onCloseEmergency: () -> Unit,
    onOpenNotification: () -> Unit,
    onCloseNotification: () -> Unit,
    onShowInfo: (String) -> Unit,
    onClearInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.infoMessage) {
        uiState.infoMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            onClearInfo()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            HomeBottomBar(
                currentTab = HomeTab.BERANDA,
                onTabSelected = { tab ->
                    when (tab) {
                        HomeTab.BERANDA -> { /* already here */ }
                        HomeTab.PMK -> onNavigateToPmk()
                        HomeTab.EDUKASI -> onShowInfo("Modul Edukasi BBLR tersedia di fase berikutnya.")
                        HomeTab.ALARM -> onShowInfo("Pengaturan Alarm ASI tersedia di fase berikutnya.")
                        HomeTab.PROFIL -> onShowInfo("Pengaturan Profil Bayi tersedia di fase berikutnya.")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BrandPink)
                    }
                }
                uiState.activeBaby != null -> {
                    HomeContent(
                        baby = uiState.activeBaby,
                        ageFormatted = uiState.babyAgeFormatted,
                        weightFormatted = uiState.babyWeightFormatted,
                        todaySessionsCount = uiState.todaySessionsCount,
                        todayTargetSessions = uiState.todayTargetSessions,
                        todayProgress = uiState.todayProgressFraction,
                        unreadNotificationsCount = uiState.unreadNotificationsCount,
                        onNavigateToPmk = onNavigateToPmk,
                        onOpenEmergency = onOpenEmergency,
                        onOpenNotification = onOpenNotification,
                        onShowInfo = onShowInfo,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                }
                else -> {
                    EmptyHomeContent(
                        onNavigateToProfileSetup = onNavigateToProfileSetup,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    )
                }
            }
        }
    }

    // Emergency Modal Bottom Sheet
    if (uiState.showEmergencyDialog) {
        EmergencyBottomSheet(
            onDismiss = onCloseEmergency,
            onCallEmergency = {
                val callIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:119")
                }
                context.startActivity(callIntent)
            }
        )
    }

    // Notification Dialog Sheet
    if (uiState.showNotificationSheet) {
        NotificationBottomSheet(
            onDismiss = onCloseNotification,
            onNavigateToPmk = {
                onCloseNotification()
                onNavigateToPmk()
            }
        )
    }
}

@Composable
private fun HomeContent(
    baby: Baby,
    ageFormatted: String,
    weightFormatted: String,
    todaySessionsCount: Int,
    todayTargetSessions: Int,
    todayProgress: Float,
    unreadNotificationsCount: Int,
    onNavigateToPmk: () -> Unit,
    onOpenEmergency: () -> Unit,
    onOpenNotification: () -> Unit,
    onShowInfo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // 1. Header Profile Bayi
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Circular Photo / Avatar
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(BrandLightPink)
                        .border(1.5.dp, BrandCardBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (baby.photoUri != null) {
                        AsyncImage(
                            model = Uri.parse(baby.photoUri),
                            contentDescription = baby.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_kangaroo_mascot),
                            contentDescription = null,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Halo,",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = baby.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = (-0.3).sp
                    )
                    Text(
                        text = "$ageFormatted • $weightFormatted",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            // Notification button with badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable { onOpenNotification() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifikasi",
                    tint = Color(0xFF475569),
                    modifier = Modifier.size(24.dp)
                )

                if (unreadNotificationsCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 4.dp, end = 4.dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(BrandPink)
                            .border(2.dp, BrandBackground, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = unreadNotificationsCount.toString(),
                            color = White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 2. 4 Main Action Cards Grid (2x2)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Card 1: PMK
            ActionGridCard(
                title = "Perawatan Metode\nKanguru (PMK)",
                subtitle = "Video, Timer, Pengingat",
                iconRes = R.drawable.ic_card_pmk,
                containerColor = BrandLightPink,
                borderColor = Color(0xFFFFDDE4),
                onClick = onNavigateToPmk,
                modifier = Modifier.weight(1f)
            )

            // Card 2: Bayi BBLR
            ActionGridCard(
                title = "Perawatan\nBayi BBLR",
                subtitle = "Panduan lengkap",
                iconRes = R.drawable.ic_card_bblr,
                containerColor = Color(0xFFEBF8F1),
                borderColor = Color(0xFFD3F3E1),
                onClick = { onShowInfo("Panduan Perawatan BBLR tersedia di modul Edukasi.") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Card 3: Tanda Kegawatan
            ActionGridCard(
                title = "Tanda Kegawatan\npada BBLR",
                subtitle = "Kenali tanda bahaya",
                iconRes = R.drawable.ic_card_emergency,
                containerColor = Color(0xFFFFF6E9),
                borderColor = Color(0xFFFFE7C6),
                onClick = onOpenEmergency,
                modifier = Modifier.weight(1f)
            )

            // Card 4: Alarm ASI
            ActionGridCard(
                title = "Alarm Pemberian\nASI",
                subtitle = "Untuk bayi dengan OGT/NGT",
                iconRes = R.drawable.ic_card_asi,
                containerColor = Color(0xFFEBF6FF),
                borderColor = Color(0xFFD2E9FF),
                onClick = { onShowInfo("Alarm Jadwal Pemberian ASI tersedia di modul Alarm.") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Pertumbuhan Bayi Card Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFFFDDE4), RoundedCornerShape(24.dp))
                .clickable { onShowInfo("Grafik Pertumbuhan Fenton tersedia di fase berikutnya.") },
            colors = CardDefaults.cardColors(containerColor = BrandLightPink),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_card_growth),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pertumbuhan Bayi",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Grafik Fenton (Berat, Panjang, Lingkar Kepala)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = BrandPink,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Aktivitas Hari Ini Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Aktivitas Hari Ini",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Lihat semua ›",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = BrandPink,
                modifier = Modifier.clickable { onNavigateToPmk() }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // PMK Activity Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFFFE8D1), RoundedCornerShape(20.dp))
                .clickable { onNavigateToPmk() },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBF6)),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Amber Icon Badge
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFF1DC)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_kangaroo_mascot),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PMK",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "$todaySessionsCount dari $todayTargetSessions sesi",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { todayProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF10B981),
                        trackColor = Color(0xFFE2E8F0),
                        strokeCap = StrokeCap.Round
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = BrandPink,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun ActionGridCard(
    title: String,
    subtitle: String,
    iconRes: Int,
    containerColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(164.dp)
            .border(1.dp, borderColor, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(46.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                fontSize = 10.5.sp,
                maxLines = 1
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmergencyBottomSheet(
    onDismiss: () -> Unit,
    onCallEmergency: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = White,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Tanda Kegawatan BBLR",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Segera bawa ke RS/Puskesmas jika ada tanda berikut",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Alert Box
            Surface(
                color = BrandLightPink,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD6DD))
            ) {
                Text(
                    text = "⚠️ Perhatian Cepat: Bayi dengan berat lahir rendah sangat rentan terhadap hipotermia dan gangguan napas mendadak.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFBE123C),
                    fontWeight = FontWeight.Medium,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Danger Items List
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(18.dp))
            ) {
                DangerItemRow(
                    icon = "🚨",
                    title = "Gangguan Pernapasan",
                    subtitle = "Tarikan dada ke dalam"
                )
                HorizontalDivider(color = Color(0xFFF1F5F9))
                DangerItemRow(
                    icon = "🚨",
                    title = "Perubahan Warna Kulit",
                    subtitle = "Kuning / Kebiruan (Sianosis)"
                )
                HorizontalDivider(color = Color(0xFFF1F5F9))
                DangerItemRow(
                    icon = "🚨",
                    title = "Bayi Sulit Dibangunkan",
                    subtitle = "Letargis / lemas"
                )
                HorizontalDivider(color = Color(0xFFF1F5F9))
                DangerItemRow(
                    icon = "🚨",
                    title = "Kesulitan Menyusu",
                    subtitle = "Daya isap sangat lemah"
                )
                HorizontalDivider(color = Color(0xFFF1F5F9))
                DangerItemRow(
                    icon = "🚨",
                    title = "Suhu Tubuh Tidak Normal",
                    subtitle = "< 36.5°C atau > 37.5°C"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Call ambulance button
            Button(
                onClick = onCallEmergency,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Panggil Ambulans / Nakes (119)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DangerItemRow(
    icon: String,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 12.sp
            )
        }
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            fontSize = 11.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationBottomSheet(
    onDismiss: () -> Unit,
    onNavigateToPmk: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = White,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Pemberitahuan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                color = Color(0xFFFFF9F5),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE8D6)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToPmk() }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(BrandLightPink),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🦘", fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Pengingat PMK Pagi",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Saatnya sesi kontak kulit ke kulit minimal 60 menit untuk kehangatan si kecil.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

enum class HomeTab(val label: String) {
    BERANDA("Beranda"),
    PMK("PMK"),
    EDUKASI("Edukasi"),
    ALARM("Alarm"),
    PROFIL("Profil")
}

@Composable
fun HomeBottomBar(
    currentTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = White,
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeTab.entries.forEach { tab ->
                val isSelected = tab == currentTab
                val contentColor = if (isSelected) BrandPink else Color(0xFF94A3B8)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    val icon = when (tab) {
                        HomeTab.BERANDA -> R.drawable.ic_kangaroo_mascot
                        HomeTab.PMK -> R.drawable.ic_card_pmk
                        HomeTab.EDUKASI -> R.drawable.il_trusted_guide
                        HomeTab.ALARM -> R.drawable.ic_card_asi
                        HomeTab.PROFIL -> R.drawable.il_welcome_mother_baby
                    }

                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = tab.label,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = contentColor,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyHomeContent(
    onNavigateToProfileSetup: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_kangaroo_mascot),
            contentDescription = null,
            modifier = Modifier.size(96.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "KANGURU SIAGA",
            style = MaterialTheme.typography.headlineSmall,
            color = BrandPink,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Belum ada profil bayi aktif untuk menampilkan data pemantauan.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onNavigateToProfileSetup,
            colors = ButtonDefaults.buttonColors(containerColor = BrandPink),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(50.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Mulai Isi Data Bayi",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}
