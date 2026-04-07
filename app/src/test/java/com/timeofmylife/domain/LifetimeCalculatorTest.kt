package com.timeofmylife.domain

import com.timeofmylife.data.model.*
import kotlin.test.Test
import kotlin.test.assertEquals

class LifetimeCalculatorTest {

    private val balances = listOf(
        Balance(name = "Bank", reliability = Reliability.HIGH, amount = 200.0),
        Balance(name = "Broker", reliability = Reliability.MEDIUM, amount = 500.0),
        Balance(name = "Crypto", reliability = Reliability.LOW, amount = 1000.0),
    )
    private val budgetItems = listOf(
        BudgetItem(name = "Food", type = ItemType.EXPENSE, goodAmount = 100.0, badAmount = 250.0),
        BudgetItem(name = "Rent", type = ItemType.EXPENSE, goodAmount = 200.0, badAmount = 300.0),
        BudgetItem(name = "Salary", type = ItemType.INCOME, goodAmount = 600.0, badAmount = 375.0),
    )

    @Test
    fun `returns exactly 6 rows`() {
        val rows = LifetimeCalculator.calculate(balances, budgetItems)
        assertEquals(6, rows.size)
    }

    @Test
    fun `row 0 is H-bad - uses only HIGH balance and bad amounts`() {
        // HIGH = $200
        // expenses bad = $250 + $300 = $550; incomes bad = $375; netBurn = $175
        // balance1m = $200 - $175 = $25
        // monthsLeft = $200 / $175 ≈ 1.14
        val row = LifetimeCalculator.calculate(balances, budgetItems)[0]
        assertEquals(25.0, row.balance1m, 0.01)
        assertEquals(-325.0, row.balance3m, 0.01)
        assertEquals(200.0 / 175.0, row.monthsLeft, 0.01)
    }

    @Test
    fun `row 1 is H-good - netBurn is negative so monthsLeft is infinity`() {
        // HIGH = $200; expenses good = $100 + $200 = $300; incomes good = $600
        // netBurn = $300 - $600 = -$300 (income > expenses)
        // monthsLeft = ∞; balance1m = $200 - (-$300) = $500
        val row = LifetimeCalculator.calculate(balances, budgetItems)[1]
        assertEquals(Double.POSITIVE_INFINITY, row.monthsLeft)
        assertEquals(500.0, row.balance1m, 0.01)
    }

    @Test
    fun `row 4 is HML-bad - uses all balances`() {
        // ALL = $200 + $500 + $1000 = $1700; netBurn bad = $175
        // balance1m = $1700 - $175 = $1525
        // monthsLeft = $1700 / $175 ≈ 9.71
        val row = LifetimeCalculator.calculate(balances, budgetItems)[4]
        assertEquals(1525.0, row.balance1m, 0.01)
        assertEquals(1700.0 / 175.0, row.monthsLeft, 0.01)
    }

    @Test
    fun `empty inputs produce zero balances and infinite months`() {
        val rows = LifetimeCalculator.calculate(emptyList(), emptyList())
        rows.forEach { row ->
            assertEquals(0.0, row.balance1m, 0.0)
            assertEquals(0.0, row.balance12m, 0.0)
            assertEquals(Double.POSITIVE_INFINITY, row.monthsLeft)
        }
    }

    @Test
    fun `row labels are correct`() {
        val rows = LifetimeCalculator.calculate(balances, budgetItems)
        assertEquals("high / worst", rows[0].label)
        assertEquals("high / best", rows[1].label)
        assertEquals("medium / worst", rows[2].label)
        assertEquals("medium / best", rows[3].label)
        assertEquals("low / worst", rows[4].label)
        assertEquals("low / best", rows[5].label)
    }
}
