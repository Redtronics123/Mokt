plugins {
    org.jetbrains.dokka
}

version = System.getenv(/* name = */ "CI_COMMIT_TAG") ?: System.getenv(/* name = */ "CI_COMMIT_SHORT_SHA")?.let { "$it-dev" } ?: "0.0.0-DEV"

repositories {
    mavenCentral()
}
