plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    kotlin("plugin.serialization") version "1.9.20"
//    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.biblio"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.biblio"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
            isDebuggable = true

            resValue("string", "app_name", "Biblio (DEBUG)")
            buildConfigField("Boolean", "IS_DEBUG", "true")
        }

        release {
            resValue("string", "app_name", "Biblio")
            buildConfigField("Boolean", "IS_DEBUG", "false")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        android.buildFeatures.buildConfig = true
    }
}

dependencies {
    // ===== CORE ANDROID =====
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Image Loading
    implementation("io.coil-kt:coil-compose:2.5.0")

    implementation("com.google.accompanist:accompanist-systemuicontroller:0.34.0")

    // ===== COMPOSE BOM (Bill of Materials) - Otomatis atur versi =====
    val composeBom = platform("androidx.compose:compose-bom:2024.10.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // ===== COMPOSE UI =====
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // ===== MATERIAL DESIGN =====
    // PILIH SALAH SATU:
    // Option 1: Material 3 (Recommended untuk app baru)
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Option 2: Material 2 (Jika butuh komponen lama)
    // implementation("androidx.compose.material:material")

    // Material Components (untuk View-based, bukan Compose)
    implementation("com.google.android.material:material:1.13.0")

    // ===== COMPOSE ADDITIONAL =====
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.navigation:navigation-compose:2.8.4")

    // Adaptive layouts (untuk tablet/desktop)
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite")

    // ===== LIFECYCLE =====
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")

    // ===== FRAGMENT (Jika pakai Fragment + Compose hybrid) =====
    implementation("androidx.fragment:fragment-ktx:1.8.5")

    // ===== SPLASH SCREEN =====
    implementation("androidx.core:core-splashscreen:1.0.1")

    // ===== TESTING =====
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:34.5.0"))


    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")

    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries
}