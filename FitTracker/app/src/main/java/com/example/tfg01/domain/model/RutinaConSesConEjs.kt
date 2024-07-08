package com.example.tfg01.domain.model

import com.example.tfg01.data.model.Rutina

//guardara datos de rutina sus sesiones y los ejs de cada una de las sesiones
data class RutinaConSesConEjs(
    val rutina: Rutina? = null,
    val listaSesConListaEjs: List<SesionConEjercicios> = emptyList()
)
