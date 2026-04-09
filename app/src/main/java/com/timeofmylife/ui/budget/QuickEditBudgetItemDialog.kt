package com.timeofmylife.ui.budget

import androidx.compose.runtime.Composable
import com.timeofmylife.data.model.BudgetItem
import com.timeofmylife.ui.QuickEditDialog

@Composable
fun QuickEditBudgetItemDialog(
    item: BudgetItem,
    onSave: (BudgetItem) -> Unit,
    onFullEdit: () -> Unit,
    onDismiss: () -> Unit
) {
    QuickEditDialog(
        title = item.name,
        initialValue = if (item.lastAmount == 0.0) "" else item.lastAmount.toString(),
        onSave = { amount -> onSave(item.copy(lastAmount = amount)) },
        onFullEdit = onFullEdit,
        onDismiss = onDismiss
    )
}
