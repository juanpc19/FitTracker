package com.example.tfg01.data.model

data class Sesion(
    val dia: String,
    val diaNum: Int,
    val nombre: String,
    val descanso: Boolean
) {
    // Constructor con argumentos requerido por Firestore
    constructor() : this("", 0, "", false)
}