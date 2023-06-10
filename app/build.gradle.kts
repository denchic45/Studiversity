// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    kotlin("plugin.serialization") version "1.8.20"
}

buildscript {
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
        classpath("com.android.tools.build:gradle:8.0.2")
        classpath("app.cash.sqldelight:gradle-plugin:2.0.0-alpha05")
        classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.8.20-1.0.11")
    }
}

group = "com.denchic45.studiversity"
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