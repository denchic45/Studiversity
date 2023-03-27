// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    kotlin("plugin.serialization") version "1.7.20"
}

buildscript {
    val kotlinVersion = "1.7.20"
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
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.4.2")
        classpath("com.android.tools.build:gradle:7.3.1")
//        classpath("com.google.gms:google-services:4.3.13")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.squareup.sqldelight:gradle-plugin:1.5.3")

        classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.7.20-1.0.7")
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