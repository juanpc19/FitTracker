package com.example.tfg01.domain.usecases.rutina

import com.example.tfg01.domain.model.RutinaConSesConEjs
import com.example.tfg01.domain.repository.RutinaRepository
import javax.inject.Inject

class CrearRutina @Inject constructor(private val rutinaRepository: RutinaRepository) {

    suspend operator fun invoke(rutinaConSesConEjs: RutinaConSesConEjs) {
        rutinaRepository.crearRutina(rutinaConSesConEjs)
    }
}