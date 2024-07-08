package com.example.tfg01.di

import com.example.tfg01.data.datasource.firestore.RutinaFirestoreDataSourceImpl
import com.example.tfg01.data.repository.RutinaRepositoryImpl
import com.example.tfg01.domain.repository.RutinaRepository
import com.example.tfg01.domain.usecases.rutina.ActivarRutina
import com.example.tfg01.domain.usecases.rutina.BuscarRutinaActivaUsuario
import com.example.tfg01.domain.usecases.rutina.BuscarRutinasUsuario
import com.example.tfg01.domain.usecases.rutina.ComprobarExistenciaRutina
import com.example.tfg01.domain.usecases.rutina.CrearRutina
import com.example.tfg01.domain.usecases.rutina.EditarRutina
import com.example.tfg01.domain.usecases.rutina.EliminarRutinaSeleccionada
import com.example.tfg01.domain.usecases.rutina.ObtenerRutinaParaEditar
import com.example.tfg01.domain.usecases.rutina.ObtenerRutinaSeleccionada
import com.example.tfg01.domain.usecases.rutina.RutinaUseCases
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//proovera dependencias de repositorios y use cases, para metodos backend y vms que usen modelos de rutina
@Module
@InstallIn(SingletonComponent::class)
object RutinaModule {

    @Provides
    @Singleton
    fun provideRutinaFirestoreDataSource(firestore: FirebaseFirestore): RutinaFirestoreDataSourceImpl {
        return RutinaFirestoreDataSourceImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideRutinaFirestoreRepository(rutinaFirestoreDataSourceImpl: RutinaFirestoreDataSourceImpl): RutinaRepository {
        return RutinaRepositoryImpl(rutinaFirestoreDataSourceImpl)
    }

    @Provides
    @Singleton
    fun provideRutinaUseCases(rutinaRepository: RutinaRepository): RutinaUseCases {
        return RutinaUseCases(
            buscarRutinaActivaUsuario = BuscarRutinaActivaUsuario(rutinaRepository),
            buscarRutinasUsuario = BuscarRutinasUsuario(rutinaRepository),
            obtenerRutinaSeleccionada = ObtenerRutinaSeleccionada(rutinaRepository),
            activarRutina = ActivarRutina(rutinaRepository),
            comprobarExistenciaRutina = ComprobarExistenciaRutina(rutinaRepository),
            crearRutina = CrearRutina(rutinaRepository),
            obtenerRutinaParaEditar = ObtenerRutinaParaEditar(rutinaRepository),
            editarRutina = EditarRutina(rutinaRepository),
            eliminarRutinaSeleccionada = EliminarRutinaSeleccionada(rutinaRepository)
        )
    }
}