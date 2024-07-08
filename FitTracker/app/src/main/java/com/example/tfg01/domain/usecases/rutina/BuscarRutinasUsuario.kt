package com.example.tfg01.domain.usecases.rutina

import com.example.tfg01.data.model.Rutina
import com.example.tfg01.domain.repository.RutinaRepository
import javax.inject.Inject

class BuscarRutinasUsuario @Inject constructor(private val rutinaRepository: RutinaRepository) {
    suspend operator fun invoke(userId: String): List<Rutina> {
        return rutinaRepository.buscarRutinasUsuario(userId)
    }
}