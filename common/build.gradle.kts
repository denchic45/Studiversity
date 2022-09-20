import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.1.1"
    id("com.android.library")
    id("com.squareup.sqldelight")
    id("kotlin-kapt")
    kotlin("plugin.serialization") version "1.6.10"


    id("com.google.devtools.ksp")
//    id("dagger.hilt.android.plugin")
}

kotlin {
    android()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }

//    ksp {
//        arg("me.tatarka.inject.enableJavaxAnnotations", "true")
//    }

    sourceSets {
        val ktorVersion = "2.0.3"
        val koinVersion = "3.2.0"
        val sqlDelightVersion = "1.5.3"
        val decomposeVersion = "1.0.0-alpha-02"
//        val korimVersion = "2.2.0"

        val commonJvmMain by creating {
            dependencies {

                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.4")

                implementation("com.squareup.sqldelight:runtime-jvm:$sqlDelightVersion")
                implementation("com.squareup.sqldelight:coroutines-extensions-jvm:$sqlDelightVersion")

                implementation("com.google.code.gson:gson:2.9.0")

                implementation("net.harawata:appdirs:1.2.1")

                api("org.jetbrains.kotlin:kotlin-reflect:1.7.0")

                //Dagger
                api("com.google.dagger:dagger:2.38.1")
                configurations.getByName("kapt").dependencies.add(project.dependencies.create("com.google.dagger:dagger-compiler:2.38.1"))
//                api("com.google.dagger:hilt-android:2.38.1")



                implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
            }
        }

        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                api(compose.material3)

                // Ktor
                api("io.ktor:ktor-client-core:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                api("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                api("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                api("io.ktor:ktor-client-logging:$ktorVersion")
                api("io.ktor:ktor-client-auth:$ktorVersion")

                // Decompose
                api("com.arkivanov.decompose:decompose:$decomposeVersion")

                api("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

                implementation("com.squareup.sqldelight:runtime:$sqlDelightVersion")
                implementation("com.squareup.sqldelight:coroutines-extensions:$sqlDelightVersion")

                implementation("io.insert-koin:koin-core:$koinVersion")

                // Settings
                api("com.russhwolf:multiplatform-settings:0.9")
                api("com.russhwolf:multiplatform-settings-coroutines:0.9")

                // kotlin-inject
                configurations["ksp"].dependencies.add(project.dependencies.create("me.tatarka.inject:kotlin-inject-compiler-ksp:0.5.1"))
                implementation("me.tatarka.inject:kotlin-inject-runtime:0.5.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(kotlin("test-junit"))
            }
        }
        val androidMain by getting {
            dependencies {
                dependsOn(commonJvmMain)

                api("androidx.appcompat:appcompat:1.4.2")
                api("androidx.core:core-ktx:1.8.0")

                api("io.ktor:ktor-client-android:$ktorVersion")
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
                implementation("com.squareup.sqldelight:android-driver:$sqlDelightVersion")

                api("com.squareup.retrofit2:retrofit:2.9.0")

                // Dagger
                api("com.google.dagger:dagger-android:2.38.1")
                api("com.google.dagger:dagger-android-support:2.38.1")

                // Navigation
                api("androidx.navigation:navigation-fragment-ktx:2.5.1")
                api("androidx.navigation:navigation-ui-ktx:2.5.1")

                // Firebase SDK
                api(project.dependencies.platform("com.google.firebase:firebase-bom:30.2.0"))
                api("com.google.firebase:firebase-firestore") {
                    exclude("com.squareup.okhttp")
                }
                api("com.google.firebase:firebase-storage")
                api("com.google.firebase:firebase-auth")
                api("com.google.firebase:firebase-analytics")

                implementation("io.grpc:grpc-okhttp:1.44.1") {
                    exclude("com.squareup.okhttp")
                }

                api("com.google.android.play:core:1.10.3")
                api("com.google.android.play:core-ktx:1.8.1")

                api("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
        val desktopMain by getting {
            kotlin.srcDir("build/generated/ksp/desktop/desktopMain/kotlin")
            dependencies {
                api(compose.desktop.currentOs)

                api(compose.preview)

                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")

                api("com.squareup.sqldelight:sqlite-driver:$sqlDelightVersion")
                api("com.squareup.sqldelight:coroutines-extensions-jvm:$sqlDelightVersion")

                // Dagger
                api("com.google.dagger:dagger:2.38.1")
                configurations.getByName("kapt").dependencies.add(project.dependencies.create("com.google.dagger:dagger-compiler:2.38.1"))

                // Decompose
                api("com.arkivanov.decompose:extensions-compose-jetbrains:$decomposeVersion")

                // KorIM
//                api("com.soywiz.korlibs.korim:korim-jvm:$korimVersion")

                dependsOn(commonJvmMain)
            }
        }
        val desktopTest by getting {
            dependencies {


            }
        }
    }
}

android {
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
        targetSdk = 33
        buildFeatures {
            viewBinding = true
            compose = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    sourceSets.all {
        kotlin.srcDir("src/$name/kotlin")
    }
    sourceSets["main"].res.srcDirs("src/androidMain/res", "src/commonMain/resources")
    namespace = "com.denchic45.common"
    kapt {
        correctErrorTypes = true
        }
}
dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

sqldelight {
    database("AppDatabase") {
        packageName = "com.denchic45.kts"
        sourceFolders = listOf("sqldelight")
        dialect = "sqlite:3.25"
    }
}