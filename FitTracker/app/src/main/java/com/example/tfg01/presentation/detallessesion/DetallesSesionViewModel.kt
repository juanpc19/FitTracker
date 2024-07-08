package com.example.tfg01.presentation.detallessesion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfg01.data.model.Ejercicio
import com.example.tfg01.data.model.Sesion
import com.example.tfg01.domain.usecases.ejercicio.EjercicioUseCases
import com.example.tfg01.domain.usecases.sesion.SesionUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetallesSesionViewModel @Inject constructor(
    private val sesionUseCases: SesionUseCases,
    private val ejercicioUseCases: EjercicioUseCases
) :
    ViewModel() {

        //sesion a mostrar
    private var _sesion = MutableStateFlow<Sesion?>(null)
    val sesion = _sesion.asStateFlow()

    //ejercicios de la sesion mostrada
    private var _ejercicios: MutableStateFlow<List<Ejercicio>> = MutableStateFlow(emptyList())
    val ejercicios = _ejercicios.asStateFlow()

    //controlara si se muestra el modal de detalles de ej
    private var _mostrarModal = MutableStateFlow(false)
    val mostrarModal = _mostrarModal.asStateFlow()

    private var _ejercicioSeleccionado = MutableStateFlow<Ejercicio?>(null)
    val ejercicioSeleccionado = _ejercicioSeleccionado.asStateFlow()

    //carga la sesion y sus ejercicios para el vm
    fun cargarEjerciciosSesion(
        userId: String, nombreRutina: String, dia: String
    ) {
        viewModelScope.launch {
            val ejerciciosDeSesion =
                ejercicioUseCases.obtenerEjPorSesionPorRutina(userId, nombreRutina, dia)
            _ejercicios.update { ejerciciosDeSesion }

            val sesionSeleccionada =
                sesionUseCases.obtenerSesionSeleccionada(userId, nombreRutina, dia)
            _sesion.update { sesionSeleccionada }
        }
    }

    //actualiza ejercicio selecionado con el pulsado y muestra modal de detalles con el mismo
    fun mostrarModal(ejercicio: Ejercicio) {
        _ejercicioSeleccionado.update { ejercicio }
        _mostrarModal.update { true }
    }

    fun ocultarModal() {
        _mostrarModal.update { false }
    }

    //reinicia datos de vm
    fun resetViewModel() {
        _mostrarModal.update { false }
        _sesion.update { null }
        _ejercicios.update { emptyList() }
        _ejercicioSeleccionado.update { null }
    }
}