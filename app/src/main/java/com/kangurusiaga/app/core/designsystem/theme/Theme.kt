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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.core.view.WindowCompat
import com.kangurusiaga.app.domain.model.settings.AppTextScale
import com.kangurusiaga.app.domain.model.settings.AppThemeMode

/**
 * Semantic color design tokens for Kanguru Siaga.
 * Covers background, surface, text, status (success, warning, error, info),
 * PMK gradients, and Fenton chart palette.
 */
@Immutable
abstract class KanguruColors {
    abstract val isDark: Boolean

    // Core Background & Surfaces
    abstract val background: Color
    abstract val surface: Color
    abstract val surfaceElevated: Color
    abstract val surfaceVariant: Color
    abstract val cardBackground: Color
    abstract val cardBorder: Color
    abstract val divider: Color
    abstract val outline: Color
    abstract val outlineVariant: Color

    // Primary Brand
    abstract val primary: Color
    abstract val onPrimary: Color
    abstract val primaryContainer: Color
    abstract val onPrimaryContainer: Color

    // Secondary / Soft Brand
    abstract val secondary: Color
    abstract val onSecondary: Color
    abstract val secondaryContainer: Color
    abstract val onSecondaryContainer: Color

    // Typography
    abstract val textPrimary: Color
    abstract val textSecondary: Color
    abstract val textTertiary: Color
    abstract val textOnDark: Color

    // Status Colors (Containers, Text, and Borders for Badges/Chips)
    abstract val success: Color
    abstract val successContainer: Color
    abstract val onSuccessContainer: Color
    abstract val successBorder: Color

    abstract val warning: Color
    abstract val warningContainer: Color
    abstract val onWarningContainer: Color
    abstract val warningBorder: Color

    abstract val error: Color
    abstract val errorContainer: Color
    abstract val onErrorContainer: Color
    abstract val errorBorder: Color

    abstract val info: Color
    abstract val infoContainer: Color
    abstract val onInfoContainer: Color
    abstract val infoBorder: Color

    // PMK / Gradients
    abstract val pinkGradientStart: Color
    abstract val pinkGradientMid: Color
    abstract val pinkGradientEnd: Color
    abstract val timerTrack: Color
    abstract val timerAccent: Color

    // Growth / Fenton Specific
    abstract val fentonP97: Color
    abstract val fentonP90: Color
    abstract val fentonP50: Color
    abstract val fentonP10: Color
    abstract val fentonP3: Color
    abstract val growthCardRed: Color
    abstract val growthCardGreen: Color
    abstract val growthCardBlue: Color
    abstract val tipBackground: Color
    abstract val tipBorder: Color
    abstract val tipText: Color

    // Form Inputs & Modals (Stitch aligned)
    abstract val inputBackground: Color
    abstract val inputFocusedBackground: Color
    abstract val inputBorder: Color
    abstract val dragHandle: Color
    abstract val closeButtonBackground: Color
    abstract val closeButtonTint: Color
    abstract val buttonSecondaryBorder: Color
    abstract val buttonSecondaryText: Color

    // Action Pills (Photo/Gallery & Quick Actions)
    abstract val actionPillPrimaryBackground: Color
    abstract val actionPillPrimaryText: Color
    abstract val actionPillSecondaryBackground: Color
    abstract val actionPillSecondaryText: Color

    // Gender Selection Tokens
    abstract val genderMaleActiveBackground: Color
    abstract val genderMaleActiveBorder: Color
    abstract val genderMaleActiveText: Color
    abstract val genderMaleActiveIconContainer: Color
    abstract val genderMaleInactiveIconContainer: Color
    abstract val genderMaleIcon: Color
    abstract val genderMaleInactiveIcon: Color

    abstract val genderFemaleActiveBackground: Color
    abstract val genderFemaleActiveBorder: Color
    abstract val genderFemaleActiveText: Color
    abstract val genderFemaleActiveIconContainer: Color
    abstract val genderFemaleInactiveIconContainer: Color
    abstract val genderFemaleIcon: Color
    abstract val genderFemaleInactiveIcon: Color

    // Avatar Frame
    abstract val avatarRingStart: Color
    abstract val avatarRingEnd: Color
    abstract val avatarInnerBackground: Color

    // Continuous PMK Caregiver & Timeline Tokens
    abstract val caregiverIbu: Color
    abstract val caregiverIbuContainer: Color
    abstract val caregiverIbuText: Color
    abstract val caregiverAyah: Color
    abstract val caregiverAyahContainer: Color
    abstract val caregiverAyahText: Color
    abstract val caregiverPendamping: Color
    abstract val caregiverPendampingContainer: Color
    abstract val caregiverPendampingText: Color
    abstract val caregiverPause: Color
    abstract val caregiverPauseContainer: Color
    abstract val caregiverPauseText: Color
    abstract val timelineTrack: Color

    // Temperature Modal Tokens (Google Stitch aligned)
    abstract val tempColdTrack: Color
    abstract val tempColdText: Color
    abstract val tempNormalTrack: Color
    abstract val tempNormalText: Color
    abstract val tempWarmTrack: Color
    abstract val tempWarmText: Color
    abstract val tempNormalBadgeBg: Color
    abstract val tempSliderTrack: Color
    abstract val tempStepperBg: Color
    abstract val tempStepperBorder: Color
    abstract val tempStepperIcon: Color
    abstract val tempCancelBg: Color
    abstract val tempCancelText: Color

    // Toast / Snackbar Tokens (Pill shape, high-contrast)
    abstract val toastBackground: Color
    abstract val toastText: Color
    abstract val toastSuccessIconBg: Color
    abstract val toastSuccessIconTint: Color
    abstract val toastErrorIconBg: Color
    abstract val toastErrorIconTint: Color

