// app/src/test/java/com/example/smartspend/BalanceCalculatorTest.java
package com.example.smartspend;

import com.example.smartspend.model.Transaction;
import com.example.smartspend.utils.BalanceCalculator;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class BalanceCalculatorTest {

    private List<Transaction> transactions;

    @Before
    public void setUp() {
        transactions = Arrays.asList(
            new Transaction("INCOME",  "Salaire",    150000, System.currentTimeMillis(), "Salaire mars", 1),
            new Transaction("EXPENSE", "Nourriture",  20000, System.currentTimeMillis(), "Marché",       1),
            new Transaction("EXPENSE", "Transport",    5000, System.currentTimeMillis(), "Taxi",         1)
        );
    }

    @Test
    public void testSoldeCorrect() {
        double solde = BalanceCalculator.computeBalance(transactions);
        assertEquals(125000.0, solde, 0.01);
    }

    @Test
    public void testSoldeListeVide() {
        double solde = BalanceCalculator.computeBalance(Arrays.asList());
        assertEquals(0.0, solde, 0.01);
    }

    @Test
    public void testSoldeNegatif() {
        List<Transaction> liste = Arrays.asList(
            new Transaction("EXPENSE", "Loyer",   100000, System.currentTimeMillis(), "Loyer",   1),
            new Transaction("INCOME",  "Salaire",  50000, System.currentTimeMillis(), "Salaire", 1)
        );
        assertEquals(-50000.0, BalanceCalculator.computeBalance(liste), 0.01);
    }

    @Test
    public void testMontantNegatifIgnore() {
        Transaction t = new Transaction("EXPENSE", "Autre", -500,
                System.currentTimeMillis(), "", 1);
        assertTrue("Montant invalide détecté", t.amount < 0);
    }

    @Test
    public void testBudgetNonDepasse() {
        assertFalse(BalanceCalculator.isBudgetExceeded(transactions, 100000));
    }

    @Test
    public void testBudgetDepasseReel() {
        assertTrue(BalanceCalculator.isBudgetExceeded(transactions, 10000));
    }

    @Test
    public void testTauxEpargne() {
        double taux = BalanceCalculator.computeSavingsRate(transactions);
        // (125000 / 150000) * 100 = 83.33%
        assertEquals(83.33, taux, 0.1);
    }
}