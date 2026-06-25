package com.team.smartspend.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.team.smartspend.database.AppDatabase
import com.team.smartspend.model.Transaction
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

data class DashboardUiState(
    val solde: Double = 0.0,
    val revenus: Double = 0.0,
    val depenses: Double = 0.0,
    val budgetMax: Double = 0.0,
    val budgetUtilisePourcent: Int = 0,
    val budgetDepasse: Boolean = false
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val transactionDao = AppDatabase.getDatabase(application).transactionDao()
    private val budgetDao = AppDatabase.getDatabase(application).budgetDao()

    val lastTransactions: LiveData<List<Transaction>> = transactionDao.getLastTransactions()

    private val _uiState = MutableLiveData(DashboardUiState())
    val uiState: LiveData<DashboardUiState> = _uiState

    fun loadDashboard() {
        viewModelScope.launch {
            val revenus = transactionDao.getTotalRevenus() ?: 0.0
            val depenses = transactionDao.getTotalDepenses() ?: 0.0
            val solde = revenus - depenses

            val moisActuel = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(java.util.Date())
            val budget = budgetDao.getBudgetByMois(moisActuel)
            val budgetMax = budget?.montantMax ?: 0.0

            val pourcent = if (budgetMax > 0) {
                ((depenses / budgetMax) * 100).toInt()
            } else 0

            _uiState.postValue(
                DashboardUiState(
                    solde = solde,
                    revenus = revenus,
                    depenses = depenses,
                    budgetMax = budgetMax,
                    budgetUtilisePourcent = pourcent.coerceAtMost(999),
                    budgetDepasse = budgetMax > 0 && depenses > budgetMax
                )
            )
        }
    }
}