package com.kangurusiaga.app.presentation.growth.batch.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.core.designsystem.theme.White
import com.kangurusiaga.app.domain.model.Baby
import com.kangurusiaga.app.domain.model.Gender
import com.kangurusiaga.app.presentation.growth.batch.BatchField
import com.kangurusiaga.app.presentation.growth.batch.GrowthBatchRowState
import com.kangurusiaga.app.presentation.growth.components.GrowthIcons
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Baby Summary Banner matching Google Stitch Screen 41abf5181c9c46e3aa16074c795fe9b7
 */
@Composable
fun GrowthBabySummaryCard(
    baby: Baby?,
    currentPmaWeeks: Int,
    modifier: Modifier = Modifier
) {
    val name = baby?.name ?: "Bayi Arka Narendra"
    val initials = name.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .map { it.first().uppercaseChar() }
        .joinToString("")
        .ifEmpty { "AN" }

    val isMale = baby?.gender != Gender.FEMALE
    val gaBirth = baby?.gestationalAgeWeeks ?: 32
    val weightBirthGram = baby?.birthWeightGram ?: 1850
    val weightFormatted = NumberFormat.getNumberInstance(Locale("id", "ID")).format(weightBirthGram)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = KanguruTheme.colors.growthBabyBannerBg
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Avatar + Identity Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Circle Avatar Initials
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(KanguruTheme.colors.growthBabyAvatarBg)
                        .border(1.dp, KanguruTheme.colors.growthBabyAvatarBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        style = KanguruTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = name,
                            style = KanguruTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Gender Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(KanguruTheme.colors.growthBabyGenderBadgeBg)
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isMale) "♂ Laki-laki" else "♀ Perempuan",
                                style = KanguruTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = KanguruTheme.colors.growthBabyGenderBadgeText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = stringResource(R.string.growth_batch_born_summary, gaBirth, weightFormatted),
                        style = KanguruTheme.typography.bodySmall,
                        color = KanguruTheme.colors.growthBabyGenderBadgeText.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Right: PMA Highlight Block
            Box(
                modifier = Modifier
                    .drawBehind {
                        // Vertical divider line on the left side
                        drawLine(
                            color = Color(0x33A5F3FC),
                            start = androidx.compose.ui.geometry.Offset(0f, 4f),
                            end = androidx.compose.ui.geometry.Offset(0f, size.height - 4f),
                            strokeWidth = 1.2.dp.toPx()
                        )
                    }
                    .padding(start = 12.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.growth_batch_pma_label),
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = KanguruTheme.colors.growthBabyGenderBadgeText
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "$currentPmaWeeks",
                            style = KanguruTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = KanguruTheme.colors.growthPmaHighlight
                        )
                        Text(
                            text = "Minggu",
                            style = KanguruTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = KanguruTheme.colors.growthBabyGenderBadgeText,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Multi-Row Table Section for Fenton input
 */
@Composable
fun GrowthBatchTable(
    rows: List<GrowthBatchRowState>,
    onFieldChange: (rowId: String, field: BatchField, value: String) -> Unit,
    onDeleteRow: (rowId: String) -> Unit,
    onAddRow: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = KanguruTheme.colors.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, KanguruTheme.colors.divider),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row of the Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0284C7))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.growth_batch_table_title),
                            style = KanguruTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = KanguruTheme.colors.textPrimary
                        )
                    }
                    Text(
                        text = stringResource(R.string.growth_batch_table_subtitle),
                        style = KanguruTheme.typography.labelSmall,
                        color = KanguruTheme.colors.textSecondary,
                        modifier = Modifier.padding(start = 14.dp, top = 2.dp)
                    )
                }

                // Range Badge "22-50 Minggu"
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(KanguruTheme.colors.growthTableBadgePmaBg)
                        .border(1.dp, KanguruTheme.colors.growthTableBadgePmaBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 9.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.growth_batch_table_range_badge),
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = KanguruTheme.colors.growthTableBadgePmaText,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Table Column Headers Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.growth_batch_col_index),
                    style = KanguruTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = KanguruTheme.colors.textTertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(22.dp)
                )

                // GA Column
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.growth_batch_col_ga),
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = KanguruTheme.colors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.growth_batch_col_ga_sub),
                        style = KanguruTheme.typography.labelSmall,
                        color = KanguruTheme.colors.textTertiary,
                        textAlign = TextAlign.Center
                    )
                }

                // Weight Column
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.growth_batch_col_weight),
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = KanguruTheme.colors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.growth_batch_col_weight_sub),
                        style = KanguruTheme.typography.labelSmall,
                        color = KanguruTheme.colors.textTertiary,
                        textAlign = TextAlign.Center
                    )
                }

                // Head Circ Column
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.growth_batch_col_head),
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = KanguruTheme.colors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.growth_batch_col_head_sub),
                        style = KanguruTheme.typography.labelSmall,
                        color = KanguruTheme.colors.textTertiary,
                        textAlign = TextAlign.Center
                    )
                }

                // Length Column
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.growth_batch_col_length),
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = KanguruTheme.colors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.growth_batch_col_length_sub),
                        style = KanguruTheme.typography.labelSmall,
                        color = KanguruTheme.colors.textTertiary,
                        textAlign = TextAlign.Center
                    )
                }

                // Spacer for Action Button alignment
                Spacer(modifier = Modifier.width(28.dp))
            }

            HorizontalDivider(
                color = KanguruTheme.colors.divider,
                thickness = 1.dp
            )

            // Table Dynamic Rows
            rows.forEachIndexed { index, row ->
                GrowthBatchTableRow(
                    rowIndex = index + 1,
                    row = row,
                    onFieldChange = { field, value -> onFieldChange(row.id, field, value) },
                    onDelete = { onDeleteRow(row.id) }
                )
                if (index < rows.size - 1) {
                    HorizontalDivider(
                        color = KanguruTheme.colors.divider.copy(alpha = 0.5f),
                        thickness = 0.8.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Add Row Dashed Button
            GrowthDashedAddButton(onClick = onAddRow)
        }
    }
}

