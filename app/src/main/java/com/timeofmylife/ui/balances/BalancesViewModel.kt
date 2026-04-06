package com.timeofmylife.ui.balances

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.timeofmylife.data.FinanceRepository
import com.timeofmylife.data.model.Balance
import com.timeofmylife.data.model.Reliability
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BalancesViewModel(private val repo: FinanceRepository) : ViewModel() {

    // Groups balances by reliability in declaration order: HIGH, MEDIUM, LOW
    val grouped: StateFlow<Map<Reliability, List<Balance>>> = repo.balances
        .map { list ->
            Reliability.entries.associateWith { r ->
                list.filter { it.reliability == r }.sortedBy { it.name }
            }.filterValues { it.isNotEmpty() }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun upsert(balance: Balance) = viewModelScope.launch { repo.upsertBalance(balance) }
    fun delete(balance: Balance) = viewModelScope.launch { repo.deleteBalance(balance) }

    class Factory(private val repo: FinanceRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) = BalancesViewModel(repo) as T
    }
}
