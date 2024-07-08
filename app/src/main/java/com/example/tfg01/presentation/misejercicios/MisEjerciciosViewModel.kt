package com.example.tfg01.presentation.misejercicios

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfg01.data.model.Ejercicio
import com.example.tfg01.domain.model.EstadoToast
import com.example.tfg01.domain.model.UsuarioActualSingleton
import com.example.tfg01.domain.usecases.ejercicio.EjercicioUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MisEjerciciosViewModel @Inject constructor(private val ejercicioUseCases: EjercicioUseCases) :
    ViewModel() {

        //ejercicio que se cargara en el modal de crear/editar
    private var _ejercicioParaModal: MutableStateFlow<Ejercicio?> = MutableStateFlow(null)
    val ejercicioParaModal = _ejercicioParaModal.asStateFlow()

    //permitira o no la escritura en el primer textfield del modal segun se este creando o editando ej
    private var _escrituraNombreEjModal: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val escrituraNombreEjModal = _escrituraNombreEjModal.asStateFlow()

    private var _ejercicioSeleccionado: MutableStateFlow<Ejercicio?> = MutableStateFlow(null)
    val ejercicioSeleccionado = _ejercicioSeleccionado.asStateFlow()

    //contendra la lista de los ejercicios personalizados del usuario para la vista
    private var _listaEjsPer: MutableStateFlow<List<Ejercicio>> = MutableStateFlow(emptyList())
    val listaEjerciciosPer = _listaEjsPer.asStateFlow()

    //contendra el id mas grande existente de la lista de ejs se usara con +1 para crear ej y darle id
    private var _idMaxExistente: MutableStateFlow<Int?> = MutableStateFlow(0)

    //contendra ejercicio "falso" para usar al desplegar el modal de crear,
    // su valor sera dado a _ejercicioParaModal al cerrarse modal crear o modal editar
    private var _ejercicioPlantilla: MutableStateFlow<Ejercicio?> = MutableStateFlow(null)

    //usado para controlar si debe mostrarse el modal de carga
    private var _mostrarModalCarga: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val mostrarModalCarga = _mostrarModalCarga.asStateFlow()

    //usado para controlar si debe mostrarse el modal de eliminar
    private var _mostrarModalEliminar: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val mostrarModalEliminar = _mostrarModalEliminar.asStateFlow()

    //usado para controlar si debe mostrarse el modal de crear
    private var _mostrarModalCrear: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val mostrarModalCrear = _mostrarModalCrear.asStateFlow()

    //usado para controlar si debe mostrarse el modal de editar
    private var _mostrarModalEditar: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val mostrarModalEditar = _mostrarModalEditar.asStateFlow()

    //se usara para saber si debe hacerse toast y que mensaje poner en el desde la vista
    private var _estadoToast: MutableStateFlow<EstadoToast?> = MutableStateFlow(null)
    val estadoToast = _estadoToast.asStateFlow()

    //modificara el bool de ToastEstado.hacerToast para que el efecto se compruebe en IU
    //hara toast si true y con valor de ToastEstado.mensaje dejando all listo al efecto
    private fun activarToast(mensaje: String) {
        viewModelScope.launch {
            _estadoToast.update { estadoActual ->
                estadoActual?.copy(hacerToast = true, mensajeToast = mensaje)
            }
        }
    }

    // Método para resetear el estado del toast después de mostrarlo
    fun resetearToast() {
        viewModelScope.launch {
            _estadoToast.update { estadoActual ->
                estadoActual?.copy(hacerToast = false, mensajeToast = "")
            }
        }
    }

    //establecera el ej pulsado como seleccionado
    fun setEjercicioSeleccionado(ejercicio: Ejercicio) {
        _ejercicioSeleccionado.update { ejercicio }
    }

    //mostrara modal para crear y permitira escritura en campo nombre del mismo
    fun desplegarModalCrearEj() {
        _escrituraNombreEjModal.update { true }
        _mostrarModalCrear.update { true }
    }

    //creara ejercicio o no en base a decision
    fun crearSegunDecisionModal(decision: Boolean) {

        viewModelScope.launch {
            //en caso afirmativo
            if (decision) {
                //comprueba existeccia de ej
                val ejercicioExistente = ejercicioUseCases.comprobarExistenciaEjercicio(
                    UsuarioActualSingleton.idUsuarioActual,
                    _ejercicioParaModal.value?.nombre.toString()
                )
                //de existir el ej hace toast pidiendo nombre diferente
                if (ejercicioExistente) {
                    activarToast("Ya existe un ejercicio con ese nombre")
                    //sino existe recoge datos del modal y le da  un id nuevo
                } else {
                    val nuevoId = _idMaxExistente.value?.plus(1)
                    _ejercicioParaModal.update { ejercicio ->
                        nuevoId?.let {
                            ejercicio?.copy(
                                id = nuevoId,
                                urlVideoEjemplo = ejercicio.urlVideoEjemplo.takeIf { !it.isNullOrBlank() }
                                    ?: "https://www.dropbox.com/scl/fi/dbeacfca6ewr3zoedbkdk/stick-bug.mp4?rlkey=l0p3u6inigguj4ajj9qulhk6t&raw=1"
                            )
                        }
                    }
                    // y lo crea y lo notifica con toast
                    activarToast("Ejercicio creado")
                    _ejercicioParaModal.value?.let {
                        ejercicioUseCases.crearEjercicio(
                            UsuarioActualSingleton.idUsuarioActual,
                            it
                        )
                    }
                    //recargo datos de vm
                    resetViewModel()
                    cargarDatos()
                }
            } else {
                //de lo contrario retiro modal y restablezco su contenido
                _mostrarModalCrear.update { false }//retiro el modal
                _ejercicioParaModal.update { _ejercicioPlantilla.value }
            }
        }
    }

    //mostrara modal para editar cargando ej seleccionado y denegara escritura en el campo nombre
    //o hara toast si no hay ej selccionado
    fun desplegarModalEditarEj() {
        viewModelScope.launch {
            if (_ejercicioSeleccionado.value != null) {
                _ejercicioParaModal.value = _ejercicioSeleccionado.value
                _escrituraNombreEjModal.update { false }
                _mostrarModalEditar.update { true }
            } else {
                activarToast("Seleccione ejercicio para editar")
            }
        }
    }
    //edita el ej o no en base a decision, en caso afirmativo lo edita y notifica a usuario
    //en caso negativo retira el modal
    fun editarSegunDecisionModal(decision: Boolean) {
        viewModelScope.launch {
            if (decision) {
                activarToast("Ejercicio editado")
                _ejercicioParaModal.value?.let {
                    ejercicioUseCases.editarEjercicio(
                        UsuarioActualSingleton.idUsuarioActual,
                        it
                    )
                }
                _mostrarModalEditar.update { false }
                _mostrarModalCarga.update { true }
                resetViewModel()
                cargarDatos()
            } else {
                _mostrarModalEditar.update { false }
                _ejercicioParaModal.update { _ejercicioPlantilla.value }
            }
        }
    }

    // actualizara el campo indicado del ejercicio usando nuevoValor para ello cuando
    // se escriba en un textfield del ejercicio en el modal
    fun actualizarCampoEjercicio(campo: String, nuevoValor: String) {
        viewModelScope.launch {
            //switch para evaluar que campos modificar y modifico el campo
            _ejercicioParaModal.update { ejercicio ->
                when (campo) {
                    "nombre" -> ejercicio?.copy(nombre = nuevoValor)
                    "sets" -> ejercicio?.copy(sets = nuevoValor.toIntOrNull() ?: 0)
                    "repeticiones" -> ejercicio?.copy(repeticiones = nuevoValor.toIntOrNull() ?: 0)
                    "peso" -> ejercicio?.copy(peso = nuevoValor.toIntOrNull() ?: 0)
                    "anotaciones" -> ejercicio?.copy(anotaciones = nuevoValor)
                    "imagen" -> ejercicio?.copy(imagen = nuevoValor)
                    "urlVideoEjemplo" -> ejercicio?.copy(urlVideoEjemplo = nuevoValor)
                    else -> ejercicio
                }
            }
        }
    }

    //mostrara modal para eliminar cargando nombre de ej seleccionado o hara toast si no hay ej selccionado
    fun desplegarModalEliminarEj() {
        if (_ejercicioSeleccionado.value != null) {
            _mostrarModalEliminar.update { true }
        } else {
            activarToast("Seleccione ejercicio para eliminar")
        }
    }

    //eliminara un ej o no en base a decision, en caso afirmativo eliminara ejercicio y recargara datos de vm
    //en caso negativo retira modal
    fun eliminarSegunDecicionModal(decision: Boolean) {
        viewModelScope.launch {

            if (decision) {
                activarToast("Ejercicio eliminado")
                _ejercicioSeleccionado.value?.nombre.let {
                    ejercicioUseCases.eliminarEjercicio(
                        UsuarioActualSingleton.idUsuarioActual,
                        it.toString()
                    )
                }
                _mostrarModalEliminar.update { false }
                _mostrarModalCarga.update { true }
                resetViewModel()
                cargarDatos()
            } else {
                _mostrarModalEliminar.update { false }
            }
        }
    }

    //cargara los datos iniciales del vm
    fun cargarDatos() {

        viewModelScope.launch {

            _listaEjsPer.update {
                ejercicioUseCases.obtenerEjPersonalizadosUsuario(
                    UsuarioActualSingleton.idUsuarioActual
                )
            }
            val listaEjsConjuntos =
                ejercicioUseCases.obtenerEjConjuntosUsuario(UsuarioActualSingleton.idUsuarioActual)

            _idMaxExistente.value = listaEjsConjuntos.maxByOrNull { it.id }?.id

            _estadoToast.update { EstadoToast(hacerToast = false, mensajeToast = "") }

            //preparo el ej para el crear modal, mejor tenerlo preparado a esperar que el user pulse crear
            _ejercicioPlantilla.value = Ejercicio(
                0,
                nombre = "Nombre ejercicio",
                imagen = "",
                sets = 1,
                repeticiones = 1,
                peso = 1,
                anotaciones = "",
                urlVideoEjemplo = ""
            )
            _ejercicioParaModal.value = _ejercicioPlantilla.value

            _mostrarModalCarga.update { false }
        }
    }

    //reinicia datos vm
    fun resetViewModel() {
        _estadoToast.update { null }
        _mostrarModalCarga.update { true }
        _mostrarModalCrear.update { false }
        _mostrarModalEditar.update { false }
        _mostrarModalEliminar.update { false }
        _ejercicioParaModal.update { null }
        _ejercicioSeleccionado.update { null }
        _listaEjsPer.update { emptyList() }
        _escrituraNombreEjModal.update { false }
        _idMaxExistente.update { 0 }
    }

}