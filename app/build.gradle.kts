import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.technews"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.technews"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(FileInputStream(localPropertiesFile))
        }

        val newsApiKey = localProperties.getProperty("newsAPI") ?: ""
        val bardApiKey = localProperties.getProperty("bardAPI") ?: ""
        val newApiKey2 = localProperties.getProperty("newAPI2") ?: ""

        // Add build config fields
        buildConfigField("String", "NEWS_API_KEY", "\"$newsApiKey\"")
        buildConfigField("String", "BARD_API_KEY", "\"$bardApiKey\"")
        buildConfigField("String", "NEWS_API_KEY_2", "\"$newApiKey2\"")
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

    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    implementation(libs.generativeai)
    implementation(libs.blurview)
    implementation(libs.blurry)
    implementation(libs.guava)
    implementation(libs.jsoup)
    implementation(libs.glide)
    implementation(libs.lottie)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.reactive.streams)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}