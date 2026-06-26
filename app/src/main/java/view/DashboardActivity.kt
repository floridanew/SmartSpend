package com.team.smartspend.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.team.smartspend.R
import com.team.smartspend.model.Transaction
import com.team.smartspend.utils.SessionManager
import com.team.smartspend.viewmodel.DashboardViewModel
import com.team.smartspend.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class DashboardActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: DashboardViewModel
    private lateinit var transactionViewModel: TransactionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        sessionManager = SessionManager(this)

        val welcomeText = findViewById<TextView>(R.id.textViewWelcome)
        welcomeText.text = "Bienvenue, ${sessionManager.getUserNom()} !"

        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        transactionViewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        // Observer les chiffres (solde, revenus, dépenses, budget)
        viewModel.uiState.observe(this) { state ->
            findViewById<TextView>(R.id.textViewSolde).text = "${state.solde} FCFA"
            findViewById<TextView>(R.id.textViewRevenus).text = "${state.revenus} FCFA"
            findViewById<TextView>(R.id.textViewDepenses).text = "${state.depenses} FCFA"

            val progressBar = findViewById<android.widget.ProgressBar>(R.id.progressBarBudget)
            val budgetDetail = findViewById<TextView>(R.id.textViewBudgetDetail)

            if (state.budgetMax > 0) {
                progressBar.progress = state.budgetUtilisePourcent.coerceAtMost(100)
                budgetDetail.text = "${state.depenses} / ${state.budgetMax} FCFA (${state.budgetUtilisePourcent}%)"

                if (state.budgetDepasse) {
                    budgetDetail.setTextColor(Color.RED)
                    budgetDetail.text = "⚠️ Budget dépassé ! " + budgetDetail.text
                } else {
                    budgetDetail.setTextColor(Color.parseColor("#666666"))
                }
            } else {
                progressBar.progress = 0
                budgetDetail.text = "Aucun budget défini pour ce mois"
                budgetDetail.setTextColor(Color.parseColor("#666666"))
            }
        }

        // Observer la liste des dernières transactions
        viewModel.lastTransactions.observe(this) { transactions ->
            afficherTransactions(transactions)
        }

        // Charger les données calculées (solde, budget)
        viewModel.loadDashboard()

        findViewById<android.widget.Button>(R.id.buttonLogout).setOnClickListener {
            sessionManager.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Boutons d'ajout (Membre 3)
        findViewById<android.widget.Button>(R.id.buttonAddDepense).setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }
        findViewById<android.widget.Button>(R.id.buttonAddRevenu).setOnClickListener {
            startActivity(Intent(this, AddIncomeActivity::class.java))
        }

        configurerNavigation()
    }

    override fun onResume() {
        super.onResume()
        // Recalcule le solde au retour sur l'écran (après ajout/édition/suppression)
        viewModel.loadDashboard()
    }

    /**
     * Barre de navigation du bas (Membre 5).
     * Permet de passer rapidement d'un écran principal à l'autre.
     */
    private fun configurerNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_dashboard // on est sur le Dashboard

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> true // déjà ici
                R.id.nav_stats -> {
                    startActivity(Intent(this, StatsActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }

    private fun afficherTransactions(transactions: List<Transaction>) {
        val container = findViewById<LinearLayout>(R.id.layoutTransactions)
        container.removeAllViews()

        if (transactions.isEmpty()) {
            val empty = TextView(this)
            empty.text = "Aucune transaction pour le moment"
            empty.setTextColor(Color.parseColor("#999999"))
            empty.setPadding(0, 16, 0, 16)
            container.addView(empty)
            return
        }

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        for (transaction in transactions) {
            val itemLayout = LinearLayout(this)
            itemLayout.orientation = LinearLayout.HORIZONTAL
            itemLayout.setPadding(16, 16, 16, 16)
            itemLayout.setBackgroundColor(Color.WHITE)
            val itemParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            itemParams.bottomMargin = 4
            itemLayout.layoutParams = itemParams

            val infoText = TextView(this)
            infoText.text = "${transaction.categorie}\n${dateFormat.format(java.util.Date(transaction.date))}"
            infoText.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

            val montantText = TextView(this)
            val signe = if (transaction.type == "REVENU") "+" else "-"
            montantText.text = "$signe${transaction.montant} FCFA"
            montantText.setTextColor(
                if (transaction.type == "REVENU") Color.parseColor("#2E7D32") else Color.parseColor("#C62828")
            )
            montantText.gravity = Gravity.END
            montantText.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            itemLayout.addView(infoText)
            itemLayout.addView(montantText)

            // Clic = modifier, appui long = supprimer (Membre 3)
            itemLayout.setOnClickListener { ouvrirEdition(transaction) }
            itemLayout.setOnLongClickListener {
                confirmerSuppression(transaction)
                true
            }

            container.addView(itemLayout)
        }
    }

    /** Ouvre le bon écran d'édition selon le type de transaction. */
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

    /** Demande confirmation puis supprime la transaction. */
    private fun confirmerSuppression(t: Transaction) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Supprimer")
            .setMessage("Supprimer cette transaction ?")
            .setPositiveButton("Oui") { _, _ ->
                transactionViewModel.deleteTransaction(t) {
                    android.widget.Toast.makeText(
                        this, "Transaction supprimée", android.widget.Toast.LENGTH_SHORT
                    ).show()
                    viewModel.loadDashboard()
                }
            }
            .setNegativeButton("Non", null)
            .show()
    }
}