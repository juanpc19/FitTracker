package com.example.tfg01.presentation.utils.composablesreusables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.tfg01.ui.theme.Tfg01Theme
import com.example.tfg01.ui.theme.gris20

@Composable
fun ModalConfirmacion(
    textoTitulo: String,
    textoCuerpo: String,
    textoConfirmar: String,
    textoCancelar: String,
    icono: ImageVector,
    alDecidirEnModal: (Boolean) -> Unit, //recibe metodo doble, que actua en base a bool recibido
) {

    AlertDialog(
        properties = DialogProperties(dismissOnClickOutside = false),
        icon = {
            Icon(icono, contentDescription = "Example Icon", modifier = Modifier.offset(y = 10.dp))
        },
        title = {
            // Text(text = textoTitulo)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(1f),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = textoTitulo, textAlign = TextAlign.Center, fontSize = 30.sp)
                }
                Spacer(modifier = Modifier.padding(vertical = 10.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = textoCuerpo, textAlign = TextAlign.Center)
                }
                Spacer(modifier = Modifier.padding(vertical = 10.dp))
                Row(modifier = Modifier.fillMaxWidth(1f)) {
                    Box(
                        modifier = Modifier
                            .padding(5.dp)
                            .weight(0.4f)
                            .offset(y = 15.dp)
                            .offset(x = 5.dp),
                        Alignment.Center
                    ) {
                        Button(
                            onClick = { alDecidirEnModal(false) },
                            colors = ButtonDefaults.buttonColors(containerColor = gris20)
                        ) {
                            Text(textoCancelar)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(5.dp)
                            .weight(0.4f)
                            .offset(y = 15.dp)
                            .offset(x = 5.dp),
                        Alignment.Center
                    ) {
                        Button(onClick = { alDecidirEnModal(true) }) {
                            Text(textoConfirmar)
                        }
                    }
                }
            }
        },
        onDismissRequest = {
        },
        confirmButton = {
        },
        dismissButton = {
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ModalConfirmacionPreview() {
    Tfg01Theme {
        val textoTitulo = "Titulo"
        //val textoCuerpo = "¿Deseas establecer la rutina como activa?"
        val textoCuerpo = "¿Estas seguro de querer borrar la rutina seleccionada?"
        val textoConfirmar = "Confirmar"
        val textoCancelar = "Cancelar"
        val icono: ImageVector = Icons.Default.Warning

        ModalConfirmacion(
            textoTitulo, textoCuerpo, textoConfirmar, textoCancelar, icono,
            alDecidirEnModal = { }
        )
    }
}