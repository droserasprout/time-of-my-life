package com.timeofmylife.data.json

import com.timeofmylife.data.model.*
import kotlinx.serialization.Serializable

@Serializable
data class BalanceExport(
    val name: String,
    val reliability: String,
    val amount: Double,
)

@Serializable
data class BudgetItemExport(
    val name: String,
    val type: String,
    val bestAmount: Double,
    val worstAmount: Double,
    val lastAmount: Double = 0.0,
)

@Serializable
data class AppDataExport(
    val balances: List<BalanceExport>,
    val budgetItems: List<BudgetItemExport>,
)

fun Balance.toExport() = BalanceExport(name, reliability.name, amount)

fun BudgetItem.toExport() = BudgetItemExport(name, type.name, bestAmount, worstAmount, lastAmount)

fun BalanceExport.toEntity() =
    Balance(
        name = name,
        reliability = Reliability.valueOf(reliability),
        amount = amount,
    )

fun BudgetItemExport.toEntity() =
    BudgetItem(
        name = name,
        type = ItemType.valueOf(type),
        bestAmount = bestAmount,
        worstAmount = worstAmount,
        lastAmount = lastAmount,
    )
