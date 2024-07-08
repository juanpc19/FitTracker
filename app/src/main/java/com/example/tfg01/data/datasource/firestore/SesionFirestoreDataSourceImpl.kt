package com.example.tfg01.data.datasource.firestore

import android.util.Log
import com.example.tfg01.data.model.Sesion
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.toObject

import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SesionFirestoreDataSourceImpl @Inject constructor(private val db: FirebaseFirestore) {

    //devolvera las sesiones de la rutina activa perteneciente al usuario indicado en una lista
    suspend fun buscarSesionesRutinaActiva(userId: String): List<Sesion> {

        val sesiones: MutableList<Sesion> = mutableListOf()

        try {
            val rutinaDocumentSnap = db.collection("rutinas")
                .whereEqualTo("userId", userId)
                .whereEqualTo("rutinaActiva", true)
                .get().await()

            //query en anidamiento a partir de rutinas hacia sesiones
            for (document in rutinaDocumentSnap.documents) {
                val sesionesDocumentSnap = document.reference
                    .collection("sesiones")
                    .get().await()

                for (sesionDocument in sesionesDocumentSnap.documents) {
                    val sesion = sesionDocument.toObject<Sesion>()
                    sesion?.let { sesiones.add(it) }
                }
            }

        } catch (firebaseException: FirebaseFirestoreException) {
            Log.e(
                "TAG",
                "Error al buscar SESIONES de la RUTINA en Firebase: ${firebaseException.message}",
                firebaseException
            )
            throw firebaseException
        } catch (e: Exception) {
            Log.e("TAG", "Error desconocido al buscar SESIONES de la RUTINA: ${e.message}", e)
            throw e
        }


        return sesiones.sortedBy { it.diaNum }
    }

    //devolvera los datos de la sesion indicada por dia de la rutina a la que pertenece, del usuario al que pertenece
    //o null de no encontrarse
    suspend fun obtenerSesionSeleccionada(userId: String, nombreRutina: String, dia: String)
            : Sesion? {

        var sesion: Sesion? = null

        try {
            val rutinaDocumentSnap = db.collection("rutinas")
                .whereEqualTo("userId", userId)
                .whereEqualTo("nombre", nombreRutina)
                .get().await()

            val rutinaDocument = rutinaDocumentSnap.documents.firstOrNull()
            //query en anidamiento a partir de rutinas hacia sesiones
            if (rutinaDocument != null) {
                val sesioneDocumentSnap = rutinaDocument.reference
                    .collection("sesiones")
                    .whereEqualTo("dia", dia)
                    .get().await()

                sesion = sesioneDocumentSnap.firstOrNull()?.toObject(Sesion::class.java)
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

        return sesion
    }

}