package com.kangurusiaga.app.core.designsystem.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.kangurusiaga.app.domain.model.settings.AppTextScale
import com.kangurusiaga.app.domain.model.settings.AppThemeMode

/**
 * Semantic color design tokens for Kanguru Siaga.
 * Covers background, surface, text, status (success, warning, error, info),
 * PMK gradients, and Fenton chart palette.
 */
@Immutable
data class KanguruColors(
    val isDark: Boolean,

    // Core Background & Surfaces
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val surfaceVariant: Color,
    val cardBackground: Color,
    val cardBorder: Color,
    val divider: Color,
    val outline: Color,
    val outlineVariant: Color,

    // Primary Brand
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,

    // Secondary / Soft Brand
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,

    // Typography
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textOnDark: Color,

    // Status Colors (Containers, Text, and Borders for Badges/Chips)
    val success: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val successBorder: Color,

    val warning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val warningBorder: Color,

    val error: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,
    val errorBorder: Color,

    val info: Color,
    val infoContainer: Color,
    val onInfoContainer: Color,
    val infoBorder: Color,

    // PMK / Gradients
    val pinkGradientStart: Color,
    val pinkGradientMid: Color,
    val pinkGradientEnd: Color,
    val timerTrack: Color,
    val timerAccent: Color,

    // Growth / Fenton Specific
    val fentonP97: Color,
    val fentonP90: Color,
    val fentonP50: Color,
    val fentonP10: Color,
    val fentonP3: Color,
    val growthCardRed: Color,
    val growthCardGreen: Color,
    val growthCardBlue: Color,
    val tipBackground: Color,
    val tipBorder: Color,
    val tipText: Color
) {
    val primaryBorder: Color
        get() = if (isDark) Color(0xFF5E2734) else Color(0xFFFFD1DC)

    val successText: Color
        get() = onSuccessContainer

    val warningText: Color
        get() = onWarningContainer

    val errorText: Color
        get() = onErrorContainer

    val infoText: Color
        get() = onInfoContainer
}

// 1. TERANG HANGAT (Warm Light - Default)
val LightKanguruColors = KanguruColors(
    isDark = false,
    background = Color(0xFFFAF7F2),
    surface = Color(0xFFFFFFFF),
    surfaceElevated = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF5EFE9),
    cardBackground = Color(0xFFFFFFFF),
    cardBorder = Color(0xFFF1ECE6),
    divider = Color(0xFFF1ECE6),
    outline = Color(0xFFE5DFD7),
    outlineVariant = Color(0xFFF1ECE6),

    primary = Color(0xFFFF5C7A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFF0F3),
    onPrimaryContainer = Color(0xFFE04866),

    secondary = Color(0xFFFFEFEA),
    onSecondary = Color(0xFF1E1E1E),
    secondaryContainer = Color(0xFFFFEFEA),
    onSecondaryContainer = Color(0xFF8A3C1B),

    textPrimary = Color(0xFF1E1E1E),
    textSecondary = Color(0xFF6B7280),
    textTertiary = Color(0xFF9CA3AF),
    textOnDark = Color(0xFFFFFFFF),

    success = Color(0xFF16A34A),
    successContainer = Color(0xFFEAF8F1),
    onSuccessContainer = Color(0xFF15803D),
    successBorder = Color(0xFFBBF7D0),

    warning = Color(0xFFD97706),
    warningContainer = Color(0xFFFFF6E9),
    onWarningContainer = Color(0xFFB45309),
    warningBorder = Color(0xFFFED7AA),

    error = Color(0xFFDC2626),
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF991B1B),
    errorBorder = Color(0xFFFECACA),

    info = Color(0xFF2563EB),
    infoContainer = Color(0xFFEBF6FF),
    onInfoContainer = Color(0xFF1D4ED8),
    infoBorder = Color(0xFFBFDBFE),

    pinkGradientStart = Color(0xFFFF5C77),
    pinkGradientMid = Color(0xFFFF6D88),
    pinkGradientEnd = Color(0xFFFF7B96),
    timerTrack = Color(0xFFFFE4E8),
    timerAccent = Color(0xFFFF8DA3),

    fentonP97 = Color(0xFFF87171),
    fentonP90 = Color(0xFFFB923C),
    fentonP50 = Color(0xFF34D399),
    fentonP10 = Color(0xFFFCD34D),
    fentonP3 = Color(0xFFFDA4AF),
    growthCardRed = Color(0xFFFEECEC),
    growthCardGreen = Color(0xFFEAF8F1),
    growthCardBlue = Color(0xFFEBF4FE),
    tipBackground = Color(0xFFFFF9EA),
    tipBorder = Color(0xFFFEEAB8),
    tipText = Color(0xFF92400E)
)

