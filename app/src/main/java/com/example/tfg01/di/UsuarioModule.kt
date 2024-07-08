package com.example.tfg01.di

import com.example.tfg01.data.datasource.firestore.UsuarioFirestoreDataSourceImpl
import com.example.tfg01.data.repository.UsuarioRepositoryImpl
import com.example.tfg01.domain.repository.UsuarioRepository
import com.example.tfg01.domain.usecases.usuario.BuscarUsuario
import com.example.tfg01.domain.usecases.usuario.InsertarUsuario
import com.example.tfg01.domain.usecases.usuario.UsuarioUseCases
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//proovera dependencias de repositorios y use cases, para metodos backend y vms que usen modelos de usuario
@Module
@InstallIn(SingletonComponent::class)
//este objeto se encargara de indicar a dagger hilt que componente inyectar y donde inyectarlo, son singleton
object UsuarioModule {

    @Provides
    @Singleton
    fun provideUsuarioFirestoreDataSource(firestore: FirebaseFirestore): UsuarioFirestoreDataSourceImpl {
        return UsuarioFirestoreDataSourceImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideUsuarioRepository(usuarioFirestoreDataSourceImpl: UsuarioFirestoreDataSourceImpl): UsuarioRepository {
        return UsuarioRepositoryImpl(usuarioFirestoreDataSourceImpl)
    }

    @Provides
    @Singleton
    fun provideUsuarioUseCases(usuarioRepository: UsuarioRepository): UsuarioUseCases {
        return UsuarioUseCases(
            insertarUsuario = InsertarUsuario(usuarioRepository),
            buscarUsuario = BuscarUsuario(usuarioRepository),
        )
    }

}




