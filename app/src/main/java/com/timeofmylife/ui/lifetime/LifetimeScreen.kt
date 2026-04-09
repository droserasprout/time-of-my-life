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
import com.timeofmylife.ui.SegmentedSelector
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
import com.timeofmylife.ui.LocalDemoMode
import com.timeofmylife.ui.formatAmount
import com.timeofmylife.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.ceil
import kotlin.math.max

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
    val budgetMode by vm.budgetMode.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = innerPadding.calculateTopPadding(), bottom = innerPadding.calculateBottomPadding())
    ) {
        // Scrollable tables
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Budget mode selector
            SegmentedSelector(
                options = BudgetMode.entries.toList(),
                selected = budgetMode,
                onSelect = { vm.setBudgetMode(it) },
                label = { it.name.lowercase() }
            )

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

            // Survival table — full width
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    SurvivalHeaderCell(SURVIVAL_COLUMNS[0], isLabel = true)
                    SurvivalHeaderCell(SURVIVAL_COLUMNS[1])
                    SurvivalHeaderCell(SURVIVAL_COLUMNS[2])
                }
                rows.forEachIndexed { index, row ->
                    SurvivalRow(row, LifetimeRowColors.getOrElse(index) { Color.Transparent })
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                }
            }
        }

        // Coverage bar pinned to bottom
        LifetimeCoverageBar(rows)
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
    val demo = LocalDemoMode.current
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(120.dp).padding(vertical = 8.dp, horizontal = 4.dp)) {
            ScenarioLabel(row.label, dotColor)
        }
        Cell(formatAmount(row.balance1m, demo), 70.dp)
        Cell(formatAmount(row.balance3m, demo), 70.dp)
        Cell(formatAmount(row.balance6m, demo), 70.dp)
        Cell(formatAmount(row.balance12m, demo), 70.dp)
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

private data class BarSegments(
    val highYears: Double,
    val medYears: Double,
    val lowYears: Double,
    val totalYears: Double,
    val isInfinite: Boolean
)

private fun computeBarSegments(
    rows: List<LifetimeRow>,
    highIdx: Int,
    medIdx: Int,
    lowIdx: Int
): BarSegments {
    if (rows.size < 6) return BarSegments(0.0, 0.0, 0.0, 0.0, false)

    val highMonths = rows[highIdx].monthsLeft
    val medMonths = rows[medIdx].monthsLeft
    val lowMonths = rows[lowIdx].monthsLeft

    if (highMonths.isInfinite()) return BarSegments(0.0, 0.0, 0.0, 0.0, true)

    val highYears = highMonths / 12.0
    val medYearsTotal = if (medMonths.isInfinite()) highYears else medMonths / 12.0
    val lowYearsTotal = if (lowMonths.isInfinite()) medYearsTotal else lowMonths / 12.0

    val medYears = max(0.0, medYearsTotal - highYears)
    val lowYears = max(0.0, lowYearsTotal - medYearsTotal)
    val totalYears = highYears + medYears + lowYears

    return BarSegments(highYears, medYears, lowYears, totalYears, false)
}

@Composable
private fun LifetimeCoverageBar(rows: List<LifetimeRow>) {
    if (rows.size < 6) return

    val currentYear = LocalDate.now().year

    val worst = computeBarSegments(rows, 0, 2, 4)
    val best = computeBarSegments(rows, 1, 3, 5)

    val bothInfinite = worst.isInfinite && best.isInfinite
    val maxYears = when {
        bothInfinite -> 1.0
        worst.isInfinite -> best.totalYears.coerceAtLeast(1.0)
        best.isInfinite -> worst.totalYears.coerceAtLeast(1.0)
        else -> max(worst.totalYears, best.totalYears).coerceAtLeast(1.0)
    }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        // Year labels or infinity
        if (bothInfinite) {
            Text(
                text = "∞",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(start = 40.dp)
            )
        } else {
            YearLabels(currentYear, maxYears)
        }
        Spacer(modifier = Modifier.height(2.dp))

        SegmentedBar("worst", worst, maxYears)
        Spacer(modifier = Modifier.height(4.dp))
        SegmentedBar("best", best, maxYears)

        Spacer(modifier = Modifier.height(6.dp))
    }
}

@Composable
private fun YearLabels(currentYear: Int, maxYears: Double) {
    val endYear = currentYear + ceil(maxYears).toInt()
    val span = endYear - currentYear
    val step = max(1, (span + 3) / 5) // ~5 labels
    val years = (currentYear..endYear step step).toList().let {
        if (it.last() != endYear && span > 1) it + endYear else it
    }

    Row(modifier = Modifier.padding(start = 40.dp)) {
        years.forEachIndexed { i, year ->
            val yearLabel = "'${(year % 100).toString().padStart(2, '0')}"
            if (i > 0) Spacer(modifier = Modifier.weight(1f))
            Text(
                text = yearLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
private fun SegmentedBar(label: String, segments: BarSegments, maxYears: Double) {
    val barHeight = 14.dp
    val shape = MaterialTheme.shapes.small

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (label == "worst") WorstOrange else BestBlue,
            modifier = Modifier.width(40.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(barHeight)
                .clip(shape)
                .background(if (segments.isInfinite) HighColor else UncoveredDark)
        ) {
            if (!segments.isInfinite) {
                Row(modifier = Modifier.matchParentSize()) {
                    val parts = listOf(
                        segments.highYears to HighColor,
                        segments.medYears to MediumColor,
                        segments.lowYears to LowColor
                    )
                    parts.forEach { (years, color) ->
                        if (years > 0.001) {
                            Box(
                                modifier = Modifier
                                    .weight((years / maxYears).toFloat())
                                    .fillMaxHeight()
                                    .background(color)
                            )
                        }
                    }
                    val uncovered = maxYears - segments.totalYears
                    if (uncovered > 0.001) {
                        Spacer(modifier = Modifier.weight((uncovered / maxYears).toFloat()))
                    }
                }
            }
        }
    }
}

