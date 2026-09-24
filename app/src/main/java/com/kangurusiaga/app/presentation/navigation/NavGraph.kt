package com.kangurusiaga.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kangurusiaga.app.presentation.babyprofile.BabyProfileSetupRoute
import com.kangurusiaga.app.presentation.home.HomeRoute
import com.kangurusiaga.app.presentation.onboarding.OnboardingRoute
import com.kangurusiaga.app.presentation.onboarding.ProfileIntroRoute
import com.kangurusiaga.app.presentation.onboarding.SplashRoute

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
                onNavigateToProfileIntro = {
                    navController.navigate(Screen.ProfileIntro.route)
                }
            )
        }

        composable(route = Screen.ProfileIntro.route) {
            ProfileIntroRoute(
                onNavigateBack = {
                    navController.popBackStack()
                },
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
            HomeRoute()
        }
    }
}
