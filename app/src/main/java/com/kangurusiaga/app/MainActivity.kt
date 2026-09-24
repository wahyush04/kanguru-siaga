package com.kangurusiaga.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kangurusiaga.app.core.designsystem.theme.KanguruSiagaTheme
import com.kangurusiaga.app.presentation.navigation.KanguruNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            KanguruSiagaTheme {
                KanguruNavGraph()
            }
        }
    }
}
