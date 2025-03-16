package com.hadirahimi.passwordchecker

data class PasswordItem(
    val id: Int,
    val password: String,
    val strength: String,
    val date: String
)