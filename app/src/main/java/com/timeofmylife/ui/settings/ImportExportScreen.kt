package com.timeofmylife.ui.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.data.json.readBalancesCsv
import com.timeofmylife.data.json.readBudgetItemsCsv
import com.timeofmylife.data.json.readImportFromUri
import com.timeofmylife.data.json.writeBalancesCsv
import com.timeofmylife.data.json.writeBudgetItemsCsv
import com.timeofmylife.data.json.writeExportToUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private fun timestamp(): String =
    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))

@Composable
fun ImportExportScreen(repository: FinanceRepository, innerPadding: PaddingValues) {
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

    val balancesCsvExportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch(Dispatchers.IO) {
            val balances = repository.getAllBalances()
            writeBalancesCsv(context, uri, balances)
            launch(Dispatchers.Main) { Toast.makeText(context, "Balances CSV exported", Toast.LENGTH_SHORT).show() }
        }
    }

    val budgetCsvExportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch(Dispatchers.IO) {
            val items = repository.getAllBudgetItems()
            writeBudgetItemsCsv(context, uri, items)
            launch(Dispatchers.Main) { Toast.makeText(context, "Budget CSV exported", Toast.LENGTH_SHORT).show() }
        }
    }

    val balancesCsvImportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        showImportConfirm = {
            scope.launch(Dispatchers.IO) {
                val balances = readBalancesCsv(context, uri)
                repository.importBalances(balances)
                launch(Dispatchers.Main) { Toast.makeText(context, "Balances CSV imported", Toast.LENGTH_SHORT).show() }
            }
        }
    }

    val budgetCsvImportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        showImportConfirm = {
            scope.launch(Dispatchers.IO) {
                val items = readBudgetItemsCsv(context, uri)
                repository.importBudgetItems(items)
                launch(Dispatchers.Main) { Toast.makeText(context, "Budget CSV imported", Toast.LENGTH_SHORT).show() }
            }
        }
    }

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
            Text("Export", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            ImportExportItem("Export JSON", "Single file with all data") {
                jsonExportLauncher.launch("finances_${timestamp()}.json")
            }
            ImportExportItem("Export balances CSV", "Single CSV file") {
                balancesCsvExportLauncher.launch("balances_${timestamp()}.csv")
            }
            ImportExportItem("Export budget CSV", "Single CSV file") {
                budgetCsvExportLauncher.launch("budget_${timestamp()}.csv")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Import", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            ImportExportItem("Import JSON", "Replace all data from a JSON file") {
                jsonImportLauncher.launch(arrayOf("application/json", "*/*"))
            }
            ImportExportItem("Import balances CSV", "Replace balances from a CSV file") {
                balancesCsvImportLauncher.launch(arrayOf("text/csv", "*/*"))
            }
            ImportExportItem("Import budget CSV", "Replace budget items from a CSV file") {
                budgetCsvImportLauncher.launch(arrayOf("text/csv", "*/*"))
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
private fun ImportExportItem(title: String, subtitle: String, onClick: () -> Unit) {
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
