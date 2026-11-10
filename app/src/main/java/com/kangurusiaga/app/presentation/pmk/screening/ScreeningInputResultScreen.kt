package com.kangurusiaga.app.presentation.pmk.screening

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kangurusiaga.app.core.designsystem.component.KanguruSnackbarHost
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.domain.model.ScreeningType

@Composable
fun ScreeningInputResultRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScreeningInputResultViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempCameraUri?.let { viewModel.onPhotoCaptured(it) }
        }
    }

    val documentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.onDocumentSelected(it) }
    }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    ScreeningInputResultScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onSelectScreeningType = viewModel::selectScreeningType,
        onNotesChanged = viewModel::updateClinicalNotes,
        onInsertQuickPoint = viewModel::insertQuickPoint,
        onAddPhoto = {
            try {
                val uri = viewModel.createTempCameraUri()
                tempCameraUri = uri
                cameraLauncher.launch(uri)
            } catch (_: Exception) {
                // Handled in ViewModel
            }
        },
        onAddPdf = {
            documentLauncher.launch(arrayOf("application/pdf", "image/*"))
        },
        onOpenAttachment = {
            viewModel.openCurrentAttachment(context)
        },
        onRemoveAttachment = viewModel::removeAttachment,
        onSave = {
            viewModel.saveResult(onSuccess = onNavigateBack)
        },
        onSetHelpDialogVisible = viewModel::setHelpDialogVisible,
        modifier = modifier,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun ScreeningInputResultScreen(
    uiState: ScreeningInputResultUiState,
    onNavigateBack: () -> Unit,
    onSelectScreeningType: (ScreeningType) -> Unit,
    onNotesChanged: (String) -> Unit,
    onInsertQuickPoint: (String) -> Unit,
    onAddPhoto: () -> Unit,
    onAddPdf: () -> Unit,
    onRemoveAttachment: () -> Unit,
    onSave: () -> Unit,
    onSetHelpDialogVisible: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onOpenAttachment: (() -> Unit)? = null,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
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
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
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

                        Column {
                            Text(
                                text = "Input Hasil & Resume",
                                style = KanguruTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "Penapisan Bayi Prematur (BBLR)",
                                style = KanguruTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(colors.primaryContainer)
                            .clickable { onSetHelpDialogVisible(true) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Help,
                            contentDescription = "Bantuan Klinis",
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
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onNavigateBack,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = colors.surface,
                            contentColor = colors.textPrimary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Batal",
                            style = KanguruTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }

                    Button(
                        onClick = onSave,
                        enabled = !uiState.isSaving,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary,
                            contentColor = colors.onPrimary
                        ),
                        modifier = Modifier
                            .weight(2f)
                            .height(50.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = colors.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = if (uiState.isSaving) "Menyimpan..." else "Simpan Hasil Skrining",
                                style = KanguruTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = colors.onPrimary
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Patient Profile Card
            PatientProfileSummaryCard(
                babyInitials = uiState.babyInitials,
                babyName = uiState.babyName,
                genderDisplay = uiState.genderDisplay,
                isMale = uiState.isMale,
                pmaWeeksDisplay = uiState.pmaWeeksDisplay,
                birthGestationalDisplay = uiState.birthGestationalDisplay,
                birthWeightDisplay = uiState.birthWeightDisplay
            )

            // Section 1: PILIH JENIS PENAPISAN (IDAI / KEMENKES) dengan badge Wajib 4 Skrining & Grid 2x2
            MandatoryScreeningSelectorCard(
                selectedType = uiState.selectedType,
                onSelectType = onSelectScreeningType
            )

            // Section 2: Hasil & Resume Medis (Text Area + Quick Insertion Chips)
            ClinicalNotesEditorCard(
                notes = uiState.clinicalNotes,
                charCount = uiState.charCount,
                onNotesChanged = onNotesChanged,
                onInsertQuickPoint = onInsertQuickPoint
            )

            // Section 3: Lampiran Dokumen Medis
            MedicalAttachmentsCard(
                attachmentName = uiState.attachmentName,
                attachmentSize = uiState.attachmentSize,
                isAttachmentImage = uiState.isAttachmentImage,
                onAddPhoto = onAddPhoto,
                onAddPdf = onAddPdf,
                onRemoveAttachment = onRemoveAttachment,
                onOpenAttachment = onOpenAttachment
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (uiState.helpDialogVisible) {
        ClinicalHelpDialog(
            onDismiss = { onSetHelpDialogVisible(false) }
        )
    }
}

/**
 * Card Pemilihan 4 Jenis Penapisan Wajib (IDAI / KEMENKES)
 * Sesuai persis dengan gambar referensi Google Stitch (media_1794281298293_0fd19e97.png)
 */
@Composable
private fun MandatoryScreeningSelectorCard(
    selectedType: ScreeningType,
    onSelectType: (ScreeningType) -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row: PILIH JENIS PENAPISAN (IDAI / KEMENKES) & Badge Wajib 4 Skrining
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PILIH JENIS PENAPISAN (IDAI /\nKEMENKES)",
                    style = KanguruTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.clinicalRoseBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Wajib 4\nSkrining",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.clinicalRoseSolid,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Grid 2x2 Modalitas Skrining Wajib IDAI / Kemenkes
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Row 1: USG Kepala & Retinopati (ROP)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModalityTile(
                        title = "USG Kepala",
                        subtitle = "Neurosonografi",
                        icon = Icons.Default.Psychology,
                        iconBg = colors.clinicalBlueBg,
                        iconTint = colors.clinicalBlueSolid,
                        isSelected = selectedType == ScreeningType.USG_HEAD,
                        onClick = { onSelectType(ScreeningType.USG_HEAD) },
                        modifier = Modifier.weight(1f)
                    )

                    ModalityTile(
                        title = "Retinopati (ROP)",
                        subtitle = "Skrining Mata",
                        icon = Icons.Default.Visibility,
                        iconBg = colors.clinicalRoseBg,
                        iconTint = colors.clinicalRoseSolid,
                        isSelected = selectedType == ScreeningType.ROP_EYE,
                        onClick = { onSelectType(ScreeningType.ROP_EYE) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2: Pendengaran & Fungsi Tiroid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModalityTile(
                        title = "Pendengaran",
                        subtitle = "OAE & BERA",
                        icon = Icons.Default.Hearing,
                        iconBg = colors.clinicalAmberBg,
                        iconTint = colors.clinicalAmberSolid,
                        isSelected = selectedType == ScreeningType.HEARING_OAE,
                        onClick = { onSelectType(ScreeningType.HEARING_OAE) },
                        modifier = Modifier.weight(1f)
                    )

                    ModalityTile(
                        title = "Fungsi Tiroid",
                        subtitle = "SHK / FT4 & TSH",
                        icon = Icons.Default.Science,
                        iconBg = colors.clinicalPurpleBg,
                        iconTint = colors.clinicalPurpleSolid,
                        isSelected = selectedType == ScreeningType.THYROID_FUNCTION,
                        onClick = { onSelectType(ScreeningType.THYROID_FUNCTION) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Tile Modalitas Skrining IDAI / Kemenkes
 */
@Composable
internal fun ModalityTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) colors.clinicalRoseBg.copy(alpha = 0.35f) else colors.background)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) colors.primary else colors.cardBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = KanguruTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) colors.primary else colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Terpilih",
                            tint = colors.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = subtitle,
                    style = KanguruTheme.typography.labelSmall,
                    color = if (isSelected) colors.primary.copy(alpha = 0.85f) else colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Reusable Patient Profile Card matching Google Stitch designs
 */
@Composable
internal fun PatientProfileSummaryCard(
    babyInitials: String,
    babyName: String,
    genderDisplay: String,
    isMale: Boolean,
    pmaWeeksDisplay: String,
    birthGestationalDisplay: String,
    birthWeightDisplay: String,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(colors.primary, colors.clinicalRoseSolid)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = babyInitials,
                    style = KanguruTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.onPrimary
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = babyName,
                        style = KanguruTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isMale) colors.clinicalBlueBg else colors.clinicalRoseBg)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isMale) "♂ $genderDisplay" else "♀ $genderDisplay",
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isMale) colors.clinicalBlueText else colors.clinicalRoseText
                        )
                    }
                }

                Text(
                    text = "Kronologis: $pmaWeeksDisplay • Lahir: $birthGestationalDisplay ($birthWeightDisplay)",
                    style = KanguruTheme.typography.bodySmall,
                    color = colors.textSecondary
                )

                HorizontalDivider(
                    color = colors.divider.copy(alpha = 0.6f),
                    modifier = Modifier.padding(vertical = 2.dp)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(colors.clinicalEmeraldBg)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Rawat Jalan / NICU",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.clinicalEmeraldText
                    )
                }
            }
        }
    }
}

