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

// Theme Swatch Previews (used in settings/theme picker)
val WarmLightSwatches = listOf(Color(0xFFFFF9F6), Color(0xFFFF5C77), Color(0xFFF59E0B))
val DimNursingSwatches = listOf(Color(0xFF141312), Color(0xFFFF5C77), Color(0xFFA8A399))
val SystemThemeSwatches = listOf(Color(0xFFFAF7F2), Color(0xFFFF8598), Color(0xFF141312))

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

val CategoryPurple: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.categoryPurple

val CategoryPurpleContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.categoryPurpleContainer

val CategoryPurpleBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.categoryPurpleBorder

val CategoryPurpleText: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.categoryPurpleText

val CategoryTeal: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.categoryTeal

val CategoryTealContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.categoryTealContainer

val CategoryTealBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.categoryTealBorder

val CategoryTealText: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.categoryTealText

val CategoryOrange: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.categoryOrange

val CategoryOrangeContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.categoryOrangeContainer

val CategoryOrangeBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.categoryOrangeBorder

val CategoryOrangeText: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.categoryOrangeText

val CategoryRose: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.categoryRose

val CategoryRoseContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.categoryRoseContainer

val CategoryRoseBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.categoryRoseBorder

val CategoryRoseText: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.categoryRoseText

val EmergencyBannerBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.emergencyBannerBackground

val EmergencyBannerBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.emergencyBannerBorder

val EmergencyBannerTitle: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.emergencyBannerTitle

val EmergencyBannerSubtitle: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.emergencyBannerSubtitle

val EmergencyAction: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.emergencyAction

val DisclaimerBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.disclaimerBackground

val DisclaimerBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.disclaimerBorder

val DisclaimerTitle: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.disclaimerTitle

val DisclaimerText: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.disclaimerText

val GrowthLength: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.growthLength

val GrowthLengthBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.growthLengthBorder

val GrowthHead: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.growthHead

val GrowthHeadBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.growthHeadBorder

// Fenton Batch Input Screen Convenience Tokens
val GrowthBabyBannerBg: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.growthBabyBannerBg

val GrowthBabyAvatarBg: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.growthBabyAvatarBg

val GrowthBabyAvatarBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.growthBabyAvatarBorder

val GrowthBabyGenderBadgeBg: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.growthBabyGenderBadgeBg

val GrowthBabyGenderBadgeText: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.growthBabyGenderBadgeText

val GrowthPmaHighlight: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.growthPmaHighlight

val GrowthTableDashedBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.growthTableDashedBorder

val GrowthTableDashedBg: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.growthTableDashedBg

val GrowthTableDashedText: Color
    @Composable
    @ReadOnlyComposable
    get() = KanguruTheme.colors.growthTableDashedText



