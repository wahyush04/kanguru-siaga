package com.kangurusiaga.app.presentation.pmk.screening

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kangurusiaga.app.core.designsystem.component.KanguruSnackbarHost
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme

@Composable
fun CustomScreeningInputResultRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CustomScreeningInputResultViewModel = hiltViewModel()
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

    CustomScreeningInputResultScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onCustomTitleChanged = viewModel::updateCustomTitle,
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
fun CustomScreeningInputResultScreen(
    uiState: CustomScreeningInputResultUiState,
    onNavigateBack: () -> Unit,
    onCustomTitleChanged: (String) -> Unit,
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

            // Section 1: NAMA PENAPISAN * (Wajib diisi) dengan Input Text Field & Chip Cepat
            CustomScreeningTitleCard(
                customTitle = uiState.customTitle,
                onCustomTitleChanged = onCustomTitleChanged
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
 * Card Input Nama Penapisan Kustom
 * Sesuai persis dengan gambar referensi Stitch (media_1794281296129_0cf2ea02.png)
 */
@Composable
private fun CustomScreeningTitleCard(
    customTitle: String,
    onCustomTitleChanged: (String) -> Unit,
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
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "NAMA PENAPISAN",
                        style = KanguruTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "*",
                        style = KanguruTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                }

                Text(
                    text = "Wajib diisi",
                    style = KanguruTheme.typography.labelSmall,
                    color = colors.textSecondary
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.background)
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    BasicTextField(
                        value = customTitle,
                        onValueChange = onCustomTitleChanged,
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        textStyle = KanguruTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        ),
                        cursorBrush = SolidColor(colors.primary),
                        decorationBox = { innerTextField ->
                            Box(contentAlignment = Alignment.CenterStart) {
                                if (customTitle.isEmpty()) {
                                    Text(
                                        text = "Contoh: Ekokardiografi, USG Abdomen...",
                                        style = KanguruTheme.typography.bodySmall,
                                        color = colors.textSecondary.copy(alpha = 0.6f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Pilihan Cepat Tindakan Spesialis:",
                    style = KanguruTheme.typography.labelSmall,
                    color = colors.textSecondary
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuickChoiceChip(
                        label = "+ Ekokardiografi",
                        onClick = { onCustomTitleChanged("USG Ekokardiografi") }
                    )
                    QuickChoiceChip(
                        label = "+ USG Abdomen / Ginjal",
                        onClick = { onCustomTitleChanged("USG Abdomen / Ginjal") }
                    )
                    QuickChoiceChip(
                        label = "+ Evaluasi Neurologi",
                        onClick = { onCustomTitleChanged("Evaluasi Neurologi Anak") }
                    )
                    QuickChoiceChip(
                        label = "+ Skrining Gigi / Oral",
                        onClick = { onCustomTitleChanged("Skrining Gigi & Rongga Mulut") }
                    )
                    QuickChoiceChip(
                        label = "+ Lab Darah Lengkap",
                        onClick = { onCustomTitleChanged("Laboratorium Darah Lengkap") }
                    )
                }
            }
        }
    }
}

