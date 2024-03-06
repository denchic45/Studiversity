val ktorVersion: String by project

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                // Ktor client
                api("io.ktor:ktor-client-cio:$ktorVersion")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-client-auth:$ktorVersion")

                implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")

                // kotlin-result
                implementation("com.michael-bull.kotlin-result:kotlin-result:1.1.17")
            }
        }

        val commonTest by getting {}

        val jvmMain by getting {}

        val jvmTest by getting {}
    }
}