    // Multi-Category / Feature Accent Tokens (Education, Guides, Reminders)
    abstract val categoryPurple: Color
    abstract val categoryPurpleContainer: Color
    abstract val categoryPurpleBorder: Color
    abstract val categoryPurpleText: Color

    abstract val categoryTeal: Color
    abstract val categoryTealContainer: Color
    abstract val categoryTealBorder: Color
    abstract val categoryTealText: Color

    abstract val categoryOrange: Color
    abstract val categoryOrangeContainer: Color
    abstract val categoryOrangeBorder: Color
    abstract val categoryOrangeText: Color

    abstract val categoryRose: Color
    abstract val categoryRoseContainer: Color
    abstract val categoryRoseBorder: Color
    abstract val categoryRoseText: Color

    // Emergency & Hotline Banner Tokens
    abstract val emergencyBannerBackground: Color
    abstract val emergencyBannerBorder: Color
    abstract val emergencyBannerTitle: Color
    abstract val emergencyBannerSubtitle: Color
    abstract val emergencyAction: Color

    // Medical Disclaimer Card Tokens
    abstract val disclaimerBackground: Color
    abstract val disclaimerBorder: Color
    abstract val disclaimerTitle: Color
    abstract val disclaimerText: Color

    // Growth Parameter Specific Tokens
    abstract val growthLength: Color
    abstract val growthLengthBorder: Color
    abstract val growthHead: Color
    abstract val growthHeadBorder: Color

    // Ambient / Illustration Tokens (Splash & Onboarding)
    abstract val ambientRosePulse: Color
    abstract val ambientAmberPulse: Color
    abstract val ambientWave1: Color
    abstract val ambientWave2: Color
    abstract val onboardingCardBgStart: Color
    abstract val onboardingCardBgEnd: Color

    // Home Kasidig Grid & Hero Banner Tokens (Stitch aligned)
    abstract val heroBannerBgStart: Color
    abstract val heroBannerBgMid: Color
    abstract val heroBannerBgEnd: Color
    abstract val heroBannerBorder: Color
    abstract val heroBadgeBg: Color
    abstract val heroBadgeText: Color

    abstract val gridIconRoseStart: Color
    abstract val gridIconRoseEnd: Color
    abstract val gridIconRoseOuterStart: Color
    abstract val gridIconRoseOuterEnd: Color
    abstract val gridSubtitleRose: Color

    abstract val gridIconTealStart: Color
    abstract val gridIconTealEnd: Color
    abstract val gridIconTealOuterStart: Color
    abstract val gridIconTealOuterEnd: Color
    abstract val gridSubtitleTeal: Color

    abstract val gridIconAmberStart: Color
    abstract val gridIconAmberEnd: Color
    abstract val gridIconAmberOuterStart: Color
    abstract val gridIconAmberOuterEnd: Color
    abstract val gridSubtitleAmber: Color

    abstract val gridIconPinkStart: Color
    abstract val gridIconPinkEnd: Color
    abstract val gridIconPinkOuterStart: Color
    abstract val gridIconPinkOuterEnd: Color
    abstract val gridSubtitlePink: Color

    abstract val gridIconBlueStart: Color
    abstract val gridIconBlueEnd: Color
    abstract val gridIconBlueOuterStart: Color
    abstract val gridIconBlueOuterEnd: Color
    abstract val gridSubtitleBlue: Color

    abstract val gridIconPurpleStart: Color
    abstract val gridIconPurpleEnd: Color
    abstract val gridIconPurpleOuterStart: Color
    abstract val gridIconPurpleOuterEnd: Color
    abstract val gridSubtitlePurple: Color

    open val primaryBorder: Color
        get() = if (isDark) Color(0xFF5E2734) else Color(0xFFFFD1DC)

    open val successText: Color
        get() = onSuccessContainer

    open val warningText: Color
        get() = onWarningContainer

    open val errorText: Color
        get() = onErrorContainer

    open val pmkGaugeTrack: Color
        get() = if (isDark) Color(0xFF3A2028) else Color(0xFFFFE4E8)

    open val pmkPauseButtonBg: Color
        get() = if (isDark) Color(0xFF3D2029) else Color(0xFFFFE8EC)

    open val pmkPauseButtonBorder: Color
        get() = if (isDark) Color(0xFF5A2C38) else Color(0xFFFFCCD5)

    open val pmkCaregiverTitleBrown: Color
        get() = if (isDark) Color(0xFFFDE68A) else Color(0xFF78350F)

    open val pmkCaregiverSubtitleOrange: Color
        get() = if (isDark) Color(0xFFFCA5A5) else Color(0xFFD9531E)

    open val pmkCaregiverBadgeBg: Color
        get() = if (isDark) Color(0xFF452614) else Color(0xFFFEEAD8)

    open val pmkCaregiverIbu: Color
        get() = if (isDark) Color(0xFFFF758F) else Color(0xFFFF5C77)

    open val pmkCaregiverIbuBg: Color
        get() = if (isDark) Color(0xFF3D2029) else Color(0xFFFFF0F3)

    open val pmkCaregiverIbuBorder: Color
        get() = if (isDark) Color(0xFF5A2C38) else Color(0xFFFFE4E8)

