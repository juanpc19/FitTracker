package com.example.tfg01.presentation.signin

//datos del estado de un sign in
data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)
