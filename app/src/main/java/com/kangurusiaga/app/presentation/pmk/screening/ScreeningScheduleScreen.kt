package com.kangurusiaga.app.presentation.pmk.screening

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Verified
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.domain.model.PrematureScreening
import com.kangurusiaga.app.domain.model.ScreeningType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ScreeningScheduleRoute(
    onNavigateBack: () -> Unit,
    onNavigateToInputResult: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScreeningScheduleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    ScreeningScheduleScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNavigateToInputResult = onNavigateToInputResult,
        onSelectTab = viewModel::selectTab,
        onToggleAlarm = viewModel::toggleAlarm,
        onOpenRecordModal = viewModel::openRecordModal,
        onCloseRecordModal = viewModel::closeRecordModal,
        onUpdateRecordRealizationDate = viewModel::updateRecordRealizationDate,
        onUpdateRecordHospital = viewModel::updateRecordHospital,
        onUpdateRecordDoctor = viewModel::updateRecordDoctor,
        onUpdateRecordResult = viewModel::updateRecordResult,
        onUpdateRecordCompleted = viewModel::updateRecordCompleted,
        onSaveRecord = viewModel::saveScreeningResult,
        onOpenAddModal = viewModel::openAddModal,
        onCloseAddModal = viewModel::closeAddModal,
        onUpdateAddTitle = viewModel::updateAddTitle,
        onUpdateAddCategory = viewModel::updateAddCategory,
        onUpdateAddTiming = viewModel::updateAddTiming,
        onUpdateAddTargetDate = viewModel::updateAddTargetDate,
        onUpdateAddIndication = viewModel::updateAddIndication,
        onUpdateAddGoal = viewModel::updateAddGoal,
        onUpdateAddClinic = viewModel::updateAddClinic,
        onSaveAdd = viewModel::saveNewScreening,
        onSetInfoDialogVisible = viewModel::setInfoDialogVisible,
        onDownloadPdf = viewModel::downloadPdfSummary,
        modifier = modifier,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun ScreeningScheduleScreen(
    uiState: ScreeningScheduleUiState,
    onNavigateBack: () -> Unit,
    onNavigateToInputResult: (String) -> Unit,
    onSelectTab: (ScreeningTab) -> Unit,
    onToggleAlarm: (PrematureScreening, Boolean) -> Unit,
    onOpenRecordModal: (PrematureScreening) -> Unit,
    onCloseRecordModal: () -> Unit,
    onUpdateRecordRealizationDate: (Long) -> Unit,
    onUpdateRecordHospital: (String) -> Unit,
    onUpdateRecordDoctor: (String) -> Unit,
    onUpdateRecordResult: (String) -> Unit,
    onUpdateRecordCompleted: (Boolean) -> Unit,
    onSaveRecord: () -> Unit,
    onOpenAddModal: () -> Unit,
    onCloseAddModal: () -> Unit,
    onUpdateAddTitle: (String) -> Unit,
    onUpdateAddCategory: (String) -> Unit,
    onUpdateAddTiming: (String) -> Unit,
    onUpdateAddTargetDate: (Long) -> Unit,
    onUpdateAddIndication: (String) -> Unit,
    onUpdateAddGoal: (String) -> Unit,
    onUpdateAddClinic: (String) -> Unit,
    onSaveAdd: () -> Unit,
    onSetInfoDialogVisible: (Boolean) -> Unit,
    onDownloadPdf: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val colors = KanguruTheme.colors

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(
                color = colors.background,
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
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(colors.surface)
                            .border(1.dp, colors.cardBorder, CircleShape)
                            .clickable { onNavigateBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = colors.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Jadwal & Evaluasi Penapisan",
                            style = KanguruTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(colors.primary)
                            )
                            Text(
                                text = "Kanguru Siaga Rawat Jalan",
                                style = KanguruTheme.typography.labelSmall,
                                color = colors.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(colors.surface)
                            .border(1.dp, colors.cardBorder, CircleShape)
                            .clickable { onSetInfoDialogVisible(true) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Informasi Medis",
                            tint = colors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = colors.surface,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDownloadPdf,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Unduh PDF Rekap",
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Button(
                        onClick = onOpenAddModal,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary,
                            contentColor = colors.onPrimary
                        ),
                        modifier = Modifier
                            .weight(1.1f)
                            .height(48.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Tambah Jadwal",
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
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
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Card dependent on Tab:
            // Tab 1: HeroScreeningCard
            // Tab 2: PatientSummaryMedicalCard (Matching Google Stitch Lembar Medis Screen)
            if (uiState.selectedTab == ScreeningTab.REMINDERS) {
                HeroScreeningCard(
                    uiState = uiState,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                PatientSummaryMedicalCard(
                    uiState = uiState,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Segmented Switcher Tabs (Jadwal & Alarm vs Hasil & Lembar Medis)
            SegmentedScreeningTabs(
                selectedTab = uiState.selectedTab,
                onSelectTab = onSelectTab,
                modifier = Modifier.fillMaxWidth()
            )

            // Tab Content
            if (uiState.selectedTab == ScreeningTab.REMINDERS) {
                RemindersView(
                    screenings = uiState.screenings,
                    onToggleAlarm = onToggleAlarm,
                    onNavigateToInputResult = onNavigateToInputResult,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                EvaluationMedicalSheetView(
                    screenings = uiState.screenings,
                    onNavigateToInputResult = onNavigateToInputResult,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Modal Tambah Jadwal Baru
    if (uiState.addModalState.isOpen) {
        AddScreeningDialog(
            state = uiState.addModalState,
            onClose = onCloseAddModal,
            onTitleChanged = onUpdateAddTitle,
            onCategoryChanged = onUpdateAddCategory,
            onTimingChanged = onUpdateAddTiming,
            onTargetDateChanged = onUpdateAddTargetDate,
            onIndicationChanged = onUpdateAddIndication,
            onGoalChanged = onUpdateAddGoal,
            onClinicChanged = onUpdateAddClinic,
            onSave = onSaveAdd
        )
    }

    // Info Dialog Pedoman Medis
    if (uiState.infoDialogVisible) {
        AlertDialog(
            onDismissRequest = { onSetInfoDialogVisible(false) },
            confirmButton = {
                TextButton(onClick = { onSetInfoDialogVisible(false) }) {
                    Text(text = "Mengerti", color = colors.primary, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text(
                    text = "Pedoman Penapisan Bayi Prematur",
                    style = KanguruTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            },
            text = {
                Text(
                    text = "Bayi berat lahir rendah (BBLR) dan prematur membutuhkan evaluasi berkala untuk pemantauan fungsi organ vital: USG Kepala (Neurosonografi), Retinopati Prematuritas (ROP), Gangguan Pendengaran (OAE & BERA), dan Fungsi Tiroid (SHK & FT4/TSH). Pasang pengingat otomatis dan pastikan hasil tercatat bersama dokter spesialis.",
                    style = KanguruTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            },
            containerColor = colors.cardBackground
        )
    }
}

/**
 * Tab 1: Hero Screening Card
 */
@Composable
private fun HeroScreeningCard(
    uiState: ScreeningScheduleUiState,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.clinicalBlueBorder),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            colors.clinicalBlueBg,
                            colors.surface,
                            colors.clinicalRoseBg.copy(alpha = 0.5f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.clinicalBlueText)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "PEDOMAN KLINIS",
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = colors.onPrimary
                            )
                        }
                        Text(
                            text = "BBLR / Prematur",
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = colors.textSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.clinicalEmeraldBg)
                            .border(1.dp, colors.clinicalEmeraldBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = colors.clinicalEmeraldText,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Diisi oleh Nakes",
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.clinicalEmeraldText
                            )
                        }
                    }
                }

                Text(
                    text = "Penapisan Bayi Prematur Saat Rawat Jalan",
                    style = KanguruTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )

                Text(
                    text = "Bayi prematur membutuhkan evaluasi berkala untuk pemantauan fungsi organ vital. Pasang pengingat otomatis dan pastikan hasil tercatat bersama dokter spesialis.",
                    style = KanguruTheme.typography.bodySmall,
                    color = colors.textSecondary
                )

                HorizontalDivider(color = colors.clinicalBlueBorder.copy(alpha = 0.6f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(colors.clinicalAmberBg)
                                .border(1.dp, colors.clinicalAmberBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "👶", style = KanguruTheme.typography.bodyMedium)
                        }
                        Column {
                            Text(
                                text = "${uiState.baby?.name ?: "By. Ny. Arini"} (${uiState.babyAgeDisplay})",
                                style = KanguruTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "Koreksi: ${uiState.correctedAgeDisplay} | Lahir: ${uiState.birthDateDisplay}",
                                style = KanguruTheme.typography.labelSmall,
                                color = colors.textSecondary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.clinicalEmeraldBg)
                            .border(1.dp, colors.clinicalEmeraldBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Rawat Jalan Aktif",
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.clinicalEmeraldText
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tab 2: Patient Summary Card with BBLR Metrics & Completion Progress Bar
 * Directly matching Google Stitch Screen "Lembar Medis & Hasil Penapisan Bayi Prematur"
 */
@Composable
private fun PatientSummaryMedicalCard(
    uiState: ScreeningScheduleUiState,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Patient Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.clinicalAmberBg)
                            .border(1.dp, colors.clinicalAmberBorder, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👶", style = KanguruTheme.typography.titleMedium)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = uiState.baby?.name ?: "By. Ny. Arini",
                                style = KanguruTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(colors.primaryContainer)
                                    .border(1.dp, colors.clinicalRoseBorder, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${uiState.baby?.gestationalAgeWeeks ?: 31} Mgg",
                                    style = KanguruTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.primary
                                )
                            }
                        }

                        Text(
                            text = "Koreksi: ${uiState.correctedAgeDisplay} | Lahir: ${uiState.birthDateDisplay}",
                            style = KanguruTheme.typography.labelSmall,
                            color = colors.textSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.clinicalEmeraldBg)
                        .border(1.dp, colors.clinicalEmeraldBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Rawat Jalan Aktif",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.clinicalEmeraldText
                    )
                }
            }

            HorizontalDivider(color = colors.divider.copy(alpha = 0.6f))

            // Metric Strip (2 Columns)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.background)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(14.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "Berat Lahir (BBLR)",
                            style = KanguruTheme.typography.labelSmall,
                            color = colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiState.birthWeightDisplay,
                            style = KanguruTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.background)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(14.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "Berat Terakhir (Kini)",
                            style = KanguruTheme.typography.labelSmall,
                            color = colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = uiState.currentWeightDisplay,
                                style = KanguruTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.clinicalEmeraldText
                            )
                            Text(
                                text = uiState.weightGainDisplay,
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.clinicalEmeraldSolid
                            )
                        }
                    }
                }
            }

            // Completion Progress Bar
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = colors.clinicalEmeraldText,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Kelengkapan Lembar Medis",
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = colors.textSecondary
                        )
                    }

                    Text(
                        text = "${uiState.completedScreeningsCount} dari ${uiState.totalScreeningsCount} Selesai (${uiState.completionPercentage}%)",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.clinicalEmeraldText
                    )
                }

                // Gradient Progress Track
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(colors.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = (uiState.completionPercentage / 100f).coerceIn(0.05f, 1f))
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(colors.clinicalEmeraldSolid, colors.clinicalEmeraldText)
                                )
                            )
                    )
                }
            }
        }
    }
}

