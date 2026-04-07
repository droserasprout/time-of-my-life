package com.timeofmylife.ui.budget

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import com.timeofmylife.data.model.BudgetItem
import com.timeofmylife.data.model.ItemType
import com.timeofmylife.ui.theme.ExpenseRed
import com.timeofmylife.ui.theme.IncomeGreen

@Composable
fun BudgetScreen(repository: FinanceRepository, innerPadding: PaddingValues) {
    val vm: BudgetViewModel = viewModel(factory = BudgetViewModel.Factory(repository))
    val items by vm.items.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<BudgetItem?>(null) }

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
            items(items, key = { it.id }) { item ->
                BudgetItemRow(item = item, onEdit = { editTarget = item }, onDelete = { vm.delete(item) })
            }
        }
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = innerPadding.calculateBottomPadding() + 16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add budget item")
        }
    }

    if (showAddDialog) {
        AddEditBudgetItemDialog(
            initial = null,
            onConfirm = { vm.upsert(it); showAddDialog = false },
            onDismiss = { showAddDialog = false }
        )
    }
    editTarget?.let { target ->
        AddEditBudgetItemDialog(
            initial = target,
            onConfirm = { vm.upsert(it); editTarget = null },
            onDismiss = { editTarget = null }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun BudgetItemRow(
    item: BudgetItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val borderColor = if (item.type == ItemType.EXPENSE) ExpenseRed else IncomeGreen
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) { onDelete(); true } else false
        }
    )
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
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
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Colored left border
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(60.dp)
                        .background(borderColor)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(item.name, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = item.type.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelSmall,
                            color = borderColor
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("good $${item.goodAmount.toLong()}", style = MaterialTheme.typography.bodySmall, color = IncomeGreen)
                        Text("bad $${item.badAmount.toLong()}", style = MaterialTheme.typography.bodySmall, color = ExpenseRed)
                    }
                }
            }
        }
    }
}
