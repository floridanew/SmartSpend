package com.team.smartspend.utils

import com.team.smartspend.model.Transaction

/**
 * BalanceCalculator — calculs financiers purs (Membre 4, adapté).
 *
 * Classe sans dépendance Android : elle est donc facilement TESTABLE
 * par des tests unitaires JUnit (voir BalanceCalculatorTest).
 */
object BalanceCalculator {

    /** Solde = somme des revenus − somme des dépenses. */
    fun computeBalance(transactions: List<Transaction>): Double {
        var solde = 0.0
        for (t in transactions) {
            if (t.type == "REVENU") solde += t.montant else solde -= t.montant
        }
        return solde
    }

    /** Total des dépenses. */
    fun totalDepenses(transactions: List<Transaction>): Double =
        transactions.filter { it.type == "DEPENSE" }.sumOf { it.montant }

    /** Total des revenus. */
    fun totalRevenus(transactions: List<Transaction>): Double =
        transactions.filter { it.type == "REVENU" }.sumOf { it.montant }

    /** Vrai si le total des dépenses dépasse le budget donné. */
    fun isBudgetExceeded(transactions: List<Transaction>, budget: Double): Boolean =
        totalDepenses(transactions) > budget

    /** Taux d'épargne = (revenus − dépenses) / revenus × 100 (0 si aucun revenu). */
    fun computeSavingsRate(transactions: List<Transaction>): Double {
        val revenus = totalRevenus(transactions)
        val depenses = totalDepenses(transactions)
        return if (revenus > 0) (revenus - depenses) / revenus * 100 else 0.0
    }
}
