package com.example.tfg01.presentation.cargarperfil


import android.util.Log
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.tfg01.data.model.Rutina
import com.example.tfg01.data.model.Sesion
import com.example.tfg01.data.model.Usuario
import com.example.tfg01.domain.model.EstadoToast
import com.example.tfg01.presentation.utils.composablesreusables.BottomBar
import com.example.tfg01.presentation.utils.composablesreusables.ModalCargaDatos
import com.example.tfg01.presentation.utils.navegation.RutasPantallas
import com.example.tfg01.ui.theme.Tfg01Theme
import com.example.tfg01.ui.theme.grisMarron20
import com.example.tfg01.ui.theme.marronIntenso40
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPerfil(
    navController: NavController,
    usuario: Usuario,
    rutinaActual: Rutina?,
    sesiones: List<Sesion>,
    onSignOut: () -> Unit,
    mostrarModalCarga: Boolean,
    estadoToast: EstadoToast?,
    navegarToDetallesSesion: Boolean,
    sesionPulsada: Sesion?,
    alPulsarSesion: (Sesion) -> Unit,
    resetToastState: () -> Unit,
    ) {

    val hoy = LocalDate.now().dayOfWeek.value

    val context = LocalContext.current

    LaunchedEffect(estadoToast) {
        if (estadoToast?.hacerToast == true) {
            Toast.makeText(context, estadoToast.mensajeToast, Toast.LENGTH_SHORT).show()
            resetToastState()
        }
    }

    LaunchedEffect(navegarToDetallesSesion) {
        if (navegarToDetallesSesion)
            navController.navigate(
                "${RutasPantallas.PantallaDetallesSesion.route}/${sesionPulsada?.dia}/${rutinaActual?.nombre}"
            )

    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(1f),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically

                    ) {
                        Spacer(modifier = Modifier.weight(0.02f))
                        AsyncImage(
                            model = usuario.profilePictureUrl,
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                .weight(0.15f)
                                .size(35.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.weight(0.08f))
                        Text(
                            text = rutinaActual?.nombre ?: "",
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.weight(0.05f))
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
            //defino contenido en base a switch
            when {
                mostrarModalCarga -> { //modal de carga y recarga de datos, bool controlado por vm
                    ModalCargaDatos(mensaje = "Cargando datos...")
                }

                rutinaActual == null -> {//si la rutina es null muestro mensaje para crear una
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
                            text = "Crea tu primera rutina de entrenamiento o activa una existente",
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
                        items(sesiones) { sesion ->
                            val colorFondo = if (sesion.diaNum < hoy) {
                                grisMarron20 // Si el día de la sesión es menor que el día actual, establece el color de fondo a gris
                            } else {
                                marronIntenso40 // Si el día de la sesión es mayor que el día actual, establece el color de fondo a blanco
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .clip(RoundedCornerShape(40.dp))
                                    .clickable {
                                        alPulsarSesion(sesion)
                                    }
                                    .background(colorFondo)
                                    .padding(15.dp)//padding para childs

                            ) {
                                Text(text = sesion.dia, modifier = Modifier.weight(1.25f))
                                Spacer(modifier = Modifier.weight(0.25f))
                                Text(
                                    text = if (sesion.descanso) "Descanso" else sesion.nombre,
                                    modifier = Modifier.weight(2f)

                                )

                                Spacer(modifier = Modifier.weight(0.25f))
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Icon",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .weight(0.5f)
                                )
                            }
                            Divider(modifier = Modifier.padding(8.dp)) //antes de siguiente row

                        }
                    }
                }
            }

        }
    )
}

@Preview(showBackground = true)
@Composable
fun PantallaPerfilPreview() {
    Tfg01Theme {
        val navController = rememberNavController()
        val mostrarModalCarga = false
        val usuario = Usuario(
            userId = "123",
            userName = "John Doe",
            profilePictureUrl = "https://loremflickr.com/g/320/240/paris",
            email = "juan@gmail.com"
        )

        val rutina = Rutina(
            userId = "Hg9pbXYbEgYKE5yICuKfnZaozYM2",
            nombre = "Rutina 1",
            rutinaActiva = true
        )
        val sesiones = listOf(
            Sesion(dia = "Lunes", diaNum = 1, nombre = "Empuje", descanso = false),
            Sesion(dia = "Martes", diaNum = 2, nombre = "Descanso", descanso = true),
            Sesion(dia = "Miércoles", diaNum = 3, nombre = "Tirar", descanso = false),
            Sesion(dia = "Jueves", diaNum = 4, nombre = "Descanso", descanso = true),
            Sesion(dia = "Viernes", diaNum = 5, nombre = "Piernas", descanso = false),
            Sesion(dia = "Sábado", diaNum = 6, nombre = "Descanso", descanso = true),
            Sesion(dia = "Domingo", diaNum = 7, nombre = "Descanso", descanso = true)
        )

        val sesionPulsada = Sesion(dia = "Lunes", diaNum = 1, nombre = "Empuje", descanso = false)

        val estadoToast = EstadoToast(false, "beeee")
        PantallaPerfil(
            navController, usuario, rutina, sesiones,
            onSignOut = {},
            mostrarModalCarga,
            estadoToast,
            navegarToDetallesSesion = false,
            sesionPulsada,
            alPulsarSesion = { },
            resetToastState = { }
        )
    }
}
