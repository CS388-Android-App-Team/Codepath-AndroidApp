package com.example.completionist

interface SignInAndUpClickListener {
    fun onRegisterClick()
    fun onSignUpClick(fName: String, lName: String, email: String, password: String, passwordConfirm: String)
    fun onForgotPasswordClick()
    fun onBackClick()
    fun onSignInClick(email: String, password: String)
    fun onSendLinkClick(email: String)

}