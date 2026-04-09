package com.timeofmylife.ui.budget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.data.model.BudgetItem
import com.timeofmylife.data.model.ItemType
import com.timeofmylife.ui.LocalDemoMode
import com.timeofmylife.ui.SegmentedSelector
import com.timeofmylife.ui.formatAmount
import com.timeofmylife.ui.theme.BestBlue
import com.timeofmylife.ui.theme.ExpenseRed
import com.timeofmylife.ui.theme.IncomeGreen
import com.timeofmylife.ui.theme.LastGrey
import com.timeofmylife.ui.theme.WorstOrange

private val AMOUNT_COL_WIDTH = 64.dp

@Composable
fun BudgetScreen(repository: FinanceRepository, innerPadding: PaddingValues) {
    val vm: BudgetViewModel = viewModel(factory = BudgetViewModel.Factory(repository))
    val items by vm.items.collectAsStateWithLifecycle()
    val sortOrder by vm.sortOrder.collectAsStateWithLifecycle()
    val ascending by vm.ascending.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<BudgetItem?>(null) }

    val expenses = remember(items) { items.filter { it.type == ItemType.EXPENSE } }
    val incomes = remember(items) { items.filter { it.type == ItemType.INCOME } }
    val expenseGood = remember(expenses) { expenses.sumOf { it.goodAmount } }
    val expenseBad = remember(expenses) { expenses.sumOf { it.badAmount } }
    val expenseLast = remember(expenses) { expenses.sumOf { it.lastAmount } }
    val incomeGood = remember(incomes) { incomes.sumOf { it.goodAmount } }
    val incomeBad = remember(incomes) { incomes.sumOf { it.badAmount } }
    val incomeLast = remember(incomes) { incomes.sumOf { it.lastAmount } }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Totals pinned at top
            if (items.isNotEmpty()) {
                TotalsCard(
                    expenseGood, expenseBad, expenseLast,
                    incomeGood, incomeBad, incomeLast,
                    modifier = Modifier.padding(
                        top = innerPadding.calculateTopPadding() + 4.dp,
                        start = 16.dp, end = 16.dp
                    )
                )
            }

            // Sort selector below separator
            SegmentedSelector(
                options = SortOrder.entries.toList(),
                selected = sortOrder,
                onSelect = { vm.setSortOrder(it) },
                label = { order ->
                    val arrow = if (order == sortOrder) { if (ascending) " \u25B2" else " \u25BC" } else ""
                    when (order) {
                        SortOrder.ALPHA -> "a-z$arrow"
                        SortOrder.SIZE -> "size$arrow"
                    }
                }
            )

            LazyColumn(
                contentPadding = PaddingValues(
                    top = 8.dp,
                    bottom = innerPadding.calculateBottomPadding() + 80.dp,
                    start = 16.dp, end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (expenses.isNotEmpty()) {
                    item { SectionHeader("Expenses", ExpenseRed) }
                    items(expenses, key = { it.id }) { item ->
                        BudgetItemRow(item = item, onEdit = { editTarget = item })
                    }
                }
                if (incomes.isNotEmpty()) {
                    item { SectionHeader("Income", IncomeGreen) }
                    items(incomes, key = { it.id }) { item ->
                        BudgetItemRow(item = item, onEdit = { editTarget = item })
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
            onDelete = { vm.delete(target); editTarget = null },
            onDismiss = { editTarget = null }
        )
    }
}

@Composable
private fun SectionHeader(label: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(end = 8.dp)
        )
        HorizontalDivider(color = color.copy(alpha = 0.3f), modifier = Modifier.weight(1f))
    }
}

