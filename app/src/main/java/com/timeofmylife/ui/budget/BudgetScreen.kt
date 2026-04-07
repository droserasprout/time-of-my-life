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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.data.model.BudgetItem
import com.timeofmylife.data.model.ItemType
import com.timeofmylife.ui.theme.ExpenseRed
import com.timeofmylife.ui.theme.IncomeGreen

private val AMOUNT_COL_WIDTH = 68.dp

@Composable
fun BudgetScreen(repository: FinanceRepository, innerPadding: PaddingValues) {
    val vm: BudgetViewModel = viewModel(factory = BudgetViewModel.Factory(repository))
    val items by vm.items.collectAsStateWithLifecycle()
    val sortOrder by vm.sortOrder.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<BudgetItem?>(null) }

    val expenses = remember(items) { items.filter { it.type == ItemType.EXPENSE } }
    val incomes = remember(items) { items.filter { it.type == ItemType.INCOME } }
    val expenseGood = remember(expenses) { expenses.sumOf { it.goodAmount } }
    val expenseBad = remember(expenses) { expenses.sumOf { it.badAmount } }
    val incomeGood = remember(incomes) { incomes.sumOf { it.goodAmount } }
    val incomeBad = remember(incomes) { incomes.sumOf { it.badAmount } }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Sort row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = innerPadding.calculateTopPadding() + 4.dp, start = 16.dp, end = 4.dp),
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

            // Totals pinned below sort row
            if (items.isNotEmpty()) {
                TotalsCard(
                    expenseGood, expenseBad, incomeGood, incomeBad,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

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
    incomeGood: Double,
    incomeBad: Double,
    modifier: Modifier = Modifier
) {
    val netGood = incomeGood - expenseGood
    val netBad = incomeBad - expenseBad
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "best", style = MaterialTheme.typography.labelSmall,
                    color = IncomeGreen, textAlign = TextAlign.End,
                    modifier = Modifier.width(AMOUNT_COL_WIDTH)
                )
                Text(
                    "worst", style = MaterialTheme.typography.labelSmall,
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
        Text(label, style = MaterialTheme.typography.bodyMedium, color = labelColor, modifier = Modifier.weight(1f))
        Text(formatAmount(good), style = MaterialTheme.typography.bodySmall, color = goodColor, textAlign = TextAlign.End, modifier = Modifier.width(AMOUNT_COL_WIDTH))
        Text(formatAmount(bad), style = MaterialTheme.typography.bodySmall, color = badColor, textAlign = TextAlign.End, modifier = Modifier.width(AMOUNT_COL_WIDTH))
    }
}

private fun formatAmount(amount: Double): String =
    if (amount >= 0) "\$${amount.toLong()}" else "-\$${(-amount).toLong()}"

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BudgetItemRow(item: BudgetItem, onEdit: () -> Unit) {
    val borderColor = if (item.type == ItemType.EXPENSE) ExpenseRed else IncomeGreen
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = {}, onLongClick = onEdit)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(borderColor))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(item.name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontSize = 14.sp, color = IncomeGreen)) {
                            append("\$${item.goodAmount.toLong()}")
                        }
                        withStyle(SpanStyle(fontSize = 10.sp, color = IncomeGreen.copy(alpha = 0.7f))) {
                            append(" best ")
                        }
                        withStyle(SpanStyle(fontSize = 14.sp, color = ExpenseRed)) {
                            append("\$${item.badAmount.toLong()}")
                        }
                        withStyle(SpanStyle(fontSize = 10.sp, color = ExpenseRed.copy(alpha = 0.7f))) {
                            append(" worst")
                        }
                    }
                )
            }
        }
    }
}
