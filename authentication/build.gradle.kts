plugins {
    `mokt-publishing`
    `mokt-multiplatform`
}

group = Project.GROUP

kotlin {
    linuxX64()

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":common"))

                implementation(libs.ktor.server.core)
                implementation(libs.ktor.server.cio)
                implementation(libs.ktor.server.content.negotiation)
                implementation(libs.ktor.server.html)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotest.assertions.core)
                implementation(libs.kotest.framework.engine)
                implementation(libs.kotest.property)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.kotest.runner.junit5)
            }
        }
    }
}
