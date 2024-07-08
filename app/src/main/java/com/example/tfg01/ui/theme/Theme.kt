package com.example.tfg01.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

//primary borde superior app, fondo switch activo y boton sign in
//on primary circulo switch activo
//surface barra top y bot de scaffold y fondo de modal carga y otros modales
//on surface texto en surface
//-secondary no se
//-terciary no se
//background fondo general y boton despliegue info
//primaryContainer y OnprimaryContainer boton flotante de scaffold
//surfaceVariant para fondo de switch inactivo y fondo de item de card de info
//onSurfaceVariant para iconos custom y texto modal y AMBOS textos de card de info
//outline borde switch inactivo y icono desplegar card info

//negro verde gris
private val DarkColorScheme = darkColorScheme(
    primary = marronIntenso40,
    onPrimary = blanco,
    secondary = blanco,
    onSecondary = Color.Yellow,
    tertiary = blanco,
    onTertiary = Color.Yellow,
    background = gris20,
    onBackground = blanco,
    surface = negro,
    onSurface = blanco,
    primaryContainer = marronIntenso40,
    onPrimaryContainer = blanco,
    secondaryContainer = Color.Yellow,
    onSecondaryContainer = Color.Yellow,
    tertiaryContainer = Color.Yellow,
    onTertiaryContainer = Color.Yellow,
    surfaceVariant = grisMarron20,
    onSurfaceVariant = blanco,
    outline = marronIntenso40
)

//blanco azul marron gris
//gris claro, gris oscuro, verde, marron, marron claro
private val LightColorScheme = lightColorScheme(
    primary = Color.Yellow,
    secondary = blanco,
    tertiary = blanco,
    background = Color.Gray,
    surface = Color.Gray,
    onPrimary = blanco,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    primaryContainer = Color.White,
    onPrimaryContainer = Color.Gray,
//    secondaryContainer = Color.Yellow,
//    onSecondaryContainer = Color.Yellow,
//    tertiaryContainer = Color.Yellow,
//    onTertiaryContainer = Color.Yellow,
    surfaceVariant = Color.Green,
    onSurfaceVariant = Color.Yellow,
    outline = Color.Blue
)

//primary borde superior app, fondo switch activo y boton sign in
//on primary circulo switch activo
//surface barra top y bot de scaffold y fondo de modal carga y otros modales
//-secondary no se
//-terciary no se
//background fondo general y boton despliegue info
//primaryContainer y OnprimaryContainer boton flotante de scaffold
//surfaceVariant para fondo de switch inactivo y fondo de item de card de info
//onSurfaceVariant para iconos custom y texto modal y AMBOS textos de card de info
//outline borde switch inactivo y icono desplegar card info

@Composable
fun Tfg01Theme(
    //darkTheme: Boolean = isSystemInDarkTheme(),
    darkTheme: Boolean = true, // Force dark theme//para forzar modo oscuro
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, //pongo a false para quitar colores dinamicos de sistema y ver colores de app
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}



