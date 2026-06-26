package com.team.smartspend.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.team.smartspend.R
import com.team.smartspend.utils.SessionManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sessionManager = SessionManager(this)

        val switchTheme = findViewById<androidx.appcompat.widget.SwitchCompat>(R.id.switchTheme)
        val userInfo = findViewById<android.widget.TextView>(R.id.textViewUserInfo)
        val logoutButton = findViewById<android.widget.Button>(R.id.buttonLogout)

        // Afficher les infos de l'utilisateur
        userInfo.text = "Compte : ${sessionManager.getUserEmail()}"

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
}