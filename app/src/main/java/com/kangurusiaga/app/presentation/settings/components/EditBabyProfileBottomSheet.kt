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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kangurusiaga.app.core.designsystem.theme.BrandPink
import com.kangurusiaga.app.core.designsystem.theme.White
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
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDatePicker by remember { mutableStateOf(false) }

    // Camera launcher
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempCameraUri?.let { onPhotoSelected(it) }
        }
    }

    // Photo gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onPhotoSelected(it) }
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

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 44.dp, height = 4.dp)
                        .background(Color(0xFFE2E8F0), CircleShape)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Edit Data Bayi",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = Color(0xFFFFF1F2),
                            border = BorderStroke(1.dp, Color(0xFFFFCCD5))
                        ) {
                            Text(
                                text = "Profil Pasien",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BrandPink,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Perbarui identitas dan data klinis kelahiran si kecil",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFF1F5F9), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Form Body (Scrollable)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Photo Editor
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .background(
                                brush = Brush.linearGradient(listOf(BrandPink, Color(0xFFFDA4AF))),
                                shape = CircleShape
                            )
                            .padding(3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color(0xFFFFF1F2))
                                .border(2.dp, White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!photoUri.isNullOrBlank()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(photoUri)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = BrandPink,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = Color(0xFFFFF1F2),
                            modifier = Modifier.clickable {
                                val uri = createTempCameraUri()
                                tempCameraUri = uri
                                cameraLauncher.launch(uri)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = BrandPink,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Ambil Foto",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandPink
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.clickable {
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    tint = Color(0xFF475569),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Pilih Galeri",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF475569)
                                )
                            }
                        }
                    }
                }

                // 1. Nama Lengkap Bayi
                Column {
                    Text(
                        text = "NAMA LENGKAP BAYI *",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = onNameChange,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        placeholder = { Text("Masukkan nama bayi...", fontSize = 14.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandPink,
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedContainerColor = White,
                            unfocusedContainerColor = Color(0xFFFAF8F6)
                        ),
                        trailingIcon = {
                            if (name.isNotEmpty()) {
                                IconButton(onClick = { onNameChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Hapus nama",
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    )
                }

                // 2. Tanggal Lahir
                Column {
                    Text(
                        text = "TANGGAL LAHIR *",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = formattedDate,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandPink,
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedContainerColor = Color(0xFFFAF8F6),
                            unfocusedContainerColor = Color(0xFFFAF8F6)
                        ),
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Pilih tanggal",
                                    tint = BrandPink
                                )
                            }
                        }
                    )
                    if (birthDateEpoch > 0) {
                        Text(
                            text = "Usia saat ini: $weeks minggu ($months bulan)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }
                }

                // 3. Jenis Kelamin
                Column {
                    Text(
                        text = "JENIS KELAMIN *",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Laki-laki
                        val isMale = gender == Gender.MALE
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onGenderChange(Gender.MALE) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isMale) Color(0xFFEFF6FF) else Color(0xFFFAF8F6),
                            border = BorderStroke(
                                if (isMale) 2.dp else 1.dp,
                                if (isMale) Color(0xFF3B82F6) else Color(0xFFE2E8F0)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Male,
                                    contentDescription = null,
                                    tint = Color(0xFF3B82F6),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Laki-laki",
                                    fontSize = 13.sp,
                                    fontWeight = if (isMale) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isMale) Color(0xFF1E40AF) else Color(0xFF475569)
                                )
                            }
                        }

                        // Perempuan
                        val isFemale = gender == Gender.FEMALE
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onGenderChange(Gender.FEMALE) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isFemale) Color(0xFFFFF1F2) else Color(0xFFFAF8F6),
                            border = BorderStroke(
                                if (isFemale) 2.dp else 1.dp,
                                if (isFemale) BrandPink else Color(0xFFE2E8F0)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Female,
                                    contentDescription = null,
                                    tint = BrandPink,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Perempuan",
                                    fontSize = 13.sp,
                                    fontWeight = if (isFemale) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isFemale) BrandPink else Color(0xFF475569)
                                )
                            }
                        }
                    }
                }

                // 4. Berat Lahir
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BERAT LAHIR BAYI *",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569),
                            letterSpacing = 0.5.sp
                        )
                        if (weightGram > 0) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (isBblr) Color(0xFFFFFBEB) else Color(0xFFF0FDF4),
                                border = BorderStroke(
                                    1.dp,
                                    if (isBblr) Color(0xFFFDE68A) else Color(0xFFDCFCE7)
                                )
                            ) {
                                Text(
                                    text = if (isBblr) "Status: BBLR" else "Status: Normal",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isBblr) Color(0xFFD97706) else Color(0xFF16A34A),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandPink,
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedContainerColor = White,
                            unfocusedContainerColor = Color(0xFFFAF8F6)
                        ),
                        trailingIcon = {
                            Text(
                                text = "gram",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF94A3B8),
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        }
                    )

                    // Educative Warning Card for BBLR (< 2500g)
                    if (isBblr) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFFBEB),
                            border = BorderStroke(1.dp, Color(0xFFFDE68A))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Kategori BBLR (< 2.500 gram) — Bayi memerlukan kontak kulit ke kulit (Metode Kanguru) rutin dan monitoring kenaikan berat badan intensif.",
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF78350F)
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
                        color = Color(0xFFDC2626),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Bottom Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                ) {
                    Text(
                        text = "Batal",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569)
                    )
                }

                Button(
                    onClick = onSave,
                    enabled = !isSaving,
                    modifier = Modifier
                        .weight(1.5f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPink)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = White, modifier = Modifier.size(20.dp))
                    } else {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Simpan Perubahan",
                            fontWeight = FontWeight.Bold,
                            color = White
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
                    Text("Pilih", color = BrandPink, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
