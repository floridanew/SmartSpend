package com.team.smartspend

import com.team.smartspend.model.Transaction
import com.team.smartspend.utils.BalanceCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Tests unitaires des calculs de solde (Membre 4, adapté au modèle de l'équipe).
 *
 * Ces tests s'exécutent sur la JVM (rapides, sans émulateur) :
 *   clic droit sur le fichier -> "Run BalanceCalculatorTest".
 */
class BalanceCalculatorTest {

    private lateinit var transactions: List<Transaction>

    private fun tx(type: String, categorie: String, montant: Double) =
        Transaction(montant = montant, type = type, categorie = categorie, date = 0L)

    @Before
    fun setUp() {
        transactions = listOf(
            tx("REVENU", "Salaire", 150000.0),
            tx("DEPENSE", "Nourriture", 20000.0),
            tx("DEPENSE", "Transport", 5000.0)
        )
    }

    @Test
    fun testSoldeCorrect() {
        // 150000 - 20000 - 5000 = 125000
        assertEquals(125000.0, BalanceCalculator.computeBalance(transactions), 0.01)
    }

    @Test
    fun testSoldeListeVide() {
        assertEquals(0.0, BalanceCalculator.computeBalance(emptyList()), 0.01)
    }

    @Test
    fun testSoldeNegatif() {
        val liste = listOf(
            tx("DEPENSE", "Loyer", 100000.0),
            tx("REVENU", "Salaire", 50000.0)
        )
        assertEquals(-50000.0, BalanceCalculator.computeBalance(liste), 0.01)
    }

    @Test
    fun testBudgetNonDepasse() {
        // Dépenses = 25000, budget = 100000 -> pas dépassé
        assertFalse(BalanceCalculator.isBudgetExceeded(transactions, 100000.0))
    }

    @Test
    fun testBudgetDepasseReel() {
        // Dépenses = 25000, budget = 10000 -> dépassé
        assertTrue(BalanceCalculator.isBudgetExceeded(transactions, 10000.0))
    }

    @Test
    fun testTauxEpargne() {
        // (150000 - 25000) / 150000 * 100 = 83.33 %
        assertEquals(83.33, BalanceCalculator.computeSavingsRate(transactions), 0.1)
    }
}
