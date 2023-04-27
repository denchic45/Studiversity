plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.4.0"
    id("com.android.library")
    id("kotlin-kapt")
    kotlin("plugin.serialization") version "1.8.20"
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("app.cash.sqldelight")
}

val sqlDelightVersion = "2.0.0-alpha05"
kotlin {
    android()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
    }

    sourceSets {
        val ktorVersion = "2.0.3"
        val koinVersion = "3.2.0"
        val decomposeVersion = "1.0.0"
        val daggerVersion = "2.44"

        val commonMain by getting {
            dependencies {
                // Compose
                api(compose.runtime)
                api(compose.foundation)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                api(compose.material3)
                api(compose.materialIconsExtended)

                api(project(":common:api"))

                api("ca.gosyer:compose-material-dialogs-datetime:0.9.2")

                api("io.github.qdsfdhvh:image-loader:1.3.1")

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

                implementation("app.cash.sqldelight:runtime:$sqlDelightVersion")
                implementation("app.cash.sqldelight:coroutines-extensions:$sqlDelightVersion")
                api("app.cash.sqldelight:primitive-adapters:2.0.0-alpha05")

                implementation("io.insert-koin:koin-core:$koinVersion")

                // Settings
                api("com.russhwolf:multiplatform-settings:1.0.0")
                api("com.russhwolf:multiplatform-settings-coroutines:1.0.0")

                implementation("com.squareup.okio:okio:3.3.0")

                api("me.tatarka.inject:kotlin-inject-runtime:0.6.1")

                // kotlin-result
                api("com.michael-bull.kotlin-result:kotlin-result:1.1.17")
                api("com.michael-bull.kotlin-result:kotlin-result-coroutines:1.1.17")
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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.4")

                implementation("app.cash.sqldelight:runtime-jvm:$sqlDelightVersion")
                implementation("app.cash.sqldelight:coroutines-extensions-jvm:$sqlDelightVersion")

                implementation("com.google.code.gson:gson:2.9.0")

                implementation("net.harawata:appdirs:1.2.1")

                api("org.jetbrains.kotlin:kotlin-reflect:1.7.0")

                //Dagger
                api("com.google.dagger:dagger:$daggerVersion")
                configurations.getByName("kapt").dependencies.add(project.dependencies.create("com.google.dagger:dagger-compiler:$daggerVersion"))

                implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
            }
        }
        val androidMain by getting {
            dependencies {
                dependsOn(commonJvmMain)
                kotlin.srcDir("build/generated/ksp/android/androidDebug")

                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.9.0")

                api("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")

//                implementation ("com.github.JoelKanyi:HorizontalCalendarView:1.0.4")

                api("com.github.kirich1409:viewbindingpropertydelegate-noreflection:1.5.3")

                api("com.arkivanov.decompose:extensions-android:$decomposeVersion")

                api("io.ktor:ktor-client-android:$ktorVersion")
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
                implementation("app.cash.sqldelight:android-driver:$sqlDelightVersion")

                api("com.squareup.retrofit2:retrofit:2.9.0")

                api("androidx.work:work-runtime-ktx:2.8.1")

                // Compose
                val composeVersion = "1.4.0"
                api("androidx.compose.runtime:runtime:$composeVersion")
                api("androidx.compose.ui:ui:$composeVersion")
                api("androidx.activity:activity-compose:1.7.1")
                // Tooling support (Previews, etc.)
                api("androidx.compose.ui:ui-tooling:$composeVersion")
                // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
                api("androidx.compose.foundation:foundation:$composeVersion")
                // Material Design
                api("androidx.compose.material3:material3:1.1.0-rc01")

                api("androidx.recyclerview:recyclerview:1.3.0")

                // Dagger
                api("com.google.dagger:dagger-android:$daggerVersion")
                api("com.google.dagger:dagger-android-support:$daggerVersion")

                // Decompose
                api("com.arkivanov.decompose:extensions-compose-jetpack:$decomposeVersion")

                // Navigation
                api("androidx.navigation:navigation-fragment-ktx:2.5.3")
                api("androidx.navigation:navigation-ui-ktx:2.5.3")

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

                api("io.coil-kt:coil-compose:2.2.2")
                api("io.coil-kt:coil-svg:2.3.0")

                api("com.google.android.play:core:1.10.3")
                api("com.google.android.play:core-ktx:1.8.1")

                api("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")
                // Flow layout
                api("com.google.accompanist:accompanist-flowlayout:0.27.0")
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

                api("app.cash.sqldelight:sqlite-driver:$sqlDelightVersion")
                api("app.cash.sqldelight:coroutines-extensions-jvm:$sqlDelightVersion")

                // Dagger
//                api("com.google.dagger:dagger:$daggerVersion")
//                configurations.getByName("kapt").dependencies.add(project.dependencies.create("com.google.dagger:dagger-compiler:$daggerVersion"))

                // Decompose
                api("com.arkivanov.decompose:extensions-compose-jetbrains:$decomposeVersion")

                dependsOn(commonJvmMain)
            }
        }
        val desktopTest by getting {
            dependencies {

            }
        }
    }
}

dependencies {
    add("kspAndroid", "me.tatarka.inject:kotlin-inject-compiler-ksp:0.6.1")
    add("kspDesktop", "me.tatarka.inject:kotlin-inject-compiler-ksp:0.6.1")
}

android {

    namespace = "com.denchic45.kts"

    compileSdk = 33
    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("src/commonMain/resources", "src/androidMain/res")
    }
    defaultConfig {
        minSdk = 24
        targetSdk = 33
        buildFeatures {
            viewBinding = true
            compose = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    sourceSets.all {
        kotlin.srcDir("src/$name/kotlin")
        java.srcDir("src/$name/java")
    }
    kapt {
        correctErrorTypes = true
    }
}
dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.denchic45.kts")
            sourceFolders.set(listOf("sqldelight"))
            dialect("app.cash.sqldelight:sqlite-3-38-dialect:$sqlDelightVersion")
        }
    }
}