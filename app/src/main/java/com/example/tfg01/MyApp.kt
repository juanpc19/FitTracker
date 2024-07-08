package com.example.tfg01

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

//añadir android:name=".MyApp" a manifest para que la detecte
// inicializa el "administrador de inyección de dependencias cuando se inicie la aplicación.
@HiltAndroidApp
class MyApp:Application() {
}