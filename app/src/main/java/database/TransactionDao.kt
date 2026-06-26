package com.team.smartspend.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.team.smartspend.model.Transaction

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: Transaction)

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :debut AND :fin ORDER BY date DESC")
    fun getTransactionsByDate(debut: Long, fin: Long): LiveData<List<Transaction>>

    @Query("SELECT SUM(montant) FROM transactions WHERE type = 'REVENU'")
    suspend fun getTotalRevenus(): Double?

    @Query("SELECT SUM(montant) FROM transactions WHERE type = 'DEPENSE'")
    suspend fun getTotalDepenses(): Double?

    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT 5")
    fun getLastTransactions(): LiveData<List<Transaction>>

    // Récupérer une transaction précise (pour l'édition)
    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Transaction?

    // Total des dépenses d'un mois donné (pour la vérification du budget / notifications)
    // :debut et :fin sont les bornes (timestamps) du mois.
    @Query("SELECT SUM(montant) FROM transactions WHERE type = 'DEPENSE' AND date BETWEEN :debut AND :fin")
    suspend fun getTotalDepensesMois(debut: Long, fin: Long): Double?
}