package com.example.tfg01.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//proovera instancia de FirebaseFirestore a todos los metodos backend que la soliciten
@Module
@InstallIn(SingletonComponent::class)
object GeneralModule {

    @Provides
    @Singleton
    fun provideFirestoreInstance(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }


}