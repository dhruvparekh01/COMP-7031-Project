// build.gradle.kts
plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.assign3"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.assign3"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.viewpager2)
    implementation(libs.material.v100)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.foundation.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("androidx.core:core-ktx:1.9.0")
    implementation ("com.google.android.exoplayer:exoplayer:2.19.0")

    // Add OkHttpClient dependency
    implementation(libs.okhttp)
    implementation(libs.lombok)
    annotationProcessor(libs.lombok)
}