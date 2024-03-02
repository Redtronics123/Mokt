plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.serialization.json)
}

group = "dev.redtronics"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Mokt
    implementation(project(":lib"))

    // ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.negotiation)

    // logging
    implementation(libs.logback)
}
