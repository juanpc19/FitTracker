package com.example.tfg01.domain.model

import com.example.tfg01.data.model.Ejercicio
import com.example.tfg01.data.model.Sesion

//guardara sesiones y los ejs de cada una de las sesiones
data class SesionConEjercicios(
    val sesion: Sesion? = null,
    val listaEjercicios: List<Ejercicio> = emptyList()
)

