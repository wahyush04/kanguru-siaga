package com.kangurusiaga.app.presentation.growth.add

import android.app.DatePickerDialog
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.BrandBackground
import com.kangurusiaga.app.core.designsystem.theme.BrandCardBorder
import com.kangurusiaga.app.core.designsystem.theme.BrandLightPink
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.GrowthTipBg
import com.kangurusiaga.app.core.designsystem.theme.GrowthTipBorder
import com.kangurusiaga.app.core.designsystem.theme.GrowthTipText
import com.kangurusiaga.app.core.designsystem.theme.TextPrimary
import com.kangurusiaga.app.core.designsystem.theme.TextSecondary
import com.kangurusiaga.app.core.designsystem.theme.TextTertiary
import com.kangurusiaga.app.core.designsystem.theme.White
import com.kangurusiaga.app.domain.model.GrowthParameter
import com.kangurusiaga.app.presentation.growth.components.GrowthIcons
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AddGrowthMeasurementScreen(
    onNavigateBack: () -> Unit,
    onSavedSuccessfully: () -> Unit,
    viewModel: AddGrowthMeasurementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val parameter = uiState.parameter

    val dateFormatter = remember { SimpleDateFormat("d MMMM yyyy", Locale("id", "ID")) }

    // Date picker dialog
    val calendar = remember { Calendar.getInstance() }
    val datePickerDialog = remember(context) {
        calendar.timeInMillis = uiState.measurementDateEpochMillis
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newCal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 12, 0, 0)
                }
                viewModel.updateDate(newCal.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
        }
    }

    Scaffold(
        containerColor = BrandBackground,
        bottomBar = {
            // Footer Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.surface)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateBack,
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.surface,
                            contentColor = BrandPink
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(BrandPink),
                            width = 1.2.dp
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.growth_add_btn_cancel),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BrandPink
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.saveMeasurement(onSuccess = onSavedSuccessfully)
                        },
                        enabled = !uiState.isSaving,
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPink),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                color = White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.growth_add_btn_save),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
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
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            // Top App Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = GrowthIcons.Back,
                        contentDescription = stringResource(R.string.common_back),
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = stringResource(R.string.growth_add_title, parameter.displayName),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.size(36.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Field 1: Tanggal Pengukuran
            Text(
                text = stringResource(R.string.growth_add_label_date),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.surfaceElevated)
                    .border(1.dp, com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.cardBorder, RoundedCornerShape(12.dp))
                    .clickable { datePickerDialog.show() }
                    .padding(horizontal = 14.dp, vertical = 13.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateFormatter.format(uiState.measurementDateEpochMillis),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Icon(
                    imageVector = GrowthIcons.Calendar,
                    contentDescription = null,
                    tint = TextTertiary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Field 2: Usia Kronologis
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.growth_add_label_chronological),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BrandLightPink)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = stringResource(R.string.growth_add_tag_auto),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandPink
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.surfaceElevated)
                    .border(1.dp, com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.cardBorder, RoundedCornerShape(12.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiState.chronologicalAgeDisplay,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 14.dp, vertical = 13.dp)
                )
                Box(
                    modifier = Modifier
                        .background(com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.surfaceElevated)
                        .padding(horizontal = 16.dp, vertical = 13.dp)
                ) {
                    Text(
                        text = stringResource(R.string.growth_add_unit_weeks),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                }
            }
            Text(
                text = stringResource(R.string.growth_add_chronological_desc),
                fontSize = 11.sp,
                color = TextTertiary,
                modifier = Modifier.padding(top = 4.dp, start = 2.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Field 3: Usia Gestasi Pasca-Menstruasi (PMA)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.growth_add_label_pma),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = GrowthIcons.Help,
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BrandLightPink)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = stringResource(R.string.growth_add_tag_auto),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandPink
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.surfaceElevated)
                    .border(1.dp, com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.cardBorder, RoundedCornerShape(12.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiState.pmaDisplay,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 14.dp, vertical = 13.dp)
                )
                Box(
                    modifier = Modifier
                        .background(com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.surfaceElevated)
                        .padding(horizontal = 16.dp, vertical = 13.dp)
                ) {
                    Text(
                        text = stringResource(R.string.growth_add_unit_weeks),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                }
            }
            Text(
                text = stringResource(R.string.growth_add_pma_desc),
                fontSize = 11.sp,
                color = TextTertiary,
                modifier = Modifier.padding(top = 4.dp, start = 2.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Field 4: Parameter Value Input
            Text(
                text = parameter.displayName,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = uiState.valueText,
                onValueChange = { viewModel.updateValue(it) },
                placeholder = {
                    Text(
                        text = when (parameter) {
                            GrowthParameter.WEIGHT -> stringResource(R.string.growth_add_placeholder_weight)
                            GrowthParameter.LENGTH -> stringResource(R.string.growth_add_placeholder_length)
                            GrowthParameter.HEAD_CIRCUMFERENCE -> stringResource(R.string.growth_add_placeholder_head)
                        },
                        fontSize = 13.sp,
                        color = TextTertiary
                    )
                },
                trailingIcon = {
                    Box(
                        modifier = Modifier
                            .background(com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.surfaceElevated)
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Text(
                            text = parameter.unit,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandPink,
                    unfocusedBorderColor = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.cardBorder,
                    focusedContainerColor = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.surface,
                    unfocusedContainerColor = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.surfaceElevated
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage ?: "",
                    fontSize = 11.5.sp,
                    color = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.errorText,
                    modifier = Modifier.padding(top = 4.dp, start = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Field 5: Catatan (Opsional)
            Text(
                text = stringResource(R.string.growth_add_label_note),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = uiState.noteText,
                onValueChange = { viewModel.updateNote(it) },
                placeholder = {
                    Text(
                        text = stringResource(R.string.growth_add_placeholder_note),
                        fontSize = 13.sp,
                        color = TextTertiary
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandPink,
                    unfocusedBorderColor = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.cardBorder,
                    focusedContainerColor = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.surface,
                    unfocusedContainerColor = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.surfaceElevated
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Reminder Option Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.surface)
                    .border(1.dp, BrandCardBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(BrandLightPink),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = GrowthIcons.Reminder,
                            contentDescription = null,
                            tint = BrandPink,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = stringResource(R.string.growth_add_reminder_title),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        val nextDateFormatted = dateFormatter.format(uiState.nextReminderDateEpochMillis)
                        Text(
                            text = stringResource(R.string.growth_add_reminder_desc, nextDateFormatted),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = TextTertiary,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Switch(
                    checked = uiState.reminderEnabled,
                    onCheckedChange = { viewModel.toggleReminder(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = White,
                        checkedTrackColor = BrandPink,
                        uncheckedThumbColor = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.outline,
                        uncheckedTrackColor = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.divider
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Medical Tip Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(GrowthTipBg)
                    .border(1.dp, GrowthTipBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = GrowthIcons.Lightbulb,
                    contentDescription = null,
                    tint = com.kangurusiaga.app.core.designsystem.theme.KanguruTheme.colors.warningText,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(top = 1.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                val tipText = when (parameter) {
                    GrowthParameter.WEIGHT -> stringResource(R.string.growth_add_tip_weight)
                    GrowthParameter.LENGTH -> stringResource(R.string.growth_add_tip_length)
                    GrowthParameter.HEAD_CIRCUMFERENCE -> stringResource(R.string.growth_add_tip_head)
                }
                Text(
                    text = tipText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = GrowthTipText,
                    lineHeight = 17.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