/**
 * Segmented Navigation Tab Switcher
 */
@Composable
private fun SegmentedScreeningTabs(
    selectedTab: ScreeningTab,
    onSelectTab: (ScreeningTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surfaceVariant)
            .padding(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selectedTab == ScreeningTab.REMINDERS) colors.surface else Color.Transparent)
                    .clickable { onSelectTab(ScreeningTab.REMINDERS) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = if (selectedTab == ScreeningTab.REMINDERS) colors.primary else colors.textSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Jadwal & Alarm (4)",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = if (selectedTab == ScreeningTab.REMINDERS) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == ScreeningTab.REMINDERS) colors.primary else colors.textSecondary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selectedTab == ScreeningTab.EVALUATION) colors.surface else Color.Transparent)
                    .clickable { onSelectTab(ScreeningTab.EVALUATION) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = if (selectedTab == ScreeningTab.EVALUATION) colors.primary else colors.textSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Hasil & Lembar Medis",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = if (selectedTab == ScreeningTab.EVALUATION) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == ScreeningTab.EVALUATION) colors.primary else colors.textSecondary
                    )
                }
            }
        }
    }
}

/**
 * Tab 1: Reminders View
 */
@Composable
private fun RemindersView(
    screenings: List<PrematureScreening>,
    onToggleAlarm: (PrematureScreening, Boolean) -> Unit,
    onNavigateToInputResult: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section Title Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SKRINING WAJIB BBLR",
                style = KanguruTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textSecondary
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.primaryContainer)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "Semua Alarm Aktif",
                    style = KanguruTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }
        }

        // Screening Cards
        screenings.forEach { screening ->
            ScreeningCardItem(
                screening = screening,
                onToggleAlarm = { isAlarm1 -> onToggleAlarm(screening, isAlarm1) },
                onRecordClick = { onNavigateToInputResult(screening.type.name) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ScreeningCardItem(
    screening: PrematureScreening,
    onToggleAlarm: (Boolean) -> Unit,
    onRecordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    val (badgeBg, badgeText, iconEmoji, btnColor, boxBg, boxBorder, boxText) = when (screening.type) {
        ScreeningType.USG_HEAD -> tuple7(
            colors.clinicalAmberBg, colors.clinicalAmberText, "🧠",
            colors.clinicalAmberSolid, colors.clinicalAmberBg, colors.clinicalAmberBorder, colors.clinicalAmberText
        )
        ScreeningType.ROP_EYE -> tuple7(
            colors.clinicalRoseBg, colors.clinicalRoseText, "👁️",
            colors.clinicalRoseSolid, colors.clinicalRoseBg, colors.clinicalRoseBorder, colors.clinicalRoseText
        )
        ScreeningType.HEARING_OAE -> tuple7(
            colors.clinicalEmeraldBg, colors.clinicalEmeraldText, "👂",
            colors.clinicalEmeraldSolid, colors.clinicalEmeraldBg, colors.clinicalEmeraldBorder, colors.clinicalEmeraldText
        )
        ScreeningType.THYROID_FUNCTION -> tuple7(
            colors.clinicalPurpleBg, colors.clinicalPurpleText, "🧪",
            colors.clinicalPurpleSolid, colors.clinicalPurpleBg, colors.clinicalPurpleBorder, colors.clinicalPurpleText
        )
        ScreeningType.CUSTOM -> tuple7(
            colors.primaryContainer, colors.primary, "📋",
            colors.primary, colors.surfaceVariant, colors.outline, colors.textPrimary
        )
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Icon + Badge + Title + Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(badgeBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = iconEmoji, style = KanguruTheme.typography.bodyLarge)
                    }

                    Column {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(badgeBg)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = screening.categoryBadge.uppercase(),
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = badgeText
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = screening.title,
                            style = KanguruTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }
                }

                Switch(
                    checked = screening.isAlarmActive,
                    onCheckedChange = { onToggleAlarm(true) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colors.surface,
                        checkedTrackColor = colors.primary,
                        uncheckedThumbColor = colors.surface,
                        uncheckedTrackColor = colors.outline
                    )
                )
            }

            // Timing Box (Dual Alarm for Hearing, Single Box for Others)
            if (screening.type == ScreeningType.HEARING_OAE && screening.targetDateEpoch2 != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(boxBg.copy(alpha = 0.5f))
                        .border(1.dp, boxBorder, RoundedCornerShape(14.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = boxText,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "JADWAL ALARM PENDENGARAN:",
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = boxText
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = colors.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, boxBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = "Alarm 1", style = KanguruTheme.typography.labelSmall, color = colors.textSecondary)
                                        Text(text = "2 Bln Kronologis", style = KanguruTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                                        Text(text = screening.targetDateFormatted, style = KanguruTheme.typography.labelSmall, color = boxText, fontWeight = FontWeight.SemiBold)
                                    }
                                    Checkbox(
                                        checked = screening.isAlarmActive,
                                        onCheckedChange = { onToggleAlarm(true) },
                                        colors = CheckboxDefaults.colors(checkedColor = colors.clinicalEmeraldText)
                                    )
                                }
                            }

                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = colors.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, boxBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = "Alarm 2 (Batas)", style = KanguruTheme.typography.labelSmall, color = colors.textSecondary)
                                        Text(text = "3 Bln Kronologis", style = KanguruTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                                        Text(text = screening.targetDate2Formatted, style = KanguruTheme.typography.labelSmall, color = boxText, fontWeight = FontWeight.SemiBold)
                                    }
                                    Checkbox(
                                        checked = screening.isAlarm2Active,
                                        onCheckedChange = { onToggleAlarm(false) },
                                        colors = CheckboxDefaults.colors(checkedColor = colors.clinicalEmeraldText)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(boxBg.copy(alpha = 0.7f))
                        .border(1.dp, boxBorder, RoundedCornerShape(14.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = boxText,
                                modifier = Modifier.size(16.dp)
                            )
                            Column {
                                Text(
                                    text = "PENGINGAT WAKTU SKRINING:",
                                    style = KanguruTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = boxText
                                )
                                Text(
                                    text = screening.targetTimingLabel,
                                    style = KanguruTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.surface)
                                .border(1.dp, boxBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = screening.targetDateFormatted,
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = boxText
                            )
                        }
                    }
                }
            }

            // Indication & Goal Text
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = screening.indication,
                    style = KanguruTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(colors.clinicalEmeraldText)
                    )
                    Text(
                        text = screening.clinicalGoal,
                        style = KanguruTheme.typography.labelSmall,
                        color = colors.clinicalEmeraldText,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            HorizontalDivider(color = colors.cardBorder)

            // Card Footer Action: Left Status Text + Right Catat Hasil Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Side: Status Display
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Status: ",
                        style = KanguruTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                    if (screening.isCompleted) {
                        Text(
                            text = "Selesai",
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.clinicalEmeraldText
                        )
                    } else {
                        Text(
                            text = "Belum Diperiksa",
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.clinicalRoseText
                        )
                    }
                }

                // Right Side: Action Button tailored per type
                Button(
                    onClick = onRecordClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = btnColor),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    val btnTitle = when (screening.type) {
                        ScreeningType.USG_HEAD -> "+ Catat Hasil USG"
                        ScreeningType.ROP_EYE -> "+ Catat Hasil Mata"
                        ScreeningType.HEARING_OAE -> "+ Catat Hasil OAE"
                        ScreeningType.THYROID_FUNCTION -> "+ Catat Hasil Lab"
                        ScreeningType.CUSTOM -> "+ Catat Hasil"
                    }
                    Text(
                        text = if (screening.isCompleted) "Edit Hasil" else btnTitle,
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.onPrimary
                    )
                }
            }
        }
    }
}

