package com.timeofmylife.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Reliability { HIGH, MEDIUM, LOW }

@Entity(tableName = "balances")
data class Balance(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val reliability: Reliability,
    val amount: Double
)
