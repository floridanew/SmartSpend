package com.team.smartspend.view

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team.smartspend.R
import com.team.smartspend.model.Transaction
import com.team.smartspend.utils.ExportUtils
import com.team.smartspend.viewmodel.HistoryViewModel
import java.util.Calendar

/**
 * HistoryActivity — écran Historique (Membre 4, adapté en Kotlin).
 *
 * Fonctionnalités : liste (RecyclerView triée par date décroissante), recherche
 * temps réel, filtre par plage de dates, export CSV/PDF, et clic sur une
 * transaction pour la modifier (réutilise les écrans du Membre 3).
 */
class HistoryActivity : AppCompatActivity() {

    private lateinit var viewModel: HistoryViewModel
    private lateinit var adapter: HistoryAdapter

    private var startDate: Long = -1
    private var endDate: Long = -1

    // Dernière liste affichée (sert à l'export)
    private var listeAffichee: List<Transaction> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        viewModel = ViewModelProvider(this)[HistoryViewModel::class.java]

        // RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_history)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = HistoryAdapter()
        recyclerView.adapter = adapter

        // Observer la liste (réactif)
        viewModel.transactions.observe(this) { liste ->
            listeAffichee = liste
            adapter.setTransactions(liste)
        }

        // Clic sur une transaction -> édition (écrans du Membre 3)
        adapter.setOnTransactionClickListener { t -> ouvrirEdition(t) }

        // Recherche en temps réel
        val searchView = findViewById<SearchView>(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.search(newText ?: "")
                return true
            }
        })

        // Filtre par date
        val btnStart = findViewById<Button>(R.id.btn_start_date)
        val btnEnd = findViewById<Button>(R.id.btn_end_date)
        val btnReset = findViewById<Button>(R.id.btn_reset_filter)

        btnStart.setOnClickListener { pickDate(true, btnStart) }
        btnEnd.setOnClickListener { pickDate(false, btnEnd) }
        btnReset.setOnClickListener {
            startDate = -1; endDate = -1
            btnStart.text = "Date début"
            btnEnd.text = "Date fin"
            searchView.setQuery("", false)
            viewModel.resetFilter()
        }

        // Export
        findViewById<Button>(R.id.btn_export_csv).setOnClickListener {
            val ok = ExportUtils.exportCSV(this, listeAffichee)
            Toast.makeText(this,
                if (ok) "✅ Export CSV réussi (dossier Téléchargements)" else "❌ Échec de l'export CSV",
                Toast.LENGTH_LONG).show()
        }
        findViewById<Button>(R.id.btn_export_pdf).setOnClickListener {
            val ok = ExportUtils.exportPDF(this, listeAffichee)
            Toast.makeText(this,
                if (ok) "✅ Export PDF réussi (dossier Téléchargements)" else "❌ Échec de l'export PDF",
                Toast.LENGTH_LONG).show()
        }
    }

    private fun pickDate(isStart: Boolean, btn: Button) {
        val cal = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, day ->
            val selected = Calendar.getInstance()
            // début de journée pour la date début, fin de journée pour la date fin
            if (isStart) selected.set(year, month, day, 0, 0, 0)
            else selected.set(year, month, day, 23, 59, 59)
            val timestamp = selected.timeInMillis
            btn.text = "$day/${month + 1}/$year"
            if (isStart) startDate = timestamp else endDate = timestamp

            if (startDate != -1L && endDate != -1L) {
                viewModel.filterByDateRange(startDate, endDate)
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun ouvrirEdition(t: Transaction) {
        val intent = if (t.type == "REVENU") {
            Intent(this, AddIncomeActivity::class.java)
                .putExtra(AddIncomeActivity.EXTRA_ID, t.id)
        } else {
            Intent(this, AddExpenseActivity::class.java)
                .putExtra(AddExpenseActivity.EXTRA_ID, t.id)
        }
        startActivity(intent)
    }
}
