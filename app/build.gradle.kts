plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services") // Bu satırı buraya ekleyin
}

android {
    namespace = "com.example.spinboxmusicapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.spinboxmusicapplication"
        minSdk = 24
        //noinspection OldTargetApi
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation ("com.github.bumptech.glide:glide:4.13.2")
    implementation("com.google.firebase:firebase-auth:23.1.0")  // Firebase Authentication
    implementation("com.google.firebase:firebase-firestore:25.1.1")  // Firebase Firestore
    implementation(libs.firebase.database) // Firebase Database
    implementation(libs.gson) // Gson
    implementation(libs.glide) // Glide
    annotationProcessor(libs.glide.compiler)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
