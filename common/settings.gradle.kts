pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://androidx.dev/storage/compose-compiler/repository/")
    }
    plugins {
        id("com.android.library") version "7.4.1"
    }
}

rootProject.name = "common"
include("api")
