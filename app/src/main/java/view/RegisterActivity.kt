package com.team.smartspend.view

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.team.smartspend.R
import com.team.smartspend.model.User
import com.team.smartspend.viewmodel.UserViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        userViewModel = UserViewModel(application)

        val nomField = findViewById<android.widget.EditText>(R.id.editTextNom)
        val emailField = findViewById<android.widget.EditText>(R.id.editTextEmail)
        val passwordField = findViewById<android.widget.EditText>(R.id.editTextPassword)
        val registerButton = findViewById<android.widget.Button>(R.id.buttonRegister)
        val loginLink = findViewById<android.widget.TextView>(R.id.textViewGoToLogin)

        registerButton.setOnClickListener {
            val nom = nomField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            // Validation inline (message d'erreur directement sous chaque champ)
            if (nom.isEmpty()) {
                nomField.error = "Veuillez saisir votre nom"
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                emailField.error = "Veuillez saisir votre email"
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailField.error = "Format d'email invalide"
                return@setOnClickListener
            }
            if (password.length < 6) {
                passwordField.error = "Le mot de passe doit contenir au moins 6 caractères"
                return@setOnClickListener
            }

            val newUser = User(nom = nom, email = email, motDePasse = password)

            userViewModel.register(newUser) { success ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "Compte créé avec succès !", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Cet email est déjà utilisé", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}