// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.com.android.application) apply(false)
    alias(libs.plugins.org.jetbrains.kotlin.android1810) apply(false)
    alias(libs.plugins.com.android.library) apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.3"
}
