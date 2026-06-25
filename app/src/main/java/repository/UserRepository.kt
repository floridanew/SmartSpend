//package com.team.smartspend.repository
//
//import com.team.smartspend.database.UserDao
//import com.team.smartspend.model.User
//
//class UserRepository(private val userDao: UserDao) {
//
//    suspend fun register(user: User): Boolean {
//        val existingUser = userDao.getUserByEmail(user.email)
//        if (existingUser != null) {
//            return false // l'email existe déjà
//        }
//        userDao.insert(user)
//        return true
//    }
//
//    suspend fun login(email: String, motDePasse: String): User? {
//        return userDao.login(email, motDePasse)
//    }
//}
package com.team.smartspend.repository

import com.team.smartspend.database.UserDao
import com.team.smartspend.model.User
import com.team.smartspend.utils.PasswordHasher

class UserRepository(private val userDao: UserDao) {

    suspend fun register(user: User): Boolean {
        // Vérifier si l'email existe déjà
        val existingUser = userDao.getUserByEmail(user.email)
        if (existingUser != null) {
            return false // l'email existe déjà
        }

        // Hasher le mot de passe avant de sauvegarder
        val hashedPassword = PasswordHasher.hash(user.motDePasse)
        val userToSave = user.copy(motDePasse = hashedPassword)

        userDao.insert(userToSave)
        return true
    }

    suspend fun login(email: String, motDePasse: String): User? {
        val user = userDao.getUserByEmail(email)

        // Si l'utilisateur existe et que le mot de passe correspond
        if (user != null && PasswordHasher.verify(motDePasse, user.motDePasse)) {
            return user
        }

        return null // Email incorrect ou mot de passe erroné
    }
}