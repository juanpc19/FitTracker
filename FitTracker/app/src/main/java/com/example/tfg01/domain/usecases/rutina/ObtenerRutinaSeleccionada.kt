package com.example.tfg01.domain.usecases.rutina

import com.example.tfg01.data.model.Rutina
import com.example.tfg01.domain.repository.RutinaRepository
import javax.inject.Inject

class ObtenerRutinaSeleccionada @Inject constructor(private val rutinaRepository: RutinaRepository) {
    suspend operator fun invoke(userId: String, nombre: String): Rutina? {
        return rutinaRepository.obtenerRutinaSeleccionada(userId, nombre)
    }
}