package com.example.completionist

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Friend(val userID: String? = null, val username: String? = null, val streak: Int? = null, val consistency: Int? = null)
