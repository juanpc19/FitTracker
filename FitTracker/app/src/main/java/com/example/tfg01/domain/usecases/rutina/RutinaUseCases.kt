package com.example.tfg01.domain.usecases.rutina

data class RutinaUseCases(
    val buscarRutinaActivaUsuario: BuscarRutinaActivaUsuario,
    val buscarRutinasUsuario: BuscarRutinasUsuario,
    val obtenerRutinaSeleccionada: ObtenerRutinaSeleccionada,
    val activarRutina: ActivarRutina,
    val comprobarExistenciaRutina: ComprobarExistenciaRutina,
    val crearRutina: CrearRutina,
    val obtenerRutinaParaEditar:ObtenerRutinaParaEditar,
    val editarRutina: EditarRutina,
    val eliminarRutinaSeleccionada: EliminarRutinaSeleccionada
)
