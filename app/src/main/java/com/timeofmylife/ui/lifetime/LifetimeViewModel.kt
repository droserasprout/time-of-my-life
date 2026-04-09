package com.timeofmylife.ui.lifetime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.domain.LifetimeCalculator
import com.timeofmylife.domain.LifetimeRow
import kotlinx.coroutines.flow.*

enum class BudgetMode { ALL, EXPENSE, INCOME }

class LifetimeViewModel(repo: FinanceRepository) : ViewModel() {

    private val _budgetMode = MutableStateFlow(BudgetMode.ALL)
    val budgetMode: StateFlow<BudgetMode> = _budgetMode

    fun setBudgetMode(mode: BudgetMode) { _budgetMode.value = mode }

    val rows: StateFlow<List<LifetimeRow>> = combine(repo.balances, repo.budgetItems, _budgetMode) { balances, items, mode ->
        LifetimeCalculator.calculate(
            balances, items,
            includeIncome = mode != BudgetMode.EXPENSE,
            includeExpenses = mode != BudgetMode.INCOME
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    class Factory(private val repo: FinanceRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) = LifetimeViewModel(repo) as T
    }
}
