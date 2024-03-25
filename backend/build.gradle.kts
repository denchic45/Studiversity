import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val exposedVersion: String by project
val h2Version: String by project
val koinVersion: String by project

plugins {
    application
    kotlin("jvm")
    id("io.ktor.plugin") version "2.3.3"
    kotlin("plugin.serialization")

    id("org.jetbrains.kotlinx.dataframe") version "0.12.1"
}

group = "com.denchic45.studiversity"

version = "0.0.1"
application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
//    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(":common:api"))
    // Ktor server
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-server-request-validation:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-auto-head-response:$ktorVersion")
    implementation("io.ktor:ktor-server-partial-content:$ktorVersion")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")

    implementation("org.apache.commons:commons-email:1.5")

    // Ktor client
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-auth:$ktorVersion")

    // Exposed
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("com.h2database:h2:$h2Version")
    implementation("com.zaxxer:HikariCP:5.1.0")

    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")

    // Koin
    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")
    testImplementation("io.insert-koin", "koin-test-junit5", koinVersion) {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-test-junit")
    }

    implementation("org.mindrot:jbcrypt:0.4")

    // Hoplite
    implementation("com.sksamuel.hoplite:hoplite-hocon:2.7.1")
    implementation("com.sksamuel.hoplite:hoplite-json:2.7.1")
    implementation("com.sksamuel.hoplite:hoplite-watch:2.7.1")

    implementation("org.postgresql:postgresql:42.5.1")

    implementation("org.jetbrains.kotlinx:dataframe:0.12.1")

    implementation("com.squareup.okio:okio:3.9.0")

    implementation("io.github.oshai:kotlin-logging-jvm:6.0.3")

    implementation("com.michael-bull.kotlin-result:kotlin-result:1.1.16")

    implementation("net.harawata:appdirs:1.2.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}