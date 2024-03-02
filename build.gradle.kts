plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.serialization.json) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotest.multiplatform) apply false
}

repositories {
    mavenCentral()
}

group = "dev.redtronics"