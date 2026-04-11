package com.timeofmylife.ui.balances

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.data.model.Balance
import com.timeofmylife.ui.budget.SortOrder
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BalancesViewModel(private val repo: FinanceRepository) : ViewModel() {
    private val _sortOrder = MutableStateFlow(SortOrder.ALPHA)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val _ascending = MutableStateFlow(true)
    val ascending: StateFlow<Boolean> = _ascending.asStateFlow()

    val items: StateFlow<List<Balance>> =
        combine(repo.balances, _sortOrder, _ascending) { list, order, asc ->
            val sorted =
                when (order) {
                    SortOrder.ALPHA -> list.sortedBy { it.name.lowercase() }
                    SortOrder.AVG, SortOrder.LAST -> list.sortedBy { it.amount }
                }
            if (asc) sorted else sorted.reversed()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSortOrder(order: SortOrder) {
        if (_sortOrder.value == order) {
            _ascending.value = !_ascending.value
        } else {
            _sortOrder.value = order
            _ascending.value = true
        }
    }

    fun upsert(balance: Balance) = viewModelScope.launch { repo.upsertBalance(balance) }

    fun delete(balance: Balance) = viewModelScope.launch { repo.deleteBalance(balance) }

    class Factory(private val repo: FinanceRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) = BalancesViewModel(repo) as T
    }
}
