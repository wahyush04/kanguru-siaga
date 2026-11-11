package com.kangurusiaga.app.presentation.pmk.screening

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File
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
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CalendarToday
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kangurusiaga.app.core.designsystem.component.KanguruSnackbarHost
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.PrematureScreening
import com.kangurusiaga.app.domain.model.ScreeningType
import com.kangurusiaga.app.domain.util.PmaCalculator
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ScreeningScheduleRoute(
    onNavigateBack: () -> Unit,
    onNavigateToInputResult: (type: String) -> Unit,
    onNavigateToCustomInputResult: (screeningId: Long?) -> Unit,
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
        onNavigateToCustomInputResult = onNavigateToCustomInputResult,
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
        onUpdateAddAlarm = viewModel::updateAddAlarm,
        onUpdateAddNotes = viewModel::updateAddNotes,
        onUpdateAddIndication = viewModel::updateAddIndication,
        onUpdateAddGoal = viewModel::updateAddGoal,
        onUpdateAddClinic = viewModel::updateAddClinic,
        onSaveAdd = viewModel::saveNewScreening,
        onOpenRescheduleModal = viewModel::openRescheduleModal,
        onCloseRescheduleModal = viewModel::closeRescheduleModal,
        onUpdateRescheduleDate = viewModel::updateRescheduleDate,
        onSaveRescheduleDate = viewModel::saveRescheduledDate,
        onSetInfoDialogVisible = viewModel::setInfoDialogVisible,
        onDownloadPdf = viewModel::downloadPdfSummary,
        modifier = modifier,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreeningScheduleScreen(
    uiState: ScreeningScheduleUiState,
    onNavigateBack: () -> Unit,
    onNavigateToInputResult: (type: String) -> Unit,
    onNavigateToCustomInputResult: (screeningId: Long?) -> Unit,
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
    onUpdateAddAlarm: (Boolean) -> Unit,
    onUpdateAddNotes: (String) -> Unit,
    onUpdateAddIndication: (String) -> Unit,
    onUpdateAddGoal: (String) -> Unit,
    onUpdateAddClinic: (String) -> Unit,
    onSaveAdd: () -> Unit,
    onOpenRescheduleModal: (PrematureScreening) -> Unit,
    onCloseRescheduleModal: () -> Unit,
    onUpdateRescheduleDate: (Long) -> Unit,
    onSaveRescheduleDate: () -> Unit,
    onSetInfoDialogVisible: (Boolean) -> Unit,
    onDownloadPdf: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val context = LocalContext.current
    val colors = KanguruTheme.colors

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        snackbarHost = { KanguruSnackbarHost(snackbarHostState) },
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
                                style = KanguruTheme.typography.labelMedium,
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
                totalCount = uiState.screenings.size,
                onSelectTab = onSelectTab,
                modifier = Modifier.fillMaxWidth()
            )

            // Tab Content
            if (uiState.selectedTab == ScreeningTab.REMINDERS) {
                RemindersView(
                    screenings = uiState.screenings,
                    baby = uiState.baby,
                    onToggleAlarm = onToggleAlarm,
                    onNavigateToInputResult = onNavigateToInputResult,
                    onNavigateToCustomInputResult = onNavigateToCustomInputResult,
                    onRescheduleClick = onOpenRescheduleModal,
                    onOpenAddModal = onOpenAddModal,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                EvaluationMedicalSheetView(
                    screenings = uiState.screenings,
                    onNavigateToInputResult = onNavigateToInputResult,
                    onNavigateToCustomInputResult = onNavigateToCustomInputResult,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Modal Tambah Jadwal Baru (Google Stitch Bottom Sheet)
    if (uiState.addModalState.isOpen) {
        AddCustomScreeningBottomSheet(
            state = uiState.addModalState,
            baby = uiState.baby,
            onDismiss = onCloseAddModal,
            onTitleChanged = onUpdateAddTitle,
            onTargetDateChanged = onUpdateAddTargetDate,
            onAlarmChanged = onUpdateAddAlarm,
            onNotesChanged = onUpdateAddNotes,
            onSave = onSaveAdd
        )
    }

    // Modal / Pop-Up Atur Jadwal Ulang Skrining
    if (uiState.rescheduleModalState.isOpen) {
        RescheduleScreeningBottomSheet(
            state = uiState.rescheduleModalState,
            baby = uiState.baby,
            onDismiss = onCloseRescheduleModal,
            onDateSelected = onUpdateRescheduleDate,
            onSave = onSaveRescheduleDate
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
    val context = LocalContext.current
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
                                style = KanguruTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = colors.onPrimary
                            )
                        }
                        Text(
                            text = "BBLR / Prematur",
                            style = KanguruTheme.typography.labelMedium,
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
                                style = KanguruTheme.typography.labelMedium,
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
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${uiState.baby?.name ?: "Si Kecil"} (${uiState.babyAgeDisplay})",
                                style = KanguruTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Umur Kronologis: ${uiState.chronologicalAgeDisplay}\nLahir: ${uiState.birthDateDisplay}",
                                style = KanguruTheme.typography.labelMedium,
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
                            text = "Rawat Jalan\nAktif",
                            style = KanguruTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.clinicalEmeraldText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tab 2: Patient Summary Card with Completion Progress Bar
 */
@Composable
private fun PatientSummaryMedicalCard(
    uiState: ScreeningScheduleUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
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

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = uiState.baby?.name ?: "Si Kecil",
                                style = KanguruTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            if (uiState.baby?.gestationalAgeWeeks != null) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(colors.primaryContainer)
                                        .border(1.dp, colors.clinicalRoseBorder, RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${uiState.baby.gestationalAgeWeeks} Mgg",
                                        style = KanguruTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.primary
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Umur Kronologis: ${uiState.chronologicalAgeDisplay}\nLahir: ${uiState.birthDateDisplay}",
                            style = KanguruTheme.typography.labelMedium,
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
                        text = "Rawat Jalan\nAktif",
                        style = KanguruTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.clinicalEmeraldText,
                        textAlign = TextAlign.Center
                    )
                }
            }

            HorizontalDivider(color = colors.divider.copy(alpha = 0.6f))

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
                            style = KanguruTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = colors.textSecondary
                        )
                    }

                    Text(
                        text = "${uiState.completedScreeningsCount} dari ${uiState.totalScreeningsCount} Selesai (${uiState.completionPercentage}%)",
                        style = KanguruTheme.typography.labelMedium,
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
    totalCount: Int,
    onSelectTab: (ScreeningTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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
                        text = "Jadwal & Alarm ($totalCount)",
                        style = KanguruTheme.typography.labelMedium,
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
                        style = KanguruTheme.typography.labelMedium,
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
    baby: Baby?,
    onToggleAlarm: (PrematureScreening, Boolean) -> Unit,
    onNavigateToInputResult: (type: String) -> Unit,
    onNavigateToCustomInputResult: (screeningId: Long?) -> Unit,
    onRescheduleClick: (PrematureScreening) -> Unit,
    onOpenAddModal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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
                style = KanguruTheme.typography.labelMedium,
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
                    style = KanguruTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }
        }

        // Screening Cards
        screenings.forEach { screening ->
            ScreeningCardItem(
                screening = screening,
                baby = baby,
                onToggleAlarm = { isAlarm1 -> onToggleAlarm(screening, isAlarm1) },
                onRecordClick = {
                    if (screening.type == ScreeningType.CUSTOM) {
                        onNavigateToCustomInputResult(screening.id)
                    } else {
                        onNavigateToInputResult(screening.type.name)
                    }
                },
                onRescheduleClick = { onRescheduleClick(screening) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Tombol Tambah Jadwal Skrining Lainnya (Stitch matching)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.clinicalRoseBg.copy(alpha = 0.5f))
                .border(
                    width = 1.dp,
                    color = colors.clinicalRoseBorder,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { onOpenAddModal() }
                .padding(vertical = 14.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Tambah Jadwal Skrining Lainnya",
                    style = KanguruTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }
        }

        Text(
            text = "Jadwalkan pemeriksaan spesialis tambahan (misal: USG Ginjal, Ekokardiografi, dll.)",
            style = KanguruTheme.typography.labelSmall,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun ScreeningCardItem(
    screening: PrematureScreening,
    baby: Baby?,
    onToggleAlarm: (Boolean) -> Unit,
    onRecordClick: () -> Unit,
    onRescheduleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = KanguruTheme.colors

    if (screening.type == ScreeningType.CUSTOM) {
        // Special custom card matching Google Stitch media_1794231007559_2f4ba9f6.png
        val pmaEstimate = remember(screening.targetDateEpoch, baby) {
            if (baby != null && screening.targetDateEpoch > 0) {
                val calculated = PmaCalculator.calculate(
                    birthDateEpochMillis = baby.birthDateEpochMillis,
                    measurementDateEpochMillis = screening.targetDateEpoch,
                    gestationalAgeAtBirthWeeks = baby.gestationalAgeWeeks
                )
                "${calculated.pmaWeeks.toInt()} Minggu PMA"
            } else {
                screening.targetTimingLabel.ifBlank { "Evaluasi Lanjutan" }
            }
        }

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.clinicalBlueBorder),
            modifier = modifier
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header: Medical Icon + Badges (JADWAL TAMBAHAN + Kustom) + Title + Switch
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
                                .background(colors.clinicalBlueBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = colors.clinicalBlueSolid,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(colors.clinicalBlueBg)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "JADWAL TAMBAHAN",
                                        style = KanguruTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.clinicalBlueText
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(colors.surfaceVariant.copy(alpha = 0.5f))
                                        .border(1.dp, colors.cardBorder, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Kustom",
                                        style = KanguruTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.textSecondary
                                    )
                                }
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

                // Timing Box: Date + Estimasi Usia
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.clinicalBlueBg.copy(alpha = 0.5f))
                        .border(1.dp, colors.clinicalBlueBorder, RoundedCornerShape(14.dp))
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
                                tint = colors.clinicalBlueText,
                                modifier = Modifier.size(16.dp)
                            )
                            Column {
                                Text(
                                    text = "TANGGAL RENCANA PEMERIKSAAN:",
                                    style = KanguruTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.clinicalBlueText
                                )
                                Text(
                                    text = screening.targetDateFormatted,
                                    style = KanguruTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.surface)
                                .border(1.dp, colors.clinicalBlueBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Estimasi Usia:",
                                    style = KanguruTheme.typography.labelSmall,
                                    color = colors.textSecondary
                                )
                                Text(
                                    text = pmaEstimate,
                                    style = KanguruTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.clinicalBlueText
                                )
                            }
                        }
                    }
                }

                // Catatan / Instruksi Khusus (if present)
                if (!screening.notes.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.surfaceVariant.copy(alpha = 0.4f))
                            .border(1.dp, colors.cardBorder, RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Notes,
                                    contentDescription = null,
                                    tint = colors.textSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Catatan / Instruksi Khusus:",
                                    style = KanguruTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textSecondary
                                )
                            }
                            Text(
                                text = screening.notes!!,
                                style = KanguruTheme.typography.bodySmall,
                                color = colors.textPrimary
                            )
                        }
                    }
                } else if (screening.indication.isNotBlank()) {
                    Text(
                        text = screening.indication,
                        style = KanguruTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                }

                HorizontalDivider(color = colors.cardBorder)

                // Card Footer Action
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Status: ",
                            style = KanguruTheme.typography.labelMedium,
                            color = colors.textSecondary
                        )
                        if (screening.isCompleted) {
                            Text(
                                text = "Selesai",
                                style = KanguruTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.clinicalEmeraldText
                            )
                        } else {
                            Text(
                                text = "Belum Diperiksa",
                                style = KanguruTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.clinicalRoseText
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onRescheduleClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = colors.surface,
                                contentColor = colors.textPrimary
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = colors.textSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "Jadwal Ulang",
                                    style = KanguruTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textPrimary,
                                    maxLines = 1
                                )
                            }
                        }

                        Button(
                            onClick = onRecordClick,
                            modifier = Modifier
                                .weight(1.3f)
                                .height(44.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colors.clinicalBlueSolid),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = if (screening.isCompleted) "Edit Hasil" else "+ Catat Hasil",
                                style = KanguruTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.onPrimary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
        return
    }

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
            colors.clinicalBlueBg, colors.clinicalBlueText, "🩺",
            colors.clinicalBlueSolid, colors.clinicalBlueBg, colors.clinicalBlueBorder, colors.clinicalBlueText
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
                                style = KanguruTheme.typography.labelMedium,
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
                                style = KanguruTheme.typography.labelMedium,
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
                                        Text(text = "Alarm 1", style = KanguruTheme.typography.labelMedium, color = colors.textSecondary)
                                        Text(text = "2 Bln Kronologis", style = KanguruTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                                        Text(text = screening.targetDateFormatted, style = KanguruTheme.typography.labelMedium, color = boxText, fontWeight = FontWeight.SemiBold)
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
                                        Text(text = "Alarm 2 (Batas)", style = KanguruTheme.typography.labelMedium, color = colors.textSecondary)
                                        Text(text = "3 Bln Kronologis", style = KanguruTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                                        Text(text = screening.targetDate2Formatted, style = KanguruTheme.typography.labelMedium, color = boxText, fontWeight = FontWeight.SemiBold)
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
                                    style = KanguruTheme.typography.labelMedium,
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
                                style = KanguruTheme.typography.labelMedium,
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
                        style = KanguruTheme.typography.labelMedium,
                        color = colors.clinicalEmeraldText,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            HorizontalDivider(color = colors.cardBorder)

            // Card Footer Action: Status Text on Top, Two Buttons below
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Status Display Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Status: ",
                        style = KanguruTheme.typography.labelMedium,
                        color = colors.textSecondary
                    )
                    if (screening.isCompleted) {
                        Text(
                            text = "Selesai",
                            style = KanguruTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.clinicalEmeraldText
                        )
                    } else {
                        Text(
                            text = "Belum Diperiksa",
                            style = KanguruTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.clinicalRoseText
                        )
                    }
                }

                // Action Buttons Row: Jadwal Ulang (Outlined) + Catat Hasil (Filled)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tombol Jadwal Ulang (Outlined)
                    OutlinedButton(
                        onClick = onRescheduleClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = colors.surface,
                            contentColor = colors.textPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = colors.textSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "Jadwal Ulang",
                                style = KanguruTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary,
                                maxLines = 1
                            )
                        }
                    }

                    // Tombol Catat Hasil (Filled)
                    val btnTitle = when (screening.type) {
                        ScreeningType.USG_HEAD -> "+ Catat Hasil USG"
                        ScreeningType.ROP_EYE -> "+ Catat Hasil Mata"
                        ScreeningType.HEARING_OAE -> "+ Catat Hasil OAE"
                        ScreeningType.THYROID_FUNCTION -> "+ Catat Hasil Lab"
                        ScreeningType.CUSTOM -> "+ Catat Hasil"
                    }
                    Button(
                        onClick = onRecordClick,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = btnColor),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = if (screening.isCompleted) "Edit Hasil" else btnTitle,
                            style = KanguruTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.onPrimary,
                            maxLines = 1
                        )
                    }
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
    onNavigateToInputResult: (type: String) -> Unit,
    onNavigateToCustomInputResult: (screeningId: Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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
                style = KanguruTheme.typography.labelMedium,
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
                    style = KanguruTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }
        }

        // Clinical Record Cards (4 Modalities + Custom cards)
        screenings.forEach { screening ->
            ClinicalRecordCard(
                screening = screening,
                onNavigateToInputResult = onNavigateToInputResult,
                onNavigateToCustomInputResult = onNavigateToCustomInputResult,
                modifier = Modifier.fillMaxWidth()
            )
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

        // Tombol Tambah Hasil Skrining Tambahan (Stitch matching)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.clinicalRoseBg.copy(alpha = 0.5f))
                .border(
                    width = 1.dp,
                    color = colors.clinicalRoseBorder,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { onNavigateToCustomInputResult(null) }
                .padding(vertical = 14.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Tambah Hasil Skrining Tambahan",
                    style = KanguruTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }
        }

        Text(
            text = "Catat lembar hasil pemeriksaan spesialis kustom lainnya (misal: USG Abdomen, Ekokardiografi, Lab tambahan)",
            style = KanguruTheme.typography.labelSmall,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
    }
}

