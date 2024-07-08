package com.example.tfg01.domain.usecases.ejercicio

data class EjercicioUseCases(
    val obtenerPlantillaEjercicios: ObtenerPlantillaEjercicios,
    val obtenerEjPorSesionPorRutina: ObtenerEjPorSesionPorRutina,
    val obtenerEjPersonalizadosUsuario: ObtenerEjPersonalizadosUsuario,
    val obtenerEjConjuntosUsuario: ObtenerEjConjuntosUsuario,
    val comprobarExistenciaEjercicio: ComprobarExistenciaEjercicio,
    val crearEjercicio: CrearEjercicio,
    val editarEjercicio: EditarEjercicio,
    val eliminarEjercicio: EliminarEjercicio
)