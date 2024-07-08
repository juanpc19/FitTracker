package com.example.tfg01.domain.model

object UsuarioActualSingleton {
    private lateinit var _idUsuarioActual: String//es privado y se iniciara a posteriori

    //que esto se val y cuente solo con un get hara que sea read only,
    // al llamar al sin guion que es publico se accedera al valor del privado,
    // no pudiendo modificarse este ultimo si no es con el metodo
    val idUsuarioActual: String
        get() = _idUsuarioActual

    //este metodo permitira modificar valor de _idUsuarioActual
    fun establecerUsuario(idUsuario: String) {

        //le da al atributo valor recibido
        _idUsuarioActual = idUsuario
    }
}