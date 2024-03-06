import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp")
}

group = "com.denchic45.studiversity"

kotlin {
    jvmToolchain(17)
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
        configurations.all {
            // some dependencies contains it, this causes an exception to initialize the Main dispatcher in desktop
            exclude("org.jetbrains.kotlinx", "kotlinx-coroutines-android")
        }
        withJava()
    }

    sourceSets {
        val jvmMain by getting {
            kotlin.srcDir("build/generated/ksp/jvm/jvmMain/kotlin")
            dependencies {
                implementation(project(":common"))
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.denchic45.studiversity.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            modules("java.sql")
            packageName = "Studiversity"
            packageVersion = "1.0.0"

            macOS {
                iconFile.set(project.file("app_logo.icns"))
            }
            windows {
                iconFile.set(project.file("app_logo.ico"))
            }
            linux {
                iconFile.set(project.file("app_logo.png"))
            }
        }
    }
}