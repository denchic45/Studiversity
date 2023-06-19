plugins {
    id("com.android.application")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin") apply false
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.android")

//    id("com.google.gms.google-services")
}

apply(plugin = "androidx.navigation.safeargs.kotlin")

group = "com.denchic45.studiversity"
version = "1.0"

android {
    namespace = "com.denchic45.studiversity"
    packaging {
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    defaultConfig {
        applicationId = "com.denchic45.studiversity"
        minSdk = 24
        targetSdk = 33
        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        versionCode = 84
        versionName = "1.0.8"
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
        jvmTarget = "17"
        freeCompilerArgs = listOf("-Xjvm-default=compatibility")

    }
    kapt {
        correctErrorTypes = true
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.6"
    }

    sourceSets.all {
        kotlin.srcDir("src/$name/kotlin")
        kotlin.srcDir("build/generated/ksp/debug")
    }
}

val daggerVersion = "2.44"

dependencies {
    implementation(project((":common")))

    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.preference:preference-ktx:1.2.0")

    implementation("com.github.denchic45:SearchBar:1.1")

    implementation("androidx.test:core-ktx:1.5.0")
//    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")

    // support new language API
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")

    implementation("com.github.alexmamo:FirestoreDocument-Android:0.1.5")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")

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

    // kotlin-inject
    ksp("me.tatarka.inject:kotlin-inject-compiler-ksp:0.6.1")

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

    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("android.arch.core:core-testing:1.1.1")

    implementation("com.github.chrisbanes:PhotoView:2.3.0")

    implementation("com.sun.mail:javax.mail:1.6.2")
}
