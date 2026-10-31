package com.kangurusiaga.app.presentation.pmk.screening

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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Hearing
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
        onAddPhoto = viewModel::addPhotoAttachment,
        onAddPdf = viewModel::addPdfAttachment,
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Button(
                        onClick = onSave,
                        enabled = !uiState.isSaving,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary,
                            contentColor = colors.onPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = if (uiState.isSaving) "Menyimpan..." else "Simpan Hasil & Resume Medis",
                            style = KanguruTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = colors.onPrimary
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Patient Profile Card
            PatientProfileSummaryCard(uiState = uiState)

            // Section 1: Modalitas Skrining (2x2 Grid)
            ScreeningModalitySelectorCard(
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
                onAddPhoto = onAddPhoto,
                onAddPdf = onAddPdf,
                onRemoveAttachment = onRemoveAttachment
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Help Dialog
    if (uiState.helpDialogVisible) {
        AlertDialog(
            onDismissRequest = { onSetHelpDialogVisible(false) },
            confirmButton = {
                TextButton(onClick = { onSetHelpDialogVisible(false) }) {
                    Text(text = "Mengerti", color = colors.primary, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text(
                    text = "Pedoman Input Resume Penapisan",
                    style = KanguruTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            },
            text = {
                Text(
                    text = "Laman ini digunakan dokter spesialis atau tenaga kesehatan untuk mencatat ringkasan temuan organ vital (USG Kepala, Mata/ROP, Pendengaran OAE/BERA, dan Lab Tiroid). Anda dapat melampirkan foto hasil atau PDF rujukan untuk arsip rekam medis keluarga.",
                    style = KanguruTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            },
            containerColor = colors.cardBackground
        )
    }
}

@Composable
private fun PatientProfileSummaryCard(
    uiState: ScreeningInputResultUiState,
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
            // Gradient Initials Box
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
                    text = uiState.babyInitials,
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
                        text = uiState.babyName,
                        style = KanguruTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (uiState.isMale) colors.clinicalBlueBg else colors.clinicalRoseBg)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (uiState.isMale) "♂ ${uiState.genderDisplay}" else "♀ ${uiState.genderDisplay}",
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (uiState.isMale) colors.clinicalBlueText else colors.clinicalRoseText
                        )
                    }
                }

                Text(
                    text = "PMA: ${uiState.pmaWeeksDisplay} • Lahir: ${uiState.birthGestationalDisplay} (${uiState.birthWeightDisplay})",
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

@Composable
private fun ScreeningModalitySelectorCard(
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PILIH JENIS PENAPISAN (IDAI / KEMENKES)",
                    style = KanguruTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.textSecondary
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.primaryContainer)
                        .border(1.dp, colors.clinicalRoseBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Wajib 4 Skrining",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                }
            }

            // 2x2 Grid of screening options
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModalityTile(
                        title = "USG Kepala",
                        subtitle = "Neurosonografi",
                        icon = Icons.Default.Psychology,
                        iconBg = colors.clinicalBlueBg,
                        iconTint = colors.clinicalBlueText,
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

@Composable
private fun ModalityTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: androidx.compose.ui.graphics.Color,
    iconTint: androidx.compose.ui.graphics.Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) colors.primaryContainer.copy(alpha = 0.5f) else colors.surface)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) colors.primary else colors.cardBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
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
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Text(
                    text = subtitle,
                    style = KanguruTheme.typography.labelSmall,
                    color = if (isSelected) colors.primary.copy(alpha = 0.8f) else colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ClinicalNotesEditorCard(
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
            // Header
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
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.clinicalEmeraldBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
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

            // Textarea container
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
                        focusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                        unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                        focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                        unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent
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

            // Quick Insertion Helper Pills
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
private fun QuickInsertChip(
    label: String,
    onClick: () -> Unit
) {
    val colors = KanguruTheme.colors

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceVariant)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = KanguruTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = colors.textPrimary
        )
    }
}

@Composable
private fun MedicalAttachmentsCard(
    attachmentName: String?,
    attachmentSize: String?,
    onAddPhoto: () -> Unit,
    onAddPdf: () -> Unit,
    onRemoveAttachment: () -> Unit,
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
                        style = KanguruTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                }
            }

            // Two action buttons for upload
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                UploadBox(
                    title = "Ambil Foto",
                    subtitle = "Langsung dari kamera",
                    icon = Icons.Default.CameraAlt,
                    iconTint = colors.primary,
                    onClick = onAddPhoto,
                    modifier = Modifier.weight(1f)
                )

                UploadBox(
                    title = "Unggah PDF / File",
                    subtitle = "Maksimal ukuran 10MB",
                    icon = Icons.Default.UploadFile,
                    iconTint = colors.clinicalBlueText,
                    onClick = onAddPdf,
                    modifier = Modifier.weight(1f)
                )
            }

            // Attached Item Preview (if present)
            if (attachmentName != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceVariant)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(colors.clinicalAmberBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = colors.clinicalAmberSolid,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
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

@Composable
private fun UploadBox(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(colors.background)
            .border(1.dp, colors.cardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(colors.surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Text(
                text = title,
                style = KanguruTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                textAlign = TextAlign.Center
            )

            Text(
                text = subtitle,
                style = KanguruTheme.typography.labelSmall,
                color = colors.textSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
