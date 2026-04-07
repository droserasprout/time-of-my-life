package com.timeofmylife.ui.balances

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.timeofmylife.data.model.Balance

@Composable
fun QuickEditBalanceDialog(
    balance: Balance,
    onSave: (Balance) -> Unit,
    onFullEdit: () -> Unit,
    onDismiss: () -> Unit
) {
    val initialText = balance.amount.toString()
    var field by remember {
        mutableStateOf(TextFieldValue(initialText, TextRange(0, initialText.length)))
    }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = MaterialTheme.shapes.large) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(balance.name, style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = onFullEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Full edit")
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = field,
                    onValueChange = { field = it },
                    label = { Text("Amount (USD)") },
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    keyboardActions = KeyboardActions(onDone = {
                        val amount = field.text.toDoubleOrNull() ?: return@KeyboardActions
                        onSave(balance.copy(amount = amount))
                    }),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = {
                        val amount = field.text.toDoubleOrNull() ?: return@TextButton
                        onSave(balance.copy(amount = amount))
                    }) { Text("Save") }
                }
            }
        }
    }
}
