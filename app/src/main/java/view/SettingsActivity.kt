package com.team.smartspend.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.team.smartspend.R
import com.team.smartspend.utils.DateUtils
import com.team.smartspend.utils.SessionManager
import com.team.smartspend.viewmodel.BudgetViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var budgetViewModel: BudgetViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sessionManager = SessionManager(this)
        budgetViewModel = ViewModelProvider(this)[BudgetViewModel::class.java]

        val switchTheme = findViewById<androidx.appcompat.widget.SwitchCompat>(R.id.switchTheme)
        val userInfo = findViewById<android.widget.TextView>(R.id.textViewUserInfo)
        val logoutButton = findViewById<android.widget.Button>(R.id.buttonLogout)

        // Afficher les infos de l'utilisateur
        userInfo.text = "Compte : ${sessionManager.getUserEmail()}"

        // --- Budget mensuel (Membre 3) ---
        configurerBudget()

        // Charger l'état du thème
        val isDarkMode = sessionManager.getDarkMode()
        switchTheme.isChecked = isDarkMode

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            sessionManager.saveDarkMode(isChecked)
        }

        logoutButton.setOnClickListener {
            sessionManager.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    /** Gère l'affichage et l'enregistrement du budget mensuel. */
    private fun configurerBudget() {
        val inputBudget = findViewById<EditText>(R.id.inputBudget)
        val btnSaveBudget = findViewById<Button>(R.id.buttonSaveBudget)
        val textBudgetActuel = findViewById<TextView>(R.id.textBudgetActuel)
        val mois = DateUtils.moisActuel()

        // Afficher le budget déjà défini pour ce mois
        budgetViewModel.getBudget(mois) { budget ->
            if (budget != null && budget.montantMax > 0) {
                textBudgetActuel.text = "Budget actuel ($mois) : ${budget.montantMax.toInt()} FCFA"
                inputBudget.setText(budget.montantMax.toInt().toString())
            } else {
                textBudgetActuel.text = "Aucun budget défini ce mois"
            }
        }

        btnSaveBudget.setOnClickListener {
            val montant = inputBudget.text.toString().trim().toDoubleOrNull()
            if (montant == null || montant <= 0) {
                Toast.makeText(this, "Veuillez entrer un montant valide", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            budgetViewModel.definirBudget(mois, montant) {
                Toast.makeText(this, "✅ Budget défini : ${montant.toInt()} FCFA", Toast.LENGTH_SHORT).show()
                textBudgetActuel.text = "Budget actuel ($mois) : ${montant.toInt()} FCFA"
            }
        }
    }
}