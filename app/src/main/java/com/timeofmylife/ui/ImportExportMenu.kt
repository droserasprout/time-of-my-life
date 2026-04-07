package com.timeofmylife.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.data.json.readImportFromUri
import com.timeofmylife.data.json.writeExportToUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ImportExportMenu(repository: FinanceRepository) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }
    var showImportConfirm by remember { mutableStateOf<android.net.Uri?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch(Dispatchers.IO) {
            val data = repository.exportData()
            writeExportToUri(context, uri, data)
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) showImportConfirm = uri
    }

    IconButton(onClick = { expanded = true }) {
        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        DropdownMenuItem(
            text = { Text("Export JSON") },
            onClick = {
                expanded = false
                exportLauncher.launch("finances.json")
            }
        )
        DropdownMenuItem(
            text = { Text("Import JSON") },
            onClick = {
                expanded = false
                importLauncher.launch(arrayOf("application/json", "*/*"))
            }
        )
    }

    showImportConfirm?.let { uri ->
        AlertDialog(
            onDismissRequest = { showImportConfirm = null },
            title = { Text("Import data?") },
            text = { Text("This will replace all current data with the contents of the selected file.") },
            confirmButton = {
                TextButton(onClick = {
                    showImportConfirm = null
                    scope.launch(Dispatchers.IO) {
                        val data = readImportFromUri(context, uri)
                        repository.importData(data)
                    }
                }) { Text("Replace") }
            },
            dismissButton = {
                TextButton(onClick = { showImportConfirm = null }) { Text("Cancel") }
            }
        )
    }
}
