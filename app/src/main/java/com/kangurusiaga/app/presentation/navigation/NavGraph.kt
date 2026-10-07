package com.kangurusiaga.app.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kangurusiaga.app.presentation.babyprofile.BabyProfileSetupRoute
import com.kangurusiaga.app.presentation.education.EducationCenterScreen
import com.kangurusiaga.app.presentation.education.EducationDetailRoute
import com.kangurusiaga.app.presentation.education.EducationListRoute
import com.kangurusiaga.app.presentation.emergency.EmergencyWarningDetailRoute
import com.kangurusiaga.app.presentation.emergency.EmergencyWarningListRoute
import com.kangurusiaga.app.presentation.feeding.AlarmRoute
import com.kangurusiaga.app.presentation.home.HomeRoute
import com.kangurusiaga.app.presentation.onboarding.OnboardingRoute
import com.kangurusiaga.app.presentation.onboarding.SplashRoute
import com.kangurusiaga.app.presentation.pmk.center.PmkCenterScreen
import com.kangurusiaga.app.presentation.pmk.history.PmkHistoryRoute
import com.kangurusiaga.app.presentation.pmk.manual.PmkManualLogRoute
import com.kangurusiaga.app.presentation.pmk.reminders.PmkRemindersRoute
import com.kangurusiaga.app.presentation.pmk.statistics.PmkStatisticsRoute
import com.kangurusiaga.app.presentation.pmk.timer.PmkTimerRoute
import com.kangurusiaga.app.presentation.pmk.video.PmkVideoListScreen
import com.kangurusiaga.app.presentation.pmk.video.PmkVideoScreen
import com.kangurusiaga.app.domain.model.GrowthParameter
import com.kangurusiaga.app.presentation.growth.about.AboutFentonScreen
import com.kangurusiaga.app.presentation.growth.add.AddGrowthMeasurementScreen
import com.kangurusiaga.app.presentation.growth.chart.GrowthChartScreen
import com.kangurusiaga.app.presentation.growth.hub.GrowthHubScreen
import com.kangurusiaga.app.presentation.growth.summary.GrowthSummaryScreen
import com.kangurusiaga.app.presentation.profile.BabyProfileRoute
import com.kangurusiaga.app.presentation.settings.SettingsRoute
import com.kangurusiaga.app.presentation.settings.info.AboutAppScreen
import com.kangurusiaga.app.presentation.settings.info.ClinicalGuidelinesScreen
import com.kangurusiaga.app.presentation.settings.info.MedicalDisclaimerScreen
import com.kangurusiaga.app.presentation.settings.info.UserGuideScreen

