plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "dev.redtronics"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lib"))
}
