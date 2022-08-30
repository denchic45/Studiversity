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
        id("com.android.library") version "7.2.2"
    }
}

rootProject.name = "KtsApp"

include(":app")
rootProject.name = "KTS"
include(":appbarcontroller")
include(":common")
include(":desktop")
