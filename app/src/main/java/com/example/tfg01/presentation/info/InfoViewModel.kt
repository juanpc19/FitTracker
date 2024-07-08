package com.example.tfg01.presentation.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfg01.data.model.Consejo
import com.example.tfg01.domain.model.EstadoDesplegable
import com.example.tfg01.domain.usecases.consejo.ConsejoUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val consejoUseCases: ConsejoUseCases,
) : ViewModel() {

    //guarda los consejos a mostrar en vista
    private var _consejos: MutableStateFlow<List<Consejo>> = MutableStateFlow(emptyList())
    val consejos = _consejos.asStateFlow()

    //guarda consejos relacionados con nutricion solo para vm para evitar tener que filtrarlos de nuevo
    private var _consejosNutricionSalud: MutableStateFlow<List<Consejo>> =
        MutableStateFlow(emptyList())

    //guarda todos consejos solo para vm para evitar tener que filtrarlos de nuevo
    private var _consejosTodos: MutableStateFlow<List<Consejo>> =
        MutableStateFlow(emptyList())
    //guarda consejos relacionados con entrenamiento solo para vm para evitar tener que filtrarlos de nuevo
    private var _consejosEntrenamiento: MutableStateFlow<List<Consejo>> =
        MutableStateFlow(emptyList())

    //controla si se muestra modal de carga
    private var _mostrarModalCarga: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val mostrarModalCarga = _mostrarModalCarga.asStateFlow()

    //contendra el estado de los desplegables de las cards con consejo
    //sera usado en lazy column, tendra ids asignados a cada item en lazy column proveniente del consejo
    private val _estadosDeDesplegables = MutableStateFlow<List<EstadoDesplegable>>(emptyList())
    val estadosDeDesplegables = _estadosDeDesplegables.asStateFlow()


    //dara a consejos a mostrar en vista valor de consejos de nutricion y recargara los desplegables
    fun mostrarConsejosNutricionSalud() {
        _mostrarModalCarga.update { true }
        _consejos.update { _consejosNutricionSalud.value }

        recargarEstadosDesplegables()

        _mostrarModalCarga.update { false }
    }

    //dara a consejos a mostrar en vista valor de consejos de entrenamiento y recargara los desplegables
    fun mostrarConsejosEntrenamiento() {
        _mostrarModalCarga.update { true }
        _consejos.update { _consejosEntrenamiento.value }

        recargarEstadosDesplegables()

        _mostrarModalCarga.update { false }
    }

    //dara a consejos a mostrar en vista valor de todos los consejos y recargara los desplegables
    fun mostrarConsejosTodos() {
        _mostrarModalCarga.update { true }
        _consejos.update { _consejosTodos.value }

        recargarEstadosDesplegables()

        _mostrarModalCarga.update { false }
    }

    //alterna el valor del desplegable indicado por id
    fun alternarEstadoDesplegable(idConsejo: Int) {
        viewModelScope.launch {
            _estadosDeDesplegables.value = _estadosDeDesplegables.value.map { estadoDesplegable ->
                if (estadoDesplegable.id == idConsejo) {
                    estadoDesplegable.copy(estado = !estadoDesplegable.estado)
                } else {
                    estadoDesplegable
                }
            }
        }
    }

    //reiniciara el estado de todos los desplegables de los consejos en las cards
    private fun recargarEstadosDesplegables() {
        val listaConsejos = _consejos.value
        _estadosDeDesplegables.update { emptyList() }
        _estadosDeDesplegables.value =
            listaConsejos.map { EstadoDesplegable(it.id, false) }
    }

    //carga los datos iniciales del vm
    fun cargarDatos() {
        viewModelScope.launch {
            val listaConsejos = consejoUseCases.obtenerConsejos()
            _consejos.value = listaConsejos
            //preparo los estados de los desplegables
            _estadosDeDesplegables.value =
                listaConsejos.map { EstadoDesplegable(it.id, false) }

            _consejosTodos.update { listaConsejos }

            _consejosNutricionSalud.update { listaConsejos.filter { it.topico == "nutricion y salud" } }

            _consejosEntrenamiento.update { listaConsejos.filter { it.topico == "entrenamiento" } }

            _mostrarModalCarga.update { false }
        }
    }

    //reinicia datos de vm
    fun resetViewModel() {
        _mostrarModalCarga.update { true }
        _consejos.update { emptyList() }
        _consejosEntrenamiento.update { emptyList() }
        _consejosNutricionSalud.update { emptyList() }
        _consejosTodos.update { emptyList() }
        _estadosDeDesplegables.update { emptyList() }
    }

}