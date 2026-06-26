package com.team.smartspend.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

/**
 * NotificationHelper — gère les notifications d'alerte de budget.
 *
 * - crée le canal de notification (obligatoire depuis Android 8 / API 26) ;
 * - envoie une notification quand le budget mensuel est dépassé.
 */
object NotificationHelper {

    private const val CHANNEL_ID = "budget_alerts"
    private const val CHANNEL_NOM = "Alertes de budget"
    private const val NOTIF_ID = 1001

    /** Crée le canal de notification (à appeler avant d'envoyer une notification). */
    fun creerCanal(context: Context) {
        val canal = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NOM,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Alertes lorsque le budget mensuel est dépassé"
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(canal)
    }

    /** Affiche une notification d'alerte de dépassement de budget. */
    fun alerteBudgetDepasse(context: Context, depenses: Double, budget: Double) {
        creerCanal(context)

        // Depuis Android 13 (API 33), une permission est nécessaire.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val accordee = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!accordee) return // pas de permission → on n'envoie pas (pas de plantage)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentTitle("⚠️ Budget dépassé !")
            .setContentText(
                "Vous avez dépensé ${depenses.toInt()} / ${budget.toInt()} FCFA ce mois-ci."
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIF_ID, notification)
    }
}