/**
 * Tab 2: Hasil & Lembar Medis
 * Complete Implementation matching Google Stitch Screen "Lembar Medis & Hasil Penapisan Bayi Prematur"
 */
@Composable
private fun EvaluationMedicalSheetView(
    screenings: List<PrematureScreening>,
    onNavigateToInputResult: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section Title & Standard IDAI Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "LEMBAR CATATAN KLINIS RESMI",
                style = KanguruTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textSecondary
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.primaryContainer)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "Standard IDAI",
                    style = KanguruTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }
        }

        // Clinical Record Cards (4 Modalities matching Google Stitch)
        screenings.forEach { screening ->
            ClinicalRecordCard(
                screening = screening,
                onNavigateToInputResult = onNavigateToInputResult,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Doctor Endorsement & Digital Stamp
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Dokter Penanggung Jawab:",
                        style = KanguruTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "dr. Siti Nurhaliza, Sp.A(K) Neo",
                        style = KanguruTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.clinicalEmeraldBg)
                        .border(1.dp, colors.clinicalEmeraldBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = colors.clinicalEmeraldText,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Terverifikasi RS",
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.clinicalEmeraldText
                        )
                    }
                }
            }
        }

        // Catatan Penting Nakes Disclaimer Box
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colors.clinicalAmberBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.clinicalAmberBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(text = "💡", style = KanguruTheme.typography.bodyMedium)
                Text(
                    text = "Catatan Penting Nakes: Bila terjadi komplikasi klinis (sepsis, hiperbilirubinemia berat, atau bantuan ventilasi mekanis lama), jadwal evaluasi dapat dimajukan sesuai instruksi Konsultan Neonatologi.",
                    style = KanguruTheme.typography.bodySmall,
                    color = colors.clinicalAmberText
                )
            }
        }
    }
}

