package com.example.tfg01.data.model

data class Rutina(
    val userId: String,
    val nombre: String,
    val rutinaActiva: Boolean
) {
    // Constructor con argumentos requerido por Firestore
    constructor() : this("", "", false)
}