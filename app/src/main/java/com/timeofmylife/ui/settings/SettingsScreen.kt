package com.timeofmylife.ui.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.ui.LocalDemoMode
import com.timeofmylife.ui.LocalSetDemoMode
import com.timeofmylife.data.json.readCsvFromFolder
import com.timeofmylife.data.json.readImportFromUri
import com.timeofmylife.data.json.writeCsvToFolder
import com.timeofmylife.data.json.writeExportToUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(repository: FinanceRepository, innerPadding: PaddingValues) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showImportConfirm by remember { mutableStateOf<(() -> Unit)?>(null) }

    val jsonExportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch(Dispatchers.IO) {
            val data = repository.exportData()
            writeExportToUri(context, uri, data)
            launch(Dispatchers.Main) { Toast.makeText(context, "JSON exported", Toast.LENGTH_SHORT).show() }
        }
    }

    val jsonImportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        showImportConfirm = {
            scope.launch(Dispatchers.IO) {
                val data = readImportFromUri(context, uri)
                repository.importData(data)
                launch(Dispatchers.Main) { Toast.makeText(context, "JSON imported", Toast.LENGTH_SHORT).show() }
            }
        }
    }

    val csvExportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch(Dispatchers.IO) {
            val balances = repository.getAllBalances()
            val budgetItems = repository.getAllBudgetItems()
            writeCsvToFolder(context, uri, balances, budgetItems)
            launch(Dispatchers.Main) { Toast.makeText(context, "CSV exported", Toast.LENGTH_SHORT).show() }
        }
    }

    val csvImportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        showImportConfirm = {
            scope.launch(Dispatchers.IO) {
                val (balances, budgetItems) = readCsvFromFolder(context, uri)
                repository.importRaw(balances, budgetItems)
                launch(Dispatchers.Main) { Toast.makeText(context, "CSV imported", Toast.LENGTH_SHORT).show() }
            }
        }
    }

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
        Text("Display", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
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

        Text("Export", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        SettingsItem("Export JSON", "Single file with all data") {
            jsonExportLauncher.launch("finances.json")
        }
        SettingsItem("Export CSV", "balances.csv + budget_items.csv to a folder") {
            csvExportLauncher.launch(null)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Import", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        SettingsItem("Import JSON", "Replace all data from a JSON file") {
            jsonImportLauncher.launch(arrayOf("application/json", "*/*"))
        }
        SettingsItem("Import CSV", "Replace all data from a folder with CSV files") {
            csvImportLauncher.launch(null)
        }
    }

    showImportConfirm?.let { onConfirm ->
        AlertDialog(
            onDismissRequest = { showImportConfirm = null },
            title = { Text("Import data?") },
            text = { Text("This will replace all current data with the contents of the selected file(s).") },
            confirmButton = {
                TextButton(onClick = { showImportConfirm = null; onConfirm() }) { Text("Replace") }
            },
            dismissButton = {
                TextButton(onClick = { showImportConfirm = null }) { Text("Cancel") }
            }
        )
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
