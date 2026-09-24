package com.kangurusiaga.app.presentation.navigation

import com.kangurusiaga.app.core.navigation.NavigationDestination

sealed class Screen(override val route: String, override val destination: String) : NavigationDestination {
    data object Splash : Screen(route = "splash", destination = "splash_destination")
    data object Onboarding : Screen(route = "onboarding", destination = "onboarding_destination")
    data object ProfileIntro : Screen(route = "profile_intro", destination = "profile_intro_destination")
    data object ProfileSetup : Screen(route = "profile_setup", destination = "profile_setup_destination")
    data object Home : Screen(route = "home", destination = "home_destination")
    data object BabyProfile : Screen(route = "baby_profile", destination = "baby_profile_destination")

    // PMK Module Screens
    data object Pmk : Screen(route = "pmk", destination = "pmk_destination") // legacy alias pointing to PmkCenter
    data object PmkCenter : Screen(route = "pmk_center", destination = "pmk_center_destination")
    data object PmkTimer : Screen(route = "pmk_timer", destination = "pmk_timer_destination")
    data object PmkManualLog : Screen(route = "pmk_manual_log", destination = "pmk_manual_log_destination")
    data object PmkStatistics : Screen(route = "pmk_statistics", destination = "pmk_statistics_destination")
    data object PmkReminders : Screen(route = "pmk_reminders", destination = "pmk_reminders_destination")
    data object PmkVideoList : Screen(route = "pmk_video_list", destination = "pmk_video_list_destination")
    data object PmkVideoDetail : Screen(route = "pmk_video_detail/{videoId}", destination = "pmk_video_detail_destination") {
        fun createRoute(videoId: Int): String = "pmk_video_detail/$videoId"
    }
    data object PmkVideo : Screen(route = "pmk_video", destination = "pmk_video_destination") // legacy alias pointing to video list

    // Other non-scoped screens
    data object Education : Screen(route = "education", destination = "education_destination")
    data object Emergency : Screen(route = "emergency", destination = "emergency_destination")
    data object Feeding : Screen(route = "feeding", destination = "feeding_destination")
    data object Growth : Screen(route = "growth", destination = "growth_destination")
}
