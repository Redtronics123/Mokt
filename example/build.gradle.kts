plugins {
    org.jetbrains.kotlin.jvm
}

group = Project.GROUP

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":authentication"))
}