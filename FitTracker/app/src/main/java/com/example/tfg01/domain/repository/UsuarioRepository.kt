package com.example.tfg01.domain.repository

import com.example.tfg01.data.model.Usuario

// Interfaz del repositorio de usuarios del dominio
interface UsuarioRepository {
    suspend fun insertarUsuario(usuario: Usuario)
    suspend fun buscarUsuario(userId: String): Usuario?
}