package com.example.tfg01.presentation.cargarperfil

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfg01.data.model.Rutina
import com.example.tfg01.data.model.Sesion
import com.example.tfg01.presentation.signin.UserData
import com.example.tfg01.data.model.Usuario
import com.example.tfg01.domain.model.EstadoToast
import com.example.tfg01.domain.usecases.usuario.UsuarioUseCases
import com.example.tfg01.domain.model.UsuarioActualSingleton
import com.example.tfg01.domain.usecases.rutina.RutinaUseCases
import com.example.tfg01.domain.usecases.sesion.SesionUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CargaPerfilViewModel @Inject constructor(
    private val usuarioUseCases: UsuarioUseCases,
    private val rutinaUseCases: RutinaUseCases,
    private val sesionUseCases: SesionUseCases
) : ViewModel() {

    //contendra valor de usuario actual
    private var _usuario = MutableStateFlow<Usuario?>(null)
    val usuario = _usuario.asStateFlow()

    //controlara si es necesario hacer registro
    private var _registroNecesario = MutableStateFlow(false)
    val registroNecesario = _registroNecesario.asStateFlow()

    //controlara si se ha comprobado existencia de usuario
    private var _checkUsuarioHecho = MutableStateFlow(false)
    val checkUsuarioHecho = _checkUsuarioHecho.asStateFlow()

    //tendra el valor de la rutina activa actual a mostrar en la vista
    private var _rutinaActual = MutableStateFlow<Rutina?>(null)
    val rutinaActual = _rutinaActual.asStateFlow()

    //contendra las sesiones a mostrar en la vista
    private var _sesiones: MutableStateFlow<List<Sesion>> = MutableStateFlow(emptyList())
    val sesiones = _sesiones.asStateFlow()

    //contendra la sesion pulsada para pasar sus datos por navegacion al pulsarla
    private var _sesionPulsada = MutableStateFlow<Sesion?>(null)
    val sesionPulsada = _sesionPulsada.asStateFlow()

    //controlara si ha de mostrarse el modal de carga
    private var _mostrarModalCarga = MutableStateFlow(true)
    val mostrarModalCarga = _mostrarModalCarga.asStateFlow()

    //se usara para saber si debe hacerse toast y que mensaje poner en el desde la vista
    private var _estadoToast = MutableStateFlow<EstadoToast?>(null)
    val estadoToast = _estadoToast.asStateFlow()

    //controlara si se hace la navegacion a vista detalles
    private val _navegarToDetallesSesion = MutableStateFlow(false)
    val navegarToDetallesSesion = _navegarToDetallesSesion.asStateFlow()


    //comprobara existencia de usuario en firestore devolviendo el usuario o null en base a id proporcionado
    //establecera el usuario en el singleton y en vm de encontrarlo y cargara sus datos y preparara vm,
    // de lo contrario indica registro necesario, independiente al resultado indica que check esta hecho
    fun obtenerUsuario(userId: String) {
        viewModelScope.launch {
            val busquedaUsuario = usuarioUseCases.buscarUsuario(userId)
            if (busquedaUsuario != null) {
                UsuarioActualSingleton.establecerUsuario(busquedaUsuario.userId) //en companion igualo porque no es un flujo
                _usuario.update { busquedaUsuario } //Actualizo el estado _usuario con el usuario encontrado
                cargarRutinaActual(UsuarioActualSingleton.idUsuarioActual)
            } else {
                _registroNecesario.update { true }
            }
            _checkUsuarioHecho.update { true }
        }
    }

    //registrara al usuario en firestore, usara sus datos para esblecer el usuario en singleton y vm
    //y prepara datos a usar por vm (solo simula carga y prepara el lanza toast)
    fun registrarUsuario(usuarioGoogleAuth: UserData) {
        var usuarioNuevo: Usuario

        viewModelScope.launch {
            usuarioNuevo = Usuario(
                userId = usuarioGoogleAuth.userId,
                userName = usuarioGoogleAuth.userName,
                email = usuarioGoogleAuth.email,
                profilePictureUrl = usuarioGoogleAuth.profilePictureUrl

            )
            UsuarioActualSingleton.establecerUsuario(usuarioNuevo.userId)
            usuarioUseCases.insertarUsuario(usuarioNuevo)
            _usuario.update { usuarioNuevo }
            cargarRutinaActual(UsuarioActualSingleton.idUsuarioActual)
        }
    }

    //cargara la rutina actual y sus sesiones, preparara el estado de los toast  y retirara el modal de carga al acabar
    fun cargarRutinaActual(userId: String) {
        viewModelScope.launch {

            val rutinaBuscada = rutinaUseCases.buscarRutinaActivaUsuario(userId)
            val sesionesRutinaBuscada = sesionUseCases.buscarSesionesRutinaActiva(userId)

            if (rutinaBuscada != null) {
                resetViewModelParcialOnNav()
                _estadoToast.update { EstadoToast(hacerToast = false, mensajeToast = "") }
                _rutinaActual.update { rutinaBuscada }
                _sesiones.update { sesionesRutinaBuscada }
            }
            _mostrarModalCarga.update { false }
        }
    }

    //comprueba que el campo descanso de sesion no es true para permitir navegacion a detalles sesion,
    //en caso de ser descanso true hace toast mas que razonable
    fun comprobarSesion(sesion: Sesion) {

        Log.d("SESION PULSADA VM","$sesion")

        viewModelScope.launch {
            _sesionPulsada.update { sesion }
            if (_sesionPulsada.value?.descanso == true) {
                activarToast("Sesion de descanso, ¡Sal a tomarte unas birras!")
            } else {
                _navegarToDetallesSesion.update { true }
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

    //se usara para resetear datos de vm parcialmente al llegar a su vista mediante navegacion sin
    //cerrar sesion, recargara datos y parara navegacion a detalles sesion sin reiniciar
    //datos y logica de registro, inicio sesion y usuario
    fun resetViewModelParcialOnNav() {
        _navegarToDetallesSesion.update { false }
        _mostrarModalCarga.update { true }
        _estadoToast.update { null }
        _rutinaActual.update { null }
        _sesiones.update { emptyList() }
        _sesionPulsada.update { null }
    }

    //reinicia vm por completo sera usado al hacer sign out
    fun resetViewModel() {
        _navegarToDetallesSesion.update { false }
        _mostrarModalCarga.update { true }
        _estadoToast.update { null }
        _usuario.update { null }
        _registroNecesario.update { false }
        _checkUsuarioHecho.update { false }
        _rutinaActual.update { null }
        _sesiones.update { emptyList() }
        _sesionPulsada.update { null }
    }
}