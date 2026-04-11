package com.timeofmylife.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ItemType { EXPENSE, INCOME }

@Entity(tableName = "budget_items")
data class BudgetItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: ItemType,
    val bestAmount: Double,
    val worstAmount: Double,
    val lastAmount: Double = 0.0,
)
