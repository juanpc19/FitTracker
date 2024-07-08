package com.example.tfg01.data.datasource.firestore

import android.util.Log
import com.example.tfg01.data.model.Consejo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ConsejoFirestoreDataSourceImpl @Inject constructor(private val db: FirebaseFirestore) {

    //obtiene los consejos de la app y los devuelve en lista
    suspend fun obtenerConsejos(): List<Consejo> {

        val consejos: MutableList<Consejo> = mutableListOf()

        try {
            val consejosDocumentsSnaps = db.collection("consejos")
                .get().await()

            for (consejoDocument in consejosDocumentsSnaps.documents) {
                val consejo = consejoDocument.toObject<Consejo>()
                consejo?.let { consejos.add(it) }
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

        return consejos
    }
}