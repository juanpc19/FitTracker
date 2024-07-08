package com.example.tfg01.presentation.info

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tfg01.data.model.Consejo
import com.example.tfg01.domain.model.EstadoDesplegable
import com.example.tfg01.presentation.utils.composablesreusables.BottomBar
import com.example.tfg01.presentation.utils.composablesreusables.ModalCargaDatos
import com.example.tfg01.ui.theme.Tfg01Theme
import com.example.tfg01.ui.theme.marronIntenso50


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Info(
    navController: NavController,
    consejos: List<Consejo>,
    mostrarModalCarga: Boolean,
    onSignOut: () -> Unit,
    alFiltrarPorTodo: () -> Unit,
    alFiltrarPorNutricionSalud: () -> Unit,
    alFiltrarPorEntrenamiento: () -> Unit,
    estadosDeDesplegables: List<EstadoDesplegable>,
    alPulsarDesplegable: (Int) -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Spacer(modifier = Modifier.weight(0.02f))
                        Text(text = "Preguntas frecuentes", modifier = Modifier.weight(1f))
                    }
                },
                actions = {
                    IconButton(onClick = onSignOut) {
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
                            contentDescription = "icono cerrar sesion"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomBar(navController)
        },
        content = { paddingValues ->

            if (mostrarModalCarga) {
                ModalCargaDatos(mensaje = "Cargando Datos...")
            } else {
                Column(modifier = Modifier.padding(paddingValues)) {
                    LazyRow(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .offset(y = 10.dp, x = 10.dp),
                    ) {
                        item {
                            Button(
                                onClick = alFiltrarPorTodo,
                                contentPadding = PaddingValues(
                                    start = 10.dp,
                                    top = 8.dp,
                                    end = 10.dp,
                                    bottom = 8.dp
                                )
                            ) {
                                Text(
                                    text = "Todos",
                                    fontSize = 14.sp
                                )
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        item {
                            Button(
                                onClick = alFiltrarPorNutricionSalud,
                                contentPadding = PaddingValues(
                                    start = 10.dp,
                                    top = 8.dp,
                                    end = 10.dp,
                                    bottom = 8.dp
                                )
                            ) {
                                Text(
                                    text = "Nutricion y salud",
                                    fontSize = 14.sp,
                                )
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        item {
                            Button(
                                onClick = alFiltrarPorEntrenamiento,
                                contentPadding = PaddingValues(
                                    start = 10.dp,
                                    top = 8.dp,
                                    end = 10.dp,
                                    bottom = 8.dp
                                )
                            ) {
                                Text(
                                    text = "Entrenamiento",
                                    fontSize = 14.sp,
                                )
                            }
                        }
                    }
                    FAQSection(consejos, estadosDeDesplegables, alPulsarDesplegable)
                }
            }
        }
    )
}

//crea una columna con las card desplegables
@Composable
fun FAQSection(
    consejos: List<Consejo>,
    estadosDeDesplegables: List<EstadoDesplegable>,
    alPulsarDesplegable: (Int) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .padding(20.dp)
    )
    {
        items(consejos, key = { it.id }) { consejo ->
            val estado = estadosDeDesplegables.find { it.id == consejo.id }?.estado ?: false
            ExpandableFAQCard(consejo, estado, alPulsarDesplegable)
        }
    }
}

@Composable
fun ExpandableFAQCard(consejo: Consejo, estado: Boolean, alPulsarDesplegable: (Int) -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(//tipo animacion en expansion
                animationSpec = spring(
                    dampingRatio = 0.35f,
                    stiffness = 600f,
                )
//                        animationSpec = tween(
//                        durationMillis = 400,
//                easing = LinearOutSlowInEasing)
            )
            .clickable { alPulsarDesplegable(consejo.id) }
            .padding(vertical = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 0.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(3.dp)
            ) {
                Text(
                    text = consejo.pregunta,
                    modifier = Modifier.weight(6f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = if (!estado) 2 else 3,
                    //lineas a mostrar del texto si expanded se muestra 1 y se plica ellipsis else se muestran hasta 3 como visible
                    //elipsis pone puntos al final del texto visible permite salida de texto de su container
                    overflow = if (!estado) TextOverflow.Ellipsis else TextOverflow.Visible,
                    fontFamily = FontFamily.Default
                )
                IconButton(
                    onClick = { alPulsarDesplegable(consejo.id) },
                    modifier = Modifier
                        .weight(0.75f)
                        .alpha(0.9f),
                ) {//decides icono en base bool expandedState
                    if (estado)
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "icono replegar",
                            modifier = Modifier.size(20.dp),
                            tint = marronIntenso50
                        )
                    else
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "icono desplegar",
                            modifier = Modifier.size(20.dp),
                            tint = marronIntenso50
                        )
                }
            }//y si expandido creas el texto
            if (estado) {
                Text(
                    text = consejo.respuesta,
                    modifier = Modifier.padding(bottom = 10.dp),
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun InfoPreview() {
    Tfg01Theme {
        val navController = rememberNavController()
        val mostrarModalCarga = false
        val estadosDeDesplegables: List<EstadoDesplegable> = emptyList()
        val consejos = listOf(
            (Consejo(
                id = 0,
                pregunta = "¿He perdido la cabeza?",
                respuesta = "Esa yegua no es mi vieja yegua gris, vieja yegua gris, vieja yegua gris...",
                topico = "entrenamiento"
            )),
            (Consejo(
                id = 1,
                pregunta = "¿He perdido la cabeza?",
                respuesta = "Esa yegua no es mi vieja yegua gris, vieja yegua gris, vieja yegua gris...",
                topico = "entrenamiento"
            )),
            (Consejo(
                id = 2,
                pregunta = "¿He perdido la cabeza?",
                respuesta = "Esa yegua no es mi vieja yegua gris, vieja yegua gris, vieja yegua gris...",
                topico = "entrenamiento"
            )),
            (Consejo(
                id = 3,
                pregunta = "¿He perdido la cabeza?",
                respuesta = "Esa yegua no es mi vieja yegua gris, vieja yegua gris, vieja yegua gris...",
                topico = "entrenamiento"
            )),
            (Consejo(
                id = 4,
                pregunta = "¿He perdido la cabeza?",
                respuesta = "Esa yegua no es mi vieja yegua gris, vieja yegua gris, vieja yegua gris...",
                topico = "entrenamiento"
            )),
            (Consejo(
                id = 5,
                pregunta = "¿He perdido la cabeza?",
                respuesta = "Esa yegua no es mi vieja yegua gris, vieja yegua gris, vieja yegua gris...",
                topico = "entrenamiento"
            )),
            (Consejo(
                id = 6,
                pregunta = "¿He perdido la cabeza?",
                respuesta = "Esa yegua no es mi vieja yegua gris, vieja yegua gris, vieja yegua gris...",
                topico = "entrenamiento"
            )),
            (Consejo(
                id = 7,
                pregunta = "¿He perdido la cabeza?",
                respuesta = "Esa yegua no es mi vieja yegua gris, vieja yegua gris, vieja yegua gris...",
                topico = "entrenamiento"
            )),
            (Consejo(
                id = 8,
                pregunta = "¿He perdido la cabeza?",
                respuesta = "Esa yegua no es mi vieja yegua gris, vieja yegua gris, vieja yegua gris...",
                topico = "entrenamiento"
            )),
            (Consejo(
                id = 9,
                pregunta = "¿He perdido la cabeza?",
                respuesta = "Esa yegua no es mi vieja yegua gris, vieja yegua gris, vieja yegua gris...",
                topico = "entrenamiento"
            )),
            (Consejo(
                id = 10,
                pregunta = "¿He perdido la cabeza?",
                respuesta = "Esa yegua no es mi vieja yegua gris, vieja yegua gris, vieja yegua gris...",
                topico = "entrenamiento"
            )),
            (Consejo(
                id = 11,
                pregunta = "¿He perdido la cabeza?",
                respuesta = "Esa yegua no es mi vieja yegua gris, vieja yegua gris, vieja yegua gris...",
                topico = "entrenamiento"
            )),
            (Consejo(
                id = 12,
                pregunta = "¿He perdido la cabeza?",
                respuesta = "Esa yegua no es mi vieja yegua gris, vieja yegua gris, vieja yegua gris...",
                topico = "entrenamiento"
            )),
            (Consejo(
                id = 13,
                pregunta = "¿He perdido la cabeza?",
                respuesta = "Esa yegua no es mi vieja yegua gris, vieja yegua gris, vieja yegua gris...",
                topico = "entrenamiento"
            )),
            (Consejo(
                id = 14,
                pregunta = "¿He perdido la cabeza?",
                respuesta = "Esa yegua no es mi vieja yegua gris, vieja yegua gris, vieja yegua gris...",
                topico = "entrenamiento"
            )),
            (Consejo(
                id = 15,
                pregunta = "¿He perdido la cabeza?",
                respuesta = "Esa yegua no es mi vieja yegua gris, vieja yegua gris, vieja yegua gris...",
                topico = "entrenamiento"
            ))
        )
        Info(
            navController, consejos, mostrarModalCarga,
            onSignOut = {},
            alFiltrarPorTodo = {},
            alFiltrarPorNutricionSalud = {},
            alFiltrarPorEntrenamiento = {},
            estadosDeDesplegables = estadosDeDesplegables,
            alPulsarDesplegable = {}
        )
    }
}