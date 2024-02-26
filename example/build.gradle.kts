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

    // logging
    implementation(libs.logback)
}
