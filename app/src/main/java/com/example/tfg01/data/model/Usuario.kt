package com.example.tfg01.data.model

data class Usuario(
    val userId: String,
    val userName: String?,
    val email: String?,
    val profilePictureUrl: String?,

) {
    // Constructor con argumentos requerido por Firestore
    constructor() : this("", null, null, null)
}
