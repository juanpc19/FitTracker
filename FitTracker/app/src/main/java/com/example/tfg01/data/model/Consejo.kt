package com.example.tfg01.data.model

data class Consejo(
    val id: Int,
    val topico: String,
    val pregunta: String,
    val respuesta: String
) {
    // Constructor con argumentos requerido por Firestore
    constructor() : this(0, "", "", "")
}