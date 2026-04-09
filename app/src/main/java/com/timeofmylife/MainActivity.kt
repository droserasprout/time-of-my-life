package com.timeofmylife

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
import com.timeofmylife.ui.AppNavigation
import com.timeofmylife.ui.LocalDemoMode
import com.timeofmylife.ui.LocalSetDemoMode
import com.timeofmylife.ui.isDemoMode
import com.timeofmylife.ui.persistDemoMode
import com.timeofmylife.ui.LocalBirthYear
import com.timeofmylife.ui.LocalSetBirthYear
import com.timeofmylife.ui.LocalLifeExpectancy
import com.timeofmylife.ui.LocalSetLifeExpectancy
import com.timeofmylife.ui.getBirthYear
import com.timeofmylife.ui.persistBirthYear
import com.timeofmylife.ui.getLifeExpectancy
import com.timeofmylife.ui.persistLifeExpectancy
import com.timeofmylife.ui.theme.TimeOfMyLifeTheme
import com.timeofmylife.ui.welcome.WelcomeScreen
import com.timeofmylife.ui.welcome.hasSeenWelcome

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = (application as TimeOfMyLifeApp).repository
        setContent {
            var demoMode by remember { mutableStateOf(isDemoMode(this@MainActivity)) }
            var birthYear by remember { mutableIntStateOf(getBirthYear(this@MainActivity)) }
            var lifeExpectancy by remember { mutableIntStateOf(getLifeExpectancy(this@MainActivity)) }
            val ctx = this@MainActivity
            TimeOfMyLifeTheme {
                CompositionLocalProvider(
                    LocalDemoMode provides demoMode,
                    LocalSetDemoMode provides { enabled ->
                        demoMode = enabled
                        persistDemoMode(ctx, enabled)
                    },
                    LocalBirthYear provides birthYear,
                    LocalSetBirthYear provides { year ->
                        birthYear = year
                        persistBirthYear(ctx, year)
                    },
                    LocalLifeExpectancy provides lifeExpectancy,
                    LocalSetLifeExpectancy provides { years ->
                        lifeExpectancy = years
                        persistLifeExpectancy(ctx, years)
                    }
                ) {
                    var showWelcome by remember { mutableStateOf(!hasSeenWelcome(ctx)) }
                    if (showWelcome) {
                        WelcomeScreen(onGetStarted = { showWelcome = false })
                    } else {
                        AppNavigation(repository)
                    }
                }
            }
        }
    }
}
