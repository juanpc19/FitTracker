package com.example.tfg01.presentation.utils.composablesreusables


import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tfg01.R
import com.example.tfg01.presentation.utils.navegation.RutasPantallas
import com.example.tfg01.ui.theme.Tfg01Theme

//Barra inferior de navegacion, contiene iconos con clickeables que haran navegacion,
@Composable
fun BottomBar(navController: NavController) {

    BottomAppBar() {
        Spacer(modifier = Modifier.weight(0.5f))
        IconButton(onClick = { navController.navigate(RutasPantallas.PantallaCargaPerfil.route) }) {
            Icon(Icons.Default.Home, contentDescription = "Boton navegacion carga perfil")
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { navController.navigate(RutasPantallas.PantallaMisRutinas.route) }) {
            Icon(Icons.Default.List, contentDescription = "Boton navegacion mis rutinas")
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { navController.navigate(RutasPantallas.PantallaInfo.route) }) {
            Icon(Icons.Default.Info, contentDescription = "Boton navegacion Info")
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { navController.navigate(RutasPantallas.PantallaMisEjercicios.route) }) {
            //TODO EL CUSTOM  PETA TODOS LOS PREVIEWS
//            Icon(Icons.Default.Add, contentDescription = "Boton navegacion crear ejercicio")
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.add_exercise),
                contentDescription = "Boton navegacion crear ejercicio"
            )
        }
        Spacer(modifier = Modifier.weight(0.5f))
    }
}

@Preview(showBackground = true)
@Composable
fun BottomBarPreview() {
    Tfg01Theme {
        val navController = rememberNavController()
        BottomBar(navController)
    }
}