package com.example.stunby.data.pref

data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)