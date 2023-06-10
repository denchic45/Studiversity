import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("kotlin-kapt")
    id("org.jetbrains.compose") version "1.4.0"

    id("com.google.devtools.ksp")
}

group = "com.denchic45"
version = "1.0"
kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
        configurations.all {
            // some dependencies contains it, this causes an exception to initialize the Main dispatcher in desktop
            exclude("org.jetbrains.kotlinx", "kotlinx-coroutines-android")
        }
        withJava()
        kapt {
            correctErrorTypes = true
        }
    }

    sourceSets {
        val jvmMain by getting {

            kotlin.srcDir("build/generated/ksp/jvm/jvmMain/kotlin")
            dependencies {

                implementation(project(mapOf("path" to ":common")))

                implementation(compose.desktop.currentOs)

                // Kamel
                implementation("com.alialbaali.kamel:kamel-image:0.4.0")
            }
        }
    }
}

dependencies {
    add("kspJvm", "me.tatarka.inject:kotlin-inject-compiler-ksp:0.6.1")
}

compose.desktop {
    application {
        mainClass = "com.denchic45.studiversity.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            modules("java.sql")
            packageName = "jvm"
            packageVersion = "1.0.0"
        }
    }
}