package com.example.tfg01.data.datasource.firestore

import android.util.Log
import com.example.tfg01.data.model.Ejercicio
import com.example.tfg01.data.model.Rutina
import com.example.tfg01.data.model.Sesion
import com.example.tfg01.domain.model.RutinaConSesConEjs
import com.example.tfg01.domain.model.SesionConEjercicios
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RutinaFirestoreDataSourceImpl @Inject constructor(private val db: FirebaseFirestore) {

    //devuelve la rutina activa del usuario en base a id o null
    suspend fun buscarRutinaActivaUsuario(userId: String): Rutina? {

        val rutina: Rutina?
        try {
            val rutinaDocumentSnap = db.collection("rutinas")
                .whereEqualTo("userId", userId)
                .whereEqualTo("rutinaActiva", true)
                .get().await()
            rutina = rutinaDocumentSnap.firstOrNull()?.toObject(Rutina::class.java)


        } catch (firebaseException: FirebaseFirestoreException) {
            // Manejar la excepción específica de Firebase Firestore
            Log.e(
                "TAG",
                "Error al buscar RUTINA en Firebase: ${firebaseException.message}",
                firebaseException
            )
            throw firebaseException // Lanzar la excepción nuevamente para que pueda ser manejada en capas superiores si es necesario
        } catch (e: Exception) {
            // Manejar otras excepciones generales
            Log.e("TAG", "Error desconocido al buscar RUTINA: ${e.message}", e)
            throw e // Lanzar la excepción nuevamente para que pueda ser manejada en capas superiores si es necesario
        }
        return rutina
    }

    //devuelve lista de rutinas del usuario en abse a id
    suspend fun buscarRutinasUsuario(userId: String): List<Rutina> {
        val listaRutinas: MutableList<Rutina> = mutableListOf()

        try {
            val rutinaDocumentSnap = db.collection("rutinas").whereEqualTo("userId", userId)
                .get().await()
            for (document in rutinaDocumentSnap.documents) {
                val rutina = document.toObject<Rutina>()
                if (rutina != null) {
                    listaRutinas.add(rutina)
                }
            }
        } catch (firebaseException: FirebaseFirestoreException) {
            // Manejar la excepción específica de Firebase Firestore
            Log.e(
                "TAG",
                "Error al buscar RUTINA en Firebase: ${firebaseException.message}",
                firebaseException
            )
            throw firebaseException // Lanzar la excepción nuevamente para que pueda ser manejada en capas superiores si es necesario
        } catch (e: Exception) {
            // Manejar otras excepciones generales
            Log.e("TAG", "Error desconocido al buscar RUTINA: ${e.message}", e)
            throw e // Lanzar la excepción nuevamente para que pueda ser manejada en capas superiores si es necesario
        }

        return listaRutinas.sortedByDescending { it.rutinaActiva }
    }

    //encontrara la rutina del usuario actual a partir del nombre que actuara como identificador
    // y la devolvera o devuelve null si no la encuentra
    suspend fun obtenerRutinaSeleccionada(userId: String, nombre: String): Rutina? {

        val rutina: Rutina?
        try {
            val rutinaDocumentSnap = db.collection("rutinas")
                .whereEqualTo("userId", userId)
                .whereEqualTo("nombre", nombre)
                .get().await()
            rutina = rutinaDocumentSnap.firstOrNull()?.toObject(Rutina::class.java)


        } catch (firebaseException: FirebaseFirestoreException) {
            // Manejar la excepción específica de Firebase Firestore
            Log.e(
                "TAG",
                "Error al buscar RUTINA en Firebase: ${firebaseException.message}",
                firebaseException
            )
            throw firebaseException // Lanzar la excepción nuevamente para que pueda ser manejada en capas superiores si es necesario
        } catch (e: Exception) {
            // Manejar otras excepciones generales
            Log.e("TAG", "Error desconocido al buscar RUTINA: ${e.message}", e)
            throw e // Lanzar la excepción nuevamente para que pueda ser manejada en capas superiores si es necesario
        }
        return rutina
    }

    //activara la rutina indicada del usuario indicado y desactivara las demas
    suspend fun activarRutina(userId: String, nombre: String) {

        try {
            val rutinasSnap = db.collection("rutinas")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val batch = db.batch()
            val updates =
                mutableListOf<Pair<String, Boolean>>() // Para almacenar las actualizaciones

            for (rutina in rutinasSnap.documents) {
                val campoNombreRutina = rutina.getString("nombre")
                if (campoNombreRutina == nombre) {
                    batch.update(rutina.reference, "rutinaActiva", true)
                    updates.add(Pair(campoNombreRutina ?: "", true))

                } else {
                    batch.update(rutina.reference, "rutinaActiva", false)
                    updates.add(Pair(campoNombreRutina ?: "", false))

                }
            }

            // Log de las actualizaciones que se van a realizar
            updates.forEach { update ->
                Log.d("BATCH UPDATE", "Rutina: ${update.first}, Activada: ${update.second}")
            }
            batch.commit().await()

            Log.d("MisRutinasViewModel", "Rutinas actualizadas exitosamente.")
        } catch (firebaseException: FirebaseFirestoreException) {
            Log.e(
                "MisRutinasViewModel",
                "Error al activar rutina en Firebase: ${firebaseException.message}",
                firebaseException
            )
            throw firebaseException
        } catch (e: Exception) {
            Log.e("MisRutinasViewModel", "Error desconocido al activar rutina: ${e.message}", e)
            throw e
        }
    }

    //comprobara existencia de rutina con nombre dado para usuario indicado, devuelve true si existe else false
    suspend fun comprobarExistenciaRutina(userId: String, nombre: String): Boolean {

        var existente = true

        try {
            val rutinaDocumentSnap = db.collection("rutinas")
                .whereEqualTo("userId", userId)
                .whereEqualTo("nombre", nombre)
                .get().await()

            val rutina = rutinaDocumentSnap.firstOrNull()?.toObject(Rutina::class.java)

            if (rutinaDocumentSnap.isEmpty) {
                existente = false
            }
            Log.d("RUTINA EXISTE¿", "$rutina")

        } catch (firebaseException: FirebaseFirestoreException) {
            // Manejar la excepción específica de Firebase Firestore
            Log.e(
                "TAG",
                "Error al buscar RUTINA en Firebase: ${firebaseException.message}",
                firebaseException
            )
            throw firebaseException // Lanzar la excepción nuevamente para que pueda ser manejada en capas superiores si es necesario
        } catch (e: Exception) {
            // Manejar otras excepciones generales
            Log.e("TAG", "Error desconocido al buscar RUTINA: ${e.message}", e)
            throw e // Lanzar la excepción nuevamente para que pueda ser manejada en capas superiores si es necesario
        }
        return existente
    }

    //guardara la rutina, las sesiones que contiene en su lista de sesiones con ejs, y los ejercicios contenidos en cada sesion,
    //anidando los datos donde coresponda en FB
    //para hacer el edit en rutinaRef busco la que concida nombre y por lo demas deberia ser igual
    suspend fun crearRutina(rutinaConSesConEjs: RutinaConSesConEjs) {

        try {
            val batch = db.batch()

            // guardo la rutina
            val rutinaRef = db.collection("rutinas").document()
            rutinaConSesConEjs.rutina?.let {
                batch.set(
                    rutinaRef,
                    it
                )
            }
            // guardo las sesiones y ejercicios
            for (sesionConEjercicios in rutinaConSesConEjs.listaSesConListaEjs) {
                val sesionRef = rutinaRef.collection("sesiones").document()
                sesionConEjercicios.sesion?.let { batch.set(sesionRef, it) }

                for (ejercicio in sesionConEjercicios.listaEjercicios) {
                    if (ejercicio.nombre != "-Selecciona un ejercicio-") {
                        val ejercicioRef = sesionRef.collection("ejerciciosSesion").document()
                        batch.set(ejercicioRef, ejercicio)
                    }
                }
            }
            batch.commit().await()

        } catch (firebaseException: FirebaseFirestoreException) {
            Log.e(
                "guardarRutina",
                "Error al guardar RUTINA en Firebase: ${firebaseException.message}",
                firebaseException
            )
            throw firebaseException
        } catch (e: Exception) {
            Log.e("guardarRutina", "Error desconocido al guardar RUTINA: ${e.message}", e)
            throw e
        }
    }

    //devuelve la RutinaConSesConEjs que ha de editarse a partir de user id y nombre rutina
    suspend fun obtenerRutinaParaEditar(userId: String, nombreRutina: String): RutinaConSesConEjs? {
        var rutinaConSesConEjs: RutinaConSesConEjs? = null

        try {
            // referencia a la colección de rutinas
            val rutinasRef = db.collection("rutinas")

            // consulta para encontrar la rutina que coincida con el userId y el nombreRutina
            val rutinaQuerySnapshot = rutinasRef
                .whereEqualTo("userId", userId)
                .whereEqualTo("nombre", nombreRutina)
                .get()
                .await()

            // obtengo la referencia al documento de la rutina
            val rutinaDoc = rutinaQuerySnapshot.documents[0]
            val rutina = rutinaDoc.toObject(Rutina::class.java)

            // referencia a las sesiones de la rutina
            val sesionesRef = rutinaDoc.reference.collection("sesiones")
            val sesionesQuerySnapshot = sesionesRef.get().await()

            // lista para almacenar las sesiones con ejercicios
            val listaSesConListaEjs = mutableListOf<SesionConEjercicios>()

            for (sesionDoc in sesionesQuerySnapshot.documents) {
                val sesion = sesionDoc.toObject(Sesion::class.java)

                // referencia a los ejercicios de la sesión
                val ejerciciosRef = sesionDoc.reference.collection("ejerciciosSesion")
                val ejerciciosQuerySnapshot = ejerciciosRef.get().await()

                // lista para almacenar los ejercicios de la sesión
                val listaEjercicios = ejerciciosQuerySnapshot.documents.mapNotNull {
                    it.toObject(Ejercicio::class.java)
                }

                // añade la sesión con sus ejercicios a la lista
                listaSesConListaEjs.add(SesionConEjercicios(sesion, listaEjercicios))
            }

            // asigna la rutina con sus sesiones y ejercicios al resultado
            rutinaConSesConEjs = RutinaConSesConEjs(rutina, listaSesConListaEjs)

        } catch (firebaseException: FirebaseFirestoreException) {
            Log.e(
                "obtenerRutinaParaEditar",
                "Error al obtener RUTINA de Firebase: ${firebaseException.message}",
                firebaseException
            )
        } catch (e: Exception) {
            Log.e("obtenerRutinaParaEditar", "Error desconocido al obtener RUTINA: ${e.message}", e)
        }

        return rutinaConSesConEjs
    }

    //edita la RutinaConSesConEjs que ha de editarse a partir de user id y nombre rutina usando los datos de RutinaConSesConEjs recibida
    suspend fun editarRutina(
        userId: String,
        nombreRutina: String,
        rutinaConSesConEjs: RutinaConSesConEjs
    ) {
        try {
            // referencia a la colección de rutinas
            val rutinasRef = db.collection("rutinas")

            // Consulta para encontrar la rutina que coincida con el userID y el nombreRutina
            val rutinaQuerySnapshot = rutinasRef
                .whereEqualTo("userId", userId)
                .whereEqualTo("nombre", nombreRutina)
                .get()
                .await()

            // si no encuentra la rutina, lanzar una excepción o manejar el caso de que no exista
            if (rutinaQuerySnapshot.isEmpty) {
                throw Exception("No se encontró la rutina con el nombre especificado.")
            }

            // obtengo la referencia al documento de la rutina
            val rutinaDoc = rutinaQuerySnapshot.documents[0].reference

            val batch = db.batch()

            // actualizo la rutina
            rutinaConSesConEjs.rutina?.let {
                batch.set(rutinaDoc, it)
            }

            // elimino las sesiones y ejercicios existentes antes de añadir las nuevas
            val sesionesQuerySnapshot = rutinaDoc.collection("sesiones").get().await()
            for (sesionDoc in sesionesQuerySnapshot.documents) {
                val ejerciciosQuerySnapshot =
                    sesionDoc.reference.collection("ejerciciosSesion").get().await()
                for (ejercicioDoc in ejerciciosQuerySnapshot.documents) {
                    batch.delete(ejercicioDoc.reference)
                }
                batch.delete(sesionDoc.reference)
            }

            // guardo las nuevas sesiones y ejercicios
            for (sesionConEjercicios in rutinaConSesConEjs.listaSesConListaEjs) {
                val sesionRef = rutinaDoc.collection("sesiones").document()
                sesionConEjercicios.sesion?.let { batch.set(sesionRef, it) }

                for (ejercicio in sesionConEjercicios.listaEjercicios) {
                    if (ejercicio.nombre != "-Selecciona un ejercicio-") {
                        val ejercicioRef = sesionRef.collection("ejerciciosSesion").document()
                        batch.set(ejercicioRef, ejercicio)
                    }
                }
            }

           //hago el batch
            batch.commit().await()

        } catch (firebaseException: FirebaseFirestoreException) {
            Log.e(
                "editarRutina",
                "Error al editar RUTINA en Firebase: ${firebaseException.message}",
                firebaseException
            )
            throw firebaseException
        } catch (e: Exception) {
            Log.e("editarRutina", "Error desconocido al editar RUTINA: ${e.message}", e)
            throw e
        }
    }

    //eliminara la rutina indicada mediante nombre del usuario indicado
    suspend fun eliminarRutinaSeleccionada(userId: String, nombre: String) {
        try {
            val rutinaDocumentSnap = db.collection("rutinas")
                .whereEqualTo("userId", userId)
                .whereEqualTo("nombre", nombre)
                .get().await()

            val document = rutinaDocumentSnap.documents.firstOrNull()

            if (document != null) {
                val rutinaRef = db.collection("rutinas").document(document.id)
                val sesionesSnap = rutinaRef.collection("sesiones").get().await()

                // elimino ejercicios anidados dentro de cada sesión
                for (sesionDocument in sesionesSnap.documents) {
                    val ejerciciosSnap =
                        sesionDocument.reference.collection("ejerciciosSesion").get().await()
                    for (ejercicioDocument in ejerciciosSnap.documents) {
                        ejercicioDocument.reference.delete().await()
                    }
                    // eliminao la sesión
                    sesionDocument.reference.delete().await()
                }

                //hago el batch
                rutinaRef.delete().await()
            } else {
                Log.d("TAG", "Rutina no encontrada")
            }
        } catch (firebaseException: FirebaseFirestoreException) {
            Log.e(
                "TAG",
                "Error al eliminar la RUTINA en Firebase: ${firebaseException.message}",
                firebaseException
            )
            throw firebaseException // Lanzar la excepción nuevamente para que pueda ser manejada en capas superiores si es necesario
        } catch (e: Exception) {
            Log.e("TAG", "Error desconocido al eliminar la RUTINA: ${e.message}", e)
            throw e // Lanzar la excepción nuevamente para que pueda ser manejada en capas superiores si es necesario
        }
    }


}