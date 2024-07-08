package com.example.tfg01.domain.usecases.consejo

import com.example.tfg01.data.model.Consejo
import com.example.tfg01.domain.repository.ConsejoRepository
import javax.inject.Inject

class ObtenerConsejos @Inject constructor(private val consejoRepository: ConsejoRepository) {

    suspend operator fun invoke(): List<Consejo> {
        return consejoRepository.obtenerConsejos()
    }
}