@Composable
fun KanguruNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = Screen.Splash.route) {
            SplashRoute(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route)
                }
            )
        }

        composable(route = Screen.Onboarding.route) {
            OnboardingRoute(
                onNavigateToSetup = {
                    navController.navigate(Screen.ProfileSetup.route)
                }
            )
        }

        composable(route = Screen.ProfileSetup.route) {
            BabyProfileSetupRoute(
                onNavigateBackToIntro = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Home.route) {
            HomeRoute(
                onNavigateToPmk = {
                    navController.navigate(Screen.PmkTimer.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToProfileSetup = {
                    navController.navigate(Screen.ProfileSetup.route)
                },
                onNavigateToEducation = {
                    navController.navigate(Screen.EducationCenter.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToEmergency = {
                    navController.navigate(Screen.EmergencyWarningList.route)
                },
                onNavigateToAlarm = {
                    navController.navigate(Screen.Feeding.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToGrowth = {
                    navController.navigate(Screen.GrowthHub.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.BabyProfile.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        // PMK Center
        composable(route = Screen.PmkCenter.route) {
            PmkCenterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToVideo = {
                    navController.navigate(Screen.PmkVideoList.route)
                },
                onNavigateToTimer = {
                    navController.navigate(Screen.PmkTimer.route)
                },
                onNavigateToReminders = {
                    navController.navigate(Screen.PmkReminders.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.PmkHistory.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToEducation = {
                    navController.navigate(Screen.EducationCenter.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToAlarm = {
                    navController.navigate(Screen.Feeding.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.BabyProfile.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        // PMK Legacy Alias -> routes directly to Continuous PMK Timer
        composable(route = Screen.Pmk.route) {
            PmkTimerRoute(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.PmkHistory.route)
                },
                onNavigateToGuide = {
                    navController.navigate(Screen.PmkGuide.route)
                }
            )
        }

        // PMK Timer
        composable(route = Screen.PmkTimer.route) {
            PmkTimerRoute(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.PmkHistory.route)
                },
                onNavigateToGuide = {
                    navController.navigate(Screen.PmkGuide.route)
                }
            )
        }

        // Screen 5: Panduan PMK Kontinu
        composable(route = Screen.PmkGuide.route) {
            com.kangurusiaga.app.presentation.pmk.guide.PmkGuideScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToTimer = {
                    navController.navigate(Screen.PmkTimer.route) {
                        popUpTo(Screen.PmkGuide.route) { inclusive = true }
                    }
                }
            )
        }

        // PMK Manual Logging
        composable(route = Screen.PmkManualLog.route) {
            PmkManualLogRoute(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Screen 1: Kanguru Siaga - Riwayat PMK (Halaman Utama Riwayat PMK)
        composable(route = Screen.PmkHistory.route) {
            PmkHistoryRoute(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDetail = {
                    navController.navigate(Screen.PmkStatisticsDetail.route)
                },
                onNavigateToManualLog = {
                    navController.navigate(Screen.PmkManualLog.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToEducation = {
                    navController.navigate(Screen.EducationCenter.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Screen 2: Kanguru Siaga - Detail Statistik PMK (Halaman Detail Lanjutan Statistik)
        composable(route = Screen.PmkStatisticsDetail.route) {
            PmkStatisticsRoute(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToManualLog = {
                    navController.navigate(Screen.PmkManualLog.route)
                }
            )
        }

        // PMK Statistics Legacy Alias -> navigates to PmkHistory
        composable(route = Screen.PmkStatistics.route) {
            PmkHistoryRoute(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDetail = {
                    navController.navigate(Screen.PmkStatisticsDetail.route)
                },
                onNavigateToManualLog = {
                    navController.navigate(Screen.PmkManualLog.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToEducation = {
                    navController.navigate(Screen.EducationCenter.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        // PMK Reminders
        composable(route = Screen.PmkReminders.route) {
            PmkRemindersRoute(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // PMK Video Educational Module List (Stitch: Kanguru Siaga - Video Edukasi PMK)
        composable(
            route = Screen.PmkVideoList.route,
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300)
                )
            }
        ) {
            PmkVideoListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDetail = { videoId ->
                    navController.navigate(Screen.PmkVideoDetail.createRoute(videoId))
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToEducation = {
                    navController.navigate(Screen.EducationCenter.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        // PMK Video Player Detail (Stitch: Kanguru Siaga - Detail Pemutar Video PMK)
        composable(
            route = Screen.PmkVideoDetail.route,
            arguments = listOf(
                navArgument("videoId") {
                    type = NavType.IntType
                    defaultValue = 4
                }
            ),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300)
                )
            }
        ) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getInt("videoId") ?: 1
            PmkVideoScreen(
                videoId = videoId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToTimer = {
                    navController.navigate(Screen.PmkTimer.route)
                },
                onNavigateToNextVideo = { nextVideoId ->
                    navController.navigate(Screen.PmkVideoDetail.createRoute(nextVideoId)) {
                        popUpTo(Screen.PmkVideoDetail.route) { inclusive = true }
                    }
                }
            )
        }

        // PMK Video Legacy Alias -> navigates to Video List
        composable(
            route = Screen.PmkVideo.route,
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300)
                )
            }
        ) {
            PmkVideoListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDetail = { videoId ->
                    navController.navigate(Screen.PmkVideoDetail.createRoute(videoId))
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToEducation = {
                    navController.navigate(Screen.EducationCenter.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Screen: Kanguru Siaga - Edukasi (Pusat Edukasi & Panduan)
        composable(route = Screen.EducationCenter.route) {
            EducationCenterScreen(
                onNavigateToPmk = {
                    navController.navigate(Screen.PmkVideoList.route)
                },
                onNavigateToBblrEducation = {
                    navController.navigate(Screen.EducationList.route)
                },
                onNavigateToEmergencyWarning = {
                    navController.navigate(Screen.EmergencyWarningList.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToAlarm = {
                    navController.navigate(Screen.Feeding.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.BabyProfile.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Perawatan Bayi BBLR - Education List (Stitch: Kanguru Siaga - Perawatan Bayi BBLR)
        composable(route = Screen.EducationList.route) {
            EducationListRoute(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDetail = { moduleId ->
                    navController.navigate(Screen.EducationDetail.createRoute(moduleId))
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToPmk = {
                    navController.navigate(Screen.PmkTimer.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToAlarm = {
                    navController.navigate(Screen.Feeding.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToEducation = {
                    navController.popBackStack()
                }
            )
        }

        // Perawatan Bayi BBLR - Detail Module (Single Reusable Screen)
        composable(
            route = Screen.EducationDetail.route,
            arguments = listOf(
                navArgument("moduleId") {
                    type = NavType.StringType
                    defaultValue = "bblr_01"
                }
            )
        ) {
            EducationDetailRoute(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToNextModule = { nextModuleId ->
                    navController.navigate(Screen.EducationDetail.createRoute(nextModuleId)) {
                        popUpTo(Screen.EducationDetail.route) { inclusive = true }
                    }
                }
            )
        }

        // Education Legacy Alias -> navigates to EducationCenter
        composable(route = Screen.Education.route) {
            EducationCenterScreen(
                onNavigateToPmk = {
                    navController.navigate(Screen.PmkVideoList.route)
                },
                onNavigateToBblrEducation = {
                    navController.navigate(Screen.EducationList.route)
                },
                onNavigateToEmergencyWarning = {
                    navController.navigate(Screen.EmergencyWarningList.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToAlarm = {
                    navController.navigate(Screen.Feeding.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.BabyProfile.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Tanda Kegawatan pada BBLR - List Screen
        composable(route = Screen.EmergencyWarningList.route) {
            EmergencyWarningListRoute(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDetail = { moduleId ->
                    navController.navigate(Screen.EmergencyWarningDetail.createRoute(moduleId))
                }
            )
        }

        // Tanda Kegawatan pada BBLR - Detail Screen (Single Reusable Screen)
        composable(
            route = Screen.EmergencyWarningDetail.route,
            arguments = listOf(
                navArgument("moduleId") {
                    type = NavType.StringType
                    defaultValue = "emergency_01"
                }
            )
        ) {
            EmergencyWarningDetailRoute(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Alarm Pemberian ASI (OGT/NGT)
        composable(route = Screen.Feeding.route) {
            AlarmRoute(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToPmk = {
                    navController.navigate(Screen.PmkTimer.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToEducation = {
                    navController.navigate(Screen.EducationCenter.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.BabyProfile.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Alarm Legacy / Friendly Alias
        composable(route = "alarm") {
            AlarmRoute(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToPmk = {
                    navController.navigate(Screen.PmkTimer.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToEducation = {
                    navController.navigate(Screen.EducationCenter.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.BabyProfile.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Growth Module Screens
        composable(route = Screen.GrowthHub.route) {
            GrowthHubScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToChart = { param ->
                    navController.navigate(Screen.GrowthChart.createRoute(param, tab = 0))
                },
                onNavigateToAdd = { param ->
                    navController.navigate(Screen.GrowthAdd.createRoute(param))
                },
                onNavigateToSummary = {
                    navController.navigate(Screen.GrowthSummary.route)
                },
                onNavigateToAboutFenton = {
                    navController.navigate(Screen.AboutFenton.route)
                }
            )
        }

        composable(route = Screen.Growth.route) {
            GrowthHubScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToChart = { param ->
                    navController.navigate(Screen.GrowthChart.createRoute(param, tab = 0))
                },
                onNavigateToAdd = { param ->
                    navController.navigate(Screen.GrowthAdd.createRoute(param))
                },
                onNavigateToSummary = {
                    navController.navigate(Screen.GrowthSummary.route)
                },
                onNavigateToAboutFenton = {
                    navController.navigate(Screen.AboutFenton.route)
                }
            )
        }

        composable(
            route = Screen.GrowthChart.route,
            arguments = listOf(
                navArgument("parameter") { type = NavType.StringType },
                navArgument("tab") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) {
            GrowthChartScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAdd = { param ->
                    navController.navigate(Screen.GrowthAdd.createRoute(param))
                },
                onNavigateToAboutFenton = {
                    navController.navigate(Screen.AboutFenton.route)
                }
            )
        }

        composable(
            route = Screen.GrowthAdd.route,
            arguments = listOf(
                navArgument("parameter") { type = NavType.StringType }
            )
        ) {
            AddGrowthMeasurementScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSavedSuccessfully = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.GrowthSummary.route) {
            GrowthSummaryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.AboutFenton.route) {
            AboutFentonScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Baby Profile & Settings Module
        composable(route = Screen.BabyProfile.route) {
            BabyProfileRoute(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToPmk = {
                    navController.navigate(Screen.PmkTimer.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToEducation = {
                    navController.navigate(Screen.EducationCenter.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToAlarm = {
                    navController.navigate(Screen.Feeding.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(route = Screen.Settings.route) {
            SettingsRoute(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToPmkReminders = {
                    navController.navigate(Screen.PmkReminders.route)
                },
                onNavigateToFeedingAlarm = {
                    navController.navigate(Screen.Feeding.route)
                },
                onNavigateToUserGuide = {
                    navController.navigate(Screen.UserGuide.route)
                },
                onNavigateToAboutApp = {
                    navController.navigate(Screen.AboutApp.route)
                },
                onNavigateToClinicalGuidelines = {
                    navController.navigate(Screen.ClinicalGuidelines.route)
                },
                onNavigateToMedicalDisclaimer = {
                    navController.navigate(Screen.MedicalDisclaimer.route)
                }
            )
        }

        composable(route = Screen.UserGuide.route) {
            UserGuideScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.MedicalDisclaimer.route) {
            MedicalDisclaimerScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.AboutApp.route) {
            AboutAppScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.ClinicalGuidelines.route) {
            ClinicalGuidelinesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
