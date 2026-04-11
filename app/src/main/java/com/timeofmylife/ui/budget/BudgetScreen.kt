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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.data.model.BudgetItem
import com.timeofmylife.data.model.ItemType
import com.timeofmylife.ui.ItemCard
import com.timeofmylife.ui.LocalDemoMode
import com.timeofmylife.ui.QuickEditDialog
import com.timeofmylife.ui.SegmentedSelector
import com.timeofmylife.ui.formatAmount
import com.timeofmylife.ui.theme.*

enum class BudgetColumn { BEST, LAST, WORST }

@Composable
fun BudgetScreen(
    repository: FinanceRepository,
    innerPadding: PaddingValues,
) {
    val vm: BudgetViewModel = viewModel(factory = BudgetViewModel.Factory(repository))
    val items by vm.items.collectAsStateWithLifecycle()
    val sortOrder by vm.sortOrder.collectAsStateWithLifecycle()
    val ascending by vm.ascending.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<BudgetItem?>(null) }
    var quickEditTarget by remember { mutableStateOf<Pair<BudgetItem, BudgetColumn>?>(null) }

    val expenses = remember(items) { items.filter { it.type == ItemType.EXPENSE } }
    val incomes = remember(items) { items.filter { it.type == ItemType.INCOME } }
    val expenseBest = remember(expenses) { expenses.sumOf { it.bestAmount } }
    val expenseWorst = remember(expenses) { expenses.sumOf { it.worstAmount } }
    val expenseLast = remember(expenses) { expenses.sumOf { it.lastAmount } }
    val incomeBest = remember(incomes) { incomes.sumOf { it.bestAmount } }
    val incomeWorst = remember(incomes) { incomes.sumOf { it.worstAmount } }
    val incomeLast = remember(incomes) { incomes.sumOf { it.lastAmount } }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(top = innerPadding.calculateTopPadding())) {
            // Totals panel
            if (items.isNotEmpty()) {
                TotalsCard(
                    expenseBest,
                    expenseWorst,
                    expenseLast,
                    incomeBest,
                    incomeWorst,
                    incomeLast,
                    modifier =
                        Modifier.padding(
                            start = ScreenHorizontalPadding,
                            end = ScreenHorizontalPadding,
                        ),
                )
            }

            // Sort selector
            SegmentedSelector(
                options = SortOrder.entries.toList(),
                selected = sortOrder,
                onSelect = { vm.setSortOrder(it) },
                label = { order ->
                    val arrow = if (ascending) " \u25B2" else " \u25BC"
                    when (order) {
                        SortOrder.ALPHA -> "a-z$arrow"
                        SortOrder.AVG -> "avg$arrow"
                        SortOrder.LAST -> "last$arrow"
                    }
                },
                showSuffix = { it == sortOrder },
            )

            LazyColumn(
                contentPadding =
                    PaddingValues(
                        top = ItemSpacing,
                        bottom = innerPadding.calculateBottomPadding() + FabBottomClearance,
                        start = ScreenHorizontalPadding,
                        end = ScreenHorizontalPadding,
                    ),
                verticalArrangement = Arrangement.spacedBy(ItemSpacing),
                modifier = Modifier.weight(1f),
            ) {
                if (expenses.isNotEmpty()) {
                    item { SectionHeader("Expenses", ExpenseRed) }
                    items(expenses, key = { it.id }) { item ->
                        BudgetItemRow(
                            item = item,
                            onColumnClick = { col -> quickEditTarget = item to col },
                            onNameClick = { editTarget = item },
                        )
                    }
                }
                if (incomes.isNotEmpty()) {
                    item { SectionHeader("Income", IncomeGreen) }
                    items(incomes, key = { it.id }) { item ->
                        BudgetItemRow(
                            item = item,
                            onColumnClick = { col -> quickEditTarget = item to col },
                            onNameClick = { editTarget = item },
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = ScreenHorizontalPadding, bottom = innerPadding.calculateBottomPadding() + ScreenHorizontalPadding),
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add budget item")
        }
    }

    if (showAddDialog) {
        AddEditBudgetItemDialog(
            initial = null,
            onConfirm = {
                vm.upsert(it)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false },
        )
    }
    editTarget?.let { target ->
        AddEditBudgetItemDialog(
            initial = target,
            onConfirm = {
                vm.upsert(it)
                editTarget = null
            },
            onDelete = {
                vm.delete(target)
                editTarget = null
            },
            onDismiss = { editTarget = null },
        )
    }
    quickEditTarget?.let { (target, column) ->
        val columnLabel =
            when (column) {
                BudgetColumn.BEST -> "best"
                BudgetColumn.LAST -> "last"
                BudgetColumn.WORST -> "worst"
            }
        val initialValue =
            when (column) {
                BudgetColumn.BEST -> target.bestAmount
                BudgetColumn.LAST -> target.lastAmount
                BudgetColumn.WORST -> target.worstAmount
            }
        QuickEditDialog(
            title = "${target.name} | $columnLabel",
            initialValue = if (initialValue == 0.0) "" else initialValue.toString(),
            onSave = { amount ->
                val updated =
                    when (column) {
                        BudgetColumn.BEST -> target.copy(bestAmount = amount)
                        BudgetColumn.LAST -> target.copy(lastAmount = amount)
                        BudgetColumn.WORST -> target.copy(worstAmount = amount)
                    }
                vm.upsert(updated)
                quickEditTarget = null
            },
            onFullEdit = {
                quickEditTarget = null
                editTarget = target
            },
            onDismiss = { quickEditTarget = null },
        )
    }
}

@Composable
private fun SectionHeader(
    label: String,
    color: Color,
) {
    Text(
        label,
        style = MaterialTheme.typography.labelMedium,
        color = color,
        modifier = Modifier.padding(vertical = 4.dp),
    )
}

@Composable
private fun TotalsCard(
    expenseBest: Double,
    expenseWorst: Double,
    expenseLast: Double,
    incomeBest: Double,
    incomeWorst: Double,
    incomeLast: Double,
    modifier: Modifier = Modifier,
) {
    val netBest = incomeBest - expenseBest
    val netWorst = incomeWorst - expenseWorst
    val netLast = incomeLast - expenseLast
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp)) {
        // Accuracy bar at top
        TickBar(
            best = netBest,
            worst = netWorst,
            last = netLast,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(ItemSpacing))
        // Header row
        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(1f))
            val headerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            Text(
                "best",
                style = MaterialTheme.typography.labelSmall,
                color = headerColor,
                textAlign = TextAlign.End,
                modifier = Modifier.width(AmountColumnWidth),
            )
            Text(
                "last",
                style = MaterialTheme.typography.labelSmall,
                color = headerColor,
                textAlign = TextAlign.End,
                modifier = Modifier.width(AmountColumnWidth),
            )
            Text(
                "worst",
                style = MaterialTheme.typography.labelSmall,
                color = headerColor,
                textAlign = TextAlign.End,
                modifier = Modifier.width(AmountColumnWidth),
            )
        }
        TotalsRow("Expenses", expenseBest, expenseWorst, expenseLast, ExpenseRed)
        TotalsRow("Income", incomeBest, incomeWorst, incomeLast, IncomeGreen)
        TotalsRow("Net", netBest, netWorst, netLast, MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun TotalsRow(
    label: String,
    best: Double,
    worst: Double,
    last: Double,
    labelColor: Color,
) {
    val demo = LocalDemoMode.current
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = labelColor, modifier = Modifier.weight(1f))
        Text(
            formatAmount(best, demo),
            style = MaterialTheme.typography.bodySmall,
            color = BestBlue,
            textAlign = TextAlign.End,
            modifier = Modifier.width(AmountColumnWidth),
        )
        Text(
            formatAmount(last, demo),
            style = MaterialTheme.typography.bodySmall,
            color = LastGrey,
            textAlign = TextAlign.End,
            modifier = Modifier.width(AmountColumnWidth),
        )
        Text(
            formatAmount(worst, demo),
            style = MaterialTheme.typography.bodySmall,
            color = WorstOrange,
            textAlign = TextAlign.End,
            modifier = Modifier.width(AmountColumnWidth),
        )
    }
}

