package com.example.tfg01.presentation.crearrutina

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfg01.data.model.Ejercicio
import com.example.tfg01.data.model.Rutina
import com.example.tfg01.data.model.Sesion
import com.example.tfg01.domain.model.EstadoDesplegable
import com.example.tfg01.domain.model.EstadoToast
import com.example.tfg01.domain.model.RutinaConSesConEjs
import com.example.tfg01.domain.model.SesionConEjercicios
import com.example.tfg01.domain.model.UsuarioActualSingleton
import com.example.tfg01.domain.usecases.ejercicio.EjercicioUseCases
import com.example.tfg01.domain.usecases.rutina.RutinaUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CrearRutinaViewModel @Inject constructor(
    private val rutinaUseCases: RutinaUseCases,
    private val ejercicioUseCases: EjercicioUseCases
) :
    ViewModel() {
    //guardara la rutina con lista de de sesiones cada una con su lista ejs para pasarla
    // a use case de guardar la rutina al final de la creacion de la misma
    private var _rutinaConSesConEjs = MutableStateFlow<RutinaConSesConEjs?>(null)
    val rutinaConSesConEjs = _rutinaConSesConEjs.asStateFlow()

    //guardar la lista de sesiones de la rutina para facilidad de uso en lazy column de vista
    // principal se actualizara junto a la lista de sesiones de ejs de rutina
    private var _listaSesionesConEjs = MutableStateFlow<List<SesionConEjercicios>>(emptyList())
    val listaSesionesConEjs = _listaSesionesConEjs.asStateFlow()

    //guardara valores default a usar en carga inicial y cuando usuario vuelva a marcar una sesion como descanso
    private var _listaSesConEjsPlantilla = MutableStateFlow<List<SesionConEjercicios>>(emptyList())

    //guardara la informacion de la sesion pulsada para mostrarla en el modal crear sesion
    // y guardarla en _listaSesionesConEjs cuando user pulse guardar
    private var _sesionConEjsParaModal = MutableStateFlow<SesionConEjercicios?>(null)
    val sesionConEjsParaModal = _sesionConEjsParaModal.asStateFlow()

    //contendra el estado de los desplegables mostrados en el modal para crear sesion
    //sera usado en lazy column de este modal, tendra ids de ejs de _sesionConEjsParaModal
    private val _estadosDeDesplegables = MutableStateFlow<List<EstadoDesplegable>>(emptyList())
    val estadosDeDesplegables = _estadosDeDesplegables.asStateFlow()

    //guardara los ejs del usuario obtenidos de fb para mostrarlos en cada desplegable del modal crear sesion
    //contiene los ejs base de la app y los personalizados del usuario
    private var _ejerciciosUsuario = MutableStateFlow<List<Ejercicio>>(emptyList())
    val ejerciciosUsuario = _ejerciciosUsuario.asStateFlow()

    //sera usado para controlar visualizacion de modal de carga
    private var _cargaDatosInicialFinalizada = MutableStateFlow(false)
    val cargaDatosInicialFinalizada = _cargaDatosInicialFinalizada.asStateFlow()

    //se pondra a true cuando una sesion de las 7 haya sido validada, lo cual validara el contenido,
    //posteriormente se comprobara el nombre al intentar guardar
    private var _contenidoRutinaValidado = MutableStateFlow(false)

    //se pondra a true cuando se selecione al menos un ejercicio en el modal de sesion y se le de nombre a sesion,
    //cuando este modal se cierre se pondra a false para ser usado en la siguiente apertura
    //de modal sesion para comprobar nuevamente la cumplimentacion del formulario
    private var _sesionValidada = MutableStateFlow(false)

    //sera usado para controlar visualizacion de modal de crear sesion
    private var _mostrarModalSesion = MutableStateFlow(false)
    val mostrarModalSesion = _mostrarModalSesion.asStateFlow()

    //sera usado para controlar visualizacion de modal guardar rutina para establecerla como activa o no
    private var _mostrarModalGuardarRutina = MutableStateFlow(false)
    val mostrarModalGuardarRutina = _mostrarModalGuardarRutina.asStateFlow()

    //se usara para saber si debe hacerse toast y que mensaje poner en el desde la vista
    private var _estadoToast = MutableStateFlow<EstadoToast?>(null)
    val estadoToast = _estadoToast.asStateFlow()

    //controlara la navegacion a vista mis rutinas, se pondra a true al crear rutina
    private val _navegarToMisRutinas = MutableStateFlow(false)
    val navegarToMisRutinas = _navegarToMisRutinas.asStateFlow()

    //recibira un string cuando el usuario escriba en textfield de rutina y lo usara para actualizar nombre de rutina
    fun actualizarNombreRutina(nuevoNombre: String) {
        _rutinaConSesConEjs.update { rutinaConSesConEjs ->
            rutinaConSesConEjs?.let {
                val rutinaActualizada = it.rutina?.copy(nombre = nuevoNombre)
                it.copy(rutina = rutinaActualizada)
            }
        }
    }

    //recibira un string cuando el usuario escriba en textfield de sesion y lo usara para actualizar nombre de sesion
    fun actualizarNombreSesionModal(nuevoNombre: String) {
        _sesionConEjsParaModal.update { sesionConEjsParaModal ->
            sesionConEjsParaModal?.let {
                val sesionActualizada = it.sesion?.copy(nombre = nuevoNombre)
                it.copy(sesion = sesionActualizada)
            }
        }
    }

    //actualizara el check de la sesion correspondiente en base a boolean de campo descanso de sesion,
    //si pasa de no descanso (sin check) a descanso (con check) reiniciara la sesion a default
    //aplica cambios a lista sesiones con ejercicios,  reemapea la lista entera
    fun actualizarDescansoSesion(sesionConEjsPulsada: SesionConEjercicios) {
        _listaSesionesConEjs.update { listaSesionesConEjs ->
            listaSesionesConEjs.map { sesionConEjs ->
                if (sesionConEjs.sesion?.dia == sesionConEjsPulsada.sesion?.dia) {
                    //si la sesion coincide en dia copio la sesion poniendola a false
                    if (sesionConEjsPulsada.sesion?.descanso == true) {
                        sesionConEjs.copy(
                            sesion = sesionConEjs.sesion?.copy(
                                descanso = false // reinicio el valor de descanso
                            )
                        )
                    } else {//copio la sesion poniendola a true restableciendo su valor a default
                        //tanto su nombre como su campo descanso y la list ejs asociada
                        val sesionConEjsDefault = _listaSesConEjsPlantilla.value.find {
                            it.sesion?.dia == sesionConEjs.sesion?.dia
                        }
                        sesionConEjs.copy(
                            sesion = sesionConEjsDefault?.sesion,
                            listaEjercicios = sesionConEjsDefault!!.listaEjercicios
                        )
                    }
                } else {//copio la sesion tal como estaba porque no es la que hay que cambiar
                    sesionConEjs
                }
            }
        }

    }

    // alternara el estado boolean del desplegable del ej indicado mediante id para mostrarlo/ocultarlo
    fun alternarEstadoDesplegable(idEjercicio: Int) {
        viewModelScope.launch {
            _estadosDeDesplegables.value = _estadosDeDesplegables.value.map { estadoDesplegable ->
                if (estadoDesplegable.id == idEjercicio) {
                    estadoDesplegable.copy(estado = !estadoDesplegable.estado)
                } else {
                    estadoDesplegable
                }
            }
        }
    }

    // Método para actualizar el campo id de de un estado desplegable,
    // lo hara sobre el id antiguo machacandolo con el nuevo, reemapea la lista entera
    private fun actualizarIdEnEstadosDesplegables(idAnterior: Int, idNuevo: Int) {

        viewModelScope.launch {
            _estadosDeDesplegables.value = _estadosDeDesplegables.value.map { estadoDesplegable ->
                if (estadoDesplegable.id == idAnterior) {
                    estadoDesplegable.copy(id = idNuevo, estado = false)
                } else {
                    estadoDesplegable
                }
            }
        }
    }

    // actualizara un ejercicio específico dentro del modal crear sesion usando el ejercicio recibido
    // para escribir sus datos sobre el que coincida el id recibido al seleccionar un ejercicio del desplegable,
    // reemapea la lista entera, permite seleccion si ej no existe en lista, hace toast en caso contrario
    //tambien actualiza los ids y estados de desplegables
    fun actualizarSesionModalConEjercicio(idEjercicio: Int, ejercicioDesplegable: Ejercicio) {
        viewModelScope.launch {
            //guarda true si existe ejercicio en lista ejs de sesion else false
            val ejercicioExistenteEnSesion =
                _sesionConEjsParaModal.value?.listaEjercicios?.any { it.id == ejercicioDesplegable.id }
                    ?: false

            //si el ej no esta ya en la sesion permito seleccionarlo de lo contrario no porque conduce a errores
            if (!ejercicioExistenteEnSesion) {
                _sesionConEjsParaModal.value?.let { sesionConEjs ->
                    val updatedEjercicios = sesionConEjs.listaEjercicios.map { ejercicio ->
                        if (ejercicio.id == idEjercicio) {
                            ejercicio.copy(
                                nombre = ejercicioDesplegable.nombre,
                                imagen = ejercicioDesplegable.imagen,
                                anotaciones = ejercicioDesplegable.anotaciones,
                                sets = ejercicioDesplegable.sets,
                                repeticiones = ejercicioDesplegable.repeticiones,
                                peso = ejercicioDesplegable.peso,
                                urlVideoEjemplo = ejercicioDesplegable.urlVideoEjemplo,
                                id = ejercicioDesplegable.id
                            )
                        } else {
                            ejercicio
                        }
                    }

                    _sesionConEjsParaModal.update { sesionConEjercicios ->
                        sesionConEjercicios?.copy(//remapeo la sesion con
                            listaEjercicios = updatedEjercicios
                        )
                    }
                    actualizarIdEnEstadosDesplegables(idEjercicio, ejercicioDesplegable.id)
                }
            } else {
                activarToast("Esa ejercicio ya existe en la sesión")
            }
        }
    }

    // actualizara el campo indicado del ejercicio cuya id coincida usando nuevoValor para ello cuando
    // se escriba en un textfield del ejercicio, hara switch para evaluar que campos modificar
    //y aplicara cambios a _sesionConEjsParaModal
    fun actualizarCampoEjercicio(idEjercicio: Int, campo: String, nuevoValor: String) {

        viewModelScope.launch {
            _sesionConEjsParaModal.value?.let { sesionConEjs ->
                val updatedEjercicios = sesionConEjs.listaEjercicios.map { ejercicio ->
                    if (ejercicio.id == idEjercicio) {
                        when (campo) {
                            "anotaciones" -> ejercicio.copy(anotaciones = nuevoValor)
                            "sets" -> ejercicio.copy(sets = nuevoValor.toIntOrNull() ?: 0)
                            "repeticiones" -> ejercicio.copy(
                                repeticiones = nuevoValor.toIntOrNull() ?: 0
                            )

                            "peso" -> ejercicio.copy(peso = nuevoValor.toIntOrNull() ?: 0)
                            else -> ejercicio
                        }
                    } else {
                        ejercicio
                    }
                }
                _sesionConEjsParaModal.value =
                    sesionConEjs.copy(listaEjercicios = updatedEjercicios)
            }
        }
    }

    //muestra modal con los datos de la sesion pulsada si esta tiene el campo descanso a false
    //de lo contrario hara toast indicando desmarcar casilla descanso de sesion pulsada
    fun desplegarModalSesion(sesionConEjsPulsada: SesionConEjercicios) {

        if (sesionConEjsPulsada.sesion?.descanso == false) { //si campo descanso de sesion es false

            //guardo valores de sesion con ejs pulsada en variables
            val listaEjs = sesionConEjsPulsada.listaEjercicios.map { it.copy() }
            val sesion = sesionConEjsPulsada.sesion.copy()

            //y actualizo la _sesionConEjsParaModal con dichos valores
            _sesionConEjsParaModal.update { sesionConEjercicios ->
                //en caso de que _sesionConEjsParaModal ya haya sido usada/instanciada le doy valores
                sesionConEjercicios?.copy(
                    sesion = sesion,
                    listaEjercicios = listaEjs
                    //encaso de _sesionConEjsParaModal NO haya sido instanciada la instancio y doy valores
                ) ?: SesionConEjercicios(
                    sesion = sesion,
                    listaEjercicios = listaEjs
                )
            }
            // actualizo la lista de estados desplegables con ids de ejs de sesion con ejs
            _sesionConEjsParaModal.value?.let { sesionConEjs ->
                val nuevosEstadosDesplegables = sesionConEjs.listaEjercicios.map { ejercicio ->
                    EstadoDesplegable(ejercicio.id, false)
                }
                _estadosDeDesplegables.value = nuevosEstadosDesplegables
            }

            _mostrarModalSesion.update { true } //muestro modal
            _sesionValidada.update { false } // preparo validacion
        } else {
            activarToast("Desmarque descanso para desplegar sesión")
        }
    }

    //cerrara el modal crear sesion al pulsar cancelar, sin guardar nada
    fun cerrarModalSesion() {
        _mostrarModalSesion.update { false }
    }

    //recibe sesionConEjercicios cargada en modal crear sesion al pulsar boton guardar sesion,
    // comprueba si es valida, actualiza su valor en _listaSesionesConEjs y cierra modal
    //hara toast si sesion no ha recibido nombre o si no se ha selecionado al menos un ej de un desplegable
    fun guardarSesion(sesionConEjerciciosRecibida: SesionConEjercicios) {

        //si alguno de los ejs no tiene nombre default "-Selecciona un ejercicio-" guarda bool true else guarda false
        val sesionTieneEjercicio =
            sesionConEjerciciosRecibida.listaEjercicios.any { it.nombre != "-Selecciona un ejercicio-" }
        val nombreSesion = sesionConEjerciciosRecibida.sesion?.nombre

        //comprobaciones para validar sesion y hacer toast si no es valida
        if (sesionTieneEjercicio && !nombreSesion.isNullOrBlank()) {
            _sesionValidada.update { true } //doy a _sesionValidada valor de comprobacion
        } else {
            activarToast("Debe seleccionar al menos 1 ejercicio y darle nombre a la sesion")
            _sesionValidada.update { false }
        }

        //si la sesion esta validada actualizo lista sesiones en base a sesionConEjerciciosRecibida
        if (_sesionValidada.value) {
            _listaSesionesConEjs.update { listaSesionesConEjs ->
                listaSesionesConEjs.map { sesion ->
                    if (sesion.sesion?.dia == sesionConEjerciciosRecibida.sesion?.dia) {
                        sesionConEjerciciosRecibida
                    } else {
                        sesion
                    }
                }
            }
            //updateo _rutinaConSesConEjs en base a nuevo value de _listaSesionesConEjs
            _rutinaConSesConEjs.update { rutinaConSesConEjs ->
                rutinaConSesConEjs?.copy(listaSesConListaEjs = _listaSesionesConEjs.value)
            }

            _mostrarModalSesion.update { false } //y cierro el modal de sesion
        }
    }

    //comprueba si las sesiones esta creadas correctamente en base a si tienen nombre en caso de ser no descanso
    // (si tienen nombre se tambien tienen ejs) y si no hay 7 que sean descanso
    private fun verificarSesiones(listaSesionesConEjs: List<SesionConEjercicios>): Boolean {
        var sesionesCorrectas = true
        var cuentaDescansos = 0

        // recorro la lista de sesiones
        for (sesionConEjs in listaSesionesConEjs) {
            // verifico si la sesión no es descanso
            if (sesionConEjs.sesion?.descanso == false) {
                // verifico si el nombre de la sesión es nulo o está en blanco
                if (sesionConEjs.sesion.nombre.isNullOrBlank()) {
                    // si encuentro una sesión que no es descanso y su nombre es nulo o está en blanco retorno false
                    sesionesCorrectas = false
                }
            } else {
                cuentaDescansos += 1
            }
        }
        if (cuentaDescansos == 7) {
            sesionesCorrectas = false
        }
        // Si no se encontraron sesiones con nombre nulo o en blanco, devolvemos true
        return sesionesCorrectas
    }

    //mostrara modal con confirmacion de establecer rutina como activa si rutina ha recibido nombre
    //y su contenido ha sido validado, de lo contrario hara toast correspondiente indicado pasos a seguir
    fun desplegarModalGuardarRutina() {
        viewModelScope.launch {
            val nombreRutina = _rutinaConSesConEjs.value?.rutina?.nombre.toString()

            //metodo que comprueba si ya existe una rutina con ese nombre
            val rutinaExistente = rutinaUseCases.comprobarExistenciaRutina(
                UsuarioActualSingleton.idUsuarioActual,
                nombreRutina
            )
            _contenidoRutinaValidado.update { verificarSesiones(_listaSesionesConEjs.value) }

            //guardo menaje de error en base a case de switch activado y lo uso tras swtich
            val mensajeError = when {
                nombreRutina.isBlank() -> "Introduzca nombre de rutina"
                rutinaExistente -> "Ya existe una rutina con ese nombre"
                !_contenidoRutinaValidado.value -> "Debes crear al menos una sesion y toda sesion debe tener nombre"
                //si no se da ningun case no hay erorr, por lo que guardo rutina y toast indicandolo
                else -> {
                    // Si todas las validaciones pasan, mostrar el modal
                    _mostrarModalGuardarRutina.update { true }
                    return@launch //sale del switch
                }
            }
            activarToast(mensajeError)
        }
    }

    //recibira un bool del modal guardar rutina en IU, si true rutina activa si false rutina no activa
    //y acto seguido se hara nav a mis Rutinas en la IU tras hacer este metodo
    fun guardarRutina(activarRutina: Boolean) {
        viewModelScope.launch {
            //actualizo campos rutina activa y userId de rutina, llegados aqui ya tiene campo nombre actualizado
            _rutinaConSesConEjs.update { rutinaConSesConEjs ->
                rutinaConSesConEjs?.let {
                    val rutinaActualizada = it.rutina?.copy(
                        rutinaActiva = activarRutina,
                        userId = UsuarioActualSingleton.idUsuarioActual
                    )
                    it.copy(
                        rutina = rutinaActualizada,
                        listaSesConListaEjs = _listaSesionesConEjs.value
                    )
                }
            }

            val nombreRutina = _rutinaConSesConEjs.value?.rutina?.nombre

            //metodo que escribe la rutina/sesion/ejercicios en fb
            _rutinaConSesConEjs.value?.let { rutinaUseCases.crearRutina(it) }

            //establezco la rutina recien creada como activa tras comprobacion de nombre
            // (el ide se pone tonto si no la hago pero llegados aqui no deberia ser null)
            if (nombreRutina != null && activarRutina) {
                    rutinaUseCases.activarRutina(
                        UsuarioActualSingleton.idUsuarioActual,
                        nombreRutina
                    )
                //hago toast de guardada y activada si solicita activacion
                activarToast("Rutina guardada y activada")
                //toast de guardada de lo contrario
            } else {
                activarToast("Rutina guardada")
            }

            _mostrarModalGuardarRutina.update { false } //cerramos modal
            _cargaDatosInicialFinalizada.update { false } // y mostramos el de cargar mientras hacemos navegacion
            //hago navegacion
            _navegarToMisRutinas.update { true }

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
        _cargaDatosInicialFinalizada.update { false }
        _estadoToast.update { null }
        _navegarToMisRutinas.update { false }
        _mostrarModalSesion.update { false }
        _mostrarModalGuardarRutina.update { false }
        _rutinaConSesConEjs.update { null }
        _listaSesionesConEjs.update { emptyList() }
        _sesionConEjsParaModal.update { null }
        _ejerciciosUsuario.update { emptyList() }
        _sesionValidada.update { false }
        _contenidoRutinaValidado.update { false }
        _estadosDeDesplegables.update { emptyList() }
    }

    //prepara los datos a usar en ventana principal y en modal sesion
    fun cargarDatos() {
        //preparo los ejs del usuario de fb que son asincronos por lo que vm scope
        viewModelScope.launch {
            _ejerciciosUsuario.update {
                ejercicioUseCases.obtenerEjConjuntosUsuario(
                    UsuarioActualSingleton.idUsuarioActual
                )
            }

            //preparo plantilla de sesiones
            val sesionesPlantilla = listOf(
                Sesion(
                    dia = "Lunes",
                    diaNum = 1,
                    nombre = "",
                    descanso = true
                ),
                Sesion(
                    dia = "Martes",
                    diaNum = 2,
                    nombre = "",
                    descanso = true
                ),
                Sesion(
                    dia = "Miércoles",
                    diaNum = 3,
                    nombre = "",
                    descanso = true
                ),
                Sesion(
                    dia = "Jueves",
                    diaNum = 4,
                    nombre = "",
                    descanso = true
                ),
                Sesion(
                    dia = "Viernes",
                    diaNum = 5,
                    nombre = "",
                    descanso = true
                ),
                Sesion(
                    dia = "Sábado",
                    diaNum = 6,
                    nombre = "",
                    descanso = true
                ),
                Sesion(
                    dia = "Domingo",
                    diaNum = 7,
                    nombre = "",
                    descanso = true
                )
            )


            //preparo plantilla de lista ejs de todas las sesiones
            //ESTA LINEA DICTA CUANTOS EJS HABRA EN LA LISTA DE CADA SESION
            //cambiar 1000+i por 1 + index (cambiando i por index) y darle a ejs datos reales de los 6 primeros ejs si da problemas
            val ejerciciosPlantilla = List(6) { i ->
                Ejercicio(
                    id = 1000 + i,
                    nombre = "-Selecciona un ejercicio-",
                    imagen = "",
                    anotaciones = "",
                    sets = 0,
                    repeticiones = 0,
                    peso = 0,
                    urlVideoEjemplo = ""
                )
            }

            //preparo los estados de los desplegables
            _estadosDeDesplegables.value =
                ejerciciosPlantilla.map { EstadoDesplegable(it.id, false) }

            //creo una lista de sesiones con ejs
            val nuevaListaSesionesConEjs = mutableListOf<SesionConEjercicios>()
            // agregamos la nueva sesión a la lista de sesiones con ejercicios
            sesionesPlantilla.forEach { sesionPlantilla ->
                val nuevaSesionConEjs = SesionConEjercicios(
                    sesion = sesionPlantilla,
                    listaEjercicios = ejerciciosPlantilla // Mantenemos la misma lista de ejercicios
                )
                nuevaListaSesionesConEjs.add(nuevaSesionConEjs)
            }

            //y la uso para darle valor a la que usare a nivel vm en caso de que user vuelva a marcar una sesion como descanso
            _listaSesConEjsPlantilla.value = nuevaListaSesionesConEjs
            //y le doy a la "real" que guardara los datos modificados su valor porque inicialmente seran iguales inicialmente
            _listaSesionesConEjs.value = _listaSesConEjsPlantilla.value

            // doy valores iniciales a _rutinaConSesConEjs a partir de datos manuales y _listaSesionesConEjs
            _rutinaConSesConEjs.update {
                RutinaConSesConEjs(
                    rutina = Rutina(userId = "", nombre = "", rutinaActiva = false),
                    listaSesConListaEjs = _listaSesionesConEjs.value
                )
            }

            //preparo el estado de hacer toasts
            _estadoToast.update { EstadoToast(hacerToast = false, mensajeToast = "") }

            //preparo la navegacion
            _navegarToMisRutinas.update { false }

            //confirmo final de carga de datos inicial
            _cargaDatosInicialFinalizada.update { true }
        }
    }
}