plugins {
    id("com.android.application")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.android")
//    id("com.google.gms.google-services")
}

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
    compileSdk = 34
    buildToolsVersion = "34.0.0"

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    defaultConfig {
        applicationId = "com.denchic45.studiversity"
        minSdk = 24
        targetSdk = 34
        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        versionCode = 86
        versionName = "1.0.10"
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

    }
    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    sourceSets.all {
        kotlin.srcDir("src/$name/kotlin")
        kotlin.srcDir("build/generated/ksp/debug")
    }
}

dependencies {
    implementation(project((":common")))
    implementation("androidx.compose.runtime:runtime-tracing:1.0.0-beta01")

    implementation("androidx.appcompat:appcompat:1.6.1")
    // support new language API
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")

//    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.8.1")

    // junit
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("androidx.test:core-ktx:1.5.0")
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("android.arch.core:core-testing:1.1.1")
}
