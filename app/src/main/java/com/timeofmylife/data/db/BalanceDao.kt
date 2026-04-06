package com.timeofmylife.data.db

import androidx.room.*
import com.timeofmylife.data.model.Balance
import kotlinx.coroutines.flow.Flow

@Dao
interface BalanceDao {
    @Query("SELECT * FROM balances")
    fun observeAll(): Flow<List<Balance>>

    @Query("SELECT * FROM balances")
    suspend fun getAll(): List<Balance>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(balance: Balance): Long

    @Delete
    suspend fun delete(balance: Balance)

    @Query("DELETE FROM balances")
    suspend fun deleteAll()
}