    open val pmkCaregiverAyah: Color
        get() = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7)

    open val pmkCaregiverAyahBg: Color
        get() = if (isDark) Color(0xFF132E47) else Color(0xFFF0F9FF)

    open val pmkCaregiverAyahBorder: Color
        get() = if (isDark) Color(0xFF1E4E73) else Color(0xFFBAE6FD)

    open val pmkCaregiverPendamping: Color
        get() = if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706)

    open val pmkCaregiverPendampingBg: Color
        get() = if (isDark) Color(0xFF3D2E14) else Color(0xFFFFFBEB)

    open val pmkCaregiverPendampingBorder: Color
        get() = if (isDark) Color(0xFF5C451D) else Color(0xFFFEF3C7)

    open val pmkPauseDashedBg: Color
        get() = if (isDark) Color(0xFF332712) else Color(0xFFFFFBEB)

    open val pmkPauseDashedBorder: Color
        get() = if (isDark) Color(0xFF523F1B) else Color(0xFFFDE68A)

    open val pmkDoctorNoteBg: Color
        get() = if (isDark) Color(0xFF381E26) else Color(0xFFFFF0F3)

    open val pmkDoctorNoteBorder: Color
        get() = if (isDark) Color(0xFF542835) else Color(0xFFFFE4E8)

    open val pmkTargetMetMarker: Color
        get() = if (isDark) Color(0xFFFF758F) else Color(0xFFFF5C77)

    open val pmkTargetUnmetMarker: Color
        get() = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)

    open val pmkMotivationCardBg: Color
        get() = if (isDark) Color(0xFF3A1C25) else Color(0xFFFFE4E8)

    open val pmkMotivationHeartBg: Color
        get() = if (isDark) Color(0xFFFF758F) else Color(0xFFFF5C77)

    open val infoText: Color
        get() = onInfoContainer

    // Educational & Emergency Hero Cards (Google Stitch aligned)
    open val heroPlayButton: Color
        get() = if (isDark) Color(0xFFFB7185) else Color(0xFFF43F5E)
    open val heroPlayButtonBorder: Color
        get() = Color(0x66FFFFFF)
    open val heroPlayButtonRing: Color
        get() = Color(0x33FFFFFF)
    open val heroDurationBadgeBg: Color
        get() = Color(0x99000000)
    open val heroDurationBadgeText: Color
        get() = Color(0xFFFFFFFF)
    open val heroTypeBadgeBg: Color
        get() = if (isDark) Color(0xFFE11D48) else Color(0xFFF43F5E)
    open val heroTypeBadgeText: Color
        get() = Color(0xFFFFFFFF)

    open val headerPillRoseBg: Color
        get() = if (isDark) Color(0xFF4C0519) else Color(0xFFFFE4E6)
    open val headerPillRoseText: Color
        get() = if (isDark) Color(0xFFFDA4AF) else Color(0xFFE11D48)
    open val headerPillRoseBorder: Color
        get() = if (isDark) Color(0xFF881337) else Color(0xFFFECDD3)

    open val badgeEmeraldBg: Color
        get() = if (isDark) Color(0xFF064E3B) else Color(0xFFD1FAE5)
    open val badgeEmeraldText: Color
        get() = if (isDark) Color(0xFF6EE7B7) else Color(0xFF047857)
    open val badgeEmeraldBorder: Color
        get() = if (isDark) Color(0xFF065F46) else Color(0xFFA7F3D0)

    open val pediatricNoteCardBg: Color
        get() = if (isDark) Color(0xFF2E2413) else Color(0xFFFFFBEB)
    open val pediatricNoteCardBorder: Color
        get() = if (isDark) Color(0xFF78350F) else Color(0xFFFDE68A)
    open val pediatricNoteIconContainer: Color
        get() = if (isDark) Color(0xFF451A03) else Color(0xFFFEF3C7)
    open val pediatricNoteIconTint: Color
        get() = if (isDark) Color(0xFFFDE68A) else Color(0xFF92400E)
    open val pediatricNoteTitle: Color
        get() = if (isDark) Color(0xFFFEF3C7) else Color(0xFF78350F)
    open val pediatricNoteSubtitle: Color
        get() = if (isDark) Color(0xFFFCD34D) else Color(0xFFB45309)
    open val pediatricNoteQuoteCardBg: Color
        get() = if (isDark) Color(0xFF1E170E) else Color(0xCCFFFFFF)
    open val pediatricNoteQuoteCardBorder: Color
        get() = if (isDark) Color(0xFF78350F) else Color(0x99FDE68A)
    open val pediatricNoteQuoteText: Color
        get() = if (isDark) Color(0xFFFDE68A) else Color(0xFF78350F)

    open val emergencyStickyButtonStart: Color
        get() = if (isDark) Color(0xFFE11D48) else Color(0xFFF43F5E)
    open val emergencyStickyButtonEnd: Color
        get() = if (isDark) Color(0xFFBE123C) else Color(0xFFE11D48)
    open val emergencyStickyButtonText: Color
        get() = Color(0xFFFFFFFF)
}

// 1. TERANG HANGAT (Warm Light - Default)
private class LightKanguruColorsImpl : KanguruColors() {
    override val isDark: Boolean = false
    override val background: Color = Color(0xFFFAF7F2)
    override val surface: Color = Color(0xFFFFFFFF)
    override val surfaceElevated: Color = Color(0xFFFFFFFF)
    override val surfaceVariant: Color = Color(0xFFf8fafc)
    override val cardBackground: Color = Color(0xFFFFFFFF)
    override val cardBorder: Color = Color(0xFFF1ECE6)
    override val divider: Color = Color(0xFFF1ECE6)
    override val outline: Color = Color(0xFFE5DFD7)
    override val outlineVariant: Color = Color(0xFFF1ECE6)

