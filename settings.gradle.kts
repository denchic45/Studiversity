pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven ("https://androidx.dev/storage/compose-compiler/repository/")
//        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
plugins {
    id ("org.jetbrains.kotlin.android") version "1.7.10"
}
}

rootProject.name = "KtsApp"

include (":app")
rootProject.name = "KTS"
include (":appbarcontroller")
include(":common")
include(":desktop")
