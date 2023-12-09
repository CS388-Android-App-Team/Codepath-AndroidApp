package com.example.completionist

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Friend(val username: String? = null, val trueFriend: Boolean)
