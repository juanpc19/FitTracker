package com.example.tfg01.domain.repository

import com.example.tfg01.data.model.Sesion

interface SesionRepository {

    suspend fun buscarSesionesRutinaActiva(userId: String): List<Sesion>
    suspend fun obtenerSesionSeleccionada(userId: String, nombreRutina: String, dia: String)
            : Sesion?
}