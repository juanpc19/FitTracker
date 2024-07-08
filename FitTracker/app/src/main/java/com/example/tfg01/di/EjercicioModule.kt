package com.example.tfg01.di

import com.example.tfg01.data.datasource.firestore.EjercicioFirestoreDataSourceImpl
import com.example.tfg01.data.repository.EjercicioRepositoryImpl
import com.example.tfg01.domain.repository.EjercicioRepository
import com.example.tfg01.domain.usecases.ejercicio.ComprobarExistenciaEjercicio
import com.example.tfg01.domain.usecases.ejercicio.CrearEjercicio
import com.example.tfg01.domain.usecases.ejercicio.EditarEjercicio
import com.example.tfg01.domain.usecases.ejercicio.EjercicioUseCases
import com.example.tfg01.domain.usecases.ejercicio.EliminarEjercicio
import com.example.tfg01.domain.usecases.ejercicio.ObtenerEjConjuntosUsuario
import com.example.tfg01.domain.usecases.ejercicio.ObtenerEjPersonalizadosUsuario
import com.example.tfg01.domain.usecases.ejercicio.ObtenerEjPorSesionPorRutina
import com.example.tfg01.domain.usecases.ejercicio.ObtenerPlantillaEjercicios
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//proovera dependencias de repositorios y use cases, para metodos backend y vms que usen modelos de ejercicio
@Module
@InstallIn(SingletonComponent::class)
object EjercicioModule {

    @Provides
    @Singleton
    fun provideEjercicioFirestoreDataSource(firestore: FirebaseFirestore): EjercicioFirestoreDataSourceImpl {
        return EjercicioFirestoreDataSourceImpl((firestore))
    }

    @Provides
    @Singleton
    fun provideEjercicioFirestoreRepository(ejercicioFirestoreDataSourceImpl: EjercicioFirestoreDataSourceImpl): EjercicioRepository {
        return EjercicioRepositoryImpl(ejercicioFirestoreDataSourceImpl)
    }

    @Provides
    @Singleton
    fun provideEjercicioUseCases(ejercicioRepository: EjercicioRepository): EjercicioUseCases {
        return EjercicioUseCases(
            obtenerPlantillaEjercicios = ObtenerPlantillaEjercicios(ejercicioRepository),
            obtenerEjPorSesionPorRutina = ObtenerEjPorSesionPorRutina(ejercicioRepository),
            obtenerEjPersonalizadosUsuario = ObtenerEjPersonalizadosUsuario(ejercicioRepository),
            obtenerEjConjuntosUsuario = ObtenerEjConjuntosUsuario(ejercicioRepository),
            comprobarExistenciaEjercicio = ComprobarExistenciaEjercicio(ejercicioRepository),
            crearEjercicio = CrearEjercicio(ejercicioRepository),
            editarEjercicio = EditarEjercicio(ejercicioRepository),
            eliminarEjercicio = EliminarEjercicio(ejercicioRepository)
        )
    }
}