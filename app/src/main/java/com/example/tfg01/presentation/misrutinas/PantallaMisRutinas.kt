package com.example.tfg01.presentation.misrutinas

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tfg01.data.model.Rutina
import com.example.tfg01.domain.model.EstadoToast
import com.example.tfg01.presentation.utils.navegation.RutasPantallas
import com.example.tfg01.presentation.utils.composablesreusables.BottomBar
import com.example.tfg01.domain.model.UsuarioActualSingleton
import com.example.tfg01.presentation.utils.composablesreusables.ModalCargaDatos
import com.example.tfg01.presentation.utils.composablesreusables.ModalConfirmacion
import com.example.tfg01.ui.theme.grisMarron20
import com.example.tfg01.ui.theme.marronIntenso40

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisRutinas(
    navController: NavController,
    onSignOut: () -> Unit,
    rutinas: List<Rutina>,
    rutinaSeleccionada: Rutina?,
    mostrarModalCarga: Boolean,
    alSeleccionarRutina: (Rutina) -> Unit,
    alPulsarSwitch: (String, String) -> Unit,
    mostrarModalConfirmacion: Boolean,
    estadoToast: EstadoToast?,
    resetToastState: () -> Unit,
    alPulsarEliminar: () -> Unit,
    alDecidirModalConfirmacion: (Boolean) -> Unit,
    navToEditarRutina: Boolean,
    alPulsarEditarRutina: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(estadoToast) {
        if (estadoToast?.hacerToast == true) {
            Toast.makeText(context, estadoToast.mensajeToast, Toast.LENGTH_SHORT).show()
            resetToastState()
        }
    }

    LaunchedEffect(navToEditarRutina) {
        if (navToEditarRutina) {
            navController.navigate(
                "${RutasPantallas.PantallaEditarRutina.route}/${rutinaSeleccionada?.nombre}"
            )
        }
    }

    if (mostrarModalConfirmacion) {
        ModalConfirmacion(
            textoTitulo = "Eliminar rutina",
            textoCuerpo = "¿Esta seguro que desea eliminar esta rutina? ${rutinaSeleccionada?.nombre}",
            textoConfirmar = "Confirmar",
            textoCancelar = "Cancelar",
            icono = Icons.Default.Warning,
            alDecidirEnModal = alDecidirModalConfirmacion
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(0.02f))
                        Text(text = "Mis rutinas", modifier = Modifier.weight(1f))
                    }
                },
                navigationIcon = {
                },
                actions = {
                    IconButton(onClick = { alPulsarEliminar() }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "boton borrar rutina",
                        )
                    }

                    IconButton(onClick = { alPulsarEditarRutina() }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "boton editar rutina",
                        )
                    }


                    IconButton(onClick = onSignOut) {
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
                            contentDescription = "cerrar sesion",
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomBar(navController)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(RutasPantallas.PantallaCrearRutina.route) }) {
                Icon(Icons.Default.Add, contentDescription = "Add rutina")
            }
        },
        //defino contenido en base a switch
        content = { paddingValues ->
            when {
                mostrarModalCarga -> { //modal de carga y recarga de datos, bool controlado por vm
                    ModalCargaDatos(mensaje = "Cargando datos...")
                }

                rutinas.isEmpty() -> {//si la rutina esta vacia muestro mensaje para crear una
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(10.dp)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Crea tu primera rutina de entrenamiento",
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> { //escenario normal donde muestro el contenido
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .padding(20.dp)
                                .weight(0.75f),
                        ) {
                            items(rutinas) { rutina ->
                                val colorFondo =
                                    if (rutina.nombre == rutinaSeleccionada?.nombre) {
                                        marronIntenso40
                                    } else {
                                        grisMarron20
                                    }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                        .clip(RoundedCornerShape(40.dp))
                                        .clickable {
                                            alSeleccionarRutina(rutina)
                                        }
                                        .background(colorFondo)
                                        .padding(15.dp)//padding para childs

                                ) {
                                    Text(text = rutina.nombre, modifier = Modifier.weight(2f))
                                    Spacer(modifier = Modifier.weight(0.1f))
                                    Switch(
                                        checked = rutina.rutinaActiva, //El check del switch toma bool de rutina
                                        onCheckedChange = {
                                            alPulsarSwitch(
                                                UsuarioActualSingleton.idUsuarioActual,
                                                rutina.nombre
                                            )
                                        },
                                        modifier = Modifier.weight(0.5f)
                                    )
                                }
                                Divider(
                                    modifier = Modifier.padding(10.dp),
                                    thickness = 1.dp
                                ) //antes de siguiente row
                            }
                        }
                        Spacer(modifier = Modifier.weight(0.1f))
                    }
                }
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun MisRutinasPreview() {
    val navController = rememberNavController()

    val rutinas = listOf(
        Rutina(userId = "1", nombre = "Rutina nombre largo", rutinaActiva = false),
        Rutina(userId = "1", nombre = "Rutina B", rutinaActiva = true),
        Rutina(userId = "1", nombre = "Rutina nombre largo", rutinaActiva = false),
        Rutina(userId = "1", nombre = "Rutina nombre largo", rutinaActiva = false),
        Rutina(userId = "1", nombre = "Rutina nombre largo", rutinaActiva = false),
        Rutina(userId = "1", nombre = "Rutina nombre largo", rutinaActiva = false),
        Rutina(userId = "1", nombre = "Rutina nombre largo", rutinaActiva = false),
        Rutina(userId = "1", nombre = "Rutina nombre largo", rutinaActiva = false)
    )

    val rutinaSeleccionada = rutinas[0]
    val mostrarModal = false

    MisRutinas(
        navController = navController,
        onSignOut = {},
        rutinas = rutinas,
        rutinaSeleccionada = rutinaSeleccionada,
        mostrarModalCarga = mostrarModal,
        alSeleccionarRutina = { },
        alPulsarSwitch = { _, _ -> },
        mostrarModalConfirmacion = false,
        estadoToast = null,
        resetToastState = {},
        alPulsarEliminar = {},
        alDecidirModalConfirmacion = { },
        navToEditarRutina = false,
        alPulsarEditarRutina = {}
    )

//    val textoTitulo = "Titulo"
//    //val textoCuerpo = "¿Deseas establecer la rutina como activa?"
//    val textoCuerpo = "¿Estas seguro de querer borrar la rutina seleccionada?"
//    val textoConfirmar = "Confirmar"
//    val textoCancelar = "Cancelar"
//    val icono: ImageVector = Icons.Default.Warning
//
//    ModalConfirmacion(
//        textoTitulo, textoCuerpo, textoConfirmar, textoCancelar, icono,
//        alDecidirEnModal = { }
//    )

}