/**
 * Individual Table Row
 */
@Composable
private fun GrowthBatchTableRow(
    rowIndex: Int,
    row: GrowthBatchRowState,
    onFieldChange: (field: BatchField, value: String) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Index #
        Text(
            text = "$rowIndex",
            style = KanguruTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = KanguruTheme.colors.textTertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(22.dp)
        )

        // GA input
        BatchCompactInputField(
            value = row.pmaWeeks,
            placeholder = "e.g. 36",
            keyboardType = KeyboardType.Number,
            onValueChange = { onFieldChange(BatchField.PMA, it) },
            isError = row.pmaError != null,
            modifier = Modifier.weight(1f)
        )

        // Weight input
        BatchCompactInputField(
            value = row.weightGrams,
            placeholder = "(opt)",
            keyboardType = KeyboardType.Number,
            onValueChange = { onFieldChange(BatchField.WEIGHT, it) },
            modifier = Modifier.weight(1f)
        )

        // Head Circumference input
        BatchCompactInputField(
            value = row.headCircumferenceCm,
            placeholder = "(opt)",
            keyboardType = KeyboardType.Decimal,
            onValueChange = { onFieldChange(BatchField.HEAD_CIRCUMFERENCE, it) },
            modifier = Modifier.weight(1f)
        )

        // Length input
        BatchCompactInputField(
            value = row.lengthCm,
            placeholder = "(opt)",
            keyboardType = KeyboardType.Decimal,
            onValueChange = { onFieldChange(BatchField.LENGTH, it) },
            modifier = Modifier.weight(1f)
        )

        // Delete button
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = GrowthIcons.Delete,
                contentDescription = stringResource(R.string.growth_batch_cd_delete_row),
                tint = KanguruTheme.colors.textTertiary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * Compact input box styled after iOS clinical inputs in Stitch
 */
@Composable
private fun BatchCompactInputField(
    value: String,
    placeholder: String,
    keyboardType: KeyboardType,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    val borderColor = if (isError) KanguruTheme.colors.error else KanguruTheme.colors.divider
    val textColor = KanguruTheme.colors.textPrimary

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        textStyle = TextStyle(
            color = textColor,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            fontFamily = KanguruTheme.typography.bodyMedium.fontFamily,
            fontSize = KanguruTheme.typography.bodyMedium.fontSize,
            lineHeight = KanguruTheme.typography.bodyMedium.lineHeight
        ),
        cursorBrush = SolidColor(KanguruTheme.colors.primary),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(KanguruTheme.colors.surface)
                    .border(1.dp, borderColor, RoundedCornerShape(10.dp))
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = KanguruTheme.typography.labelSmall,
                        color = KanguruTheme.colors.textTertiary,
                        textAlign = TextAlign.Center
                    )
                }
                innerTextField()
            }
        },
        modifier = modifier
    )
}

