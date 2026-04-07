package com.timeofmylife.ui.balances

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.data.model.Balance
import com.timeofmylife.data.model.Reliability
import com.timeofmylife.ui.theme.HighColor
import com.timeofmylife.ui.theme.LowColor
import com.timeofmylife.ui.theme.MediumColor

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BalancesScreen(repository: FinanceRepository, innerPadding: PaddingValues) {
    val vm: BalancesViewModel = viewModel(factory = BalancesViewModel.Factory(repository))
    val grouped by vm.grouped.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<Balance?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 80.dp,
                start = 16.dp, end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            grouped.forEach { (reliability, items) ->
                stickyHeader(key = reliability.name) {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        Text(
                            text = when (reliability) {
                                Reliability.HIGH -> "High reliability"
                                Reliability.MEDIUM -> "Medium reliability"
                                Reliability.LOW -> "Low reliability"
                            },
                            color = when (reliability) {
                                Reliability.HIGH -> HighColor
                                Reliability.MEDIUM -> MediumColor
                                Reliability.LOW -> LowColor
                            },
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        )
                    }
                }
                items(items, key = { it.id }) { balance ->
                    BalanceItem(balance = balance, onEdit = { editTarget = balance }, onDelete = { vm.delete(balance) })
                }
            }
        }
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = innerPadding.calculateBottomPadding() + 16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add balance")
        }
    }

    if (showAddDialog) {
        AddEditBalanceDialog(null, onConfirm = { vm.upsert(it); showAddDialog = false }, onDismiss = { showAddDialog = false })
    }
    editTarget?.let { target ->
        AddEditBalanceDialog(target, onConfirm = { vm.upsert(it); editTarget = null }, onDismiss = { editTarget = null })
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun BalanceItem(
    balance: Balance,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) { onDelete(); true } else false
        }
    )
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(onClick = {}, onLongClick = onEdit)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(balance.name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    "$${balance.amount.toLong()}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
