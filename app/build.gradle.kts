// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    kotlin("plugin.serialization") version "1.9.22"
}

buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.1.0")
        classpath("app.cash.sqldelight:gradle-plugin:2.0.0-alpha05")
        classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.9.22-1.0.17")
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

        maven("https://packages.jetbrains.team/maven/p/kpm/public/")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}