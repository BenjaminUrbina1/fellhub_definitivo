plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services) // <--- Esto activa lo que definiste en el TOML
}

android {
    namespace = "com.dev.fellpulse_hub"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dev.fellpulse_hub"
        // Se sube la versión mínima para soportar iconos adaptativos y funciones modernas
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    lint {
        abortOnError = false
    }
}

dependencies {
    implementation(platform(libs.firebase.bom)) // <--- Importante para que funcione el BOM
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}
