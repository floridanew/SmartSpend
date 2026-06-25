package com.team.smartspend.utils

import androidx.annotation.DrawableRes
import androidx.annotation.ColorRes
import com.team.smartspend.R

object CategoryUtils {

    // Catégories de dépenses
    const val CATEGORIE_TRANSPORT = "Transport"
    const val CATEGORIE_NOURRITURE = "Nourriture"
    const val CATEGORIE_LOYER = "Loyer"
    const val CATEGORIE_LOISIRS = "Loisirs"
    const val CATEGORIE_SANTE = "Santé"
    const val CATEGORIE_AUTRE = "Autre"

    // Sources de revenus
    const val SOURCE_SALAIRE = "Salaire"
    const val SOURCE_FREELANCE = "Freelance"
    const val SOURCE_AUTRE = "Autre"

    //Retourne toutes les catégories de dépenses (pour un Spinner)
    fun getDepenseCategories(): List<String> = listOf(
        CATEGORIE_TRANSPORT,
        CATEGORIE_NOURRITURE,
        CATEGORIE_LOYER,
        CATEGORIE_LOISIRS,
        CATEGORIE_SANTE,
        CATEGORIE_AUTRE
    )

    //Retourne toutes les sources de revenus (pour un Spinner)
    fun getRevenueSources(): List<String> = listOf(
        SOURCE_SALAIRE,
        SOURCE_FREELANCE,
        SOURCE_AUTRE
    )

    //Associe une icône à une catégorie de dépense
    @DrawableRes
    fun getIconForCategory(categorie: String): Int {
        return when (categorie) {
            CATEGORIE_TRANSPORT -> R.drawable.ic_transport
            CATEGORIE_NOURRITURE -> R.drawable.ic_nourriture
            CATEGORIE_LOYER -> R.drawable.ic_loyer
            CATEGORIE_LOISIRS -> R.drawable.ic_loisirs
            CATEGORIE_SANTE -> R.drawable.ic_sante
            else -> R.drawable.ic_autre
        }
    }

    //Associe une couleur à une catégorie de dépense
    @ColorRes
    fun getColorForCategory(categorie: String): Int {
        return when (categorie) {
            CATEGORIE_TRANSPORT -> R.color.categorie_transport
            CATEGORIE_NOURRITURE -> R.color.categorie_nourriture
            CATEGORIE_LOYER -> R.color.categorie_loyer
            CATEGORIE_LOISIRS -> R.color.categorie_loisirs
            CATEGORIE_SANTE -> R.color.categorie_sante
            else -> R.color.categorie_autre
        }
    }

//      Retourne l'icône pour une source de revenu

    @DrawableRes
    fun getIconForRevenueSource(source: String): Int {
        return when (source) {
            SOURCE_SALAIRE -> R.drawable.ic_salaire
            SOURCE_FREELANCE -> R.drawable.ic_freelance
            else -> R.drawable.ic_autre
        }
    }
}