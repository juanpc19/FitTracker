package com.example.tfg01.presentation.detallessesion

import android.net.Uri
import android.util.Log
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.tfg01.data.model.Ejercicio
import com.example.tfg01.data.model.Sesion
import com.example.tfg01.presentation.utils.composablesreusables.BottomBar
import com.example.tfg01.ui.theme.Tfg01Theme
import com.example.tfg01.ui.theme.blanco
import com.example.tfg01.ui.theme.grisMarron20
import com.example.tfg01.ui.theme.marronIntenso40

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallesSesion(
    navController: NavController,
    sesionSeleccionada: Sesion,
    ejercicios: List<Ejercicio>,
    mostrarModal: Boolean,
    ejercicioSeleccionado: Ejercicio?,
    onSignOut: () -> Unit,
    alSeleccionarEjercicio: (Ejercicio) -> Unit,
    alPulsarCerrarModal: () -> Unit

) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(0.02f))
                        Text(
                            //${sesionSeleccionada.dia}:
                            modifier = Modifier.weight(1f),
                            text = sesionSeleccionada.nombre,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
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
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(20.dp),
            ) {
                items(ejercicios) { ejercicio ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(RoundedCornerShape(40.dp))
                            .clickable {
                                alSeleccionarEjercicio(ejercicio)
                            }
                            .background(marronIntenso40)
                            .padding(15.dp)//padding para childs
                    ) {
                        Text(
                            text = ejercicio.nombre, maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Icon",
                            modifier = Modifier
                                .size(24.dp)
                                .weight(0.15f)
                        )
                    }
                    Divider(modifier = Modifier.padding(8.dp)) //antes de siguiente row
                }
            }
        }
    )
    //hago if aparte para que se muestre sobre contenido scaffold y no elimine lo que ya hay en pantalla
    if (mostrarModal) {
        ejercicioSeleccionado?.let {
            ModalDetallesEjercicio(
                alPulsarCerrarModal = alPulsarCerrarModal,
                ejercicio = it
            )
        }
    }
}

@Composable
fun ModalDetallesEjercicio(alPulsarCerrarModal: () -> Unit, ejercicio: Ejercicio) {

    Dialog(
        onDismissRequest = { alPulsarCerrarModal() },
        properties = DialogProperties(dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 10.dp, // Ajusta la altura de la sombra
            tonalElevation = 5.dp // Ajusta la profundidad tonal
        ) {
            Column(
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(0.3f)
                ) {//row de titulo
                    Spacer(modifier = Modifier.weight(0.05f))
                    Text(
                        text = ejercicio.nombre,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                        ),
                        modifier = Modifier.weight(0.8f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.weight(0.1f))
                    IconButton(
                        onClick = { alPulsarCerrarModal() },
                        modifier = Modifier.weight(0.2f)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "cerrar modal")
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(5.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(blanco),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier = Modifier.background(blanco),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ejercicio.imagen,
                            contentDescription = "Imagen Ejercicio",
                            alignment = Alignment.Center,
                            contentScale = ContentScale.Inside,
                        )
                    }
                }
                Column(//columna de campos de texto
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .fillMaxWidth()
                            .padding(5.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(grisMarron20),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Anotaciones: ${ejercicio.anotaciones}",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(12.dp)
                                .background(grisMarron20),
                        )
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight(1f)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(grisMarron20),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Sets",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 5.dp, horizontal = 18.dp)
                            )
                            Text(
                                text = "${ejercicio.sets}",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 5.dp, horizontal = 18.dp)
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxHeight(1f)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(grisMarron20),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Repeticiones",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 5.dp, horizontal = 18.dp)

                            )
                            Text(
                                text = "${ejercicio.repeticiones}",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 5.dp, horizontal = 18.dp)
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxHeight(1f)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(grisMarron20),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Peso",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 5.dp, horizontal = 18.dp)
                            )
                            Text(
                                text = "${ejercicio.peso}",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 5.dp, horizontal = 18.dp)
                            )
                        }
                    }
                }

                Row(//row de video
                    modifier = Modifier
                        .weight(1f)
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ExoPlayerView(ejercicio.urlVideoEjemplo)
                    //EXO PLAYER BUGGEA PREVIEW, QUITAR MODAL DE PREVIEW PARA VER VENTANA NORMAL SIN MODAL
                }
                Spacer(modifier = Modifier.weight(0.025f))
            }
        }
    }
}

