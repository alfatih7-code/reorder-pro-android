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
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")

    implementation("com.google.android.material:material:1.11.0")

    implementation("androidx.activity:activity:1.8.2")

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("androidx.documentfile:documentfile:1.0.1")

}