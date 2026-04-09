package com.timeofmylife.ui.budget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import com.timeofmylife.ui.theme.DialogCornerRadius
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.timeofmylife.data.model.BudgetItem
import com.timeofmylife.data.model.ItemType

@Composable
fun AddEditBudgetItemDialog(
    initial: BudgetItem?,
    onConfirm: (BudgetItem) -> Unit,
    onDelete: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var type by remember { mutableStateOf(initial?.type ?: ItemType.EXPENSE) }
    var bestText by remember { mutableStateOf(initial?.bestAmount?.toString() ?: "") }
    var worstText by remember { mutableStateOf(initial?.worstAmount?.toString() ?: "") }
    var lastText by remember { mutableStateOf(initial?.lastAmount?.let { if (it == 0.0) "" else it.toString() } ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(DialogCornerRadius)) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    if (initial == null) "Add budget item" else "Edit budget item",
                    style = MaterialTheme.typography.titleLarge
                )
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
                    value = bestText,
                    onValueChange = { bestText = it },
                    label = { Text("Best amount (USD)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = worstText,
                    onValueChange = { worstText = it },
                    label = { Text("Worst amount (USD)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = lastText,
                    onValueChange = { lastText = it },
                    label = { Text("Last month (USD)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Delete on the left, only in edit mode
                    if (onDelete != null) {
                        TextButton(
                            onClick = onDelete,
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) { Text("Delete") }
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                    Row {
                        TextButton(onClick = onDismiss) { Text("Cancel") }
                        TextButton(
                            onClick = {
                                val good = bestText.toDoubleOrNull() ?: return@TextButton
                                val bad = worstText.toDoubleOrNull() ?: return@TextButton
                                if (name.isBlank()) return@TextButton
                                val last = lastText.toDoubleOrNull() ?: 0.0
                                onConfirm(BudgetItem(id = initial?.id ?: 0, name = name.trim(), type = type, bestAmount = good, worstAmount = bad, lastAmount = last))
                            }
                        ) { Text("Save") }
                    }
                }
            }
        }
    }
}
