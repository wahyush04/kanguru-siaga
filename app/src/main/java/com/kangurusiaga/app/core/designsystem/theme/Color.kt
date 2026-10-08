package com.kangurusiaga.app.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// ============================================================================
// STATIC PALETTE CONSTANTS
// Fixed brand constants that do not change across themes
// ============================================================================
val BrandPink = Color(0xFFFF5C7A)
val BrandDarkPink = Color(0xFFE04866)
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)
val ErrorRed = Color(0xFFDC2626)
val TypographyDefaultColor = Color(0xFF1E293B)

// PMK Gradient & Timer Palette (consistent warm pink hues across light/dark)
val BrandPinkGradientStart = Color(0xFFFF5C77)
val BrandPinkGradientMid = Color(0xFFFF6D88)
val BrandPinkGradientEnd = Color(0xFFFF7B96)
val BrandPinkTrack = Color(0xFFFFE4E8)
val BrandPinkBorder = Color(0xFFFFCCD5)
val BrandPinkAccent = Color(0xFFFF8DA3)

// Fenton Reference Curve Percentile Colors (Standardized clinical colors)
val FentonP97 = Color(0xFFF87171)
val FentonP90 = Color(0xFFFB923C)
val FentonP50 = Color(0xFF34D399)
val FentonP10 = Color(0xFFFCD34D)
val FentonP3 = Color(0xFFFDA4AF)

// ============================================================================
// THEME-REACTIVE DYNAMIC COLOR TOKENS
// These properties resolve to KanguruTheme.colors based on the active theme
// (Terang Hangat, Redup Ruang Menyusui, or Sistem).
// All existing composables referencing these tokens automatically adapt!
// ============================================================================

val BrandBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.background

val BrandCardBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.cardBorder

val CardBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.cardBackground

val CardBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.cardBorder

val DividerColor: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.divider

val TextPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.textPrimary

val TextSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.textSecondary

val TextTertiary: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.textTertiary

val BrandLightPink: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.primaryContainer

val BrandPeach: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.secondaryContainer

val BrandSoftGreen: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.successContainer

val BrandTextGreen: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.onSuccessContainer

val BrandSoftAmber: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.warningContainer

val BrandTextAmber: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.onWarningContainer

val BrandSoftBlue: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.infoContainer

val BrandTextBlue: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.onInfoContainer

val ErrorContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.errorContainer

val GrowthCardRed: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.growthCardRed

val GrowthCardGreen: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.growthCardGreen

val GrowthCardBlue: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.growthCardBlue

val GrowthTipBg: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.tipBackground

val GrowthTipBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.tipBorder

val GrowthTipText: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.tipText

val InputBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.inputBackground

val InputBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.inputBorder

val DragHandleColor: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.dragHandle

val CaregiverIbuColor: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.caregiverIbu

val CaregiverAyahColor: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.caregiverAyah

val CaregiverPendampingColor: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.caregiverPendamping

val CaregiverPauseColor: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.caregiverPause

val TimelineTrackColor: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.timelineTrack


