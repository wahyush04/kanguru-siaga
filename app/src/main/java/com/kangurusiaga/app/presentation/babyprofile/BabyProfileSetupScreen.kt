package com.kangurusiaga.app.presentation.babyprofile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Male
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.domain.model.Gender
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatEpochToIndonesianDate(epochMillis: Long): String {
    val date = Date(epochMillis)
    val formatter = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
    return formatter.format(date)
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

    BabyProfileSetupScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onNameChange = viewModel::updateName,
        onGenderChange = viewModel::updateGender,
        onBirthDateChange = viewModel::updateBirthDate,
        onBirthWeightChange = viewModel::updateBirthWeight,
        onCameraClick = {
            val uri = viewModel.createTempCameraUri()
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        },
        onGalleryClick = {
            galleryLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onRemovePhoto = viewModel::onRemovePhoto,
        onNextClick = { viewModel.onNextStep() },
        onPreviousClick = {
            val hasPrevious = viewModel.onPreviousStep()
            if (!hasPrevious) {
                onNavigateBackToIntro()
            }
        },
        onJumpToStep = viewModel::jumpToStep,
        onSaveClick = viewModel::saveProfile,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BabyProfileSetupScreen(
    uiState: BabyProfileSetupUiState,
    snackbarHostState: SnackbarHostState,
    onNameChange: (String) -> Unit,
    onGenderChange: (Gender) -> Unit,
    onBirthDateChange: (Long) -> Unit,
    onBirthWeightChange: (String) -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onRemovePhoto: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onJumpToStep: (ProfileSetupStep) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePickerDialog by remember { mutableStateOf(false) }

    if (showDatePickerDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.birthDateEpochMillis
        )
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { onBirthDateChange(it) }
                        showDatePickerDialog = false
                    }
                ) {
                    Text("Pilih", color = BrandPink, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text("Batal", color = Color.Gray)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFFAF7F2),
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header with Step Indicator & Linear Progress Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                // Step counter (e.g. "1/6") centered
                Text(
                    text = "${uiState.currentStep.stepNumber}/${uiState.currentStep.totalSteps}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onPreviousClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color(0xFF1E293B)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Progress bar
                    val animatedProgress by animateFloatAsState(
                        targetValue = uiState.currentStep.stepNumber.toFloat() / uiState.currentStep.totalSteps.toFloat(),
                        label = "progress"
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFFF1F5F9))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(BrandPink)
                        )
                    }

                    Spacer(modifier = Modifier.width(36.dp)) // balance back button width
                }
            }

            // Dynamic Step Form Body
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.TopCenter
            ) {
                when (uiState.currentStep) {
                    ProfileSetupStep.NAME -> {
                        StepNameContent(
                            name = uiState.name,
                            onNameChange = onNameChange
                        )
                    }
                    ProfileSetupStep.GENDER -> {
                        StepGenderContent(
                            selectedGender = uiState.gender,
                            onGenderSelected = onGenderChange
                        )
                    }
                    ProfileSetupStep.BIRTH_DATE -> {
                        StepBirthDateContent(
                            birthDateEpochMillis = uiState.birthDateEpochMillis,
                            onCardClick = { showDatePickerDialog = true }
                        )
                    }
                    ProfileSetupStep.BIRTH_WEIGHT -> {
                        StepBirthWeightContent(
                            weightInput = uiState.birthWeightInput,
                            isBblr = uiState.isBblr,
                            onWeightChange = onBirthWeightChange
                        )
                    }
                    ProfileSetupStep.PHOTO -> {
                        StepPhotoContent(
                            photoUri = uiState.photoUri,
                            onCameraClick = onCameraClick,
                            onGalleryClick = onGalleryClick,
                            onRemovePhoto = onRemovePhoto
                        )
                    }
                    ProfileSetupStep.CONFIRMATION -> {
                        StepConfirmationContent(
                            uiState = uiState,
                            onEditPhotoClick = { onJumpToStep(ProfileSetupStep.PHOTO) }
                        )
                    }
                }
            }

            // Bottom Navigation CTA Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Secondary Button (Kembali)
                OutlinedButton(
                    onClick = onPreviousClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BrandPink.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = BrandPink
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = BrandPink,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Kembali",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Primary Button (Lanjut / Simpan Data)
                val isConfirmation = uiState.currentStep == ProfileSetupStep.CONFIRMATION
                Button(
                    onClick = {
                        if (isConfirmation) {
                            onSaveClick()
                        } else {
                            onNextClick()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPink),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (isConfirmation) "Simpan Data" else "Lanjut",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (!isConfirmation) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White,
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

// ======================== STEP CONTENTS ========================

@Composable
fun StepNameContent(
    name: String,
    onNameChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Nama Bayi",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Masukkan nama bayi sesuai dengan yang Anda inginkan.",
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Nama Bayi",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF475569),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                placeholder = {
                    Text(
                        text = "Contoh: Nirmala Endang Elis",
                        color = Color(0xFF94A3B8),
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = BrandPink,
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                ),
                singleLine = true
            )
        }
    }
}

