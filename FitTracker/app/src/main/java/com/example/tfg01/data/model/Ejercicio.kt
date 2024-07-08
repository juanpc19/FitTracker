package com.example.tfg01.data.model

data class Ejercicio(
    val id: Int,
    val nombre: String,
    val imagen: String,
    val anotaciones: String,
    val sets: Int,
    val repeticiones: Int,
    val peso: Int,
    val urlVideoEjemplo: String
) {
    // Constructor con argumentos requerido por Firestore
    constructor() : this(0,"", "", "", 0, 0, 0, "")
}
