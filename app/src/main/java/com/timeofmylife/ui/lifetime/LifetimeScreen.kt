package com.timeofmylife.ui.lifetime

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.domain.LifetimeRow
import com.timeofmylife.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val BALANCE_COLUMNS = listOf("Scenario", "1m", "3m", "6m", "12m")
private val SURVIVAL_COLUMNS = listOf("Scenario", "Time left", "Final Day")
private val FINAL_DAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd")

private val RELIABILITY_COLORS = mapOf(
    "high" to HighColor,
    "medium" to MediumColor,
    "low" to LowColor,
)
private val BUDGET_COLORS = mapOf(
    "best" to BestBlue,
    "worst" to WorstOrange,
)

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
        // Balance matrix table (horizontal scroll for narrow screens)
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

        // Survival table — full width, no horizontal scroll
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                SurvivalHeaderCell(SURVIVAL_COLUMNS[0], isLabel = true)
                SurvivalHeaderCell(SURVIVAL_COLUMNS[1])
                SurvivalHeaderCell(SURVIVAL_COLUMNS[2])
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

@Composable
private fun RowScope.SurvivalHeaderCell(text: String, isLabel: Boolean = false) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        textAlign = if (isLabel) TextAlign.Start else TextAlign.End,
        modifier = Modifier
            .weight(1f)
            .padding(vertical = 6.dp, horizontal = 4.dp)
    )
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
private fun ScenarioLabel(label: String, dotColor: Color) {
    val parts = label.split(" / ")
    val reliabilityPart = parts.getOrElse(0) { "" }
    val budgetPart = parts.getOrElse(1) { "" }
    val reliabilityColor = RELIABILITY_COLORS[reliabilityPart] ?: MaterialTheme.colorScheme.onSurface
    val budgetColor = BUDGET_COLORS[budgetPart] ?: MaterialTheme.colorScheme.onSurface
    val separatorColor = LastGrey

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(color = reliabilityColor)) { append(reliabilityPart) }
                withStyle(SpanStyle(color = separatorColor)) { append(" / ") }
                withStyle(SpanStyle(color = budgetColor)) { append(budgetPart) }
            },
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun BalanceRow(row: LifetimeRow, dotColor: Color) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(120.dp).padding(vertical = 8.dp, horizontal = 4.dp)) {
            ScenarioLabel(row.label, dotColor)
        }
        Cell(formatBalance(row.balance1m), 70.dp)
        Cell(formatBalance(row.balance3m), 70.dp)
        Cell(formatBalance(row.balance6m), 70.dp)
        Cell(formatBalance(row.balance12m), 70.dp)
    }
}

@Composable
private fun SurvivalRow(row: LifetimeRow, dotColor: Color) {
    val months = row.monthsLeft
    val totalDays = if (months.isInfinite()) Long.MAX_VALUE else (months * 30.44).toLong()
    val timeLeft = if (months.isInfinite()) "∞" else {
        val m = totalDays / 30
        val d = totalDays % 30
        if (m > 0) "${m}m ${d}d" else "${d}d"
    }
    val finalDay = if (months.isInfinite()) "∞" else
        LocalDate.now().plusDays(totalDays).format(FINAL_DAY_FMT)

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.weight(1f).padding(vertical = 8.dp, horizontal = 4.dp)
        ) {
            ScenarioLabel(row.label, dotColor)
        }
        SurvivalCell(timeLeft)
        SurvivalCell(finalDay)
    }
}

@Composable
private fun RowScope.SurvivalCell(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.End,
        modifier = Modifier
            .weight(1f)
            .padding(vertical = 8.dp, horizontal = 4.dp)
    )
}

@Composable
private fun Cell(text: String, width: Dp) {
    val isNegative = text.startsWith("-")
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = if (isNegative) NegativeText else MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.End,
        modifier = Modifier
            .width(width)
            .padding(vertical = 8.dp, horizontal = 4.dp)
    )
}

private fun formatBalance(amount: Double): String =
    if (amount >= 0) "\$${amount.toLong()}" else "-\$${(-amount).toLong()}"
