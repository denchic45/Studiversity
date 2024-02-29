plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.5.12"
    id("com.android.library")
    kotlin("plugin.serialization") version "1.9.22"
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("app.cash.sqldelight")
}

val sqlDelightVersion = "2.0.0-alpha05"
kotlin {
    androidTarget()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
    }

    sourceSets {
        val ktorVersion = "2.3.1"
        val koinVersion = "3.2.0"
        val decomposeVersion = "2.0.0"

        val commonMain by getting {
            dependencies {
                // Compose
                api(compose.runtime)
                api(compose.foundation)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                api(compose.material3)
                api(compose.materialIconsExtended)

                implementation(project(":common:api"))

                // Compose
                implementation("org.burnoutcrew.composereorderable:reorderable:0.9.6")
                implementation("ca.gosyer:compose-material-dialogs-datetime:0.9.2")
                implementation("io.github.qdsfdhvh:image-loader:1.3.1")
                implementation("androidx.compose.material3:material3-window-size-class:1.1.1")
                implementation("androidx.window:window:1.1.0")

                // Ktor
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("io.ktor:ktor-client-auth:$ktorVersion")

                // Decompose
                api("com.arkivanov.decompose:decompose:$decomposeVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

                implementation("app.cash.sqldelight:runtime:$sqlDelightVersion")
                implementation("app.cash.sqldelight:coroutines-extensions:$sqlDelightVersion")
                implementation("app.cash.sqldelight:primitive-adapters:2.0.0-alpha05")

                implementation("io.insert-koin:koin-core:$koinVersion")

                // Settings
                implementation("com.russhwolf:multiplatform-settings:1.0.0")
                implementation("com.russhwolf:multiplatform-settings-coroutines:1.0.0")

                implementation("com.squareup.okio:okio:3.3.0")

                implementation("me.tatarka.inject:kotlin-inject-runtime:0.6.1")

                implementation("com.darkrockstudios:mpfilepicker:1.1.0")

                // kotlin-result
                implementation("com.michael-bull.kotlin-result:kotlin-result:1.1.17")
                implementation("com.michael-bull.kotlin-result:kotlin-result-coroutines:1.1.17")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
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
        val commonJvmMain by creating {
            dependencies {
                dependsOn(commonMain)

                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.1")

                implementation("app.cash.sqldelight:runtime-jvm:$sqlDelightVersion")

                // Ktor
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")

                implementation("net.harawata:appdirs:1.2.1")

                api("org.jetbrains.kotlin:kotlin-reflect:1.9.22")
                implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
            }
        }
        val commonJvmTest by creating {
            dependencies {
                dependsOn(commonTest)
//                implementation("com.github.skebir:prettytable:v1.0")
            }
        }
        val androidMain by getting {
            dependencies {
                dependsOn(commonJvmMain)
                implementation("androidx.core:core-ktx:1.10.1")

                implementation("com.kizitonwose.calendar:compose:2.3.0")

                implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")

                implementation("com.arkivanov.decompose:extensions-android:$decomposeVersion")

                implementation("io.ktor:ktor-client-android:$ktorVersion")
                implementation("app.cash.sqldelight:android-driver:$sqlDelightVersion")

                implementation("androidx.work:work-runtime-ktx:2.8.1")

                // Compose
                val jetpackComposeVersion = "1.6.2"
                api("androidx.compose.runtime:runtime:$jetpackComposeVersion")
                implementation("androidx.compose.ui:ui:$jetpackComposeVersion")
                implementation("androidx.activity:activity-compose:1.7.2")
                // Tooling support (Previews, etc.)
                implementation("androidx.compose.ui:ui-tooling:$jetpackComposeVersion")
                // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
                implementation("androidx.compose.foundation:foundation:$jetpackComposeVersion")
                // Material Design
                implementation("androidx.compose.material3:material3:1.1.1")
                // Lifecycle
                implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")

                implementation("com.google.accompanist:accompanist-systemuicontroller:0.30.1")
                implementation("com.airbnb.android:lottie-compose:6.0.0")
                implementation("androidx.core:core-splashscreen:1.0.1")

                // Decompose
                implementation("com.arkivanov.decompose:extensions-compose-jetpack:$decomposeVersion")
                // Cropper
                implementation("com.github.SmartToolFactory:Compose-Cropper:0.4.0")
                // Coil
                implementation("io.coil-kt:coil-compose:2.2.2")
                implementation("io.coil-kt:coil-svg:2.3.0")

                implementation("androidx.recyclerview:recyclerview:1.3.0")

                implementation("com.google.android.play:core:1.10.3")
                implementation("com.google.android.play:core-ktx:1.8.1")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.1")
                // Flow layout
                implementation("com.google.accompanist:accompanist-flowlayout:0.27.0")

                implementation("me.omico.lux:lux-androidx-compose-material3-pullrefresh")
            }
        }
        val androidUnitTest by getting {
            dependencies {
                dependsOn(commonJvmTest)
                implementation("junit:junit:4.13.2")
            }
        }
        val desktopMain by getting {
            kotlin.srcDir("build/generated/ksp/desktop/desktopMain/kotlin")
            dependencies {
                api(compose.desktop.currentOs)
                api(compose.preview)

                implementation("org.jetbrains.jewel:jewel-int-ui-decorated-window:0.15.0")

                implementation("app.cash.sqldelight:sqlite-driver:$sqlDelightVersion")

                // Decompose
                api("com.arkivanov.decompose:extensions-compose-jetbrains:$decomposeVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.1")
                
                implementation("media.kamel:kamel-image:0.7.0")

                implementation("com.github.Dansoftowner:jSystemThemeDetector:3.6")

                dependsOn(commonJvmMain)
            }
        }
        val desktopTest by getting {
            dependencies {
                dependsOn(commonJvmTest)
            }
        }
    }
}

dependencies {
    // kotlin-inject
    add("kspAndroid", "me.tatarka.inject:kotlin-inject-compiler-ksp:0.6.1")
    add("kspDesktop", "me.tatarka.inject:kotlin-inject-compiler-ksp:0.6.1")
    // support new language API
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")
}

android {
    namespace = "com.denchic45.studiversity.common"
    compileSdk = 34
    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("src/commonMain/resources", "src/androidMain/res")
    }
    defaultConfig {
        minSdk = 24
        buildFeatures {
            viewBinding = true
            compose = true
        }
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    packaging {
        resources.excludes.apply {
            add("META-INF/LICENSE")
            add("META-INF/*.properties")
            add("META-INF/AL2.0")
            add("META-INF/LGPL2.1")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    sourceSets.all {
        kotlin.srcDir("src/$name/kotlin")
        java.srcDir("src/$name/java")
    }
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.denchic45.studiversity.entity")
            sourceFolders.set(listOf("sqldelight"))
            dialect("app.cash.sqldelight:sqlite-3-38-dialect:$sqlDelightVersion")
        }
    }
}