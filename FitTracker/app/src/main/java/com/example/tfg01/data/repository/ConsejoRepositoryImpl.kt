package com.example.tfg01.data.repository

import com.example.tfg01.data.datasource.firestore.ConsejoFirestoreDataSourceImpl
import com.example.tfg01.data.model.Consejo
import com.example.tfg01.domain.repository.ConsejoRepository
import javax.inject.Inject

class ConsejoRepositoryImpl @Inject constructor(private val consejoFirestoreDataSourceImpl: ConsejoFirestoreDataSourceImpl)
    : ConsejoRepository {

    override suspend fun obtenerConsejos(): List<Consejo> {
        return consejoFirestoreDataSourceImpl.obtenerConsejos()
    }

}