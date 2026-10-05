package com.kangurusiaga.app.presentation.settings

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import coil.request.ImageRequest
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.BrandBackground
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.White
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.domain.model.settings.AppTextScale
import com.kangurusiaga.app.domain.model.settings.AppThemeMode
import com.kangurusiaga.app.presentation.settings.components.EditBabyProfileBottomSheet
import com.kangurusiaga.app.presentation.settings.components.TextSizeBottomSheet
import com.kangurusiaga.app.presentation.settings.components.ThemeSelectionBottomSheet
import java.util.Locale

@Composable
fun SettingsRoute(
    onNavigateBack: () -> Unit,
    onNavigateToPmkReminders: () -> Unit,
    onNavigateToFeedingAlarm: () -> Unit,
    onNavigateToUserGuide: () -> Unit,
    onNavigateToAboutApp: () -> Unit,
    onNavigateToClinicalGuidelines: () -> Unit,
    onNavigateToMedicalDisclaimer: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    SettingsScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNavigateToPmkReminders = onNavigateToPmkReminders,
        onNavigateToFeedingAlarm = onNavigateToFeedingAlarm,
        onNavigateToUserGuide = onNavigateToUserGuide,
        onNavigateToAboutApp = onNavigateToAboutApp,
        onNavigateToClinicalGuidelines = onNavigateToClinicalGuidelines,
        onNavigateToMedicalDisclaimer = onNavigateToMedicalDisclaimer,
        onOpenEditProfile = viewModel::openEditProfileSheet,
        onCloseEditProfile = viewModel::closeEditProfileSheet,
        onEditNameChange = viewModel::updateEditName,
        onEditGenderChange = viewModel::updateEditGender,
        onEditBirthDateChange = viewModel::updateEditBirthDate,
        onEditBirthWeightChange = viewModel::updateEditBirthWeight,
        onEditPhotoSelected = viewModel::onPhotoSelected,
        createTempCameraUri = viewModel::createTempCameraUri,
        onSaveProfile = viewModel::saveProfileChanges,
        onOpenTheme = viewModel::openThemeSheet,
        onCloseTheme = viewModel::closeThemeSheet,
        onSelectTheme = viewModel::selectThemeMode,
        onApplyTheme = viewModel::applyTheme,
        onOpenTextSize = viewModel::openTextSizeSheet,
        onCloseTextSize = viewModel::closeTextSizeSheet,
        onSelectTextSize = viewModel::selectTextScale,
        onApplyTextSize = viewModel::applyTextScale,
        onToggleNotifications = viewModel::toggleNotifications,
        onOpenClearHistory = viewModel::openClearHistoryDialog,
        onCloseClearHistory = viewModel::closeClearHistoryDialog,
        onConfirmClearHistory = viewModel::confirmClearHistory,
        onOpenResetApp = viewModel::openResetAppDialog,
        onCloseResetApp = viewModel::closeResetAppDialog,
        onConfirmResetApp = viewModel::confirmResetApp,
        modifier = modifier,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onNavigateBack: () -> Unit,
    onNavigateToPmkReminders: () -> Unit,
    onNavigateToFeedingAlarm: () -> Unit,
    onNavigateToUserGuide: () -> Unit,
    onNavigateToAboutApp: () -> Unit,
    onNavigateToClinicalGuidelines: () -> Unit,
    onNavigateToMedicalDisclaimer: () -> Unit,
    onOpenEditProfile: () -> Unit,
    onCloseEditProfile: () -> Unit,
    onEditNameChange: (String) -> Unit,
    onEditGenderChange: (Gender) -> Unit,
    onEditBirthDateChange: (Long) -> Unit,
    onEditBirthWeightChange: (String) -> Unit,
    onEditPhotoSelected: (android.net.Uri) -> Unit,
    createTempCameraUri: () -> android.net.Uri,
    onSaveProfile: () -> Unit,
    onOpenTheme: () -> Unit,
    onCloseTheme: () -> Unit,
    onSelectTheme: (AppThemeMode) -> Unit,
    onApplyTheme: () -> Unit,
    onOpenTextSize: () -> Unit,
    onCloseTextSize: () -> Unit,
    onSelectTextSize: (AppTextScale) -> Unit,
    onApplyTextSize: () -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onOpenClearHistory: () -> Unit = {},
    onCloseClearHistory: () -> Unit = {},
    onConfirmClearHistory: () -> Unit = {},
    onOpenResetApp: () -> Unit = {},
    onCloseResetApp: () -> Unit = {},
    onConfirmResetApp: () -> Unit = {},
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // BEGIN: NavigationHeader (matching Stitch nav header)
            Surface(
                color = BrandBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable(onClick = onNavigateBack),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = Color(0xFF1E293B),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Text(
                            text = "Pengaturan",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.size(36.dp))
                    }

                    HorizontalDivider(
                        color = Color(0xFFF2ECE9),
                        thickness = 1.dp
                    )
                }
            }
            // END: NavigationHeader

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrandPink)
                }
            } else {
                val baby = uiState.baby
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // ==========================================
                    // 1. SECTION: PROFIL BAYI
                    // ==========================================
                    if (baby != null) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "PROFIL BAYI",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF9CA3AF),
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Edit Cepat",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFE11D48),
                                    modifier = Modifier.clickable(onClick = onOpenEditProfile)
                                )
                            }

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = White,
                                border = BorderStroke(1.dp, Color(0xFFF2ECE9)),
                                shadowElevation = 0.5.dp
                            ) {
                                Column {
                                    // Row 1: Foto Bayi (with active indicator & photo set from onboarding)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(onClick = onOpenEditProfile)
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Box(modifier = Modifier.size(48.dp)) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .clip(CircleShape)
                                                        .border(2.dp, Color(0xFFFFE4E6), CircleShape)
                                                        .background(Color(0xFFFFF1F2)),
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
                                                        Image(
                                                            painter = painterResource(id = R.drawable.ic_kangaroo_mascot),
                                                            contentDescription = null,
                                                            modifier = Modifier.size(32.dp)
                                                        )
                                                    }
                                                }

                                                // Emerald active dot indicator matching Stitch
                                                Box(
                                                    modifier = Modifier
                                                        .size(14.dp)
                                                        .align(Alignment.BottomEnd)
                                                        .border(2.dp, White, CircleShape)
                                                        .background(Color(0xFF10B981), CircleShape)
                                                )
                                            }

                                            Column {
                                                Text(
                                                    text = "Foto Bayi",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color(0xFF1F2937)
                                                )
                                                Text(
                                                    text = "Perbarui foto profil si kecil",
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF6B7280),
                                                    modifier = Modifier.padding(top = 2.dp)
                                                )
                                            }
                                        }

                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                            contentDescription = null,
                                            tint = Color(0xFF9CA3AF),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                                    // Row 2: Nama Bayi
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(onClick = onOpenEditProfile)
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Nama Bayi",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF374151)
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = baby.name,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF111827)
                                            )
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                                contentDescription = null,
                                                tint = Color(0xFF9CA3AF),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                                    // Row 3: Tanggal Lahir
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(onClick = onOpenEditProfile)
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Tanggal Lahir",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF374151)
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = uiState.formattedBirthDate,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF4B5563)
                                            )
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                                contentDescription = null,
                                                tint = Color(0xFF9CA3AF),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                                    // Row 4: Jenis Kelamin
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(onClick = onOpenEditProfile)
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Jenis Kelamin",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF374151)
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            val isFemale = baby.gender == Gender.FEMALE
                                            Surface(
                                                shape = RoundedCornerShape(100.dp),
                                                color = if (isFemale) Color(0xFFFFF1F2) else Color(0xFFEFF6FF),
                                                border = BorderStroke(1.dp, if (isFemale) Color(0xFFFFE4E6) else Color(0xFFDBEAFE))
                                            ) {
                                                Text(
                                                    text = if (isFemale) "Perempuan" else "Laki-laki",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = if (isFemale) Color(0xFFE11D48) else Color(0xFF2563EB),
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                                                )
                                            }
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                                contentDescription = null,
                                                tint = Color(0xFF9CA3AF),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                                    // Row 5: Berat Lahir
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(onClick = onOpenEditProfile)
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Berat Lahir",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF374151)
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            val formattedWeight = String.format(Locale("id", "ID"), "%,d", baby.birthWeightGram).replace(',', '.')
                                            val isBblr = baby.birthWeightGram < 2500
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text(
                                                    text = "$formattedWeight gram",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color(0xFF1F2937)
                                                )
                                                if (isBblr) {
                                                    Surface(
                                                        shape = RoundedCornerShape(4.dp),
                                                        color = Color(0xFFFFFBEB),
                                                        border = BorderStroke(1.dp, Color(0xFFFDE68A))
                                                    ) {
                                                        Text(
                                                            text = "BBLR",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Medium,
                                                            color = Color(0xFFD97706),
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                                contentDescription = null,
                                                tint = Color(0xFF9CA3AF),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ==========================================
                    // 2. SECTION: PENGINGAT & NOTIFIKASI
                    // ==========================================
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "PENGINGAT & NOTIFIKASI",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9CA3AF),
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = White,
                            border = BorderStroke(1.dp, Color(0xFFF2ECE9)),
                            shadowElevation = 0.5.dp
                        ) {
                            Column {
                                // Notifikasi Umum
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(end = 12.dp)
                                    ) {
                                        Text(
                                            text = "Notifikasi Umum",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF1F2937)
                                        )
                                        Text(
                                            text = "Izinkan push notification di perangkat",
                                            fontSize = 12.sp,
                                            color = Color(0xFF6B7280),
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                    Switch(
                                        checked = uiState.appSettings.notificationsEnabled,
                                        onCheckedChange = onToggleNotifications,
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = White,
                                            checkedTrackColor = BrandPink,
                                            uncheckedThumbColor = White,
                                            uncheckedTrackColor = Color(0xFFE5E7EB),
                                            uncheckedBorderColor = Color.Transparent
                                        )
                                    )
                                }

                                HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                                // Pengingat PMK
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(onClick = onNavigateToPmkReminders)
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(end = 8.dp)
                                    ) {
                                        Text(
                                            text = "Pengingat PMK",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF1F2937)
                                        )
                                        Text(
                                            text = "Jadwal alarm kontak kulit harian",
                                            fontSize = 12.sp,
                                            color = Color(0xFF6B7280),
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(100.dp),
                                            color = Color(0xFFFFF1F2),
                                            border = BorderStroke(1.dp, Color(0xFFFFCCD5))
                                        ) {
                                            Text(
                                                text = uiState.pmkScheduleBadgeText,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFFE11D48),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                            contentDescription = null,
                                            tint = Color(0xFF9CA3AF),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                                // Pengingat ASI OGT/NGT
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(onClick = onNavigateToFeedingAlarm)
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(end = 8.dp)
                                    ) {
                                        Text(
                                            text = "Pengingat ASI OGT/NGT",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF1F2937)
                                        )
                                        Text(
                                            text = "Jadwal minum ASI per 2-3 jam",
                                            fontSize = 12.sp,
                                            color = Color(0xFF6B7280),
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(100.dp),
                                            color = Color(0xFFECFDF5),
                                            border = BorderStroke(1.dp, Color(0xFFA7F3D0))
                                        ) {
                                            Text(
                                                text = uiState.feedingAlarmBadgeText,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF047857),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                            contentDescription = null,
                                            tint = Color(0xFF9CA3AF),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ==========================================
                    // 3. SECTION: TAMPILAN
                    // ==========================================
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "TAMPILAN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9CA3AF),
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = White,
                            border = BorderStroke(1.dp, Color(0xFFF2ECE9)),
                            shadowElevation = 0.5.dp
                        ) {
                            Column {
                                // Ukuran Teks
                                SettingsItemRow(
                                    label = "Ukuran Teks",
                                    value = uiState.appSettings.textScale.title,
                                    onClick = onOpenTextSize
                                )

                                HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                                // Tema Aplikasi
                                SettingsItemRow(
                                    label = "Tema Aplikasi",
                                    value = uiState.appSettings.themeMode.title,
                                    onClick = onOpenTheme
                                )
                            }
                        }
                    }

                    // ==========================================
                    // 4. SECTION: DATA & PRIVASI
                    // ==========================================
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "DATA & PRIVASI",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9CA3AF),
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = White,
                            border = BorderStroke(1.dp, Color(0xFFF2ECE9)),
                            shadowElevation = 0.5.dp
                        ) {
                            Column {
                                // Data Tersimpan di Perangkat
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(end = 8.dp)
                                    ) {
                                        Text(
                                            text = "Data Tersimpan di Perangkat",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF1F2937)
                                        )
                                        Text(
                                            text = "Penyimpanan lokal aman & terenkripsi",
                                            fontSize = 12.sp,
                                            color = Color(0xFF6B7280),
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF059669),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                                // Hapus Riwayat Aktivitas
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(onClick = onOpenClearHistory)
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(end = 8.dp)
                                    ) {
                                        Text(
                                            text = "Hapus Riwayat Aktivitas",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFFE11D48)
                                        )
                                        Text(
                                            text = "Hapus log PMK & jadwal lama",
                                            fontSize = 12.sp,
                                            color = Color(0xFF9CA3AF),
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = Color(0xFFFB7185),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                                // Reset Data Aplikasi
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(onClick = onOpenResetApp)
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(end = 8.dp)
                                    ) {
                                        Text(
                                            text = "Reset Data Aplikasi",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFFDC2626)
                                        )
                                        Text(
                                            text = "Kembalikan ke pengaturan awal",
                                            fontSize = 12.sp,
                                            color = Color(0xFF9CA3AF),
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = Color(0xFFF87171),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // ==========================================
                    // 5. SECTION: INFORMASI
                    // ==========================================
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "INFORMASI",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9CA3AF),
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = White,
                            border = BorderStroke(1.dp, Color(0xFFF2ECE9)),
                            shadowElevation = 0.5.dp
                        ) {
                            Column {
                                // Tentang KANGURU SIAGA
                                SettingsNavigationItem(
                                    title = "Tentang KANGURU SIAGA",
                                    subtitle = "Aplikasi pendamping perawatan BBLR di rumah",
                                    onClick = onNavigateToAboutApp
                                )

                                HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                                // Versi Aplikasi
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Versi Aplikasi",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF1F2937)
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFFF3F4F6)
                                    ) {
                                        Text(
                                            text = "v1.2.0 (Build 2026)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF9CA3AF),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                                // Sumber Informasi & Panduan Klinis
                                SettingsNavigationItem(
                                    title = "Sumber Informasi & Panduan Klinis",
                                    subtitle = "Standar IDAI, Kemenkes RI, & Kurva Fenton",
                                    onClick = onNavigateToClinicalGuidelines
                                )

                                HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                                // Disclaimer Medis
                                SettingsNavigationItem(
                                    title = "Disclaimer Medis",
                                    subtitle = "Aplikasi bukan pengganti rujukan medis darurat",
                                    onClick = onNavigateToMedicalDisclaimer
                                )
                            }
                        }
                    }

                    // ==========================================
                    // 6. SECTION: BANTUAN
                    // ==========================================
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "BANTUAN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9CA3AF),
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = White,
                            border = BorderStroke(1.dp, Color(0xFFF2ECE9)),
                            shadowElevation = 0.5.dp
                        ) {
                            SettingsNavigationItem(
                                title = "Panduan Penggunaan",
                                subtitle = "Petunjuk lengkap fitur Kanguru Siaga",
                                onClick = onNavigateToUserGuide
                            )
                        }
                    }

                    // ==========================================
                    // 7. FOOTER BRANDING
                    // ==========================================
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "KANGURU SIAGA • Pendamping BBLR Mandiri",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF9CA3AF)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // Modal Bottom Sheets
    if (uiState.showEditProfileSheet) {
        EditBabyProfileBottomSheet(
            name = uiState.editName,
            gender = uiState.editGender,
            birthDateEpoch = uiState.editBirthDateEpoch,
            birthWeightInput = uiState.editBirthWeightInput,
            photoUri = uiState.editPhotoUri,
            errorMessage = uiState.editErrorMessage,
            isSaving = uiState.isSavingProfile,
            onNameChange = onEditNameChange,
            onGenderChange = onEditGenderChange,
            onBirthDateChange = onEditBirthDateChange,
            onBirthWeightChange = onEditBirthWeightChange,
            onPhotoSelected = onEditPhotoSelected,
            createTempCameraUri = createTempCameraUri,
            onSave = onSaveProfile,
            onDismiss = onCloseEditProfile
        )
    }

    if (uiState.showThemeSheet) {
        ThemeSelectionBottomSheet(
            selectedTheme = uiState.selectedThemeMode,
            onSelectTheme = onSelectTheme,
            onApplyTheme = onApplyTheme,
            onDismiss = onCloseTheme
        )
    }

    if (uiState.showTextSizeSheet) {
        TextSizeBottomSheet(
            selectedScale = uiState.selectedTextScale,
            onSelectScale = onSelectTextSize,
            onApplyScale = onApplyTextSize,
            onDismiss = onCloseTextSize
        )
    }

    // Confirmation Dialog: Hapus Riwayat Aktivitas
    if (uiState.showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = onCloseClearHistory,
            title = {
                Text(
                    text = "Hapus Riwayat Aktivitas?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "Catatan sesi PMK yang telah selesai dan riwayat jadwal lama akan dibersihkan dari penyimpanan lokal. Data profil bayi dan riwayat pengukuran pertumbuhan tetap aman.",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            },
            confirmButton = {
                TextButton(onClick = onConfirmClearHistory) {
                    Text(text = "Hapus", color = Color(0xFFE11D48), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onCloseClearHistory) {
                    Text(text = "Batal", color = Color(0xFF64748B))
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = White
        )
    }

    // Confirmation Dialog: Reset Data Aplikasi
    if (uiState.showResetAppDialog) {
        AlertDialog(
            onDismissRequest = onCloseResetApp,
            title = {
                Text(
                    text = "Reset Data Aplikasi?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "Perhatian: Tindakan ini akan mengembalikan aplikasi ke pengaturan awal. Anda perlu mengatur ulang profil bayi.",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            },
            confirmButton = {
                TextButton(onClick = onConfirmResetApp) {
                    Text(text = "Reset", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onCloseResetApp) {
                    Text(text = "Batal", color = Color(0xFF64748B))
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = White
        )
    }
}

@Composable
private fun SettingsItemRow(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1F2937)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6B7280)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun SettingsNavigationItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F2937)
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF6B7280),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(16.dp)
        )
    }
}
