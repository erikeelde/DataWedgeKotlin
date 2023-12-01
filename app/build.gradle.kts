plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android1810)
    kotlin("plugin.serialization") version "1.9.20"
}

android {
    namespace = "com.darryncampbell.datawedgekotlin"
    compileSdk = 34

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
    }
    defaultConfig {
        applicationId = "com.darryncampbell.datawedgekotlin"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        debug {
//            applicationIdSuffix = ".debug"
        }
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(projects.datawedgerepository)

    implementation(libs.androidx.annotation)
    implementation(libs.androidx.appcompat.v161)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.com.google.android.material)
    implementation(libs.androidx.coordinatorlayout)
    implementation(libs.androidx.core.core.ktx)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
    implementation(platform(libs.androidx.compose.compose.bom))
    implementation(libs.androidx.core.core.ktx)
    implementation(libs.com.squareup.logcat)
    implementation(libs.com.squareup.okio)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.datetime)
    implementation(libs.androidx.lifecycle.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.ui.graphics)
    implementation(libs.androidx.navigation.navigation.compose)
    implementation(libs.androidx.compose.ui.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.se.eelde.toggles.toggles.flow)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.compose.bom))
    androidTestImplementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.androidx.test.espresso.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.ui.test.manifest)
}