    override val primary: Color = Color(0xFFFF5C7A)
    override val onPrimary: Color = Color(0xFFFFFFFF)
    override val primaryContainer: Color = Color(0xFFFFF0F3)
    override val onPrimaryContainer: Color = Color(0xFFE04866)

    override val secondary: Color = Color(0xFFFFEFEA)
    override val onSecondary: Color = Color(0xFF1E1E1E)
    override val secondaryContainer: Color = Color(0xFFFFEFEA)
    override val onSecondaryContainer: Color = Color(0xFF8A3C1B)

    override val textPrimary: Color = TypographyDefaultColor
    override val textSecondary: Color = Color(0xFF6B7280)
    override val textTertiary: Color = Color(0xFF9CA3AF)
    override val textOnDark: Color = Color(0xFFFFFFFF)

    override val success: Color = Color(0xFF16A34A)
    override val successContainer: Color = Color(0xFFEAF8F1)
    override val onSuccessContainer: Color = Color(0xFF15803D)
    override val successBorder: Color = Color(0xFFBBF7D0)

    override val warning: Color = Color(0xFFD97706)
    override val warningContainer: Color = Color(0xFFFFF6E9)
    override val onWarningContainer: Color = Color(0xFFB45309)
    override val warningBorder: Color = Color(0xFFFED7AA)

    override val error: Color = Color(0xFFDC2626)
    override val errorContainer: Color = Color(0xFFFEE2E2)
    override val onErrorContainer: Color = Color(0xFF991B1B)
    override val errorBorder: Color = Color(0xFFFECACA)

    override val info: Color = Color(0xFF2563EB)
    override val infoContainer: Color = Color(0xFFEBF6FF)
    override val onInfoContainer: Color = Color(0xFF1D4ED8)
    override val infoBorder: Color = Color(0xFFBFDBFE)

    override val pinkGradientStart: Color = Color(0xFFFF5C77)
    override val pinkGradientMid: Color = Color(0xFFFF6D88)
    override val pinkGradientEnd: Color = Color(0xFFFF7B96)
    override val timerTrack: Color = Color(0xFFFFE4E8)
    override val timerAccent: Color = Color(0xFFFF8DA3)

    override val fentonP97: Color = Color(0xFFF87171)
    override val fentonP90: Color = Color(0xFFFB923C)
    override val fentonP50: Color = Color(0xFF34D399)
    override val fentonP10: Color = Color(0xFFFCD34D)
    override val fentonP3: Color = Color(0xFFFDA4AF)
    override val growthCardRed: Color = Color(0xFFFEECEC)
    override val growthCardGreen: Color = Color(0xFFEAF8F1)
    override val growthCardBlue: Color = Color(0xFFEBF4FE)
    override val tipBackground: Color = Color(0xFFFFF9EA)
    override val tipBorder: Color = Color(0xFFFEEAB8)
    override val tipText: Color = Color(0xFF92400E)

    override val inputBackground: Color = Color(0xFFFAF8F6)
    override val inputFocusedBackground: Color = Color(0xFFFFFFFF)
    override val inputBorder: Color = Color(0xFFE2E8F0)
    override val dragHandle: Color = Color(0xFFCBD5E1)
    override val closeButtonBackground: Color = Color(0xFFF1F5F9)
    override val closeButtonTint: Color = Color(0xFF64748B)
    override val buttonSecondaryBorder: Color = Color(0xFFCBD5E1)
    override val buttonSecondaryText: Color = Color(0xFF334155)

    override val actionPillPrimaryBackground: Color = Color(0xFFFFF1F2)
    override val actionPillPrimaryText: Color = Color(0xFFE11D48)
    override val actionPillSecondaryBackground: Color = Color(0xFFF1F5F9)
    override val actionPillSecondaryText: Color = Color(0xFF334155)

    override val genderMaleActiveBackground: Color = Color(0xFFEFF6FF)
    override val genderMaleActiveBorder: Color = Color(0xFF3B82F6)
    override val genderMaleActiveText: Color = Color(0xFF2563EB)
    override val genderMaleActiveIconContainer: Color = Color(0xFFDBEAFE)
    override val genderMaleInactiveIconContainer: Color = Color(0xFFEFF6FF)
    override val genderMaleIcon: Color = Color(0xFF2563EB)
    override val genderMaleInactiveIcon: Color = Color(0xFF3B82F6)

    override val genderFemaleActiveBackground: Color = Color(0xFFFFF1F2)
    override val genderFemaleActiveBorder: Color = Color(0xFFFF5C77)
    override val genderFemaleActiveText: Color = Color(0xFFE11D48)
    override val genderFemaleActiveIconContainer: Color = Color(0xFFFFE4E6)
    override val genderFemaleInactiveIconContainer: Color = Color(0xFFFFF1F2)
    override val genderFemaleIcon: Color = Color(0xFFFF5C77)
    override val genderFemaleInactiveIcon: Color = Color(0xFFFF5C77)

    override val avatarRingStart: Color = Color(0xFFFF5C77)
    override val avatarRingEnd: Color = Color(0xFFFDA4AF)
    override val avatarInnerBackground: Color = Color(0xFFFFF1F2)

    override val caregiverIbu: Color = Color(0xFFFF5C77)
    override val caregiverIbuContainer: Color = Color(0xFFFFF0F3)
    override val caregiverIbuText: Color = Color(0xFFE11D48)
    override val caregiverAyah: Color = Color(0xFF3B82F6)
    override val caregiverAyahContainer: Color = Color(0xFFEFF6FF)
    override val caregiverAyahText: Color = Color(0xFF1D4ED8)
    override val caregiverPendamping: Color = Color(0xFFF59E0B)
    override val caregiverPendampingContainer: Color = Color(0xFFFFFBEB)
    override val caregiverPendampingText: Color = Color(0xFFB45309)
    override val caregiverPause: Color = Color(0xFF94A3B8)
    override val caregiverPauseContainer: Color = Color(0xFFF1F5F9)
    override val caregiverPauseText: Color = Color(0xFF475569)
    override val timelineTrack: Color = Color(0xFFF1ECE6)

