package com.team.smartspend.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Utilitaires de dates partagés par l'application.
 */
object DateUtils {

    /** Mois courant au format "AAAA-MM" (ex : "2026-06"). */
    fun moisActuel(): String =
        SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

    /** Timestamp du tout début du mois courant (1er à 00:00:00). */
    fun debutDuMois(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    /** Timestamp de la toute fin du mois courant (dernier jour à 23:59:59). */
    fun finDuMois(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }
}