/**
 * Individual Medical Record Card in Lembar Medis
 */
@Composable
private fun ClinicalRecordCard(
    screening: PrematureScreening,
    onNavigateToInputResult: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    val (badgeBg, badgeText, iconEmoji) = when (screening.type) {
        ScreeningType.USG_HEAD -> Triple(colors.clinicalAmberBg, colors.clinicalAmberText, "🧠")
        ScreeningType.ROP_EYE -> Triple(colors.clinicalRoseBg, colors.clinicalRoseText, "👁️")
        ScreeningType.HEARING_OAE -> Triple(colors.clinicalAmberBg, colors.clinicalAmberText, "👂")
        ScreeningType.THYROID_FUNCTION -> Triple(colors.clinicalPurpleBg, colors.clinicalPurpleText, "🧪")
        ScreeningType.CUSTOM -> Triple(colors.primaryContainer, colors.primary, "📋")
    }

    val examinationDate = screening.realizationDateFormatted ?: when (screening.type) {
        ScreeningType.USG_HEAD -> "14-10-2024"
        ScreeningType.ROP_EYE -> "18-10-2024"
        ScreeningType.HEARING_OAE -> "24-10-2024"
        ScreeningType.THYROID_FUNCTION -> "22-10-2024"
        else -> screening.targetDateFormatted
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Emoji + Category + Title + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(badgeBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = iconEmoji, style = KanguruTheme.typography.titleMedium)
                    }

                    Column {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(badgeBg)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = screening.categoryBadge.uppercase(),
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = badgeText
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = screening.title,
                            style = KanguruTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }
                }

                // Top-Right Status Badge & Date
                Column(horizontalAlignment = Alignment.End) {
                    if (screening.isCompleted) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(colors.clinicalEmeraldBg)
                                .border(1.dp, colors.clinicalEmeraldBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = colors.clinicalEmeraldText,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Selesai",
                                    style = KanguruTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.clinicalEmeraldText
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(colors.clinicalAmberBg)
                                .border(1.dp, colors.clinicalAmberBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = "⏳", style = KanguruTheme.typography.labelSmall)
                                Text(
                                    text = "Belum Selesai",
                                    style = KanguruTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.clinicalAmberText
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = examinationDate,
                        style = KanguruTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                }
            }

            // Body: If completed show Findings box, if not completed show Scheduled note box
            if (screening.isCompleted) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.background)
                        .border(1.dp, colors.cardBorder.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "HASIL TEMUAN & INTERPRETASI:",
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textSecondary
                        )

                        // Findings Content
                        val findingsText = screening.findings ?: when (screening.type) {
                            ScreeningType.USG_HEAD -> "Ventrikel lateralis simetris, tidak tampak dilatasi patologis.\nPerdarahan Intraventrikular: Grade 0 (Normal / Negatif).\nEko parenkim hemisfer serebri homogen, tidak ada leukomalasia."
                            ScreeningType.ROP_EYE -> "Vaskularisasi retina: Mencapai Zone III nasal & temporal (Matang).\nTanda ROP Aktif: Negatif / No Plus Disease, tidak ditemukan batas demarkasi patologis.\nMedia refraksi jernih, papil saraf optik dalam batas normal."
                            ScreeningType.THYROID_FUNCTION -> "TSH Neonatus Ulang: 2.1 µIU/mL (Nilai Rujukan Normal: 0.5 - 5.0 µIU/mL).\nFT4 (Free Thyroxine): 1.45 ng/dL (Nilai Rujukan Normal: 0.9 - 2.2 ng/dL).\nStatus Skrining Awal: TSH 4.8 µIU/mL (Lolos / Eutiroid)."
                            else -> screening.notes ?: "Hasil pemeriksaan dalam batas normal."
                        }

                        val highlightPhrases = listOf(
                            "Grade 0 (Normal / Negatif)",
                            "Mencapai Zone III nasal & temporal (Matang)",
                            "2.1 µIU/mL",
                            "1.45 ng/dL"
                        )

                        findingsText.split("\n").filter { it.isNotBlank() }.forEach { rawLine ->
                            val cleanLine = rawLine.trim().removePrefix("•").trim()
                            val matchingPhrase = highlightPhrases.firstOrNull { cleanLine.contains(it) }
                            if (matchingPhrase != null) {
                                val startIndex = cleanLine.indexOf(matchingPhrase)
                                val endIndex = startIndex + matchingPhrase.length
                                val annotatedText = buildAnnotatedString {
                                    append(cleanLine.substring(0, startIndex))
                                    withStyle(
                                        SpanStyle(
                                            color = colors.clinicalEmeraldText,
                                            fontWeight = FontWeight.Bold
                                        )
                                    ) {
                                        append(matchingPhrase)
                                    }
                                    append(cleanLine.substring(endIndex))
                                }
                                Text(
                                    text = annotatedText,
                                    style = KanguruTheme.typography.bodySmall,
                                    color = colors.textPrimary
                                )
                            } else {
                                Text(
                                    text = cleanLine,
                                    style = KanguruTheme.typography.bodySmall,
                                    color = colors.textPrimary
                                )
                            }
                        }
                    }
                }

                // Action Buttons: Lampiran + Edit
                val attachmentLabel = screening.attachmentName ?: when (screening.type) {
                    ScreeningType.USG_HEAD -> "Lampiran Gambar (2)"
                    ScreeningType.ROP_EYE -> "Lampiran Fundus (2)"
                    ScreeningType.THYROID_FUNCTION -> "Lampiran Resume Lab (1)"
                    else -> "Lampiran Medis (1)"
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { onNavigateToInputResult(screening.type.name) },
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = colors.textSecondary
                            )
                            Text(
                                text = attachmentLabel,
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Button(
                        onClick = { onNavigateToInputResult(screening.type.name) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.surfaceVariant,
                            contentColor = colors.textPrimary
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Edit",
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                // Incomplete Status: Yellow scheduled box & prominent "+ Catat Hasil Sekarang" button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.clinicalAmberBg.copy(alpha = 0.6f))
                        .border(1.dp, colors.clinicalAmberBorder, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "📋", style = KanguruTheme.typography.bodyMedium)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "STATUS BLUM ADA HASIL PEMERIKSAAN:",
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.clinicalAmberText
                            )
                            Text(
                                text = screening.notes ?: "Pemeriksaan OAE tahap 2 & BERA dijadwalkan pada Tanggal 12 Desember 2024. Menunggu konfirmasi hasil pemeriksaan dokter.",
                                style = KanguruTheme.typography.bodySmall,
                                color = colors.textPrimary
                            )
                        }
                    }
                }

                Button(
                    onClick = { onNavigateToInputResult(screening.type.name) },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "+ Catat Hasil Sekarang",
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Modal Tambah Jadwal Skrining Baru
 */
@Composable
private fun AddScreeningDialog(
    state: AddScreeningModalState,
    onClose: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onCategoryChanged: (String) -> Unit,
    onTimingChanged: (String) -> Unit,
    onTargetDateChanged: (Long) -> Unit,
    onIndicationChanged: (String) -> Unit,
    onGoalChanged: (String) -> Unit,
    onClinicChanged: (String) -> Unit,
    onSave: () -> Unit
) {
    val colors = KanguruTheme.colors
    val context = LocalContext.current
    val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")) }

    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {},
        dismissButton = {},
        containerColor = colors.surface,
        shape = RoundedCornerShape(24.dp),
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tambah Jadwal Penapisan",
                        style = KanguruTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = colors.textSecondary)
                    }
                }

                OutlinedTextField(
                    value = state.title,
                    onValueChange = onTitleChanged,
                    label = { Text("Nama Penapisan", style = KanguruTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.cardBorder
                    )
                )

                OutlinedTextField(
                    value = state.categoryBadge,
                    onValueChange = onCategoryChanged,
                    label = { Text("Kategori", style = KanguruTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.cardBorder
                    )
                )

                OutlinedTextField(
                    value = state.timingLabel,
                    onValueChange = onTimingChanged,
                    label = { Text("Target Waktu Evaluasi", style = KanguruTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.cardBorder
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceVariant)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            val cal = Calendar.getInstance().apply { timeInMillis = state.targetDateEpoch }
                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val newCal = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
                                    onTargetDateChanged(newCal.timeInMillis)
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Tanggal: ${dateFormat.format(Date(state.targetDateEpoch))}",
                        style = KanguruTheme.typography.bodySmall,
                        color = colors.textPrimary
                    )
                }

                OutlinedTextField(
                    value = state.indication,
                    onValueChange = onIndicationChanged,
                    label = { Text("Indikasi Klinis", style = KanguruTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.cardBorder
                    )
                )

                OutlinedTextField(
                    value = state.clinicOrLab,
                    onValueChange = onClinicChanged,
                    label = { Text("Poli / Unit Rujukan", style = KanguruTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.cardBorder
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onClose,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Batal", style = KanguruTheme.typography.labelSmall)
                    }

                    Button(
                        onClick = onSave,
                        enabled = state.title.isNotBlank(),
                        modifier = Modifier.weight(1.4f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                    ) {
                        Text("Simpan Jadwal", style = KanguruTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    )
}

private data class Tuple7<A, B, C, D, E, F, G>(
    val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G
)

private fun <A, B, C, D, E, F, G> tuple7(
    a: A, b: B, c: C, d: D, e: E, f: F, g: G
) = Tuple7(a, b, c, d, e, f, g)
