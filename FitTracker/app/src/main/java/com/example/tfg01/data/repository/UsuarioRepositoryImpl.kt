package com.example.tfg01.data.repository

import com.example.tfg01.data.datasource.firestore.UsuarioFirestoreDataSourceImpl
import com.example.tfg01.data.model.Usuario
import com.example.tfg01.domain.repository.UsuarioRepository
import javax.inject.Inject

//inyectar dependencia para instancia
// Implementación del repositorio de usuarios
class UsuarioRepositoryImpl @Inject constructor(private val usuarioFirestoreDataSourceImpl: UsuarioFirestoreDataSourceImpl)
    : UsuarioRepository {

    //metodo que insertara los datos de un usuario mediante firebase
    override suspend fun insertarUsuario(usuario: Usuario) {
        usuarioFirestoreDataSourceImpl.insertarUsuario(usuario)
    }

    override suspend fun buscarUsuario(userId: String): Usuario? {
        return usuarioFirestoreDataSourceImpl.buscarUsuario(userId)
    }
}