package com.example.tfg01.domain.repository

import com.example.tfg01.data.model.Rutina
import com.example.tfg01.domain.model.RutinaConSesConEjs

interface RutinaRepository {
    suspend fun buscarRutinaActivaUsuario(userId: String): Rutina?
    suspend fun buscarRutinasUsuario(userId: String): List<Rutina>
    suspend fun obtenerRutinaSeleccionada(userId: String, nombre: String): Rutina?
    suspend fun activarRutina(userId: String, nombre: String)
    suspend fun comprobarExistenciaRutina(userId: String, nombre: String): Boolean
    suspend fun crearRutina(rutinaConSesConEjs: RutinaConSesConEjs)
    suspend fun obtenerRutinaParaEditar(userId: String, nombreRutina: String): RutinaConSesConEjs?
    suspend fun editarRutina(userId: String, nombreRutina: String, rutinaConSesConEjs: RutinaConSesConEjs)
    suspend fun eliminarRutinaSeleccionada(userId: String, nombre: String)

}