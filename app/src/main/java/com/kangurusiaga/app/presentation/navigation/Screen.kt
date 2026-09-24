package com.kangurusiaga.app.presentation.navigation

import com.kangurusiaga.app.core.navigation.NavigationDestination

sealed class Screen(override val route: String, override val destination: String) : NavigationDestination {
    data object Splash : Screen(route = "splash", destination = "splash_destination")
    data object Onboarding : Screen(route = "onboarding", destination = "onboarding_destination")
    data object ProfileIntro : Screen(route = "profile_intro", destination = "profile_intro_destination")
    data object ProfileSetup : Screen(route = "profile_setup", destination = "profile_setup_destination")
    data object Home : Screen(route = "home", destination = "home_destination")
    data object BabyProfile : Screen(route = "baby_profile", destination = "baby_profile_destination")
    data object Pmk : Screen(route = "pmk", destination = "pmk_destination")
    data object Education : Screen(route = "education", destination = "education_destination")
    data object Emergency : Screen(route = "emergency", destination = "emergency_destination")
    data object Feeding : Screen(route = "feeding", destination = "feeding_destination")
    data object Growth : Screen(route = "growth", destination = "growth_destination")
}
