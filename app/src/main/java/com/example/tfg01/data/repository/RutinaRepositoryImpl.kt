package com.example.tfg01.data.repository

import com.example.tfg01.data.datasource.firestore.RutinaFirestoreDataSourceImpl
import com.example.tfg01.data.model.Rutina
import com.example.tfg01.domain.model.RutinaConSesConEjs
import com.example.tfg01.domain.repository.RutinaRepository
import javax.inject.Inject

class RutinaRepositoryImpl @Inject constructor(private val rutinasFirestoresDataSourceImpl: RutinaFirestoreDataSourceImpl) :
    RutinaRepository {

    override suspend fun buscarRutinaActivaUsuario(userId: String): Rutina? {
        return rutinasFirestoresDataSourceImpl.buscarRutinaActivaUsuario(userId)
    }

    override suspend fun buscarRutinasUsuario(userId: String): List<Rutina> {
        return rutinasFirestoresDataSourceImpl.buscarRutinasUsuario(userId)
    }

    override suspend fun obtenerRutinaSeleccionada(userId: String, nombre: String): Rutina? {
        return rutinasFirestoresDataSourceImpl.obtenerRutinaSeleccionada(userId, nombre)
    }

    override suspend fun activarRutina(userId: String, nombre: String) {
        rutinasFirestoresDataSourceImpl.activarRutina(userId, nombre)
    }

    override suspend fun comprobarExistenciaRutina(userId: String, nombre: String): Boolean {
        return rutinasFirestoresDataSourceImpl.comprobarExistenciaRutina(userId, nombre)
    }

    override suspend fun crearRutina(rutinaConSesConEjs: RutinaConSesConEjs) {
        return rutinasFirestoresDataSourceImpl.crearRutina(rutinaConSesConEjs)
    }

    override suspend fun obtenerRutinaParaEditar(userId: String, nombreRutina: String): RutinaConSesConEjs? {
        return rutinasFirestoresDataSourceImpl.obtenerRutinaParaEditar(userId, nombreRutina)
    }

    override suspend fun editarRutina(userId: String, nombreRutina: String, rutinaConSesConEjs: RutinaConSesConEjs) {
        return rutinasFirestoresDataSourceImpl.editarRutina(userId,nombreRutina,rutinaConSesConEjs)
    }

    override suspend fun eliminarRutinaSeleccionada(userId: String, nombre: String) {
        return rutinasFirestoresDataSourceImpl.eliminarRutinaSeleccionada(userId, nombre)
    }


}