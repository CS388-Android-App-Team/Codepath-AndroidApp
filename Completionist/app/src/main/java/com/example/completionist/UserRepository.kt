package com.example.completionist

import androidx.lifecycle.LiveData
import com.example.completionist.User

class UserRepository(private val userDao: UserDao) {
    val allUsers: LiveData<List<User>> = userDao.getAllUsers()

    fun getUserById(idToken: String): LiveData<User?> {
        return userDao.getUserById(idToken)
    }

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun deleteUserById(idToken: String) {
        userDao.deleteUserById(idToken)
    }

    suspend fun deleteAllUsers() {
        userDao.deleteAllUsers()
    }

    suspend fun updateEmailById(idToken: String, email: String) {
        userDao.updateEmailById(idToken, email)
    }

    suspend fun updateFirstNameById(idToken: String, firstName: String) {
        userDao.updateFirstNameById(idToken, firstName)
    }

    suspend fun updateLastNameById(idToken: String, lastName: String) {
        userDao.updateLastNameById(idToken, lastName)
    }

    suspend fun updateUsernameById(idToken: String, username: String) {
        userDao.updateUsernameById(idToken, username)
    }

    suspend fun updateLevelById(idToken: String, newLevel: Int){
        userDao.updateLevelById(idToken, newLevel)
    }

    suspend fun updateXpById(idToken: String, xpToAdd: Int){
        userDao.updateXpById(idToken, xpToAdd)
    }

}
