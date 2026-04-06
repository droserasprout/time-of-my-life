package com.timeofmylife.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.data.model.BudgetItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BudgetViewModel(private val repo: FinanceRepository) : ViewModel() {

    val items = repo.budgetItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun upsert(item: BudgetItem) = viewModelScope.launch { repo.upsertBudgetItem(item) }
    fun delete(item: BudgetItem) = viewModelScope.launch { repo.deleteBudgetItem(item) }

    class Factory(private val repo: FinanceRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) = BudgetViewModel(repo) as T
    }
}
