package com.team.smartspend.repository

import com.team.smartspend.database.UserDao
import com.team.smartspend.model.User

class UserRepository(private val userDao: UserDao) {

    suspend fun register(user: User): Boolean {
        val existingUser = userDao.getUserByEmail(user.email)
        if (existingUser != null) {
            return false // l'email existe déjà
        }
        userDao.insert(user)
        return true
    }

    suspend fun login(email: String, motDePasse: String): User? {
        return userDao.login(email, motDePasse)
    }
}