/**
 * Reusable Clinical Notes Editor Card
 */
@Composable
internal fun ClinicalNotesEditorCard(
    notes: String,
    charCount: Int,
    onNotesChanged: (String) -> Unit,
    onInsertQuickPoint: (String) -> Unit,
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
                            .size(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Notes,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "HASIL & RESUME MEDIS",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.clinicalEmeraldBg)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(colors.clinicalEmeraldSolid)
                        )
                        Text(
                            text = "Tersimpan",
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.clinicalEmeraldText
                        )
                    }
                }
            }

            Text(
                text = "Catatan Temuan Klinis, Kesimpulan, & Rekomendasi",
                style = KanguruTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.background)
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
            ) {
                OutlinedTextField(
                    value = notes,
                    onValueChange = onNotesChanged,
                    placeholder = {
                        Text(
                            text = "Hasil Catatan....",
                            style = KanguruTheme.typography.bodySmall,
                            color = colors.textSecondary.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 140.dp),
                    textStyle = KanguruTheme.typography.bodySmall.copy(color = colors.textPrimary),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                HorizontalDivider(color = colors.cardBorder.copy(alpha = 0.5f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = colors.textSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Draf lokal tersimpan otomatis",
                            style = KanguruTheme.typography.labelSmall,
                            color = colors.textSecondary
                        )
                    }

                    Text(
                        text = "$charCount karakter",
                        style = KanguruTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sisipkan poin:",
                    style = KanguruTheme.typography.labelSmall,
                    color = colors.textSecondary
                )

                QuickInsertChip(label = "+ Temuan", onClick = { onInsertQuickPoint("Temuan Klinis: ") })
                QuickInsertChip(label = "+ Kesimpulan", onClick = { onInsertQuickPoint("Kesimpulan: ") })
                QuickInsertChip(label = "+ Anjuran PMK", onClick = { onInsertQuickPoint("Anjuran / Terapi PMK: ") })
                QuickInsertChip(label = "+ Jadwal Kontrol", onClick = { onInsertQuickPoint("Jadwal Evaluasi Ulang: ") })
            }
        }
    }
}

