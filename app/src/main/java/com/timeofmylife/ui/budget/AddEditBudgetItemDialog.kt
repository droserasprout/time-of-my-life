package com.timeofmylife.ui.budget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.timeofmylife.data.model.BudgetItem
import com.timeofmylife.data.model.ItemType

@Composable
fun AddEditBudgetItemDialog(
    initial: BudgetItem?,
    onConfirm: (BudgetItem) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var type by remember { mutableStateOf(initial?.type ?: ItemType.EXPENSE) }
    var goodText by remember { mutableStateOf(initial?.goodAmount?.toString() ?: "") }
    var badText by remember { mutableStateOf(initial?.badAmount?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Add Budget Item" else "Edit Budget Item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Text("Type", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ItemType.entries.forEach { t ->
                        FilterChip(
                            selected = type == t,
                            onClick = { type = t },
                            label = { Text(t.name.lowercase().replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }
                OutlinedTextField(
                    value = goodText,
                    onValueChange = { goodText = it },
                    label = { Text("Best amount (USD)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = badText,
                    onValueChange = { badText = it },
                    label = { Text("Worst amount (USD)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val good = goodText.toDoubleOrNull() ?: return@TextButton
                    val bad = badText.toDoubleOrNull() ?: return@TextButton
                    if (name.isBlank()) return@TextButton
                    onConfirm(BudgetItem(id = initial?.id ?: 0, name = name.trim(), type = type, goodAmount = good, badAmount = bad))
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
