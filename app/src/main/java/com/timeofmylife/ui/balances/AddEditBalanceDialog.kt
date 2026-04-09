package com.timeofmylife.ui.balances

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.timeofmylife.data.model.Balance
import com.timeofmylife.data.model.Reliability

@Composable
fun AddEditBalanceDialog(
    initial: Balance?,           // null = adding new
    onConfirm: (Balance) -> Unit,
    onDelete: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var reliability by remember { mutableStateOf(initial?.reliability ?: Reliability.HIGH) }
    var amountText by remember { mutableStateOf(initial?.amount?.toString() ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    if (initial == null) "Add Balance" else "Edit Balance",
                    style = MaterialTheme.typography.titleLarge
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount (USD)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                Text("Reliability", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Reliability.entries.forEach { r ->
                        FilterChip(
                            selected = reliability == r,
                            onClick = { reliability = r },
                            label = { Text(r.name) }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
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
                                val amount = amountText.toDoubleOrNull() ?: return@TextButton
                                if (name.isBlank()) return@TextButton
                                onConfirm(Balance(id = initial?.id ?: 0, name = name.trim(), reliability = reliability, amount = amount))
                            }
                        ) { Text("Save") }
                    }
                }
            }
        }
    }
}
