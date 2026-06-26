package com.team.smartspend

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.team.smartspend.utils.SessionManager

/**
 * Classe Application : exécutée une seule fois au démarrage de l'app,
 * AVANT toute activité.
 *
 * Elle réapplique le thème (clair/sombre) choisi par l'utilisateur et
 * sauvegardé dans les SharedPreferences. Sans cela, le mode sombre était
 * "oublié" à chaque redémarrage de l'application.
 */
class SmartSpendApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val modeSombre = SessionManager(this).getDarkMode()
        AppCompatDelegate.setDefaultNightMode(
            if (modeSombre) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
