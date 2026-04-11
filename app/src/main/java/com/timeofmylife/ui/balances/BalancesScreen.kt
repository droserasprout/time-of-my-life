package com.timeofmylife.ui.balances

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.data.model.Balance
import com.timeofmylife.data.model.Reliability
import com.timeofmylife.ui.ItemCard
import com.timeofmylife.ui.LocalDemoMode
import com.timeofmylife.ui.SegmentedSelector
import com.timeofmylife.ui.budget.SortOrder
import com.timeofmylife.ui.formatAmount
import com.timeofmylife.ui.theme.*

private fun reliabilityColor(reliability: Reliability): Color =
    when (reliability) {
        Reliability.HIGH -> HighColor
        Reliability.MEDIUM -> MediumColor
        Reliability.LOW -> LowColor
    }

private fun reliabilityLabel(reliability: Reliability): String =
    when (reliability) {
        Reliability.HIGH -> "High"
        Reliability.MEDIUM -> "Medium"
        Reliability.LOW -> "Low"
    }

private val AMOUNT_COL_WIDTH = 72.dp
private val PCT_COL_WIDTH = 52.dp

@Composable
fun BalancesScreen(
    repository: FinanceRepository,
    innerPadding: PaddingValues,
) {
    val vm: BalancesViewModel = viewModel(factory = BalancesViewModel.Factory(repository))
    val items by vm.items.collectAsStateWithLifecycle()
    val sortOrder by vm.sortOrder.collectAsStateWithLifecycle()
    val ascending by vm.ascending.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<Balance?>(null) }
    var quickEditTarget by remember { mutableStateOf<Balance?>(null) }

    val totalAmount = remember(items) { items.sumOf { it.amount } }
    val tierSums =
        remember(items) {
            Reliability.entries.associateWith { r -> items.filter { it.reliability == r }.sumOf { it.amount } }
        }

    // Group items by reliability for section separators
    val groupedItems =
        remember(items) {
            val result = mutableListOf<Any>() // Reliability headers and Balance items
            Reliability.entries.forEach { r ->
                val tierItems = items.filter { it.reliability == r }
                if (tierItems.isNotEmpty()) {
                    result.add(r)
                    result.addAll(tierItems)
                }
            }
            result
        }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(top = innerPadding.calculateTopPadding())) {
            // Totals panel
            if (items.isNotEmpty()) {
                TotalsPanel(
                    tierSums,
                    totalAmount,
                    modifier =
                        Modifier.padding(
                            start = ScreenHorizontalPadding,
                            end = ScreenHorizontalPadding,
                        ),
                )
            }

            // Sort selector
            SegmentedSelector(
                options = listOf(SortOrder.ALPHA, SortOrder.AVG),
                selected = sortOrder,
                onSelect = { vm.setSortOrder(it) },
                label = { order ->
                    val arrow = if (ascending) " \u25B2" else " \u25BC"
                    when (order) {
                        SortOrder.ALPHA -> "a-z$arrow"
                        else -> "amount$arrow"
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
                groupedItems.forEach { entry ->
                    when (entry) {
                        is Reliability ->
                            item(key = "header_${entry.name}") {
                                SectionHeader(reliabilityLabel(entry), reliabilityColor(entry))
                            }
                        is Balance ->
                            item(key = entry.id) {
                                BalanceItem(
                                    balance = entry,
                                    onTap = { quickEditTarget = entry },
                                )
                            }
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
            Icon(Icons.Default.Add, contentDescription = "Add balance")
        }
    }

    if (showAddDialog) {
        AddEditBalanceDialog(null, onConfirm = {
            vm.upsert(it)
            showAddDialog = false
        }, onDismiss = { showAddDialog = false })
    }
    editTarget?.let { target ->
        AddEditBalanceDialog(
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
    quickEditTarget?.let { target ->
        QuickEditBalanceDialog(
            balance = target,
            onSave = {
                vm.upsert(it)
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
private fun TotalsPanel(
    tierSums: Map<Reliability, Double>,
    totalAmount: Double,
    modifier: Modifier = Modifier,
) {
    val demo = LocalDemoMode.current
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp)) {
        // Proportion bar
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(BarHeight)
                    .clip(MaterialTheme.shapes.small),
        ) {
            Reliability.entries.forEach { r ->
                val sum = tierSums[r] ?: 0.0
                if (sum > 0 && totalAmount > 0) {
                    Box(
                        modifier =
                            Modifier
                                .weight((sum / totalAmount).toFloat())
                                .fillMaxHeight()
                                .background(reliabilityColor(r)),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(ItemSpacing))
        // Header row
        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(1f))
            val headerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            Text(
                "sum",
                style = MaterialTheme.typography.labelSmall,
                color = headerColor,
                textAlign = TextAlign.End,
                modifier = Modifier.width(AMOUNT_COL_WIDTH),
            )
            Text(
                "%",
                style = MaterialTheme.typography.labelSmall,
                color = headerColor,
                textAlign = TextAlign.End,
                modifier = Modifier.width(PCT_COL_WIDTH),
            )
        }
        Reliability.entries.forEach { r ->
            val sum = tierSums[r] ?: 0.0
            val pct = if (totalAmount != 0.0) sum / totalAmount * 100.0 else 0.0
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    reliabilityLabel(r),
                    style = MaterialTheme.typography.bodyMedium,
                    color = reliabilityColor(r),
                    modifier = Modifier.weight(1f),
                )
                Text(
                    formatAmount(sum, demo),
                    style = MaterialTheme.typography.bodySmall,
                    color = reliabilityColor(r),
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(AMOUNT_COL_WIDTH),
                )
                Text(
                    "%.1f".format(pct),
                    style = MaterialTheme.typography.bodySmall,
                    color = reliabilityColor(r),
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(PCT_COL_WIDTH),
                )
            }
        }
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
private fun BalanceItem(
    balance: Balance,
    onTap: () -> Unit,
) {
    val borderColor = reliabilityColor(balance.reliability)
    ItemCard(borderColor = borderColor, onClick = onTap) {
        Text(balance.name, style = MaterialTheme.typography.bodyLarge)
        Text(
            formatAmount(balance.amount, LocalDemoMode.current),
            style = MaterialTheme.typography.bodyLarge,
            color = borderColor,
        )
    }
}
