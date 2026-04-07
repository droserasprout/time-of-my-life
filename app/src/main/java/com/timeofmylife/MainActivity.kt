package com.timeofmylife

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.timeofmylife.ui.AppNavigation
import com.timeofmylife.ui.theme.TimeOfMyLifeTheme
import com.timeofmylife.ui.welcome.WelcomeScreen
import com.timeofmylife.ui.welcome.hasSeenWelcome

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = (application as TimeOfMyLifeApp).repository
        setContent {
            TimeOfMyLifeTheme {
                var showWelcome by remember { mutableStateOf(!hasSeenWelcome(this@MainActivity)) }
                if (showWelcome) {
                    WelcomeScreen(onGetStarted = { showWelcome = false })
                } else {
                    AppNavigation(repository)
                }
            }
        }
    }
}
