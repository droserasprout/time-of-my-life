package com.timeofmylife.ui.lifetime

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.domain.LifetimeRow
import com.timeofmylife.ui.theme.LifetimeRowColors
import com.timeofmylife.ui.theme.NegativeText

private val COLUMNS = listOf("Scenario", "1m", "3m", "6m", "12m", "left")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifetimeScreen(repository: FinanceRepository, innerPadding: PaddingValues) {
    val vm: LifetimeViewModel = viewModel(factory = LifetimeViewModel.Factory(repository))
    val rows by vm.rows.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Life Time") }) },
        contentWindowInsets = WindowInsets(0)
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = scaffoldPadding.calculateTopPadding(), bottom = innerPadding.calculateBottomPadding())
                .horizontalScroll(rememberScrollState())
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header row
            Row {
                COLUMNS.forEachIndexed { i, col ->
                    HeaderCell(col, if (i == 0) 120.dp else 70.dp)
                }
            }
            HorizontalDivider()

            rows.forEachIndexed { index, row ->
                MatrixRow(row, LifetimeRowColors.getOrElse(index) { Color.Transparent })
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            }
        }
    }
}

@Composable
private fun HeaderCell(text: String, width: androidx.compose.ui.unit.Dp) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        textAlign = if (text == "Scenario") TextAlign.Start else TextAlign.End,
        modifier = Modifier
            .width(width)
            .padding(vertical = 6.dp, horizontal = 4.dp)
    )
}

@Composable
private fun MatrixRow(row: LifetimeRow, background: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(color = background, modifier = Modifier.fillMaxWidth()) {
            Row {
                Cell(row.label, 120.dp, isLabel = true)
                Cell(formatBalance(row.balance1m), 70.dp)
                Cell(formatBalance(row.balance3m), 70.dp)
                Cell(formatBalance(row.balance6m), 70.dp)
                Cell(formatBalance(row.balance12m), 70.dp)
                Cell(formatMonths(row.monthsLeft), 70.dp)
            }
        }
    }
}

@Composable
private fun Cell(text: String, width: androidx.compose.ui.unit.Dp, isLabel: Boolean = false) {
    val isNegative = text.startsWith("-")
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = when {
            isNegative -> NegativeText
            isLabel -> MaterialTheme.colorScheme.onSurface
            else -> MaterialTheme.colorScheme.onSurface
        },
        textAlign = if (isLabel) TextAlign.Start else TextAlign.End,
        modifier = Modifier
            .width(width)
            .padding(vertical = 8.dp, horizontal = 4.dp)
    )
}

private fun formatBalance(amount: Double): String = when {
    amount >= 0 -> "$${amount.toLong()}"
    else -> "-$${(-amount).toLong()}"
}

private fun formatMonths(months: Double): String =
    if (months.isInfinite()) "∞" else months.toLong().toString()
