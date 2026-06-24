package com.team.smartspend.database

import androidx.room.*
import com.team.smartspend.model.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE email = :email AND motDePasse = :motDePasse LIMIT 1")
    suspend fun login(email: String, motDePasse: String): User?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Delete
    suspend fun delete(user: User)
}