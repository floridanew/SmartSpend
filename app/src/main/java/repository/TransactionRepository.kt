package com.team.smartspend.repository

import androidx.lifecycle.LiveData
import com.team.smartspend.database.TransactionDao
import com.team.smartspend.model.Transaction

class TransactionRepository(private val transactionDao: TransactionDao) {

    val allTransactions: LiveData<List<Transaction>> = transactionDao.getAllTransactions()

    suspend fun insert(transaction: Transaction) {
        transactionDao.insert(transaction)
    }

    suspend fun update(transaction: Transaction) {
        transactionDao.update(transaction)
    }

    suspend fun delete(transaction: Transaction) {
        transactionDao.delete(transaction)
    }

    suspend fun getTotalRevenus(): Double {
        return transactionDao.getTotalRevenus() ?: 0.0
    }

    suspend fun getTotalDepenses(): Double {
        return transactionDao.getTotalDepenses() ?: 0.0
    }
}