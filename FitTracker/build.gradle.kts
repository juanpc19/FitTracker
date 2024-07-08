buildscript {
    dependencies {
        classpath(libs.google.services)
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false

    //para inyeccion de dependencias con dagger hilt
    id("com.google.dagger.hilt.android") version "2.51.1" apply false

    //para usar servicios de google relacionados con firestore
    id("com.google.gms.google-services") version "4.4.1" apply false
}