package com.example.tfg01.presentation.utils.navegation

//sealed class similar a singleton una clase con subclases(objetos) de instancia única con
// atributos predefinidos, se usara para definir rutas y evitar sustituirlas a mano en varias partes
//de la app evitando errores de escritura, si requieren parametros indicar de 1 de estas formas:
//route = "${AppPantallas.PantallaCargaPerfil.route}/{userIdNav}"
//route = AppPantallas.PantallaCargaPerfil.route + "/{userIdNav}"
sealed class RutasPantallas(val route: String) {
    object PantallaSignIn : RutasPantallas("pantallaSignIn")
    object PantallaCargaPerfil : RutasPantallas("pantallaCargaPerfil")
    object PantallaDetallesSesion : RutasPantallas("pantallaDetallesSesion")
    object PantallaMisRutinas : RutasPantallas("pantallaMisRutinas")
    object PantallaCrearRutina : RutasPantallas("pantallaCrearRutina")
    object PantallaEditarRutina : RutasPantallas("pantallaEditarRutina")
    object PantallaInfo : RutasPantallas("pantallaInfo")
    object PantallaMisEjercicios : RutasPantallas("pantallaMisEjercicios")
}
