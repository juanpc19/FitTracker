package com.example.tfg01.domain.usecases.ejercicio

import com.example.tfg01.domain.repository.EjercicioRepository
import javax.inject.Inject

class EliminarEjercicio @Inject constructor(private val ejercicioRepository: EjercicioRepository) {

    suspend operator fun invoke(userId: String, nombreEjercicio: String) {
        return ejercicioRepository.eliminarEjercicio(userId, nombreEjercicio)
    }
}