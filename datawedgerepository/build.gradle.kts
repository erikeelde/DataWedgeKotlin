plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android1810)
    kotlin("plugin.serialization") version "1.9.20"
}

android {
    namespace = "com.example.datawedgerepository"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(libs.androidx.core.core.ktx)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.datetime)
    implementation(libs.com.squareup.logcat)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
}