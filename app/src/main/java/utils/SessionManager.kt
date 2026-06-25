package com.team.smartspend.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("smartspend_session", Context.MODE_PRIVATE)

    fun saveSession(userId: Int, nom: String, email: String) {
        prefs.edit().apply {
            putBoolean("is_logged_in", true)
            putInt("user_id", userId)
            putString("user_nom", nom)
            putString("user_email", email)
            apply()
        }
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false)
    }

    fun getUserId(): Int {
        return prefs.getInt("user_id", -1)
    }

    fun getUserNom(): String? {
        return prefs.getString("user_nom", null)
    }

    fun getUserEmail(): String? {
        return prefs.getString("user_email", null)
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}