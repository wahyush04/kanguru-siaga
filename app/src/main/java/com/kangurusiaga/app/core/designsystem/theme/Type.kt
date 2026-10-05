package com.kangurusiaga.app.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 14.sp
    )
)

val LocalTextScaleFactor = androidx.compose.runtime.staticCompositionLocalOf { 1.0f }

fun getScaledTypography(scaleFactor: Float = 1.0f): Typography {
    if (scaleFactor == 1.0f) return Typography
    return Typography(
        displayLarge = Typography.displayLarge.copy(
            fontSize = (32 * scaleFactor).sp,
            lineHeight = (40 * scaleFactor).sp
        ),
        displayMedium = Typography.displayMedium.copy(
            fontSize = (28 * scaleFactor).sp,
            lineHeight = (36 * scaleFactor).sp
        ),
        headlineLarge = Typography.headlineLarge.copy(
            fontSize = (24 * scaleFactor).sp,
            lineHeight = (32 * scaleFactor).sp
        ),
        headlineMedium = Typography.headlineMedium.copy(
            fontSize = (20 * scaleFactor).sp,
            lineHeight = (28 * scaleFactor).sp
        ),
        titleLarge = Typography.titleLarge.copy(
            fontSize = (18 * scaleFactor).sp,
            lineHeight = (24 * scaleFactor).sp
        ),
        titleMedium = Typography.titleMedium.copy(
            fontSize = (16 * scaleFactor).sp,
            lineHeight = (22 * scaleFactor).sp
        ),
        bodyLarge = Typography.bodyLarge.copy(
            fontSize = (16 * scaleFactor).sp,
            lineHeight = (24 * scaleFactor).sp
        ),
        bodyMedium = Typography.bodyMedium.copy(
            fontSize = (14 * scaleFactor).sp,
            lineHeight = (20 * scaleFactor).sp
        ),
        labelLarge = Typography.labelLarge.copy(
            fontSize = (14 * scaleFactor).sp,
            lineHeight = (20 * scaleFactor).sp
        ),
        labelMedium = Typography.labelMedium.copy(
            fontSize = (12 * scaleFactor).sp,
            lineHeight = (16 * scaleFactor).sp
        ),
        labelSmall = Typography.labelSmall.copy(
            fontSize = (11 * scaleFactor).sp,
            lineHeight = (14 * scaleFactor).sp
        )
    )
}

