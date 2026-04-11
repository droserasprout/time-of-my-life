package com.timeofmylife.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.timeofmylife.ui.theme.DialogCornerRadius

@Composable
fun QuickEditDialog(
    title: String,
    initialValue: String,
    onSave: (Double) -> Unit,
    onFullEdit: () -> Unit,
    onDismiss: () -> Unit,
) {
    var field by remember {
        mutableStateOf(TextFieldValue(initialValue, TextRange(0, initialValue.length)))
    }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    fun save() {
        val amount = field.text.toDoubleOrNull() ?: return
        onSave(amount)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(DialogCornerRadius)) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = field,
                    onValueChange = { field = it },
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    keyboardActions = KeyboardActions(onDone = { save() }),
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
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
