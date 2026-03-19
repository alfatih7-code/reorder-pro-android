plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.reorderpro"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.reorderpro"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // UI Core
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // SplashScreen (اتركها هنا ولكن لا تناديها في الكود حالياً)
    implementation("androidx.core:core-splashscreen:1.0.1")

    // --- ⬇️ Room Database (معدلة لتعمل بـ Java) ⬇️ ---
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    // إضافة لدعم LiveData مع Room
    implementation("androidx.room:room-ktx:$room_version")

    // --- ⬇️ Lifecycle (ViewModel & LiveData) ⬇️ ---
    val lifecycle_version = "2.7.0"
    implementation("androidx.lifecycle:lifecycle-viewmodel:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-runtime:$lifecycle_version")

    // WorkManager
    implementation("androidx.work:work-runtime:2.9.0")
}