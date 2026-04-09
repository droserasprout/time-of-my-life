package com.timeofmylife.ui.balances

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.data.model.Balance
import com.timeofmylife.data.model.Reliability
import com.timeofmylife.ui.LocalDemoMode
import com.timeofmylife.ui.formatAmount
import com.timeofmylife.ui.theme.HighColor
import com.timeofmylife.ui.theme.LowColor
import com.timeofmylife.ui.theme.MediumColor

private fun reliabilityColor(reliability: Reliability): Color = when (reliability) {
    Reliability.HIGH -> HighColor
    Reliability.MEDIUM -> MediumColor
    Reliability.LOW -> LowColor
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BalancesScreen(repository: FinanceRepository, innerPadding: PaddingValues) {
    val vm: BalancesViewModel = viewModel(factory = BalancesViewModel.Factory(repository))
    val grouped by vm.grouped.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<Balance?>(null) }
    var quickEditTarget by remember { mutableStateOf<Balance?>(null) }

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
                            color = reliabilityColor(reliability),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        )
                    }
                }
                items(items, key = { it.id }) { balance ->
                    BalanceItem(
                        balance = balance,
                        onTap = { quickEditTarget = balance }
                    )
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
        AddEditBalanceDialog(
            initial = target,
            onConfirm = { vm.upsert(it); editTarget = null },
            onDelete = { vm.delete(target); editTarget = null },
            onDismiss = { editTarget = null }
        )
    }
    quickEditTarget?.let { target ->
        QuickEditBalanceDialog(
            balance = target,
            onSave = { vm.upsert(it); quickEditTarget = null },
            onFullEdit = { quickEditTarget = null; editTarget = target },
            onDismiss = { quickEditTarget = null }
        )
    }
}

@Composable
private fun BalanceItem(
    balance: Balance,
    onTap: () -> Unit
) {
    val borderColor = reliabilityColor(balance.reliability)
    Surface(
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onTap)
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
            Text(balance.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                formatAmount(balance.amount, LocalDemoMode.current),
                style = MaterialTheme.typography.bodyLarge,
                color = borderColor
            )
        }
    }
}
