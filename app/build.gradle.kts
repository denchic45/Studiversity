// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    kotlin("plugin.serialization") version "1.8.0"
}

buildscript {
    val kotlinVersion = "1.8.0"
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven(url = "https://dl.bintray.com/kotlin/kotlinx/")
        maven(url = "https://kotlin.bintray.com/kotlin-eap")
        maven(url = "https://kotlin.bintray.com/kotlin-dev")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")
        classpath("com.android.tools.build:gradle:8.0.0")
//        classpath("com.google.gms:google-services:4.3.13")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("app.cash.sqldelight:gradle-plugin:2.0.0-alpha05")

        classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.8.0-1.0.8")
//        classpath("com.google.dagger:hilt-android-gradle-plugin:2.38.1")
    }
}

group = "com.denchic45.kts"
version = "1.0"

allprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://jitpack.io")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}