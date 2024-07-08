package com.example.tfg01.data.datasource.firestore

import android.util.Log
import com.example.tfg01.data.model.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UsuarioFirestoreDataSourceImpl @Inject constructor(private val db: FirebaseFirestore) {

    //funcion insertara usuario
    suspend fun insertarUsuario(usuario: Usuario) {

        try {
            db.collection("usuarios")//coleccion en la que realizar insert/update
                //busca documento existente con user id, de no haberlo lo crea dandole el id del usuario
                .add(usuario)
                .await() // Espera a que se complete la operación antes de continuar
        } catch (e: Exception) {
            // Manejar la excepción
            Log.e("TAG", "Error al insertar/actualizar usuario en Firebase: ${e.message}", e)
            throw e // Lanzar la excepción nuevamente para que pueda ser manejada en capas superiores si es necesario
        }
    }

    //metodo que devolvera un usuario o null en base a id recibido
    suspend fun buscarUsuario(userId: String): Usuario? {

        val usuario: Usuario?
        try {
            val usuarioDocumentSnapshot = db.collection("usuarios")
                .whereEqualTo("userId", userId)
                .get().await()
            usuario = usuarioDocumentSnapshot.firstOrNull()?.toObject(Usuario::class.java)
        } catch (firebaseException: FirebaseFirestoreException) {
            // Manejar la excepción específica de Firebase Firestore
            Log.e(
                "TAG",
                "Error al buscar usuario en Firebase: ${firebaseException.message}",
                firebaseException
            )
            throw firebaseException // Lanzar la excepción nuevamente para que pueda ser manejada en capas superiores si es necesario
        } catch (e: Exception) {
            // Manejar otras excepciones generales
            Log.e("TAG", "Error desconocido al buscar usuario: ${e.message}", e)
            throw e // Lanzar la excepción nuevamente para que pueda ser manejada en capas superiores si es necesario
        }
        return usuario
    }

}
