package com.timeofmylife.domain

import com.timeofmylife.data.model.*

data class LifetimeRow(
    val label: String,
    val balance1m: Double,
    val balance3m: Double,
    val balance6m: Double,
    val balance12m: Double,
    val monthsLeft: Double   // Double.POSITIVE_INFINITY means income >= expenses
)

object LifetimeCalculator {

    private data class ScenarioDef(
        val tiers: Set<Reliability>,
        val useGood: Boolean,
        val label: String
    )

    private val SCENARIOS = listOf(
        ScenarioDef(setOf(Reliability.HIGH), false, "high / worst"),
        ScenarioDef(setOf(Reliability.HIGH), true, "high / best"),
        ScenarioDef(setOf(Reliability.HIGH, Reliability.MEDIUM), false, "medium / worst"),
        ScenarioDef(setOf(Reliability.HIGH, Reliability.MEDIUM), true, "medium / best"),
        ScenarioDef(setOf(Reliability.HIGH, Reliability.MEDIUM, Reliability.LOW), false, "low / worst"),
        ScenarioDef(setOf(Reliability.HIGH, Reliability.MEDIUM, Reliability.LOW), true, "low / best"),
    )

    fun calculate(balances: List<Balance>, budgetItems: List<BudgetItem>, includeIncome: Boolean = true): List<LifetimeRow> =
        SCENARIOS.map { scenario ->
            val totalBalance = balances
                .filter { it.reliability in scenario.tiers }
                .sumOf { it.amount }

            val totalExpenses = budgetItems
                .filter { it.type == ItemType.EXPENSE }
                .sumOf { if (scenario.useGood) it.goodAmount else it.badAmount }

            val totalIncomes = if (includeIncome) budgetItems
                .filter { it.type == ItemType.INCOME }
                .sumOf { if (scenario.useGood) it.goodAmount else it.badAmount }
            else 0.0

            val netBurn = totalExpenses - totalIncomes

            val monthsLeft = if (netBurn <= 0) Double.POSITIVE_INFINITY
                             else totalBalance / netBurn

            LifetimeRow(
                label = scenario.label,
                balance1m = totalBalance - netBurn * 1,
                balance3m = totalBalance - netBurn * 3,
                balance6m = totalBalance - netBurn * 6,
                balance12m = totalBalance - netBurn * 12,
                monthsLeft = monthsLeft
            )
        }
}
