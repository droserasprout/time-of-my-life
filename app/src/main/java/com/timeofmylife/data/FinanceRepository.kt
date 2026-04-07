package com.timeofmylife.data

import com.timeofmylife.data.db.BalanceDao
import com.timeofmylife.data.db.BudgetItemDao
import com.timeofmylife.data.json.*
import com.timeofmylife.data.model.Balance
import com.timeofmylife.data.model.BudgetItem
import kotlinx.coroutines.flow.Flow

class FinanceRepository(
    private val balanceDao: BalanceDao,
    private val budgetItemDao: BudgetItemDao
) {
    val balances: Flow<List<Balance>> = balanceDao.observeAll()
    val budgetItems: Flow<List<BudgetItem>> = budgetItemDao.observeAll()

    suspend fun upsertBalance(balance: Balance) = balanceDao.upsert(balance)
    suspend fun deleteBalance(balance: Balance) = balanceDao.delete(balance)
    suspend fun upsertBudgetItem(item: BudgetItem) = budgetItemDao.upsert(item)
    suspend fun deleteBudgetItem(item: BudgetItem) = budgetItemDao.delete(item)

    suspend fun exportData(): AppDataExport = AppDataExport(
        balances = balanceDao.getAll().map { it.toExport() },
        budgetItems = budgetItemDao.getAll().map { it.toExport() }
    )

    suspend fun importData(data: AppDataExport) {
        balanceDao.deleteAll()
        budgetItemDao.deleteAll()
        data.balances.forEach { balanceDao.upsert(it.toEntity()) }
        data.budgetItems.forEach { budgetItemDao.upsert(it.toEntity()) }
    }

    suspend fun getAllBalances(): List<Balance> = balanceDao.getAll()
    suspend fun getAllBudgetItems(): List<BudgetItem> = budgetItemDao.getAll()

    suspend fun importRaw(balances: List<Balance>, budgetItems: List<BudgetItem>) {
        balanceDao.deleteAll()
        budgetItemDao.deleteAll()
        balances.forEach { balanceDao.upsert(it) }
        budgetItems.forEach { budgetItemDao.upsert(it) }
    }
}
