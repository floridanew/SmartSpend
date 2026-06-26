package com.team.smartspend.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieEntry
import com.team.smartspend.database.AppDatabase
import com.team.smartspend.model.Transaction
import com.team.smartspend.repository.TransactionRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Petit conteneur qui regroupe les chiffres affichés dans le résumé textuel
 * de l'écran Statistiques (revenus, dépenses, solde, etc.).
 */
data class StatsSummary(
    val totalRevenus: Double = 0.0,
    val totalDepenses: Double = 0.0,
    val soldeNet: Double = 0.0,
    val moyenneDepenses: Double = 0.0,
    val tauxEpargne: Double = 0.0,
    val categorieLaPlusDepensiere: String = "-"
)

/**
 * ViewModel des statistiques.
 *
 * Il observe TOUTES les transactions de la base (via le repository de l'équipe)
 * et recalcule automatiquement les statistiques à chaque changement, grâce à LiveData.
 * Les écrans n'ont donc qu'à "observer" les LiveData exposées ici.
 */
class StatsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository

    /** La source : toutes les transactions, observée en temps réel. */
    val allTransactions: LiveData<List<Transaction>>

    init {
        val dao = AppDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepository(dao)
        allTransactions = repository.allTransactions
    }

    // ---------------------------------------------------------------------
    //  1) DONNÉES POUR LES GRAPHIQUES (MPAndroidChart)
    // ---------------------------------------------------------------------

    /**
     * Données du PieChart : le total des DÉPENSES regroupé par catégorie.
     * Chaque part du camembert = une catégorie.
     */
    val pieEntries: LiveData<List<PieEntry>> = allTransactions.map { transactions ->
        transactions
            .filter { it.type == "DEPENSE" }
            .groupBy { it.categorie }
            .map { (categorie, list) ->
                val totalCategorie = list.sumOf { it.montant }
                PieEntry(totalCategorie.toFloat(), categorie)
            }
    }

    /**
     * Données du BarChart : l'évolution mensuelle des revenus ET des dépenses.
     * Pour chaque mois, on a deux barres (revenus / dépenses).
     *
     * On renvoie un objet prêt à l'emploi : les libellés des mois (axe X),
     * la liste des barres de revenus et la liste des barres de dépenses.
     */
    val barData: LiveData<MonthlyBarData> = allTransactions.map { transactions ->
        construireDonneesMensuelles(transactions)
    }

    // ---------------------------------------------------------------------
    //  2) RÉSUMÉ TEXTUEL (revenus, dépenses, solde, taux d'épargne...)
    // ---------------------------------------------------------------------

    /**
     * Le résumé chiffré, recalculé automatiquement à chaque changement.
     */
    val summary: LiveData<StatsSummary> = allTransactions.map { transactions ->
        val revenus = transactions.filter { it.type == "REVENU" }
        val depenses = transactions.filter { it.type == "DEPENSE" }

        val totalRevenus = revenus.sumOf { it.montant }
        val totalDepenses = depenses.sumOf { it.montant }
        val soldeNet = totalRevenus - totalDepenses

        // Moyenne des dépenses (évite la division par zéro s'il n'y a aucune dépense)
        val moyenneDepenses = if (depenses.isNotEmpty()) {
            totalDepenses / depenses.size
        } else {
            0.0
        }

        // Taux d'épargne = (revenus - dépenses) / revenus * 100
        val tauxEpargne = if (totalRevenus > 0) {
            (totalRevenus - totalDepenses) / totalRevenus * 100
        } else {
            0.0
        }

        // Catégorie où l'on dépense le plus
        val categorieMax = depenses
            .groupBy { it.categorie }
            .maxByOrNull { (_, list) -> list.sumOf { it.montant } }
            ?.key ?: "-"

        StatsSummary(
            totalRevenus = totalRevenus,
            totalDepenses = totalDepenses,
            soldeNet = soldeNet,
            moyenneDepenses = moyenneDepenses,
            tauxEpargne = tauxEpargne,
            categorieLaPlusDepensiere = categorieMax
        )
    }

    // ---------------------------------------------------------------------
    //  Fonctions utilitaires internes
    // ---------------------------------------------------------------------

    /**
     * Regroupe les transactions par mois (format "yyyy-MM") et calcule,
     * pour chaque mois, le total des revenus et le total des dépenses.
     */
    private fun construireDonneesMensuelles(transactions: List<Transaction>): MonthlyBarData {
        val formatMois = SimpleDateFormat("yyyy-MM", Locale.FRANCE)

        // Map : "2026-06" -> liste de transactions de ce mois
        val parMois = transactions.groupBy { transaction ->
            formatMois.format(java.util.Date(transaction.date))
        }.toSortedMap() // trie les mois dans l'ordre chronologique

        val labels = mutableListOf<String>()
        val revenusEntries = mutableListOf<BarEntry>()
        val depensesEntries = mutableListOf<BarEntry>()

        parMois.entries.forEachIndexed { index, (mois, list) ->
            val totalRevenus = list.filter { it.type == "REVENU" }.sumOf { it.montant }
            val totalDepenses = list.filter { it.type == "DEPENSE" }.sumOf { it.montant }

            labels.add(mois)
            revenusEntries.add(BarEntry(index.toFloat(), totalRevenus.toFloat()))
            depensesEntries.add(BarEntry(index.toFloat(), totalDepenses.toFloat()))
        }

        return MonthlyBarData(labels, revenusEntries, depensesEntries)
    }
}

/**
 * Conteneur prêt pour le BarChart : les libellés de mois (axe X)
 * et les deux séries de barres (revenus et dépenses).
 */
data class MonthlyBarData(
    val labels: List<String>,
    val revenus: List<BarEntry>,
    val depenses: List<BarEntry>
)
