package com.team.smartspend.view

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.team.smartspend.R
import com.team.smartspend.model.User
import com.team.smartspend.utils.SessionManager
import com.team.smartspend.viewmodel.UserViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        userViewModel = UserViewModel(application)
        sessionManager = SessionManager(this)

        val emailField = findViewById<android.widget.EditText>(R.id.editTextEmail)
        val passwordField = findViewById<android.widget.EditText>(R.id.editTextPassword)
        val loginButton = findViewById<android.widget.Button>(R.id.buttonLogin)
        val registerLink = findViewById<android.widget.TextView>(R.id.textViewGoToRegister)

        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            // Validation inline (message d'erreur directement sous le champ)
            if (email.isEmpty()) {
                emailField.error = "Veuillez saisir votre email"
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailField.error = "Format d'email invalide"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                passwordField.error = "Veuillez saisir votre mot de passe"
                return@setOnClickListener
            }

            userViewModel.login(email, password) { user ->
                runOnUiThread {
                    if (user != null) {
                        sessionManager.saveSession(user.id, user.nom, user.email)
                        startActivity(Intent(this, DashboardActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}