// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
}

buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.2.2")
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