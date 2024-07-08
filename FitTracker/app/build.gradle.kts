plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    //para usar servicios de google relacionados con firestore
    id("com.google.gms.google-services")
    //
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    signingConfigs {
        create("release") {
            storeFile = file("D:\\CosasFormateo\\CosasGrado2\\malditokeystore\\keystore.jks")
            storePassword = "murcielagolp2019"
            keyPassword = "murcielagolp2019"
            keyAlias = "FitTrackerGold"
        }
    }
    namespace = "com.example.tfg01"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tfg01"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isDebuggable = false
            signingConfig = signingConfigs.getByName("release")


        }
        debug {
            isDebuggable = true
            // Aquí puedes agregar cualquier otra configuración específica para el tipo de compilación debug si es necesario
        }
        //para debuggear mejor puede dar conflictos al hacer el apk
//        kotlinOptions {
//            freeCompilerArgs = listOf("-Xdebug")
//        }
//        getByName("release") {
//        }
//        create("customDebugType") {
//            isDebuggable = true//para poder debuggear?
//        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.firestore.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // permite el uso de view models y la extraccion de datos de los mismos en compose
    // mantiene los datos en rotacion de pantalla (como by rememberSaveable),
    // ademas de proporcionar funciones relacionadas con ciclos de vida de la app
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    //amplia las herramientas de manipulacion de ciclos de vida y permite su uso en compose
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    //para navegacion compose
    val navVersion = "2.7.7"
    implementation("androidx.navigation:navigation-compose:$navVersion")

    //para uso de firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation("com.google.firebase:firebase-analytics")

    //para uso de imagenes asincronas
    implementation("io.coil-kt:coil-compose:2.6.0")

    //para comprobar leaks de memoria en ejecucion
    //debugImplementation("com.squareup.leakcanary:leakcanary-android:2.9.1")

    //para serializar y deserializar datos en formato JSON en Kotlin (sin usar)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

    //para reproducir video con exo player
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.3.1")
    implementation("androidx.media3:media3-ui:1.3.1")

    //para inyeccion de dependencias con dagger hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
}

//para inyeccion de dependencias con dagger hilt
kapt {
    correctErrorTypes = true
}