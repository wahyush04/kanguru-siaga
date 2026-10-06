package com.kangurusiaga.app.presentation.pmk.reminders

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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.BrandBackground
import com.kangurusiaga.app.core.designsystem.theme.BrandLightPink
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.CardBorder
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.core.designsystem.theme.TextPrimary
import com.kangurusiaga.app.core.designsystem.theme.TextSecondary
import com.kangurusiaga.app.core.designsystem.theme.White
import com.kangurusiaga.app.domain.model.PmkReminder
import com.kangurusiaga.app.presentation.home.HomeBottomBar
import com.kangurusiaga.app.presentation.home.HomeTab

@Composable
fun PmkRemindersRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PmkRemindersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message) {
        uiState.message?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    PmkRemindersScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onToggleReminder = viewModel::toggleReminder,
        onOpenAddDialog = viewModel::openAddDialog,
        onCloseAddDialog = viewModel::closeAddDialog,
        onOpenInfoDialog = viewModel::openInfoDialog,
        onCloseInfoDialog = viewModel::closeInfoDialog,
        onAddReminder = viewModel::addReminder,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PmkRemindersScreen(
    uiState: PmkRemindersUiState,
    onNavigateBack: () -> Unit,
    onToggleReminder: (PmkReminder) -> Unit,
    onOpenAddDialog: () -> Unit,
    onCloseAddDialog: () -> Unit,
    onOpenInfoDialog: () -> Unit,
    onCloseInfoDialog: () -> Unit,
    onAddReminder: (Int, Int, String, Int) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(
                color = BrandBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.common_back),
                                tint = TextPrimary
                            )
                        }

                        Text(
                            text = stringResource(R.string.pmk_reminders_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        IconButton(onClick = onOpenInfoDialog) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = stringResource(R.string.pmk_reminders_cd_info),
                                tint = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = stringResource(R.string.pmk_reminders_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        },
        bottomBar = {
            HomeBottomBar(
                currentTab = HomeTab.PMK,
                onTabSelected = { tab ->
                    if (tab == HomeTab.BERANDA) {
                        onNavigateBack()
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BrandPink)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Medical Alert Banner
                Surface(
                    color = KanguruTheme.colors.errorContainer,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, KanguruTheme.colors.errorBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(KanguruTheme.colors.error.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = KanguruTheme.colors.error,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = stringResource(R.string.pmk_reminders_alert),
                            style = MaterialTheme.typography.bodySmall,
                            color = KanguruTheme.colors.onErrorContainer,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 17.sp
                        )
                    }
                }

                // 2. Daily Goal Tracker Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorder, RoundedCornerShape(22.dp)),
                    colors = CardDefaults.cardColors(containerColor = KanguruTheme.colors.surface),
                    shape = RoundedCornerShape(22.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(BrandPink.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = BrandPink,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.pmk_reminders_target_header),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Surface(
                                color = KanguruTheme.colors.successContainer,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, KanguruTheme.colors.successBorder)
                            ) {
                                Text(
                                    text = stringResource(R.string.pmk_reminders_completed_count, uiState.todayCompletedSessions, uiState.todayTargetSessions),
                                    color = KanguruTheme.colors.onSuccessContainer,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${uiState.todayCompletedMinutes}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(R.string.pmk_reminders_target_minutes, uiState.todayTargetMinutes),
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Text(
                                text = stringResource(R.string.pmk_reminders_percent_fulfilled, uiState.progressPercent),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandPink
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { uiState.progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(9.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = BrandPink,
                            trackColor = KanguruTheme.colors.surfaceVariant,
                            strokeCap = StrokeCap.Round
                        )
                    }
                }

                // 3. Schedule List Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.pmk_reminders_list_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = stringResource(R.string.pmk_reminders_list_total, uiState.reminders.size),
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                // 4. Reminders List
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    uiState.reminders.forEach { reminder ->
                        ReminderCardItem(
                            reminder = reminder,
                            onToggle = { onToggleReminder(reminder) }
                        )
                    }
                }

                // 5. Add Schedule Button
                Button(
                    onClick = onOpenAddDialog,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPink),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.pmk_reminders_btn_add),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Add Reminder Dialog
    if (uiState.showAddDialog) {
        AddReminderDialog(
            onAdd = onAddReminder,
            onDismiss = onCloseAddDialog
        )
    }

    // Clinical Information Dialog
    if (uiState.showInfoDialog) {
        AlertDialog(
            onDismissRequest = onCloseInfoDialog,
            title = {
                Text(
                    text = stringResource(R.string.pmk_reminders_info_dialog_title),
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.pmk_reminders_info_dialog_msg),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = onCloseInfoDialog) {
                    Text(text = stringResource(R.string.alarm_btn_understand), color = BrandPink, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = KanguruTheme.colors.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun ReminderCardItem(
    reminder: PmkReminder,
    onToggle: () -> Unit
) {
    val isDark = KanguruTheme.colors.isDark
    val (iconBg, iconTint, badgeBg, badgeText) = when {
        reminder.timeHour < 12 -> Quadruple(
            if (isDark) BrandPink.copy(alpha = 0.2f) else Color(0xFFFFF1F2),
            BrandPink,
            if (isDark) BrandPink.copy(alpha = 0.2f) else Color(0xFFFFF1F2),
            if (isDark) Color(0xFFFFB3BA) else Color(0xFFE11D48)
        )
        reminder.timeHour < 17 -> Quadruple(
            if (isDark) Color(0xFFF97316).copy(alpha = 0.2f) else Color(0xFFFFF7ED),
            Color(0xFFF97316),
            if (isDark) Color(0xFFF97316).copy(alpha = 0.2f) else Color(0xFFFFF7ED),
            if (isDark) Color(0xFFFFCC99) else Color(0xFFC2410C)
        )
        else -> Quadruple(
            if (isDark) Color(0xFF6366F1).copy(alpha = 0.2f) else Color(0xFFEEF2FF),
            Color(0xFF6366F1),
            if (isDark) Color(0xFF6366F1).copy(alpha = 0.2f) else Color(0xFFEEF2FF),
            if (isDark) Color(0xFFC7D2FE) else Color(0xFF4338CA)
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = KanguruTheme.colors.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = reminder.formattedTime,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (reminder.isEnabled) TextPrimary else TextSecondary.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = badgeBg,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = reminder.label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Target ${reminder.targetMinutes} Menit • Setiap hari ${if (!reminder.isEnabled) "(Nonaktif)" else ""}",
                        fontSize = 11.5.sp,
                        color = TextSecondary
                    )
                }
            }

            Switch(
                checked = reminder.isEnabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = White,
                    checkedTrackColor = KanguruTheme.colors.success,
                    uncheckedThumbColor = TextSecondary.copy(alpha = 0.7f),
                    uncheckedTrackColor = KanguruTheme.colors.surfaceVariant
                )
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddReminderDialog(
    onAdd: (Int, Int, String, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(initialHour = 8, initialMinute = 0, is24Hour = true)
    var label by remember { mutableStateOf("Pagi") }
    var targetMinutes by remember { mutableIntStateOf(60) }
    val labels = listOf("Pagi", "Siang", "Sore", "Malam")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.pmk_reminders_btn_add),
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TimePicker(
                    state = timePickerState,
                    colors = androidx.compose.material3.TimePickerDefaults.colors(
                        clockDialColor = KanguruTheme.colors.surfaceVariant,
                        clockDialSelectedContentColor = White,
                        clockDialUnselectedContentColor = TextPrimary,
                        selectorColor = BrandPink,
                        containerColor = KanguruTheme.colors.surface,
                        periodSelectorBorderColor = CardBorder,
                        periodSelectorSelectedContainerColor = BrandLightPink,
                        periodSelectorUnselectedContainerColor = KanguruTheme.colors.surfaceVariant,
                        periodSelectorSelectedContentColor = BrandPink,
                        periodSelectorUnselectedContentColor = TextSecondary,
                        timeSelectorSelectedContainerColor = BrandLightPink,
                        timeSelectorUnselectedContainerColor = KanguruTheme.colors.surfaceVariant,
                        timeSelectorSelectedContentColor = BrandPink,
                        timeSelectorUnselectedContentColor = TextPrimary
                    )
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Label Jadwal:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        labels.forEach { item ->
                            val isSelected = item == label
                            FilterChip(
                                selected = isSelected,
                                onClick = { label = item },
                                label = { Text(item, fontSize = 11.5.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandLightPink,
                                    selectedLabelColor = BrandPink,
                                    containerColor = KanguruTheme.colors.surfaceVariant,
                                    labelColor = TextPrimary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) BrandPink else CardBorder
                                )
                            )
                        }
                    }
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Durasi Target: $targetMinutes menit",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    val targets = listOf(30, 45, 60, 90)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        targets.forEach { min ->
                            val isSelected = min == targetMinutes
                            FilterChip(
                                selected = isSelected,
                                onClick = { targetMinutes = min },
                                label = { Text("$min m", fontSize = 11.5.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandLightPink,
                                    selectedLabelColor = BrandPink,
                                    containerColor = KanguruTheme.colors.surfaceVariant,
                                    labelColor = TextPrimary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) BrandPink else CardBorder
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAdd(timePickerState.hour, timePickerState.minute, label, targetMinutes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandPink),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = stringResource(R.string.alarm_btn_save), fontWeight = FontWeight.Bold, color = White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.alarm_btn_cancel), color = TextSecondary)
            }
        },
        containerColor = KanguruTheme.colors.surface,
        shape = RoundedCornerShape(24.dp)
    )
}