@Composable
private fun TotalsCard(
    expenseGood: Double,
    expenseBad: Double,
    expenseLast: Double,
    incomeGood: Double,
    incomeBad: Double,
    incomeLast: Double,
    modifier: Modifier = Modifier
) {
    val netGood = incomeGood - expenseGood
    val netBad = incomeBad - expenseBad
    val netLast = incomeLast - expenseLast
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp)) {
        // Header row
        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(1f))
            Text("best", style = MaterialTheme.typography.labelSmall, color = BestBlue, textAlign = TextAlign.End, modifier = Modifier.width(AMOUNT_COL_WIDTH))
            Text("last", style = MaterialTheme.typography.labelSmall, color = LastGrey, textAlign = TextAlign.End, modifier = Modifier.width(AMOUNT_COL_WIDTH))
            Text("worst", style = MaterialTheme.typography.labelSmall, color = WorstOrange, textAlign = TextAlign.End, modifier = Modifier.width(AMOUNT_COL_WIDTH))
        }
        TotalsRow("Expenses", expenseGood, expenseBad, expenseLast, ExpenseRed)
        TotalsRow("Income", incomeGood, incomeBad, incomeLast, IncomeGreen)
        TotalsRow("Net", netGood, netBad, netLast, MaterialTheme.colorScheme.onSurface)
        // Single tick bar spanning all 3 columns
        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(1f))
            TickBar(good = netGood, bad = netBad, last = netLast, modifier = Modifier.width(AMOUNT_COL_WIDTH * 3))
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = LastGrey.copy(alpha = 0.3f))
    }
}

@Composable
private fun TotalsRow(
    label: String,
    good: Double,
    bad: Double,
    last: Double,
    labelColor: Color,
) {
    val demo = LocalDemoMode.current
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = labelColor, modifier = Modifier.weight(1f))
        Text(formatAmount(good, demo), style = MaterialTheme.typography.bodySmall, color = BestBlue, textAlign = TextAlign.End, modifier = Modifier.width(AMOUNT_COL_WIDTH))
        Text(formatAmount(last, demo), style = MaterialTheme.typography.bodySmall, color = LastGrey, textAlign = TextAlign.End, modifier = Modifier.width(AMOUNT_COL_WIDTH))
        Text(formatAmount(bad, demo), style = MaterialTheme.typography.bodySmall, color = WorstOrange, textAlign = TextAlign.End, modifier = Modifier.width(AMOUNT_COL_WIDTH))
    }
}

@Composable
private fun TickBar(good: Double, bad: Double, last: Double, modifier: Modifier = Modifier) {
    val range = bad - good
    val fraction = if (range != 0.0) ((last - good) / range).toFloat().coerceIn(0f, 1f) else 0.5f
    val barColor = LastGrey.copy(alpha = 0.3f)
    val tickColor = LastGrey
    Box(
        modifier = modifier
            .height(4.dp)
            .drawBehind {
                drawLine(
                    color = barColor,
                    start = Offset(0f, size.height / 2),
                    end = Offset(size.width, size.height / 2),
                    strokeWidth = 2.dp.toPx()
                )
                val tickX = fraction * size.width
                drawLine(
                    color = tickColor,
                    start = Offset(tickX, 0f),
                    end = Offset(tickX, size.height),
                    strokeWidth = 2.dp.toPx()
                )
            }
    )
}


@Composable
private fun BudgetItemRow(item: BudgetItem, onEdit: () -> Unit) {
    val borderColor = if (item.type == ItemType.EXPENSE) ExpenseRed else IncomeGreen
    Surface(
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onEdit)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRect(
                        color = borderColor,
                        size = androidx.compose.ui.geometry.Size(4.dp.toPx(), size.height)
                    )
                }
                .padding(start = 16.dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val demo = LocalDemoMode.current
            Text(item.name, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Row {
                Text(formatAmount(item.goodAmount, demo), style = MaterialTheme.typography.bodySmall, color = BestBlue, textAlign = TextAlign.End, modifier = Modifier.width(AMOUNT_COL_WIDTH))
                Text(formatAmount(item.lastAmount, demo), style = MaterialTheme.typography.bodySmall, color = LastGrey, textAlign = TextAlign.End, modifier = Modifier.width(AMOUNT_COL_WIDTH))
                Text(formatAmount(item.badAmount, demo), style = MaterialTheme.typography.bodySmall, color = WorstOrange, textAlign = TextAlign.End, modifier = Modifier.width(AMOUNT_COL_WIDTH))
            }
        }
    }
}
