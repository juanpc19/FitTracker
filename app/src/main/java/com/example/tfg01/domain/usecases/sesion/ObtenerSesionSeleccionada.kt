package com.example.tfg01.domain.usecases.sesion

import com.example.tfg01.data.model.Sesion
import com.example.tfg01.domain.repository.SesionRepository
import javax.inject.Inject

class ObtenerSesionSeleccionada @Inject constructor(private val sesionRepository: SesionRepository) {
    suspend operator fun invoke(userId: String, nombreRutina: String, dia: String)
            : Sesion? {
        return sesionRepository.obtenerSesionSeleccionada(userId, nombreRutina, dia)
    }
}