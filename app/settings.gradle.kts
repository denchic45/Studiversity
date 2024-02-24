pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://androidx.dev/storage/compose-compiler/repository/")
    }
    plugins {
        id("org.jetbrains.kotlin.android") version "1.9.22"
        id("org.jetbrains.kotlin.jvm") version "1.9.22"
        id("com.android.library") version "7.4.1"
    }
}

rootProject.name = "app"

include(":android")
include(":common")
include(":desktop")

include(":common:api")
project(":common:api").projectDir = file("../common/api")

includeBuild("common/libs/androidx-compose-material3-pullrefresh") {
    dependencySubstitution {
        substitute(module("me.omico.lux:lux-androidx-compose-material3-pullrefresh")).using(project(":library"))
    }
}