package com.timeofmylife.ui.lifetime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.domain.LifetimeCalculator
import com.timeofmylife.domain.LifetimeRow
import kotlinx.coroutines.flow.*

class LifetimeViewModel(repo: FinanceRepository) : ViewModel() {

    val rows: StateFlow<List<LifetimeRow>> = combine(repo.balances, repo.budgetItems) { balances, items ->
        LifetimeCalculator.calculate(balances, items)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    class Factory(private val repo: FinanceRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) = LifetimeViewModel(repo) as T
    }
}
