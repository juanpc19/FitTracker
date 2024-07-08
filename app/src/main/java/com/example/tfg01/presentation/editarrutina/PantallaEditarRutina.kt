package com.example.tfg01.presentation.editarrutina

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tfg01.data.model.Ejercicio
import com.example.tfg01.data.model.Rutina
import com.example.tfg01.data.model.Sesion
import com.example.tfg01.domain.model.EstadoDesplegable
import com.example.tfg01.domain.model.EstadoToast
import com.example.tfg01.domain.model.RutinaConSesConEjs
import com.example.tfg01.domain.model.SesionConEjercicios
import com.example.tfg01.presentation.crearrutina.ModalSesion
import com.example.tfg01.presentation.utils.composablesreusables.BottomBar
import com.example.tfg01.presentation.utils.composablesreusables.ModalCargaDatos
import com.example.tfg01.presentation.utils.composablesreusables.ModalConfirmacion
import com.example.tfg01.presentation.utils.navegation.RutasPantallas
import com.example.tfg01.ui.theme.Tfg01Theme
import com.example.tfg01.ui.theme.gris20
import com.example.tfg01.ui.theme.grisMarron20
import com.example.tfg01.ui.theme.marronIntenso15

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarRutina(
    navController: NavController,
    onSignOut: () -> Unit,
    alModificarNombreSesion: (String) -> Unit,
    alPulsarCheckBoxSesion: (SesionConEjercicios) -> Unit,
    alPulsarSesion: (SesionConEjercicios) -> Unit,
    alPulsarCerrarModalSesion: () -> Unit,
    alPulsarGuardarSesion: (SesionConEjercicios) -> Unit,
    alPulsarGuardarRutina: () -> Unit,
    alGuardarRutina: (Boolean) -> Unit,
    alModificarCampoEjercicio: (Int, String, String) -> Unit,
    rutinaConSesConEjs: RutinaConSesConEjs?,
    sesionConEjsParaModal: SesionConEjercicios?,
    listaSesionesConEjs: List<SesionConEjercicios>,
    mostrarModalSesion: Boolean,
    mostrarModalGuardarRutina: Boolean,
    cargaDatosInicialFinalizada: Boolean,
    ejerciciosParaDesplegable: List<Ejercicio>,
    alSeleccionarEjercicioDesplegable: (Int, Ejercicio) -> Unit,
    estadosDeDesplegables: List<EstadoDesplegable>,
    alPulsarDesplegable: (Int) -> Unit,
    estadoToast: EstadoToast?,
    resetToastState: () -> Unit,
    navegarToMisRutinas: Boolean

) {

    val context = LocalContext.current

    LaunchedEffect(estadoToast) {
        if (estadoToast?.hacerToast == true) {
            Toast.makeText(context, estadoToast.mensajeToast, Toast.LENGTH_SHORT).show()
            resetToastState()
        }
    }

    LaunchedEffect(navegarToMisRutinas) {
        if (navegarToMisRutinas)
            navController.navigate(RutasPantallas.PantallaMisRutinas.route)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Spacer(modifier = Modifier.weight(0.02f))
                        Text(text = "Editar rutina", modifier = Modifier.weight(1f))
                    }
                },
                actions = {
                    IconButton(onClick = onSignOut) {
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
                            contentDescription = "cerrar sesion"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomBar(navController)
        },
        //defino contenido en base a cargaDatosInicialFinalizada
        content = { paddingValues ->
            if (!cargaDatosInicialFinalizada) {//modal de carga y recarga de datos, bool controlado por vm
                ModalCargaDatos(mensaje = "Cargando datos...")
            } else {
                Column(
                    Modifier
                        .padding(paddingValues)
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        modifier = Modifier
                            .weight(0.13f)
                            .offset(y = 10.dp)
                    ) {
                        TextField(
                            value = rutinaConSesConEjs?.rutina?.nombre ?: "",
                            onValueChange = { },
                            readOnly = true,
                            enabled = false,
                            label = { Text("Nombre Rutina: ") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(3.dp)
                                .clip(RoundedCornerShape(10.dp)),
                        )
                    }
                    Spacer(modifier = Modifier.weight(0.025f))
                    LazyColumn(
                        modifier = Modifier
                            .padding(10.dp)
                            .weight(0.9f)
                    ) {
                        items(listaSesionesConEjs) { sesionConEjercicios ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(55.dp)
                                    .clip(RoundedCornerShape(30.dp))
                                    .clickable {
                                        alPulsarSesion(sesionConEjercicios)
                                    }
                                    .padding(5.dp)//padding para childs
                            ) {
                                Text(
                                    text = sesionConEjercicios.sesion!!.dia,
                                    modifier = Modifier.weight(0.85f)
                                )
                                Spacer(modifier = Modifier.weight(0.05f))
                                Text(
                                    text = sesionConEjercicios.sesion.nombre,
                                    modifier = Modifier.weight(1.05f)
                                )
                                Spacer(modifier = Modifier.weight(0.05f))
                                Text(
                                    text = if (sesionConEjercicios.sesion.descanso) "Descanso" else "No descanso",
                                    modifier = Modifier.weight(0.95f)
                                )
                                Spacer(modifier = Modifier.weight(0.05f))
                                Checkbox(
                                    checked = sesionConEjercicios.sesion.descanso, //El check del switch toma bool de rutina
                                    onCheckedChange = {
                                        alPulsarCheckBoxSesion(
                                            sesionConEjercicios
                                        )
                                    },
                                    modifier = Modifier.weight(0.30f)
                                )
                            }
                            Divider(modifier = Modifier.padding(1.dp)) //antes de siguiente row
                        }
                    }
                    Row(modifier = Modifier.weight(0.1f)) {
                        Spacer(modifier = Modifier.weight(0.25f))
                        Button(
                            onClick = {
                                alPulsarGuardarRutina()
                            }, modifier = Modifier.weight(0.5f)
                        ) {
                            Text(text = "Guardar rutina")
                        }
                        Spacer(modifier = Modifier.weight(0.25f))
                    }
                    Spacer(modifier = Modifier.weight(0.001f))
                }
            }
        }
    )

    //hago if aparte para que se muestro sobre contenido scaffold y no elimine lo que ya hay en pantalla
    if (mostrarModalGuardarRutina) {
        ModalConfirmacion(
            textoTitulo = "Modificar Rutina",
            textoCuerpo = "¿Deseas guardar los cambios realizados?",
            textoConfirmar = "Si",
            textoCancelar = "No",
            icono = Icons.Default.Build,
            alDecidirEnModal = alGuardarRutina
        )
    }

    //hago if aparte para que se muestro sobre contenido scaffold y no elimine lo que ya hay en pantalla
    if (mostrarModalSesion) {
        ModalSesion(
            sesionConEjsParaModal = sesionConEjsParaModal,
            alPulsarGuardarSesion = alPulsarGuardarSesion,
            alPulsarCerrarModalSesion = alPulsarCerrarModalSesion,
            alModificarCampoEjercicio = alModificarCampoEjercicio,
            ejerciciosParaDesplegable = ejerciciosParaDesplegable,
            alSeleccionarEjercicioDesplegable = alSeleccionarEjercicioDesplegable,
            estadosDeDesplegables = estadosDeDesplegables,
            alPulsarDesplegable = alPulsarDesplegable,
            alModificarNombreSesion = alModificarNombreSesion
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalSesion(
    sesionConEjsParaModal: SesionConEjercicios?,
    alPulsarGuardarSesion: (SesionConEjercicios) -> Unit,
    alPulsarCerrarModalSesion: () -> Unit,
    alModificarCampoEjercicio: (Int, String, String) -> Unit,
    ejerciciosParaDesplegable: List<Ejercicio>,
    alSeleccionarEjercicioDesplegable: (Int, Ejercicio) -> Unit,
    estadosDeDesplegables: List<EstadoDesplegable>,
    alPulsarDesplegable: (Int) -> Unit,
    alModificarNombreSesion: (String) -> Unit
) {

    val modalFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    AlertDialog(
        onDismissRequest = { alPulsarCerrarModalSesion() },
        properties = DialogProperties(dismissOnClickOutside = false),
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextField(
                        value = sesionConEjsParaModal?.sesion?.nombre ?: "",
                        onValueChange = { nuevoValor -> alModificarNombreSesion(nuevoValor) },
                        label = { Text("Nombre Sesion :") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(3.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .focusRequester(modalFocusRequester),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            autoCorrect = true,
                        ),
                        keyboardActions = KeyboardActions(onNext = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        })
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    //Al usar key = { it.id }, cada ítem en LazyColumn tiene una clave única que
                    //facilita a Compose rastrear y mantener el estado del ítem incluso cuando la lista cambia.
                    items(sesionConEjsParaModal!!.listaEjercicios, key = { it.id }) { ejercicio ->
                        val estado =
                            estadosDeDesplegables.find { it.id == ejercicio.id }?.estado ?: false
                        Spacer(modifier = Modifier.padding(12.dp))
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(marronIntenso15)
                                .padding(5.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ExposedDropdownMenuBox( //esto controla el despliegue en si
                                expanded = estado,
                                onExpandedChange = { alPulsarDesplegable(ejercicio.id) },
                                modifier = Modifier.padding(10.dp)

                            ) {
                                TextField( //texto "default del desplegable con icono
                                    value = ejercicio.nombre,
                                    onValueChange = { },
                                    readOnly = true,
//                                    label = { Text("soy label: ") },
//                                    placeholder = { Text("soy placeholder: ") },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(grisMarron20),
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = estado,
                                        )
                                    }
                                )
                                ExposedDropdownMenu(
                                    expanded = estado,
                                    //esto solo afecta a tocar otra cosa o back arrow pa quitarlo
                                    onDismissRequest = { alPulsarDesplegable(ejercicio.id) },
                                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                                ) {
                                    ejerciciosParaDesplegable.forEach { ejercicioDesplegable ->
                                        DropdownMenuItem(
                                            text = { Text(ejercicioDesplegable.nombre) },
                                            onClick = {
                                                alSeleccionarEjercicioDesplegable(
                                                    ejercicio.id,
                                                    ejercicioDesplegable
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                            Row(modifier = Modifier.padding(5.dp)) {
                                Box(
                                    Modifier
                                        .weight(0.33f)
                                        .padding(3.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                ) {
                                    TextField(
                                        value = ejercicio.sets.toString(),
                                        onValueChange = { nuevoValor ->
                                            if (nuevoValor.all { it.isDigit() }) {
                                                alModificarCampoEjercicio(
                                                    ejercicio.id,
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
                                Box(
                                    Modifier
                                        .weight(0.33f)
                                        .padding(3.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                ) {
                                    TextField(
                                        value = ejercicio.repeticiones.toString(),
                                        onValueChange = { nuevoValor ->
                                            if (nuevoValor.all { it.isDigit() }) {
                                                alModificarCampoEjercicio(
                                                    ejercicio.id,
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
                                Box(
                                    Modifier
                                        .weight(0.33f)
                                        .padding(3.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                ) {
                                    TextField(
                                        value = ejercicio.peso.toString(),
                                        onValueChange = { nuevoValor ->
                                            if (nuevoValor.all { it.isDigit() }) {
                                                alModificarCampoEjercicio(
                                                    ejercicio.id,
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
                            Row(modifier = Modifier.padding(10.dp)) {
                                Box(
                                    modifier = Modifier
                                        .padding(3.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                ) {
                                    TextField(
                                        value = ejercicio.anotaciones,
                                        onValueChange = { nuevoValor ->
                                            alModificarCampoEjercicio(
                                                ejercicio.id,
                                                "anotaciones",
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
                                        label = { Text("Anotaciones: ") },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    sesionConEjsParaModal?.let { alPulsarGuardarSesion(it) }
                },
            ) {
                Text("Guardar sesión")
            }
        },
        dismissButton = {
            Button(
                onClick = alPulsarCerrarModalSesion,
                colors = ButtonDefaults.buttonColors(containerColor = gris20)
            ) {
                Text("Cancelar")
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun EditarRutinaPreview() {
    Tfg01Theme {
        val navController = rememberNavController()

        val mostrarModalSesion = false
        val sesionSeleccionada =
            Sesion(dia = "Lunes", descanso = false, nombre = "pecho", diaNum = 1)
        val listaSesiones = listOf(
            Sesion(dia = "Lunes", descanso = false, nombre = "brazos y hombros", diaNum = 1),
            Sesion(dia = "Martes", descanso = false, nombre = " matame camion", diaNum = 2),
            Sesion(dia = "Miércoles", descanso = false, nombre = " matame camion", diaNum = 3),
            Sesion(dia = "Jueves", descanso = false, nombre = " matame camion", diaNum = 4),
            Sesion(dia = "Viernes", descanso = false, nombre = " matame camion", diaNum = 5),
            Sesion(dia = "Sábado", descanso = false, nombre = " matame camion", diaNum = 6),
            Sesion(dia = "Domingo", descanso = false, nombre = " matame camion", diaNum = 7)
        )
        val ejerciciosFalsos = listOf(
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
            ),
        )
        // Datos falsos
        val sesionConEjsFalsa = SesionConEjercicios(
            sesion = Sesion(
                dia = "Miercoles",
                descanso = false,
                nombre = "brazos y hombros",
                diaNum = 1
            ),
            listaEjercicios = ejerciciosFalsos,
        )

        val rutinaFalsa = RutinaConSesConEjs(
            Rutina(userId = "1", nombre = "Rutina A", rutinaActiva = false),
            listaSesConListaEjs = listOf(sesionConEjsFalsa)
        )

        val estadosDeDesplegables: List<EstadoDesplegable> = emptyList()
        EditarRutina(
            navController = navController,
            onSignOut = { },
            alModificarNombreSesion = { nuevoNombre -> },
            alPulsarCheckBoxSesion = { sesionConEjercicios -> },
            alPulsarSesion = { sesionConEjercicios -> },
            alPulsarCerrarModalSesion = { },
            alPulsarGuardarSesion = { sesionConEjercicios -> },
            alPulsarGuardarRutina = { },
            alGuardarRutina = { },
            alModificarCampoEjercicio = { a, b, c -> },
            rutinaConSesConEjs = rutinaFalsa,
            sesionConEjsParaModal = sesionConEjsFalsa,
            listaSesionesConEjs = listOf(
                sesionConEjsFalsa,
                sesionConEjsFalsa,
                sesionConEjsFalsa,
                sesionConEjsFalsa,
                sesionConEjsFalsa,
                sesionConEjsFalsa,
                sesionConEjsFalsa
            ),
            mostrarModalSesion = false,
            mostrarModalGuardarRutina = false,
            cargaDatosInicialFinalizada = true,
            ejerciciosParaDesplegable = ejerciciosFalsos,
            alSeleccionarEjercicioDesplegable = { ejercicioId, ejercicio -> },
            estadosDeDesplegables = estadosDeDesplegables,
            alPulsarDesplegable = { ejercicioId -> },
            estadoToast = EstadoToast(false, ""),
            resetToastState = { },
            navegarToMisRutinas = false
        )


        val estadosDeDesplegables2: List<EstadoDesplegable> = emptyList()

        ModalSesion(
            sesionConEjsParaModal = sesionConEjsFalsa,
            alPulsarGuardarSesion = { sesionConEjercicios -> },
            alPulsarCerrarModalSesion = { },
            alModificarCampoEjercicio = { a, b, c -> },
            ejerciciosParaDesplegable = ejerciciosFalsos,
            alSeleccionarEjercicioDesplegable = { ejercicioId, ejercicio -> },
            estadosDeDesplegables = estadosDeDesplegables2,
            alPulsarDesplegable = { ejercicioId -> },
            alModificarNombreSesion = { nuevoNombre -> },
        )

        val textoTitulo = "Titulo"
        //val textoCuerpo = "¿Deseas establecer la rutina como activa?"
        val textoCuerpo = "¿Estas seguro de querer borrar la rutina seleccionada?"
        val textoConfirmar = "Confirmar"
        val textoCancelar = "Cancelar"
        val icono: ImageVector = Icons.Default.Warning

//        ModalConfirmacion(
//            textoTitulo = "Modificar Rutina",
//            textoCuerpo = "¿Deseas guardar los cambios realizados?",
//            textoConfirmar = "Si",
//            textoCancelar = "No",
//            icono = Icons.Default.Build,
//            alDecidirEnModal = { }
//        )

    }
}