@Composable
fun StepGenderContent(
    selectedGender: Gender,
    onGenderSelected: (Gender) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Jenis Kelamin",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Pilih jenis kelamin bayi.",
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(36.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Laki-laki
            val isMale = selectedGender == Gender.MALE
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(0.9f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFEFF6FF))
                    .border(
                        width = if (isMale) 2.dp else 1.dp,
                        color = if (isMale) Color(0xFF2563EB) else Color(0xFFBFDBFE),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable { onGenderSelected(Gender.MALE) }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Male,
                        contentDescription = "Laki-laki",
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Laki-laki",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1D4ED8)
                    )
                }
            }

            // Perempuan
            val isFemale = selectedGender == Gender.FEMALE
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(0.9f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFFFF1F2))
                    .border(
                        width = if (isFemale) 2.dp else 1.dp,
                        color = if (isFemale) BrandPink else Color(0xFFFECDD3),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable { onGenderSelected(Gender.FEMALE) }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Female,
                        contentDescription = "Perempuan",
                        tint = BrandPink,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Perempuan",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFF4D6D)
                    )
                }
            }
        }
    }
}

@Composable
fun StepBirthDateContent(
    birthDateEpochMillis: Long,
    onCardClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tanggal Lahir",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tanggal lahir digunakan untuk menghitung usia bayi dan menampilkan grafik pertumbuhan yang sesuai.",
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Tanggal Lahir",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF475569),
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(16.dp))
                    .clickable { onCardClick() }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatEpochToIndonesianDate(birthDateEpochMillis),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0F172A)
                    )
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Pilih tanggal",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StepBirthWeightContent(
    weightInput: String,
    isBblr: Boolean,
    onWeightChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Berat Lahir",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Masukkan berat badan bayi saat lahir sesuai dengan yang tercatat.",
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Berat Lahir",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF475569),
                modifier = Modifier.padding(bottom = 6.dp)
            )

            OutlinedTextField(
                value = weightInput,
                onValueChange = onWeightChange,
                placeholder = { Text("Contoh: 1850", color = Color(0xFF94A3B8)) },
                trailingIcon = {
                    Text(
                        text = "gram",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(end = 14.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = BrandPink,
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                ),
                singleLine = true
            )

            // Alert Callout for BBLR classification matching Stitch design
            AnimatedVisibility(visible = isBblr) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF0F2)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFFFE2E5))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFFF4D6D),
                            modifier = Modifier
                                .size(18.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Bayi dengan berat lahir kurang dari 2.500 gram dikategorikan sebagai BBLR (Bayi Berat Lahir Rendah).",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFFE11D48),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StepPhotoContent(
    photoUri: String?,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onRemovePhoto: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Foto Bayi",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tambahkan foto bayi untuk mempermudah identifikasi dan membuat pengalaman lebih personal.",
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Avatar Preview container matching Stitch
        Box(
            modifier = Modifier
                .size(144.dp)
                .clip(CircleShape)
                .background(Color(0xFFEEF2F6))
                .border(2.dp, Color(0xFFE2E8F0), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (photoUri != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(Uri.parse(photoUri))
                        .crossfade(true)
                        .build(),
                    contentDescription = "Foto Bayi",
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
                        contentDescription = "Pilih Foto Bayi",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Pilih Foto Bayi",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        if (photoUri != null) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onRemovePhoto) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus Foto",
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Hapus Foto",
                    fontSize = 13.sp,
                    color = Color(0xFFDC2626),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // 2 Action Buttons: Ambil Foto (Kamera) & Pilih dari Galeri
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Kamera
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                    .clickable { onCameraClick() }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Ambil Foto dengan Kamera",
                        tint = Color(0xFF334155),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ambil Foto",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B)
                    )
                }
            }

            // Galeri
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                    .clickable { onGalleryClick() }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Pilih dari Galeri",
                        tint = BrandPink,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pilih dari Galeri",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandPink
                    )
                }
            }
        }
    }
}

@Composable
fun StepConfirmationContent(
    uiState: BabyProfileSetupUiState,
    onEditPhotoClick: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Konfirmasi Data Bayi",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Pastikan data yang Anda masukkan sudah benar.",
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Top Card: Photo Card matching Stitch
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF5F7)
            ),
            border = BorderStroke(1.dp, Color(0xFFFFE2E7))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2E8F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.photoUri != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(Uri.parse(uiState.photoUri))
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Foto Bayi",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "FOTO BAYI",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF94A3B8),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (uiState.photoUri != null) "Tersimpan" else "Belum ditambahkan",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }
                }

                OutlinedButton(
                    onClick = onEditPhotoClick,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, BrandPink),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = BrandPink
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Ubah Foto",
                        tint = BrandPink,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Ubah Foto",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Data Summary Card matching Stitch
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFF1F5F9))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                SummaryRow(label = "Nama Bayi", value = uiState.name)
                HorizontalDivider(color = Color(0xFFF1F5F9))
                SummaryRow(
                    label = "Tanggal Lahir",
                    value = formatEpochToIndonesianDate(uiState.birthDateEpochMillis)
                )
                HorizontalDivider(color = Color(0xFFF1F5F9))
                SummaryRow(
                    label = "Berat Lahir",
                    value = "${uiState.birthWeightGram} gram"
                )
                HorizontalDivider(color = Color(0xFFF1F5F9))
                SummaryRow(
                    label = "Jenis Kelamin",
                    value = if (uiState.gender == Gender.MALE) "Laki-laki" else "Perempuan"
                )
            }
        }
    }
}

@Composable
fun SummaryRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF64748B)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
    }
}
