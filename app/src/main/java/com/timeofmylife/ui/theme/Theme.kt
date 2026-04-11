package com.timeofmylife.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
    darkColorScheme(
        primary = Accent,
        onPrimary = OnAccent,
        primaryContainer = AccentContainer,
        onPrimaryContainer = OnAccent,
        background = Color(0xFF121212),
        surface = Color(0xFF1E1E1E),
        onBackground = Color(0xFFE0E0E0),
        onSurface = Color(0xFFE0E0E0),
    )

@Composable
fun TimeOfMyLifeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content,
    )
}
