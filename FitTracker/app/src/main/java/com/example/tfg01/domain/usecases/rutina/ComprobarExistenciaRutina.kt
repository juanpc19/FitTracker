package com.example.tfg01.domain.usecases.rutina

import com.example.tfg01.domain.repository.RutinaRepository
import javax.inject.Inject

class ComprobarExistenciaRutina @Inject constructor(private val rutinaRepository: RutinaRepository) {
    suspend operator fun invoke(userId: String, nombre: String): Boolean {
        return rutinaRepository.comprobarExistenciaRutina(userId, nombre)
    }
}