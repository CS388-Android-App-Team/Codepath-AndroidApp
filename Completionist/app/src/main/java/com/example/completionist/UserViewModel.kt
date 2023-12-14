package com.example.completionist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.completionist.Quests.QuestDatabase
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository
    val allUsers: LiveData<List<User>>

    init {
        val userDao = QuestDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
        allUsers = repository.allUsers
    }

    fun getUserById(idToken: String): LiveData<User?> {
        return repository.getUserById(idToken)
    }

    fun insertUser(user: User) {
        viewModelScope.launch {
            repository.insertUser(user)
        }
    }

    fun deleteUserById(idToken: String) {
        viewModelScope.launch {
            repository.deleteUserById(idToken)
        }
    }

    fun deleteAllUsers() {
        viewModelScope.launch {
            repository.deleteAllUsers()
        }
    }
    fun updateEmailById(idToken: String, email: String) {
        viewModelScope.launch {
            repository.updateEmailById(idToken, email)
        }
    }
    fun updateFirstNameById(idToken: String, firstName: String) {
        viewModelScope.launch {
            repository.updateFirstNameById(idToken, firstName)
        }
    }
    fun updateLastNameById(idToken: String, lastName: String) {
        viewModelScope.launch {
            repository.updateLastNameById(idToken, lastName)
        }
    }
    fun updateUsernameById(idToken: String, username: String) {
        viewModelScope.launch {
            repository.updateUsernameById(idToken, username)
        }
    }

    fun updateLevelById(idToken: String, newLevel: Int){
        viewModelScope.launch {
            repository.updateLevelById(idToken, newLevel)
        }
    }

    fun addXpById(idToken: String, xpToAdd: Int){
        viewModelScope.launch {
            repository.addXpById(idToken, xpToAdd)
        }
    }

    fun addToFriendCountById(idToken: String){
        viewModelScope.launch {
            repository.addToFriendCountById(idToken)
        }
    }

}