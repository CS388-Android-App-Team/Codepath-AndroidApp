package com.example.completionist

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
@Entity(tableName = "user")
data class User(
    @PrimaryKey
    val idToken: String,
    var username: String? = null,
    var email: String?,
    var firstName: String? = "",
    var lastName: String? = "",
    var level: Int? = null,
    var xp: Int? = null,
    var streak: Int? = null,
    var consistency: Int? = null,
    var friendCount: Int? = null
){
    constructor() : this("", "", "", "", "", 0, 0, 0, 0, 0)
}