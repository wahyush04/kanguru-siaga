package com.kangurusiaga.app.presentation.pmk.screening

import android.app.DatePickerDialog
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.util.PmaCalculator
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomScreeningBottomSheet(
    state: AddScreeningModalState,
    baby: Baby?,
    onDismiss: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onTargetDateChanged: (Long) -> Unit,
    onAlarmChanged: (Boolean) -> Unit,
    onNotesChanged: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val colors = KanguruTheme.colors
    val context = LocalContext.current
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")) }

    // Dynamic PMA calculation
    val calculatedPmaWeeks = remember(baby, state.targetDateEpoch) {
        if (baby != null && baby.birthDateEpochMillis > 0) {
            val calc = PmaCalculator.calculate(
                birthDateEpochMillis = baby.birthDateEpochMillis,
                measurementDateEpochMillis = state.targetDateEpoch,
                gestationalAgeAtBirthWeeks = baby.gestationalAgeWeeks
            )
            calc.pmaWeeks.toInt().coerceAtLeast(baby.gestationalAgeWeeks)
        } else {
            36
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 44.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(colors.cardBorder)
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Section: Icon + Title + Close Button
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
                            .size(46.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.primaryContainer)
                            .border(1.dp, colors.clinicalRoseBorder, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Tambah Skrining Baru",
                            style = KanguruTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Catat jadwal pemeriksaan spesialis si kecil",
                            style = KanguruTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(colors.surfaceVariant)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup formulir",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // 1. Nama Skrining / Tindakan
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Nama Skrining / Tindakan",
                    style = KanguruTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.background)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Notes,
                            contentDescription = null,
                            tint = colors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )

                        OutlinedTextField(
                            value = state.title,
                            onValueChange = onTitleChanged,
                            placeholder = {
                                Text(
                                    text = "Contoh: USG Abdomen / Ginjal, Ekokardiografi",
                                    style = KanguruTheme.typography.bodySmall,
                                    color = colors.textSecondary.copy(alpha = 0.6f)
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = KanguruTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                    }
                }

                // Quick choice chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuickChipItem(label = "+ USG Ekokardiografi", onClick = { onTitleChanged("USG Ekokardiografi") })
                    QuickChipItem(label = "+ USG Abdomen / Ginjal", onClick = { onTitleChanged("USG Abdomen / Ginjal") })
                    QuickChipItem(label = "+ Evaluasi Neurologi", onClick = { onTitleChanged("Evaluasi Neurologi Anak") })
                    QuickChipItem(label = "+ Skrining Gigi / Oral", onClick = { onTitleChanged("Skrining Gigi & Mulut") })
                }
            }

            // 2. Tanggal Rencana Pemeriksaan
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Tanggal Rencana Pemeriksaan",
                    style = KanguruTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.background)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                        .clickable {
                            val cal = Calendar.getInstance().apply { timeInMillis = state.targetDateEpoch }
                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val newCal = Calendar.getInstance().apply {
                                        set(year, month, dayOfMonth, 9, 0, 0)
                                    }
                                    onTargetDateChanged(newCal.timeInMillis)
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                        .padding(horizontal = 14.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = colors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )

                        Text(
                            text = dateFormat.format(Date(state.targetDateEpoch)),
                            style = KanguruTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }
                }

                // Clinical PMA Reference Badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.clinicalEmeraldBg)
                        .border(1.dp, colors.clinicalEmeraldBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = colors.clinicalEmeraldText,
                            modifier = Modifier.size(18.dp)
                        )

                        val pmaAnnotated = buildAnnotatedString {
                            append("Perkiraan usia bayi saat jadwal: ")
                            withStyle(
                                SpanStyle(
                                    color = colors.clinicalEmeraldText,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("$calculatedPmaWeeks Minggu PMA")
                            }
                            append(" (Pascamenstruasi)")
                        }

                        Text(
                            text = pmaAnnotated,
                            style = KanguruTheme.typography.labelSmall,
                            color = colors.textPrimary
                        )
                    }
                }
            }

            // 3. Pengingat Otomatis Switch Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.background)
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
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
                                .clip(RoundedCornerShape(10.dp))
                                .background(colors.primaryContainer)
                                .border(1.dp, colors.clinicalRoseBorder, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Pengingat Otomatis",
                                style = KanguruTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "Kirim notifikasi & alarm gawai",
                                style = KanguruTheme.typography.labelSmall,
                                color = colors.textSecondary
                            )
                        }
                    }

                    Switch(
                        checked = state.isAlarmActive,
                        onCheckedChange = onAlarmChanged,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colors.surface,
                            checkedTrackColor = colors.primary,
                            uncheckedThumbColor = colors.surface,
                            uncheckedTrackColor = colors.outline
                        )
                    )
                }
            }

            // 4. Catatan / Instruksi Khusus (Opsional)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Catatan / Instruksi Khusus",
                        style = KanguruTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Opsional",
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
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    OutlinedTextField(
                        value = state.notes,
                        onValueChange = onNotesChanged,
                        placeholder = {
                            Text(
                                text = "Contoh: Bawa buku KIA, puasa 1 jam sebelum pemeriksaan, rujukan BPJS aktif...",
                                style = KanguruTheme.typography.bodySmall,
                                color = colors.textSecondary.copy(alpha = 0.6f)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(88.dp),
                        textStyle = KanguruTheme.typography.bodySmall.copy(color = colors.textPrimary),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 5. Action Buttons (Batal + Simpan Jadwal)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = colors.surface,
                        contentColor = colors.textPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
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
                    enabled = state.title.isNotBlank(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = colors.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Simpan Jadwal",
                            style = KanguruTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = colors.onPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickChipItem(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surfaceVariant.copy(alpha = 0.5f))
            .border(1.dp, colors.cardBorder, RoundedCornerShape(8.dp))
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
