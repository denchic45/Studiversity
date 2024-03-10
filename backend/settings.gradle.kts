pluginManagement {
    val kotlinVersion: String by settings
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
    }
}

rootProject.name = "backend"

include(":common:api")
project(":common:api").projectDir = file("../common/api")