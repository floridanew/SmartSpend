package com.team.smartspend.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.team.smartspend.database.AppDatabase
import com.team.smartspend.model.Transaction
import com.team.smartspend.repository.TransactionRepository
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository
    val allTransactions: LiveData<List<Transaction>>

    init {
        val dao = AppDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepository(dao)
        allTransactions = repository.allTransactions
    }

    fun insert(transaction: Transaction) = viewModelScope.launch {
        repository.insert(transaction)
    }

    fun update(transaction: Transaction) = viewModelScope.launch {
        repository.update(transaction)
    }

    fun delete(transaction: Transaction) = viewModelScope.launch {
        repository.delete(transaction)
    }

    fun getTotalRevenus(callback: (Double) -> Unit) = viewModelScope.launch {
        callback(repository.getTotalRevenus())
    }

    fun getTotalDepenses(callback: (Double) -> Unit) = viewModelScope.launch {
        callback(repository.getTotalDepenses())
    }

    // ---- Versions avec callback : exécutent une action une fois terminé ----

    fun insertTransaction(transaction: Transaction, onDone: () -> Unit) = viewModelScope.launch {
        repository.insert(transaction)
        onDone()
    }

    fun updateTransaction(transaction: Transaction, onDone: () -> Unit) = viewModelScope.launch {
        repository.update(transaction)
        onDone()
    }

    fun deleteTransaction(transaction: Transaction, onDone: () -> Unit) = viewModelScope.launch {
        repository.delete(transaction)
        onDone()
    }

    fun getById(id: Int, callback: (Transaction?) -> Unit) = viewModelScope.launch {
        callback(repository.getById(id))
    }

    fun getTotalDepensesMois(debut: Long, fin: Long, callback: (Double) -> Unit) = viewModelScope.launch {
        callback(repository.getTotalDepensesMois(debut, fin))
    }
}