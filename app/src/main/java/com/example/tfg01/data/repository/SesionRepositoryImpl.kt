package com.example.tfg01.data.repository

import com.example.tfg01.data.datasource.firestore.SesionFirestoreDataSourceImpl
import com.example.tfg01.data.model.Sesion
import com.example.tfg01.domain.repository.SesionRepository
import javax.inject.Inject

class SesionRepositoryImpl @Inject constructor(private val sesionFirestoreDataSourceImpl: SesionFirestoreDataSourceImpl) :
    SesionRepository {

    override suspend fun buscarSesionesRutinaActiva(userId: String): List<Sesion> {
        return sesionFirestoreDataSourceImpl.buscarSesionesRutinaActiva(userId)
    }

    override suspend fun obtenerSesionSeleccionada(
        userId: String,
        nombreRutina: String,
        dia: String
    )
            : Sesion? {
        return sesionFirestoreDataSourceImpl.obtenerSesionSeleccionada(userId, nombreRutina, dia)
    }
}