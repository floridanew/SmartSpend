package com.team.smartspend.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.team.smartspend.database.AppDatabase
import com.team.smartspend.model.Budget
import com.team.smartspend.repository.BudgetRepository
import kotlinx.coroutines.launch

class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BudgetRepository

    init {
        val dao = AppDatabase.getDatabase(application).budgetDao()
        repository = BudgetRepository(dao)
    }

    fun definirBudget(mois: String, montantMax: Double, onDone: () -> Unit) = viewModelScope.launch {
        repository.definir(mois, montantMax)
        onDone()
    }

    fun getBudget(mois: String, callback: (Budget?) -> Unit) = viewModelScope.launch {
        callback(repository.getBudget(mois))
    }
}
