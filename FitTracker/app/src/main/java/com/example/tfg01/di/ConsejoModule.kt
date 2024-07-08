package com.example.tfg01.di

import com.example.tfg01.data.datasource.firestore.ConsejoFirestoreDataSourceImpl
import com.example.tfg01.data.repository.ConsejoRepositoryImpl
import com.example.tfg01.domain.repository.ConsejoRepository
import com.example.tfg01.domain.usecases.consejo.ConsejoUseCases
import com.example.tfg01.domain.usecases.consejo.ObtenerConsejos
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


//proovera dependencias de repositorios y use cases, para metodos backend y vms que usen modelos de consejo
@Module
@InstallIn(SingletonComponent::class)
object ConsejoModule {

    @Provides
    @Singleton
    fun provideConsejoFirestoreDataSource(firestore: FirebaseFirestore): ConsejoFirestoreDataSourceImpl {
        return ConsejoFirestoreDataSourceImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideConsejoRepository(consejoFirestoreDataSourceImpl: ConsejoFirestoreDataSourceImpl): ConsejoRepository {
        return ConsejoRepositoryImpl(consejoFirestoreDataSourceImpl)
    }

    @Provides
    @Singleton
    fun provideConsejoUseCases(consejoRepository: ConsejoRepository): ConsejoUseCases {
        return ConsejoUseCases(
            obtenerConsejos = ObtenerConsejos(consejoRepository)
        )
    }

}