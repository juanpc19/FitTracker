package com.example.tfg01.data.datasource.firestore

import android.util.Log
import com.example.tfg01.data.model.Ejercicio
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EjercicioFirestoreDataSourceImpl @Inject constructor(private val db: FirebaseFirestore) {

    //devolvera los ejs de la app en una lista (AL FINAL NO SE HA USADO)
    suspend fun obtenerPlantillaEjercicios(): List<Ejercicio> {

        val ejercicios: MutableList<Ejercicio> = mutableListOf()

        try {
            val ejerciciosDocumentsSnaps = db.collection("ejercicios")
                .get().await()

            for (ejercicioDocument in ejerciciosDocumentsSnaps.documents) {
                val ejercicio = ejercicioDocument.toObject<Ejercicio>()
                ejercicio?.let { ejercicios.add(it) }
            }
        } catch (firebaseException: FirebaseFirestoreException) {
            Log.e(
                "TAG",
                "Error al buscar Consejos en Firebase: ${firebaseException.message}",
                firebaseException
            )
            throw firebaseException
        } catch (e: Exception) {
            Log.e("TAG", "Error desconocido al buscar Consejos: ${e.message}", e)
            throw e
        }

        return ejercicios.sortedBy { it.id }
    }

    //devuelve lista de ejercicios asociados a una sesion (identificada por dia)
    // asociados a una rutina (identificada por id user y nombre rutina)
    suspend fun obtenerEjPorSesionPorRutina(userId: String, nombreRutina: String, dia: String)
            : List<Ejercicio> {

        val ejercicios: MutableList<Ejercicio> = mutableListOf()

        try {
            val rutinaDocumentSnap = db.collection("rutinas")
                .whereEqualTo("userId", userId)
                .whereEqualTo("nombre", nombreRutina)
                .get().await()

            val rutinaDocument = rutinaDocumentSnap.documents.firstOrNull()
            //query en anidamiento a partir de rutinas hacia sesiones
            if (rutinaDocument != null) {
                val sesionesDocumentSnap = rutinaDocument.reference
                    .collection("sesiones")
                    .whereEqualTo("dia", dia)
                    .get().await()

                val sesionDocument = sesionesDocumentSnap.documents.firstOrNull()
                if (sesionDocument != null) {
                    val ejerciciosDocumentSnap = sesionDocument.reference
                        .collection("ejerciciosSesion")
                        .get().await()

                    for (ejercicioDocument in ejerciciosDocumentSnap.documents) {
                        val ejercicio = ejercicioDocument.toObject<Ejercicio>()
                        ejercicio?.let { ejercicios.add(it) }
                    }
                }
            }

        } catch (firebaseException: FirebaseFirestoreException) {
            Log.e(
                "TAG",
                "Error al buscar EJERCICIOS de la RUTINA en Firebase: ${firebaseException.message}",
                firebaseException
            )
            throw firebaseException
        } catch (e: Exception) {
            Log.e("TAG", "Error desconocido al buscar EJERCICIOS de la sesion: ${e.message}", e)
            throw e
        }

        return ejercicios
    }

    //devuelve lista de ejs personaizados de usuario de id dado ordenados por id ejercicio
    suspend fun obtenerEjPersonalizadosUsuario(userId: String): List<Ejercicio> {
        val ejercicios: MutableList<Ejercicio> = mutableListOf()

        try {
            val usuarioDocumentSnap = db.collection("usuarios")
                .whereEqualTo("userId", userId)
                .get().await()

            val usuarioDocument = usuarioDocumentSnap.documents.firstOrNull()
            if (usuarioDocument != null) {
                val ejerciciosDocumentSnap = usuarioDocument.reference
                    .collection("ejerciciosPersonalizados")
                    .get().await()

                for (ejercicioDocument in ejerciciosDocumentSnap.documents) {
                    val ejercicio = ejercicioDocument.toObject<Ejercicio>()
                    ejercicio?.let { ejercicios.add(it) }
                }
            }

        } catch (firebaseException: FirebaseFirestoreException) {
            Log.e(
                "TAG",
                "Error al buscar EJERCICIOS de la RUTINA en Firebase: ${firebaseException.message}",
                firebaseException
            )
            throw firebaseException
        } catch (e: Exception) {
            Log.e("TAG", "Error desconocido al buscar EJERCICIOS de la sesion: ${e.message}", e)
            throw e
        }

        return ejercicios.sortedBy { it.id }
    }


    //devuelve lista de ejs conjuntos (de la app y de usuario) de usuario de id dado ordenados por id ejercicio
    suspend fun obtenerEjConjuntosUsuario(userId: String): List<Ejercicio> {
        val ejercicios: MutableList<Ejercicio> = mutableListOf()

        try {
            val ejerciciosDocumentsSnaps = db.collection("ejercicios")
                .get().await()

            for (ejercicioDocument in ejerciciosDocumentsSnaps.documents) {
                val ejercicio = ejercicioDocument.toObject<Ejercicio>()
                ejercicio?.let { ejercicios.add(it) }
            }

            val usuarioDocumentSnap = db.collection("usuarios")
                .whereEqualTo("userId", userId)
                .get().await()

            val usuarioDocument = usuarioDocumentSnap.documents.firstOrNull()

            if (usuarioDocument != null) {
                val ejerciciosPerDocumentSnap = usuarioDocument.reference
                    .collection("ejerciciosPersonalizados")
                    .get().await()

                for (ejercicioPerDocument in ejerciciosPerDocumentSnap.documents) {
                    val ejercicio = ejercicioPerDocument.toObject<Ejercicio>()
                    ejercicio?.let { ejercicios.add(it) }
                }
            }
        } catch (firebaseException: FirebaseFirestoreException) {
            Log.e(
                "TAG",
                "Error al buscar EJERCICIOS de la RUTINA en Firebase: ${firebaseException.message}",
                firebaseException
            )
            throw firebaseException
        } catch (e: Exception) {
            Log.e("TAG", "Error desconocido al buscar EJERCICIOS de la sesion: ${e.message}", e)
            throw e
        }

        return ejercicios.sortedBy { it.id }
    }

    //comprueba la existencia de un ej personalizado usando el nombre de ejercicio y id de usuario
    //devuelve true si existe false de lo contrario
    suspend fun comprobarExistenciaEjercicio(userId: String, nombreEjercicio: String): Boolean {

        var ejercicioExistente = false

        try {
            val usuarioDocumentSnap = db.collection("usuarios")
                .whereEqualTo("userId", userId)
                .get().await()

            val usuarioDocument = usuarioDocumentSnap.documents.firstOrNull()
            if (usuarioDocument != null) {
                val ejerciciosDocumentSnap = usuarioDocument.reference
                    .collection("ejerciciosPersonalizados")
                    .whereEqualTo("nombre", nombreEjercicio)
                    .get()
                    .await()

                if (!ejerciciosDocumentSnap.isEmpty) {
                    ejercicioExistente = true
                }
            }
        } catch (firebaseException: FirebaseFirestoreException) {
            Log.e(
                "TAG",
                "Error al buscar EJERCICIO en Firebase: ${firebaseException.message}",
                firebaseException
            )
            throw firebaseException
        } catch (e: Exception) {
            Log.e("TAG", "Error desconocido al buscar EJERCICIO: ${e.message}", e)
            throw e
        }

        return ejercicioExistente
    }

    //crea un ejercicio recibido en la coleccion de ejs personalizados del usuario cuyo id se indica
    suspend fun crearEjercicio(userId: String, ejercicio: Ejercicio) {

        try {
            val usuarioDocumentSnap = db.collection("usuarios")
                .whereEqualTo("userId", userId)
                .get().await()

            val usuarioDocument = usuarioDocumentSnap.documents.firstOrNull()
            usuarioDocument?.reference?.collection("ejerciciosPersonalizados")?.add(ejercicio)
                ?.await()
        } catch (firebaseException: FirebaseFirestoreException) {
            Log.e(
                "TAG",
                "Error al crear ejercicio en firebase: ${firebaseException.message}",
                firebaseException
            )
            throw firebaseException
        } catch (e: Exception) {
            Log.e("TAG", "Error desconocido al crear ejercicio: ${e.message}", e)
            throw e
        }
    }

    //edita un ejercicio recibido en la coleccion de ejs personalizados del usuario cuyo id se indica
    suspend fun editarEjercicio(userId: String, ejercicio: Ejercicio) {

        try {
            val usuarioDocumentSnap = db.collection("usuarios")
                .whereEqualTo("userId", userId)
                .get().await()

            val usuarioDocument = usuarioDocumentSnap.documents.firstOrNull()
            if (usuarioDocument != null) {
                val ejerciciosQuerySnap = usuarioDocument.reference
                    .collection("ejerciciosPersonalizados")
                    .whereEqualTo("nombre", ejercicio.nombre)
                    .get()
                    .await()

                val ejercicioDocument = ejerciciosQuerySnap.documents.firstOrNull()
                ejercicioDocument?.reference?.set(ejercicio)?.await()
            }

        } catch (firebaseException: FirebaseFirestoreException) {
            Log.e(
                "TAG",
                "Error al editar ejercicio en firebase: ${firebaseException.message}",
                firebaseException
            )
            throw firebaseException
        } catch (e: Exception) {
            Log.e("TAG", "Error desconocido al editar ejercicio: ${e.message}", e)
            throw e
        }
    }

    //elimina un ejercicio cuyo nombre es recibido en la coleccion de ejs personalizados del usuario cuyo id se indica
    suspend fun eliminarEjercicio(userId: String, nombreEjercicio: String) {

        try {
            val usuarioDocumentSnap = db.collection("usuarios")
                .whereEqualTo("userId", userId)
                .get().await()

            val usuarioDocument = usuarioDocumentSnap.documents.firstOrNull()
            if (usuarioDocument != null) {
                val ejerciciosQuerySnap = usuarioDocument.reference
                    .collection("ejerciciosPersonalizados")
                    .whereEqualTo("nombre", nombreEjercicio)
                    .get()
                    .await()

                val ejercicioDocument = ejerciciosQuerySnap.documents.firstOrNull()
                ejercicioDocument?.reference?.delete()?.await()
            }

        } catch (firebaseException: FirebaseFirestoreException) {
            Log.e(
                "TAG",
                "Error al eliminar ejercicio en firebase: ${firebaseException.message}",
                firebaseException
            )
            throw firebaseException
        } catch (e: Exception) {
            Log.e("TAG", "Error desconocido al eliminar ejercicio: ${e.message}", e)
            throw e
        }
    }

}