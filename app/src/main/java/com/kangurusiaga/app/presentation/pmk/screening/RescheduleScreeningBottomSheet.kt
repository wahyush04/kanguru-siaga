package com.kangurusiaga.app.presentation.pmk.screening

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.PrematureScreening
import com.kangurusiaga.app.domain.model.ScreeningType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Bottom Sheet Pop-Up: Atur Jadwal Ulang Skrining
 * Directly matching Google Stitch Screen:
 * projects/10808370107038581899/screens/1c142e19ba4345d2b7d7dc5d0d6ee0b9
 * Title: "Kanguru Siaga - Pop Up Atur Jadwal Ulang Skrining"
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RescheduleScreeningBottomSheet(
    state: RescheduleScreeningModalState,
    baby: Baby?,
    onDismiss: () -> Unit,
    onDateSelected: (Long) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val screening = state.screening ?: return
    val colors = KanguruTheme.colors
    var showDatePicker by remember { mutableStateOf(false) }
    val dateDisplayFormat = remember { SimpleDateFormat("d MMMM yyyy", Locale("id", "ID")) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val iconEmoji = when (screening.type) {
        ScreeningType.USG_HEAD -> "🧠"
        ScreeningType.ROP_EYE -> "👁️"
        ScreeningType.HEARING_OAE -> "👂"
        ScreeningType.THYROID_FUNCTION -> "🧪"
        ScreeningType.CUSTOM -> "📋"
    }

    val displayTitle = if (screening.categoryBadge.isNotBlank() && !screening.title.contains(screening.categoryBadge, ignoreCase = true)) {
        "${screening.title} (${screening.categoryBadge})"
    } else {
        screening.title
    }

    val babyName = baby?.name?.let {
        if (it.startsWith("By.", ignoreCase = true)) it else "By. Ny. $it"
    } ?: "By. Ny. Arini"
    val gaWeeks = baby?.gestationalAgeWeeks ?: 31

    val oldDateFormatted = screening.targetDateFormatted.ifBlank {
        SimpleDateFormat("d MMMM yyyy", Locale("id", "ID")).format(Date(screening.targetDateEpoch))
    }

    // Material 3 DatePicker Dialog matching BabyProfileSetupScreen Tanggal Lahir
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = if (state.newDateEpoch > 0) state.newDateEpoch else System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedUtc ->
                            val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                                timeInMillis = selectedUtc
                            }
                            val localCal = Calendar.getInstance().apply {
                                set(Calendar.YEAR, utcCal.get(Calendar.YEAR))
                                set(Calendar.MONTH, utcCal.get(Calendar.MONTH))
                                set(Calendar.DAY_OF_MONTH, utcCal.get(Calendar.DAY_OF_MONTH))
                                set(Calendar.HOUR_OF_DAY, 9)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            onDateSelected(localCal.timeInMillis)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(
                        text = "Pilih",
                        style = KanguruTheme.typography.labelLarge,
                        color = colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(
                        text = "Batal",
                        style = KanguruTheme.typography.labelLarge,
                        color = colors.textSecondary
                    )
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = colors.surface
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = colors.surface,
                    titleContentColor = colors.textPrimary,
                    headlineContentColor = colors.textPrimary,
                    weekdayContentColor = colors.textSecondary,
                    subheadContentColor = colors.textSecondary,
                    yearContentColor = colors.textPrimary,
                    currentYearContentColor = colors.primary,
                    selectedYearContentColor = colors.onPrimary,
                    selectedYearContainerColor = colors.primary,
                    dayContentColor = colors.textPrimary,
                    selectedDayContentColor = colors.onPrimary,
                    selectedDayContainerColor = colors.primary,
                    todayContentColor = colors.primary,
                    todayDateBorderColor = colors.primary
                )
            )
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        containerColor = colors.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 6.dp)
                    .size(width = 48.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(colors.dragHandle)
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header Pop-Up
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
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
                            .clip(RoundedCornerShape(14.dp))
                            .background(colors.clinicalRoseBg)
                            .border(1.dp, colors.clinicalRoseBorder, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Atur Jadwal Ulang Skrining",
                            style = KanguruTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Penyesuaian Janji Temu & Pengingat Bayi",
                            style = KanguruTheme.typography.labelSmall,
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
                        contentDescription = "Tutup",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            HorizontalDivider(color = colors.cardBorder.copy(alpha = 0.6f))

            // Body Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Info Pasien & Jenis Skrining Terpilih
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    colors.clinicalRoseBg.copy(alpha = 0.7f),
                                    colors.clinicalAmberBg.copy(alpha = 0.4f)
                                )
                            )
                        )
                        .border(1.dp, colors.clinicalRoseBorder, RoundedCornerShape(18.dp))
                        .padding(14.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.surface)
                                    .border(1.dp, colors.clinicalRoseBorder.copy(alpha = 0.7f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = iconEmoji,
                                    style = KanguruTheme.typography.titleMedium
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = displayTitle,
                                    style = KanguruTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = buildAnnotatedString {
                                        append("Bayi: ")
                                        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = colors.textPrimary)) {
                                            append(babyName)
                                        }
                                        append(" ")
                                        withStyle(SpanStyle(color = colors.textSecondary)) {
                                            append("(Prematur $gaWeeks Mgg)")
                                        }
                                    },
                                    style = KanguruTheme.typography.labelSmall,
                                    color = colors.textSecondary
                                )
                            }
                        }

                        // Amber pill badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(colors.clinicalAmberBg)
                                .border(1.dp, colors.clinicalAmberBorder, RoundedCornerShape(50))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Jadwal Lama: $oldDateFormatted",
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.clinicalAmberText
                            )
                        }
                    }
                }

                // Input Pilihan Tanggal Baru
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "PILIH TANGGAL PEMERIKSAAN BARU",
                        style = KanguruTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textSecondary
                    )

                    Text(
                        text = "Tanggal Pemeriksaan Baru",
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary
                    )

                    // Clickable date picker box styled identically to Tanggal Lahir in Baby Profile Setup
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.surfaceVariant)
                            .border(1.dp, colors.cardBorder, RoundedCornerShape(12.dp))
                            .clickable { showDatePicker = true }
                            .padding(horizontal = 14.dp, vertical = 13.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = dateDisplayFormat.format(Date(state.newDateEpoch)),
                                style = KanguruTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary
                            )
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Pilih Tanggal",
                                tint = colors.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = colors.cardBorder.copy(alpha = 0.6f))

            // Action Buttons Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, colors.cardBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = colors.surface,
                        contentColor = colors.textPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
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
                    modifier = Modifier
                        .weight(2f)
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = colors.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Simpan Jadwal Baru",
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
