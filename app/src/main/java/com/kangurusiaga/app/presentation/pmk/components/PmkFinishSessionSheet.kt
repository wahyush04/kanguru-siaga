package com.kangurusiaga.app.presentation.pmk.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.domain.model.PmkCaregiver

/**
 * Bottom Sheet Modal: Selesaikan Sesi PMK
 * Referensi UI Single Source of Truth: Google Stitch
 * Layout: Kanguru Siaga - Bottomsheet Selesaikan Sesi PMK
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PmkFinishSessionSheet(
    sheetState: SheetState,
    formattedActiveDuration: String,
    caregiver: PmkCaregiver,
    formattedPauseDuration: String,
    formattedTodayAccumulation: String = "18j 24m",
    todayPercentage: Int = 75,
    temperatureInput: String,
    responseInput: String,
    notesInput: String,
    onTemperatureChange: (String) -> Unit,
    onResponseChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    // Daftar opsi perilaku bayi sesuai Google Stitch HTML
    val behaviorOptions = remember {
        listOf(
            BehaviorOption("😌", "Tenang & Tidur"),
            BehaviorOption("😴", "Tidur Pulas Nyenyak"),
            BehaviorOption("👶", "Aktif & Nyaman"),
            BehaviorOption("🍼", "Menyusu / Rooting"),
            BehaviorOption("🥺", "Gelisah / Sempat Menangis Singkat", isFullWidth = true)
        )
    }

    // Parsing multi-select response string
    val selectedResponses = remember(responseInput) {
        responseInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
    }

    val tempDouble = temperatureInput.toDoubleOrNull()
    val isNormalTemp = tempDouble != null && tempDouble in 36.5..37.5

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surface,
        dragHandle = {
            // Top Pull Bar Indicator (w-12 h-1.5 bg-slate-200)
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 6.dp)
                    .size(width = 48.dp, height = 5.dp)
                    .clip(CircleShape)
                    .background(colors.dragHandle)
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
        ) {
            // =========================================================================
            // BEGIN: ModalHeader (Judul, Subtitle & Tombol Tutup X)
            // =========================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Selesaikan Sesi PMK",
                        style = KanguruTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Rekapitulasi kontak kulit si kecil hari ini",
                        style = KanguruTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                }

                // Close Button (w-8 h-8 rounded-full bg-slate-100 text-slate-500)
                Surface(
                    shape = CircleShape,
                    color = colors.closeButtonBackground,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onDismiss)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup Dialog",
                            tint = colors.closeButtonTint,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = colors.inputBorder.copy(alpha = 0.5f), thickness = 1.dp)

            // =========================================================================
            // SCROLLABLE FORM CONTAINER
            // =========================================================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 14.dp)
            ) {
                // ---------------------------------------------------------------------
                // BEGIN: SessionSummaryCard
                // ---------------------------------------------------------------------
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.cardBackground)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "DURASI SESI INI",
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.textTertiary
                            )

                            // Target Achieved Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(colors.surface)
                                    .border(1.dp, colors.cardBorder, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(colors.primary)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Target Tercapai $todayPercentage%",
                                        style = KanguruTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.primary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Durasi Stopwatch Besar
                        Text(
                            text = formattedActiveDuration,
                            style = KanguruTheme.timerDisplay,
                            color = colors.primary
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(
                            color = colors.cardBorder.copy(alpha = 0.7f),
                            thickness = 1.dp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Daily Continuous PMK Metrics
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "⏱️",
                                    style = KanguruTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Akumulasi Hari Ini: ",
                                    style = KanguruTheme.typography.bodySmall,
                                    color = colors.textSecondary
                                )
                                Text(
                                    text = formattedTodayAccumulation,
                                    style = KanguruTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                            }

                            Text(
                                text = "Target 20 Jam (WHO)",
                                style = KanguruTheme.typography.labelSmall,
                                color = colors.textTertiary
                            )
                        }
                    }
                }
                // END: SessionSummaryCard

                Spacer(modifier = Modifier.height(16.dp))

                // ---------------------------------------------------------------------
                // BEGIN: TemperatureInputSection
                // ---------------------------------------------------------------------
                var showTempDialog by remember { mutableStateOf(false) }

                val (statusText, statusDotColor, statusBg, statusBorder) = when {
                    tempDouble == null -> FinishTempStatus("Belum Diisi", colors.textTertiary, colors.surfaceVariant, colors.cardBorder)
                    tempDouble in 36.5..37.5 -> FinishTempStatus("36.5° - 37.5° (Normal)", colors.success, colors.successContainer, colors.successBorder)
                    tempDouble < 36.5 -> FinishTempStatus("< 36.5° (Hipotermia)", colors.info, colors.infoContainer, colors.infoBorder)
                    else -> FinishTempStatus("> 37.5° (Hangat/Demam)", colors.warning, colors.warningContainer, colors.warningBorder)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.cardBackground)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Suhu Bayi Pasca PMK",
                                style = KanguruTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        }

                        // Status Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(statusBg)
                                .border(1.dp, statusBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(statusDotColor)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = statusText,
                                    style = KanguruTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Interactive Selector Card (Membuka Modal Pop-up Suhu Tubuh Bayi)
                    Surface(
                        onClick = { showTempDialog = true },
                        shape = RoundedCornerShape(14.dp),
                        color = colors.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.outlineVariant),
                        shadowElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(colors.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Thermostat,
                                        contentDescription = "Suhu",
                                        tint = colors.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = temperatureInput.ifEmpty { "36.8" },
                                            style = KanguruTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = colors.textPrimary
                                        )
                                        Text(
                                            text = " °C",
                                            style = KanguruTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.textTertiary,
                                            modifier = Modifier.padding(bottom = 1.dp)
                                        )
                                    }
                                    Text(
                                        text = "Ketuk untuk atur suhu visual",
                                        style = KanguruTheme.typography.labelSmall,
                                        color = colors.textTertiary
                                    )
                                }
                            }

                            // Pill Tombol "Atur Suhu"
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(colors.primaryContainer)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Atur Suhu",
                                    style = KanguruTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.primary
                                )
                            }
                        }
                    }
                }

                if (showTempDialog) {
                    PmkTemperatureInputDialog(
                        initialTemperature = temperatureInput.ifEmpty { "36.8" },
                        onConfirm = { newTemp ->
                            onTemperatureChange(newTemp)
                            showTempDialog = false
                        },
                        onDismiss = { showTempDialog = false }
                    )
                }
                // END: TemperatureInputSection

                Spacer(modifier = Modifier.height(16.dp))

                // ---------------------------------------------------------------------
                // BEGIN: BabyBehaviorSection (Chips Grid Multi-Select)
                // ---------------------------------------------------------------------
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Kondisi / Respon Perilaku Bayi",
                            style = KanguruTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )

                        Text(
                            text = "(Bisa pilih > 1)",
                            style = KanguruTheme.typography.labelSmall,
                            color = colors.textTertiary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Grid 2 Kolom Opsi 1 & 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BehaviorChipItem(
                            option = behaviorOptions[0],
                            isSelected = selectedResponses.contains(behaviorOptions[0].label),
                            onToggle = {
                                val updated = toggleOption(selectedResponses, behaviorOptions[0].label)
                                onResponseChange(updated)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        BehaviorChipItem(
                            option = behaviorOptions[1],
                            isSelected = selectedResponses.contains(behaviorOptions[1].label),
                            onToggle = {
                                val updated = toggleOption(selectedResponses, behaviorOptions[1].label)
                                onResponseChange(updated)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Grid 2 Kolom Opsi 3 & 4
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BehaviorChipItem(
                            option = behaviorOptions[2],
                            isSelected = selectedResponses.contains(behaviorOptions[2].label),
                            onToggle = {
                                val updated = toggleOption(selectedResponses, behaviorOptions[2].label)
                                onResponseChange(updated)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        BehaviorChipItem(
                            option = behaviorOptions[3],
                            isSelected = selectedResponses.contains(behaviorOptions[3].label),
                            onToggle = {
                                val updated = toggleOption(selectedResponses, behaviorOptions[3].label)
                                onResponseChange(updated)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Full Width Opsi 5 (col-span 2)
                    BehaviorChipItem(
                        option = behaviorOptions[4],
                        isSelected = selectedResponses.contains(behaviorOptions[4].label),
                        onToggle = {
                            val updated = toggleOption(selectedResponses, behaviorOptions[4].label)
                            onResponseChange(updated)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                // END: BabyBehaviorSection

                Spacer(modifier = Modifier.height(16.dp))

                // ---------------------------------------------------------------------
                // BEGIN: AdditionalNotesSection
                // ---------------------------------------------------------------------
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Catatan Tambahan ",
                                style = KanguruTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "(Opsional)",
                                style = KanguruTheme.typography.labelSmall,
                                color = colors.textTertiary
                            )
                        }

                        Text(
                            text = "Maks. 200 karakter",
                            style = KanguruTheme.typography.labelSmall,
                            color = colors.textTertiary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = notesInput,
                        onValueChange = { if (it.length <= 200) onNotesChange(it) },
                        placeholder = {
                            Text(
                                text = "cth: Bayi tidur lelap di dada ibu, suhu stabil dan pernapasan sangat teratur...",
                                style = KanguruTheme.typography.bodySmall,
                                color = colors.textTertiary
                            )
                        },
                        textStyle = KanguruTheme.typography.bodySmall.copy(color = colors.textPrimary),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = colors.inputFocusedBackground,
                            unfocusedContainerColor = colors.inputBackground,
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.inputBorder
                        ),
                        minLines = 3,
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                // END: AdditionalNotesSection
            }

            // =========================================================================
            // BEGIN: ActionButtonsGroup (Footer)
            // =========================================================================
            HorizontalDivider(color = colors.inputBorder.copy(alpha = 0.5f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Main Submit Button: Simpan ke Riwayat PMK
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = colors.primary,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = onConfirm)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Simpan",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Simpan ke Riwayat PMK",
                        style = KanguruTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Secondary Cancel/Continue Button: Lanjutkan Sesi (Belum Selesai)
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                Text(
                    text = "Lanjutkan Sesi (Belum Selesai)",
                    style = KanguruTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * Data model untuk opsi perilaku bayi di bottom sheet
 */
private data class BehaviorOption(
    val emoji: String,
    val label: String,
    val isFullWidth: Boolean = false
)

/**
 * Composable Chip Item dengan checkbox badge bulat di pojok kanan atas saat dipilih
 */
@Composable
private fun BehaviorChipItem(
    option: BehaviorOption,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KanguruTheme.colors

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onToggle)
            .background(if (isSelected) colors.primaryContainer else colors.surface)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) colors.primary else colors.inputBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = option.emoji,
                style = KanguruTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = option.label,
                style = KanguruTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) colors.primary else colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Active Checkmark Badge di pojok kanan atas
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
                    .clip(CircleShape)
                    .background(colors.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    color = Color.White,
                    style = KanguruTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Helper toggle multi-select options
 */
private fun toggleOption(currentSet: Set<String>, option: String): String {
    val newSet = if (currentSet.contains(option)) {
        currentSet - option
    } else {
        currentSet + option
    }
    return newSet.joinToString(", ")
}

private data class FinishTempStatus(
    val text: String,
    val dotColor: Color,
    val bg: Color,
    val border: Color
)