@Composable
internal fun QuickInsertChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceVariant.copy(alpha = 0.6f))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = KanguruTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary
        )
    }
}

@Composable
internal fun QuickChoiceChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(colors.surface)
            .border(1.dp, colors.cardBorder, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = KanguruTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = colors.primary
        )
    }
}

/**
 * Reusable Medical Attachments Card
 */
@Composable
internal fun MedicalAttachmentsCard(
    attachmentName: String?,
    attachmentSize: String?,
    isAttachmentImage: Boolean,
    onAddPhoto: () -> Unit,
    onAddPdf: () -> Unit,
    onRemoveAttachment: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenAttachment: (() -> Unit)? = null
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.clinicalPurpleBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = null,
                        tint = colors.clinicalPurpleSolid,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = "LAMPIRAN DOKUMEN MEDIS",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Hasil cetak USG, foto retina, atau resume lab (Opsional)",
                        style = KanguruTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(96.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.background)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                        .clickable { onAddPhoto() }
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(colors.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "Ambil Foto",
                            style = KanguruTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Langsung dari kamera",
                            style = KanguruTheme.typography.labelSmall,
                            color = colors.textSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(96.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.background)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                        .clickable { onAddPdf() }
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(colors.clinicalBlueBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.UploadFile,
                                contentDescription = null,
                                tint = colors.clinicalBlueText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "Unggah PDF / File",
                            style = KanguruTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Maksimal ukuran 5MB",
                            style = KanguruTheme.typography.labelSmall,
                            color = colors.textSecondary
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = !attachmentName.isNullOrBlank(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (!attachmentName.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(colors.background)
                            .border(1.dp, colors.cardBorder, RoundedCornerShape(14.dp))
                            .clickable { onOpenAttachment?.invoke() }
                            .padding(12.dp)
                    ) {
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
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(colors.clinicalAmberBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isAttachmentImage) Icons.Default.Image else Icons.Default.Description,
                                        contentDescription = null,
                                        tint = colors.clinicalAmberSolid,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = attachmentName,
                                        style = KanguruTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = attachmentSize ?: "Siap disimpan",
                                        style = KanguruTheme.typography.labelSmall,
                                        color = colors.textSecondary
                                    )
                                }
                            }

                            IconButton(
                                onClick = onRemoveAttachment,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Hapus Berkas",
                                    tint = colors.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun ClinicalHelpDialog(
    onDismiss: () -> Unit
) {
    val colors = KanguruTheme.colors

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Panduan Resume Klinis",
                style = KanguruTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "• 4 Skrining Wajib: Merupakan penapisan standar IDAI & Kemenkes untuk setiap bayi risiko tinggi BBLR/Prematur (USG Kepala, ROP, Pendengaran, Tiroid).",
                    style = KanguruTheme.typography.bodySmall,
                    color = colors.textPrimary
                )
                Text(
                    text = "• Hasil Temuan: Catat kesimpulan dokter spesialis anak, advis terapi, atau evaluasi lanjutan.",
                    style = KanguruTheme.typography.bodySmall,
                    color = colors.textPrimary
                )
                Text(
                    text = "• Berkas Pendukung: Ambil foto resume kertas atau unggah fail PDF pemeriksaan untuk arsip digital yang aman.",
                    style = KanguruTheme.typography.bodySmall,
                    color = colors.textPrimary
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Mengerti",
                    style = KanguruTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }
        },
        containerColor = colors.surface,
        shape = RoundedCornerShape(20.dp)
    )
}
