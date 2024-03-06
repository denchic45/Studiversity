pluginManagement {
    val kotlinVersion: String by settings
    val jetbrainsCompose: String by settings

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://androidx.dev/storage/compose-compiler/repository/")
    }

    plugins {
        kotlin("android") version kotlinVersion
        kotlin("jvm") version kotlinVersion
//        kotlin("multiplatform") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        id("org.jetbrains.compose") version jetbrainsCompose

        id("com.android.library") version "7.4.1"
    }
}

rootProject.name = "app"

include(":android")
include(":common")
include(":desktop")

include(":common:api")
project(":common:api").projectDir = file("../common/api")