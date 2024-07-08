package com.example.tfg01.domain.usecases.usuario

import com.example.tfg01.data.model.Usuario
import com.example.tfg01.domain.repository.UsuarioRepository
import javax.inject.Inject

// Caso de uso: insertar usuario
class InsertarUsuario @Inject constructor(private val usuarioRepository: UsuarioRepository) {

    suspend operator fun invoke(usuario: Usuario) {
        usuarioRepository.insertarUsuario(usuario)
    }
}