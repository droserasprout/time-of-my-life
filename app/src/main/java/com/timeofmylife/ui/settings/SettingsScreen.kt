package com.timeofmylife.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.ui.LocalDemoMode
import com.timeofmylife.ui.LocalSetDemoMode

@Composable
fun SettingsScreen(
    repository: FinanceRepository,
    innerPadding: PaddingValues,
    onShowWelcome: () -> Unit,
    onShowHelp: () -> Unit
) {
    var showImportExport by remember { mutableStateOf(false) }

    if (showImportExport) {
        BackHandler { showImportExport = false }
        ImportExportScreen(repository, innerPadding)
    } else {
        SettingsContent(innerPadding, onShowWelcome, onShowHelp, onShowImportExport = { showImportExport = true })
    }
}

@Composable
private fun SettingsContent(
    innerPadding: PaddingValues,
    onShowWelcome: () -> Unit,
    onShowHelp: () -> Unit,
    onShowImportExport: () -> Unit
) {
    val demoMode = LocalDemoMode.current
    val setDemoMode = LocalSetDemoMode.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = innerPadding.calculateTopPadding() + 16.dp,
                bottom = innerPadding.calculateBottomPadding(),
                start = 16.dp,
                end = 16.dp
            )
    ) {
        Text("Display", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Demo mode", style = MaterialTheme.typography.bodyLarge)
                Text("Hide all amounts", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = demoMode, onCheckedChange = { setDemoMode(it) })
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Data", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        SettingsItem("Import / Export", "JSON and CSV backup") { onShowImportExport() }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Help", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        SettingsItem("Show welcome screen", "View the intro again") { onShowWelcome() }
        SettingsItem("Show help", "How the app works") { onShowHelp() }
    }
}

@Composable
private fun SettingsItem(title: String, subtitle: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
