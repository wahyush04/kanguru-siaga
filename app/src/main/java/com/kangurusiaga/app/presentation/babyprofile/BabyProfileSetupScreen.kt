package com.kangurusiaga.app.presentation.babyprofile

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) {
            onNavigateToHome()
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFFDFBF9)
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
                    Text(stringResource(R.string.baby_profile_date_picker_select), color = BrandPink, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.baby_profile_date_picker_cancel), color = TextSecondary)
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
                    fontSize = 11.sp,
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
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.baby_profile_form_guidance),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
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
                                        if (isDark) Color(0xFF3B252C) else Color(0xFFFFF1F2),
                                        if (isDark) Color(0xFF2C2520) else Color(0xFFF4F4F5)
                                    )
                                )
                            )
                            .border(2.dp, if (isDark) BrandPink.copy(alpha = 0.5f) else Color(0xFFFECDD3), CircleShape)
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
                                    fontSize = 11.sp,
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
                                contentColor = TextPrimary
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
                                fontSize = 12.sp,
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
                                contentColor = BrandPink
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
                                fontSize = 12.sp,
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
                                fontSize = 11.sp,
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
                                fontSize = 11.sp,
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
                                        color = TextTertiary,
                                        fontSize = 14.sp
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
                                fontSize = 11.sp,
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
                                    fontSize = 14.sp,
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
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = uiState.gestationalAgeWeeks,
                                onValueChange = onGestationalAgeChange,
                                placeholder = {
                                    Text(stringResource(R.string.baby_profile_placeholder_gestational_age), color = TextTertiary, fontSize = 14.sp)
                                },
                                trailingIcon = {
                                    Text(
                                        stringResource(R.string.baby_profile_unit_weeks),
                                        color = TextTertiary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
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
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = uiState.birthWeightInput,
                                        onValueChange = onBirthWeightChange,
                                        placeholder = { Text(stringResource(R.string.baby_profile_placeholder_weight_birth), fontSize = 13.sp) },
                                        trailingIcon = {
                                            Text(
                                                stringResource(R.string.baby_profile_unit_grams),
                                                color = TextTertiary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
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
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = uiState.currentWeightInput,
                                        onValueChange = onCurrentWeightChange,
                                        placeholder = { Text(stringResource(R.string.baby_profile_placeholder_weight_current), fontSize = 13.sp) },
                                        trailingIcon = {
                                            Text(
                                                stringResource(R.string.baby_profile_unit_grams),
                                                color = TextTertiary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
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
                                    .background(if (isDark) KanguruTheme.colors.errorContainer else Color(0xFFFFF1F2), RoundedCornerShape(12.dp))
                                    .border(1.dp, if (isDark) KanguruTheme.colors.errorBorder else Color(0xFFFFE4E6), RoundedCornerShape(12.dp))
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
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isDark) KanguruTheme.colors.error else Color(0xFFE11D48)
                                )
                            }
                        }
                    }

                    // 5. Jenis Kelamin Card (Dual Cards)
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
                                fontSize = 11.sp,
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
                                        .background(if (isMale) (if (isDark) Color(0xFF1E293B) else Color(0xFFEFF6FF)) else KanguruTheme.colors.surfaceVariant)
                                        .border(
                                            width = if (isMale) 2.dp else 1.dp,
                                            color = if (isMale) Color(0xFF3B82F6) else CardBorder,
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
                                                .background(Color(0xFF3B82F6)),
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
                                                .background(if (isDark) Color(0xFF1E3A8A) else Color(0xFFDBEAFE)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Male,
                                                contentDescription = null,
                                                tint = Color(0xFF3B82F6),
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = stringResource(R.string.baby_profile_gender_male),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isMale) (if (isDark) Color(0xFF60A5FA) else Color(0xFF1D4ED8)) else TextSecondary
                                        )
                                    }
                                }

                                // Perempuan Option
                                val isFemale = uiState.gender == Gender.FEMALE
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (isFemale) (if (isDark) Color(0xFF381E28) else Color(0xFFFFF1F2)) else KanguruTheme.colors.surfaceVariant)
                                        .border(
                                            width = if (isFemale) 2.dp else 1.dp,
                                            color = if (isFemale) BrandPink else CardBorder,
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
                                                .background(BrandPink),
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
                                                .background(if (isDark) Color(0xFF4C1D24) else Color(0xFFFFE4E6)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Female,
                                                contentDescription = null,
                                                tint = BrandPink,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = stringResource(R.string.baby_profile_gender_female),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isFemale) (if (isDark) BrandPink else Color(0xFFE11D48)) else TextSecondary
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
                        spotColor = Color(0xFFFF5C77).copy(alpha = 0.35f)
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
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
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
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.baby_profile_confirm_subtitle),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
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
                            if (isDark) KanguruTheme.colors.surface else Color(0xFFFFF1F2).copy(alpha = 0.5f),
                            RoundedCornerShape(16.dp)
                        )
                        .border(
                            1.dp,
                            if (isDark) CardBorder else Color(0xFFFFE4E6),
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
                                    .background(if (isDark) KanguruTheme.colors.surfaceVariant else Color(0xFFFFE4E6))
                                    .border(2.dp, if (isDark) CardBorder else Color.White, CircleShape),
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
                                        tint = if (isDark) KanguruTheme.colors.primary else Color(0xFFFB7185),
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = stringResource(R.string.baby_profile_photo_label),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextTertiary,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (uiState.photoUri != null) stringResource(R.string.baby_profile_photo_saved) else stringResource(R.string.baby_profile_photo_none),
                                    fontSize = 14.sp,
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
                                fontSize = 12.sp,
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
                                if (isDark) KanguruTheme.colors.errorContainer else Color(0xFFFFF1F2),
                                RoundedCornerShape(12.dp)
                            )
                            .border(
                                1.dp,
                                if (isDark) KanguruTheme.colors.errorBorder else Color(0xFFFFE4E6),
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
                            fontSize = 11.sp,
                            color = if (isDark) KanguruTheme.colors.error else Color(0xFFE11D48),
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
                            fontSize = 14.sp,
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
                                spotColor = Color(0xFFFF5C77).copy(alpha = 0.35f)
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
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
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
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary
        )
        Text(
            text = value,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.End
        )
    }
}