    override val tempColdTrack: Color = Color(0xFF7DD3FC)
    override val tempColdText: Color = Color(0xFF0284C7)
    override val tempNormalTrack: Color = Color(0xFF34D399)
    override val tempNormalText: Color = Color(0xFF047857)
    override val tempWarmTrack: Color = Color(0xFFFB7185)
    override val tempWarmText: Color = Color(0xFFF43F5E)
    override val tempNormalBadgeBg: Color = Color(0xFFD1FAE5)
    override val tempSliderTrack: Color = Color(0xFFE2E8F0)
    override val tempStepperBg: Color = Color(0xFFFFFFFF)
    override val tempStepperBorder: Color = Color(0xFFF1F5F9)
    override val tempStepperIcon: Color = Color(0xFF334155)
    override val tempCancelBg: Color = Color(0xFFF1F5F9)
    override val tempCancelText: Color = Color(0xFF475569)

    override val toastBackground: Color = Color(0xFF1E293B)
    override val toastText: Color = Color(0xFFFFFFFF)
    override val toastSuccessIconBg: Color = Color(0xFF10B981)
    override val toastSuccessIconTint: Color = Color(0xFFFFFFFF)
    override val toastErrorIconBg: Color = Color(0xFFEF4444)
    override val toastErrorIconTint: Color = Color(0xFFFFFFFF)

    override val categoryPurple: Color = Color(0xFF9333EA)
    override val categoryPurpleContainer: Color = Color(0xFFFAF5FF)
    override val categoryPurpleBorder: Color = Color(0xFFE9D5FF)
    override val categoryPurpleText: Color = Color(0xFF7E22CE)

    override val categoryTeal: Color = Color(0xFF0D9488)
    override val categoryTealContainer: Color = Color(0xFFF0FDFA)
    override val categoryTealBorder: Color = Color(0xFF99F6E4)
    override val categoryTealText: Color = Color(0xFF0F766E)

    override val categoryOrange: Color = Color(0xFFEA580C)
    override val categoryOrangeContainer: Color = Color(0xFFFFF7ED)
    override val categoryOrangeBorder: Color = Color(0xFFFED7AA)
    override val categoryOrangeText: Color = Color(0xFFC2410C)

    override val categoryRose: Color = Color(0xFFF43F5E)
    override val categoryRoseContainer: Color = Color(0xFFFFF1F2)
    override val categoryRoseBorder: Color = Color(0xFFFECDD3)
    override val categoryRoseText: Color = Color(0xFFBE123C)

    override val emergencyBannerBackground: Color = Color(0xFFFFF0F3)
    override val emergencyBannerBorder: Color = Color(0xFFFFDDE4)
    override val emergencyBannerTitle: Color = Color(0xFFE11D48)
    override val emergencyBannerSubtitle: Color = Color(0xFF9F1239)
    override val emergencyAction: Color = Color(0xFFE11D48)

    override val disclaimerBackground: Color = Color(0xFFFFF4F5)
    override val disclaimerBorder: Color = Color(0xFFFFCCD5)
    override val disclaimerTitle: Color = Color(0xFF4C0519)
    override val disclaimerText: Color = Color(0xFF881337)

    override val growthLength: Color = Color(0xFF10B981)
    override val growthLengthBorder: Color = Color(0xFFDCF5E6)
    override val growthHead: Color = Color(0xFF3B82F6)
    override val growthHeadBorder: Color = Color(0xFFDFEEFF)

    override val ambientRosePulse: Color = Color(0xFFFFE4E6)
    override val ambientAmberPulse: Color = Color(0xFFFEF3C7)
    override val ambientWave1: Color = Color(0xFFF8E5D8)
    override val ambientWave2: Color = Color(0xFFF3D8C8)
    override val onboardingCardBgStart: Color = Color(0xFFFFF0F3)
    override val onboardingCardBgEnd: Color = Color(0xFFFFF6EA)

    // Home Kasidig Grid & Hero Banner Tokens (Stitch aligned)
    override val heroBannerBgStart: Color = Color(0xFFA7F3D0)
    override val heroBannerBgMid: Color = Color(0xFF6EE7B7)
    override val heroBannerBgEnd: Color = Color(0xFF99F6E4)
    override val heroBannerBorder: Color = Color(0xFF5EEAD4).copy(alpha = 0.5f)
    override val heroBadgeBg: Color = Color(0xB3FFFFFF)
    override val heroBadgeText: Color = Color(0xFF115E59)

    override val gridIconRoseStart: Color = Color(0xFFF43F5E)
    override val gridIconRoseEnd: Color = Color(0xFFEC4899)
    override val gridIconRoseOuterStart: Color = Color(0xFFFFE4E6)
    override val gridIconRoseOuterEnd: Color = Color(0xFFFDF2F8)
    override val gridSubtitleRose: Color = Color(0xFFF43F5E)

    override val gridIconTealStart: Color = Color(0xFF10B981)
    override val gridIconTealEnd: Color = Color(0xFF14B8A6)
    override val gridIconTealOuterStart: Color = Color(0xFFD1FAE5)
    override val gridIconTealOuterEnd: Color = Color(0xFFF0FDFA)
    override val gridSubtitleTeal: Color = Color(0xFF0D9488)

