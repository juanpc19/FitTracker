package com.example.tfg01.domain.repository

import com.example.tfg01.data.model.Ejercicio

interface EjercicioRepository {
    suspend fun obtenerPlantillaEjercicios(): List<Ejercicio>
    suspend fun obtenerEjPorSesionPorRutina(userId: String, nombreRutina: String, dia: String)
            : List<Ejercicio>

    suspend fun obtenerEjPersonalizadosUsuario(userId: String): List<Ejercicio>
    suspend fun obtenerEjConjuntosUsuario(userId: String): List<Ejercicio>
    suspend fun comprobarExistenciaEjercicio(userId: String, nombreEjercicio: String): Boolean
    suspend fun crearEjercicio(userId: String, ejercicio: Ejercicio)
    suspend fun editarEjercicio(userId: String, ejercicio: Ejercicio)
    suspend fun eliminarEjercicio(userId: String, nombreEjercicio: String)
}