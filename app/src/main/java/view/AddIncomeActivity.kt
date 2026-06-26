package com.team.smartspend.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.team.smartspend.R
import com.team.smartspend.model.Transaction
import com.team.smartspend.utils.CategoryUtils
import com.team.smartspend.viewmodel.TransactionViewModel
import java.util.Calendar

/**
 * AddIncomeActivity — ajouter OU modifier un REVENU (Membre 3).
 *
 * Logique identique à l'ajout de dépense, mais :
 *   - type = "REVENU" ;
 *   - choix d'une SOURCE (Salaire, Freelance, Autre) au lieu d'une catégorie ;
 *   - pas de vérification de budget (le budget concerne les dépenses).
 */
class AddIncomeActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "transaction_id"
    }

    private lateinit var transactionViewModel: TransactionViewModel

    private lateinit var montantInput: EditText
    private lateinit var spinner: Spinner
    private lateinit var dateInput: EditText
    private lateinit var descriptionInput: EditText

    private var selectedDate: Long = System.currentTimeMillis()
    private var modeEdition = false
    private var transactionAEditer: Transaction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_income)

        transactionViewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        montantInput = findViewById(R.id.inputMontant)
        spinner = findViewById(R.id.spinnerSource)
        dateInput = findViewById(R.id.inputDate)
        descriptionInput = findViewById(R.id.inputDescription)
        val saveButton = findViewById<Button>(R.id.buttonSauvegarder)
        val deleteButton = findViewById<Button>(R.id.buttonSupprimer)
        val titre = findViewById<TextView>(R.id.textTitre)

        // Remplir le spinner avec les sources de revenu
        val sources = CategoryUtils.getRevenueSources()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sources)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Date par défaut : aujourd'hui
        afficherDate(selectedDate)
        dateInput.setOnClickListener {
            showDatePicker { timestamp ->
                selectedDate = timestamp
                afficherDate(timestamp)
            }
        }

        // Mode édition ?
        val id = intent.getIntExtra(EXTRA_ID, -1)
        if (id != -1) {
            modeEdition = true
            titre.text = "Modifier le revenu"
            deleteButton.visibility = Button.VISIBLE
            chargerTransaction(id, sources)
        }

        saveButton.setOnClickListener { enregistrer() }
        deleteButton.setOnClickListener { confirmerSuppression() }
    }

    private fun chargerTransaction(id: Int, sources: List<String>) {
        transactionViewModel.getById(id) { t ->
            if (t != null) {
                transactionAEditer = t
                montantInput.setText(t.montant.toString())
                descriptionInput.setText(t.description)
                selectedDate = t.date
                afficherDate(t.date)
                val index = sources.indexOf(t.categorie)
                if (index >= 0) spinner.setSelection(index)
            }
        }
    }

    private fun afficherDate(timestamp: Long) {
        dateInput.setText(android.text.format.DateFormat.format("dd/MM/yyyy", timestamp))
    }

    private fun enregistrer() {
        val montant = montantInput.text.toString().trim().toDoubleOrNull()
        val source = spinner.selectedItem.toString()
        val description = descriptionInput.text.toString().trim()

        if (montant == null || montant <= 0) {
            Toast.makeText(this, "Veuillez entrer un montant valide", Toast.LENGTH_SHORT).show()
            return
        }

        val transaction = Transaction(
            id = transactionAEditer?.id ?: 0,
            type = "REVENU",
            categorie = source,      // pour un revenu, la "catégorie" stocke la source
            montant = montant,
            description = description,
            date = selectedDate
        )

        if (modeEdition) {
            transactionViewModel.updateTransaction(transaction) {
                Toast.makeText(this, "✅ Revenu modifié", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            transactionViewModel.insertTransaction(transaction) {
                Toast.makeText(this, "✅ Revenu enregistré", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun confirmerSuppression() {
        val t = transactionAEditer ?: return
        AlertDialog.Builder(this)
            .setTitle("Supprimer")
            .setMessage("Voulez-vous vraiment supprimer ce revenu ?")
            .setPositiveButton("Oui") { _, _ ->
                transactionViewModel.deleteTransaction(t) {
                    Toast.makeText(this, "Revenu supprimé", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .setNegativeButton("Non", null)
            .show()
    }

    private fun showDatePicker(onDateSelected: (Long) -> Unit) {
        val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }
        DatePickerDialog(
            this,
            { _, year, month, day ->
                val cal = Calendar.getInstance().apply { set(year, month, day, 0, 0, 0) }
                onDateSelected(cal.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