    override val gridIconAmberStart: Color = Color(0xFFF59E0B)
    override val gridIconAmberEnd: Color = Color(0xFFFB923C)
    override val gridIconAmberOuterStart: Color = Color(0xFFFEF3C7)
    override val gridIconAmberOuterEnd: Color = Color(0xFFFFF7ED)
    override val gridSubtitleAmber: Color = Color(0xFFD97706)

    override val gridIconPinkStart: Color = Color(0xFFEC4899)
    override val gridIconPinkEnd: Color = Color(0xFFFB7185)
    override val gridIconPinkOuterStart: Color = Color(0xFFFCE7F3)
    override val gridIconPinkOuterEnd: Color = Color(0xFFFFF1F2)
    override val gridSubtitlePink: Color = Color(0xFFEC4899)

    override val gridIconBlueStart: Color = Color(0xFF6366F1)
    override val gridIconBlueEnd: Color = Color(0xFF3B82F6)
    override val gridIconBlueOuterStart: Color = Color(0xFFE0E7FF)
    override val gridIconBlueOuterEnd: Color = Color(0xFFEFF6FF)
    override val gridSubtitleBlue: Color = Color(0xFF6366F1)

    override val gridIconPurpleStart: Color = Color(0xFFA855F7)
    override val gridIconPurpleEnd: Color = Color(0xFF6366F1)
    override val gridIconPurpleOuterStart: Color = Color(0xFFF3E8FF)
    override val gridIconPurpleOuterEnd: Color = Color(0xFFF5F3FF)
    override val gridSubtitlePurple: Color = Color(0xFF9333EA)
}

val LightKanguruColors: KanguruColors = LightKanguruColorsImpl()

// 2. REDUP RUANG MENYUSUI (Dim Nursing Room - Dark)
// Warm espresso/charcoal tones designed for eye comfort during nighttime nursing/monitoring.
private class DarkKanguruColorsImpl : KanguruColors() {
    override val isDark: Boolean = true
    override val background: Color = Color(0xFF141312)
    override val surface: Color = Color(0xFF1E1D1A)
    override val surfaceElevated: Color = Color(0xFF262421)
    override val surfaceVariant: Color = Color(0xFF2B2824)
    override val cardBackground: Color = Color(0xFF1E1D1A)
    override val cardBorder: Color = Color(0xFF33302B)
    override val divider: Color = Color(0xFF2B2824)
    override val outline: Color = Color(0xFF3E3A34)
    override val outlineVariant: Color = Color(0xFF2E2B27)

    override val primary: Color = Color(0xFFFF5C7A)
    override val onPrimary: Color = Color(0xFFFFFFFF)
    override val primaryContainer: Color = Color(0xFF3E1A22)
    override val onPrimaryContainer: Color = Color(0xFFFFB3C1)

    override val secondary: Color = Color(0xFF38231B)
    override val onSecondary: Color = Color(0xFFF5E6DC)
    override val secondaryContainer: Color = Color(0xFF38231B)
    override val onSecondaryContainer: Color = Color(0xFFFFD7C2)

    override val textPrimary: Color = Color(0xFFF5F3EF)
    override val textSecondary: Color = Color(0xFFA8A399)
    override val textTertiary: Color = Color(0xFF757065)
    override val textOnDark: Color = Color(0xFFF5F3EF)

    override val success: Color = Color(0xFF4ADE80)
    override val successContainer: Color = Color(0xFF152A1E)
    override val onSuccessContainer: Color = Color(0xFF86EFAC)
    override val successBorder: Color = Color(0xFF234C34)

    override val warning: Color = Color(0xFFFBBF24)
    override val warningContainer: Color = Color(0xFF33240F)
    override val onWarningContainer: Color = Color(0xFFFDE68A)
    override val warningBorder: Color = Color(0xFF593E1A)

    override val error: Color = Color(0xFFF87171)
    override val errorContainer: Color = Color(0xFF381717)
    override val onErrorContainer: Color = Color(0xFFFECACA)
    override val errorBorder: Color = Color(0xFF5E2727)

    override val info: Color = Color(0xFF60A5FA)
    override val infoContainer: Color = Color(0xFF152538)
    override val onInfoContainer: Color = Color(0xFFBFDBFE)
    override val infoBorder: Color = Color(0xFF233E5E)

    override val pinkGradientStart: Color = Color(0xFFFF5C77)
    override val pinkGradientMid: Color = Color(0xFFFF6D88)
    override val pinkGradientEnd: Color = Color(0xFFFF7B96)
    override val timerTrack: Color = Color(0xFF3B1D24)
    override val timerAccent: Color = Color(0xFFFF8DA3)

    override val fentonP97: Color = Color(0xFFF87171)
    override val fentonP90: Color = Color(0xFFFB923C)
    override val fentonP50: Color = Color(0xFF34D399)
    override val fentonP10: Color = Color(0xFFFCD34D)
    override val fentonP3: Color = Color(0xFFFDA4AF)
    override val growthCardRed: Color = Color(0xFF331619)
    override val growthCardGreen: Color = Color(0xFF152A1E)
    override val growthCardBlue: Color = Color(0xFF152538)
    override val tipBackground: Color = Color(0xFF2D2314)
    override val tipBorder: Color = Color(0xFF4E3D20)
    override val tipText: Color = Color(0xFFFDE68A)

