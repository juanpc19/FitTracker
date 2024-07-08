package com.example.tfg01.domain.repository

import com.example.tfg01.data.model.Consejo

interface ConsejoRepository {
    suspend fun obtenerConsejos(): List<Consejo>
}