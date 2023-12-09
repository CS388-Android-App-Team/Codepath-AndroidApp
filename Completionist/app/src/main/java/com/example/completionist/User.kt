package com.example.completionist

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(val idToken: String? = null, val username: String? = null, val email: String?)
