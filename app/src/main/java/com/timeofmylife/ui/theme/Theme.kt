package com.timeofmylife.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DarkColorScheme =
    darkColorScheme(
        primary = Accent,
        onPrimary = OnAccent,
        primaryContainer = AccentContainer,
        onPrimaryContainer = OnAccent,
        background = Color(0xFF121212),
        surface = Color(0xFF181818),
        onBackground = Color(0xFFE0E0E0),
        onSurface = Color(0xFFE0E0E0),
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeOfMyLifeTheme(content: @Composable () -> Unit) {
    val shapes =
        Shapes(
            small = RoundedCornerShape(4.dp),
            medium = RoundedCornerShape(8.dp),
            large = RoundedCornerShape(12.dp),
        )
    MaterialTheme(
        colorScheme = DarkColorScheme,
        shapes = shapes,
    ) {
        CompositionLocalProvider(LocalRippleConfiguration provides null) {
            content()
        }
    }
}
