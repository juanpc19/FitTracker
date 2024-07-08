package com.example.tfg01.presentation.utils.composablesreusables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.tfg01.ui.theme.Tfg01Theme

@Composable
fun ModalCargaDatos(mensaje: String) {

    Dialog(onDismissRequest = { }, properties = DialogProperties(dismissOnClickOutside = false)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), //
            contentAlignment = Alignment.Center // Centra el contenido dentro del Box
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(mensaje)
                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(modifier = Modifier.size(64.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ModalCargaDatosPreview() {
    Tfg01Theme {
        val mensaje: String = "hola mundo!"
        ModalCargaDatos(mensaje)
    }
}