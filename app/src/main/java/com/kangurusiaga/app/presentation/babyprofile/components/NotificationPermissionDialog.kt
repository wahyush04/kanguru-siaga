package com.kangurusiaga.app.presentation.babyprofile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme

@Composable
fun NotificationPermissionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        containerColor = KanguruTheme.colors.surface,
        titleContentColor = KanguruTheme.colors.textPrimary,
        textContentColor = KanguruTheme.colors.textSecondary,
        icon = {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = KanguruTheme.colors.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = KanguruTheme.colors.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Text(
                text = stringResource(R.string.notification_permission_dialog_title),
                style = KanguruTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = KanguruTheme.colors.textPrimary,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.notification_permission_dialog_subtitle),
                    style = KanguruTheme.typography.bodyMedium,
                    color = KanguruTheme.colors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(2.dp))

                // 1. Pemberian ASI / Nutrisi (WaterDrop - Tetesan ASI)
                BenefitItem(
                    icon = Icons.Default.WaterDrop,
                    text = stringResource(R.string.notification_permission_dialog_benefit_feeding),
                    iconContainerColor = KanguruTheme.colors.infoContainer,
                    iconTint = KanguruTheme.colors.info
                )

                // 2. Kontak Kulit PMK (Favorite - Dekapan Skin-to-Skin)
                BenefitItem(
                    icon = Icons.Default.Favorite,
                    text = stringResource(R.string.notification_permission_dialog_benefit_pmk),
                    iconContainerColor = KanguruTheme.colors.primaryContainer,
                    iconTint = KanguruTheme.colors.primary
                )

                // 3. Timbang Berat Badan & Kegawatan (Scale - Timbangan Bayi)
                BenefitItem(
                    icon = Icons.Default.Scale,
                    text = stringResource(R.string.notification_permission_dialog_benefit_health),
                    iconContainerColor = KanguruTheme.colors.warningContainer,
                    iconTint = KanguruTheme.colors.warning
                )
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KanguruTheme.colors.primary,
                        contentColor = KanguruTheme.colors.onPrimary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.notification_permission_dialog_btn_allow),
                        style = KanguruTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = KanguruTheme.colors.onPrimary
                    )
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = stringResource(R.string.notification_permission_dialog_btn_later),
                        style = KanguruTheme.typography.labelLarge,
                        color = KanguruTheme.colors.textSecondary
                    )
                }
            }
        },
        dismissButton = null
    )
}

@Composable
private fun BenefitItem(
    icon: ImageVector,
    text: String,
    iconContainerColor: Color,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = KanguruTheme.colors.cardBackground,
                shape = RoundedCornerShape(14.dp)
            )
            .border(
                width = 1.dp,
                color = KanguruTheme.colors.cardBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = iconContainerColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            style = KanguruTheme.typography.bodySmall,
            color = KanguruTheme.colors.textPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}
