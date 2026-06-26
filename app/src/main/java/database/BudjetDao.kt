package com.team.smartspend.database

import androidx.room.*
import com.team.smartspend.model.Budget

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: Budget)

    @Query("SELECT * FROM budgets WHERE mois = :mois LIMIT 1")
    suspend fun getBudgetByMois(mois: String): Budget?

    @Delete
    suspend fun delete(budget: Budget)
}