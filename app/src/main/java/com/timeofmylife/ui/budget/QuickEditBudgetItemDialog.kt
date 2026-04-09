package com.timeofmylife.ui.budget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.timeofmylife.data.model.BudgetItem

@Composable
fun QuickEditBudgetItemDialog(
    item: BudgetItem,
    onSave: (BudgetItem) -> Unit,
    onFullEdit: () -> Unit,
    onDismiss: () -> Unit
) {
    val initialText = if (item.lastAmount == 0.0) "" else item.lastAmount.toString()
    var field by remember {
        mutableStateOf(TextFieldValue(initialText, TextRange(0, initialText.length)))
    }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    fun save() {
        val amount = field.text.toDoubleOrNull() ?: return
        onSave(item.copy(lastAmount = amount))
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = MaterialTheme.shapes.large) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                OutlinedTextField(
                    value = field,
                    onValueChange = { field = it },
                    label = { Text(item.name) },
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    keyboardActions = KeyboardActions(onDone = { save() }),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onFullEdit) { Text("Edit") }
                    Row {
                        TextButton(onClick = onDismiss) { Text("Cancel") }
                        TextButton(onClick = { save() }) { Text("Save") }
                    }
                }
            }
        }
    }
}
