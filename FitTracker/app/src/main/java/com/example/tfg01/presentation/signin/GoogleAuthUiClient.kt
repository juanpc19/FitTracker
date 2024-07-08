package com.example.tfg01.presentation.signin

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.example.tfg01.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.concurrent.CancellationException

// Clase para manejar inicio de sesión, cierre de sesión e información del usuario
class GoogleAuthUiClient(
    private val context: Context, //contexto usado para resources
    private val oneTapClient: SignInClient//instancia de SignInClient de google desde firebase (cuando metes cuenta google)
) {
    private val auth = Firebase.auth // objeto Cliente de autenticación de Firebase

    //metodo con suspend porque es corutina asincrona, devuelve IntentSender a partir de oneTapClient (cuenta del user)
    suspend fun signIn(): IntentSender? {//devuelve objeto para hacer un intent a posteriori
        val result = try { //devuelve el siguiente try de intent
            oneTapClient.beginSignIn(//empieza peticion de sign in
                buildSignInRequest()//llamo a funcion que construye y devulve la peticion que enviar
            ).await()//para esperar peticion
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e//si la excepcion es de tipo CancellationException hacer esto para evitar petardazo  de corutina
            null//en caso de que cumpla if return null para evitar el petardazo de corutina
        }
        return result?.pendingIntent?.intentSender //devuelve el intent con la info del user, ? porque pueden ser null
    }

    //este metodo construye la peticion del sign in y la devuelve
    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)//permite sign in con GoogleIdToken
                    .setFilterByAuthorizedAccounts(false)//permite sign in con todas las cuentas del dispositivo al no aplicar un filtro de las autorizadas/actual
                    .setServerClientId(context.getString(R.string.web_client_id))//asigno id usando el contexto para acceder a string de resources del proyecto
                    .build()//construyo opciones del builder
            )
            .setAutoSelectEnabled(true)//si solo 1 cuenta hara auto select
            .build()//finalmente construir la request
    }

    //metodo con suspend porque es corutina asincrona, hara sign in con usuario recibido como intent,
    //devolvera el resultado del sign in
    suspend fun signInWithIntent(intent: Intent): SignInResult {
        //extrae credencial al introducir tu cuenta google
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken//extrae token de la credencial
        //credendiales de google que seran usadas en sign in
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)

        return try {
            //asigno a user el user de la credencial de google  devuelta de forma asincrona
            val user = auth.signInWithCredential(googleCredentials).await().user
            SignInResult( //construyo el SignInResult a devolver con datos usuario extraidos del user de google
                data = user?.run {//run construye objeto
                    UserData(
                        userId = uid,//uid devuelve userId con funcionalidad de firebase Authentication
                        userName = displayName,//displayName devuelve userName con funcionalidad de firebase Authentication
                        profilePictureUrl = photoUrl?.toString(), //photoUrl devuelve url de foto perfil con funcionalidad de firebase Authentication
                        email = email.toString()//uid devuelve email con funcionalidad de firebase Authentication
                    )
                },
                errorMessage = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e//si la excepcion es de tipo CancellationException hacer esto para evitar petardazo  de corutina
            SignInResult(// y dar los siguiente valores a sign in result
                data = null,
                errorMessage = e.message
            )
        }
    }

    //metodo con suspend porque es corutina asincrona, cerrara la sesion
    suspend fun signOut() {
        try {
            oneTapClient.signOut().await()//cierre de sesion asincrono del usuario
            auth.signOut()//cierre de cliente firebase de autentificacion
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e//si la excepcion es de tipo Cance   llationException hacer esto para evitar petardazo  de corutina
        }
    }

    //metodo que devuelve data class user data con datos del usuario actual, guardados en objeto auth
    fun getSignedInUser(): UserData? = auth.currentUser?.run {//run construye objeto
        UserData(
            userId = uid,//uid devuelve userId con funcionalidad de firebase Authentication
            userName = displayName,//displayName devuelve userName con funcionalidad de firebase Authentication
            profilePictureUrl = photoUrl?.toString(), //photoUrl devuelve url de foto perfil con funcionalidad de firebase Authentication
            email = email.toString()//uid devuelve email con funcionalidad de firebase Authentication
        )
    }
}