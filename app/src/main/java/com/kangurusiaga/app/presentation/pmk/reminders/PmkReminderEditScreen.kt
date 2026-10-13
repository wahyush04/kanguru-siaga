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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@Composable
fun PmkReminderEditRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PmkReminderAddEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved, uiState.isDeleted) {
        if (uiState.isSaved || uiState.isDeleted) {
            onNavigateBack()
        }
    }

    PmkReminderEditScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onTimeSelected = viewModel::setTime,
        onDurationSelected = viewModel::setDurationHours,
        onCategorySelected = viewModel::setCategory,
        onNotesChanged = viewModel::setNotes,
        onToggleEnabled = viewModel::setIsEnabled,
        onToggleRepeat = viewModel::setRepeatDaily,
        onOpenDeleteDialog = viewModel::openDeleteDialog,
        onCloseDeleteDialog = viewModel::closeDeleteDialog,
        onConfirmDelete = viewModel::deleteSchedule,
        onSave = viewModel::saveSchedule,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PmkReminderEditScreen(
    uiState: PmkReminderAddEditUiState,
    onNavigateBack: () -> Unit,
    onTimeSelected: (Int, Int) -> Unit,
    onDurationSelected: (Int) -> Unit,
    onCategorySelected: (String) -> Unit,
    onNotesChanged: (String) -> Unit,
    onToggleEnabled: (Boolean) -> Unit,
    onToggleRepeat: (Boolean) -> Unit,
    onOpenDeleteDialog: () -> Unit,
    onCloseDeleteDialog: () -> Unit,
    onConfirmDelete: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showTimePickerDialog by remember { mutableStateOf(false) }

    val durationOptions = listOf(2, 3, 4, 6, 8)
    val categoryOptionsRow1 = listOf("Pagi", "Siang", "Sore")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandBackground,
        topBar = {
            Surface(
                color = BrandBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
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
                        text = stringResource(R.string.pmk_reminder_edit_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onOpenDeleteDialog,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = stringResource(R.string.pmk_reminder_btn_delete),
                            tint = Color(0xFFEF4444)
                        )
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = BrandBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Tombol: Hapus Jadwal Ini (Outline Pink/Merah)
                    Button(
                        onClick = onOpenDeleteDialog,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFF0F2),
                            contentColor = Color(0xFFEF4444)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECDD3)),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.pmk_reminder_btn_delete),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                    }

                    // Baris: Batal & Simpan Perubahan
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF1F5F9),
                                contentColor = Color(0xFF334155)
                            ),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.pmk_reminder_btn_cancel),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF334155)
                            )
                        }

                        Button(
                            onClick = onSave,
                            enabled = !uiState.isLoading,
                            modifier = Modifier
                                .weight(1.5f)
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandPink,
                                contentColor = White
                            ),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.pmk_reminder_btn_save_changes),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Clinical Guideline Banner
            Surface(
                color = KanguruTheme.colors.categoryRoseContainer,
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, KanguruTheme.colors.categoryRoseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = BrandPink,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = stringResource(R.string.pmk_reminder_banner_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        val guidelineText = buildAnnotatedString {
                            append("Dianjurkan kontak kulit dilakukan tanpa putus secara estafet (Ibu, Ayah & Pendamping) dengan target kumulatif harian ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFE11D48))) {
                                append("minimal ≥8 jam")
                            }
                            append(" dan ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFE11D48))) {
                                append("ideal ≥20 jam/hari")
                            }
                            append("(Standar Kemenkes/WHO/IDAI).")
                        }
                        Text(
                            text = guidelineText,
                            style = MaterialTheme.typography.bodySmall,
                            color = KanguruTheme.colors.categoryRoseText,
                            lineHeight = MaterialTheme.typography.bodySmall.lineHeight
                        )
                    }
                }
            }

            // 2. Field Waktu Mulai Estafet / Sesi
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.pmk_reminder_field_start_time),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTimePickerDialog = true }
                        .border(1.dp, CardBorder, RoundedCornerShape(18.dp)),
                    colors = CardDefaults.cardColors(containerColor = White),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.formattedTime,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // 3. Field Target Durasi Sesi
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = KanguruTheme.colors.categoryRoseContainer,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.pmk_reminder_badge_min_session),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandPink,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorder, RoundedCornerShape(18.dp)),
                    colors = CardDefaults.cardColors(containerColor = White),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${uiState.durationHours}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Text(
                            text = stringResource(R.string.pmk_reminder_unit_hours),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                    }
                }

                // Quick duration chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    durationOptions.forEach { hours ->
                        val isSelected = uiState.durationHours == hours
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) KanguruTheme.colors.categoryRoseContainer else White)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) BrandPink else CardBorder,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { onDurationSelected(hours) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$hours Jam",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) BrandPink else TextSecondary
                            )
                        }
                    }
                }
            }

            // 4. Field Kategori Sesi / Giliran Estafet
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.pmk_reminder_field_category),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = stringResource(R.string.pmk_reminder_category_hint),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categoryOptionsRow1.forEach { cat ->
                        val isSelected = uiState.category == cat
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) KanguruTheme.colors.categoryRoseContainer else White)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) BrandPink else CardBorder,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { onCategorySelected(cat) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) BrandPink else TextPrimary
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val malamSelected = uiState.category == "Malam"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (malamSelected) KanguruTheme.colors.categoryRoseContainer else White)
                            .border(
                                width = if (malamSelected) 1.5.dp else 1.dp,
                                color = if (malamSelected) BrandPink else CardBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onCategorySelected("Malam") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Malam",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (malamSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (malamSelected) BrandPink else TextPrimary
                        )
                    }

                    val diniSelected = uiState.category.startsWith("Dini")
                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (diniSelected) KanguruTheme.colors.categoryRoseContainer else White)
                            .border(
                                width = if (diniSelected) 1.5.dp else 1.dp,
                                color = if (diniSelected) BrandPink else CardBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onCategorySelected("Dini Hari / Tidur (Estafet Pendamping)") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Dini Hari / Tidur (Estafet Pendamping)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (diniSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (diniSelected) BrandPink else TextPrimary,
                            maxLines = 1
                        )
                    }
                }
            }

            // 5. Catatan / Rencana Pendamping (Opsional)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.pmk_reminder_field_notes),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                OutlinedTextField(
                    value = uiState.notes,
                    onValueChange = onNotesChanged,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.pmk_reminder_notes_placeholder),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        focusedBorderColor = BrandPink,
                        unfocusedBorderColor = CardBorder
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
                )
            }

            // 6. Switch: Aktifkan Pengingat Estafet
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = White),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.pmk_reminder_toggle_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.pmk_reminder_toggle_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Switch(
                        checked = uiState.isEnabled,
                        onCheckedChange = onToggleEnabled,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = White,
                            checkedTrackColor = Color(0xFF10B981),
                            uncheckedThumbColor = White,
                            uncheckedTrackColor = Color(0xFFE2E8F0)
                        )
                    )
                }
            }

            // 7. Ulangi Jadwal Card
            var showRepeatDropdown by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showRepeatDropdown = true }
                    .border(1.dp, CardBorder, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = White),
                shape = RoundedCornerShape(18.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.pmk_reminder_field_repeat),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (uiState.repeatDaily) {
                                    stringResource(R.string.pmk_reminder_repeat_daily)
                                } else {
                                    stringResource(R.string.pmk_reminder_repeat_once)
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showRepeatDropdown,
                        onDismissRequest = { showRepeatDropdown = false },
                        modifier = Modifier.background(White)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.pmk_reminder_repeat_daily),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (uiState.repeatDaily) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (uiState.repeatDaily) BrandPink else TextPrimary
                                )
                            },
                            trailingIcon = if (uiState.repeatDaily) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = BrandPink,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else null,
                            onClick = {
                                onToggleRepeat(true)
                                showRepeatDropdown = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.pmk_reminder_repeat_once),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (!uiState.repeatDaily) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (!uiState.repeatDaily) BrandPink else TextPrimary
                                )
                            },
                            trailingIcon = if (!uiState.repeatDaily) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = BrandPink,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else null,
                            onClick = {
                                onToggleRepeat(false)
                                showRepeatDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Modal Konfirmasi Hapus Jadwal
    if (uiState.showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = onCloseDeleteDialog,
            icon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(KanguruTheme.colors.errorContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(R.string.pmk_reminder_dialog_delete_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = stringResource(
                        R.string.pmk_reminder_dialog_delete_msg,
                        uiState.formattedTime,
                        uiState.notes.ifBlank { "Estafet Sesi ${uiState.category}" }
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = MaterialTheme.typography.bodySmall.lineHeight
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirmDelete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444),
                        contentColor = White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.pmk_reminder_dialog_delete_confirm),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onCloseDeleteDialog) {
                    Text(
                        text = stringResource(R.string.pmk_reminder_btn_cancel),
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                }
            },
            containerColor = White,
            shape = RoundedCornerShape(24.dp)
        )
    }

    if (showTimePickerDialog) {
        val timePickerState = rememberTimePickerState(
            initialHour = uiState.timeHour,
            initialMinute = uiState.timeMinute,
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = { showTimePickerDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.pmk_reminder_field_start_time),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TimePicker(
                        state = timePickerState,
                        colors = androidx.compose.material3.TimePickerDefaults.colors(
                            clockDialColor = KanguruTheme.colors.surfaceVariant,
                            clockDialSelectedContentColor = White,
                            clockDialUnselectedContentColor = TextPrimary,
                            selectorColor = BrandPink,
                            containerColor = White,
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
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onTimeSelected(timePickerState.hour, timePickerState.minute)
                        showTimePickerDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPink),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.alarm_btn_save),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePickerDialog = false }) {
                    Text(
                        text = stringResource(R.string.alarm_btn_cancel),
                        style = MaterialTheme.typography.labelLarge,
                        color = TextSecondary
                    )
                }
            },
            containerColor = White,
            shape = RoundedCornerShape(24.dp)
        )
    }
}