// 2. REDUP RUANG MENYUSUI (Dim Nursing Room - Dark)
// Warm espresso/charcoal tones designed for eye comfort during nighttime nursing/monitoring.
val DarkKanguruColors = KanguruColors(
    isDark = true,
    background = Color(0xFF141312),
    surface = Color(0xFF1E1D1A),
    surfaceElevated = Color(0xFF262421),
    surfaceVariant = Color(0xFF2B2824),
    cardBackground = Color(0xFF1E1D1A),
    cardBorder = Color(0xFF33302B),
    divider = Color(0xFF2B2824),
    outline = Color(0xFF3E3A34),
    outlineVariant = Color(0xFF2E2B27),

    primary = Color(0xFFFF5C7A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF3E1A22),
    onPrimaryContainer = Color(0xFFFFB3C1),

    secondary = Color(0xFF38231B),
    onSecondary = Color(0xFFF5E6DC),
    secondaryContainer = Color(0xFF38231B),
    onSecondaryContainer = Color(0xFFFFD7C2),

    textPrimary = Color(0xFFF5F3EF),
    textSecondary = Color(0xFFA8A399),
    textTertiary = Color(0xFF757065),
    textOnDark = Color(0xFFF5F3EF),

    success = Color(0xFF4ADE80),
    successContainer = Color(0xFF152A1E),
    onSuccessContainer = Color(0xFF86EFAC),
    successBorder = Color(0xFF234C34),

    warning = Color(0xFFFBBF24),
    warningContainer = Color(0xFF33240F),
    onWarningContainer = Color(0xFFFDE68A),
    warningBorder = Color(0xFF593E1A),

    error = Color(0xFFF87171),
    errorContainer = Color(0xFF381717),
    onErrorContainer = Color(0xFFFECACA),
    errorBorder = Color(0xFF5E2727),

    info = Color(0xFF60A5FA),
    infoContainer = Color(0xFF152538),
    onInfoContainer = Color(0xFFBFDBFE),
    infoBorder = Color(0xFF233E5E),

    pinkGradientStart = Color(0xFFFF5C77),
    pinkGradientMid = Color(0xFFFF6D88),
    pinkGradientEnd = Color(0xFFFF7B96),
    timerTrack = Color(0xFF3B1D24),
    timerAccent = Color(0xFFFF8DA3),

    fentonP97 = Color(0xFFF87171),
    fentonP90 = Color(0xFFFB923C),
    fentonP50 = Color(0xFF34D399),
    fentonP10 = Color(0xFFFCD34D),
    fentonP3 = Color(0xFFFDA4AF),
    growthCardRed = Color(0xFF331619),
    growthCardGreen = Color(0xFF152A1E),
    growthCardBlue = Color(0xFF152538),
    tipBackground = Color(0xFF2D2314),
    tipBorder = Color(0xFF4E3D20),
    tipText = Color(0xFFFDE68A)
)

val LocalKanguruColors = staticCompositionLocalOf { LightKanguruColors }

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFF5C7A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFF0F3),
    onPrimaryContainer = Color(0xFFE04866),
    secondary = Color(0xFFFFEFEA),
    onSecondary = Color(0xFF1E1E1E),
    secondaryContainer = Color(0xFFFFEFEA),
    onSecondaryContainer = Color(0xFF8A3C1B),
    tertiary = Color(0xFF2DA467),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFEAF8F1),
    onTertiaryContainer = Color(0xFF2DA467),
    background = Color(0xFFFAF7F2),
    onBackground = Color(0xFF1E1E1E),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFFF5EFE9),
    onSurfaceVariant = Color(0xFF6B7280),
    surfaceContainer = Color(0xFFFFFFFF),
    surfaceContainerHigh = Color(0xFFFDFCFA),
    surfaceContainerHighest = Color(0xFFF1ECE6),
    outline = Color(0xFFE5DFD7),
    outlineVariant = Color(0xFFF1ECE6),
    error = Color(0xFFDC2626),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFFDC2626)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF5C7A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF3E1A22),
    onPrimaryContainer = Color(0xFFFFB3C1),
    secondary = Color(0xFF38231B),
    onSecondary = Color(0xFFF5E6DC),
    secondaryContainer = Color(0xFF38231B),
    onSecondaryContainer = Color(0xFFFFD7C2),
    tertiary = Color(0xFF4ADE80),
    onTertiary = Color(0xFF0F2918),
    tertiaryContainer = Color(0xFF152A1E),
    onTertiaryContainer = Color(0xFF86EFAC),
    background = Color(0xFF141312),
    onBackground = Color(0xFFF5F3EF),
    surface = Color(0xFF1E1D1A),
    onSurface = Color(0xFFF5F3EF),
    surfaceVariant = Color(0xFF282622),
    onSurfaceVariant = Color(0xFFA8A399),
    surfaceContainer = Color(0xFF1E1D1A),
    surfaceContainerHigh = Color(0xFF262421),
    surfaceContainerHighest = Color(0xFF33302B),
    outline = Color(0xFF33302B),
    outlineVariant = Color(0xFF2B2824),
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF381717),
    onErrorContainer = Color(0xFFFECACA)
)

object KanguruTheme {
    val dimensions: Dimensions
        @Composable
        @ReadOnlyComposable
        get() = LocalDimensions.current

    val colors: KanguruColors
        @Composable
        @ReadOnlyComposable
        get() = LocalKanguruColors.current

    val isDark: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalKanguruColors.current.isDark
}

@Composable
fun KanguruSiagaTheme(
    themeMode: AppThemeMode = AppThemeMode.WARM_LIGHT,
    textScale: AppTextScale = AppTextScale.STANDARD,
    darkTheme: Boolean = when (themeMode) {
        AppThemeMode.WARM_LIGHT -> false
        AppThemeMode.DIM_NURSING -> true
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
    },
    content: @Composable () -> Unit
) {
    val kanguruColors = if (darkTheme) DarkKanguruColors else LightKanguruColors
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val dimensions = Dimensions()
    val scaledTypography = getScaledTypography(textScale.scaleFactor)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalDimensions provides dimensions,
        LocalTextScaleFactor provides textScale.scaleFactor,
        LocalKanguruColors provides kanguruColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = scaledTypography,
            shapes = KanguruShapes,
            content = content
        )
    }
}
