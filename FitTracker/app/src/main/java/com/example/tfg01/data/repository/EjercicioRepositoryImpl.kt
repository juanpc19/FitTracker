package com.example.tfg01.data.repository

import com.example.tfg01.data.datasource.firestore.EjercicioFirestoreDataSourceImpl
import com.example.tfg01.data.model.Ejercicio
import com.example.tfg01.domain.repository.EjercicioRepository
import javax.inject.Inject

class EjercicioRepositoryImpl @Inject constructor(private val ejercicioFirestoreDataSourceImpl: EjercicioFirestoreDataSourceImpl) :
    EjercicioRepository {

    override suspend fun obtenerPlantillaEjercicios(): List<Ejercicio> {
        return ejercicioFirestoreDataSourceImpl.obtenerPlantillaEjercicios()
    }

    override suspend fun obtenerEjPorSesionPorRutina(
        userId: String,
        nombreRutina: String,
        dia: String
    ): List<Ejercicio> {
        return ejercicioFirestoreDataSourceImpl.obtenerEjPorSesionPorRutina(
            userId,
            nombreRutina,
            dia
        )
    }

    override suspend fun obtenerEjConjuntosUsuario(userId: String): List<Ejercicio> {
        return ejercicioFirestoreDataSourceImpl.obtenerEjConjuntosUsuario(userId)
    }

    override suspend fun obtenerEjPersonalizadosUsuario(userId: String): List<Ejercicio> {
        return ejercicioFirestoreDataSourceImpl.obtenerEjPersonalizadosUsuario(userId)
    }

    override suspend fun comprobarExistenciaEjercicio(
        userId: String,
        nombreEjercicio: String
    ): Boolean {
        return ejercicioFirestoreDataSourceImpl.comprobarExistenciaEjercicio(
            userId,
            nombreEjercicio
        )
    }

    override suspend fun crearEjercicio(userId: String, ejercicio: Ejercicio) {
        return ejercicioFirestoreDataSourceImpl.crearEjercicio(userId, ejercicio)
    }

    override suspend fun editarEjercicio(userId: String, ejercicio: Ejercicio) {
        return ejercicioFirestoreDataSourceImpl.editarEjercicio(userId, ejercicio)
    }

    override suspend fun eliminarEjercicio(userId: String, nombreEjercicio: String) {
        return ejercicioFirestoreDataSourceImpl.eliminarEjercicio(userId, nombreEjercicio)
    }

}