    override val inputBackground: Color = Color(0xFF2B2824)
    override val inputFocusedBackground: Color = Color(0xFF1E1D1A)
    override val inputBorder: Color = Color(0xFF3E3A34)
    override val dragHandle: Color = Color(0xFF524E48)
    override val closeButtonBackground: Color = Color(0xFF2B2824)
    override val closeButtonTint: Color = Color(0xFFA8A399)
    override val buttonSecondaryBorder: Color = Color(0xFF3E3A34)
    override val buttonSecondaryText: Color = Color(0xFFF5F3EF)

    override val actionPillPrimaryBackground: Color = Color(0xFF3E1A22)
    override val actionPillPrimaryText: Color = Color(0xFFFFB3C1)
    override val actionPillSecondaryBackground: Color = Color(0xFF2B2824)
    override val actionPillSecondaryText: Color = Color(0xFFF5F3EF)

    override val genderMaleActiveBackground: Color = Color(0xFF152538)
    override val genderMaleActiveBorder: Color = Color(0xFF60A5FA)
    override val genderMaleActiveText: Color = Color(0xFF93C5FD)
    override val genderMaleActiveIconContainer: Color = Color(0xFF1E3A5F)
    override val genderMaleInactiveIconContainer: Color = Color(0xFF1B2838)
    override val genderMaleIcon: Color = Color(0xFF60A5FA)
    override val genderMaleInactiveIcon: Color = Color(0xFF60A5FA)

    override val genderFemaleActiveBackground: Color = Color(0xFF3E1A22)
    override val genderFemaleActiveBorder: Color = Color(0xFFFF5C7A)
    override val genderFemaleActiveText: Color = Color(0xFFFF8DA3)
    override val genderFemaleActiveIconContainer: Color = Color(0xFF5A202D)
    override val genderFemaleInactiveIconContainer: Color = Color(0xFF3A1820)
    override val genderFemaleIcon: Color = Color(0xFFFF8DA3)
    override val genderFemaleInactiveIcon: Color = Color(0xFFFF5C7A)

    override val avatarRingStart: Color = Color(0xFFFF5C77)
    override val avatarRingEnd: Color = Color(0xFFFDA4AF)
    override val avatarInnerBackground: Color = Color(0xFF3E1A22)

    override val caregiverIbu: Color = Color(0xFFFF5C7A)
    override val caregiverIbuContainer: Color = Color(0xFF3E1A22)
    override val caregiverIbuText: Color = Color(0xFFFFB3C1)
    override val caregiverAyah: Color = Color(0xFF60A5FA)
    override val caregiverAyahContainer: Color = Color(0xFF152538)
    override val caregiverAyahText: Color = Color(0xFFBFDBFE)
    override val caregiverPendamping: Color = Color(0xFFFBBF24)
    override val caregiverPendampingContainer: Color = Color(0xFF33240F)
    override val caregiverPendampingText: Color = Color(0xFFFDE68A)
    override val caregiverPause: Color = Color(0xFF64748B)
    override val caregiverPauseContainer: Color = Color(0xFF242220)
    override val caregiverPauseText: Color = Color(0xFFCBD5E1)
    override val timelineTrack: Color = Color(0xFF2B2824)

    override val tempColdTrack: Color = Color(0xFF0369A1)
    override val tempColdText: Color = Color(0xFF38BDF8)
    override val tempNormalTrack: Color = Color(0xFF059669)
    override val tempNormalText: Color = Color(0xFF34D399)
    override val tempWarmTrack: Color = Color(0xFFE11D48)
    override val tempWarmText: Color = Color(0xFFFDA4AF)
    override val tempNormalBadgeBg: Color = Color(0xFF064E3B)
    override val tempSliderTrack: Color = Color(0xFF334155)
    override val tempStepperBg: Color = Color(0xFF262421)
    override val tempStepperBorder: Color = Color(0xFF33302B)
    override val tempStepperIcon: Color = Color(0xFFF1F5F9)
    override val tempCancelBg: Color = Color(0xFF2B2824)
    override val tempCancelText: Color = Color(0xFF94A3B8)

    override val toastBackground: Color = Color(0xFF262421)
    override val toastText: Color = Color(0xFFFFFFFF)
    override val toastSuccessIconBg: Color = Color(0xFF10B981)
    override val toastSuccessIconTint: Color = Color(0xFFFFFFFF)
    override val toastErrorIconBg: Color = Color(0xFFEF4444)
    override val toastErrorIconTint: Color = Color(0xFFFFFFFF)

    override val categoryPurple: Color = Color(0xFFC084FC)
    override val categoryPurpleContainer: Color = Color(0xFF2E1945)
    override val categoryPurpleBorder: Color = Color(0xFF4C2870)
    override val categoryPurpleText: Color = Color(0xFFE9D5FF)

    override val categoryTeal: Color = Color(0xFF2DD4BF)
    override val categoryTealContainer: Color = Color(0xFF132B27)
    override val categoryTealBorder: Color = Color(0xFF1E4B43)
    override val categoryTealText: Color = Color(0xFF99F6E4)

    override val categoryOrange: Color = Color(0xFFFB923C)
    override val categoryOrangeContainer: Color = Color(0xFF331F14)
    override val categoryOrangeBorder: Color = Color(0xFF57331E)
    override val categoryOrangeText: Color = Color(0xFFFFD7C2)

    override val categoryRose: Color = Color(0xFFFB7185)
    override val categoryRoseContainer: Color = Color(0xFF3E1A22)
    override val categoryRoseBorder: Color = Color(0xFF5E2734)
    override val categoryRoseText: Color = Color(0xFFFFB3C1)

    override val emergencyBannerBackground: Color = Color(0xFF381717)
    override val emergencyBannerBorder: Color = Color(0xFF5E2727)
    override val emergencyBannerTitle: Color = Color(0xFFF87171)
    override val emergencyBannerSubtitle: Color = Color(0xFFFECACA)
    override val emergencyAction: Color = Color(0xFFDC2626)

