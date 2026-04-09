package com.timeofmylife.ui.balances

import androidx.compose.runtime.Composable
import com.timeofmylife.data.model.Balance
import com.timeofmylife.ui.QuickEditDialog

@Composable
fun QuickEditBalanceDialog(
    balance: Balance,
    onSave: (Balance) -> Unit,
    onFullEdit: () -> Unit,
    onDismiss: () -> Unit
) {
    QuickEditDialog(
        title = balance.name,
        initialValue = balance.amount.toString(),
        onSave = { amount -> onSave(balance.copy(amount = amount)) },
        onFullEdit = onFullEdit,
        onDismiss = onDismiss
    )
}
