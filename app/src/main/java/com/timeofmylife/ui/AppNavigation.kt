package com.timeofmylife.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.ui.balances.BalancesScreen
import com.timeofmylife.ui.budget.BudgetScreen
import com.timeofmylife.ui.lifetime.LifetimeScreen
import com.timeofmylife.ui.settings.SettingsScreen
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Balances : Screen("balances", "Balances", Icons.Default.AccountBalance)

    object Budget : Screen("budget", "Budget", Icons.Default.AttachMoney)

    object Lifetime : Screen("lifetime", "Life Time", Icons.Default.Timeline)

    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

private val screens = listOf(Screen.Balances, Screen.Budget, Screen.Lifetime, Screen.Settings)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppNavigation(
    repository: FinanceRepository,
    onShowWelcome: () -> Unit,
    onShowHelp: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { screens.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            NavigationBar {
                screens.forEachIndexed { index, screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = pagerState.currentPage == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    )
                }
            }
        },
    ) { innerPadding ->
        val pagePadding = PaddingValues(0.dp)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(innerPadding),
            beyondViewportPageCount = 1,
            key = { screens[it].route },
        ) { page ->
            when (page) {
                0 -> BalancesScreen(repository, pagePadding)
                1 -> BudgetScreen(repository, pagePadding)
                2 -> LifetimeScreen(repository, pagePadding)
                3 -> SettingsScreen(repository, pagePadding, onShowWelcome, onShowHelp)
            }
        }
    }
}
