package com.timeofmylife

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.timeofmylife.ui.AppNavigation
import com.timeofmylife.ui.theme.TimeOfMyLifeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = (application as TimeOfMyLifeApp).repository
        setContent {
            TimeOfMyLifeTheme {
                AppNavigation(repository)
            }
        }
    }
}
