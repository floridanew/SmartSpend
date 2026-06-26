package com.team.smartspend.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.team.smartspend.R
import com.team.smartspend.utils.SessionManager

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val sessionManager = SessionManager(this)

        // Animation fade-in du logo (de invisible à visible sur 1,2 s)
        val logo = findViewById<ImageView>(R.id.imageLogo)
        logo.alpha = 0f
        logo.animate().alpha(1f).setDuration(1200).start()

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = if (sessionManager.isLoggedIn()) {
                Intent(this, DashboardActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 2000)
    }
}