/**
 * Dashed outline button for adding rows
 */
@Composable
fun GrowthDashedAddButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = KanguruTheme.colors.growthTableDashedBorder
    val bgColor = KanguruTheme.colors.growthTableDashedBg
    val contentColor = KanguruTheme.colors.growthTableDashedText

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .drawBehind {
                val stroke = Stroke(
                    width = 1.2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                )
                drawRoundRect(
                    color = borderColor,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
                    style = stroke
                )
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "+",
                style = KanguruTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Text(
                text = stringResource(R.string.growth_batch_btn_add_row),
                style = KanguruTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        }
    }
}

/**
 * Form Card for Date, Reminder Switch, and Clinical Notes
 */
@Composable
fun GrowthBatchMetadataCard(
    dateEpochMillis: Long,
    onDateClick: () -> Unit,
    reminderEnabled: Boolean,
    onReminderToggle: (Boolean) -> Unit,
    clinicalNotes: String,
    onNotesChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateText = SimpleDateFormat("dd/MM/yyyy", Locale("id", "ID")).format(Date(dateEpochMillis))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = KanguruTheme.colors.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, KanguruTheme.colors.divider),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Date Picker Row
            Text(
                text = stringResource(R.string.growth_batch_label_entry_date),
                style = KanguruTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = KanguruTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(KanguruTheme.colors.surfaceVariant)
                    .border(1.dp, KanguruTheme.colors.divider, RoundedCornerShape(12.dp))
                    .clickable(onClick = onDateClick)
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = dateText,
                        style = KanguruTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = KanguruTheme.colors.textPrimary
                    )
                    Icon(
                        imageVector = GrowthIcons.Calendar,
                        contentDescription = null,
                        tint = KanguruTheme.colors.textTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = KanguruTheme.colors.divider, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Routine Reminder Switch Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.growth_batch_label_reminder),
                        style = KanguruTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = KanguruTheme.colors.textPrimary
                    )
                    Text(
                        text = stringResource(R.string.growth_batch_desc_reminder),
                        style = KanguruTheme.typography.labelSmall,
                        color = KanguruTheme.colors.textSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Switch(
                    checked = reminderEnabled,
                    onCheckedChange = onReminderToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = White,
                        checkedTrackColor = KanguruTheme.colors.primary,
                        uncheckedThumbColor = White,
                        uncheckedTrackColor = KanguruTheme.colors.divider
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = KanguruTheme.colors.divider, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Clinical Notes Field
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.growth_batch_label_notes),
                    style = KanguruTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = KanguruTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.growth_batch_label_notes_optional),
                    style = KanguruTheme.typography.labelSmall,
                    color = KanguruTheme.colors.textTertiary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = clinicalNotes,
                onValueChange = onNotesChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.growth_batch_placeholder_notes),
                        style = KanguruTheme.typography.labelSmall,
                        color = KanguruTheme.colors.textTertiary
                    )
                },
                textStyle = TextStyle(
                    color = KanguruTheme.colors.textPrimary,
                    fontFamily = KanguruTheme.typography.bodyMedium.fontFamily,
                    fontSize = KanguruTheme.typography.bodyMedium.fontSize
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = KanguruTheme.colors.surface,
                    unfocusedContainerColor = KanguruTheme.colors.surfaceVariant,
                    focusedBorderColor = KanguruTheme.colors.primary,
                    unfocusedBorderColor = KanguruTheme.colors.divider
                ),
                minLines = 2,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Amber Guidance Card matching Stitch
 */
@Composable
fun GrowthAccuracyCard(
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = KanguruTheme.colors.pediatricNoteCardBg
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, KanguruTheme.colors.pediatricNoteCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(KanguruTheme.colors.pediatricNoteIconContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = GrowthIcons.Warning,
                    contentDescription = null,
                    tint = KanguruTheme.colors.pediatricNoteIconTint,
                    modifier = Modifier.size(14.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = stringResource(R.string.growth_batch_guidance_title),
                    style = KanguruTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = KanguruTheme.colors.pediatricNoteTitle
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.growth_batch_guidance_desc),
                    style = KanguruTheme.typography.bodySmall,
                    color = KanguruTheme.colors.pediatricNoteSubtitle,
                    lineHeight = KanguruTheme.typography.bodySmall.lineHeight
                )
            }
        }
    }
}
