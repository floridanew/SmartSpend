package com.team.smartspend.utils

import at.favre.lib.crypto.bcrypt.BCrypt

object PasswordHasher {

    // Coût de l'algorithme (10 = bon équilibre sécurité/performance)
    private const val COST = 10

    /**
     * Hashe le mot de passe en utilisant BCrypt
     * @param password Mot de passe en clair
     * @return Le mot de passe hashé
     */
    fun hash(password: String): String {
        return BCrypt.withDefaults().hashToString(COST, password.toCharArray())
    }

    /**
     * Vérifie si le mot de passe correspond au hash
     * @param password Mot de passe en clair
     * @param hash Hash stocké en base de données
     * @return true si correspond, false sinon
     */
    fun verify(password: String, hash: String): Boolean {
        return BCrypt.verifyer().verify(password.toCharArray(), hash).verified
    }
}