    override val disclaimerBackground: Color = Color(0xFF2C1619)
    override val disclaimerBorder: Color = Color(0xFF4C2229)
    override val disclaimerTitle: Color = Color(0xFFFFD1DC)
    override val disclaimerText: Color = Color(0xFFFFB3C1)

    override val growthLength: Color = Color(0xFF4ADE80)
    override val growthLengthBorder: Color = Color(0xFF234C34)
    override val growthHead: Color = Color(0xFF60A5FA)
    override val growthHeadBorder: Color = Color(0xFF233E5E)

    override val ambientRosePulse: Color = Color(0xFF3E1A22)
    override val ambientAmberPulse: Color = Color(0xFF33240F)
    override val ambientWave1: Color = Color(0xFF25221F)
    override val ambientWave2: Color = Color(0xFF2D2824)
    override val onboardingCardBgStart: Color = Color(0xFF3B252C)
    override val onboardingCardBgEnd: Color = Color(0xFF2C2520)

    // Home Kasidig Grid & Hero Banner Tokens (Dark / Dim Nursing Mode)
    override val heroBannerBgStart: Color = Color(0xFF16382E)
    override val heroBannerBgMid: Color = Color(0xFF1A4538)
    override val heroBannerBgEnd: Color = Color(0xFF13362E)
    override val heroBannerBorder: Color = Color(0xFF245749)
    override val heroBadgeBg: Color = Color(0xFF1E4238)
    override val heroBadgeText: Color = Color(0xFF5EEAD4)

    override val gridIconRoseStart: Color = Color(0xFFE11D48)
    override val gridIconRoseEnd: Color = Color(0xFFBE123C)
    override val gridIconRoseOuterStart: Color = Color(0xFF3B151F)
    override val gridIconRoseOuterEnd: Color = Color(0xFF2E1218)
    override val gridSubtitleRose: Color = Color(0xFFFB7185)

    override val gridIconTealStart: Color = Color(0xFF059669)
    override val gridIconTealEnd: Color = Color(0xFF0D9488)
    override val gridIconTealOuterStart: Color = Color(0xFF142B24)
    override val gridIconTealOuterEnd: Color = Color(0xFF11241E)
    override val gridSubtitleTeal: Color = Color(0xFF2DD4BF)

    override val gridIconAmberStart: Color = Color(0xFFD97706)
    override val gridIconAmberEnd: Color = Color(0xFFEA580C)
    override val gridIconAmberOuterStart: Color = Color(0xFF382310)
    override val gridIconAmberOuterEnd: Color = Color(0xFF2E1C0D)
    override val gridSubtitleAmber: Color = Color(0xFFFBBF24)

    override val gridIconPinkStart: Color = Color(0xFFDB2777)
    override val gridIconPinkEnd: Color = Color(0xFFE11D48)
    override val gridIconPinkOuterStart: Color = Color(0xFF3B1426)
    override val gridIconPinkOuterEnd: Color = Color(0xFF2E101E)
    override val gridSubtitlePink: Color = Color(0xFFF472B6)

    override val gridIconBlueStart: Color = Color(0xFF4F46E5)
    override val gridIconBlueEnd: Color = Color(0xFF2563EB)
    override val gridIconBlueOuterStart: Color = Color(0xFF1A1C38)
    override val gridIconBlueOuterEnd: Color = Color(0xFF14162B)
    override val gridSubtitleBlue: Color = Color(0xFF818CF8)

    override val gridIconPurpleStart: Color = Color(0xFF9333EA)
    override val gridIconPurpleEnd: Color = Color(0xFF7C3AED)
    override val gridIconPurpleOuterStart: Color = Color(0xFF281438)
    override val gridIconPurpleOuterEnd: Color = Color(0xFF1F102B)
    override val gridSubtitlePurple: Color = Color(0xFFC084FC)
}

val DarkKanguruColors: KanguruColors = DarkKanguruColorsImpl()

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
    onBackground = TypographyDefaultColor,
    surface = Color(0xFFFFFFFF),
    onSurface = TypographyDefaultColor,
    surfaceVariant = Color(0xFFf8fafc),
    onSurfaceVariant = Color(0xFF6B7280),
    surfaceContainer = Color(0xFFFFFFFF),
    surfaceContainerHigh = Color(0xFFFDFCFA),
    surfaceContainerHighest = Color(0xFFF1ECE6),
    inverseSurface = Color(0xFF1E293B),
    inverseOnSurface = Color(0xFFFFFFFF),
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
    inverseSurface = Color(0xFF262421),
    inverseOnSurface = Color(0xFFFFFFFF),
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

    val typography: androidx.compose.material3.Typography
        @Composable
        @ReadOnlyComposable
        get() = androidx.compose.material3.MaterialTheme.typography

    val timerDisplay: androidx.compose.ui.text.TextStyle
        get() = TimerDisplay

    val isDark: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalKanguruColors.current.isDark

    val textScaleFactor: Float
        @Composable
        @ReadOnlyComposable
        get() = LocalTextScaleFactor.current
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

    val baseDensity = LocalDensity.current
    val scaledDensity = Density(
        density = baseDensity.density,
        fontScale = baseDensity.fontScale * textScale.scaleFactor
    )

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
        LocalDensity provides scaledDensity,
        LocalDimensions provides dimensions,
        LocalTextScaleFactor provides textScale.scaleFactor,
        LocalKanguruColors provides kanguruColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = KanguruShapes,
            content = content
        )
    }
}
