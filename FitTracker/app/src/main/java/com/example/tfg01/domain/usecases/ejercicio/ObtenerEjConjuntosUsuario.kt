package com.example.tfg01.domain.usecases.ejercicio

import com.example.tfg01.data.model.Ejercicio
import com.example.tfg01.domain.repository.EjercicioRepository
import javax.inject.Inject

class ObtenerEjConjuntosUsuario @Inject constructor(private val ejercicioRepository: EjercicioRepository) {

    suspend operator fun invoke(userId: String): List<Ejercicio> {
        return ejercicioRepository.obtenerEjConjuntosUsuario(userId)
    }
}