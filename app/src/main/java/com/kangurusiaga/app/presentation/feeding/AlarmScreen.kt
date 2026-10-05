package com.kangurusiaga.app.presentation.feeding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kangurusiaga.app.core.designsystem.theme.BrandBackground
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.CardBorder
import com.kangurusiaga.app.core.designsystem.theme.TextPrimary
import com.kangurusiaga.app.core.designsystem.theme.TextSecondary
import com.kangurusiaga.app.core.designsystem.theme.TextTertiary
import com.kangurusiaga.app.core.designsystem.theme.White
import com.kangurusiaga.app.domain.model.FeedingSchedule
import com.kangurusiaga.app.presentation.feeding.components.AddFeedingScheduleBottomSheet
import com.kangurusiaga.app.presentation.feeding.components.DeleteFeedingScheduleDialog
import com.kangurusiaga.app.presentation.feeding.components.EditFeedingScheduleBottomSheet
import com.kangurusiaga.app.presentation.feeding.components.FeedingInfoDialog
import com.kangurusiaga.app.presentation.home.HomeBottomBar
import com.kangurusiaga.app.presentation.home.HomeTab

import android.content.pm.PackageManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.kangurusiaga.app.R

@Composable
fun AlarmRoute(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToPmk: () -> Unit,
    onNavigateToEducation: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    viewModel: AlarmViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Notification permission launcher for Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Gracefully handled; alarms still scheduled
    }

    val requestPermissionIfAppropriate = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    AlarmScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNavigateToHome = onNavigateToHome,
        onNavigateToPmk = onNavigateToPmk,
        onNavigateToEducation = onNavigateToEducation,
        onNavigateToProfile = onNavigateToProfile,
        onToggleSchedule = { id, enabled ->
            if (enabled) {
                requestPermissionIfAppropriate()
            }
            viewModel.onToggleSchedule(id, enabled)
        },
        onOpenAddSchedule = viewModel::onOpenAddSchedule,
        onCloseAddSchedule = viewModel::onCloseAddSchedule,
        onOpenEditSchedule = viewModel::onOpenEditSchedule,
        onCloseEditSchedule = viewModel::onCloseEditSchedule,
        onSaveNewSchedule = { hour, minute, volume, method, note, reminderEnabled, repeatType ->
            if (reminderEnabled) {
                requestPermissionIfAppropriate()
            }
            viewModel.onSaveNewSchedule(hour, minute, volume, method, note, reminderEnabled, repeatType)
        },
        onUpdateSchedule = { scheduleId, hour, minute, volume, method, note, reminderEnabled, repeatType ->
            if (reminderEnabled) {
                requestPermissionIfAppropriate()
            }
            viewModel.onUpdateSchedule(scheduleId, hour, minute, volume, method, note, reminderEnabled, repeatType)
        },
        onRequestDeleteSchedule = viewModel::onRequestDeleteSchedule,
        onCancelDeleteSchedule = viewModel::onCancelDeleteSchedule,
        onConfirmDeleteSchedule = viewModel::onConfirmDeleteSchedule,
        onOpenInfoDialog = viewModel::onOpenInfoDialog,
        onDismissInfoDialog = viewModel::onDismissInfoDialog,
        onClearUserMessage = viewModel::onClearUserMessage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(
    uiState: AlarmUiState,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToPmk: () -> Unit,
    onNavigateToEducation: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    onToggleSchedule: (Long, Boolean) -> Unit,
    onOpenAddSchedule: () -> Unit,
    onCloseAddSchedule: () -> Unit,
    onOpenEditSchedule: (FeedingSchedule) -> Unit,
    onCloseEditSchedule: () -> Unit,
    onSaveNewSchedule: (
        hour: Int,
        minute: Int,
        volumeMl: Int,
        method: com.kangurusiaga.app.domain.model.FeedingMethod,
        note: String?,
        reminderEnabled: Boolean,
        repeatType: com.kangurusiaga.app.domain.model.RepeatType
    ) -> Unit,
    onUpdateSchedule: (
        scheduleId: Long,
        hour: Int,
        minute: Int,
        volumeMl: Int,
        method: com.kangurusiaga.app.domain.model.FeedingMethod,
        note: String?,
        reminderEnabled: Boolean,
        repeatType: com.kangurusiaga.app.domain.model.RepeatType
    ) -> Unit,
    onRequestDeleteSchedule: (FeedingSchedule) -> Unit,
    onCancelDeleteSchedule: () -> Unit,
    onConfirmDeleteSchedule: () -> Unit,
    onOpenInfoDialog: () -> Unit,
    onDismissInfoDialog: () -> Unit,
    onClearUserMessage: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val addSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val editSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(uiState.userMessage) {
        val msg = uiState.userMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            onClearUserMessage()
        }
    }

    Scaffold(
        containerColor = BrandBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AlarmTopAppBar(
                onNavigateBack = onNavigateBack,
                onInfoClick = onOpenInfoDialog
            )
        },
        bottomBar = {
            HomeBottomBar(
                currentTab = HomeTab.ALARM,
                onTabSelected = { tab ->
                    when (tab) {
                        HomeTab.BERANDA -> onNavigateToHome()
                        HomeTab.PMK -> onNavigateToPmk()
                        HomeTab.EDUKASI -> onNavigateToEducation()
                        HomeTab.ALARM -> { /* Already on Alarm */ }
                        HomeTab.PROFIL -> onNavigateToProfile()
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
            if (uiState.isLoading && uiState.schedules.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandPink)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Advisory Notice Card
                    item {
                        Spacer(modifier = Modifier.height(2.dp))
                        AlarmAdvisoryCard()
                    }

                    // Schedules Container Card
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.cardBorder, RoundedCornerShape(20.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            // Section Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.alarm_section_header_schedule),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary
                                )
                                Text(
                                    text = stringResource(R.string.alarm_section_header_status),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary
                                )
                            }

                            // Items
                            uiState.schedules.forEachIndexed { index, schedule ->
                                if (index > 0) {
                                    HorizontalDivider(
                                        thickness = 1.dp,
                                        color = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.divider
                                    )
                                }

                                AlarmScheduleItem(
                                    schedule = schedule,
                                    index = index,
                                    onItemClick = { onOpenEditSchedule(schedule) },
                                    onToggle = { isEnabled -> onToggleSchedule(schedule.id, isEnabled) }
                                )
                            }
                        }
                    }

                    // Add Schedule Button (+ Tambah Jadwal)
                    item {
                        Button(
                            onClick = onOpenAddSchedule,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandPink,
                                contentColor = White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.alarm_btn_add),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet: Add Schedule
    if (uiState.isAddSheetVisible) {
        AddFeedingScheduleBottomSheet(
            sheetState = addSheetState,
            onSave = onSaveNewSchedule,
            onDismiss = onCloseAddSchedule
        )
    }

    // Modal Bottom Sheet: Edit Schedule
    if (uiState.isEditSheetVisible && uiState.selectedScheduleForEdit != null) {
        EditFeedingScheduleBottomSheet(
            schedule = uiState.selectedScheduleForEdit,
            sheetState = editSheetState,
            onSave = onUpdateSchedule,
            onRequestDelete = onRequestDeleteSchedule,
            onDismiss = onCloseEditSchedule
        )
    }

    // Confirmation Dialog: Delete Schedule
    if (uiState.schedulePendingDelete != null) {
        DeleteFeedingScheduleDialog(
            onConfirm = onConfirmDeleteSchedule,
            onDismiss = onCancelDeleteSchedule
        )
    }

    // Information Dialog: Medical Notice
    if (uiState.isInfoDialogVisible) {
        FeedingInfoDialog(
            onDismiss = onDismissInfoDialog
        )
    }
}

@Composable
private fun AlarmTopAppBar(
    onNavigateBack: () -> Unit,
    onInfoClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrandBackground)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.common_back),
                tint = TextPrimary
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.alarm_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 16.5.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.alarm_subtitle_ogt),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 14.5.sp,
                textAlign = TextAlign.Center
            )
        }

        IconButton(onClick = onInfoClick) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = stringResource(R.string.alarm_info_cd),
                tint = TextSecondary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun AlarmAdvisoryCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.warningContainer)
            .border(1.dp, com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.warningBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.warningText,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = stringResource(R.string.alarm_advisory),
            color = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.warningText,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun AlarmScheduleItem(
    schedule: FeedingSchedule,
    index: Int,
    onItemClick: () -> Unit,
    onToggle: (Boolean) -> Unit
) {
    // Alternating soft accent colors for the clock icon (orange, blue, cyan, rose, amber)
    val colorPalettes = listOf(
        Color(0xFFF97316), // orange
        Color(0xFF3B82F6), // blue
        Color(0xFF06B6D4), // cyan
        Color(0xFFF43F5E), // rose
        Color(0xFFF59E0B)  // amber
    )
    val iconColor = colorPalettes[index % colorPalettes.size]
    val bgIconColor = iconColor.copy(alpha = 0.15f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onItemClick() }
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Clock Icon Circle
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(bgIconColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Schedule Info
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = schedule.formattedTime,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = (-0.3).sp
                    )
                    Text(
                        text = stringResource(R.string.alarm_volume_unit, schedule.volumeMl),
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextSecondary
                    )
                }
                Text(
                    text = schedule.method.displayName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextSecondary
                )
            }
        }

        // Toggle Switch
        Switch(
            checked = schedule.isEnabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = White,
                checkedTrackColor = Color(0xFF10B981),
                uncheckedThumbColor = White,
                uncheckedTrackColor = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.cardBorder
            )
        )
    }
}
