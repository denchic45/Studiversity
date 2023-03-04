plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin") apply false
    id("com.google.gms.google-services")
}

apply(plugin = "androidx.navigation.safeargs.kotlin")

group = "com.denchic45.kts"
version = "1.0"

android {
    packagingOptions {
        resources.excludes.apply {
            add("META-INF/LICENSE")
            add("META-INF/*.properties")
            add("META-INF/AL2.0")
            add("META-INF/LGPL2.1")
        }
    }
    testOptions.apply {
        unitTests.isIncludeAndroidResources = true
    }
    compileSdk = 33
    buildToolsVersion = "30.0.3"

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    defaultConfig {
        applicationId = "com.denchic45.kts"
        minSdk = 24
        targetSdk = 33
        versionCode = 83
        versionName = "1.0.7"
        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=compatibility")

    }
    kapt {
        correctErrorTypes = true
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }

    namespace = "com.denchic45.kts"

    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }

    sourceSets.all {
        kotlin.srcDir("src/$name/kotlin")
    }
}

val daggerVersion = "2.44"

dependencies {
    implementation(project((":common")))
    //Compose
    implementation("androidx.compose.runtime:runtime:1.3.3")
    implementation("androidx.compose.ui:ui:1.3.3")
    implementation("androidx.activity:activity-compose:1.6.1")
    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling:1.3.3")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation("androidx.compose.foundation:foundation:1.3.1")
    // Material Design
    implementation("androidx.compose.material3:material3:1.0.1")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.google.android.material:material:1.9.0-alpha01")
    implementation("androidx.preference:preference-ktx:1.2.0")

    implementation(project(":appbarcontroller"))
    implementation("com.github.denchic45:SearchBar:1.1")

    implementation("androidx.test:core-ktx:1.5.0")

    // support new language API
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")

    implementation("com.github.alexmamo:FirestoreDocument-Android:0.1.5")

    implementation("androidx.recyclerview:recyclerview:1.2.1")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")

//    implementation("com.github.CanHub:Android-Image-Cropper:3.3.5")

    implementation("com.github.bumptech.glide:glide:4.13.0")
    kapt("com.github.bumptech.glide:compiler:4.13.0")

    implementation("org.apache.commons:commons-lang3:3.12.0")

    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation("io.reactivex.rxjava3:rxjava:3.1.0")
    implementation("com.jakewharton.rxbinding4:rxbinding-material:4.0.0")

//    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.8.1")

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.github.tarek360.RichPath:animator:0.1.1")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("com.airbnb.android:lottie:4.0.0")

    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // Dagger-android
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")
    kapt("com.google.dagger:dagger-android-processor:$daggerVersion")

    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.fasterxml.jackson.core", "jackson-core", "2.13.1")
    implementation("com.caverock:androidsvg-aar:1.4")

    // junit
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation("android.arch.core:core-testing:1.1.1")

    implementation("com.github.chrisbanes:PhotoView:2.3.0")

    implementation("com.github.kirich1409:viewbindingpropertydelegate-noreflection:1.5.3")

    implementation("com.sun.mail:javax.mail:1.6.2")

}
