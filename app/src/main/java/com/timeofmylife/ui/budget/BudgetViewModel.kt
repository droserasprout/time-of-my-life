package com.timeofmylife.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.data.model.BudgetItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortOrder { ALPHA, AVG, LAST }

class BudgetViewModel(private val repo: FinanceRepository) : ViewModel() {

    private val _sortOrder = MutableStateFlow(SortOrder.ALPHA)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val _ascending = MutableStateFlow(true)
    val ascending: StateFlow<Boolean> = _ascending.asStateFlow()

    private val rawItems = repo.budgetItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val items: StateFlow<List<BudgetItem>> = combine(rawItems, _sortOrder, _ascending) { list, order, asc ->
        val sorted = when (order) {
            SortOrder.ALPHA -> list.sortedBy { it.name.lowercase() }
            SortOrder.AVG -> list.sortedBy { (it.goodAmount + it.badAmount) / 2.0 }
            SortOrder.LAST -> list.sortedBy { it.lastAmount }
        }
        if (asc) sorted else sorted.reversed()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSortOrder(order: SortOrder) {
        if (_sortOrder.value == order) _ascending.value = !_ascending.value
        else { _sortOrder.value = order; _ascending.value = true }
    }

    fun upsert(item: BudgetItem) = viewModelScope.launch { repo.upsertBudgetItem(item) }
    fun delete(item: BudgetItem) = viewModelScope.launch { repo.deleteBudgetItem(item) }

    class Factory(private val repo: FinanceRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) = BudgetViewModel(repo) as T
    }
}
