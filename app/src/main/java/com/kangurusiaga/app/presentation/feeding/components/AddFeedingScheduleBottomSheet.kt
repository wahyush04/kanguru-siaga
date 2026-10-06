package com.kangurusiaga.app.presentation.feeding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.TextPrimary
import com.kangurusiaga.app.core.designsystem.theme.White
import com.kangurusiaga.app.domain.model.FeedingMethod
import com.kangurusiaga.app.domain.model.RepeatType
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFeedingScheduleBottomSheet(
    sheetState: SheetState,
    onSave: (
        hour: Int,
        minute: Int,
        volumeMl: Int,
        method: FeedingMethod,
        note: String?,
        reminderEnabled: Boolean,
        repeatType: RepeatType
    ) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 4.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.cardBorder)
            )
        }
    ) {
        AddFeedingScheduleForm(
            onSave = onSave,
            onDismiss = onDismiss
        )
    }
}

@Composable
fun AddFeedingScheduleForm(
    onSave: (
        hour: Int,
        minute: Int,
        volumeMl: Int,
        method: FeedingMethod,
        note: String?,
        reminderEnabled: Boolean,
        repeatType: RepeatType
    ) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedHour by remember { mutableIntStateOf(14) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    var volumeText by remember { mutableStateOf("30") }
    var noteText by remember { mutableStateOf("") }
    var reminderEnabled by remember { mutableStateOf(true) }
    var selectedRepeatType by remember { mutableStateOf(RepeatType.DAILY) }

    var showTimePicker by remember { mutableStateOf(false) }
    var showRepeatMenu by remember { mutableStateOf(false) }
    var volumeError by remember { mutableStateOf<String?>(null) }

    if (showTimePicker) {
        FeedingTimePickerDialog(
            initialHour = selectedHour,
            initialMinute = selectedMinute,
            onTimeSelected = { h, m ->
                selectedHour = h
                selectedMinute = m
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        // Title
        Text(
            text = stringResource(R.string.alarm_add_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 18.dp)
        )

        // Field: Waktu
        Text(
            text = stringResource(R.string.alarm_label_time),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.cardBorder, RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable { showTimePicker = true }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = stringResource(R.string.alarm_cd_select_time),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Field: Jumlah ASI
        Text(
            text = stringResource(R.string.alarm_label_volume),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = volumeText,
            onValueChange = { input ->
                if (input.all { it.isDigit() } && input.length <= 4) {
                    volumeText = input
                    volumeError = null
                }
            },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            trailingIcon = {
                Text(
                    text = stringResource(R.string.alarm_unit_ml),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(end = 16.dp)
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            isError = volumeError != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandPink,
                unfocusedBorderColor = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.cardBorder,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )
        if (volumeError != null) {
            Text(
                text = volumeError ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Field: Catatan (Opsional)
        Text(
            text = stringResource(R.string.alarm_label_note),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = noteText,
            onValueChange = { noteText = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            placeholder = {
                Text(
                    text = stringResource(R.string.alarm_placeholder_note),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandPink,
                unfocusedBorderColor = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.cardBorder,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Row: Aktifkan Pengingat
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.alarm_label_toggle_reminder),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                lineHeight = 18.sp
            )

            Switch(
                checked = reminderEnabled,
                onCheckedChange = { reminderEnabled = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = White,
                    checkedTrackColor = Color(0xFF10B981),
                    uncheckedThumbColor = White,
                    uncheckedTrackColor = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.cardBorder
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Row: Ulangi
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.alarm_label_repeat),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Box {
                Row(
                    modifier = Modifier
                        .clickable { showRepeatMenu = true }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedRepeatType.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded = showRepeatMenu,
                    onDismissRequest = { showRepeatMenu = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    RepeatType.entries.forEach { repeat ->
                        DropdownMenuItem(
                            text = { Text(repeat.displayName) },
                            onClick = {
                                selectedRepeatType = repeat
                                showRepeatMenu = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Bottom Actions: Batal & Simpan Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD1D8)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = BrandPink
                )
            ) {
                Text(
                    text = stringResource(R.string.alarm_btn_cancel),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandPink
                )
            }

            val errorMsg = stringResource(R.string.alarm_error_volume)
            Button(
                onClick = {
                    val volume = volumeText.toIntOrNull()
                    if (volume == null || volume <= 0) {
                        volumeError = errorMsg
                        return@Button
                    }
                    onSave(
                        selectedHour,
                        selectedMinute,
                        volume,
                        FeedingMethod.OGT_NGT,
                        noteText,
                        reminderEnabled,
                        selectedRepeatType
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandPink,
                    contentColor = White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = stringResource(R.string.alarm_btn_save),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
