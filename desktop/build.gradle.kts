import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("kotlin-kapt")
    id("org.jetbrains.compose") version "1.2.0"

    id("com.google.devtools.ksp")
}

group = "com.denchic45"
version = "1.0"
kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
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
                // Dagger
                val daggerVersion = "2.44"
                implementation("com.google.dagger:dagger:$daggerVersion")
                configurations.getByName("kapt").dependencies.add(project.dependencies.create("com.google.dagger:dagger-compiler:$daggerVersion"))

                // kotlin-inject
                implementation("me.tatarka.inject:kotlin-inject-runtime:0.5.1")
                configurations["ksp"].dependencies.add(project.dependencies.create("me.tatarka.inject:kotlin-inject-compiler-ksp:0.5.1"))

                // Kamel
                implementation("com.alialbaali.kamel:kamel-image:0.4.0")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            modules("java.sql")
            packageName = "jvm"
            packageVersion = "1.0.0"
        }
    }
}