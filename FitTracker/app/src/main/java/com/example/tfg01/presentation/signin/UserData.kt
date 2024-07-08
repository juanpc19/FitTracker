package com.example.tfg01.presentation.signin

//datos del user, se usa en SignInResult
data class UserData(
    val userId: String,
    val userName: String?,
    val profilePictureUrl: String?,
    val email: String?
)