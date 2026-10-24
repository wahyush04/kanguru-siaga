package com.kangurusiaga.app.presentation.growth.batch

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.component.KanguruSnackbarHost
import com.kangurusiaga.app.core.designsystem.theme.BrandBackground
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.core.designsystem.theme.White
import com.kangurusiaga.app.presentation.growth.batch.components.GrowthAccuracyCard
import com.kangurusiaga.app.presentation.growth.batch.components.GrowthBabySummaryCard
import com.kangurusiaga.app.presentation.growth.batch.components.GrowthBatchMetadataCard
import com.kangurusiaga.app.presentation.growth.batch.components.GrowthBatchTable
import com.kangurusiaga.app.presentation.growth.components.GrowthIcons
import java.util.Calendar

/**
 * Screen implementation for "Catat Pengukuran Baru (Batch Input Fenton)"
 * Single Source of Truth: Google Stitch Screen ID 41abf5181c9c46e3aa16074c795fe9b7
 */
@Composable
fun GrowthBatchMeasurementScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAboutFenton: () -> Unit,
    onSavedSuccessfully: () -> Unit,
    viewModel: GrowthBatchMeasurementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { errorMsg ->
            snackbarHostState.showSnackbar(errorMsg)
        }
    }

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
        snackbarHost = { KanguruSnackbarHost(snackbarHostState) },
        topBar = {
            // Sticky Top Bar matching Stitch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(KanguruTheme.colors.surface)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = GrowthIcons.Back,
                        contentDescription = stringResource(R.string.common_back),
                        tint = KanguruTheme.colors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.growth_batch_title),
                        style = KanguruTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = KanguruTheme.colors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.growth_batch_subtitle),
                        style = KanguruTheme.typography.labelSmall,
                        color = KanguruTheme.colors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                }

                IconButton(
                    onClick = onNavigateToAboutFenton,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = GrowthIcons.Help,
                        contentDescription = stringResource(R.string.growth_batch_btn_help),
                        tint = KanguruTheme.colors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        bottomBar = {
            // Action Buttons Group at Bottom
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(KanguruTheme.colors.surface)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Primary Save & Plot Button
                Button(
                    onClick = {
                        viewModel.saveBatchMeasurements(onSuccess = onSavedSuccessfully)
                    },
                    enabled = !uiState.isSaving,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KanguruTheme.colors.primary,
                        contentColor = White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp)
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            color = White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "+",
                                style = KanguruTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                            Text(
                                text = stringResource(R.string.growth_batch_btn_save),
                                style = KanguruTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                        }
                    }
                }

                // Secondary Cancel Button
                OutlinedButton(
                    onClick = onNavigateBack,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = KanguruTheme.colors.surface,
                        contentColor = KanguruTheme.colors.textSecondary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(KanguruTheme.colors.divider),
                        width = 1.dp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 46.dp)
                ) {
                    Text(
                        text = stringResource(R.string.growth_batch_btn_cancel),
                        style = KanguruTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = KanguruTheme.colors.textSecondary
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Baby Quick Summary Banner Card
            GrowthBabySummaryCard(
                baby = uiState.baby,
                currentPmaWeeks = uiState.currentPmaWeeks
            )

            // 2. Multi-Baris Fenton Measurements Table
            GrowthBatchTable(
                rows = uiState.rows,
                onFieldChange = { rowId, field, value ->
                    viewModel.updateField(rowId, field, value)
                },
                onDeleteRow = { rowId ->
                    viewModel.deleteRow(rowId)
                },
                onAddRow = {
                    viewModel.addRow()
                }
            )

            // 3. Clinical Metadata Form (Date, Switch, Notes)
            GrowthBatchMetadataCard(
                dateEpochMillis = uiState.measurementDateEpochMillis,
                onDateClick = { datePickerDialog.show() },
                reminderEnabled = uiState.reminderEnabled,
                onReminderToggle = { viewModel.toggleReminder(it) },
                clinicalNotes = uiState.clinicalNotes,
                onNotesChange = { viewModel.updateNotes(it) }
            )

            // 4. Clinical Accuracy Guidance Card
            GrowthAccuracyCard()

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
