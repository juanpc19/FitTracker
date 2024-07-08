package com.example.tfg01.presentation

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tfg01.domain.model.UsuarioActualSingleton
import com.example.tfg01.presentation.cargarperfil.CargaPerfilViewModel
import com.example.tfg01.presentation.cargarperfil.PantallaPerfil
import com.example.tfg01.presentation.crearrutina.CrearRutina
import com.example.tfg01.presentation.crearrutina.CrearRutinaViewModel
import com.example.tfg01.presentation.detallessesion.DetallesSesion
import com.example.tfg01.presentation.detallessesion.DetallesSesionViewModel
import com.example.tfg01.presentation.editarrutina.EditarRutina
import com.example.tfg01.presentation.editarrutina.EditarRutinaViewModel
import com.example.tfg01.presentation.info.Info
import com.example.tfg01.presentation.info.InfoViewModel
import com.example.tfg01.presentation.misejercicios.MisEjercicios
import com.example.tfg01.presentation.misejercicios.MisEjerciciosViewModel
import com.example.tfg01.presentation.misrutinas.MisRutinas
import com.example.tfg01.presentation.misrutinas.MisRutinasViewModel
import com.example.tfg01.presentation.signin.GoogleAuthUiClient
import com.example.tfg01.presentation.signin.SignIn
import com.example.tfg01.presentation.signin.SignInViewModel
import com.example.tfg01.presentation.utils.navegation.RutasPantallas
import com.example.tfg01.ui.theme.Tfg01Theme
import com.google.android.gms.auth.api.identity.Identity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    //declaro el googleAuthUiClient aqui para usarlo en diferentes partes de navegacion
    private val googleAuthUiClient by lazy {//by lazy inicializa en primer acceso(similar a lateinit)
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Tfg01Theme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    //establezco aqui context para usarlo en algunos lambdas que hacen toast
                    val context = LocalContext.current
                    //establezco nav controller
                    val navController = rememberNavController()//declaro nav controller

                    //declaro e inicio variables para recoger el usuario del sign in y guardar su id para navegacion
                    var signedInUser = googleAuthUiClient.getSignedInUser()
                    var signInUserId = signedInUser?.userId

                    //declaro aqui este vm para usar su metodo resetvm cuando haga sign out y
                    // poder pasarlo a otros composables como un lamda,
                    // de lo contrario no es posible hacer log out en multiples partes de la app
                    val cargaPerfilViewModel by viewModels<CargaPerfilViewModel>()

                    //creo nav host con nav controller y le doy destino inicial
                    NavHost(
                        navController = navController,
                        startDestination = RutasPantallas.PantallaSignIn.route
                    ) {
                        composable(route = RutasPantallas.PantallaSignIn.route) {
                            //declaro vm que el composable va a usar dentro de la navegacion se instancia al navegar a esta vista
                            val signInViewModel by viewModels<SignInViewModel>()
                            val state by signInViewModel.state.collectAsStateWithLifecycle()//obtengo el state partir del vm

                            //doy valor a usuario y idusuario que estan haciendo sign in
                            signedInUser = googleAuthUiClient.getSignedInUser()
                            signInUserId = signedInUser?.userId
                            Log.d("TAG", "en sign in user google: $signedInUser $signInUserId")

                            //efecto que se lanzara si ya ha usuario logeado, si uso unit se lanza solo 1 vez
                            LaunchedEffect(Unit) {
                                if (googleAuthUiClient.getSignedInUser() != null) {//si da null es porque se ha hecho log out
                                    //navego a ventana de perfil
                                    navController.navigate(RutasPantallas.PantallaCargaPerfil.route)
                                }
                            }
                            //guarda un efecto que se lanzara al navegar a sign in pasandoselo en un lamdba,
                            //se creara a partir del resultado de una actividad determinada
                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),//establece el tipo de actividad
                                onResult = { result ->//recoge el resultado y dictamina codigo a ejecutar usando el resultado con un lambda
                                    if (result.resultCode == RESULT_OK) {
                                        lifecycleScope.launch {//inicia intento de sign in con intent dentro de otro lambda
                                            val signInResult = googleAuthUiClient.signInWithIntent(
                                                //esta etiqueta se usa para indicar retorno desde lamdba donde se use en este caso de null
                                                intent = result.data ?: return@launch
                                                //uso alternativo sale del efecto sin hacer siguiente linea de codigo
                                            )
                                            signInViewModel.onSingInResult(signInResult)//paso el resultado al view model updateando el state
                                        }
                                    }
                                }
                            )
                            //efecto que se lanzara cuando el estado del sign in cambie
                            LaunchedEffect(key1 = state.isSignInSuccessful) {
                                if (state.isSignInSuccessful) {//si es true tras cambio
                                    navController.navigate(RutasPantallas.PantallaCargaPerfil.route)
                                    signInViewModel.resetState()//y reseteo estado del VM para proximo usuario
                                }
                            }
                            //composable de pantallaSignIn al que paso nav controller, state de sign in y funcion onSignInClick de vm
                            SignIn(
                                navController,
                                state = state,
                                onSignInClick = {//creo lambda que pasare como onSignInClick
                                    lifecycleScope.launch {//lambda dentro de lambda
                                        val signInIntentSender = googleAuthUiClient.signIn()
                                        launcher.launch(//usara el efecto "recordado" en launcher
                                            IntentSenderRequest.Builder(
                                                //esta etiqueta se usa para indicar retorno desde lamdba donde se use en este caso el interno
                                                signInIntentSender ?: return@launch
                                            ).build()
                                        )
                                    }
                                }
                            )
                        }
                        composable(
                            route = RutasPantallas.PantallaCargaPerfil.route
                        ) {
                            //uso cargaPerfilViewModel aqui ya que pertenece a esta vista,
                            // aunque lo declare en activity por tema de pasar el lambda sign out
                            //recojo valores de vm a traves de atributos de solo lectura para pasarlos a vista
                            val registroNecesario by cargaPerfilViewModel.registroNecesario.collectAsStateWithLifecycle()
                            val usuario by cargaPerfilViewModel.usuario.collectAsStateWithLifecycle()
                            val checkUsuarioHecho by cargaPerfilViewModel.checkUsuarioHecho.collectAsStateWithLifecycle()
                            val rutinaActual by cargaPerfilViewModel.rutinaActual.collectAsStateWithLifecycle()
                            val sesiones by cargaPerfilViewModel.sesiones.collectAsStateWithLifecycle()
                            val mostrarModalCarga by cargaPerfilViewModel.mostrarModalCarga.collectAsStateWithLifecycle()
                            val sesionPulsada by cargaPerfilViewModel.sesionPulsada.collectAsStateWithLifecycle()
                            val estadoToast by cargaPerfilViewModel.estadoToast.collectAsStateWithLifecycle()
                            val navegarToDetallesSesion by cargaPerfilViewModel.navegarToDetallesSesion.collectAsStateWithLifecycle()

                            //cada vez que navego a esta vista lanzo efecto y si cambia valor de nav se lanza otra vez
                            // para reestear nav y no romper el pop back stack reinicio vm y cargo los datos de nuevo
                            //se lanzara en base a si se ha hecho navegacion a detalles sesion desde carga perfil
                            //hara reset parcial de vm porque usuario check usario y registro necesario solo
                            //se ersetearan si se hace log out
                            LaunchedEffect(navegarToDetallesSesion) {
                                if (navegarToDetallesSesion) {
                                    cargaPerfilViewModel.resetViewModelParcialOnNav()
                                    cargaPerfilViewModel.cargarRutinaActual(UsuarioActualSingleton.idUsuarioActual)
                                }
                            }

                            //se lanzara una vez y cada vez que cambie checkUsuarioHecho
                            LaunchedEffect(checkUsuarioHecho) {

                                //se comprueba si el check del usuario se ha hecho en el primer launch
                                if (!checkUsuarioHecho) {
                                    //si no se ha hecho se hace aqui
                                    signInUserId?.let {
                                        //si el usuario ya esta registrado este quedara establecido
                                        // en vm de lo contrario se cambiara checkUsuarioHecho relanzando efecto
                                        // y se comprobara registro Necesario para registrar usuario y guardarlo en vm
                                        cargaPerfilViewModel.obtenerUsuario(it)
                                    }
                                    //si en el segundo launch tras el check de usuario es necesario
                                    // el registro porque este no se ha encontrado se hara a continuacion
                                } else if (registroNecesario) {
                                    signedInUser?.let { it1 ->
                                        cargaPerfilViewModel.registrarUsuario(it1)
                                    }
                                    //llegados al else el check esta esta hecho el registro se habra hecho de ser necesario,
                                    // la unica instruccion posible sera el codigo a continuacion
                                } else {
                                    usuario?.let { it1 ->
                                        cargaPerfilViewModel.cargarRutinaActual(it1.userId)
                                    }
                                }
                            }

                            //le doy al composable los datos que necesita a partir de vm
                            usuario?.let {
                                PantallaPerfil(
                                    navController,//le paso el nav controller
                                    usuario = it,//le paso el usuario
                                    onSignOut = {//le paso el lambda que se lanzara en la scope dada
                                        lifecycleScope.launch {//lanzo en scope lifecycleScope/main
                                            googleAuthUiClient.signOut()//llamo a metood de sign out de google auth
                                            cargaPerfilViewModel.resetViewModel() //llamo a metodo vm
                                            Toast.makeText(
                                                context,
                                                "Sesion cerrada",
                                                Toast.LENGTH_LONG
                                            )
                                                .show()
                                            navController.navigate(RutasPantallas.PantallaSignIn.route)//y navegara hacia atras(a sign in)
                                        }
                                    },
                                    rutinaActual = rutinaActual,
                                    sesiones = sesiones,
                                    mostrarModalCarga = mostrarModalCarga,
                                    sesionPulsada = sesionPulsada,
                                    estadoToast = estadoToast,
                                    navegarToDetallesSesion = navegarToDetallesSesion,
                                    alPulsarSesion = { sesion ->
                                        cargaPerfilViewModel.comprobarSesion(
                                            sesion
                                        )
                                    },
                                    resetToastState = {
                                        cargaPerfilViewModel.resetearToast()
                                    },
                                )
                            }
                        }
                        composable(
                            route = "${RutasPantallas.PantallaDetallesSesion.route}/{sesionDia}/{rutinaNombre}",
                            arguments = listOf(
                                navArgument("sesionDia") {
                                    type = NavType.StringType
                                },
                                navArgument("rutinaNombre") {
                                    type = NavType.StringType
                                }
                            )
                        ) { backStackEntry ->
                            // Obtengo datos de navegacion de backStackEntry y los asigno a variables
                            val sesionDia = backStackEntry.arguments?.getString("sesionDia")
                            val rutinaNombre = backStackEntry.arguments?.getString("rutinaNombre")

                            //declaro vm que el composable va a usar dentro de la navegacion se instancia al navegar a esta vista
                            val detallesSesionViewModel by viewModels<DetallesSesionViewModel>()
                            //recojo valores de vm a traves de atributos de solo lectura
                            val sesion by detallesSesionViewModel.sesion.collectAsStateWithLifecycle()
                            val ejercicios by detallesSesionViewModel.ejercicios.collectAsStateWithLifecycle()
                            val mostrarModal by detallesSesionViewModel.mostrarModal.collectAsStateWithLifecycle()
                            val ejercicioSeleccionado by detallesSesionViewModel.ejercicioSeleccionado.collectAsStateWithLifecycle()

                            //cada vez que navego a esta vista reinicio vm y cargo los datos de nuevo
                            LaunchedEffect(Unit) {
                                detallesSesionViewModel.resetViewModel()
                                detallesSesionViewModel.cargarEjerciciosSesion(
                                    UsuarioActualSingleton.idUsuarioActual,
                                    rutinaNombre!!,
                                    sesionDia!!
                                )
                            }

                            //le doy al composable los datos que necesita a partir de vm
                            sesion?.let {
                                DetallesSesion(
                                    navController = navController,
                                    sesionSeleccionada = it,
                                    ejercicios = ejercicios,
                                    mostrarModal = mostrarModal,
                                    ejercicioSeleccionado = ejercicioSeleccionado,
                                    onSignOut = {//le paso el lambda que se lanzara en la scope dada
                                        lifecycleScope.launch {//lanzo en scope lifecycleScope/main
                                            googleAuthUiClient.signOut()//llamo a metood de sign out de google auth
                                            cargaPerfilViewModel.resetViewModel() //llamoo metodo vm
                                            Toast.makeText(
                                                context,
                                                "Sesion cerrada",
                                                Toast.LENGTH_LONG
                                            )
                                                .show()
                                            navController.navigate(RutasPantallas.PantallaSignIn.route)//y navegara hacia atras(a sign in)
                                        }
                                    },
                                    alSeleccionarEjercicio = { ejercicio ->
                                        detallesSesionViewModel.mostrarModal(ejercicio)
                                    },
                                    alPulsarCerrarModal = {
                                        detallesSesionViewModel.ocultarModal()
                                    }
                                )
                            }
                        }
                        composable(route = RutasPantallas.PantallaMisRutinas.route) {
                            //declaro vm que el composable va a usar dentro de la navegacion se instancia al navegar a esta vista
                            val misRutinasViewModel by viewModels<MisRutinasViewModel>()
                            //recojo valores de vm a traves de atributos de solo lectura
                            val rutinas by misRutinasViewModel.rutinas.collectAsStateWithLifecycle()
                            val rutinaSeleccionada by misRutinasViewModel.rutinaSeleccionada.collectAsStateWithLifecycle()
                            val mostrarModalCarga by misRutinasViewModel.mostrarModalCarga.collectAsStateWithLifecycle()
                            val mostrarModalConfirmacion by misRutinasViewModel.mostrarModalConfirmacion.collectAsStateWithLifecycle()
                            val estadoToast by misRutinasViewModel.estadoToast.collectAsStateWithLifecycle()
                            val navToEditarRutina by misRutinasViewModel.navToEditarRutina.collectAsStateWithLifecycle()

                            //cada vez que navego a esta vista lanzo efecto y si cambia valor de nav se lanza otra vez
                            // para reestear nav y no romper el pop back stack reinicio vm y cargo los datos de nuevo
                            LaunchedEffect(navToEditarRutina) {
                                misRutinasViewModel.resetViewModel()
                                misRutinasViewModel.cargarRutinas(UsuarioActualSingleton.idUsuarioActual)
                            }

                            MisRutinas(
                                navController,
                                onSignOut = {//le paso el lambda que se lanzara en la scope dada
                                    lifecycleScope.launch {//lanzo en scope lifecycleScope/main
                                        googleAuthUiClient.signOut()//llamo a metood de sign out de google auth
                                        cargaPerfilViewModel.resetViewModel() //llamoo metodo vm
                                        Toast.makeText(context, "Sesion cerrada", Toast.LENGTH_LONG)
                                            .show()
                                        navController.navigate(RutasPantallas.PantallaSignIn.route)//y navegara hacia atras(a sign in)
                                    }
                                },
                                rutinas = rutinas,
                                rutinaSeleccionada = rutinaSeleccionada,
                                mostrarModalCarga = mostrarModalCarga,
                                mostrarModalConfirmacion = mostrarModalConfirmacion,
                                estadoToast = estadoToast,
                                navToEditarRutina = navToEditarRutina,
                                alSeleccionarRutina = { rutina ->
                                    misRutinasViewModel.setRutinaSeleccionada(rutina)
                                },
                                alPulsarSwitch = { userId, nombre ->
                                    misRutinasViewModel.establecerRutinaComoActiva(userId, nombre)
                                },
                                resetToastState = {
                                    misRutinasViewModel.resetearToast()
                                },
                                alPulsarEliminar = {
                                    misRutinasViewModel.desplegarModalConfirmacion()
                                },
                                alDecidirModalConfirmacion = { decision ->
                                    misRutinasViewModel.accionModalEliminar(decision)
                                }

                            ) {
                                misRutinasViewModel.validarNavegacion()
                            }
                        }

                        composable(route = RutasPantallas.PantallaCrearRutina.route) {
                            //declaro vm que el composable va a usar dentro de la navegacion se instancia al navegar a esta vista
                            val crearRutinaViewModel by viewModels<CrearRutinaViewModel>()

                            val rutinaConSesConEjs by crearRutinaViewModel.rutinaConSesConEjs.collectAsStateWithLifecycle()
                            val listaSesionesConEjs by crearRutinaViewModel.listaSesionesConEjs.collectAsStateWithLifecycle()
                            val sesionConEjsParaModal by crearRutinaViewModel.sesionConEjsParaModal.collectAsStateWithLifecycle()
                            val cargaDatosInicialFinalizada by crearRutinaViewModel.cargaDatosInicialFinalizada.collectAsStateWithLifecycle()
                            val mostrarModalSesion by crearRutinaViewModel.mostrarModalSesion.collectAsStateWithLifecycle()
                            val mostrarModalGuardarRutina by crearRutinaViewModel.mostrarModalGuardarRutina.collectAsStateWithLifecycle()
                            val ejerciciosUsuario by crearRutinaViewModel.ejerciciosUsuario.collectAsStateWithLifecycle()
                            val estadosDeDesplegables by crearRutinaViewModel.estadosDeDesplegables.collectAsStateWithLifecycle()
                            val estadoToast by crearRutinaViewModel.estadoToast.collectAsStateWithLifecycle()
                            val navegarToMisRutinas by crearRutinaViewModel.navegarToMisRutinas.collectAsStateWithLifecycle()

                            //cada vez que navego a esta vista lanzo efecto y si cambia valor de nav se lanza otra vez
                            // para reestear nav y no romper el pop back stack reinicio vm y cargo los datos de nuevo
                            LaunchedEffect(navegarToMisRutinas) {
                                crearRutinaViewModel.resetViewModel()
                                crearRutinaViewModel.cargarDatos()
                            }

                            CrearRutina(
                                navController,
                                onSignOut = {//le paso el lambda que se lanzara en la scope dada
                                    lifecycleScope.launch {//lanzo en scope lifecycleScope/main
                                        googleAuthUiClient.signOut()//llamo a metood de sign out de google auth
                                        cargaPerfilViewModel.resetViewModel() //llamoo metodo vm
                                        Toast.makeText(context, "Sesion cerrada", Toast.LENGTH_LONG)
                                            .show()
                                        navController.navigate(RutasPantallas.PantallaSignIn.route)//y navegara hacia atras(a sign in)
                                    }
                                },
                                alModificarNombreRutina = { nuevoNombre ->
                                    crearRutinaViewModel.actualizarNombreRutina(nuevoNombre)
                                },
                                alModificarNombreSesion = { nuevoNombre ->
                                    crearRutinaViewModel.actualizarNombreSesionModal(nuevoNombre)
                                },
                                alPulsarCheckBoxSesion = { sesionConEjsPulsada ->
                                    crearRutinaViewModel.actualizarDescansoSesion(
                                        sesionConEjsPulsada
                                    )
                                },
                                alPulsarSesion = { sesionConEjsPulsada ->
                                    crearRutinaViewModel.desplegarModalSesion(sesionConEjsPulsada)
                                },
                                alPulsarCerrarModalSesion = {
                                    crearRutinaViewModel.cerrarModalSesion()
                                },
                                alPulsarGuardarSesion = { sesionConEjerciciosRecibida ->
                                    crearRutinaViewModel.guardarSesion(sesionConEjerciciosRecibida)
                                },
                                alPulsarGuardarRutina = {
                                    crearRutinaViewModel.desplegarModalGuardarRutina()
                                },
                                alGuardarRutina = { activarRutina ->
                                    crearRutinaViewModel.guardarRutina(activarRutina)
                                },
                                alModificarCampoEjercicio = { idEjercicio, campoEjercicio, nuevoValor ->
                                    crearRutinaViewModel.actualizarCampoEjercicio(
                                        idEjercicio,
                                        campoEjercicio,
                                        nuevoValor
                                    )
                                },
                                alSeleccionarEjercicioDesplegable = { ejercicioId, ejercicio ->
                                    crearRutinaViewModel.actualizarSesionModalConEjercicio(
                                        ejercicioId,
                                        ejercicio
                                    )
                                },
                                alPulsarDesplegable = { ejercicioId ->
                                    crearRutinaViewModel.alternarEstadoDesplegable(ejercicioId)
                                },
                                resetToastState = {
                                    crearRutinaViewModel.resetearToast()
                                },

                                rutinaConSesConEjs = rutinaConSesConEjs,
                                listaSesionesConEjs = listaSesionesConEjs,
                                sesionConEjsParaModal = sesionConEjsParaModal,
                                mostrarModalSesion = mostrarModalSesion,
                                mostrarModalGuardarRutina = mostrarModalGuardarRutina,
                                cargaDatosInicialFinalizada = cargaDatosInicialFinalizada,
                                ejerciciosParaDesplegable = ejerciciosUsuario,
                                estadosDeDesplegables = estadosDeDesplegables,
                                estadoToast = estadoToast,
                                navegarToMisRutinas = navegarToMisRutinas
                            )
                        }


                        composable(route = "${RutasPantallas.PantallaEditarRutina.route}/{rutinaNombre}",
                            arguments = listOf(
                                navArgument("rutinaNombre") {
                                    type = NavType.StringType
                                }
                            )) { backStackEntry ->
                            val rutinaNombre = backStackEntry.arguments?.getString("rutinaNombre")
                            val editarRutinaViewModel by viewModels<EditarRutinaViewModel>()

                            val rutinaConSesConEjs by editarRutinaViewModel.rutinaConSesConEjs.collectAsStateWithLifecycle()
                            val listaSesionesConEjs by editarRutinaViewModel.listaSesionesConEjs.collectAsStateWithLifecycle()
                            val sesionConEjsParaModal by editarRutinaViewModel.sesionConEjsParaModal.collectAsStateWithLifecycle()
                            val cargaDatosInicialFinalizada by editarRutinaViewModel.cargaDatosInicialFinalizada.collectAsStateWithLifecycle()
                            val mostrarModalSesion by editarRutinaViewModel.mostrarModalSesion.collectAsStateWithLifecycle()
                            val mostrarModalGuardarRutina by editarRutinaViewModel.mostrarModalGuardarRutina.collectAsStateWithLifecycle()
                            val ejerciciosUsuario by editarRutinaViewModel.ejerciciosUsuario.collectAsStateWithLifecycle()
                            val estadosDeDesplegables by editarRutinaViewModel.estadosDeDesplegables.collectAsStateWithLifecycle()
                            val estadoToast by editarRutinaViewModel.estadoToast.collectAsStateWithLifecycle()
                            val navegarToMisRutinas by editarRutinaViewModel.navegarToMisRutinas.collectAsStateWithLifecycle()

                            //cada vez que navego a esta vista reinicio vm y cargo los datos de nuevo
                            LaunchedEffect(navegarToMisRutinas) {
                                //Toast.makeText(context, rutinaNombre, Toast.LENGTH_SHORT).show()
                                editarRutinaViewModel.resetViewModel()
                                editarRutinaViewModel.cargarDatos(
                                    rutinaNombre!!,
                                )
                            }
                            EditarRutina(
                                navController,
                                onSignOut = {//le paso el lambda que se lanzara en la scope dada
                                    lifecycleScope.launch {//lanzo en scope lifecycleScope/main
                                        googleAuthUiClient.signOut()//llamo a metood de sign out de google auth
                                        cargaPerfilViewModel.resetViewModel() //llamoo metodo vm
                                        Toast.makeText(context, "Sesion cerrada", Toast.LENGTH_LONG)
                                            .show()
                                        navController.navigate(RutasPantallas.PantallaSignIn.route)//y navegara hacia atras(a sign in)
                                    }
                                },
                                alModificarNombreSesion = { nuevoNombre ->
                                    editarRutinaViewModel.actualizarNombreSesionModal(nuevoNombre)
                                },
                                alPulsarCheckBoxSesion = { sesionConEjsPulsada ->
                                    editarRutinaViewModel.actualizarDescansoSesion(
                                        sesionConEjsPulsada
                                    )
                                },
                                alPulsarSesion = { sesionConEjsPulsada ->
                                    editarRutinaViewModel.desplegarModalSesion(sesionConEjsPulsada)
                                },
                                alPulsarCerrarModalSesion = {
                                    editarRutinaViewModel.cerrarModalSesion()
                                },
                                alPulsarGuardarSesion = { sesionConEjerciciosRecibida ->
                                    editarRutinaViewModel.guardarSesion(sesionConEjerciciosRecibida)
                                },
                                alPulsarGuardarRutina = {
                                    editarRutinaViewModel.desplegarModalGuardarRutina()
                                },
                                alGuardarRutina = { activarRutina ->
                                    editarRutinaViewModel.guardarRutina(activarRutina)
                                },
                                alModificarCampoEjercicio = { idEjercicio, campoEjercicio, nuevoValor ->
                                    editarRutinaViewModel.actualizarCampoEjercicio(
                                        idEjercicio,
                                        campoEjercicio,
                                        nuevoValor
                                    )
                                },
                                alSeleccionarEjercicioDesplegable = { ejercicioId, ejercicio ->
                                    editarRutinaViewModel.actualizarSesionModalConEjercicio(
                                        ejercicioId,
                                        ejercicio
                                    )
                                },
                                alPulsarDesplegable = { ejercicioId ->
                                    editarRutinaViewModel.alternarEstadoDesplegable(ejercicioId)
                                },
                                resetToastState = {
                                    editarRutinaViewModel.resetearToast()
                                },

                                rutinaConSesConEjs = rutinaConSesConEjs,
                                listaSesionesConEjs = listaSesionesConEjs,
                                sesionConEjsParaModal = sesionConEjsParaModal,
                                mostrarModalSesion = mostrarModalSesion,
                                mostrarModalGuardarRutina = mostrarModalGuardarRutina,
                                cargaDatosInicialFinalizada = cargaDatosInicialFinalizada,
                                ejerciciosParaDesplegable = ejerciciosUsuario,
                                estadosDeDesplegables = estadosDeDesplegables,
                                estadoToast = estadoToast,
                                navegarToMisRutinas = navegarToMisRutinas
                            )

                        }


                        composable(route = RutasPantallas.PantallaInfo.route) {
                            val infoViewModel by viewModels<InfoViewModel>()
                            val consejos by infoViewModel.consejos.collectAsStateWithLifecycle()
                            val mostrarModalCarga by infoViewModel.mostrarModalCarga.collectAsStateWithLifecycle()
                            val estadosDeDesplegables by infoViewModel.estadosDeDesplegables.collectAsStateWithLifecycle()

                            LaunchedEffect(Unit) {
                                infoViewModel.resetViewModel()
                                infoViewModel.cargarDatos()
                            }

                            Info(
                                navController,
                                consejos = consejos,
                                estadosDeDesplegables = estadosDeDesplegables,
                                mostrarModalCarga = mostrarModalCarga,
                                onSignOut = {//le paso el lambda que se lanzara en la scope dada
                                    lifecycleScope.launch {//lanzo en scope lifecycleScope/main
                                        googleAuthUiClient.signOut()//llamo a metood de sign out de google auth
                                        cargaPerfilViewModel.resetViewModel() //llamoo metodo vm
                                        Toast.makeText(context, "Sesion cerrada", Toast.LENGTH_LONG)
                                            .show()
                                        navController.navigate(RutasPantallas.PantallaSignIn.route)//y navegara hacia atras(a sign in)
                                    }
                                },
                                alFiltrarPorTodo = {
                                    infoViewModel.mostrarConsejosTodos()
                                },
                                alFiltrarPorNutricionSalud = {
                                    infoViewModel.mostrarConsejosNutricionSalud()
                                },
                                alFiltrarPorEntrenamiento = {
                                    infoViewModel.mostrarConsejosEntrenamiento()
                                },
                                alPulsarDesplegable = { id ->
                                    infoViewModel.alternarEstadoDesplegable(id)
                                }
                            )
                        }

                        composable(route = RutasPantallas.PantallaMisEjercicios.route) {

                            val misEjerciciosViewModel by viewModels<MisEjerciciosViewModel>()
                            val ejercicioSeleccionado by misEjerciciosViewModel.ejercicioSeleccionado.collectAsStateWithLifecycle()
                            val ejercicioParaModal by misEjerciciosViewModel.ejercicioParaModal.collectAsStateWithLifecycle()
                            val escrituraNombreEjModal by misEjerciciosViewModel.escrituraNombreEjModal.collectAsStateWithLifecycle()
                            val listaEjerciciosPer by misEjerciciosViewModel.listaEjerciciosPer.collectAsStateWithLifecycle()
                            val estadoToast by misEjerciciosViewModel.estadoToast.collectAsStateWithLifecycle()
                            val mostrarModalCarga by misEjerciciosViewModel.mostrarModalCarga.collectAsStateWithLifecycle()
                            val mostrarModalEliminar by misEjerciciosViewModel.mostrarModalEliminar.collectAsStateWithLifecycle()
                            val mostrarModalCrear by misEjerciciosViewModel.mostrarModalCrear.collectAsStateWithLifecycle()
                            val mostrarModalEditar by misEjerciciosViewModel.mostrarModalEditar.collectAsStateWithLifecycle()

                            LaunchedEffect(Unit) {
                                misEjerciciosViewModel.resetViewModel()
                                misEjerciciosViewModel.cargarDatos()
                            }

                            MisEjercicios(
                                navController,
                                onSignOut = {//le paso el lambda que se lanzara en la scope dada
                                    lifecycleScope.launch {//lanzo en scope lifecycleScope/main
                                        googleAuthUiClient.signOut()//llamo a metood de sign out de google auth
                                        cargaPerfilViewModel.resetViewModel() //llamoo metodo vm
                                        Toast.makeText(context, "Sesion cerrada", Toast.LENGTH_LONG)
                                            .show()
                                        navController.navigate(RutasPantallas.PantallaSignIn.route)//y navegara hacia atras(a sign in)
                                    }
                                },
                                ejercicioSeleccionado = ejercicioSeleccionado,
                                ejercicioParaModal = ejercicioParaModal,
                                escrituraNombreEjModal = escrituraNombreEjModal,
                                listaEjerciciosPer = listaEjerciciosPer,
                                estadoToast = estadoToast,
                                mostrarModalCarga = mostrarModalCarga,
                                mostrarModalEliminar = mostrarModalEliminar,
                                mostrarModalCrear = mostrarModalCrear,
                                mostrarModalEditar = mostrarModalEditar,
                                alPulsarEjercicio = { ejercicio ->
                                    misEjerciciosViewModel.setEjercicioSeleccionado(ejercicio)
                                },
                                alPulsarCrear = { misEjerciciosViewModel.desplegarModalCrearEj() },
                                alPulsarEditar = { misEjerciciosViewModel.desplegarModalEditarEj() },
                                alPulsarEliminar = { misEjerciciosViewModel.desplegarModalEliminarEj() },
                                resetToastState = { misEjerciciosViewModel.resetearToast() },
                                alModificarCampoEjercicio = { campoEjercicio, nuevoValor ->
                                    misEjerciciosViewModel.actualizarCampoEjercicio(
                                        campoEjercicio,
                                        nuevoValor
                                    )
                                },
                                alDecidirEnModalCrear = { decision ->
                                    misEjerciciosViewModel.crearSegunDecisionModal(decision)
                                },

                                alDecidirEnModalEditar = { decision ->
                                    misEjerciciosViewModel.editarSegunDecisionModal(decision)
                                },
                                alDecidirEnModalEliminar = { decision ->
                                    misEjerciciosViewModel.eliminarSegunDecicionModal(decision)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
