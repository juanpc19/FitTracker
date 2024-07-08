package com.example.tfg01.di

import com.example.tfg01.data.datasource.firestore.SesionFirestoreDataSourceImpl
import com.example.tfg01.data.repository.SesionRepositoryImpl
import com.example.tfg01.domain.repository.SesionRepository
import com.example.tfg01.domain.usecases.sesion.BuscarSesionesRutinaActiva
import com.example.tfg01.domain.usecases.sesion.ObtenerSesionSeleccionada
import com.example.tfg01.domain.usecases.sesion.SesionUseCases
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//proovera dependencias de repositorios y use cases, para metodos backend y vms que usen modelos de sesion
@Module
@InstallIn(SingletonComponent::class)
object SesionModule {

    @Provides
    @Singleton
    fun provideSesionFireStoreDataSource(firestore: FirebaseFirestore): SesionFirestoreDataSourceImpl {
        return SesionFirestoreDataSourceImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideSesionFirestoreRepository(sesionFirestoreDataSourceImpl: SesionFirestoreDataSourceImpl): SesionRepository {
        return SesionRepositoryImpl(sesionFirestoreDataSourceImpl)
    }

    @Provides
    @Singleton
    fun providesSesionUseCases(sesionRepository: SesionRepository): SesionUseCases {
        return SesionUseCases(
            buscarSesionesRutinaActiva = BuscarSesionesRutinaActiva(sesionRepository),
            obtenerSesionSeleccionada = ObtenerSesionSeleccionada(sesionRepository)
        )
    }
}