/**
 * Individual Medical Record Card in Lembar Medis
 */
@Composable
private fun ClinicalRecordCard(
    screening: PrematureScreening,
    onNavigateToInputResult: (type: String) -> Unit,
    onNavigateToCustomInputResult: (screeningId: Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = KanguruTheme.colors

    val isCustom = screening.type == ScreeningType.CUSTOM

    val onOpenResult: () -> Unit = {
        if (screening.type == ScreeningType.CUSTOM) {
            onNavigateToCustomInputResult(screening.id)
        } else {
            onNavigateToInputResult(screening.type.name)
        }
    }

    val (badgeBg, badgeText, iconEmoji) = when (screening.type) {
        ScreeningType.USG_HEAD -> Triple(colors.clinicalAmberBg, colors.clinicalAmberText, "🧠")
        ScreeningType.ROP_EYE -> Triple(colors.clinicalRoseBg, colors.clinicalRoseText, "👁️")
        ScreeningType.HEARING_OAE -> Triple(colors.clinicalAmberBg, colors.clinicalAmberText, "👂")
        ScreeningType.THYROID_FUNCTION -> Triple(colors.clinicalPurpleBg, colors.clinicalPurpleText, "🧪")
        ScreeningType.CUSTOM -> Triple(colors.clinicalRoseBg, colors.clinicalRoseText, "🩺")
    }

    val examinationDate = screening.realizationDateFormatted?.ifBlank { null }
        ?: screening.targetDateFormatted.ifBlank {
            when (screening.type) {
                ScreeningType.USG_HEAD -> "14-10-2024"
                ScreeningType.ROP_EYE -> "18-10-2024"
                ScreeningType.HEARING_OAE -> "24-10-2024"
                ScreeningType.THYROID_FUNCTION -> "22-10-2024"
                else -> ""
            }
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
                        if (isCustom) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = colors.clinicalRoseSolid,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(text = iconEmoji, style = KanguruTheme.typography.titleMedium)
                        }
                    }

                    Column {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(badgeBg)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isCustom) "SKRINING TAMBAHAN KUSTOM" else screening.categoryBadge.uppercase(),
                                style = KanguruTheme.typography.labelMedium,
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
                                    style = KanguruTheme.typography.labelMedium,
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
                                Text(text = "⏳", style = KanguruTheme.typography.labelMedium)
                                Text(
                                    text = "Belum Selesai",
                                    style = KanguruTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.clinicalAmberText
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = examinationDate,
                        style = KanguruTheme.typography.labelMedium,
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
                            style = KanguruTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = colors.textSecondary
                        )

                        // Findings Content
                        val findingsText = screening.findings ?: when (screening.type) {
                            ScreeningType.USG_HEAD -> "Ventrikel lateralis simetris, tidak tampak dilatasi patologis.\nPerdarahan Intraventrikular: Grade 0 (Normal / Negatif).\nEko parenkim hemisfer serebri homogen, tidak ada leukomalasia."
                            ScreeningType.ROP_EYE -> "Vaskularisasi retina: Mencapai Zone III nasal & temporal (Matang).\nTanda ROP Aktif: Negatif / No Plus Disease, tidak ditemukan batas demarkasi patologis.\nMedia refraksi jernih, papil saraf optik dalam batas normal."
                            ScreeningType.HEARING_OAE -> "Skrining OAE Awal: PASS (Lolos Bilateral / Kedua Telinga).\nTelinga Kanan (AD): PASS - Emisi Koklea Positif.\nTelinga Kiri (AS): PASS - Emisi Koklea Positif.\nRespon auditori dan ambang dengar dalam batas normal (≤ 35 dB nHL)."
                            ScreeningType.THYROID_FUNCTION -> "TSH Neonatus Ulang: 2.1 µIU/mL (Nilai Rujukan Normal: 0.5 - 5.0 µIU/mL).\nFT4 (Free Thyroxine): 1.45 ng/dL (Nilai Rujukan Normal: 0.9 - 2.2 ng/dL).\nStatus Skrining Awal: TSH 4.8 µIU/mL (Lolos / Eutiroid)."
                            ScreeningType.CUSTOM -> "Situs solitus, koneksi atrioventrikular dan ventrikuloarterial konkordan.\nPenutupan duktus arteriosus sempurna (PDA tertutup / Negatif). Foramen ovale paten kecil hemodinamik tidak signifikan (PFO minimal).\nFungsi sistolik ventrikel kiri baik (EF 68%), tidak tampak defek septum ventrikel (VSD negatif), kontraktilitas optimal."
                        }

                        val highlightPhrases = listOf(
                            "Grade 0 (Normal / Negatif)",
                            "Mencapai Zone III nasal & temporal (Matang)",
                            "PASS (Lolos Bilateral / Kedua Telinga)",
                            "PASS - Emisi Koklea Positif",
                            "2.1 µIU/mL",
                            "1.45 ng/dL",
                            "(PDA tertutup / Negatif)",
                            "(EF 68%)"
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
                                    style = KanguruTheme.typography.bodyMedium,
                                    color = colors.textPrimary
                                )
                            } else {
                                Text(
                                    text = cleanLine,
                                    style = KanguruTheme.typography.bodyMedium,
                                    color = colors.textPrimary
                                )
                            }
                        }
                    }
                }

                // Action Buttons: Lampiran + Edit
                val hasAttachment = !screening.attachmentName.isNullOrBlank()
                val attachmentLabel = if (hasAttachment) screening.attachmentName!! else "+ Tambah Lampiran"

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (hasAttachment && !screening.attachmentUri.isNullOrBlank()) {
                                try {
                                    val uri = Uri.parse(screening.attachmentUri)
                                    val file = if (uri.scheme == "file") File(uri.path ?: "") else null
                                    if (file != null && file.exists()) {
                                        val contentUri = FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.fileprovider",
                                            file
                                        )
                                        val ext = file.extension.lowercase(Locale.ROOT)
                                        val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext) ?: "*/*"
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                             setDataAndType(contentUri, mime)
                                             addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                             addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(intent)
                                    } else {
                                        onOpenResult()
                                    }
                                } catch (_: Exception) {
                                    onOpenResult()
                                }
                            } else {
                                onOpenResult()
                            }
                        },
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
                                imageVector = if (hasAttachment) Icons.Default.AttachFile else Icons.Default.Image,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (hasAttachment) colors.clinicalPurpleSolid else colors.textSecondary
                            )
                            Text(
                                text = attachmentLabel,
                                style = KanguruTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Button(
                        onClick = onOpenResult,
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
                                style = KanguruTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                val defaultPendingNote = remember(
                    screening.type,
                    screening.targetDateEpoch,
                    screening.targetDateEpoch2,
                    screening.targetDateFormatted,
                    screening.targetDate2Formatted
                ) {
                    fun formatDate(epoch: Long?, formattedFallback: String): String {
                        if (epoch != null && epoch > 0) {
                            try {
                                val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                                return sdf.format(Date(epoch))
                            } catch (_: Exception) {}
                        }
                        if (formattedFallback.isNotBlank()) {
                            try {
                                val parseSdf = SimpleDateFormat("dd-MM-yyyy", Locale("id", "ID"))
                                val parsed = parseSdf.parse(formattedFallback)
                                if (parsed != null) {
                                    val outSdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                                    return outSdf.format(parsed)
                                }
                            } catch (_: Exception) {}
                            return formattedFallback
                        }
                        return "jadwal yang ditentukan"
                    }

                    val date1 = formatDate(screening.targetDateEpoch, screening.targetDateFormatted)
                    val date2 = formatDate(
                        screening.targetDateEpoch2 ?: screening.targetDateEpoch,
                        screening.targetDate2Formatted.ifBlank { screening.targetDateFormatted }
                    )

                    when (screening.type) {
                        ScreeningType.USG_HEAD -> {
                            "Pemeriksaan Neurosonografi (USG Kepala) dijadwalkan pada Tanggal $date1. Evaluasi perdarahan intraventrikular (IVH) & leukomalasia periventrikular. Menunggu konfirmasi hasil pemeriksaan dokter."
                        }
                        ScreeningType.ROP_EYE -> {
                            "Pemeriksaan Retinopati Prematuritas (ROP) dijadwalkan pada Tanggal $date1. Evaluasi vaskularisasi perifer retina zona I–III pencegahan ablasi retina (wajib tetes mata dilatasi pupil 30 menit sebelum tindakan). Menunggu konfirmasi hasil pemeriksaan dokter."
                        }
                        ScreeningType.HEARING_OAE -> {
                            "Pemeriksaan OAE tahap 2 & BERA dijadwalkan pada Tanggal $date2. Evaluasi fungsi koklea dan respon auditori batang otak. Menunggu konfirmasi hasil pemeriksaan dokter."
                        }
                        ScreeningType.THYROID_FUNCTION -> {
                            "Pemeriksaan ulang serum FT4 & TSH dijadwalkan pada Tanggal $date1 (PMA 40 Minggu). Deteksi dini hipotiroid kongenital tertunda. Menunggu hasil evaluasi laboratorium."
                        }
                        ScreeningType.CUSTOM -> {
                            "Pemeriksaan ${screening.title} dijadwalkan pada Tanggal $date1. Menunggu konfirmasi pelaksanaan dan hasil pemeriksaan dokter."
                        }
                    }
                }

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
                                text = "STATUS BELUM ADA HASIL PEMERIKSAAN:",
                                style = KanguruTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.clinicalAmberText
                            )
                            Text(
                                text = if (!screening.notes.isNullOrBlank()) screening.notes!! else defaultPendingNote,
                                style = KanguruTheme.typography.bodySmall,
                                color = colors.textPrimary
                            )
                        }
                    }
                }

                Button(
                    onClick = onOpenResult,
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
                            text = "Catat Hasil Sekarang",
                            style = KanguruTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.onPrimary
                        )
                    }
                }
            }
        }
    }
}

private data class Tuple7<A, B, C, D, E, F, G>(
    val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G
)

private fun <A, B, C, D, E, F, G> tuple7(
    a: A, b: B, c: C, d: D, e: E, f: F, g: G
) = Tuple7(a, b, c, d, e, f, g)
