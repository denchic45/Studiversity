pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://androidx.dev/storage/compose-compiler/repository/")
    }
    plugins {
        id("org.jetbrains.kotlin.android") version "1.8.0"
        id("org.jetbrains.kotlin.jvm") version "1.8.0"
        id("com.android.library") version "7.4.1"
    }
}

rootProject.name = "Studiversity"

include(":android")
include(":appbarcontroller")
include(":common")
include(":desktop")

include(":common:api")
project(":common:api").projectDir = file("../common/api")