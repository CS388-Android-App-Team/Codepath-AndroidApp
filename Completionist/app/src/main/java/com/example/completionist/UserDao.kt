package com.example.completionist

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.completionist.User

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAllUsers(): LiveData<List<User>>

    @Query("SELECT * FROM user WHERE idToken = :idToken")
    fun getUserById(idToken: String): LiveData<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("DELETE FROM user WHERE idToken = :idToken")
    suspend fun deleteUserById(idToken: String)

    @Query("DELETE FROM user")
    suspend fun deleteAllUsers()

    @Query("UPDATE user SET email = :email WHERE idToken = :idToken")
    suspend fun updateEmailById(idToken: String, email: String)

    @Query("UPDATE user SET firstName = :firstName WHERE idToken = :idToken")
    suspend fun updateFirstNameById(idToken: String, firstName: String)

    @Query("UPDATE user SET lastName = :lastName WHERE idToken = :idToken")
    suspend fun updateLastNameById(idToken: String, lastName: String)

    @Query("UPDATE user SET username = :username WHERE idToken = :idToken")
    suspend fun updateUsernameById(idToken: String, username: String)

    @Query("UPDATE user SET level = :newLevel WHERE idToken = :idToken")
    suspend fun updateLevelById(idToken: String, newLevel: Int)

    @Query("UPDATE user SET xp = (xp + :xpToAdd ) WHERE idToken = :idToken")
    suspend fun addXpById(idToken: String, xpToAdd: Int)

    @Query("UPDATE user SET friendCount = (friendCount + 1) WHERE idToken = :idToken")
    suspend fun addToFriendCountById(idToken: String)
}
