package com.example.tfg01.presentation.signin

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class SignInViewModel @Inject constructor() :
    ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    //guarda estado del estado de sign in de forma publica como read only para uso en otras partes externas a la clase
    val state = _state.asStateFlow()

    //metodo que recibe un objeto SignInResult de nuestra data class SignInResult llamado result
    fun onSingInResult(signInResult: SignInResult?) {

        _state.update {// Dentro del bloque lambda, actualizamos el estado actual (_state)
            it.copy(
                //actualizo valor de isSignInSuccessful si el result contiene datos del user pasa a ser true
                isSignInSuccessful = signInResult?.data != null,
                //actualizo valor de signInError de sign in state con valor de errorMessage de sign in result
                signInError = signInResult?.errorMessage
            )
        }
    }

    //metodo que actualiza/reinicia valores de SignInState al hacer log out
    fun resetState() {
        //update de SignInState por defecto el isSignInSuccessful de este volvera a valor original false
        //y el sign in error a string null
        _state.update { SignInState() }
    }

}