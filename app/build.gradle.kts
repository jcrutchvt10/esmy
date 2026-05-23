plugins {
    id("com.android.application") version "8.2.0"
    id("org.jetbrains.kotlin.android") version "1.9.22"
    kotlin("kapt") version "1.9.22"
    // Note: comment out the original lines to avoid duplication
    // id("com.android.application")
    // id("org.jetbrains.kotlin.android")
    // kotlin("kapt")

}

android {
    compileSdk = 34
    namespace = "com.example.enchantedandroid"

    defaultConfig {
        applicationId = "com.example.enchantedandroid"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kapt {
    correctErrorTypes = true
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2023.10.01")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    // Room persistence
    implementation("androidx.room:room-runtime:2.5.2")
    kapt("androidx.room:room-compiler:2.5.2")
    implementation("androidx.room:room-ktx:2.5.2")
    // DataStore for simple preferences (settings)
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    // Compose runtime LiveData
    implementation("androidx.compose.runtime:runtime-livedata")
    // Icons (optional)
    implementation("androidx.compose.material:material-icons-extended:1.5.1")
    // Image loading (optional)
    implementation("io.coil-kt:coil-compose:2.5.0")
    // Markdown rendering
    implementation("com.halilibo.compose-richtext:richtext-markdown-android:1.0.0-alpha02")
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")
}
