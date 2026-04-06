package com.timeofmylife.data.db

import androidx.room.*
import com.timeofmylife.data.model.BudgetItem
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetItemDao {
    @Query("SELECT * FROM budget_items")
    fun observeAll(): Flow<List<BudgetItem>>

    @Query("SELECT * FROM budget_items")
    suspend fun getAll(): List<BudgetItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: BudgetItem): Long

    @Delete
    suspend fun delete(item: BudgetItem)

    @Query("DELETE FROM budget_items")
    suspend fun deleteAll()
}
