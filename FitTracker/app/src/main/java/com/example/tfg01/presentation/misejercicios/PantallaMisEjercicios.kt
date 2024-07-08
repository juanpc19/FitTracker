package com.example.tfg01.presentation.misejercicios

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tfg01.data.model.Ejercicio
import com.example.tfg01.domain.model.EstadoToast
import com.example.tfg01.presentation.utils.composablesreusables.BottomBar
import com.example.tfg01.presentation.utils.composablesreusables.ModalCargaDatos
import com.example.tfg01.presentation.utils.composablesreusables.ModalConfirmacion
import com.example.tfg01.ui.theme.Tfg01Theme
import com.example.tfg01.ui.theme.gris20
import com.example.tfg01.ui.theme.grisMarron20
import com.example.tfg01.ui.theme.marronIntenso40

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisEjercicios(
    navController: NavController,
    onSignOut: () -> Unit,
    ejercicioSeleccionado: Ejercicio?,
    ejercicioParaModal: Ejercicio?,
    escrituraNombreEjModal: Boolean,
    listaEjerciciosPer: List<Ejercicio>,
    estadoToast: EstadoToast?,
    mostrarModalCarga: Boolean,
    mostrarModalEliminar: Boolean,
    mostrarModalCrear: Boolean,
    mostrarModalEditar: Boolean,
    resetToastState: () -> Unit,
    alPulsarEjercicio: (Ejercicio) -> Unit,
    alPulsarCrear: () -> Unit,
    alPulsarEditar: () -> Unit,
    alPulsarEliminar: () -> Unit,
    alModificarCampoEjercicio: (String, String) -> Unit,
    alDecidirEnModalCrear: (Boolean) -> Unit,
    alDecidirEnModalEditar: (Boolean) -> Unit,
    alDecidirEnModalEliminar: (Boolean) -> Unit,

    ) {

    val context = LocalContext.current

    LaunchedEffect(estadoToast) {
        if (estadoToast?.hacerToast == true) {
            Toast.makeText(context, estadoToast.mensajeToast, Toast.LENGTH_SHORT).show()
            resetToastState()
        }
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
                        Text(text = "Mis ejercicios", modifier = Modifier.weight(1f))
                    }
                },
                navigationIcon = {
                },
                actions = {
                    IconButton(onClick = { alPulsarEliminar() }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "boton borrar ejercicio",
                        )
                    }
                    // si pongo edit poner esto en la row de titulo modifier = Modifier.fillMaxWidth(0.95f),
                    IconButton(onClick = { alPulsarEditar() }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "boton editar ejercicio",
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
            FloatingActionButton(onClick = {
                alPulsarCrear()
            }) {
                Icon(Icons.Default.Add, contentDescription = "boton crear ejercicio")
            }
        },
        //controlo el contenido en base a switch
        content = { paddingValues ->
            when {
                mostrarModalCarga -> { //modal de carga y recarga de datos, bool controlado por vm
                    ModalCargaDatos(mensaje = "Cargando datos...")
                }

                listaEjerciciosPer.isEmpty() -> {//si usuario no tiene ejs personalizados creados
                    Column(
                        modifier = Modifier
                            .padding(
                                paddingValues
                            )
                            .padding(10.dp)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Crea tu primer ejercicio personalizado",
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> { //escenario normal donde muestro el contenido
                    LazyColumn(
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(20.dp),
                    ) {
                        items(listaEjerciciosPer) { ejercicio ->
                            val colorFondo =
                                if (ejercicio.nombre == ejercicioSeleccionado?.nombre) {
                                    marronIntenso40
                                } else {
                                    grisMarron20
                                }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .clip(RoundedCornerShape(40.dp))
                                    .clickable {
                                        alPulsarEjercicio(ejercicio)
                                    }
                                    .background(colorFondo)
                                    .padding(15.dp)//padding para childs
                            ) {
                                Text(
                                    text = ejercicio.nombre, maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            Divider(modifier = Modifier.padding(8.dp)) //antes de siguiente row
                        }
                    }
                }
            }
        }
    )

    //muestro modal en base a boolean, se reutilizara el modal de confirmacion y en su metodo sicON
    if (mostrarModalEliminar) {
        ModalConfirmacion(
            textoTitulo = "Eliminar Ejercicio",
            textoCuerpo = "¿Esta seguro de querer eliminar este ejercicio? ${ejercicioSeleccionado?.nombre}",
            textoConfirmar = "Confirmar",
            textoCancelar = "Cancelar",
            icono = Icons.Default.Warning,
            alDecidirEnModal = alDecidirEnModalEliminar, //metodo vm en eliminar ej
        )
    }

    //muestro modal en base a boolean, ejercicioParaModal sera el mismo para ambos en vm,
    // manipulado por este segun proceda, alDecidirEnModal sera el metodo de crear en vm
    if (mostrarModalCrear) {
        ModalEjercicio(
            ejercicioParaModal = ejercicioParaModal,
            escrituraNombreEjModal = escrituraNombreEjModal,
            alModificarCampoEjercicio = alModificarCampoEjercicio,
            alDecidirEnModal = alDecidirEnModalCrear
        )
    }

    //muestro modal en base a boolean, ejercicioParaModal sera el mismo para ambos en vm,
    // manipulado por este segun proceda, alDecidirEnModal sera el metodo de editar en vm
    if (mostrarModalEditar) {
        ModalEjercicio(
            ejercicioParaModal = ejercicioParaModal,
            escrituraNombreEjModal = escrituraNombreEjModal,
            alModificarCampoEjercicio = alModificarCampoEjercicio,
            alDecidirEnModal = alDecidirEnModalEditar
        )
    }

}

//recibira ejercicioParaModal compartido por crear y editar modal, este se manipulara en vm para mostrarlo correctamente,
//recibira escrituraNombreEjModal para determinar si textfield nombre puede ser usado o no segun se abra el modal
// crear o editar, sera usado en el read only del textfield de nombre y en la primera row para determinar nombre del modal
//recibira alDecidirEnModal version crear o version editar, ambas seran usadas en botones confirmar y cancelar,
// los cuales pasarn el bool true o false egun proceda,
// estos 3 parametros permitiran reusar el composable para ModalEjercicio crear y editar ejs
@Composable
fun ModalEjercicio(
    ejercicioParaModal: Ejercicio?,
    escrituraNombreEjModal: Boolean,
    alModificarCampoEjercicio: (String, String) -> Unit,
    alDecidirEnModal: (Boolean) -> Unit
) {

    val modalFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    AlertDialog(
        onDismissRequest = { },
        properties = DialogProperties(dismissOnClickOutside = false),
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.05f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (escrituraNombreEjModal) "Crear ejercicio" else "Editar ejercicio",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .fillMaxHeight(0.95f),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(0.33f)
                                .padding(3.dp)
                                .clip(RoundedCornerShape(10.dp))
                        ) {
                            TextField(
                                readOnly = !escrituraNombreEjModal,
                                value = ejercicioParaModal?.nombre.toString(),
                                onValueChange = { nuevoValor ->
                                    alModificarCampoEjercicio(
                                        "nombre",
                                        nuevoValor
                                    )
                                },
                                modifier = Modifier.focusRequester(modalFocusRequester),
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next,
                                    autoCorrect = true,
                                ),
                                keyboardActions = KeyboardActions(onDone = {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                }),
                                label = { Text("Nombre: ") },
                            )
                        }
                    }


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            Modifier
                                .weight(1f)
                                .padding(3.dp)
                                .clip(RoundedCornerShape(10.dp))
                        ) {
                            TextField(
                                value = ejercicioParaModal?.sets.toString(),
                                onValueChange = { nuevoValor ->
                                    if (nuevoValor.all { it.isDigit() }) {
                                        alModificarCampoEjercicio(
                                            "sets",
                                            nuevoValor
                                        )
                                    }
                                },
                                modifier = Modifier.focusRequester(modalFocusRequester),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next,
                                ),
                                keyboardActions = KeyboardActions(onDone = {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                }),
                                label = { Text("Sets: ") },
                            )
                        }
                        Spacer(modifier = Modifier.weight(0.35f))
                        Box(
                            Modifier
                                .weight(1f)
                                .padding(3.dp)
                                .clip(RoundedCornerShape(10.dp)),
                        ) {
                            TextField(
                                value = ejercicioParaModal?.repeticiones.toString(),
                                onValueChange = { nuevoValor ->
                                    if (nuevoValor.all { it.isDigit() }) {
                                        alModificarCampoEjercicio(
                                            "repeticiones",
                                            nuevoValor
                                        )
                                    }
                                },
                                modifier = Modifier.focusRequester(modalFocusRequester),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next,
                                ),
                                keyboardActions = KeyboardActions(onDone = {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                }),
                                label = { Text("Reps: ") },
                            )
                        }
                        Spacer(modifier = Modifier.weight(0.35f))
                        Box(
                            Modifier
                                .weight(1f)
                                .padding(3.dp)
                                .clip(RoundedCornerShape(10.dp))
                        ) {
                            TextField(
                                value = ejercicioParaModal?.peso.toString(),
                                onValueChange = { nuevoValor ->
                                    if (nuevoValor.all { it.isDigit() }) {
                                        alModificarCampoEjercicio(
                                            "peso",
                                            nuevoValor
                                        )
                                    }
                                },
                                modifier = Modifier.focusRequester(modalFocusRequester),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next,
                                ),
                                keyboardActions = KeyboardActions(onDone = {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                }),
                                label = { Text("Peso: ") },
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            Modifier
                                .weight(0.33f)
                                .padding(3.dp)
                                .clip(RoundedCornerShape(10.dp))
                        ) {
                            TextField(
                                value = ejercicioParaModal?.anotaciones.toString(),
                                onValueChange = { nuevoValor ->
                                    alModificarCampoEjercicio(
                                        "anotaciones",
                                        nuevoValor
                                    )
                                },
                                modifier = Modifier.focusRequester(modalFocusRequester),
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next,
                                    autoCorrect = true,
                                ),
                                keyboardActions = KeyboardActions(onDone = {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                }),
                                label = { Text("Anotaciones: ") },
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            Modifier
                                .weight(0.33f)
                                .padding(3.dp)
                                .clip(RoundedCornerShape(10.dp))
                        ) {
                            TextField(
                                value = ejercicioParaModal?.imagen.toString(),
                                onValueChange = { nuevoValor ->
                                    alModificarCampoEjercicio(
                                        "imagen",
                                        nuevoValor
                                    )
                                },
                                modifier = Modifier.focusRequester(modalFocusRequester),
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next,
                                    autoCorrect = true,
                                ),
                                keyboardActions = KeyboardActions(onDone = {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                }),
                                label = { Text("URL imagen: ") },
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            Modifier
                                .weight(0.33f)
                                .padding(3.dp)
                                .clip(RoundedCornerShape(10.dp))
                        ) {
                            TextField(
                                value = ejercicioParaModal?.urlVideoEjemplo.toString(),
                                onValueChange = { nuevoValor ->
                                    alModificarCampoEjercicio(
                                        "urlVideoEjemplo",
                                        nuevoValor
                                    )
                                },
                                modifier = Modifier.focusRequester(modalFocusRequester),
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done,
                                    autoCorrect = true,
                                ),
                                keyboardActions = KeyboardActions(onNext = {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                }),
                                label = { Text("URL video: ") },
                            )
                        }
                    }
                }
            }

        },
        confirmButton = {
            Button(onClick = { alDecidirEnModal(true) }) {
                Text(if (escrituraNombreEjModal) "Crear ejercicio" else "Guardar Cambios")
            }
        },
        dismissButton = {
            Button(
                onClick = { alDecidirEnModal(false) },
                colors = ButtonDefaults.buttonColors(containerColor = gris20)
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MisEjerciciosRutinaPreview() {
    Tfg01Theme {
        val navController = rememberNavController()
        val elBool = false
        val estadoToast = EstadoToast(false, "")
        val ejercicioSeleccionado = Ejercicio(
            0,
            nombre = "Push-up",
            repeticiones = 10,
            anotaciones = "dadadada",
            imagen = "https://www.youtube.com/watch?v=8XujhEbaHNQ&list=PLQkwcJG4YTCTJLKhPGqgLFTKDsHfF9Arv&",
            urlVideoEjemplo = "https://www.youtube.com/watch?v=8XujhEbaHNQ&list=PLQkwcJG4YTCTJLKhPGqgLFTKDs",
            peso = 1,
            sets = 2
        )
        val listaEjerciciosPer = listOf(
            Ejercicio(
                0,
                nombre = "Push-up",
                repeticiones = 10,
                anotaciones = "dadadada",
                imagen = "",
                urlVideoEjemplo = "d",
                peso = 1,
                sets = 2
            ),
            Ejercicio(
                1,
                nombre = "Push-up",
                repeticiones = 10,
                anotaciones = "dadadada",
                imagen = "",
                urlVideoEjemplo = "d",
                peso = 1,
                sets = 2
            ),
            Ejercicio(
                2,
                nombre = "Push-up",
                repeticiones = 10,
                anotaciones = "dadadada",
                imagen = "",
                urlVideoEjemplo = "d",
                peso = 1,
                sets = 2
            ),
            Ejercicio(
                3,
                nombre = "Push-up",
                repeticiones = 10,
                anotaciones = "dadadada",
                imagen = "",
                urlVideoEjemplo = "d",
                peso = 1,
                sets = 2
            ),
            Ejercicio(
                4,
                nombre = "Push-up",
                repeticiones = 10,
                anotaciones = "dadadada",
                imagen = "",
                urlVideoEjemplo = "d",
                peso = 1,
                sets = 2
            ),
            Ejercicio(
                5,
                nombre = "Push-up",
                repeticiones = 10,
                anotaciones = "dadadada",
                imagen = "",
                urlVideoEjemplo = "d",
                peso = 1,
                sets = 2
            )
        )

        MisEjercicios(
            navController,
            onSignOut = {},
            ejercicioSeleccionado = ejercicioSeleccionado,
            ejercicioParaModal = ejercicioSeleccionado,
            escrituraNombreEjModal = false,
            listaEjerciciosPer = listaEjerciciosPer,
            estadoToast = estadoToast,
            mostrarModalCarga = elBool,
            mostrarModalEliminar = elBool,
            mostrarModalCrear = false,
            mostrarModalEditar = false,
            resetToastState = { },
            alPulsarEjercicio = { ejercicioSeleccionado },
            alPulsarCrear = {},
            alPulsarEditar = { },
            alPulsarEliminar = { },
            alModificarCampoEjercicio = { a, b -> },
            alDecidirEnModalCrear = {},
            alDecidirEnModalEditar = {},
            alDecidirEnModalEliminar = {}
        )

        ModalEjercicio(
            ejercicioParaModal = ejercicioSeleccionado,
            alModificarCampoEjercicio = { a, b -> },
            escrituraNombreEjModal = false,
            alDecidirEnModal = {},
        )

//        val textoTitulo = "Titulo"
//        //val textoCuerpo = "¿Deseas establecer la rutina como activa?"
//        val textoCuerpo = "¿Estas seguro de querer borrar la rutina seleccionada?"
//        val textoConfirmar = "Confirmar"
//        val textoCancelar = "Cancelar"
//        val icono: ImageVector = Icons.Default.Warning
//
//        ModalConfirmacion(
//            textoTitulo, textoCuerpo, textoConfirmar, textoCancelar, icono,
//            alDecidirEnModal = { }
//        )

    }
}