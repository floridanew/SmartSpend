package com.team.smartspend.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.team.smartspend.database.AppDatabase
import com.team.smartspend.model.Transaction

/**
 * HistoryViewModel — alimente l'écran Historique (Membre 4, adapté).
 *
 * Selon l'état des filtres, il choisit automatiquement la bonne requête Room :
 *   - une plage de dates active     -> getTransactionsByDate
 *   - une recherche active          -> searchTransactions
 *   - aucun filtre                  -> getAllTransactions
 *
 * Le tout est réactif : la liste se met à jour en temps réel via LiveData.
 */
class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).transactionDao()

    // État des filtres (déclencheurs)
    private val dateRange = MutableLiveData<Pair<Long, Long>?>(null)
    private val searchQuery = MutableLiveData<String>("")

    // Liste finale exposée à l'écran : recalculée dès qu'un filtre change.
    val transactions: LiveData<List<Transaction>> = dateRange.switchMap { range ->
        if (range != null) {
            // Filtre par plage de dates prioritaire
            dao.getTransactionsByDate(range.first, range.second)
        } else {
            val q = searchQuery.value ?: ""
            if (q.isNotEmpty()) dao.searchTransactions(q)
            else dao.getAllTransactions()
        }
    }

    /** Filtre par plage de dates (date début / date fin). */
    fun filterByDateRange(start: Long, end: Long) {
        dateRange.value = Pair(start, end)
    }

    /** Réinitialise tous les filtres (réaffiche tout). */
    fun resetFilter() {
        searchQuery.value = ""
        dateRange.value = null
    }

    /** Recherche en temps réel (sur description + catégorie). */
    fun search(query: String) {
        searchQuery.value = query
        // Une recherche annule le filtre de date ; comme la liste dépend de
        // dateRange, on lui réassigne null pour forcer le recalcul.
        dateRange.value = null
    }
}
