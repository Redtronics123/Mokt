plugins {
    `mokt-publishing`
    `mokt-multiplatform`
}

group = Project.GROUP

kotlin {
    js(IR) {
        generateTypeScriptDefinitions()
        nodejs()
        useEsModules()
        binaries.library()
    }

    linuxX64()
    mingwX64()

    sourceSets {
        commonMain {
            dependencies {
                api(project(":common"))
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