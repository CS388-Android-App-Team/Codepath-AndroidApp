package com.example.completionist

interface OnNavigationItemClickListener {
    fun onHomeClicked()
    fun onTaskClicked()
    fun onProfileClicked()
    fun onSignOutClicked()
    fun onSettingsClicked()
    fun onEmailSaveClicked(email: String)
    fun onProfileSaveClicked(fName: String, lName: String, uName: String)
    fun onPasswordChangeClick(password: String, passwordC: String, passwordO: String)
    fun onReminderSaveClick()
}