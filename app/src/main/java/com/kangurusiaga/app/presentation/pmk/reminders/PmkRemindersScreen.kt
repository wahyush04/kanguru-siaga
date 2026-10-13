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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
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

@Composable
fun PmkRemindersRoute(
    onNavigateBack: () -> Unit,
    onNavigateToAddSchedule: () -> Unit,
    onNavigateToEditSchedule: (Long) -> Unit,
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
        onNavigateToAddSchedule = onNavigateToAddSchedule,
        onNavigateToEditSchedule = onNavigateToEditSchedule,
        onToggleReminder = viewModel::toggleReminder,
        onOpenInfoDialog = viewModel::openInfoDialog,
        onCloseInfoDialog = viewModel::closeInfoDialog,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PmkRemindersScreen(
    uiState: PmkRemindersUiState,
    onNavigateBack: () -> Unit,
    onNavigateToAddSchedule: () -> Unit,
    onNavigateToEditSchedule: (Long) -> Unit,
    onToggleReminder: (PmkReminder) -> Unit,
    onOpenInfoDialog: () -> Unit,
    onCloseInfoDialog: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val totalMinutes = uiState.todayCompletedMinutes
    val completedHours = totalMinutes / 60
    val completedRemainingMinutes = totalMinutes % 60

    val idealMinutes = 1200
    val minMinutes = 480
    val dayMinutes = 1440

    val progressFraction24h = (totalMinutes.toFloat() / dayMinutes.toFloat()).coerceIn(0f, 1f)
    val progressPercentIdeal = ((totalMinutes.toFloat() / idealMinutes.toFloat()) * 100).toInt().coerceAtMost(100)

    val (statusText, statusBg, statusColor) = when {
        totalMinutes >= 1200 -> Triple(
            stringResource(R.string.pmk_reminders_status_optimal),
            Color(0xFFECFDF5),
            Color(0xFF059669)
        )
        totalMinutes >= 720 -> Triple(
            stringResource(R.string.pmk_reminders_status_good),
            Color(0xFFECFDF5),
            Color(0xFF059669)
        )
        totalMinutes >= 480 -> Triple(
            stringResource(R.string.pmk_reminders_status_sufficient),
            Color(0xFFFFFBEB),
            Color(0xFFD97706)
        )
        else -> Triple(
            stringResource(R.string.pmk_reminders_status_need_improvement),
            Color(0xFFFFF1F2),
            Color(0xFFE11D48)
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandBackground,
        snackbarHost = { com.kangurusiaga.app.core.designsystem.component.KanguruSnackbarHost(snackbarHostState) },
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
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.size(40.dp)
                        ) {
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
                            color = TextPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = onOpenInfoDialog,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
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
                        lineHeight = MaterialTheme.typography.bodySmall.lineHeight,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
            }
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
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Standar PMK Kontinu Mandiri Banner (Soft pink card)
                Surface(
                    color = Color(0xFFFFF0F2),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFED7DE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFE4E6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = stringResource(R.string.pmk_reminders_guideline_header),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFBE123C)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            val bannerText = buildAnnotatedString {
                                append("Target minimal ")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFE11D48))) {
                                    append("≥ 8 Jam/hari")
                                }
                                append(", dengan rekomendasi ideal ")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFE11D48))) {
                                    append("≥ 20 Jam/hari")
                                }
                                append(" (estafet bersama Ayah & Pendamping) untuk hasil optimal.")
                            }
                            Text(
                                text = bannerText,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF9F1239),
                                lineHeight = MaterialTheme.typography.bodySmall.lineHeight
                            )
                        }
                    }
                }

                // 2. Daily Goal Tracker Card (TARGET PMK KONTINU - Estafet 24 Jam Mandiri)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFEFE5DE), RoundedCornerShape(22.dp)),
                    colors = CardDefaults.cardColors(containerColor = White),
                    shape = RoundedCornerShape(22.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        // Header row with target badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFFFF0F2)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = BrandPink,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = stringResource(R.string.pmk_reminders_target_title).uppercase(),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = stringResource(R.string.pmk_reminders_target_subtitle),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Surface(
                                color = statusBg,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0))
                            ) {
                                Text(
                                    text = statusText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = statusColor,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Duration values and ideal percentage
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${completedHours}j ${completedRemainingMinutes}m",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "/ 20j Ideal",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = stringResource(R.string.pmk_reminders_percent_to_ideal, progressPercentIdeal),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandPink
                                )
                                Text(
                                    text = if (totalMinutes >= minMinutes) {
                                        stringResource(R.string.pmk_reminders_min_surpassed)
                                    } else {
                                        stringResource(R.string.pmk_reminders_min_pending)
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (totalMinutes >= minMinutes) Color(0xFF059669) else TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 24-hour custom segmented progress bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color(0xFFF1F5F9))
                        ) {
                            // Filled progress bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progressFraction24h)
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(BrandPink)
                            )

                            // Threshold marker 8h (33.3%)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.333f)
                                    .height(10.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(10.dp)
                                        .background(White.copy(alpha = 0.9f))
                                )
                            }

                            // Threshold marker 20h (83.3%)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.833f)
                                    .height(10.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(10.dp)
                                        .background(White.copy(alpha = 0.9f))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Threshold labels
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.pmk_reminders_threshold_zero),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                            Text(
                                text = stringResource(R.string.pmk_reminders_threshold_min),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = stringResource(R.string.pmk_reminders_threshold_ideal),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = BrandPink
                            )
                            Text(
                                text = stringResource(R.string.pmk_reminders_threshold_max),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
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
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                // 4. Reminders List Items
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    uiState.reminders.forEach { reminder ->
                        ReminderCardItem(
                            reminder = reminder,
                            onToggle = { onToggleReminder(reminder) },
                            onEdit = { onNavigateToEditSchedule(reminder.id) }
                        )
                    }
                }

                // 5. Add Schedule Button
                Button(
                    onClick = onNavigateToAddSchedule,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPink),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.pmk_reminders_btn_add),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

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
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = onCloseInfoDialog) {
                    Text(
                        text = stringResource(R.string.alarm_btn_understand),
                        color = BrandPink,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            },
            containerColor = White,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun ReminderCardItem(
    reminder: PmkReminder,
    onToggle: () -> Unit,
    onEdit: () -> Unit
) {
    val isPagi = reminder.timeHour in 4..11
    val isSiang = reminder.timeHour in 12..17

    val iconBg = when {
        isPagi -> Color(0xFFFFF0F2)
        isSiang -> Color(0xFFFEF3C7)
        else -> Color(0xFFEEF2FF)
    }
    val iconTint = when {
        isPagi -> BrandPink
        isSiang -> Color(0xFFD97706)
        else -> Color(0xFF6366F1)
    }
    val badgeBg = when {
        isPagi -> Color(0xFFFFF0F2)
        isSiang -> Color(0xFFFEF3C7)
        else -> Color(0xFFEEF2FF)
    }
    val badgeText = when {
        isPagi -> BrandPink
        isSiang -> Color(0xFFD97706)
        else -> Color(0xFF4F46E5)
    }
    val badgeLabel = when {
        isPagi -> "Pagi"
        isSiang -> "Siang"
        else -> "Malam"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
            .border(1.dp, Color(0xFFEFE5DE), RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = White),
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
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = reminder.formattedTime,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (reminder.isEnabled) TextPrimary else TextSecondary.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = badgeBg,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = badgeLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = badgeText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = reminder.label.ifBlank { "Estafet Sesi $badgeLabel" },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF475569)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = stringResource(
                            if (reminder.repeatDaily) R.string.pmk_reminders_item_target_hours
                            else R.string.pmk_reminders_item_target_hours_once,
                            reminder.targetHours
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = badgeText
                    )
                }
            }

            // Custom Styled Switch (Green track, Blue thumb with check icon as seen in Stitch image 2)
            Switch(
                checked = reminder.isEnabled,
                onCheckedChange = { onToggle() },
                thumbContent = if (reminder.isEnabled) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                            tint = White
                        )
                    }
                } else null,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF2563EB),
                    checkedTrackColor = Color(0xFF10B981),
                    uncheckedThumbColor = White,
                    uncheckedTrackColor = Color(0xFFE2E8F0)
                )
            )
        }
    }
}
