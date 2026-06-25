package com.team.smartspend.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.team.smartspend.database.AppDatabase
import com.team.smartspend.model.User
import com.team.smartspend.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository

    init {
        val dao = AppDatabase.getDatabase(application).userDao()
        repository = UserRepository(dao)
    }

    fun register(user: User, callback: (Boolean) -> Unit) = viewModelScope.launch {
        val success = repository.register(user)
        callback(success)
    }

    fun login(email: String, motDePasse: String, callback: (User?) -> Unit) = viewModelScope.launch {
        val user = repository.login(email, motDePasse)
        callback(user)
    }
}