package com.example.completionist

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class User(val idToken: String? = null,
                var username: String? = null,
                var email: String?,
                var firstName: String? = "",
                var lastName: String? = "",
                var level: Int? = null,
                var xp: Int? = null,
                var streak: Int? = null,
                var consistency: Int? = null,
                var friendCount: Int? = null):Serializable{
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        0,
        0,
        0,
        0,
        0
    )
                }