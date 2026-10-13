package com.kangurusiaga.app.presentation.emergency.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kangurusiaga.app.R
import com.kangurusiaga.app.core.designsystem.theme.CardBorder
import com.kangurusiaga.app.core.designsystem.theme.KanguruTheme
import com.kangurusiaga.app.core.designsystem.theme.TextPrimary
import com.kangurusiaga.app.core.designsystem.theme.TextSecondary
import com.kangurusiaga.app.core.designsystem.theme.White
import com.kangurusiaga.app.domain.model.emergency.EmergencySection

@Composable
fun EmergencyHeroCard(
    heroDrawableName: String,
    heroTag: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageRes = remember(heroDrawableName) {
        resolveDrawableRes(context, heroDrawableName)
    }
    val isDark = KanguruTheme.colors.isDark

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, if (isDark) KanguruTheme.colors.cardBorder else KanguruTheme.colors.infoBorder, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = if (isDark) KanguruTheme.colors.surfaceVariant else KanguruTheme.colors.infoContainer),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Illustration container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 10f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(KanguruTheme.colors.surface)
                    .border(1.dp, KanguruTheme.colors.cardBorder, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = heroTag,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            if (heroTag.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                // Clinical Badge Pill
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(KanguruTheme.colors.surface)
                        .border(1.dp, KanguruTheme.colors.infoBorder, CircleShape)
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = heroTag.uppercase(),
                        style = KanguruTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = KanguruTheme.colors.info,
                        letterSpacing = 0.6.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EmergencySymptomsSection(
    title: String,
    items: List<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = KanguruTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            letterSpacing = (-0.2).sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            items.forEachIndexed { index, symptomText ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 7.dp, end = 12.dp)
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(TextPrimary)
                    )
                    Text(
                        text = symptomText,
                        style = KanguruTheme.typography.bodyMedium,
                        color = TextSecondary,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EmergencyActionAlertCard(
    title: String,
    message: String,
    themeColor: String = "amber",
    modifier: Modifier = Modifier
) {
    val isDark = KanguruTheme.colors.isDark
    val isRed = themeColor == "red" || themeColor == "rose"
    val bgColor = if (isRed) KanguruTheme.colors.errorContainer else KanguruTheme.colors.warningContainer
    val borderColor = if (isRed) KanguruTheme.colors.errorBorder else KanguruTheme.colors.warningBorder
    val iconColor = if (isRed) KanguruTheme.colors.error else KanguruTheme.colors.warning
    val titleColor = if (isRed) KanguruTheme.colors.errorText else KanguruTheme.colors.warningText
    val textColor = if (isRed) KanguruTheme.colors.errorText else KanguruTheme.colors.warningText

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = KanguruTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                style = KanguruTheme.typography.bodySmall,
                color = textColor,
                lineHeight = 21.sp,
                modifier = Modifier.padding(start = 28.dp)
            )
        }
    }
}

@Composable
fun EmergencySectionRenderer(
    section: EmergencySection,
    modifier: Modifier = Modifier
) {
    when (section) {
        is EmergencySection.SymptomsList -> {
            EmergencySymptomsSection(
                title = section.title,
                items = section.items,
                modifier = modifier
            )
        }
        is EmergencySection.ActionAlert -> {
            EmergencyActionAlertCard(
                title = section.title,
                message = section.message,
                themeColor = section.themeColor,
                modifier = modifier
            )
        }
    }
}

private fun resolveDrawableRes(context: Context, name: String): Int {
    if (name.isBlank()) return R.drawable.ic_card_emergency
    val id = context.resources.getIdentifier(name, "drawable", context.packageName)
    return if (id != 0) id else R.drawable.ic_card_emergency
}
