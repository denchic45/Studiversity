pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://androidx.dev/storage/compose-compiler/repository/")
    }
    plugins {
        id("org.jetbrains.kotlin.android") version "1.7.20"
        id("org.jetbrains.kotlin.jvm") version "1.7.20"
        id("com.android.library") version "7.4.1"
    }
}

rootProject.name = "KtsApp"

include(":android")
include(":appbarcontroller")
include(":common")
include(":desktop")

include(":common-api:api")
project(":common-api:api").projectDir = file("../common-api/api")