@Composable
fun ExoPlayerView(urlVideoEjemplo: String) {
    // cojo contexto
    val context = LocalContext.current
    // inicializo exoplayer
    val exoPlayer = ExoPlayer.Builder(context).build()
    // creo recurso para media
    val mediaSource = remember(urlVideoEjemplo) {
        MediaItem.Builder()
            .setUri(Uri.parse(urlVideoEjemplo))
            .build()
    }
    // prepara el exoplayer
    LaunchedEffect(mediaSource) {
        exoPlayer.setMediaItem(mediaSource)
        exoPlayer.prepare()
    }
    // se deshace del exoplayer al "descomponer modal" al cerrarlo
    DisposableEffect(Unit) {
        onDispose {
            //exoPlayer.stop()
            exoPlayer.release()
        }
    }
    // se usa android view para insertar exoplayer en composable
    AndroidView(
        factory = { contexto -> //ejecuta el siguiente lambda con el contexto previo
            PlayerView(contexto).apply {
                player = exoPlayer
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(1f)
            .clip(RoundedCornerShape(30.dp))
    )
}

//IMAGENES ASINCRONAS NO CARGAN EN LAS PREVIEW (ESTO ES NORMAL)
@Preview(showBackground = true)
@Composable
fun DetallesSesionPreview() {
    Tfg01Theme {
        //PARA PREVIEW DetallesSesion
        val navController = rememberNavController()
        val sesion = Sesion(dia = "Lunes", diaNum = 1, nombre = "Empuje", descanso = true)
        val ejercicios = listOf(
            Ejercicio(
                0,
                nombre = "Ejercicio 1",
                imagen = "https://loremflickr.com/g/320/240/paris",
                anotaciones = "Agarre neutro",
                sets = 3,
                repeticiones = 10,
                peso = 50,
                urlVideoEjemplo = "https://www.dropbox.com/scl/fi/txfcb3s7c00pexrzcs935/alternating_dumbbell_curl.mp4?rlkey=mqzt4wslfu7baxuqj7b5tgzpk&raw=1"
            ),
            Ejercicio(
                0,
                nombre = "Ejercicio 1",
                imagen = "https://loremflickr.com/g/320/240/paris",
                anotaciones = "Agarre neutro",
                sets = 3,
                repeticiones = 10,
                peso = 50,
                urlVideoEjemplo = "https://www.dropbox.com/scl/fi/txfcb3s7c00pexrzcs935/alternating_dumbbell_curl.mp4?rlkey=mqzt4wslfu7baxuqj7b5tgzpk&raw=1"
            ),
            Ejercicio(
                0,
                nombre = "Ejercicio 1",
                imagen = "https://loremflickr.com/g/320/240/paris",
                anotaciones = "Agarre neutro",
                sets = 3,
                repeticiones = 10,
                peso = 50,
                urlVideoEjemplo = "https://www.dropbox.com/scl/fi/txfcb3s7c00pexrzcs935/alternating_dumbbell_curl.mp4?rlkey=mqzt4wslfu7baxuqj7b5tgzpk&raw=1"
            ),
            Ejercicio(
                0,
                nombre = "Ejercicio 1",
                imagen = "https://loremflickr.com/g/320/240/paris",
                anotaciones = "Agarre neutro",
                sets = 3,
                repeticiones = 10,
                peso = 50,
                urlVideoEjemplo = "https://www.dropbox.com/scl/fi/txfcb3s7c00pexrzcs935/alternating_dumbbell_curl.mp4?rlkey=mqzt4wslfu7baxuqj7b5tgzpk&raw=1"
            ),
            Ejercicio(
                0,
                nombre = "Ejercicio 1",
                imagen = "https://loremflickr.com/g/320/240/paris",
                anotaciones = "Agarre neutro",
                sets = 3,
                repeticiones = 10,
                peso = 50,
                urlVideoEjemplo = "https://www.dropbox.com/scl/fi/txfcb3s7c00pexrzcs935/alternating_dumbbell_curl.mp4?rlkey=mqzt4wslfu7baxuqj7b5tgzpk&raw=1"
            ), Ejercicio(
                0,
                nombre = "Ejercicio 1",
                imagen = "https://loremflickr.com/g/320/240/paris",
                anotaciones = "Agarre neutro",
                sets = 3,
                repeticiones = 10,
                peso = 50,
                urlVideoEjemplo = "https://www.dropbox.com/scl/fi/txfcb3s7c00pexrzcs935/alternating_dumbbell_curl.mp4?rlkey=mqzt4wslfu7baxuqj7b5tgzpk&raw=1"
            )
        )
        val ejercicioSeleccionado = Ejercicio(
            0,
            nombre = "Ejercicio 1",
            imagen = "https://loremflickr.com/g/320/240/paris",
            anotaciones = "Agarre neutro",
            sets = 3,
            repeticiones = 10,
            peso = 50,
            urlVideoEjemplo = "https://www.dropbox.com/scl/fi/txfcb3s7c00pexrzcs935/alternating_dumbbell_curl.mp4?rlkey=mqzt4wslfu7baxuqj7b5tgzpk&raw=1"
        )
        val mostrarModal = false
        DetallesSesion(
            navController,
            sesion,
            ejercicios,
            mostrarModal,
            ejercicioSeleccionado,
            onSignOut = {},
            alSeleccionarEjercicio = {},
            alPulsarCerrarModal = {})

        ModalDetallesEjercicio(
            alPulsarCerrarModal = {},
            ejercicio = Ejercicio(
                id = 0,
                nombre = "Ejercicio 1",
                imagen = "https://loremflickr.com/g/320/240/paris",
                anotaciones = "Agarreaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                sets = 3,
                repeticiones = 10,
                peso = 50,
                urlVideoEjemplo = "https://www.dropbox.com/scl/fi/txfcb3s7c00pexrzcs935/alternating_dumbbell_curl.mp4?rlkey=mqzt4wslfu7baxuqj7b5tgzpk&raw=1"
            )
        )
    }

}
