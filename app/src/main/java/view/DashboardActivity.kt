package com.team.smartspend.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.team.smartspend.R
import com.team.smartspend.utils.SessionManager

class DashboardActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        sessionManager = SessionManager(this)

        val welcomeText = findViewById<android.widget.TextView>(R.id.textViewWelcome)
        welcomeText.text = "Bienvenue, ${sessionManager.getUserNom()} !"

        val logoutButton = findViewById<android.widget.Button>(R.id.buttonLogout)
        logoutButton.setOnClickListener {
            sessionManager.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}