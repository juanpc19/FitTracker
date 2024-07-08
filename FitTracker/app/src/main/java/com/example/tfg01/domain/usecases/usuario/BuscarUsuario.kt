package com.example.tfg01.domain.usecases.usuario

import com.example.tfg01.data.model.Usuario
import com.example.tfg01.domain.repository.UsuarioRepository
import javax.inject.Inject

// Caso de uso: Buscar usuario por ID
class BuscarUsuario @Inject constructor(private val usuarioRepository: UsuarioRepository) {

    suspend operator fun invoke(userId: String): Usuario? {
        return usuarioRepository.buscarUsuario(userId)
    }
}