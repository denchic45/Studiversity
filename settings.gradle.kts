pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://androidx.dev/storage/compose-compiler/repository/")
    }
    plugins {
        id("org.jetbrains.kotlin.android") version "1.7.10"
        id("org.jetbrains.kotlin.jvm") version "1.7.10"
        id("com.android.library") version "7.3.0"
    }
}

rootProject.name = "KtsApp"

include(":android")
include(":appbarcontroller")
include(":common")
include(":desktop")
