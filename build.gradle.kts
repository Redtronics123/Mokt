plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.serialization.json) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.kover) apply false
}

group = "dev.redtronics"