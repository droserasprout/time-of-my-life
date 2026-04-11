package com.timeofmylife.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.ui.LocalDemoMode
import com.timeofmylife.ui.LocalSetDemoMode
import com.timeofmylife.ui.theme.SubduedText

@Composable
fun SettingsScreen(
    repository: FinanceRepository,
    innerPadding: PaddingValues,
    onShowWelcome: () -> Unit,
    onShowHelp: () -> Unit,
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
    onShowImportExport: () -> Unit,
) {
    val demoMode = LocalDemoMode.current
    val setDemoMode = LocalSetDemoMode.current

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding() + 16.dp,
                    bottom = innerPadding.calculateBottomPadding(),
                    start = 16.dp,
                    end = 16.dp,
                ),
    ) {
        Text("Display", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text("Demo mode", style = MaterialTheme.typography.bodyLarge)
                Text("Hide all amounts", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            AppToggle(checked = demoMode, onCheckedChange = { setDemoMode(it) })
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
private fun SettingsItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 12.dp),
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun AppToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val trackWidth = 44.dp
    val trackHeight = 24.dp
    val thumbSize = 18.dp
    val padding = (trackHeight - thumbSize) / 2
    val thumbOffset by animateFloatAsState(if (checked) 1f else 0f, label = "thumb")
    val trackColor =
        if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
    val thumbColor =
        if (checked) MaterialTheme.colorScheme.onPrimary else SubduedText

    Box(
        modifier =
            Modifier
                .width(trackWidth)
                .height(trackHeight)
                .clip(RoundedCornerShape(trackHeight / 2))
                .background(trackColor)
                .clickable { onCheckedChange(!checked) },
    ) {
        val maxOffset = trackWidth - thumbSize - padding * 2
        Box(
            modifier =
                Modifier
                    .offset(x = padding + maxOffset * thumbOffset)
                    .align(Alignment.CenterStart)
                    .size(thumbSize)
                    .clip(CircleShape)
                    .background(thumbColor),
        )
    }
}
