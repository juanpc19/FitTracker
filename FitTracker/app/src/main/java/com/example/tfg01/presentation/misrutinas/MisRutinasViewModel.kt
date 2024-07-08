package com.example.tfg01.presentation.misrutinas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfg01.data.model.Rutina
import com.example.tfg01.domain.model.EstadoToast
import com.example.tfg01.domain.model.UsuarioActualSingleton
import com.example.tfg01.domain.usecases.rutina.RutinaUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MisRutinasViewModel @Inject constructor(private val rutinaUseCases: RutinaUseCases) :
    ViewModel() {

    private var _rutinas: MutableStateFlow<List<Rutina>> = MutableStateFlow(emptyList())
    val rutinas = _rutinas.asStateFlow()

    private var _rutinaSeleccionada = MutableStateFlow<Rutina?>(null)
    val rutinaSeleccionada = _rutinaSeleccionada.asStateFlow()

    //se usara para controlar visualizacion de modal de carga en la vista
    private var _mostrarModalCarga = MutableStateFlow(true)
    val mostrarModalCarga = _mostrarModalCarga.asStateFlow()

    //se usara para controlar visualizacion de modal de confimar eliminar en la vista
    private var _mostrarModalConfirmacion = MutableStateFlow(false)
    val mostrarModalConfirmacion = _mostrarModalConfirmacion.asStateFlow()

    //se usara para saber si debe hacerse toast y que mensaje poner en el desde la vista
    private var _estadoToast = MutableStateFlow<EstadoToast?>(null)
    val estadoToast = _estadoToast.asStateFlow()

    //se usara para controlar navegacion a editar rutina
    private var _navToEditarRutina  = MutableStateFlow(false)
    val navToEditarRutina = _navToEditarRutina.asStateFlow()

    //cargara las rutinas del usuario
    fun cargarRutinas(userId: String) {
        viewModelScope.launch {
            val rutinasDelUsuario = rutinaUseCases.buscarRutinasUsuario(userId)
            _rutinas.update { rutinasDelUsuario }
            //preparo el estado de hacer toasts
            _estadoToast.update { EstadoToast(hacerToast = false, mensajeToast = "") }

            _mostrarModalCarga.update { false }
        }
    }

    //establece la rutina cuyo switch se pulsa como activa y desactiva las demas recargando vm
    fun establecerRutinaComoActiva(userId: String, nombre: String) {
        viewModelScope.launch {
            _rutinas.update { emptyList() }
            _mostrarModalCarga.update { true }
            rutinaUseCases.activarRutina(userId, nombre) //reescribe rutinas en fb
            resetViewModel()
            cargarRutinas(userId)
            activarToast("Rutina seleccionada activada")
        }
    }

    //establece la rutina pulsada como seleccionada
    fun setRutinaSeleccionada(rutina: Rutina) {
        _rutinaSeleccionada.update { rutina }
    }

    //TODO COMPROBAR QUE RUTINA NO ES NULL Y SI NO ES NULL HACER NAV, SI ES NULL COMUNICAR CON TOAST, NECESITO EL METODO Y UN BOOL DE NAV
    fun validarNavegacion () {
        if (_rutinaSeleccionada.value!=null) {
            _navToEditarRutina.update { true }
        } else {
            activarToast("Selecciona una rutina para editarla")
        }
    }

    //hace toast o muestra modal en base a si se ha seleccionado una rutina y si la rutina esta o no activa
    fun desplegarModalConfirmacion() {
        val rutinaActiva = _rutinaSeleccionada.value?.rutinaActiva

        when {
            rutinaActiva == true -> {
                activarToast("¡Desactiva la rutina antes de eliminarla!")
            }
            _rutinaSeleccionada.value == null -> {
                activarToast("Seleccione la rutina que desea eliminar")
            }
            else -> {
                _mostrarModalConfirmacion.update { true }
            }
        }
    }

    //elimina rutina o no en base a decision
    fun accionModalEliminar(decision: Boolean) {

        viewModelScope.launch {
            //comprueba que rutina a eliminar no sea la activa antes de proceder
            if (decision) {
                _mostrarModalConfirmacion.update { false }
                _mostrarModalCarga.update { true }
                _rutinaSeleccionada.value?.let {
                    rutinaUseCases.eliminarRutinaSeleccionada(
                        UsuarioActualSingleton.idUsuarioActual,
                        it.nombre
                    )
                }
                activarToast("Rutina eliminada")

                _rutinaSeleccionada.update { null }

                cargarRutinas(UsuarioActualSingleton.idUsuarioActual)
            }
            //de lo contrario retira el modal
            else {
                _mostrarModalConfirmacion.update { false }
            }
        }
    }

    //modificara el bool de ToastEstado.hacerToast para que el efecto se compruebe en IU
    //hara toast si true y con valor de ToastEstado.mensaje dejando all listo al efecto
    private fun activarToast(mensaje: String) {
        _estadoToast.update { estadoActual ->
            estadoActual?.copy(hacerToast = true, mensajeToast = mensaje)
        }
    }

    // Método para resetear el estado del toast después de mostrarlo
    fun resetearToast() {
        _estadoToast.update { estadoActual ->
            estadoActual?.copy(hacerToast = false, mensajeToast = "")
        }
    }

    //reinicia datos de vm
    fun resetViewModel() {
        _navToEditarRutina.update { false }
        _mostrarModalCarga.update { true }
        _rutinas.update { emptyList() }
        _rutinaSeleccionada.update { null }
        _mostrarModalConfirmacion.update { false }
        _estadoToast.update { null }
    }
}