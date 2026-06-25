//package com.team.smartspend.view
//
//import android.app.DatePickerDialog
//import android.os.Bundle
//import android.widget.ArrayAdapter
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.team.smartspend.R
//import com.team.smartspend.model.Transaction
//import com.team.smartspend.utils.CategoryUtils
//import com.team.smartspend.utils.SessionManager
//import com.team.smartspend.viewmodel.TransactionViewModel
//import java.util.Calendar
//
//class AddExpenseActivity : AppCompatActivity() {
//
//    private lateinit var viewModel: TransactionViewModel
//    private lateinit var sessionManager: SessionManager
//    private var selectedDate: Long = System.currentTimeMillis()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_add_expense)
//
//        viewModel = TransactionViewModel(application)
//        sessionManager = SessionManager(this)
//
//        val montantInput = findViewById<android.widget.EditText>(R.id.inputMontant)
//        val spinner = findViewById<android.widget.Spinner>(R.id.spinnerCategorie)
//        val dateInput = findViewById<android.widget.EditText>(R.id.inputDate)
//        val descriptionInput = findViewById<android.widget.EditText>(R.id.inputDescription)
//        val saveButton = findViewById<android.widget.Button>(R.id.buttonSauvegarder)
//
//        // Remplir le spinner avec les catégories
//        val categories = CategoryUtils.getDepenseCategories()
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinner.adapter = adapter
//
//        // Sélecteur de date
//        dateInput.setOnClickListener {
//            showDatePicker { timestamp ->
//                selectedDate = timestamp
//                dateInput.setText(android.text.format.DateFormat.format("dd/MM/yyyy", timestamp))
//            }
//        }
//
//        saveButton.setOnClickListener {
//            val montant = montantInput.text.toString().trim().toDoubleOrNull()
//            val categorie = spinner.selectedItem.toString()
//            val description = descriptionInput.text.toString().trim()
//
//            if (montant == null || montant <= 0) {
//                Toast.makeText(this, "Veuillez entrer un montant valide", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            val userId = sessionManager.getUserId()
//            val transaction = Transaction(
//                id = sessionManager.getUserId(),
//                type = "DEPENSE",
//                categorie = categorie,
//                montant = montant,
//                description = description,
//                date = selectedDate
//            )
//
//            viewModel.insertTransaction(transaction) {
//                Toast.makeText(this, "✅ Dépense enregistrée", Toast.LENGTH_SHORT).show()
//                finish()
//            }
//        }
//    }
//
//    private fun showDatePicker(onDateSelected: (Long) -> Unit) {
//        val calendar = Calendar.getInstance()
//        DatePickerDialog(
//            this,
//            { _, year, month, day ->
//                val cal = Calendar.getInstance().apply {
//                    set(year, month, day, 0, 0, 0)
//                }
//                onDateSelected(cal.timeInMillis)
//            },
//            calendar.get(Calendar.YEAR),
//            calendar.get(Calendar.MONTH),
//            calendar.get(Calendar.DAY_OF_MONTH)
//        ).show()
//    }
//}