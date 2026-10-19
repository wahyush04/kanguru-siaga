package com.kangurusiaga.app.presentation.babyprofile

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.kangurusiaga.app.presentation.babyprofile.components.NotificationPermissionDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Male
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.BrandBackground
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.CardBorder
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.core.designsystem.theme.TextPrimary
import com.kangurusiaga.app.core.designsystem.theme.TextSecondary
import com.kangurusiaga.app.core.designsystem.theme.TextTertiary
import com.kangurusiaga.app.core.designsystem.theme.White
import com.kangurusiaga.app.domain.model.Gender
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatEpochToIndonesianDate(epochMillis: Long): String {
    val date = Date(epochMillis)
    val formatter = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
    return formatter.format(date)
}

fun formatWeightString(weightInput: String): String {
    val num = weightInput.filter { it.isDigit() }.toIntOrNull() ?: return weightInput
    return String.format(Locale("id", "ID"), "%,d", num).replace(',', '.')
}

@Composable
fun BabyProfileSetupRoute(
    onNavigateBackToIntro: () -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BabyProfileSetupViewModel = hiltViewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showNotificationPermissionDialog by remember { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        showNotificationPermissionDialog = false
        onNavigateToHome()
    }

    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) {
            val needsPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            } else {
                !NotificationManagerCompat.from(context).areNotificationsEnabled()
            }

            if (needsPermission) {
                showNotificationPermissionDialog = true
            } else {
                onNavigateToHome()
            }
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    // Camera launcher setup
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempCameraUri?.let { viewModel.onPhotoSelected(it) }
        }
    }

    // Photo picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { viewModel.onPhotoSelected(it) }
    }

    val onCameraClick: () -> Unit = {
        val uri = viewModel.createTempCameraUri()
        tempCameraUri = uri
        cameraLauncher.launch(uri)
    }

    val onGalleryClick: () -> Unit = {
        galleryLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    // Handle back button behavior:
    // If in CONFIRMATION step -> returns to FORM
    // If in FORM step -> returns to ProfileIntro
    BackHandler(enabled = true) {
        if (uiState.currentStep == ProfileSetupStep.CONFIRMATION) {
            viewModel.backToForm()
        } else {
            onNavigateBackToIntro()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { com.kangurusiaga.app.core.designsystem.component.KanguruSnackbarHost(snackbarHostState) },
        containerColor = BrandBackground
    ) { innerPadding ->
        when (uiState.currentStep) {
            ProfileSetupStep.FORM -> {
                BabyProfileFormScreen(
                    uiState = uiState,
                    onBackClick = onNavigateBackToIntro,
                    onNameChange = viewModel::updateName,
                    onGenderChange = viewModel::updateGender,
                    onBirthDateChange = viewModel::updateBirthDate,
                    onGestationalAgeChange = viewModel::updateGestationalAge,
                    onBirthWeightChange = viewModel::updateBirthWeight,
                    onCurrentWeightChange = viewModel::updateCurrentWeight,
                    onBirthLengthChange = viewModel::updateBirthLength,
                    onCurrentLengthChange = viewModel::updateCurrentLength,
                    onBirthHeadCircumferenceChange = viewModel::updateBirthHeadCircumference,
                    onCurrentHeadCircumferenceChange = viewModel::updateCurrentHeadCircumference,
                    onCameraClick = onCameraClick,
                    onGalleryClick = onGalleryClick,
                    onRemovePhoto = viewModel::onRemovePhoto,
                    onProceedToConfirmation = { viewModel.proceedToConfirmation() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            ProfileSetupStep.CONFIRMATION -> {
                BabyProfileConfirmationScreen(
                    uiState = uiState,
                    onBackClick = { viewModel.backToForm() },
                    onEditPhotoClick = onGalleryClick,
                    onSaveClick = { viewModel.saveProfile() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }

    if (showNotificationPermissionDialog) {
        NotificationPermissionDialog(
            onConfirm = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    showNotificationPermissionDialog = false
                    onNavigateToHome()
                }
            },
            onDismiss = {
                showNotificationPermissionDialog = false
                onNavigateToHome()
            }
        )
    }
}

/**
 * Screen 4: Kanguru Siaga - Formulir Lengkap Data Bayi
 * Single-page consolidated form replacing multi-step inputs.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BabyProfileFormScreen(
    uiState: BabyProfileSetupUiState,
    onBackClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onGenderChange: (Gender) -> Unit,
    onBirthDateChange: (Long) -> Unit,
    onGestationalAgeChange: (String) -> Unit,
    onBirthWeightChange: (String) -> Unit,
    onCurrentWeightChange: (String) -> Unit,
    onBirthLengthChange: (String) -> Unit,
    onCurrentLengthChange: (String) -> Unit,
    onBirthHeadCircumferenceChange: (String) -> Unit,
    onCurrentHeadCircumferenceChange: (String) -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onRemovePhoto: () -> Unit,
    onProceedToConfirmation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.birthDateEpochMillis
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selected ->
                            onBirthDateChange(selected)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(
                        text = stringResource(R.string.baby_profile_date_picker_select),
                        style = KanguruTheme.typography.labelLarge,
                        color = BrandPink,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(
                        text = stringResource(R.string.baby_profile_date_picker_cancel),
                        style = KanguruTheme.typography.labelLarge,
                        color = TextSecondary
                    )
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val isDark = KanguruTheme.colors.isDark

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Navigation Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.common_back),
                        tint = TextPrimary
                    )
                }
                Text(
                    text = stringResource(R.string.baby_profile_header_tag),
                    style = KanguruTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextTertiary,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.size(40.dp))
            }

            // Scrollable Form Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 90.dp)
            ) {
                // Page Title & Guidance
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.baby_profile_form_title),
                        style = KanguruTheme.typography.headlineMedium,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.baby_profile_form_guidance),
                        style = KanguruTheme.typography.bodySmall,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                // ==========================================
                // Photo Section
                // ==========================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar Preview with Camera Badge
                    Box(
                        modifier = Modifier
                            .size(112.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        KanguruTheme.colors.onboardingCardBgStart,
                                        KanguruTheme.colors.onboardingCardBgEnd
                                    )
                                )
                            )
                            .border(2.dp, KanguruTheme.colors.avatarRingEnd, CircleShape)
                            .clickable { onGalleryClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.photoUri != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(uiState.photoUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = stringResource(R.string.baby_profile_photo_desc),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = TextTertiary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.baby_profile_photo_select),
                                    style = KanguruTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextTertiary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Photo Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onCameraClick,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 42.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, CardBorder),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = TextPrimary,
                                containerColor = White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.baby_profile_photo_take),
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        OutlinedButton(
                            onClick = onGalleryClick,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 42.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, BrandPink),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = BrandPink,
                                containerColor = White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = BrandPink,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.baby_profile_photo_gallery),
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (uiState.photoUri != null) {
                        TextButton(
                            onClick = onRemovePhoto,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = KanguruTheme.colors.error,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.baby_profile_photo_delete),
                                style = KanguruTheme.typography.labelSmall,
                                color = KanguruTheme.colors.error,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // ==========================================
                // Form Inputs Section
                // ==========================================
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Nama Bayi Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(KanguruTheme.colors.surface, RoundedCornerShape(16.dp))
                            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.baby_profile_label_name),
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = uiState.name,
                                onValueChange = onNameChange,
                                placeholder = {
                                    Text(
                                        stringResource(R.string.baby_profile_placeholder_name),
                                        style = KanguruTheme.typography.bodySmall,
                                        color = TextTertiary
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandPink,
                                    unfocusedBorderColor = CardBorder,
                                    focusedContainerColor = KanguruTheme.colors.surface,
                                    unfocusedContainerColor = KanguruTheme.colors.surfaceVariant,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedPlaceholderColor = TextTertiary,
                                    unfocusedPlaceholderColor = TextTertiary
                                ),
                                singleLine = true
                            )
                        }
                    }

                    // 2. Tanggal Lahir Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(KanguruTheme.colors.surface, RoundedCornerShape(16.dp))
                            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                            .clickable { showDatePicker = true }
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.baby_profile_label_birth_date),
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(KanguruTheme.colors.surfaceVariant, RoundedCornerShape(12.dp))
                                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 14.dp, vertical = 13.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = formatEpochToIndonesianDate(uiState.birthDateEpochMillis),
                                    style = KanguruTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = stringResource(R.string.baby_profile_cd_select_date),
                                    tint = BrandPink,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // 3. Usia Gestasi (Usia Kehamilan) Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(KanguruTheme.colors.surface, RoundedCornerShape(16.dp))
                            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.baby_profile_label_gestational_age),
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = uiState.gestationalAgeWeeks,
                                onValueChange = onGestationalAgeChange,
                                placeholder = {
                                    Text(stringResource(R.string.baby_profile_placeholder_gestational_age), style = KanguruTheme.typography.bodySmall, color = TextTertiary)
                                },
                                trailingIcon = {
                                    Text(
                                        stringResource(R.string.baby_profile_unit_weeks),
                                        style = KanguruTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextTertiary,
                                        modifier = Modifier.padding(end = 12.dp)
                                    )
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandPink,
                                    unfocusedBorderColor = CardBorder,
                                    focusedContainerColor = KanguruTheme.colors.surface,
                                    unfocusedContainerColor = KanguruTheme.colors.surfaceVariant,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedPlaceholderColor = TextTertiary,
                                    unfocusedPlaceholderColor = TextTertiary
                                ),
                                singleLine = true
                            )
                        }
                    }

                    // 4. Berat Lahir & Berat Sekarang + BBLR Alert
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(KanguruTheme.colors.surface, RoundedCornerShape(16.dp))
                            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Berat Lahir
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.baby_profile_label_birth_weight),
                                        style = KanguruTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = uiState.birthWeightInput,
                                        onValueChange = onBirthWeightChange,
                                        placeholder = { Text(stringResource(R.string.baby_profile_placeholder_weight_birth), style = KanguruTheme.typography.bodySmall, color = TextTertiary) },
                                        trailingIcon = {
                                            Text(
                                                stringResource(R.string.baby_profile_unit_grams),
                                                style = KanguruTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = TextTertiary,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = BrandPink,
                                            unfocusedBorderColor = CardBorder,
                                            focusedContainerColor = KanguruTheme.colors.surface,
                                            unfocusedContainerColor = KanguruTheme.colors.surfaceVariant,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedPlaceholderColor = TextTertiary,
                                            unfocusedPlaceholderColor = TextTertiary
                                        ),
                                        singleLine = true
                                    )
                                }

                                // Berat Sekarang
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.baby_profile_label_current_weight),
                                        style = KanguruTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = uiState.currentWeightInput,
                                        onValueChange = onCurrentWeightChange,
                                        placeholder = { Text(stringResource(R.string.baby_profile_placeholder_weight_current), style = KanguruTheme.typography.bodySmall, color = TextTertiary) },
                                        trailingIcon = {
                                            Text(
                                                stringResource(R.string.baby_profile_unit_grams),
                                                style = KanguruTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = TextTertiary,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = BrandPink,
                                            unfocusedBorderColor = CardBorder,
                                            focusedContainerColor = KanguruTheme.colors.surface,
                                            unfocusedContainerColor = KanguruTheme.colors.surfaceVariant,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedPlaceholderColor = TextTertiary,
                                            unfocusedPlaceholderColor = TextTertiary
                                        ),
                                        singleLine = true
                                    )
                                }
                            }

                            // Notice Edukasi BBLR Banner
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(KanguruTheme.colors.errorContainer, RoundedCornerShape(12.dp))
                                    .border(1.dp, KanguruTheme.colors.errorBorder, RoundedCornerShape(12.dp))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = BrandPink,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(top = 1.dp)
                                 )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.baby_profile_bblr_notice),
                                    style = KanguruTheme.typography.labelSmall,
                                    color = KanguruTheme.colors.error
                                )
                            }
                        }
                    }

                    // 5. Panjang Badan Card (Panjang Lahir & Panjang Sekarang)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(KanguruTheme.colors.surface, RoundedCornerShape(16.dp))
                            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Panjang Lahir
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.baby_profile_label_birth_length),
                                        style = KanguruTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = uiState.birthLengthInput,
                                        onValueChange = onBirthLengthChange,
                                        placeholder = {
                                            Text(
                                                stringResource(R.string.baby_profile_placeholder_length_birth),
                                                style = KanguruTheme.typography.bodySmall,
                                                color = TextTertiary
                                            )
                                        },
                                        trailingIcon = {
                                            Text(
                                                stringResource(R.string.baby_profile_unit_cm),
                                                style = KanguruTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = TextTertiary,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = BrandPink,
                                            unfocusedBorderColor = CardBorder,
                                            focusedContainerColor = KanguruTheme.colors.surface,
                                            unfocusedContainerColor = KanguruTheme.colors.surfaceVariant,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedPlaceholderColor = TextTertiary,
                                            unfocusedPlaceholderColor = TextTertiary
                                        ),
                                        singleLine = true
                                    )
                                }

                                // Panjang Sekarang
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.baby_profile_label_current_length),
                                        style = KanguruTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = uiState.currentLengthInput,
                                        onValueChange = onCurrentLengthChange,
                                        placeholder = {
                                            Text(
                                                stringResource(R.string.baby_profile_placeholder_length_current),
                                                style = KanguruTheme.typography.bodySmall,
                                                color = TextTertiary
                                            )
                                        },
                                        trailingIcon = {
                                            Text(
                                                stringResource(R.string.baby_profile_unit_cm),
                                                style = KanguruTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = TextTertiary,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = BrandPink,
                                            unfocusedBorderColor = CardBorder,
                                            focusedContainerColor = KanguruTheme.colors.surface,
                                            unfocusedContainerColor = KanguruTheme.colors.surfaceVariant,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedPlaceholderColor = TextTertiary,
                                            unfocusedPlaceholderColor = TextTertiary
                                        ),
                                        singleLine = true
                                    )
                                }
                            }
                        }
                    }

                    // 6. Lingkar Kepala Card (Lingkar Kepala Lahir & Lingkar Kepala Sekarang)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(KanguruTheme.colors.surface, RoundedCornerShape(16.dp))
                            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Lingkar Kepala Lahir
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.baby_profile_label_birth_head),
                                        style = KanguruTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = uiState.birthHeadCircumferenceInput,
                                        onValueChange = onBirthHeadCircumferenceChange,
                                        placeholder = {
                                            Text(
                                                stringResource(R.string.baby_profile_placeholder_head_birth),
                                                style = KanguruTheme.typography.bodySmall,
                                                color = TextTertiary
                                            )
                                        },
                                        trailingIcon = {
                                            Text(
                                                stringResource(R.string.baby_profile_unit_cm),
                                                style = KanguruTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = TextTertiary,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = BrandPink,
                                            unfocusedBorderColor = CardBorder,
                                            focusedContainerColor = KanguruTheme.colors.surface,
                                            unfocusedContainerColor = KanguruTheme.colors.surfaceVariant,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedPlaceholderColor = TextTertiary,
                                            unfocusedPlaceholderColor = TextTertiary
                                        ),
                                        singleLine = true
                                    )
                                }

                                // Lingkar Kepala Sekarang
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.baby_profile_label_current_head),
                                        style = KanguruTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = uiState.currentHeadCircumferenceInput,
                                        onValueChange = onCurrentHeadCircumferenceChange,
                                        placeholder = {
                                            Text(
                                                stringResource(R.string.baby_profile_placeholder_head_current),
                                                style = KanguruTheme.typography.bodySmall,
                                                color = TextTertiary
                                            )
                                        },
                                        trailingIcon = {
                                            Text(
                                                stringResource(R.string.baby_profile_unit_cm),
                                                style = KanguruTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = TextTertiary,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = BrandPink,
                                            unfocusedBorderColor = CardBorder,
                                            focusedContainerColor = KanguruTheme.colors.surface,
                                            unfocusedContainerColor = KanguruTheme.colors.surfaceVariant,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedPlaceholderColor = TextTertiary,
                                            unfocusedPlaceholderColor = TextTertiary
                                        ),
                                        singleLine = true
                                    )
                                }
                            }
                        }
                    }

                    // 7. Jenis Kelamin Card (Dual Cards)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(KanguruTheme.colors.surface, RoundedCornerShape(16.dp))
                            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.baby_profile_label_gender),
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Laki-laki Option
                                val isMale = uiState.gender == Gender.MALE
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (isMale) KanguruTheme.colors.genderMaleActiveBackground else KanguruTheme.colors.surfaceVariant)
                                        .border(
                                            width = if (isMale) 2.dp else 1.dp,
                                            color = if (isMale) KanguruTheme.colors.genderMaleActiveBorder else CardBorder,
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        .clickable { onGenderChange(Gender.MALE) }
                                        .padding(vertical = 14.dp, horizontal = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isMale) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(end = 4.dp, top = 0.dp)
                                                .size(18.dp)
                                                .clip(CircleShape)
                                                .background(KanguruTheme.colors.genderMaleActiveBorder),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = White,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(if (isMale) KanguruTheme.colors.genderMaleActiveIconContainer else KanguruTheme.colors.genderMaleInactiveIconContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Male,
                                                contentDescription = null,
                                                tint = if (isMale) KanguruTheme.colors.genderMaleIcon else KanguruTheme.colors.genderMaleInactiveIcon,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = stringResource(R.string.baby_profile_gender_male),
                                            style = KanguruTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isMale) KanguruTheme.colors.genderMaleActiveText else TextSecondary
                                        )
                                    }
                                }

                                // Perempuan Option
                                val isFemale = uiState.gender == Gender.FEMALE
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (isFemale) KanguruTheme.colors.genderFemaleActiveBackground else KanguruTheme.colors.surfaceVariant)
                                        .border(
                                            width = if (isFemale) 2.dp else 1.dp,
                                            color = if (isFemale) KanguruTheme.colors.genderFemaleActiveBorder else CardBorder,
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        .clickable { onGenderChange(Gender.FEMALE) }
                                        .padding(vertical = 14.dp, horizontal = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isFemale) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(end = 4.dp, top = 0.dp)
                                                .size(18.dp)
                                                .clip(CircleShape)
                                                .background(KanguruTheme.colors.genderFemaleActiveBorder),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = White,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(if (isFemale) KanguruTheme.colors.genderFemaleActiveIconContainer else KanguruTheme.colors.genderFemaleInactiveIconContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Female,
                                                contentDescription = null,
                                                tint = if (isFemale) KanguruTheme.colors.genderFemaleIcon else KanguruTheme.colors.genderFemaleInactiveIcon,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = stringResource(R.string.baby_profile_gender_female),
                                            style = KanguruTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isFemale) KanguruTheme.colors.genderFemaleActiveText else TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // Bottom Sticky Action Panel
        // ==========================================
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            BrandBackground.copy(alpha = 0f),
                            BrandBackground.copy(alpha = 0.95f),
                            BrandBackground
                        )
                    )
                )
                .padding(horizontal = 24.dp, vertical = 14.dp)
        ) {
            Button(
                onClick = onProceedToConfirmation,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = BrandPink.copy(alpha = 0.35f)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPink)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.baby_profile_btn_save),
                        style = KanguruTheme.typography.titleSmall,
                        color = White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = stringResource(R.string.baby_profile_cd_next),
                        tint = White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Screen 5: Kanguru Siaga - Konfirmasi Data Bayi
 * Summary confirmation screen before saving to local database and navigating to Home.
 */
@Composable
fun BabyProfileConfirmationScreen(
    uiState: BabyProfileSetupUiState,
    onBackClick: () -> Unit,
    onEditPhotoClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = KanguruTheme.isDark
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Top Navigation Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back),
                            tint = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Header Titles
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.baby_profile_confirm_title),
                        style = KanguruTheme.typography.headlineMedium,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.baby_profile_confirm_subtitle),
                        style = KanguruTheme.typography.bodySmall,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Photo Summary Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isDark) KanguruTheme.colors.surface else KanguruTheme.colors.primaryContainer.copy(alpha = 0.5f),
                            RoundedCornerShape(16.dp)
                        )
                        .border(
                            1.dp,
                            if (isDark) CardBorder else KanguruTheme.colors.primaryBorder,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Baby Avatar Thumbnail
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(if (isDark) KanguruTheme.colors.surfaceVariant else KanguruTheme.colors.primaryContainer)
                                    .border(2.dp, if (isDark) CardBorder else White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (uiState.photoUri != null) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(uiState.photoUri)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = stringResource(R.string.baby_profile_photo_desc),
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        tint = if (isDark) KanguruTheme.colors.primary else KanguruTheme.colors.categoryRose,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = stringResource(R.string.baby_profile_photo_label),
                                    style = KanguruTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextTertiary,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (uiState.photoUri != null) stringResource(R.string.baby_profile_photo_saved) else stringResource(R.string.baby_profile_photo_none),
                                    style = KanguruTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }
                        }

                        // Outlined "Ubah Foto" Button
                        OutlinedButton(
                            onClick = onEditPhotoClick,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, BrandPink),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = BrandPink
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                horizontal = 12.dp,
                                vertical = 6.dp
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.baby_profile_photo_edit),
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Attributes Table Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = CardBorder,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .background(KanguruTheme.colors.surface)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ConfirmationDataRow(
                            label = stringResource(R.string.baby_profile_confirm_label_name),
                            value = uiState.name
                        )
                        HorizontalDivider(
                            thickness = 0.8.dp,
                            color = CardBorder
                        )

                        ConfirmationDataRow(
                            label = stringResource(R.string.baby_profile_confirm_label_birth_date),
                            value = formatEpochToIndonesianDate(uiState.birthDateEpochMillis)
                        )
                        HorizontalDivider(
                            thickness = 0.8.dp,
                            color = CardBorder
                        )

                        ConfirmationDataRow(
                            label = stringResource(R.string.baby_profile_confirm_label_gestational_age),
                            value = stringResource(R.string.baby_profile_value_weeks, uiState.gestationalAgeWeeks)
                        )
                        HorizontalDivider(
                            thickness = 0.8.dp,
                            color = CardBorder
                        )

                        ConfirmationDataRow(
                            label = stringResource(R.string.baby_profile_confirm_label_birth_weight),
                            value = stringResource(R.string.baby_profile_value_grams, formatWeightString(uiState.birthWeightInput))
                        )
                        HorizontalDivider(
                            thickness = 0.8.dp,
                            color = CardBorder
                        )

                        ConfirmationDataRow(
                            label = stringResource(R.string.baby_profile_confirm_label_current_weight),
                            value = stringResource(R.string.baby_profile_value_grams, formatWeightString(uiState.currentWeightInput))
                        )
                        HorizontalDivider(
                            thickness = 0.8.dp,
                            color = CardBorder
                        )

                        if (uiState.birthLengthInput.isNotBlank()) {
                            ConfirmationDataRow(
                                label = stringResource(R.string.baby_profile_confirm_label_birth_length),
                                value = stringResource(R.string.baby_profile_value_cm, uiState.birthLengthInput.trim())
                            )
                            HorizontalDivider(
                                thickness = 0.8.dp,
                                color = CardBorder
                            )
                        }

                        if (uiState.currentLengthInput.isNotBlank()) {
                            ConfirmationDataRow(
                                label = stringResource(R.string.baby_profile_confirm_label_current_length),
                                value = stringResource(R.string.baby_profile_value_cm, uiState.currentLengthInput.trim())
                            )
                            HorizontalDivider(
                                thickness = 0.8.dp,
                                color = CardBorder
                            )
                        }

                        if (uiState.birthHeadCircumferenceInput.isNotBlank()) {
                            ConfirmationDataRow(
                                label = stringResource(R.string.baby_profile_confirm_label_birth_head),
                                value = stringResource(R.string.baby_profile_value_cm, uiState.birthHeadCircumferenceInput.trim())
                            )
                            HorizontalDivider(
                                thickness = 0.8.dp,
                                color = CardBorder
                            )
                        }

                        if (uiState.currentHeadCircumferenceInput.isNotBlank()) {
                            ConfirmationDataRow(
                                label = stringResource(R.string.baby_profile_confirm_label_current_head),
                                value = stringResource(R.string.baby_profile_value_cm, uiState.currentHeadCircumferenceInput.trim())
                            )
                            HorizontalDivider(
                                thickness = 0.8.dp,
                                color = CardBorder
                            )
                        }

                        ConfirmationDataRow(
                            label = stringResource(R.string.baby_profile_confirm_label_gender),
                            value = if (uiState.gender == Gender.MALE) stringResource(R.string.baby_profile_gender_male) else if (uiState.gender == Gender.FEMALE) stringResource(R.string.baby_profile_gender_female) else "-"
                        )
                    }
                }

                // Optional: BBLR Status Badge
                if (uiState.isBblr) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                KanguruTheme.colors.errorContainer,
                                RoundedCornerShape(12.dp)
                            )
                            .border(
                                1.dp,
                                KanguruTheme.colors.errorBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = if (isDark) KanguruTheme.colors.error else BrandPink,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.baby_profile_confirm_bblr_status, formatWeightString(uiState.birthWeightInput)),
                            style = KanguruTheme.typography.labelSmall,
                            color = KanguruTheme.colors.error,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Bottom Dual Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Outlined Kembali Button
                    OutlinedButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 50.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, CardBorder),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.baby_profile_btn_back),
                            style = KanguruTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Filled Simpan Data Button
                    Button(
                        onClick = onSaveClick,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 50.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(14.dp),
                                spotColor = BrandPink.copy(alpha = 0.35f)
                            ),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPink),
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.baby_profile_btn_save),
                                style = KanguruTheme.typography.titleSmall,
                                color = White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfirmationDataRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = KanguruTheme.typography.bodySmall,
            color = TextSecondary
        )
        Text(
            text = value,
            style = KanguruTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.End
        )
    }
}
