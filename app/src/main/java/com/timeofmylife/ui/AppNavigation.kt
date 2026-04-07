package com.timeofmylife.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.ui.balances.BalancesScreen
import com.timeofmylife.ui.budget.BudgetScreen
import com.timeofmylife.ui.lifetime.LifetimeScreen
import com.timeofmylife.ui.settings.SettingsScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Balances : Screen("balances", "Balances", Icons.Default.AccountBalance)
    object Budget : Screen("budget", "Budget", Icons.Default.AttachMoney)
    object Lifetime : Screen("lifetime", "Life Time", Icons.Default.Timeline)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

private val screens = listOf(Screen.Balances, Screen.Budget, Screen.Lifetime, Screen.Settings)

@Composable
fun AppNavigation(repository: FinanceRepository) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val currentDestination = navBackStackEntry?.destination
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.Balances.route) {
            composable(Screen.Balances.route) { BalancesScreen(repository, innerPadding) }
            composable(Screen.Budget.route) { BudgetScreen(repository, innerPadding) }
            composable(Screen.Lifetime.route) { LifetimeScreen(repository, innerPadding) }
            composable(Screen.Settings.route) { SettingsScreen(repository, innerPadding) }
        }
    }
}
