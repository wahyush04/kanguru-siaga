package com.kangurusiaga.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.core.util.Consumer
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kangurusiaga.app.core.designsystem.theme.KanguruSiagaTheme
import com.kangurusiaga.app.presentation.navigation.KanguruNavGraph
import com.kangurusiaga.app.presentation.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Prevent launcher from creating duplicate instance or resetting task if already in background
        if (!isTaskRoot && intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intent.action == Intent.ACTION_MAIN) {
            finish()
            return
        }

        setContent {
            KanguruSiagaTheme {
                val navController = rememberNavController()

                DisposableEffect(Unit) {
                    val listener = Consumer<Intent> { newIntent ->
                        handleNotificationIntent(newIntent, navController)
                    }
                    addOnNewIntentListener(listener)
                    onDispose {
                        removeOnNewIntentListener(listener)
                    }
                }

                LaunchedEffect(Unit) {
                    handleNotificationIntent(intent, navController)
                }

                KanguruNavGraph(navController = navController)
            }
        }
    }

    private fun handleNotificationIntent(intent: Intent?, navController: NavHostController) {
        val navigateTo = intent?.getStringExtra("navigate_to") ?: return
        when (navigateTo) {
            "pmk_timer" -> {
                navController.navigate(Screen.PmkTimer.route) {
                    launchSingleTop = true
                }
            }
            "feeding" -> {
                navController.navigate(Screen.Feeding.route) {
                    launchSingleTop = true
                }
            }
        }
    }
}
