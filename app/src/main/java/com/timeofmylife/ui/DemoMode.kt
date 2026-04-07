package com.timeofmylife.ui

import android.content.Context
import androidx.compose.runtime.compositionLocalOf

val LocalDemoMode = compositionLocalOf { false }
val LocalSetDemoMode = compositionLocalOf<(Boolean) -> Unit> { {} }

private const val PREFS = "settings"
private const val KEY = "demo_mode"

fun isDemoMode(context: Context): Boolean =
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY, false)

fun persistDemoMode(context: Context, enabled: Boolean) {
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY, enabled).apply()
}

fun formatAmount(amount: Double, demo: Boolean): String =
    if (demo) "$***"
    else if (amount >= 0) "\$${amount.toLong()}"
    else "-\$${(-amount).toLong()}"
