package com.timeofmylife.ui

import android.content.Context
import androidx.compose.runtime.compositionLocalOf

val LocalBirthYear = compositionLocalOf { 0 }
val LocalSetBirthYear = compositionLocalOf<(Int) -> Unit> { {} }
val LocalLifeExpectancy = compositionLocalOf { 0 }
val LocalSetLifeExpectancy = compositionLocalOf<(Int) -> Unit> { {} }

private const val PREFS = "settings"
private const val KEY_BIRTH_YEAR = "birth_year"
private const val KEY_LIFE_EXPECTANCY = "life_expectancy"

fun getBirthYear(context: Context): Int =
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY_BIRTH_YEAR, 0)

fun persistBirthYear(context: Context, year: Int) {
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putInt(KEY_BIRTH_YEAR, year).apply()
}

fun getLifeExpectancy(context: Context): Int =
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY_LIFE_EXPECTANCY, 0)

fun persistLifeExpectancy(context: Context, years: Int) {
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putInt(KEY_LIFE_EXPECTANCY, years).apply()
}