@Composable
private fun TickBar(
    best: Double,
    worst: Double,
    last: Double,
    modifier: Modifier = Modifier,
) {
    val range = worst - best
    val fraction = if (range != 0.0) ((last - best) / range).toFloat().coerceIn(0f, 1f) else 0.5f
    val tickColor = Color.White
    val tickOverhang = 2.dp
    val tickWidth = 8.dp
    val tickRadius = 2.dp
    val shape = MaterialTheme.shapes.small
    Box(modifier = modifier.height(BarHeight + tickOverhang * 2)) {
        // Background bar
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(BarHeight)
                    .align(Alignment.Center)
                    .clip(shape)
                    .drawBehind {
                        drawRect(color = BestBlue, topLeft = Offset.Zero, size = Size(size.width / 2, size.height))
                        drawRect(color = WorstOrange, topLeft = Offset(size.width / 2, 0f), size = Size(size.width / 2, size.height))
                    },
        )
        // Tick (rounded square, taller than bar)
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .drawBehind {
                        val tickWidthPx = tickWidth.toPx()
                        val tickX = (fraction * size.width - tickWidthPx / 2).coerceIn(0f, size.width - tickWidthPx)
                        val tickRadiusPx = tickRadius.toPx()
                        drawRoundRect(
                            color = tickColor,
                            topLeft = Offset(tickX, 0f),
                            size = Size(tickWidthPx, size.height),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(tickRadiusPx),
                        )
                    },
        )
    }
}

@Composable
private fun BudgetItemRow(
    item: BudgetItem,
    onColumnClick: (BudgetColumn) -> Unit,
    onNameClick: () -> Unit,
) {
    val borderColor = if (item.type == ItemType.EXPENSE) ExpenseRed else IncomeGreen
    ItemCard(borderColor = borderColor) {
        val demo = LocalDemoMode.current
        Text(item.name, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f).clickable(onClick = onNameClick))
        Row {
            Text(
                formatAmount(
                    item.bestAmount,
                    demo,
                ),
                style = MaterialTheme.typography.bodySmall,
                color = BestBlue,
                textAlign = TextAlign.End,
                modifier =
                    Modifier.width(
                        AmountColumnWidth,
                    ).clickable {
                        onColumnClick(BudgetColumn.BEST)
                    },
            )
            Text(
                formatAmount(
                    item.lastAmount,
                    demo,
                ),
                style = MaterialTheme.typography.bodySmall,
                color = LastGrey,
                textAlign = TextAlign.End,
                modifier =
                    Modifier.width(
                        AmountColumnWidth,
                    ).clickable {
                        onColumnClick(BudgetColumn.LAST)
                    },
            )
            Text(
                formatAmount(
                    item.worstAmount,
                    demo,
                ),
                style = MaterialTheme.typography.bodySmall,
                color = WorstOrange,
                textAlign = TextAlign.End,
                modifier =
                    Modifier.width(
                        AmountColumnWidth,
                    ).clickable {
                        onColumnClick(BudgetColumn.WORST)
                    },
            )
        }
    }
}
