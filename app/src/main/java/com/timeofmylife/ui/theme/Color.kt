package com.timeofmylife.ui.theme

import androidx.compose.ui.graphics.Color

val Purple = Color(0xFF7C4DFF)
val Accent = Color(0xFF9E9E9E)
val AccentContainer = Color(0xFF424242)
val OnAccent = Color(0xFFFFFFFF)

val ExpenseRed = Color(0xFFEF5350)
val IncomeGreen = Color(0xFF66BB6A)
val BestBlue = Color(0xFF42A5F5)
val WorstOrange = Color(0xFFFF9800)
val LastGrey = Color(0xFF9E9E9E)
val NegativeText = Color(0xFFEF9A9A)

val HighColor = Color(0xFFA5D6A7)     // green text for HIGH
val MediumColor = Color(0xFFFFCC80)   // orange text for MEDIUM
val LowColor = Color(0xFFEF9A9A)      // red text for LOW

val UncoveredDark = Color(0xFF2A2A2A)

// Lifetime matrix row backgrounds (worst → best, 25% alpha on dark bg)
val LifetimeRowColors = listOf(
    Color(0x40FF0000),  // H/bad   — red
    Color(0x33FF6400),  // H/good  — orange
    Color(0x33FFC800),  // HM/bad  — yellow
    Color(0x3364C800),  // HM/good — green
    Color(0x330064FF),  // HML/bad — blue
    Color(0x338C00FF),  // HML/good— violet
)
