package com.timeofmylife.ui.budget

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.data.model.BudgetItem
import com.timeofmylife.data.model.ItemType
import com.timeofmylife.ui.theme.ExpenseRed
import com.timeofmylife.ui.theme.IncomeGreen
import kotlinx.coroutines.launch

private val AMOUNT_COL_WIDTH = 68.dp

@Composable
fun BudgetScreen(repository: FinanceRepository, innerPadding: PaddingValues) {
    val vm: BudgetViewModel = viewModel(factory = BudgetViewModel.Factory(repository))
    val items by vm.items.collectAsStateWithLifecycle()
    val sortOrder by vm.sortOrder.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<BudgetItem?>(null) }

    val expenseGood = remember(items) { items.filter { it.type == ItemType.EXPENSE }.sumOf { it.goodAmount } }
    val expenseBad = remember(items) { items.filter { it.type == ItemType.EXPENSE }.sumOf { it.badAmount } }
    val incomeGood = remember(items) { items.filter { it.type == ItemType.INCOME }.sumOf { it.goodAmount } }
    val incomeBad = remember(items) { items.filter { it.type == ItemType.INCOME }.sumOf { it.badAmount } }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Sort row — outside LazyColumn to avoid stickyHeader overhead
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = innerPadding.calculateTopPadding() + 4.dp,
                        start = 16.dp, end = 4.dp
                    ),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (sortOrder == SortOrder.ALPHA) "A–Z" else "Size",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = { vm.toggleSort() }) {
                    Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Toggle sort order")
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(
                    bottom = innerPadding.calculateBottomPadding() + 80.dp,
                    start = 16.dp, end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items, key = { it.id }) { item ->
                    BudgetItemRow(item = item, onEdit = { editTarget = item }, onDelete = { vm.delete(item) })
                }
                if (items.isNotEmpty()) {
                    item {
                        TotalsCard(expenseGood, expenseBad, incomeGood, incomeBad)
                    }
                }
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

@Composable
private fun TotalsCard(
    expenseGood: Double,
    expenseBad: Double,
    incomeGood: Double,
    incomeBad: Double
) {
    val netGood = incomeGood - expenseGood
    val netBad = incomeBad - expenseBad
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            // Header row aligning amount columns
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "good", style = MaterialTheme.typography.labelSmall,
                    color = IncomeGreen, textAlign = TextAlign.End,
                    modifier = Modifier.width(AMOUNT_COL_WIDTH)
                )
                Text(
                    "bad", style = MaterialTheme.typography.labelSmall,
                    color = ExpenseRed, textAlign = TextAlign.End,
                    modifier = Modifier.width(AMOUNT_COL_WIDTH)
                )
            }
            TotalsRow("Expenses", expenseGood, expenseBad, ExpenseRed)
            TotalsRow("Income", incomeGood, incomeBad, IncomeGreen)
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            TotalsRow(
                label = "Net",
                good = netGood,
                bad = netBad,
                labelColor = MaterialTheme.colorScheme.onSurface,
                goodColor = if (netGood >= 0) IncomeGreen else ExpenseRed,
                badColor = if (netBad >= 0) IncomeGreen else ExpenseRed
            )
        }
    }
}

@Composable
private fun TotalsRow(
    label: String,
    good: Double,
    bad: Double,
    labelColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    goodColor: androidx.compose.ui.graphics.Color = IncomeGreen,
    badColor: androidx.compose.ui.graphics.Color = ExpenseRed,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label, style = MaterialTheme.typography.bodyMedium,
            color = labelColor, modifier = Modifier.weight(1f)
        )
        Text(
            formatAmount(good), style = MaterialTheme.typography.bodySmall,
            color = goodColor, textAlign = TextAlign.End,
            modifier = Modifier.width(AMOUNT_COL_WIDTH)
        )
        Text(
            formatAmount(bad), style = MaterialTheme.typography.bodySmall,
            color = badColor, textAlign = TextAlign.End,
            modifier = Modifier.width(AMOUNT_COL_WIDTH)
        )
    }
}

private fun formatAmount(amount: Double): String =
    if (amount >= 0) "\$${amount.toLong()}" else "-\$${(-amount).toLong()}"

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun BudgetItemRow(
    item: BudgetItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var pendingDelete by remember { mutableStateOf(false) }
    val borderColor = if (item.type == ItemType.EXPENSE) ExpenseRed else IncomeGreen
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) { pendingDelete = true; true } else false
        }
    )

    // Snap back any item that was restored in swiped state (e.g. after navigation)
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart && !pendingDelete) {
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }

    if (pendingDelete) {
        AlertDialog(
            onDismissRequest = {
                pendingDelete = false
                scope.launch { dismissState.snapTo(SwipeToDismissBoxValue.Settled) }
            },
            title = { Text("Delete \"${item.name}\"?") },
            confirmButton = {
                TextButton(onClick = { pendingDelete = false; onDelete() }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = {
                    pendingDelete = false
                    scope.launch { dismissState.snapTo(SwipeToDismissBoxValue.Settled) }
                }) { Text("Cancel") }
            }
        )
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
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
                        Text("good \$${item.goodAmount.toLong()}", style = MaterialTheme.typography.bodySmall, color = IncomeGreen)
                        Text("bad \$${item.badAmount.toLong()}", style = MaterialTheme.typography.bodySmall, color = ExpenseRed)
                    }
                }
            }
        }
    }
}
