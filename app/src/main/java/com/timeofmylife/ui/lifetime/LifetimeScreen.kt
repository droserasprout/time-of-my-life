package com.timeofmylife.ui.lifetime

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.domain.LifetimeRow
import com.timeofmylife.ui.theme.LifetimeRowColors
import com.timeofmylife.ui.theme.NegativeText
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val BALANCE_COLUMNS = listOf("Scenario", "1m", "3m", "6m", "12m")
private val SURVIVAL_COLUMNS = listOf("Scenario", "Months", "Days", "Final Day")
private val FINAL_DAY_FMT = DateTimeFormatter.ofPattern("MMM yyyy")

@Composable
fun LifetimeScreen(repository: FinanceRepository, innerPadding: PaddingValues) {
    val vm: LifetimeViewModel = viewModel(factory = LifetimeViewModel.Factory(repository))
    val rows by vm.rows.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = innerPadding.calculateTopPadding(), bottom = innerPadding.calculateBottomPadding())
            .verticalScroll(rememberScrollState())
    ) {
        // Balance matrix table
        Column(modifier = Modifier.horizontalScroll(rememberScrollState()).padding(16.dp)) {
            Row {
                BALANCE_COLUMNS.forEachIndexed { i, col ->
                    HeaderCell(col, if (i == 0) 120.dp else 70.dp)
                }
            }
            HorizontalDivider()
            rows.forEachIndexed { index, row ->
                BalanceRow(row, LifetimeRowColors.getOrElse(index) { Color.Transparent })
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Survival table
        Column(modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            Row {
                SURVIVAL_COLUMNS.forEachIndexed { i, col ->
                    HeaderCell(col, survivalColWidth(i))
                }
            }
            HorizontalDivider()
            rows.forEachIndexed { index, row ->
                SurvivalRow(row, LifetimeRowColors.getOrElse(index) { Color.Transparent })
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

private fun survivalColWidth(index: Int): Dp = when (index) {
    0 -> 120.dp
    3 -> 90.dp
    else -> 70.dp
}

@Composable
private fun HeaderCell(text: String, width: Dp) {
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
private fun BalanceRow(row: LifetimeRow, background: Color) {
    Surface(color = background) {
        Row(modifier = Modifier.padding(vertical = 2.dp)) {
            Cell(row.label, 120.dp, isLabel = true)
            Cell(formatBalance(row.balance1m), 70.dp)
            Cell(formatBalance(row.balance3m), 70.dp)
            Cell(formatBalance(row.balance6m), 70.dp)
            Cell(formatBalance(row.balance12m), 70.dp)
        }
    }
}

@Composable
private fun SurvivalRow(row: LifetimeRow, background: Color) {
    val months = row.monthsLeft
    val days = if (months.isInfinite()) Double.POSITIVE_INFINITY else months * 30.44
    val finalDay = when {
        months.isInfinite() -> "∞"
        else -> LocalDate.now().plusDays(days.toLong()).format(FINAL_DAY_FMT)
    }
    Surface(color = background) {
        Row(modifier = Modifier.padding(vertical = 2.dp)) {
            Cell(row.label, 120.dp, isLabel = true)
            Cell(formatMonths(months), 70.dp)
            Cell(formatDays(days), 70.dp)
            Cell(finalDay, 90.dp)
        }
    }
}

@Composable
private fun Cell(text: String, width: Dp, isLabel: Boolean = false) {
    val isNegative = text.startsWith("-")
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = when {
            isNegative -> NegativeText
            else -> MaterialTheme.colorScheme.onSurface
        },
        textAlign = if (isLabel) TextAlign.Start else TextAlign.End,
        modifier = Modifier
            .width(width)
            .padding(vertical = 8.dp, horizontal = 4.dp)
    )
}

private fun formatBalance(amount: Double): String =
    if (amount >= 0) "\$${amount.toLong()}" else "-\$${(-amount).toLong()}"

private fun formatMonths(months: Double): String =
    if (months.isInfinite()) "∞" else months.toLong().toString()

private fun formatDays(days: Double): String =
    if (days.isInfinite()) "∞" else days.toLong().toString()
