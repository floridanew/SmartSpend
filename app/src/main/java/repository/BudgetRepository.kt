package com.team.smartspend.repository

import com.team.smartspend.database.BudgetDao
import com.team.smartspend.model.Budget

class BudgetRepository(private val budgetDao: BudgetDao) {

    /**
     * Définit le budget d'un mois. Si un budget existe déjà pour ce mois,
     * on le met à jour (on garde son id) au lieu d'en créer un doublon.
     */
    suspend fun definir(mois: String, montantMax: Double) {
        val existant = budgetDao.getBudgetByMois(mois)
        if (existant != null) {
            budgetDao.insert(existant.copy(montantMax = montantMax))
        } else {
            budgetDao.insert(Budget(montantMax = montantMax, mois = mois))
        }
    }

    suspend fun getBudget(mois: String): Budget? {
        return budgetDao.getBudgetByMois(mois)
    }
}
