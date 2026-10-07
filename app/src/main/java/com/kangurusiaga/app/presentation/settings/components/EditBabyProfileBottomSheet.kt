package com.kangurusiaga.app.presentation.settings.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.core.designsystem.theme.TextPrimary
import com.kangurusiaga.app.core.designsystem.theme.TextSecondary
import com.kangurusiaga.app.core.designsystem.theme.TextTertiary
import com.kangurusiaga.app.domain.model.Gender
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBabyProfileBottomSheet(
    name: String,
    gender: Gender,
    birthDateEpoch: Long,
    birthWeightInput: String,
    photoUri: String?,
    errorMessage: String?,
    isSaving: Boolean,
    onNameChange: (String) -> Unit,
    onGenderChange: (Gender) -> Unit,
    onBirthDateChange: (Long) -> Unit,
    onBirthWeightChange: (String) -> Unit,
    onPhotoSelected: (Uri) -> Unit,
    createTempCameraUri: () -> Uri,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val primaryTextColor = TextPrimary
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDatePicker by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onPhotoSelected(uri)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            onPhotoSelected(tempCameraUri!!)
        }
    }

    val dateFormatter = remember { SimpleDateFormat("d MMMM yyyy", Locale("id", "ID")) }
    val formattedDate = if (birthDateEpoch > 0) dateFormatter.format(Date(birthDateEpoch)) else ""

    val now = System.currentTimeMillis()
    val diffMillis = (now - birthDateEpoch).coerceAtLeast(0L)
    val diffDays = (diffMillis / (1000L * 60 * 60 * 24)).toInt()
    val weeks = diffDays / 7
    val months = diffDays / 30

    val weightGram = birthWeightInput.toIntOrNull() ?: 0
    val isBblr = weightGram in 1..<2500

    val formFieldContainerColor = KanguruTheme.colors.inputBackground
    val formFieldFocusedContainerColor = KanguruTheme.colors.inputFocusedBackground
    val formFieldBorderColor = KanguruTheme.colors.inputBorder

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = KanguruTheme.colors.surface,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 48.dp, height = 5.dp)
                        .background(KanguruTheme.colors.dragHandle, CircleShape)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // ==========================================
            // MODAL TOP HEADER (Stitch Layout)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.edit_baby_title),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Surface(
                            shape = CircleShape,
                            color = KanguruTheme.colors.actionPillPrimaryBackground,
                            border = BorderStroke(1.dp, KanguruTheme.colors.primaryBorder),
                            modifier = Modifier.heightIn(min = 22.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.edit_baby_badge),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = KanguruTheme.colors.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = stringResource(R.string.edit_baby_subtitle),
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Close Button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(KanguruTheme.colors.closeButtonBackground)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = KanguruTheme.colors.closeButtonTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            HorizontalDivider(
                color = KanguruTheme.colors.divider,
                thickness = 1.dp
            )

            // ==========================================
            // SCROLLABLE FORM BODY
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // SECTION: Edit Photo & Quick Actions
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.size(96.dp)) {
                        // Baby Photo Frame with Coral Gradient Border
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            KanguruTheme.colors.avatarRingStart,
                                            KanguruTheme.colors.avatarRingEnd
                                        )
                                    )
                                )
                                .padding(3.dp)
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                shape = CircleShape,
                                color = KanguruTheme.colors.avatarInnerBackground,
                                border = BorderStroke(2.dp, KanguruTheme.colors.surface)
                            ) {
                                if (!photoUri.isNullOrBlank()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(photoUri)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Foto Bayi",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = KanguruTheme.colors.primary,
                                            modifier = Modifier.size(52.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Edit Badge Icon (Camera)
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(KanguruTheme.colors.primary)
                                .border(2.dp, KanguruTheme.colors.surface, CircleShape)
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Ganti foto",
                                tint = KanguruTheme.colors.onPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    // Quick Action Buttons (Always visible as seen in Stitch)
                    Row(
                        modifier = Modifier.padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Ambil Foto
                        Surface(
                            shape = CircleShape,
                            color = KanguruTheme.colors.actionPillPrimaryBackground,
                            modifier = Modifier
                                .heightIn(min = 32.dp)
                                .clickable {
                                    val uri = createTempCameraUri()
                                    tempCameraUri = uri
                                    cameraLauncher.launch(uri)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = KanguruTheme.colors.actionPillPrimaryText,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Ambil Foto",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = KanguruTheme.colors.actionPillPrimaryText
                                )
                            }
                        }

                        // Pilih Galeri
                        Surface(
                            shape = CircleShape,
                            color = KanguruTheme.colors.actionPillSecondaryBackground,
                            modifier = Modifier
                                .heightIn(min = 32.dp)
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    tint = KanguruTheme.colors.actionPillSecondaryText,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Pilih Galeri",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = KanguruTheme.colors.actionPillSecondaryText
                                )
                            }
                        }
                    }
                }

                // 1. Input: Nama Lengkap Bayi
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.edit_baby_label_name).uppercase(),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = " *",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = KanguruTheme.colors.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = onNameChange,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        placeholder = {
                            Text(
                                stringResource(R.string.edit_baby_placeholder_name),
                                fontSize = 14.sp,
                                color = TextTertiary
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = TextTertiary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (name.isNotEmpty()) {
                                IconButton(onClick = { onNameChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Hapus nama",
                                        tint = TextTertiary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = KanguruTheme.colors.primary,
                            unfocusedBorderColor = formFieldBorderColor,
                            focusedContainerColor = formFieldFocusedContainerColor,
                            unfocusedContainerColor = formFieldContainerColor,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }

                // 2. Input: Tanggal Lahir
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.edit_baby_label_birth_date).uppercase(),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = " *",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = KanguruTheme.colors.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = formattedDate,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Pilih tanggal",
                                    tint = KanguruTheme.colors.primary
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = KanguruTheme.colors.primary,
                            unfocusedBorderColor = formFieldBorderColor,
                            focusedContainerColor = formFieldContainerColor,
                            unfocusedContainerColor = formFieldContainerColor,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    if (birthDateEpoch > 0) {
                        Row(
                            modifier = Modifier.padding(top = 6.dp, start = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = TextTertiary,
                                modifier = Modifier.size(14.dp)
                            )
                            val ageText = remember(primaryTextColor, weeks, months) {
                                buildAnnotatedString {
                                    append("Usia saat ini: ")
                                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = primaryTextColor)) {
                                        append("$weeks minggu ($months bulan)")
                                    }
                                }
                            }
                            Text(
                                text = ageText,
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // 3. Toggle Radio: Jenis Kelamin
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.edit_baby_label_gender).uppercase(),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = " *",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = KanguruTheme.colors.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Option: Laki-laki
                        val isMale = gender == Gender.MALE
                        val maleBg = if (isMale) {
                            KanguruTheme.colors.genderMaleActiveBackground
                        } else {
                            formFieldContainerColor
                        }
                        val maleBorder = if (isMale) {
                            BorderStroke(2.dp, KanguruTheme.colors.genderMaleActiveBorder)
                        } else {
                            BorderStroke(1.dp, formFieldBorderColor)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(maleBg)
                                .border(maleBorder, RoundedCornerShape(12.dp))
                                .clickable { onGenderChange(Gender.MALE) }
                                .padding(vertical = 12.dp, horizontal = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(
                                            if (isMale) KanguruTheme.colors.genderMaleActiveIconContainer
                                            else KanguruTheme.colors.genderMaleInactiveIconContainer,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Male,
                                        contentDescription = null,
                                        tint = if (isMale) KanguruTheme.colors.genderMaleIcon
                                        else KanguruTheme.colors.genderMaleInactiveIcon,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.edit_baby_gender_male),
                                    fontSize = 13.5.sp,
                                    fontWeight = if (isMale) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isMale) KanguruTheme.colors.genderMaleActiveText
                                    else TextSecondary
                                )
                            }

                            // Active Check Badge on top right
                            if (isMale) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(16.dp)
                                        .background(KanguruTheme.colors.genderMaleActiveBorder, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = KanguruTheme.colors.onPrimary,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        }

                        // Option: Perempuan
                        val isFemale = gender == Gender.FEMALE
                        val femaleBg = if (isFemale) {
                            KanguruTheme.colors.genderFemaleActiveBackground
                        } else {
                            formFieldContainerColor
                        }
                        val femaleBorder = if (isFemale) {
                            BorderStroke(2.dp, KanguruTheme.colors.genderFemaleActiveBorder)
                        } else {
                            BorderStroke(1.dp, formFieldBorderColor)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(femaleBg)
                                .border(femaleBorder, RoundedCornerShape(12.dp))
                                .clickable { onGenderChange(Gender.FEMALE) }
                                .padding(vertical = 12.dp, horizontal = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(
                                            if (isFemale) KanguruTheme.colors.genderFemaleActiveIconContainer
                                            else KanguruTheme.colors.genderFemaleInactiveIconContainer,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Female,
                                        contentDescription = null,
                                        tint = if (isFemale) KanguruTheme.colors.genderFemaleIcon
                                        else KanguruTheme.colors.genderFemaleInactiveIcon,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.edit_baby_gender_female),
                                    fontSize = 13.5.sp,
                                    fontWeight = if (isFemale) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isFemale) KanguruTheme.colors.genderFemaleActiveText
                                    else TextSecondary
                                )
                            }

                            // Active Check Badge on top right
                            if (isFemale) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(16.dp)
                                        .background(KanguruTheme.colors.genderFemaleActiveBorder, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = KanguruTheme.colors.onPrimary,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Input: Berat Lahir Bayi
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.edit_baby_label_birth_weight).uppercase(),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = " *",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = KanguruTheme.colors.primary
                            )
                        }

                        if (weightGram > 0) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isBblr) KanguruTheme.colors.warningContainer else KanguruTheme.colors.successContainer,
                                border = BorderStroke(
                                    1.dp,
                                    if (isBblr) KanguruTheme.colors.warningBorder else KanguruTheme.colors.successBorder
                                ),
                                modifier = Modifier.heightIn(min = 20.dp)
                            ) {
                                Text(
                                    text = if (isBblr) stringResource(R.string.edit_baby_status_bblr) else stringResource(R.string.edit_baby_status_normal),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isBblr) KanguruTheme.colors.onWarningContainer else KanguruTheme.colors.onSuccessContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = birthWeightInput,
                        onValueChange = onBirthWeightChange,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = KanguruTheme.colors.primary,
                            unfocusedBorderColor = formFieldBorderColor,
                            focusedContainerColor = formFieldFocusedContainerColor,
                            unfocusedContainerColor = formFieldContainerColor,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        trailingIcon = {
                            Text(
                                text = stringResource(R.string.edit_baby_unit_gram),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextTertiary,
                                modifier = Modifier.padding(end = 14.dp)
                            )
                        }
                    )

                    // Educative Warning Card for LBW/BBLR (< 2.500 gram)
                    if (isBblr) {
                        val tipBg = KanguruTheme.colors.tipBackground
                        val tipBorderColor = KanguruTheme.colors.tipBorder
                        val tipTextColor = KanguruTheme.colors.tipText
                        val warningIconColor = KanguruTheme.colors.warning
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = tipBg,
                            border = BorderStroke(1.dp, tipBorderColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = warningIconColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                val warningText = remember(tipTextColor) {
                                    buildAnnotatedString {
                                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = tipTextColor)) {
                                            append("Kategori BBLR (< 2.500 gram)")
                                        }
                                        append(" — Bayi memerlukan kontak kulit ke kulit (Metode Kanguru) rutin dan monitoring kenaikan berat badan intensif.")
                                    }
                                }
                                Text(
                                    text = warningText,
                                    fontSize = 11.5.sp,
                                    lineHeight = 16.5.sp,
                                    color = tipTextColor
                                )
                            }
                        }
                    }
                }

                // Error Message if any
                if (!errorMessage.isNullOrBlank()) {
                    Text(
                        text = errorMessage,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
            }

            // ==========================================
            // BOTTOM ACTION BUTTONS (Batal & Simpan)
            // ==========================================
            HorizontalDivider(
                color = KanguruTheme.colors.divider,
                thickness = 1.dp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cancel Action
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, KanguruTheme.colors.buttonSecondaryBorder)
                ) {
                    Text(
                        text = stringResource(R.string.edit_baby_btn_cancel),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = KanguruTheme.colors.buttonSecondaryText
                    )
                }

                // Save CTA Action
                Button(
                    onClick = onSave,
                    enabled = !isSaving,
                    modifier = Modifier
                        .weight(1.5f)
                        .heightIn(min = 48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KanguruTheme.colors.primary)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = KanguruTheme.colors.onPrimary, modifier = Modifier.size(20.dp))
                    } else {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = KanguruTheme.colors.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.edit_baby_btn_save),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = KanguruTheme.colors.onPrimary
                        )
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = if (birthDateEpoch > 0) birthDateEpoch else System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { onBirthDateChange(it) }
                        showDatePicker = false
                    }
                ) {
                    Text("Pilih", color = KanguruTheme.colors.primary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal", color = KanguruTheme.colors.textSecondary)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
