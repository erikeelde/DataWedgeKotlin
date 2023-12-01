plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android1810)
    kotlin("plugin.serialization") version "1.9.20"
    id("io.gitlab.arturbosch.detekt") version "1.23.3"
}

android {
    namespace = "com.example.datawedgerepository"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
    }
}

detekt {
    autoCorrect = true
    buildUponDefaultConfig = true
    baseline = project.file("config/detekt/baseline.xml")
    config = project.files("config/detekt/detekt.yml")
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
    implementation(libs.io.github.z4kn4fein.semver)

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.3")
}